package com.lovechatapp.chat.im

import android.content.Intent
import android.support.v4.app.Fragment
import android.view.View
import android.view.WindowManager
import com.lovechatapp.chat.R
import com.lovechatapp.chat.base.BaseActivity
import com.lovechatapp.chat.helper.IMHelper
import com.lovechatapp.chat.listener.OnCommonListener
import com.tencent.qcloud.tim.uikit.modules.chat.C2CChatManagerKit
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo

/**
 * Im聊天室
 */
class ChatActivity : BaseActivity() {
    private lateinit var mChatFragment: Fragment
    private var isOnStart = false
    override fun onNewIntent(intent: Intent) {
        if (!isOnStart) return
        super.onNewIntent(intent)
        setIntent(intent)
        toChat()
    }

    override fun onStart() {
        super.onStart()
        isOnStart = true
    }

    override fun onDestroy() {
        super.onDestroy()
        C2CChatManagerKit.getInstance().setChatting(false)
    }

    override fun getContentView(): View {
        return inflate(R.layout.chat_activity)
    }

    override fun onContentAdded() {
        needHeader(false)
        toChat()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (this::mChatFragment.isInitialized) {
            data?.apply {
                mChatFragment.onActivityResult(requestCode, resultCode, this)
            }
        }
    }

    private fun toChat() {
        val mChatInfo = intent.getSerializableExtra(ImConstants.CHAT_INFO) as ChatInfo
        val otherId = mChatInfo.id.toInt() - 10000
        IMHelper.checkServeIm(otherId, object : OnCommonListener<Boolean?> {

            override fun execute(aBoolean: Boolean?) {
                if (isFinishing) {
                    return
                }
                C2CChatManagerKit.getInstance().setChatting(true)
                mChatFragment = if (aBoolean == true) {
                    ChatServeFragment()
                } else {
                    window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                    ChatFragment()
                }
                supportFragmentManager.beginTransaction().replace(R.id.empty_view, mChatFragment)
                    .commitAllowingStateLoss()
            }
        })
    }
}