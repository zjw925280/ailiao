package com.lovechatapp.chat.activity

import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lovechatapp.chat.R
import com.lovechatapp.chat.base.BaseActivity
import com.lovechatapp.chat.bean.DateBean
import com.lovechatapp.chat.constant.ChatApi
import com.lovechatapp.chat.databinding.FragmentHomeContentBinding
import com.lovechatapp.chat.ext.operateDate
import com.lovechatapp.chat.ext.setAllClick
import com.lovechatapp.chat.helper.ImageLoadHelper
import com.lovechatapp.chat.net.PageRequester
import com.lovechatapp.chat.net.RefreshHandler
import com.lovechatapp.chat.net.RefreshPageListener
import com.lovechatapp.chat.util.CommonMarginDecoration
import com.lovechatapp.chat.util.TimeUtil
import com.lovechatapp.chat.util.ToastUtil
import com.lovechatapp.chat.view.recycle.ListTypeAdapter
import com.lovechatapp.chat.view.recycle.ListTypeAdapter.BindViewHolder
import com.lovechatapp.chat.view.recycle.ViewHolder
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.tencent.qcloud.tim.uikit.databinding.LayoutChatDateHolderBinding
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder.MessageChatDateHolder
import com.tencent.qcloud.tim.uikit.utils.getPhoneMasked
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 约会邀请列表界面
 */
class DateInviteListActivity : BaseActivity() {
    /**视图绑定器数据对象*/
    private lateinit var mBinding: FragmentHomeContentBinding

    /**约会邀请列表的ViewHolder*/
    private lateinit var mHolder: BindViewHolder

    /**约会邀请列表的数据适配器*/
    private lateinit var mAdapter: ListTypeAdapter

    /**约会邀请列表的数据请求封装对象*/
    private lateinit var mRequester: PageRequester<DateBean>
    override fun getContentView(): View {
        mBinding = FragmentHomeContentBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onContentAdded() {
        needHeader(true)//需要头部显示
        title = "约会邀请"//设置页面标题
        initHolder()//初始化item组件
        initRecyclerview()//初始化列表组件
        initRequest()//初始化网络请求封装对象
        showLoadingDialog()//显示加载动画
        mRequester.onRefresh()//获取第一页数据
    }

    /**初始化列表组件*/
    private fun initRecyclerview() {
        //添加item的间隔参数
        val offset = resources.getDimensionPixelOffset(R.dimen.dp_16)
        mBinding.contentRv.addItemDecoration(
            CommonMarginDecoration(
                verticalOffset = offset,
                horizontalOffset = 0,
                horizontalCount = 1,
                isFirst = true
            )
        )
        //设置列表的左右外边距
        val params = mBinding.contentRv.layoutParams as SmartRefreshLayout.LayoutParams
        val margin = resources.getDimensionPixelOffset(R.dimen.dp_12)
        params.marginStart = margin
        params.marginEnd = margin
        mBinding.contentRv.layoutParams = params
        //设置列表的形式
        mBinding.contentRv.layoutManager = LinearLayoutManager(this)
        //初始化并设置列表的数据适配器
        mAdapter = ListTypeAdapter(mHolder)
        mBinding.contentRv.adapter = mAdapter
    }

    /**初始化列表的ViewHolder*/
    private fun initHolder() {
        mHolder = object : ListTypeAdapter.BindViewHolder(R.layout.layout_chat_date_holder, true) {
            override fun createViewHolder(parent: ViewGroup?, layoutId: Int): ViewHolder {
                val binding =
                    LayoutChatDateHolderBinding.inflate(LayoutInflater.from(parent?.context))
                val holder = object : ViewHolder(binding.root) {
                    override fun convert(holder: ViewHolder?, data: Any?) {
                        if (holder != null && data != null) {
                            data as DateBean
                            val status = data.appointmentStatus
                            ImageLoadHelper.glideShowImageWithUrl(
                                itemView.context,
                                data.giftPath,
                                binding.dateGiftImg
                            )//设置礼物图片
                            binding.textTime.text = TimeUtil.getNewChatTime(data.createTime)//设置创建时间
                            //根据状态[data.appointmentStatus]设置背景的图片加载
                            binding.dateBg.setImageDrawable(
                                ContextCompat.getDrawable(
                                    itemView.context,
                                    when (status) {
                                        DateBean.INVITE_TYPE_NEW -> {
                                            R.mipmap.bg_date_w
                                        }
                                        DateBean.INVITE_TYPE_ACCEPTED -> {
                                            R.mipmap.bg_date_n
                                        }
                                        else -> {
                                            R.mipmap.bg_date_s
                                        }
                                    }
                                )
                            )
                            binding.textPhone.text = when (status) {
                                MessageChatDateHolder.INVITE_TYPE_ACCEPTED, MessageChatDateHolder.INVITE_TYPE_AUTO_VERIFY, MessageChatDateHolder.INVITE_TYPE_CODE_VERIFY -> {
                                    data.phone
                                }
                                else -> {
                                    if (data.isSelf()) {
                                        data.phone
                                    } else {
                                        data.phone.getPhoneMasked()
                                    }
                                }
                            }
                            //根据状态[status]设置图标的显示
                            binding.iconName.setImageLevel(status)
                            binding.iconPhone.setImageLevel(status)
                            binding.iconLocation.setImageLevel(status)
                            binding.iconTime.setImageLevel(status)
                            binding.iconMark.setImageLevel(status)
                            //设置数据显示
                            binding.textName.text = data.name
                            binding.textLocation.text = data.appointmentAddress
                            binding.textTime.text =
                                SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.CHINA)
                                    .format(Date(data.appointmentTime))
                            binding.ageText.text = data.age.toString()
                            //设置性别显示
                            binding.sexIcon.setImageLevel(data.sex)
                            binding.sexIcon.isSelected = true
                            //设置备注
                            if (data.remarks.isNotEmpty()) {//备注不为空，显示相关控件并设置备注文字
                                binding.textMark.visibility = View.VISIBLE
                                binding.iconMark.visibility = View.VISIBLE
                                binding.textMark.text = data.remarks
                            } else {//备注为空，隐藏相关控件
                                binding.iconMark.visibility = View.GONE
                                binding.textMark.visibility = View.GONE
                            }
                            //根据状态[status]显示或隐藏部分控件
                            when (status) {
                                DateBean.INVITE_TYPE_NEW -> {
                                    binding.dateStatusImg.visibility = View.GONE
                                    binding.btnAccept.visibility = View.VISIBLE
                                    binding.btnRefuse.visibility = View.VISIBLE
                                    binding.textPhone.text = data.phone.getPhoneMasked()
                                    //同意或拒绝约会的按钮点击监听
                                    setAllClick(binding.btnAccept, binding.btnRefuse) { view ->
                                        operateDate(
                                            if (view == binding.btnAccept) //同意约会
                                                DateBean.INVITE_TYPE_ACCEPTED
                                            else //拒绝约会
                                                DateBean.INVITE_TYPE_REFUSED,
                                            data.appointmentId
                                        )
                                    }
                                }
                                else -> {
                                    if (status == DateBean.INVITE_TYPE_ACCEPTED) {
                                        binding.textPhone.text = data.phone
                                    } else {
                                        binding.textPhone.text = data.phone.getPhoneMasked()
                                    }
                                    binding.dateStatusImg.visibility = View.VISIBLE
                                    binding.btnAccept.visibility = View.GONE
                                    binding.btnRefuse.visibility = View.GONE
                                }
                            }
                            if (binding.dateStatusImg.visibility == View.VISIBLE) {
                                //如果状态标识显示控件显示，则设置图片
                                binding.dateStatusImg.setImageLevel(status)
                            }
                        }
                    }
                }
                return holder
            }
        }
    }

