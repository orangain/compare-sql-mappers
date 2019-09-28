package com.capybala.jdbi

import com.capybala.JST_OFFSET
import com.capybala.TestResult
import com.capybala.isEqual
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.generic.GenericType
import java.math.BigDecimal
import java.net.InetAddress
import java.net.URL
import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.time.*
import java.util.*


fun testJDBI(jdbcUrl: String) {
    val jdbi = Jdbi.create(jdbcUrl).installPlugins()

    val localDate = LocalDate.of(2019, 9, 27)
    val localTime = LocalTime.of(13, 23)
    val localDateTime = LocalDateTime.of(localDate, localTime)
    val offsetTime = OffsetTime.of(localTime, JST_OFFSET)
    val offsetDateTime = OffsetDateTime.of(localDate, localTime, JST_OFFSET)

    jdbi.useHandle<Exception> { c ->
        doTest(c, "c_boolean", true)
        doTest(c, "c_integer", 1)
        doTest(c, "c_integer", BigDecimal("1"))
        doTest(c, "c_decimal", BigDecimal("0.5"))
        doTest(c, "c_double", 0.5)
        doTest(c, "c_double", BigDecimal("0.5"))
        doTest(c, "c_varchar", "varchar")
        doTest(c, "c_text", "long long text")
        doTest(c, "c_date", localDate)
        doTest(c, "c_date", Date.valueOf(localDate))
        doTest(c, "c_time", localTime)
        doTest(c, "c_time", Time.valueOf(localTime))
        doTest(c, "c_time_with_time_zone", offsetTime)
        doTest(c, "c_timestamp", localDateTime)
        doTest(c, "c_timestamp", Timestamp.valueOf(localDateTime))
        doTest(c, "c_timestamp_with_time_zone", offsetDateTime)
        doTest(c, "c_binary", byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte()))
        doTest(c, "c_inet_ipv4", InetAddress.getByName("192.168.1.1"))
        doTest(c, "c_inet_ipv6", InetAddress.getByName("::1"))
        doTest(c, "c_integer_array", listOf(1, 2, 3), object : GenericType<List<Int>>() {})
        doTest(c, "c_integer_array", arrayOf(1, 2, 3))
        doTest(c, "c_integer_array", intArrayOf(1, 2, 3))
        doTest(c, "c_varchar_array", listOf("A", "B", "C"), object : GenericType<List<String>>() {})
        doTest(c, "c_varchar_array", arrayOf("A", "B", "C"))
        doTest(c, "c_url", URL("http://example.com/"))
        doTest(c, "c_uuid", UUID.fromString("33ee757a-19b3-45dc-be79-f1d65ac5d1a4"))
    }
}

inline fun <reified T> doTest(c: Handle, column: String, value: T, genericType: GenericType<T>? = null) {
    val b = canBind(c, column, value, genericType)
    val m = canMap(c, column, T::class.java, genericType, value)
    println("$column\t${T::class.java.canonicalName}\t${b.asColoredString()}\t${m.asColoredString()}")
}

fun <T> canBind(c: Handle, column: String, value: T, genericType: GenericType<T>?): TestResult {
    return try {
        val count = if (genericType == null)
            c.createQuery("SELECT COUNT(*) FROM sql_mapper_test WHERE $column = :value")
                    .bind("value", value)
                    .mapTo(Integer::class.java)
                    .first()
        else
            c.createQuery("SELECT COUNT(*) FROM sql_mapper_test WHERE $column = :value")
                    .bindByType("value", value, genericType) // Use bindByType for generic types
                    .mapTo(Integer::class.java)
                    .first()

        if (count.toInt() == 1) TestResult.success() else TestResult.wrongValue()
    } catch (ex: Exception) {
        TestResult.exception(ex)
    }
}

fun <T> canMap(c: Handle, column: String, klass: Class<T>, genericType: GenericType<T>?, expectedValue: T): TestResult {
    return try {
        val result: T = if (genericType == null)
            c.createQuery("SELECT $column FROM sql_mapper_test")
                    .mapTo(klass)
                    .first()
        else
            c.createQuery("SELECT $column FROM sql_mapper_test")
                    .mapTo(genericType)
                    .first()

        if (isEqual(result, expectedValue)) TestResult.success() else TestResult.wrongValue()
    } catch (ex: Exception) {
        TestResult.exception(ex)
    }
}
