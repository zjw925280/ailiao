package com.lovechatapp.chat.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.ComponentCallbacks
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.*
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.alibaba.fastjson.JSON
import com.gyf.barlibrary.ImmersionBar
import com.lovechatapp.chat.R
import com.lovechatapp.chat.bean.DateBean
import com.lovechatapp.chat.databinding.BaseActivityBaseBinding
import com.lovechatapp.chat.ext.operateDate
import com.lovechatapp.chat.listener.OnCommonListener
import com.lovechatapp.chat.socket.SocketMessageManager
import com.lovechatapp.chat.socket.domain.Mid
import com.lovechatapp.chat.socket.domain.ReceiveFloatingBean
import com.lovechatapp.chat.socket.domain.SocketResponse
import com.lovechatapp.chat.util.*
import com.zhy.http.okhttp.OkHttpUtils


/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述： Activity基类
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
abstract class BaseActivity : FragmentActivity() {
    //根布局
    protected lateinit var mBaseLayout: RelativeLayout
    protected lateinit var mBaseContent: FrameLayout

    //沉浸式状态栏
    private var mImmersionBar: ImmersionBar? = null

    //注解
    private lateinit var mUnBinder: Unbinder

    protected lateinit var mContext: BaseActivity
    protected lateinit var mRightTv: TextView
    protected lateinit var mHeadLineV: View

    //加载dialog
    private lateinit var mDialogLoading: Dialog

    //判断当前是否前台
    private var mIsActivityFront = false

