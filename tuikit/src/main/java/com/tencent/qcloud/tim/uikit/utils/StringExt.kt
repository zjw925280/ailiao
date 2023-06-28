package com.tencent.qcloud.tim.uikit.utils

fun String.getPhoneMasked(): String {
    return if (isEmpty() || length != 11) {
        "***********"
    } else {
        val head = substring(0, 3)
        val end = substring(length - 4, length)
        "$head****$end"
    }
}