package com.capybala.mybatis

import com.capybala.JST_OFFSET
import com.capybala.TestResult
import com.capybala.isEqual
import com.zaxxer.hikari.HikariDataSource
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import java.math.BigDecimal
import java.net.InetAddress
import java.net.URL
import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.time.*
import java.util.*


fun testMyBatis(jdbcUrl: String) {
    val dataSource = HikariDataSource()
    dataSource.jdbcUrl = jdbcUrl

    val localDate = LocalDate.of(2019, 9, 27)
    val localTime = LocalTime.of(13, 23)
    val localDateTime = LocalDateTime.of(localDate, localTime)
    val offsetTime = OffsetTime.of(localTime, JST_OFFSET)
    val offsetDateTime = OffsetDateTime.of(localDate, localTime, JST_OFFSET)

    val transactionFactory = JdbcTransactionFactory()
    val environment = Environment("development", transactionFactory, dataSource)
    val configuration = Configuration(environment)
    configuration.addMapper(TestMapper::class.java)
    val sqlSessionFactory = SqlSessionFactoryBuilder().build(configuration)

    sqlSessionFactory.openSession().use { c ->
        doTest(c, "c_boolean", true)
        doTest(c, "c_integer", 1)
        doTest(c, "c_integer", BigDecimal("1"))
        doTest(c, "c_decimal", BigDecimal("0.5"))
        doTest(c, "c_double", 0.5)
        doTest(c, "c_double", BigDecimal("0.5"))
        doTest(c, "c_varchar", "varchar")
        doTest(c, "c_text", "long long text")
        doTest(c, "c_bytea", byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte()))
        doTest(c, "c_uuid", UUID.fromString("33ee757a-19b3-45dc-be79-f1d65ac5d1a4"))
        doTest(c, "c_date", localDate)
        doTest(c, "c_date", Date.valueOf(localDate))
        doTest(c, "c_time", localTime)
        doTest(c, "c_time", Time.valueOf(localTime))
        doTest(c, "c_timetz", offsetTime)
        doTest(c, "c_timestamp", localDateTime)
        doTest(c, "c_timestamp", Timestamp.valueOf(localDateTime))
        doTest(c, "c_timestamptz", offsetDateTime)
        doTest(c, "c_inet_ipv4", InetAddress.getByName("192.168.1.1"))
        doTest(c, "c_inet_ipv6", InetAddress.getByName("::1"))
        doTest(c, "c_url", URL("http://example.com/"))
        doTest(c, "c_integer_array", listOf(1, 2, 3))
        doTest(c, "c_integer_array", arrayOf(1, 2, 3))
        doTest(c, "c_integer_array", intArrayOf(1, 2, 3))
        doTest(c, "c_varchar_array", listOf("A", "B", "C"))
        doTest(c, "c_varchar_array", arrayOf("A", "B", "C"))
        doTestIn(c, "c_integer", listOf(1, 2, 3))
        doTestIn(c, "c_integer", arrayOf(1, 2, 3))
        doTestIn(c, "c_integer", intArrayOf(1, 2, 3))
        doTestIn(c, "c_varchar", listOf("integer", "varchar", "text"))
        doTestIn(c, "c_varchar", arrayOf("integer", "varchar", "text"))
    }
}

inline fun <reified T> doTest(c: SqlSession, column: String, value: T) {
    val b = canBind(c, column, value)
    val m = canMap(c, column, value)
    println("$column\t${T::class.java.canonicalName}\t${b.asColoredString()}\t${m.asColoredString()}")
}

fun <T> canBind(c: SqlSession, column: String, value: T): TestResult {
    return try {
        val count = c.getMapper(TestMapper::class.java).selectCountMatches(column, value)
        if (count == 1) TestResult.success() else TestResult.wrongValue()
    } catch (ex: Exception) {
        TestResult.exception(ex)
    }
}

fun <T> canMap(c: SqlSession, column: String, expectedValue: T): TestResult {
    val mapper = c.getMapper(TestMapper::class.java)
    return try {
        val result: T = when (expectedValue) {
            is BigDecimal -> mapper.selectValueBigDecimal(column)
            is LocalDate -> mapper.selectValueLocalDate(column)
            is LocalTime -> mapper.selectValueLocalTime(column)
            is OffsetTime -> mapper.selectValueOffsetTime(column)
            is LocalDateTime -> mapper.selectValueLocalDateTime(column)
            is OffsetDateTime -> mapper.selectValueOffsetDateTime(column)
            else -> mapper.selectValue(column)
        } as T

        if (isEqual(result, expectedValue)) TestResult.success() else TestResult.wrongValue()
    } catch (ex: Exception) {
        TestResult.exception(ex)
    }
}

inline fun <reified T> doTestIn(c: SqlSession, column: String, value: T) {
    val b = canBindIn(c, column, value)
    println("$column.in\t${T::class.java.canonicalName}\t${b.asColoredString()}")
}

fun <T> canBindIn(c: SqlSession, column: String, value: T): TestResult {
    return try {
        val count = c.getMapper(TestMapper::class.java).selectCountMatchesIn(column, value)
        if (count == 1) TestResult.success() else TestResult.wrongValue()
    } catch (ex: Exception) {
        TestResult.exception(ex)
    }
}

interface TestMapper {
    @Select("SELECT COUNT(*) FROM sql_mapper_test WHERE ${'$'}{column} = #{value}")
    fun <T> selectCountMatches(@Param("column") column: String, @Param("value") value: T): Int

    @Select("SELECT COUNT(*) FROM sql_mapper_test WHERE ${'$'}{column} IN (#{value})")
    fun <T> selectCountMatchesIn(@Param("column") column: String, @Param("value") value: T): Int

    @Select("SELECT ${'$'}{column} FROM sql_mapper_test")
    fun <T> selectValue(@Param("column") column: String): T

    @Select("SELECT ${'$'}{column} FROM sql_mapper_test")
    fun selectValueBigDecimal(@Param("column") column: String): BigDecimal

    @Select("SELECT ${'$'}{column} FROM sql_mapper_test")
    fun selectValueLocalDate(@Param("column") column: String): LocalDate

    @Select("SELECT ${'$'}{column} FROM sql_mapper_test")
    fun selectValueLocalTime(@Param("column") column: String): LocalTime

    @Select("SELECT ${'$'}{column} FROM sql_mapper_test")
    fun selectValueOffsetTime(@Param("column") column: String): OffsetTime

    @Select("SELECT ${'$'}{column} FROM sql_mapper_test")
    fun selectValueLocalDateTime(@Param("column") column: String): LocalDateTime

    @Select("SELECT ${'$'}{column} FROM sql_mapper_test")
    fun selectValueOffsetDateTime(@Param("column") column: String): OffsetDateTime
}
