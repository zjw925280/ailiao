package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

public class VipInfoBean extends BaseBean {

    public int t_is_vip;
    public int t_is_svip;
    public VipTimeBean vipTime;
    public SVipTimeBean svipTime;

    public static class VipTimeBean extends BaseBean {
        public int t_vip_type;
        public String t_openUp_time;
        public String t_end_time;
    }

    public static class SVipTimeBean extends BaseBean {
        public int t_vip_type;
        public String t_openUp_time;
        public String t_end_time;
    }
}
