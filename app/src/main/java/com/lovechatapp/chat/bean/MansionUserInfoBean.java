package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

/**
 * 府邸邀请用户/房间邀请用户/房间用户Bean
 */
public class MansionUserInfoBean extends BaseBean {

    /**
     * 府邸邀请
     */
    public int t_id;
    public String t_nickName;
    public int t_sex;
    public int t_age;
    public String t_handImg;
    public String t_cover_img;
    public int t_onLine;
    public int roomId;


    /**
     * 房间邀请
     * {
     * "t_id": 159,
     * "t_nickName": "Demo",
     * "t_sex": 0,
     * "t_age": 25,
     * "t_handImg": "http://img-1256929999.cos.ap-chengdu.myqcloud.com/15877234032191.png",
     * "t_cover_img": "http://img-1256929999.cos.ap-chengdu.myqcloud.com/15854482346665.png",
     * "t_role": 1
     * }
     */
    public int t_role;

    public transient boolean isInvited;

    public transient boolean selected;
}