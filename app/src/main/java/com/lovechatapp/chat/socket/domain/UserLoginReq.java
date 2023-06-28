package com.lovechatapp.chat.socket.domain;

public class UserLoginReq extends Mid {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 用户编号
     */
    private int userId;
    /**
     * 用户角色
     */
    private int t_role;
    /**
     * 用户是否VIP
     */
    private int t_is_vip;
    /**
     * 用户性别
     */
    private int t_sex;

    public UserLoginReq() {
    }

    public int getT_role() {
        return t_role;
    }

    public void setT_role(int t_role) {
        this.t_role = t_role;
    }

    public int getT_is_vip() {
        return t_is_vip;
    }

    public void setT_is_vip(int t_is_vip) {
        this.t_is_vip = t_is_vip;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setT_sex(int t_sex) {
        this.t_sex = t_sex;
    }

    public int getT_sex() {
        return t_sex;
    }

}
