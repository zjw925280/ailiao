package com.lovechatapp.chat.constant;

import com.lovechatapp.chat.util.FileUtil;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：常量工具类
 * 作者：
 * 创建时间：2018/6/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
@SuppressWarnings("ALL")
public class Constant {
    //-------------- 第三方 start---------------
    //微信
    //微信APP ID
    public static final String WE_CHAT_APPID = "wx05cae53745c16a91";
    //微信APP SECRET
    public static final String WE_CHAT_SECRET = "53cdc2690c6c658ea6987fdfc1b879b3";

    //QQ
    //QQ app id
    public static final String QQ_APP_ID = "";

    //腾讯云
    //腾讯云 cos 服务的 app id
    public static final String TENCENT_CLOUD_APP_ID = "1257023374";
    //对应的 秘钥ID
    public static final String TENCENT_CLOUD_SECRET_ID = "AKIDRj8KK6ez60f5IG0mMis9JG0e3EOQfOvE";
    // 对应的 秘钥
    public static final String TENCENT_CLOUD_SECRET_KEY = "AcrrgIgqlKgohMmyx0tZ1EAvVu6B2iAQ";
    // bucketForObjectAPITest 所处在的地域
    public static final String TENCENT_CLOUD_REGION = "ap-shanghai";
    //存储桶
    public static final String TENCENT_CLOUD_BUCKET = "mru-123-1257023374";

    //Agora
    public static final String AGORA_APP_ID = "13055745c5ad47c197ac4aca5c19d12f";
    public static final String AGORA_APP_IDENTIFY = "895e95c886e2431ea3000c1fa39a402a";
//    public static final String AGORA_APP_ID = "37634fcc7e7b4122bdb9bd777d989b99";
//    public static final String AGORA_APP_IDENTIFY = "dd9c01044c374c86a29e12e7d6322b48";

    //腾讯IM
    public static final int TIM_APP_ID = 1400802590;

    //-------------- 第三方 end---------------

    //视频聊天
    public static final int CHAT_VIDEO = 1;

    //语音聊天
    public static final int CHAT_AUDIO = 2;

    //标题extra标志
    public static final String TITLE = "title";
    public static final String SENSU = "appeal";//申诉
    public static final String ACTOR_ID = "actor_id";//主播的id
    public static final String VIDEO_URL = "video_url";//短视频地址
    public static final String ROOM_ID = "room_id";//房间编号
    public static final String IS_LINK_USER = "is_link_user";//是否主播呼用户
    public static final String PASS_USER_ID = "pass_user_id";//视频聊天对方id
    public static final String FROM_TYPE = "from_type";//是用户发起聊天,还是主播接通聊天
    public static final int FROM_USER = 0;//是用户发起聊天
    public static final int FROM_ACTOR = 1;//还是主播接通聊天
    public static final int FROM_ACTOR_INVITE = 2;//是主播邀请用户
    public static final int FROM_USER_JOIN = 3;//是用户收到主要邀请加入房间
    public static final String PHONE_MODIFY = "phone_modify";//修改手机号
    public static final String IMAGE_URL = "image_url";//图片地址
    public static final String USER_HEAD_URL = "user_head_url";//用户头像图片地址
    public static final String MINE_HEAD_URL = "mine_head_url";//自己头像图片地址
    public static final String NICK_NAME = "nick_name";//自己nick
    public static final String RED_BEAN = "red_bean";//红包bean
    public static final String ACTIVE_ID = "active_id";//动态id
    public static final String COMMENT_NUMBER = "comment_number";//动态评论数量
    public static final String CLICK_POSITION = "click_position";//动态图片点击那个位置
    public static final String CHOOSED_POSITION = "choose_position";//选择的位置
    public static final String QUICK_ANCHOR_BEAN = "quick_anchor_bean";//速配主播bean
    public static final String JOIN_TYPE = "join_type";//加入类型
    public static final String CHAT_ROOM_ID = "chat_room_id";//聊天室ID
    public static final String MODIFY_TWO = "modify_two";//修改什么
    public static final String MODIFY_CONTENT = "modify_content";//修改内容
    public static final String AUTO_CALL = "auto_call";//自动呼叫
    public static final String START_TIME = "start_timer";//是否计时

    //关闭login页面广播
    public static final String FINISH_LOGIN_PAGE = "com.lovechatapp.chat.close";
    //关闭充值页面
    public static final String FINISH_CHARGE_PAGE = "com.lovechatapp.chat.chargeclose";
    //被封号广播
    public static final String BEEN_CLOSE = "been_close";//红包bean
    public static final String BEEN_CLOSE_DES = "been_close_des";//被封号描述
    public static final String BEEN_CLOSE_LOGIN_PAGE = "com.lovechatapp.chat.beenclose";
    //微信授权返回信息广播
    public static final String WECHAT_WITHDRAW_ACCOUNT = "com.lovechatapp.chat.account";
    public static final String WECHAT_NICK_INFO = "wechat_nick_info";
    public static final String WECHAT_HEAD_URL = "wechat_head_url";
    public static final String WECHAT_OPEN_ID = "wechat_open_id";
    //新人分享微信群成功广播关闭分享页面
    public static final String QUN_SHARE_QUN_CLOSE = "com.lovechatapp.chat.qunclose";

    //h5 url
    public static final String URL = "url";
    //权限申请
    public static final int REQUEST_PERMISSION_CODE = 0x01;//权限申请
    //图片选择
    public static final int REQUEST_CODE_CHOOSE = 0x02;
    //视频选择
    public static final int REQUEST_CODE_CHOOSE_VIDEO = 0x05;
    //u crop 裁剪请求码 封面图
    public static final int UCROP_REQUEST_CODE_COVER = 12;
    //头像选择
    public static final int REQUEST_CODE_CHOOSE_HEAD_IMG = 0x03;

    public static final int REQUEST_CODE_CREATE_DATE = 0x04;
    //u crop 裁剪请求码 头像
    public static final int UCROP_REQUEST_CODE_HEAD = 15;
    //压缩后的文件目录
    public static final String AFTER_COMPRESS_DIR = FileUtil.YCHAT_DIR + "compress/";
    //缩放后的文件目录,认证页面
    public static final String VERIFY_AFTER_RESIZE_DIR = FileUtil.YCHAT_DIR + "verify/";
    //裁剪后的文件目录,个人中心头像
    public static final String HEAD_AFTER_SHEAR_DIR = FileUtil.YCHAT_DIR + "head/";
    //编辑资料裁剪后封面的文件
    public static final String COVER_AFTER_SHEAR_DIR = FileUtil.YCHAT_DIR + "cover/";
    //举报的目录
    public static final String REPORT_DIR = FileUtil.YCHAT_DIR + "report/";
    //更新存放apk的目录
    public static final String UPDATE_DIR = FileUtil.YCHAT_DIR + "update/";
    public static final String UPDATE_APK_NAME = "chatNew.apk";
    //二维码
    public static final String ER_CODE = FileUtil.YCHAT_DIR + "code/";
    //动态视频
    public static final String ACTIVE_VIDEO_DIR = FileUtil.YCHAT_DIR + "video/";
    //动态图片
    public static final String ACTIVE_IMAGE_DIR = FileUtil.YCHAT_DIR + "image/";
    //分享图片目录
    public static final String SHARE_PATH = FileUtil.YCHAT_DIR + "share/";
    //录制音频存放目录
    public static final String RECORD_PATH = FileUtil.YCHAT_DIR + "audio/";
    //相册地址，必须此地址才能载入相册
    public static final String GALLERY_PATH = FileUtil.SDCARD_PATH + "/DCIM/Camera/";

}
