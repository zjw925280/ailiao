package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

public class BrowedBean extends BaseBean {

    /**
     * t_handImg : http://www.baidu.com
     * t_create_time : 2018-06-13 14:30:59
     * t_nickName : 李四
     * t_id : 1
     */

    public String t_handImg;
    public long t_create_time;
    public String t_nickName;
    public int t_id;

    public int isFollow;
    public int isCoverFollow;

    public int t_sex;
    public int t_age;


    public int t_browse_user;
    public int t_role;

//    聊主个人资料界面的browedBean
//            "t_browse_user": 2427,
//            "t_handImg": "https://img-1256929999.cos.ap-chengdu.myqcloud.com/head/1562124890998.png",
//            "t_sex": 1
}