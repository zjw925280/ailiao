package com.lovechatapp.chat.activity;

import android.os.SystemClock;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.dialog.Invite1v2Dialog;
import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;
import com.lovechatapp.chat.view.recycle.ViewHolder;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 多人语音
 */
public class MultipleAudioActivity extends MultipleVideoActivity {

    @BindView(R.id.mute_iv)
    ImageView muteIv;
    View itemMuteBtn;

    @BindView(R.id.speaker_iv)
    ImageView speakerIv;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_multiple_audio_activity);
    }

    @OnClick({
            R.id.mute_iv,
            R.id.speaker_iv,
            R.id.finish_btn,
            R.id.gift_iv,
            R.id.input_tv
    })
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            case R.id.mute_iv:
                if (itemMuteBtn != null) {
                    itemMuteBtn.performClick();
                }
                break;

            case R.id.speaker_iv:
                if (selfRole != null) {
                    selfRole.speaker = !selfRole.speaker;
                    rtcManager.rtcEngine().setEnableSpeakerphone(selfRole.speaker);
                    speakerIv.setImageResource(selfRole.speaker ?
                            R.drawable.multiple_audio_speaker_selected : R.drawable.multiple_audio_speaker_unselected);
                }
                break;
        }
    }

    protected void initChatRv() {
        adapter = new AbsRecycleAdapter(
                new AbsRecycleAdapter.Type(R.layout.item_multiple_audio_anchor, RoleAnchor.class),
                new AbsRecycleAdapter.Type(R.layout.item_multiple_audio_anchor, RoleBroadcaster.class)) {

            @Override
            public void convert(ViewHolder holder, Object t) {

                Role role = (Role) t;

                boolean isSelf = role.uid == AppManager.getInstance().getUserInfo().t_id;

                if (role.isJoinRoom) {

                    //加载语音视图
                    Glide.with(mContext)
                            .load(role.getHead())
                            .error(R.drawable.default_head_img)
                            .transform(new CenterCrop())
                            .into(holder.<ImageView>getView(R.id.cover_iv));

                    holder.<TextView>getView(R.id.name_tv).setText(role.getNickName());

                    //开始计时
                    if (role.getClass() == RoleBroadcaster.class) {
                        Chronometer chronometer = holder.getView(R.id.time_ch);
                        if (isManager || isSelf) {
                            chronometer.setVisibility(View.VISIBLE);
                            chronometer.setFormat("%s");
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            chronometer.start();
                        }
                    }

                }

                //计时布局
                Chronometer chronometer = holder.getView(R.id.time_ch);
                if (!role.isJoinRoom || (!isManager && !isSelf) || (isManager && isSelf)) {
                    chronometer.setVisibility(View.INVISIBLE);
                    chronometer.stop();
                }

                //移出房间btn
                holder.getView(R.id.remove_btn).setVisibility(!isSelf && role.isJoinRoom && isManager ? View.VISIBLE : View.GONE);

                //声音btn
                holder.getView(R.id.speaker_btn).setVisibility(!isSelf && role.isJoinRoom ? View.VISIBLE : View.GONE);
                holder.<ImageView>getView(R.id.speaker_btn).setImageResource(role.mutedAudio ?
                        R.drawable.multiple_chat_speaker_selected : R.drawable.multiple_chat_speaker_unselected);

                //禁麦btn
                holder.getView(R.id.mute_btn).setVisibility(isSelf && role.isJoinRoom ? View.VISIBLE : View.GONE);
                holder.<ImageView>getView(R.id.mute_btn).setImageResource(role.muted ?
                        R.drawable.multiple_chat_mute_selected : R.drawable.multiple_chat_mute_unselected);
                if (itemMuteBtn == null && holder.getView(R.id.mute_btn).getVisibility() == View.VISIBLE) {
                    itemMuteBtn = holder.getView(R.id.mute_btn);
                }
            }

            @Override
            public void setViewHolder(final ViewHolder viewHolder) {

                //邀请主播
                View inviteBtn = viewHolder.itemView.findViewById(R.id.invite_btn);
                if (inviteBtn != null) {
                    if (isManager) {
                        inviteBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Role role = (Role) getData().get(viewHolder.getRealPosition());
                                if (!role.isJoinRoom) {
                                    new Invite1v2Dialog(mContext, chatInfo).show();
                                }
                            }
                        });
                    } else {
                        inviteBtn.setVisibility(View.GONE);
                    }
                }

                //移出主播
                View removeBtn = viewHolder.getView(R.id.remove_btn);
                if (isManager) {
                    removeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Role role = (Role) getData().get(viewHolder.getRealPosition());
                            eventHandler.remove(role);
                        }
                    });
                } else {
                    removeBtn.setVisibility(View.GONE);
                }

                //禁麦
                final ImageView muteBtn = viewHolder.itemView.findViewById(R.id.mute_btn);
                if (muteBtn != null) {
                    muteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Role role = (Role) getData().get(viewHolder.getRealPosition());
                            eventHandler.mute(role, muteBtn);
                            muteIv.setImageResource(role.muted ? R.drawable.multiple_audio_mute_selected : R.drawable.multiple_audio_mute_unselected);
                        }
                    });
                }

                //禁喇叭
                final ImageView speakerBtn = viewHolder.itemView.findViewById(R.id.speaker_btn);
                if (speakerBtn != null) {
                    speakerBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Role role = (Role) getData().get(viewHolder.getRealPosition());
                            eventHandler.speaker(role, speakerBtn);
                        }
                    });
                }
            }
        };
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
        viewRv.setLayoutManager(layoutManager);
        viewRv.setAdapter(adapter);
    }

}