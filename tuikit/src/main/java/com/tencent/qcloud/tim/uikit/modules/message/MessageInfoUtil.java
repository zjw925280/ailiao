package com.tencent.qcloud.tim.uikit.modules.message;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMFaceElem;
import com.tencent.imsdk.TIMFileElem;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMGroupTipsElem;
import com.tencent.imsdk.TIMGroupTipsElemGroupInfo;
import com.tencent.imsdk.TIMGroupTipsElemMemberInfo;
import com.tencent.imsdk.TIMGroupTipsGroupInfoType;
import com.tencent.imsdk.TIMGroupTipsType;
import com.tencent.imsdk.TIMImage;
import com.tencent.imsdk.TIMImageElem;
import com.tencent.imsdk.TIMImageType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageStatus;
import com.tencent.imsdk.TIMSnapshot;
import com.tencent.imsdk.TIMSoundElem;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.TIMVideo;
import com.tencent.imsdk.TIMVideoElem;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.ImCustomMessage;
import com.tencent.qcloud.tim.uikit.utils.DateTimeUtil;
import com.tencent.qcloud.tim.uikit.utils.FileUtil;
import com.tencent.qcloud.tim.uikit.utils.ImageUtil;
import com.tencent.qcloud.tim.uikit.utils.TUIKitConstants;
import com.tencent.qcloud.tim.uikit.utils.TUIKitLog;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class MessageInfoUtil {

    public static final String GROUP_CREATE = "group_create";
    public static final String GROUP_DELETE = "group_delete";
    private static final String TAG = MessageInfoUtil.class.getSimpleName();
    private static final Gson gson = new Gson();

    private static ImFilter imFilter;

    public static void setImFilter(ImFilter filter) {
        imFilter = filter;
    }

    /**
     * 创建一条文本消息
     *
     * @param message 消息内容
     * @return 消息信息对象
     */
    public static MessageInfo buildTextMessage(String message) {
        MessageInfo info = new MessageInfo();
        TIMMessage TIMMsg = new TIMMessage();
        TIMTextElem ele = new TIMTextElem();
        ele.setText(message);
        TIMMsg.addElement(ele);
        info.setExtra(message);
        info.setMsgTime(System.currentTimeMillis() / 1000);
        info.setElement(ele);
        info.setSelf(true);
        info.setTIMMessage(TIMMsg);
        info.setFromUser(TIMManager.getInstance().getLoginUser());
        info.setMsgType(MessageInfo.MSG_TYPE_TEXT);
        return info;
    }

    /**
     * 创建一条自定义表情的消息
     *
     * @param groupId  自定义表情所在的表情组id
     * @param faceName 表情的名称
     * @return 消息信息对象
     */
    public static MessageInfo buildCustomFaceMessage(int groupId, String faceName) {
        MessageInfo info = new MessageInfo();
        TIMMessage TIMMsg = new TIMMessage();
        TIMFaceElem ele = new TIMFaceElem();
        ele.setIndex(groupId);
        ele.setData(faceName.getBytes());
        TIMMsg.addElement(ele);
        info.setExtra("[自定义表情]");
        info.setMsgTime(System.currentTimeMillis() / 1000);
        info.setElement(ele);
        info.setSelf(true);
        info.setTIMMessage(TIMMsg);
        info.setFromUser(TIMManager.getInstance().getLoginUser());
        info.setMsgType(MessageInfo.MSG_TYPE_CUSTOM_FACE);
        return info;
    }

    /**
     * 创建一条图片消息
     *
     * @param uri        图片URI
     * @param compressed 是否压缩
     * @return 消息信息对象
     */
    public static MessageInfo buildImageMessage(final Uri uri, boolean compressed) {
        final MessageInfo info = new MessageInfo();
        final TIMImageElem ele = new TIMImageElem();
        info.setDataUri(uri);
        int[] size = ImageUtil.getImageSize(uri);
        String path = FileUtil.getPathFromUri(uri);
        ele.setPath(path);
        info.setDataPath(path);
        info.setImgWidth(size[0]);
        info.setImgHeight(size[1]);

        TIMMessage TIMMsg = new TIMMessage();
        TIMMsg.setSender(TIMManager.getInstance().getLoginUser());
        TIMMsg.setTimestamp(System.currentTimeMillis());
        if (!compressed) {
            ele.setLevel(0);
        }
        TIMMsg.addElement(ele);
        info.setSelf(true);
        info.setTIMMessage(TIMMsg);
        info.setExtra("[图片]");
        info.setMsgTime(System.currentTimeMillis() / 1000);
        info.setElement(ele);
        info.setFromUser(TIMManager.getInstance().getLoginUser());
        info.setMsgType(MessageInfo.MSG_TYPE_IMAGE);
        return info;
    }

    /**
     * 创建一条视频消息
     *
     * @param imgPath   视频缩略图路径
     * @param videoPath 视频路径
     * @param width     视频的宽
     * @param height    视频的高
     * @param duration  视频的时长
     * @return 消息信息对象
     */
    public static MessageInfo buildVideoMessage(String imgPath, String videoPath, int width, int height, long duration) {
        MessageInfo info = new MessageInfo();
        TIMMessage TIMMsg = new TIMMessage();
        TIMVideoElem ele = new TIMVideoElem();

        TIMVideo video = new TIMVideo();
        video.setDuaration(duration / 1000);
        video.setType("mp4");
        TIMSnapshot snapshot = new TIMSnapshot();

        snapshot.setWidth(width);
        snapshot.setHeight(height);
        ele.setSnapshot(snapshot);
        ele.setVideo(video);
        ele.setSnapshotPath(imgPath);
        ele.setVideoPath(videoPath);

        TIMMsg.addElement(ele);
        Uri videoUri = Uri.fromFile(new File(videoPath));
        info.setSelf(true);
        info.setImgWidth(width);
        info.setImgHeight(height);
        info.setDataPath(imgPath);
        info.setDataUri(videoUri);
        info.setTIMMessage(TIMMsg);
        info.setExtra("[视频]");
        info.setMsgTime(System.currentTimeMillis() / 1000);
        info.setElement(ele);
        info.setFromUser(TIMManager.getInstance().getLoginUser());
        info.setMsgType(MessageInfo.MSG_TYPE_VIDEO);
        return info;
    }

    /**
     * 创建一条音频消息
     *
     * @param recordPath 音频路径
     * @param duration   音频的时长
     * @return 消息信息对象
     */
    public static MessageInfo buildAudioMessage(String recordPath, int duration) {
        MessageInfo info = new MessageInfo();
        info.setDataPath(recordPath);
        TIMMessage TIMMsg = new TIMMessage();
        TIMMsg.setSender(TIMManager.getInstance().getLoginUser());
        TIMMsg.setTimestamp(System.currentTimeMillis() / 1000);
        TIMSoundElem ele = new TIMSoundElem();
        ele.setDuration(duration / 1000);
        ele.setPath(recordPath);
        TIMMsg.addElement(ele);
        info.setSelf(true);
        info.setTIMMessage(TIMMsg);
        info.setExtra("[语音]");
        info.setMsgTime(System.currentTimeMillis() / 1000);
        info.setElement(ele);
        info.setFromUser(TIMManager.getInstance().getLoginUser());
        info.setMsgType(MessageInfo.MSG_TYPE_AUDIO);
        return info;
    }

    /**
     * 创建一条文件消息
     *
     * @param fileUri 文件路径
     * @return 消息信息对象
     */
    public static MessageInfo buildFileMessage(Uri fileUri) {
        String filePath = FileUtil.getPathFromUri(fileUri);
        File file = new File(filePath);
        if (file.exists()) {
            MessageInfo info = new MessageInfo();
            info.setDataPath(filePath);
            TIMMessage TIMMsg = new TIMMessage();
            TIMFileElem ele = new TIMFileElem();
            TIMMsg.setSender(TIMManager.getInstance().getLoginUser());
            TIMMsg.setTimestamp(System.currentTimeMillis() / 1000);
            ele.setPath(filePath);
            ele.setFileName(file.getName());
            TIMMsg.addElement(ele);
            info.setSelf(true);
            info.setTIMMessage(TIMMsg);
            info.setExtra("[文件]");
            info.setMsgTime(System.currentTimeMillis() / 1000);
            info.setElement(ele);
            info.setFromUser(TIMManager.getInstance().getLoginUser());
            info.setMsgType(MessageInfo.MSG_TYPE_FILE);
            return info;
        }
        return null;
    }

    /**
     * 创建一条自定义消息
     *
     * @param data 自定义消息内容，可以是任何内容
     * @return 消息信息对象
     */
    public static MessageInfo buildCustomMessage(ImCustomMessage data, String jsonData) {
        MessageInfo info = new MessageInfo();
        TIMMessage TIMMsg = new TIMMessage();
        TIMCustomElem ele = new TIMCustomElem();
        ele.setData(jsonData.getBytes());
        TIMMsg.addElement(ele);
        info.setSelf(true);
        info.setTIMMessage(TIMMsg);
        info.setMsgTime(System.currentTimeMillis() / 1000);
        info.setElement(ele);
        info.setExtra(data);
        info.setMsgType(MessageInfo.MSG_TYPE_CUSTOM_SELF_GIFT);
        info.setFromUser(TIMManager.getInstance().getLoginUser());
        return info;
    }

    /**
     * 创建一条群消息自定义内容
     *
     * @param action  群消息类型，比如建群等
     * @param message 消息内容
     * @return 消息信息对象
     */
    public static TIMMessage buildGroupCustomMessage(String action, String message) {
        TIMMessage TIMMsg = new TIMMessage();
        TIMCustomElem ele = new TIMCustomElem();
        ele.setData(action.getBytes());
        ele.setExt(message.getBytes());
        TIMMsg.addElement(ele);
        return TIMMsg;
    }

    /**
     * 把SDK的消息bean列表转化为TUIKit的消息bean列表
     *
     * @param timMessages SDK的群消息bean列表
     * @param isGroup     是否是群消息
     * @return 消息信息对象
     */
    public static List<MessageInfo> TIMMessages2MessageInfos(List<TIMMessage> timMessages, boolean isGroup) {
        if (timMessages == null) {
            return null;
        }
        List<MessageInfo> messageInfos = new ArrayList<>();
        for (int i = 0; i < timMessages.size(); i++) {
            TIMMessage timMessage = timMessages.get(i);
            List<MessageInfo> info = TIMMessage2MessageInfo(timMessage, isGroup);
            if (info != null) {
                messageInfos.addAll(info);
            }
        }
        return messageInfos;
    }

    /**
     * 把SDK的消息bean转化为TUIKit的消息bean
     *
     * @param timMessage SDK的群消息bean
     * @param isGroup    是否是群消息
     * @return 消息信息对象
     */
    public static List<MessageInfo> TIMMessage2MessageInfo(TIMMessage timMessage, boolean isGroup) {
        if (timMessage == null || timMessage.status() == TIMMessageStatus.HasDeleted || timMessage.getElementCount() == 0) {
            return null;
        }
        List<MessageInfo> list = new ArrayList<>();
        for (int i = 0; i < timMessage.getElementCount(); i++) {
            final MessageInfo msgInfo = new MessageInfo();
            if (ele2MessageInfo(msgInfo, timMessage, timMessage.getElement(i), isGroup) != null && !msgInfo.ignore) {
                list.add(msgInfo);
            }
        }
        return list;
    }

    public static List<MessageInfo> TIMMessage2MessageInfoIgnore(TIMMessage timMessage, boolean isGroup) {
        if (timMessage == null || timMessage.status() == TIMMessageStatus.HasDeleted || timMessage.getElementCount() == 0) {
            return null;
        }
        List<MessageInfo> list = new ArrayList<>();
        for (int i = 0; i < timMessage.getElementCount(); i++) {
            final MessageInfo msgInfo = new MessageInfo();
            if (ele2MessageInfo(msgInfo, timMessage, timMessage.getElement(i), isGroup) != null) {
                list.add(msgInfo);
            }
        }
        return list;
    }

    public static boolean isTyping(TIMMessage timMessage) {
        // 如果有任意一个element是正在输入，则认为这条消息是正在输入。除非测试，正常不可能发这种消息。
        for (int i = 0; i < timMessage.getElementCount(); i++) {
            if (timMessage.getElement(i).getType() == TIMElemType.Custom) {
                TIMCustomElem customElem = (TIMCustomElem) timMessage.getElement(i);
                if (isTyping(customElem.getData())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isTyping(byte[] data) {
        try {
            String str = new String(data, StandardCharsets.UTF_8);
            MessageTyping typing = new Gson().fromJson(str, MessageTyping.class);
            return typing != null
                    && typing.userAction == MessageTyping.TYPE_TYPING
                    && TextUtils.equals(typing.actionParam, MessageTyping.EDIT_START);
        } catch (Exception e) {
            TUIKitLog.e(TAG, "parse json error");
        }
        return false;
    }

    private static MessageInfo ele2MessageInfo(final MessageInfo msgInfo, TIMMessage timMessage, TIMElem ele, boolean isGroup) {
        if (msgInfo == null
                || timMessage == null
                || timMessage.status() == TIMMessageStatus.HasDeleted
                || timMessage.getElementCount() == 0
                || ele == null
                || ele.getType() == TIMElemType.Invalid) {
            TUIKitLog.e(TAG, "ele2MessageInfo parameters error");
            return null;
        }
        String sender = timMessage.getSender();
        msgInfo.setTIMMessage(timMessage);
        msgInfo.setElement(ele);
        msgInfo.setGroup(isGroup);
        msgInfo.setId(timMessage.getMsgId());
        msgInfo.setUniqueId(timMessage.getMsgUniqueId());
        msgInfo.setPeerRead(timMessage.isPeerReaded());
        msgInfo.setFromUser(sender);
        if (isGroup) {
            TIMGroupMemberInfo memberInfo = timMessage.getSenderGroupMemberProfile();
            if (memberInfo != null && !TextUtils.isEmpty(memberInfo.getNameCard())) {
                msgInfo.setGroupNameCard(memberInfo.getNameCard());
            }
        }
        msgInfo.setMsgTime(timMessage.timestamp());
        msgInfo.setSelf(sender.equals(TIMManager.getInstance().getLoginUser()));

        TIMElemType type = ele.getType();
        if (type == TIMElemType.Custom) {
            TIMCustomElem customElem = (TIMCustomElem) ele;
            String data = new String(customElem.getData());
            if (data.equals(GROUP_CREATE)) {
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_CREATE);
                String message = TUIKitConstants.covert2HTMLString(
                        TextUtils.isEmpty(msgInfo.getGroupNameCard())
                                ? msgInfo.getFromUser()
                                : msgInfo.getGroupNameCard()) + "创建群组";
                msgInfo.setExtra(message);
            } else if (data.equals(GROUP_DELETE)) {
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_DELETE);
                msgInfo.setExtra(new String(customElem.getExt()));
            } else if (data.startsWith("serverSend&&") || (data.startsWith("{") && data.endsWith("}"))) {
                try {
                    //处理自定义im消息
                    ImCustomMessage imCustomMessageBean;
                    if (data.startsWith("serverSend&&")) {
                        data = data.replace("serverSend&&", "{");
                        imCustomMessageBean = gson.fromJson(data, ImCustomMessage.class);
                        JsonObject jsonObject = gson.fromJson(data, JsonObject.class);
                        String customType = jsonObject.get("type").getAsString();
                        if (ImCustomMessage.Type_video_unconnect_user.equals(customType)) {
                            msgInfo.setMsgType(MessageInfo.MSG_TYPE_CUSTOM_CALL);
                        } else if (ImCustomMessage.Type_video_unconnect_anchor.equals(customType)) {
                            if (!msgInfo.isSelf()) {
                                msgInfo.setMsgType(MessageInfo.MSG_TYPE_CUSTOM_ANCHOR_CALL);
                            } else {
                                msgInfo.setMsgType(MessageInfo.MSG_TYPE_CUSTOM_CALL);
                            }
                        } else if (ImCustomMessage.Type_video_connect.equals(customType)) {
                            msgInfo.setMsgType(MessageInfo.MSG_TYPE_CUSTOM_CALL);
                        } else if (ImCustomMessage.Type_voice.equals(customType)) {
                            msgInfo.setMsgType(MessageInfo.MSG_TYPE_AUDIO);
                            msgInfo.setDataPath(imCustomMessageBean.fileUrl);
                        } else if (ImCustomMessage.Type_picture.equals(customType)) {
                            msgInfo.setMsgType(MessageInfo.MSG_TYPE_IMAGE);
                            msgInfo.setDataPath(imCustomMessageBean.fileUrl);
                            msgInfo.setImgWidth(540);
                            msgInfo.setImgHeight(540);
                        } else if (ImCustomMessage.Type_gift.equals(customType)) {
                            imCustomMessageBean = gson.fromJson(data, ImCustomMessage.class);
                            msgInfo.setMsgType(msgInfo.isSelf() ? MessageInfo.MSG_TYPE_CUSTOM_SELF_GIFT : MessageInfo.MSG_TYPE_CUSTOM_OTHER_GIFT);
                        } else if (ImCustomMessage.Type_Date.equals(imCustomMessageBean.type)) {//约会消息
                            imCustomMessageBean = gson.fromJson(data, ImCustomMessage.class);
                            msgInfo.setMsgType(MessageInfo.MSG_TYPE_DATE);
                        }
                    } else {
                        imCustomMessageBean = gson.fromJson(data, ImCustomMessage.class);
                        if (ImCustomMessage.Type_gift.equals(imCustomMessageBean.type) || ImCustomMessage.Type_gold.equals(imCustomMessageBean.type)) {
                            msgInfo.setMsgType(msgInfo.isSelf() ? MessageInfo.MSG_TYPE_CUSTOM_SELF_GIFT : MessageInfo.MSG_TYPE_CUSTOM_OTHER_GIFT);
                        } else if (ImCustomMessage.Type_Date.equals(imCustomMessageBean.type)) {//约会消息
                            imCustomMessageBean = gson.fromJson(data, ImCustomMessage.class);
                            msgInfo.setMsgType(MessageInfo.MSG_TYPE_DATE);
                        } else if (ImCustomMessage.Type_pulp.equals(imCustomMessageBean.type)) {
                            msgInfo.ignore = true;
                        }
                    }
                    msgInfo.setExtra(imCustomMessageBean);
                } catch (Exception e) {
                    e.printStackTrace();
                    msgInfo.setMsgType(MessageInfo.MSG_TYPE_CUSTOM);
                    msgInfo.setExtra("[不支持的消息]");
                }
            } else {
                if (isTyping(customElem.getData())) {
                    // 忽略正在输入，它不能作为真正的消息展示
                    return null;
                }
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_CUSTOM);
                msgInfo.setExtra("[不支持的消息]");
            }
        } else if (type == TIMElemType.GroupTips) {
            TIMGroupTipsElem groupTips = (TIMGroupTipsElem) ele;
            TIMGroupTipsType tipsType = groupTips.getTipsType();
            StringBuilder user = new StringBuilder();
            if (groupTips.getChangedGroupMemberInfo().size() > 0) {
                Object[] ids = groupTips.getChangedGroupMemberInfo().keySet().toArray();
                for (int i = 0; i < ids.length; i++) {
                    user.append(ids[i].toString());
                    if (i != 0)
                        user.insert(0, "，");
                    if (i == 2 && ids.length > 3) {
                        user.append("等");
                        break;
                    }
                }

            } else {
                user = new StringBuilder(groupTips.getOpUserInfo().getIdentifier());
            }
            TIMUserProfile timUserProfile = TIMFriendshipManager.getInstance().queryUserProfile(user.toString());
            if (timUserProfile != null && !TextUtils.isEmpty(timUserProfile.getNickName())) {
                user = new StringBuilder(timUserProfile.getNickName());
            }
            StringBuilder message = new StringBuilder(TUIKitConstants.covert2HTMLString(user.toString()));
            if (tipsType == TIMGroupTipsType.Join) {
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_JOIN);
                message.append("加入群组");
            }
            if (tipsType == TIMGroupTipsType.Quit) {
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_QUITE);
                message.append("退出群组");
            }
            if (tipsType == TIMGroupTipsType.Kick) {
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_KICK);
                message.append("被踢出群组");
            }
            if (tipsType == TIMGroupTipsType.SetAdmin) {
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                message.append("被设置管理员");
            }
            if (tipsType == TIMGroupTipsType.CancelAdmin) {
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                message.append("被取消管理员");
            }
            if (tipsType == TIMGroupTipsType.ModifyGroupInfo) {
                List<TIMGroupTipsElemGroupInfo> modifyList = groupTips.getGroupInfoList();
                for (int i = 0; i < modifyList.size(); i++) {
                    TIMGroupTipsElemGroupInfo modifyInfo = modifyList.get(i);
                    TIMGroupTipsGroupInfoType modifyType = modifyInfo.getType();
                    if (modifyType == TIMGroupTipsGroupInfoType.ModifyName) {
                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NAME);
                        message.append("修改群名称为\"").append(modifyInfo.getContent()).append("\"");
                    } else if (modifyType == TIMGroupTipsGroupInfoType.ModifyNotification) {
                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                        message.append("修改群公告为\"").append(modifyInfo.getContent()).append("\"");
                    } else if (modifyType == TIMGroupTipsGroupInfoType.ModifyOwner) {
                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                        message.append("转让群主给\"").append(modifyInfo.getContent()).append("\"");
                    } else if (modifyType == TIMGroupTipsGroupInfoType.ModifyFaceUrl) {
                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                        message.append("修改了群头像");
                    } else if (modifyType == TIMGroupTipsGroupInfoType.ModifyIntroduction) {
                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                        message.append("修改群介绍为\"").append(modifyInfo.getContent()).append("\"");
                    }
                    if (i < modifyList.size() - 1) {
                        message.append("、");
                    }
                }
            }
            if (tipsType == TIMGroupTipsType.ModifyMemberInfo) {
                List<TIMGroupTipsElemMemberInfo> modifyList = groupTips.getMemberInfoList();
                if (modifyList.size() > 0) {
                    long shutUpTime = modifyList.get(0).getShutupTime();
                    msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                    if (shutUpTime > 0) {
                        message.append("被禁言\"").append(DateTimeUtil.formatSeconds(shutUpTime)).append("\"");
                    } else {
                        message.append("被取消禁言");
                    }
                }
            }
            if (TextUtils.isEmpty(message.toString())) {
                return null;
            }
            msgInfo.setExtra(message.toString());
        } else {
            if (type == TIMElemType.Text) {
                TIMTextElem txtEle = (TIMTextElem) ele;
                msgInfo.setExtra(txtEle.getText());
            } else if (type == TIMElemType.Face) {
                TIMFaceElem txtEle = (TIMFaceElem) ele;
                if (txtEle.getIndex() < 1 || txtEle.getData() == null) {
                    TUIKitLog.e("MessageInfoUtil", "txtEle data is null or index<1");
                    return null;
                }
                msgInfo.setExtra("[自定义表情]");


            } else if (type == TIMElemType.Sound) {
                TIMSoundElem soundElemEle = (TIMSoundElem) ele;
                //api发的语音消息
                if (TextUtils.isEmpty(soundElemEle.getUuid())) {
                    soundElemEle.getUrl(new TIMValueCallBack<String>() {
                        @Override
                        public void onError(int i, String s) {
                            Log.d(TAG, "onError: " + s);
                        }

                        @Override
                        public void onSuccess(String s) {
                            msgInfo.setDataPath(s);
                        }
                    });
                } else {
                    if (msgInfo.isSelf()) {
                        msgInfo.setDataPath(soundElemEle.getPath());
                    } else {
                        final String path = TUIKitConstants.RECORD_DOWNLOAD_DIR + soundElemEle.getUuid();
                        File file = new File(path);
                        if (!file.exists()) {
                            soundElemEle.getSoundToFile(path, new TIMCallBack() {
                                @Override
                                public void onError(int code, String desc) {
                                    TUIKitLog.e("MessageInfoUtil getSoundToFile", code + ":" + desc);
                                }

                                @Override
                                public void onSuccess() {
                                    msgInfo.setDataPath(path);
                                }
                            });
                        } else {
                            msgInfo.setDataPath(path);
                        }
                    }
                }
                msgInfo.setExtra("[语音]");
            } else if (type == TIMElemType.Image) {
                TIMImageElem imageEle = (TIMImageElem) ele;
                String localPath = imageEle.getPath();
                if (msgInfo.isSelf() && !TextUtils.isEmpty(localPath)) {
                    msgInfo.setDataPath(localPath);
                    int[] size = ImageUtil.getImageSize(localPath);
                    msgInfo.setImgWidth(size[0]);
                    msgInfo.setImgHeight(size[1]);
                }
                //本地路径为空，可能为更换手机或者是接收的消息
                else {
                    List<TIMImage> imgList = imageEle.getImageList();
                    for (int i = 0; i < imgList.size(); i++) {
                        TIMImage img = imgList.get(i);
                        if (img.getType() == TIMImageType.Thumb) {
                            final String path = TUIKitConstants.IMAGE_DOWNLOAD_DIR + img.getUuid();
                            msgInfo.setImgWidth((int) img.getWidth());
                            msgInfo.setImgHeight((int) img.getHeight());
                            File file = new File(path);
                            if (file.exists()) {
                                msgInfo.setDataPath(path);
                            }
                        }
                    }
                }
                msgInfo.setExtra("[图片]");
            } else if (type == TIMElemType.Video) {
                TIMVideoElem videoEle = (TIMVideoElem) ele;
                if (msgInfo.isSelf() && !TextUtils.isEmpty(videoEle.getSnapshotPath())) {
                    int[] size = ImageUtil.getImageSize(videoEle.getSnapshotPath());
                    msgInfo.setImgWidth(size[0]);
                    msgInfo.setImgHeight(size[1]);
                    msgInfo.setDataPath(videoEle.getSnapshotPath());
                    msgInfo.setDataUri(FileUtil.getUriFromPath(videoEle.getVideoPath()));
                } else {
                    TIMVideo video = videoEle.getVideoInfo();
                    final String videoPath = TUIKitConstants.VIDEO_DOWNLOAD_DIR + video.getUuid();
                    Uri uri = Uri.parse(videoPath);
                    msgInfo.setDataUri(uri);
                    TIMSnapshot snapshot = videoEle.getSnapshotInfo();
                    msgInfo.setImgWidth((int) snapshot.getWidth());
                    msgInfo.setImgHeight((int) snapshot.getHeight());
                    final String snapPath = TUIKitConstants.IMAGE_DOWNLOAD_DIR + snapshot.getUuid();
                    //判断快照是否存在,不存在自动下载
                    if (new File(snapPath).exists()) {
                        msgInfo.setDataPath(snapPath);
                    }
                }

                msgInfo.setExtra("[视频]");
            } else if (type == TIMElemType.File) {
                TIMFileElem fileElem = (TIMFileElem) ele;
                String filename = fileElem.getUuid();
                if (TextUtils.isEmpty(filename)) {
                    filename = System.currentTimeMillis() + fileElem.getFileName();
                }
                final String path = TUIKitConstants.FILE_DOWNLOAD_DIR + filename;
                File file = new File(path);
                if (file.exists()) {
                    if (msgInfo.isSelf()) {
                        msgInfo.setStatus(MessageInfo.MSG_STATUS_SEND_SUCCESS);
                    } else {
                        msgInfo.setStatus(MessageInfo.MSG_STATUS_DOWNLOADED);
                    }
                    msgInfo.setDataPath(path);
                } else {
                    if (msgInfo.isSelf()) {
                        if (TextUtils.isEmpty(fileElem.getPath())) {
                            msgInfo.setStatus(MessageInfo.MSG_STATUS_UN_DOWNLOAD);
                            msgInfo.setDataPath(path);
                        } else {
                            file = new File(fileElem.getPath());
                            if (file.exists()) {
                                msgInfo.setStatus(MessageInfo.MSG_STATUS_SEND_SUCCESS);
                                msgInfo.setDataPath(fileElem.getPath());
                            } else {
                                msgInfo.setStatus(MessageInfo.MSG_STATUS_UN_DOWNLOAD);
                                msgInfo.setDataPath(path);
                            }
                        }
                    } else {
                        msgInfo.setStatus(MessageInfo.MSG_STATUS_UN_DOWNLOAD);
                        msgInfo.setDataPath(path);
                    }
                }
                msgInfo.setExtra("[文件]");
            }
            msgInfo.setMsgType(TIMElemType2MessageInfoType(type));
        }

        if (timMessage.status() == TIMMessageStatus.HasRevoked) {
            msgInfo.setStatus(MessageInfo.MSG_STATUS_REVOKE);
            msgInfo.setMsgType(MessageInfo.MSG_STATUS_REVOKE);
            if (msgInfo.isSelf()) {
                msgInfo.setExtra("您撤回了一条消息");
            } else if (msgInfo.isGroup()) {
                String message = TUIKitConstants.covert2HTMLString(msgInfo.getFromUser());
                msgInfo.setExtra(message + "撤回了一条消息");
            } else {
                msgInfo.setExtra("对方撤回了一条消息");
            }
        } else {
            if (msgInfo.isSelf()) {
                if (timMessage.status() == TIMMessageStatus.SendFail) {
                    msgInfo.setStatus(MessageInfo.MSG_STATUS_SEND_FAIL);
                } else if (timMessage.status() == TIMMessageStatus.SendSucc) {
                    msgInfo.setStatus(MessageInfo.MSG_STATUS_SEND_SUCCESS);
                } else if (timMessage.status() == TIMMessageStatus.Sending) {
                    msgInfo.setStatus(MessageInfo.MSG_STATUS_SENDING);
                }
            }
        }
        return msgInfo;
    }

    private static int TIMElemType2MessageInfoType(TIMElemType type) {
        switch (type) {
            case Text:
                return MessageInfo.MSG_TYPE_TEXT;
            case Image:
                return MessageInfo.MSG_TYPE_IMAGE;
            case Sound:
                return MessageInfo.MSG_TYPE_AUDIO;
            case Video:
                return MessageInfo.MSG_TYPE_VIDEO;
            case File:
                return MessageInfo.MSG_TYPE_FILE;
            case Location:
                return MessageInfo.MSG_TYPE_LOCATION;
            case Face:
                return MessageInfo.MSG_TYPE_CUSTOM_FACE;
            case GroupTips:
                return MessageInfo.MSG_TYPE_TIPS;
        }

        return -1;
    }
}