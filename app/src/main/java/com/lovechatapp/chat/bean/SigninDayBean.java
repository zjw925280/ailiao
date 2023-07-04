package com.lovechatapp.chat.bean;

public class SigninDayBean {
    private int isSignIn;//0：未签到 1：已经签到
    private String dayNum;//天数
    private String gole;//奖励

    public int getIsSignIn() {
        return isSignIn;
    }

    public void setIsSignIn(int isSignIn) {
        this.isSignIn = isSignIn;
    }

    public String getDayNum() {
        return dayNum;
    }

    public void setDayNum(String dayNum) {
        this.dayNum = dayNum;
    }

    public String getGole() {
        return gole;
    }

    public void setGole(String gole) {
        this.gole = gole;
    }

    public SigninDayBean(int isSignIn, String dayNum, String gole) {
        this.isSignIn = isSignIn;
        this.dayNum = dayNum;
        this.gole = gole;
    }
}