    /**初始化网络请求封装对象*/
    private fun initRequest() {
        //初始化网络请求封装对象
        mRequester = object : PageRequester<DateBean>() {
            override fun onSuccess(list: MutableList<DateBean>?, isRefresh: Boolean) {
                dismissLoadingDialog()
                list?.apply {
                    mHolder.setData(list, isRefresh)
                }
            }

            override fun onFailure(message: String?) {
                super.onFailure(message)
                dismissLoadingDialog()
            }
        }
        mRequester.setApi(ChatApi.getDateList())//设置请求的网络地址
        mRequester.setSize(10)//设置列表一页的数据条数
        //设置参数
        mRequester.setParam("flag", 1)
        mRequester.setParam("pageNum", 10)
        //设置关联刷新组件
        mRequester.setOnPageDataListener(RefreshPageListener(mBinding.refreshLayout))
        //设置刷新和加载更多的监听
        mBinding.refreshLayout.setOnRefreshListener(RefreshHandler(mRequester))
        mBinding.refreshLayout.setOnLoadMoreListener(RefreshHandler(mRequester))
    }

    /**根据需要修改的状态[status]和约会的id[dateId]请求服务器修改约会对象的状态*/
    private fun operateDate(status: Int, dateId: Int) {
        showLoadingDialog()//显示加载动画
        CoroutineScope(Dispatchers.Main).launch {
            operateDate(
                dateId,
                status,
                onSuccess = {
                    dismissLoadingDialog()
                    val str = when (status) {
                        DateBean.INVITE_TYPE_ACCEPTED -> {
                            "约会已经接受，请您按时赴约"
                        }
                        DateBean.INVITE_TYPE_REFUSED -> {
                            "约会已拒绝"
                        }
                        else -> {
                            ""
                        }
                    }
                    if (str.isNotEmpty()) {
                        ToastUtil.showToast(str)
                    }
                    mRequester.onRefresh()
                }, onFail = {
                    dismissLoadingDialog()
                    ToastUtil.showToast(it)
                }
            )
        }
    }
}