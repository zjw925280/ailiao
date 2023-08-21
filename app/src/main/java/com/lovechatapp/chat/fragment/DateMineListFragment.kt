package com.lovechatapp.chat.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.lovechatapp.chat.R
import com.lovechatapp.chat.activity.ReportActivity
import com.lovechatapp.chat.base.AppManager
import com.lovechatapp.chat.base.BaseActivity
import com.lovechatapp.chat.base.BaseFragment
import com.lovechatapp.chat.base.BaseResponse
import com.lovechatapp.chat.bean.DateBean
import com.lovechatapp.chat.constant.ChatApi
import com.lovechatapp.chat.constant.Constant
import com.lovechatapp.chat.databinding.FragmentHomeContentBinding
import com.lovechatapp.chat.databinding.LayoutItemDateMineBinding
import com.lovechatapp.chat.dialog.DateCodeInputDialog
import com.lovechatapp.chat.ext.operateDate
import com.lovechatapp.chat.net.*
import com.lovechatapp.chat.util.CommonMarginDecoration
import com.lovechatapp.chat.util.ParamUtil
import com.lovechatapp.chat.util.ToastUtil
import com.lovechatapp.chat.view.recycle.ListTypeAdapter
import com.lovechatapp.chat.view.recycle.ViewHolder
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zhy.http.okhttp.OkHttpUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DateMineListFragment : BaseFragment() {
    private lateinit var mBinding: FragmentHomeContentBinding
    private lateinit var mHolder: ListTypeAdapter.BindViewHolder
    private lateinit var mAdapter: ListTypeAdapter
    private lateinit var mRequester: PageRequester<DateBean>
    private var mStatusTag = 0
    override fun initLayout(): Int {
        return R.layout.fragment_home_content
    }

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        super.initView(view, savedInstanceState)
        mStatusTag = arguments?.getInt("tag") ?: DateBean.DATE_STATUS_NOT_GO
        view?.apply {
            mBinding = FragmentHomeContentBinding.bind(view)
            val params = mBinding.contentRv.layoutParams as SmartRefreshLayout.LayoutParams
            val margin = resources.getDimensionPixelOffset(R.dimen.dp_12)
            params.marginStart = margin
            params.marginEnd = margin
            mBinding.contentRv.layoutParams = params
            mBinding.contentRv.layoutManager = LinearLayoutManager(requireContext())
            mBinding.contentRv.addItemDecoration(
                CommonMarginDecoration(
                    resources.getDimensionPixelOffset(
                        R.dimen.dp_10
                    ), 0, 1, true
                )
            )
            mHolder = object : ListTypeAdapter.BindViewHolder(R.layout.layout_item_date_mine, true) {
                    override fun createViewHolder(parent: ViewGroup, layoutId: Int): ViewHolder {
                        val binding = LayoutItemDateMineBinding.inflate(LayoutInflater.from(parent.context))
                        val holder = object : ViewHolder(binding.root) {
                            override fun convert(holder: ViewHolder, data: Any) {
                                data as DateBean
                                val isActive = mStatusTag != DateBean.DATE_STATUS_INACTIVE
                                binding.titleBg.isSelected = isActive
                                binding.sexLayout.isSelected = isActive
                                binding.sexIcon.isSelected = isActive
                                binding.ageText.isSelected = isActive
                                binding.codeText.isSelected = isActive
                                binding.cityIcon.isSelected = isActive
                                when (mStatusTag) {
                                    DateBean.DATE_STATUS_NOT_GO -> {
                                        if (System.currentTimeMillis()>data.appointmentTime)  binding.btnAppeal.visibility = View.VISIBLE else binding.btnAppeal.visibility = View.GONE

                                        if (data.isSelf()) {
                                            binding.codeRow.visibility = View.GONE
                                            binding.codeText.text = data.appointmentCode
                                            binding.btnCode.visibility = View.GONE
                                            binding.btnCancel.visibility =
                                                if (data.canCancel()) View.VISIBLE else View.GONE
                                        } else {
                                            binding.codeRow.visibility = View.GONE
                                            binding.btnCode.visibility =
                                                if (data.appointmentStatus == DateBean.INVITE_TYPE_ACCEPTED) View.GONE else View.GONE
                                            binding.btnCancel.visibility = if (data.canCancel()) View.VISIBLE else View.GONE
                                        }
                                    }
                                    else -> {
                                        binding.btnAppeal.visibility = View.GONE
                                        binding.codeRow.visibility =
                                            if (data.isSelf()) View.GONE else View.GONE
                                        if (data.isSelf()) {
                                            binding.codeText.text = data.appointmentCode
                                        } else {
                                            binding.btnCode.visibility = View.GONE
                                        }
                                        binding.btnCode.visibility = View.GONE
                                        binding.btnCancel.visibility = View.GONE
                                    }
                                }
                                binding.cityText.text = data.appointmentAddress
                                binding.nameText.text = data.name
                                binding.sexIcon.setImageLevel(data.sex)
                                binding.ageText.text = data.age.toString()
                                binding.phoneText.text =   data.phone
//                              binding.phoneText.text = data.getPhoneText()
                                binding.timeText.text =
                                    SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.CHINA).format(
                                        Date(
                                            data.appointmentTime
                                        )
                                    )
                                binding.statusText.text =
                                    AppManager.getInstance().dateStatusMap[data.appointmentStatus]

                                binding.btnCode.setOnClickListener {
                                    DateCodeInputDialog(parent.context) { code ->
                                        (requireActivity() as BaseActivity).showLoadingDialog()
                                        //使用[code]请求服务器进行校验
                                        val paramMap = HashMap<String?, Any>()
                                        paramMap["appointmentId"] = data.appointmentId
//                                        paramMap["appointmentCode"] = code
                                        paramMap["userId"] = AppManager.getInstance().userInfo.t_id
                                        OkHttpUtils.post().url(ChatApi.dateCodeVerify())
                                            .addParams("param", ParamUtil.getParam(paramMap))
                                            .build()
                                            .execute(object :
                                                AjaxCallback<BaseResponse<String>?>() {
                                                override fun onResponse(
                                                    response: BaseResponse<String>?,
                                                    id: Int
                                                ) {
                                                    (requireActivity() as BaseActivity).dismissLoadingDialog()
                                                    if (this@DateMineListFragment.requireActivity().isFinishing) {
                                                        return
                                                    }
                                                    response?.apply {
                                                        if (m_istatus == NetCode.SUCCESS) {
                                                            ToastUtil.showToast("约会暗语验证成功！")
                                                            mRequester.onRefresh()
                                                        } else {
                                                            ToastUtil.showToast(m_strMessage)
                                                        }
                                                    } ?: apply {
                                                        ToastUtil.showToast("验证失败，请稍后重试")
                                                    }
                                                }
                                            })
                                    }.show()
                                    val inputManger =
                                        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                    inputManger.showSoftInput(
                                        mBinding.root,
                                        InputMethodManager.SHOW_IMPLICIT
                                    )
                                }

                                binding.btnCancel.setOnClickListener {
                                    //调用取消约会的接口
                                    CoroutineScope(Dispatchers.Main).launch {
                                        operateDate(
                                            data.appointmentId,
                                            3,
                                            onSuccess = {
                                                mRequester.onRefresh()
                                            }
                                        ) { msg ->
                                            ToastUtil.showToast(msg)
                                        }
                                    }
                                }

                                binding.btnAppeal.setOnClickListener {
                                    //申诉
                                    val intent =Intent(mContext, ReportActivity::class.java)
                                    intent.putExtra(Constant.ACTOR_ID, data.inviteeId)
                                    intent.putExtra(Constant.SENSU, "申诉")
                                    startActivity(intent)
                                }
                            }
                        }
                        return holder
                    }
                }
            mAdapter = ListTypeAdapter(mHolder)
            mBinding.contentRv.adapter = mAdapter
            mRequester = object : PageRequester<DateBean>() {
                override fun onSuccess(list: MutableList<DateBean>?, isRefresh: Boolean) {
                    list?.apply {
                        mHolder.setData(list, isRefresh)
                    }
                }
            }
            mRequester.setApi(ChatApi.getDateList())
            mRequester.setParam("flag", 2)
            mRequester.setParam("tag", mStatusTag + 1)
            mRequester.setParam("pageNum", 10)
            mRequester.setOnPageDataListener(RefreshPageListener(mBinding.refreshLayout))
            mBinding.refreshLayout.setOnRefreshListener(RefreshHandler(mRequester))
            mBinding.refreshLayout.setOnLoadMoreListener(RefreshHandler(mRequester))
            mRequester.onRefresh()
        }
    }
}