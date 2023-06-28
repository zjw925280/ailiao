package com.lovechatapp.chat.ext

fun String.getPhoneMasked(): String {
    return if (isEmpty()) {
        "****"
    } else {
        val head = substring(0, 3)
        val end = substring(length - 4, length)
        "$head****$end"
    }
}