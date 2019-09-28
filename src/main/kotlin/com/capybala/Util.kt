package com.capybala

import java.time.OffsetDateTime
import java.time.ZoneOffset


val JST_OFFSET = ZoneOffset.of("+09:00")!!

const val ANSI_RESET = "\u001B[0m"
const val ANSI_RED = "\u001B[31m"
const val ANSI_GREEN = "\u001B[32m"
const val ANSI_YELLOW = "\u001B[33m"

data class TestResult(val type: TestResultType, val ex: Exception? = null) {
    companion object {
        fun success() = TestResult(TestResultType.SUCCESS)
        fun wrongValue() = TestResult(TestResultType.WRONG_VALUE)
        fun exception(ex: Exception) = TestResult(TestResultType.EXCEPTION, ex)
    }

    fun asString() = when (type) {
        TestResultType.SUCCESS -> "Success"
        TestResultType.WRONG_VALUE -> "Wrong Value"
        TestResultType.EXCEPTION -> ex!!.javaClass.simpleName
    }

    fun asColoredString() = when (type) {
        TestResultType.SUCCESS -> "${ANSI_GREEN}${asString()}$ANSI_RESET"
        TestResultType.WRONG_VALUE -> "${ANSI_YELLOW}${asString()}$ANSI_RESET"
        TestResultType.EXCEPTION -> "${ANSI_RED}${asString()}$ANSI_RESET"
    }
}

enum class TestResultType {
    SUCCESS, WRONG_VALUE, EXCEPTION
}

fun <T> isEqual(a: T, b: T): Boolean {
    if (a is OffsetDateTime && b is OffsetDateTime) {
        return a.isEqual(b)
    }
    if (a is ByteArray && b is ByteArray) {
        return a.toList() == b.toList()
    }
    if (a is Array<*> && b is Array<*>) {
        return a.toList() == b.toList()
    }
    return a == b
}