    private lateinit var mBinding: BaseActivityBaseBinding
    private val receiver = CustomReceiver()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCustomDensity(application,this);
        ActivityManager.getInstance().addActivity(this)
        SocketMessageManager.get().subscribe(baseSubscribe, Mid.RECEIVE_GIFT)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        mContext = this
        if (supportFullScreen()) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            //1.设置状态栏样式
            setStatusBarStyle()
        }
        mBinding = BaseActivityBaseBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        //2.设置是否屏幕常亮
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //3.初始化http请求request集合，保证在activity结束的时候终止http请求
        //4.初始化view
        initView()
        //5.添加view到content容器中，子类实现
        addIntoContent(getContentView())
        //6.初始化view，设置onclick监听器
        //解决继承自BaseActivity且属于当前库(framework)的子类butterKnife不能使用BindView的注解，onclick的注解
        initSubView()
        //7.register eventbus
        //8.view已添加到container
        onContentAdded()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver()
        mIsActivityFront = true
        LogUtil.e("ralph", "activity name ===== ${javaClass.name}")
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
        mIsActivityFront = false
    }

    override fun onDestroy() {
        SocketMessageManager.get().unsubscribe(baseSubscribe)
        mImmersionBar?.destroy()
        mUnBinder.unbind()
        OkHttpUtils.getInstance().cancelTag(this@BaseActivity)
        ActivityManager.getInstance().removeActivity(this)
        dismissLoadingDialog()
        super.onDestroy()
    }

    /**
     * 初始化view
     */
    private fun initView() {
        mDialogLoading = DialogUtil.showLoadingDialog(this)
        mBaseLayout = mBinding.baseLayout
        mBaseContent = mBinding.baseContent
        mRightTv = mBinding.head.rightText
        mHeadLineV = mBinding.head.headLineV

        //默认处理title左上view的点击事件（back）
        mBinding.head.leftFl.setOnClickListener { finish() }
    }

    /**
     * 添加view到容器中
     */
    private fun addIntoContent(view: View?) {
        if (view != null) {
            if (!attachMergeLayout()) {
                mBaseContent.removeAllViews()
                mBaseContent.addView(view)
            }
            mUnBinder = ButterKnife.bind(this)
        } else {
            try {
                throw Exception("content view can not be null")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * @return view
     */
    protected abstract fun getContentView(): View

    /**
     * 加载布局
     */
    protected fun inflate(@LayoutRes resource: Int): View {
        return LayoutInflater.from(this).inflate(resource, null)
    }

    /**
     * 加载布局
     */
    protected fun inflate(@LayoutRes resource: Int, root: ViewGroup?): View {
        return LayoutInflater.from(this).inflate(resource, root)
    }

    /**
     * 初始化 subView，一般只用于在framework中BaseActivity子类，为了解决
     * 继承自BaseActivity且属于当前库(framework)的子类butterKnife不能使用BindView的注解，onclick的注解
     */
    protected fun initSubView() {}

    /**
     * @return return true 添加的layout以merge标签作为根布局, false layout不以merge标签作为根布局
     */
    protected fun attachMergeLayout(): Boolean {
        return false
    }

    /**
     * 添加view完成回调，用于初始化数据
     */
    protected abstract fun onContentAdded()

    /**
     * 是否需要显示顶部栏
     */
    protected fun needHeader(isNeed: Boolean) {
        if (isNeed) {
            mBinding.head.root.visibility = View.VISIBLE
        } else {
            mBinding.head.root.visibility = View.GONE
        }
    }

    /**
     * 设置页面title
     */
    override fun setTitle(res: Int) {
        if (res > 0) {
            title = resources.getText(res)
        } else {
            mBinding.head.middleTitle.visibility = View.INVISIBLE
        }
    }

    /**
     * 设置页面title
     */
    override fun setTitle(title: CharSequence) {
        if (!TextUtils.isEmpty(title)) {
            mBinding.head.middleTitle.text = title
            mBinding.head.middleTitle.visibility = View.VISIBLE
        } else {
            mBinding.head.middleTitle.visibility = View.INVISIBLE
        }
    }

    /**
     * 设置返回不可见
     */
    fun setBackVisibility(visibility: Int) {
        mBinding.head.leftFl.visibility = visibility
    }

    /**
     * 是否支持全屏
     */
    protected open fun supportFullScreen(): Boolean {
        return false
    }

    /**
     * 设置状态栏背景
     */
    protected fun setStatusBarStyle() {
        if (!isImmersionBarEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //如果不是沉浸式,就设置为黑色字体
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            return
        }
        mImmersionBar = ImmersionBar.with(this)
        mImmersionBar?.statusBarDarkFont(true)?.navigationBarColor(R.color.white)?.init()
    }

    /**
     * 是否可以使用沉浸式
     */
    protected open val isImmersionBarEnabled: Boolean
        get() = false

    /**
     * 设置状态栏背景色资源id
     */
    protected open val statusBarColorResId: Int
        get() = R.color.white

    /**
     * 设置状态栏背景色
     */
    protected open val statusBarColor: Int
        get() = if (Build.VERSION.SDK_INT > 22) {
            getColor(statusBarColorResId)
        } else {
            ContextCompat.getColor(this, statusBarColorResId)
        }

    /**
     * 显示请求网络数据进度条
     */
    fun showLoadingDialog() {
        try {
            if (!isFinishing && !mDialogLoading.isShowing) {
                runOnUiThread {
                    try {
                        mDialogLoading.show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 关闭请求网络数据进度条
     */
    fun dismissLoadingDialog() {
        try {
            if (mDialogLoading.isShowing) {
                runOnUiThread {
                    try {
                        mDialogLoading.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 设置title右边文字
     */
    protected fun setRightText(resourceId: Int) {
        if (resourceId > 0) {
            mRightTv.visibility = View.VISIBLE
            mRightTv.setText(resourceId)
        }
    }

    /**
     * 获取UserId
     */
    val userId: String
        get() = AppManager.getInstance().userInfo.t_id.toString()

    protected open fun receiveGift(response: SocketResponse) {
        try {
            //处于前台的activity才显示动画
            if (mIsActivityFront) {
                val bean = JSON.parseObject(response.sourceData, ReceiveFloatingBean::class.java)
                FloatingManagerOne.receiveGift(this@BaseActivity, bean)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var baseSubscribe = OnCommonListener { response: SocketResponse ->
        if (response.mid == Mid.RECEIVE_GIFT) {
            receiveGift(response)
        }
    }

    private fun registerReceiver() {
        val filter = IntentFilter()
        filter.addAction("operateDate")
        registerReceiver(receiver, filter)
    }

    inner class CustomReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != "operateDate") return
            val id = intent.getIntExtra("id", 0)
            val status = intent.getIntExtra("status", 0)
            operateDate(id, status, onSuccess = {
                val toast = when (status) {
                    DateBean.INVITE_TYPE_ACCEPTED -> {
                        "同意约会成功"
                    }
                    DateBean.INVITE_TYPE_REFUSED -> {
                        "拒绝约会成功"
                    }
                    DateBean.INVITE_TYPE_CANCELED -> {
                        "取消约会成功"
                    }
                    else -> {
                        "操作成功"
                    }
                }
                val outIntent = Intent()
                outIntent.action = "operateDateSuccess"
                sendBroadcast(outIntent)
                ToastUtil.showToast(toast)
            }) {
                ToastUtil.showToast(it)
            }
        }
    }

    private var aNoncompatDensity = 0f
    private var aNoncompatScaledDensity = 0f
    fun setCustomDensity(application: Application, activity: Activity) {
        val appDisplayMetrics = application.resources.displayMetrics

        if (aNoncompatDensity == 0f) {
            aNoncompatDensity = appDisplayMetrics.density
            aNoncompatScaledDensity = appDisplayMetrics.scaledDensity

            application.registerComponentCallbacks(object : ComponentCallbacks {
                override fun onConfigurationChanged(newConfig: Configuration) {
                    if (newConfig.fontScale > 0) {
                        aNoncompatScaledDensity = application.resources.displayMetrics.scaledDensity
                    }
                }

                override fun onLowMemory() {
                    // Do nothing
                }
            })
        }

        val targetDensity = appDisplayMetrics.widthPixels / 360f
        val targetScaledDensity = targetDensity * (aNoncompatScaledDensity / aNoncompatDensity)
        val targetDensityDpi = (targetDensity * 160).toInt()

        // Set application's Density
        appDisplayMetrics.density = targetDensity
        appDisplayMetrics.scaledDensity = targetScaledDensity
        appDisplayMetrics.densityDpi = targetDensityDpi

        // Set activity's Density
        val activityDisplayMetrics = activity.resources.displayMetrics
        activityDisplayMetrics.density = targetDensity
        activityDisplayMetrics.scaledDensity = targetScaledDensity
        activityDisplayMetrics.densityDpi = targetDensityDpi
    }



}