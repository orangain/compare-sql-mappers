package com.capybala

import java.time.OffsetDateTime
import java.time.ZoneOffset


const val ANSI_RESET = "\u001B[0m"
const val ANSI_RED = "\u001B[31m"
const val ANSI_GREEN = "\u001B[32m"
const val ANSI_YELLOW = "\u001B[33m"

fun coloredBool(value: Boolean): String {
    return if (value) {
        "$ANSI_GREEN$value$ANSI_RESET"
    } else {
        "$ANSI_RED$value$ANSI_RESET"
    }
}

val JST_OFFSET = ZoneOffset.of("+09:00")!!

enum class TestResult {
    SUCCESS, WRONG_VALUE, EXCEPTION
}

fun coloredResult(value: TestResult): String {
    return when (value) {
        TestResult.SUCCESS -> "${ANSI_GREEN}Success$ANSI_RESET"
        TestResult.WRONG_VALUE -> "${ANSI_YELLOW}Wrong Value$ANSI_RESET"
        TestResult.EXCEPTION -> "${ANSI_RED}Exception$ANSI_RESET"
    }
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