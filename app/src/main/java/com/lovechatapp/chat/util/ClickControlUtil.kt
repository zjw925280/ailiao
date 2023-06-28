package com.lovechatapp.chat.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ClickControlUtil {
    companion object {
        private lateinit var instance: ClickControlUtil
        @JvmStatic
        fun getInstance(): ClickControlUtil {
            if (!this::instance.isInitialized) {
                instance = ClickControlUtil()
            }
            return instance
        }
    }

    private var clickable = true

    fun isClickable(): Boolean {
        val result = clickable
        if (result) {
            clickable = false
            CoroutineScope(Dispatchers.IO).launch {
                delayReset()
            }
        }
        return result
    }


    private suspend fun delayReset() {
        delay(500)
        clickable = true
    }

}