package com.capybala

import com.capybala.jdbi.testJDBI
import com.capybala.sql2o.testSql2o
import org.jdbi.v3.core.Jdbi


fun main(args: Array<String>) {
    val jdbcUrl = "jdbc:postgresql://localhost:5432/postgres?user=postgres&password="

    val tests = listOf(
            Test("jdbi", ::testJDBI),
            Test("sql2o", ::testSql2o)
    )

    setup(jdbcUrl)
    tests.forEach { (name, testFunc) ->
        println("============================== $name ==============================")
        testFunc(jdbcUrl)
    }
}

data class Test(
        val name: String,
        val testFunc: (String) -> Unit
)

fun setup(jdbcUrl: String) {
    val jdbi = Jdbi.create(jdbcUrl).installPlugins()

    jdbi.useTransaction<Exception> { handle ->
        handle.createScript("""
            drop table if exists sql_mapper_test;
            create table sql_mapper_test (
                -- simple
                c_boolean boolean,
                c_integer integer,
                c_decimal decimal,
                c_double double precision,
                c_varchar varchar(100),
                c_text text,
                -- datetime
                c_date date,
                c_time time,
                c_time_with_time_zone time with time zone,
                c_timestamp timestamp,
                c_timestamp_with_time_zone timestamp with time zone,
                -- complex
                c_binary bytea,
                c_inet_ipv4 inet,
                c_inet_ipv6 inet,
                c_integer_array integer[],
                c_varchar_array varchar(10)[],
                c_url text,
                c_uuid uuid
            );
            insert into sql_mapper_test (
                -- simple
                c_boolean,
                c_integer,
                c_decimal,
                c_double,
                c_varchar,
                c_text,
                -- datetime
                c_date,
                c_time,
                c_time_with_time_zone,
                c_timestamp,
                c_timestamp_with_time_zone,
                -- complex
                c_binary,
                c_inet_ipv4,
                c_inet_ipv6,
                c_integer_array,
                c_varchar_array,
                c_url,
                c_uuid
            ) values (
                -- simple
                true,
                1,
                '0.5',
                0.5,
                'varchar',
                'long long text',
                -- datetime
                '2019-09-27',
                '13:23',
                '13:23+09:00',
                '2019-09-27T13:23',
                '2019-09-27T13:23+09:00',
                -- complex
                E'\\xDEADBEEF',
                inet '192.168.1.1',
                inet '::1',
                array[1, 2, 3],
                array['A', 'B', 'C'],
                'https://example.com',
                '33ee757a-19b3-45dc-be79-f1d65ac5d1a4'
            )
        """.trimIndent()).execute()
    }
}
