package com.lovechatapp.chat.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.CommonWebViewActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseBean;
import com.lovechatapp.chat.base.BaseFragment;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.MansionActorBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.dialog.CreateMultipleRoomDialog;
import com.lovechatapp.chat.dialog.MansionPermissionDialog;
import com.lovechatapp.chat.dialog.MansionInviteDialog;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.util.FileUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.util.permission.PermissionUtil;
import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;
import com.lovechatapp.chat.view.recycle.OnItemClickListener;
import com.lovechatapp.chat.view.recycle.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * 男用户府邸页面
 */
public class MansionManFragment extends BaseFragment {

    @BindView(R.id.refresh_srl)
    SwipeRefreshLayout swipeRefresh;

    @BindView(R.id.manage_btn)
    TextView manageBtn;

    @BindView(R.id.content_rv)
    RecyclerView contentRv;

    Unbinder unbinder;

    private boolean removeMode;

    private MansionPermissionDialog mansionPermissionDialog;

    private MansionPermission mansionPermission;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRv();
        swipeRefresh.setOnRefreshListener(() -> {
            if (noMansionPermission()) {
                swipeRefresh.setRefreshing(false);
            } else {
                getMansionHouseInfo();
            }
        });
    }

    @Override
    public void onDestroyView() {
        dismissPermission();
        super.onDestroyView();
        unbinder.unbind();
        FileUtil.deleteFiles(Constant.COVER_AFTER_SHEAR_DIR);
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_mansion_man;
    }

    private void initRv() {
        if (contentRv.getAdapter() == null) {

            final String[] onlineText = {"在线", "忙碌", "离线"};
            final int[] onlineDrawId = {R.drawable.mansion_online, R.drawable.mansion_busy, R.drawable.mansion_offline};

            AbsRecycleAdapter adapter = new AbsRecycleAdapter(
                    new AbsRecycleAdapter.Type(R.layout.item_mansion_actor, MansionActorBean.class)) {

                @Override
                public void convert(ViewHolder holder, Object t) {
                    MansionActorBean bean = (MansionActorBean) t;
                    holder.<TextView>getView(R.id.nick_name).setText(bean.t_nickName);
                    holder.getView(R.id.remove_btn).setVisibility(removeMode ? View.VISIBLE : View.GONE);
                    if (bean.isChoice) {
                        holder.<ImageView>getView(R.id.head_iv).setImageResource(R.drawable.mansion_add_actor);
                        holder.<TextView>getView(R.id.nick_name).setText("一键匹配");
                        holder.getView(R.id.remove_btn).setVisibility(View.GONE);
                        holder.getView(R.id.online_tv).setVisibility(View.GONE);
                        holder.getView(R.id.online_iv).setVisibility(View.GONE);
                    } else {
                        Glide.with(holder.itemView.getContext())
                                .load(bean.t_handImg)
                                .error(R.drawable.default_head)
                                .transform(new CircleCrop())
                                .into(holder.<ImageView>getView(R.id.head_iv));
                        holder.getView(R.id.online_tv).setVisibility(View.VISIBLE);
                        holder.<TextView>getView(R.id.online_tv).setText(onlineText[bean.t_onLine]);
                        holder.<ImageView>getView(R.id.online_iv).setImageResource(onlineDrawId[bean.t_onLine]);
                        holder.getView(R.id.online_iv).setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void setViewHolder(final ViewHolder viewHolder) {

                    //移除府邸内的主播
                    viewHolder.getView(R.id.remove_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (noMansionPermission()) {
                                return;
                            }

                            final MansionActorBean bean = (MansionActorBean) getData().get(viewHolder.getRealPosition());

                            new AlertDialog.Builder(getActivity())
                                    .setMessage(String.format("确认移除%s吗？", bean.t_nickName))
                                    .setNegativeButton(R.string.cancel, null)
                                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.dismiss();

                                            Map<String, Object> paramMap = new HashMap<>();
                                            paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
                                            paramMap.put("t_mansion_house_id", mansionPermission.mansionId);
                                            paramMap.put("anchorId", bean.t_id);
                                            OkHttpUtils.post().url(ChatApi.delMansionHouseAnchor())
                                                    .addParams("param", ParamUtil.getParam(paramMap))
                                                    .build().execute(new AjaxCallback<BaseResponse>() {
                                                @Override
                                                public void onResponse(BaseResponse response, int id) {
                                                    if (getActivity() == null || getActivity().isFinishing() || response == null) {
                                                        return;
                                                    }
                                                    if (response.m_istatus == 1) {
                                                        ToastUtil.INSTANCE.showToast("已移除");
                                                        getData().remove(viewHolder.getRealPosition());
                                                        notifyDataSetChanged();
                                                    } else {
                                                        ToastUtil.INSTANCE.showToast(response.m_strMessage);
                                                    }
                                                }

                                                @Override
                                                public void onError(Call call, Exception e, int id) {
                                                    if (getActivity() == null || getActivity().isFinishing()) {
                                                        return;
                                                    }
                                                    ToastUtil.INSTANCE.showToast("移除失败");
                                                }
                                            });
                                        }
                                    }).create().show();
                        }
                    });
                }

            };
            contentRv.setLayoutManager(new GridLayoutManager(getContext(), 4));
            contentRv.setAdapter(adapter);

            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, Object obj, int position) {
                    if (isFemale()) {
                        ToastUtil.INSTANCE.showToast(mContext, R.string.only_man_can_one_vs_two);
                        return;
                    }
                    if (noMansionPermission()) {
                        return;
                    }
                    //邀请主播
                    MansionActorBean bean = (MansionActorBean) obj;
                    if (bean.isChoice) {
                        new MansionInviteDialog(getActivity(), mansionPermission.mansionId) {
                            @Override
                            protected void refresh() {
                                getMansionHouseInfo();
                            }
                        }.show();
                    }
                }
            });
        }
    }

    @Override
    protected void showChanged(boolean b) {
        if (b) {
            checkMansionPermission();
        }
    }

    @OnClick({
            R.id.manage_btn,
            R.id.create_room_btn,
            R.id.introduction_btn
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {

            /*
             * 管理府邸内的用户
             */
            case R.id.manage_btn:
                if (noMansionPermission()) {
                    return;
                }
                if (isFemale()) {
                    ToastUtil.INSTANCE.showToast(mContext, R.string.only_man_can_one_vs_two);
                    return;
                }
                removeMode = !removeMode;
                manageBtn.setText(removeMode ? "取消" : "管理");
                if (contentRv.getAdapter() != null) {
                    contentRv.getAdapter().notifyDataSetChanged();
                }
                break;

            /*
             * 创建房间
             */
            case R.id.create_room_btn:
                if (noMansionPermission()) {
                    return;
                }
                if (isFemale()) {
                    ToastUtil.INSTANCE.showToast(mContext, R.string.only_man_can_one_vs_two);
                    return;
                }

                PermissionUtil.requestPermissions(getActivity(), new PermissionUtil.OnPermissionListener() {

                            @Override
                            public void onPermissionGranted() {
                                if (getActivity() != null && !getActivity().isFinishing()) {
                                    new CreateMultipleRoomDialog(getActivity(), mansionPermission.mansionId).show();
                                }
                            }

                            @Override
                            public void onPermissionDenied() {
                                ToastUtil.INSTANCE.showToast("无麦克风或者摄像头权限，无法使用该功能");
                            }

                        },
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO);

                break;

            /*
             * 玩法说明
             */
            case R.id.introduction_btn:
                CommonWebViewActivity.start(mContext, "玩法说明", "file:///android_asset/mansion_introduction.png");
                break;

        }
    }

    /**
     * 检查府邸权限
     * 没有权限返回true
     */
    private boolean noMansionPermission() {
        if (mansionPermission == null) {
            ToastUtil.INSTANCE.showToast("获取权限中，请稍候再试");
            checkMansionPermission();
            return true;
        }
        if (!mansionPermission.havePermission() && !isFemale()) {
            showPermission();
            return true;
        }
        return false;
    }

    /**
     * 检查府邸权限
     */
    private void checkMansionPermission() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        OkHttpUtils.post().url(ChatApi.getMansionHouseSwitch())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<MansionPermission>>() {
            @Override
            public void onResponse(BaseResponse<MansionPermission> response, int id) {
                if (getActivity() == null || getActivity().isFinishing() || response == null || response.m_object == null) {
                    return;
                }
                mansionPermission = response.m_object;
                if (mansionPermission.havePermission() || isFemale()) {
                    dismissPermission();
                    //判断有无第一次加载列表
                    if (contentRv.getTag() == null) {
                        getMansionHouseInfo();
                    }
                } else {
                    showPermission();
                }
            }
        });
    }

    /**
     * 获取府邸内的用户
     */
    private void getMansionHouseInfo() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("t_mansion_house_id", mansionPermission.mansionId);
        OkHttpUtils.post().url(ChatApi.getMansionHouseInfo())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<List<MansionActorBean>>>() {
            @Override
            public void onResponse(BaseResponse<List<MansionActorBean>> response, int id) {
                if (getActivity() == null || getActivity().isFinishing() || response == null || response.m_object == null) {
                    return;
                }
                removeMode = false;
                manageBtn.setText("管理");
                contentRv.setTag("");

                //列表最多maxSize个数据
                int maxSize = 16;
                List<MansionActorBean> list = new ArrayList<>();
                list.addAll(response.m_object);
                if (list.size() < maxSize) {
                    MansionActorBean choiceBan = new MansionActorBean();
                    choiceBan.isChoice = true;
                    list.add(choiceBan);
                }
                ((AbsRecycleAdapter) contentRv.getAdapter()).setDatas(list);
            }

            @Override
            public void onAfter(int id) {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void showPermission() {
        if (mansionPermissionDialog == null) {
            mansionPermissionDialog = new MansionPermissionDialog(getActivity());
        }
        if (isShowing()) {
            mansionPermissionDialog.show(mansionPermission);
        }
    }

    private void dismissPermission() {
        if (mansionPermissionDialog != null) {
            mansionPermissionDialog.dismiss();
        }
    }

    public static class MansionPermission extends BaseBean {

        public int houseSwitch;
        public int mansionId;
        public String mansionMoney;
        public String t_mansion_house_coverImg;

        private boolean havePermission() {
            return houseSwitch == 1;
        }
    }

    /**
     * 获取用户性别
     * 性别：0.女，1.男 2.需要选择性别
     */
    private boolean isFemale() {
        return AppManager.getInstance().getUserInfo().t_sex == 0;
    }

}