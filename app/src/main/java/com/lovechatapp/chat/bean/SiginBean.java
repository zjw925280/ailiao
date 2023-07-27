package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

import java.util.List;

public class SiginBean extends BaseBean {
    private List<SigninDayBean.RowsBean>signInList;
    private int day;
    private  List<SiginInRecordBean> signInRecord;
    public List<SigninDayBean.RowsBean> getSignInList() {
        return signInList;
    }
    public void setSignInList(List<SigninDayBean.RowsBean> signInList) {
        this.signInList = signInList;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public List<SiginInRecordBean> getSignInRecord() {
        return signInRecord;
    }

    public void setSignInRecord(List<SiginInRecordBean> signInRecord) {
        this.signInRecord = signInRecord;
    }

    public static class SiginInRecordBean{

        public String	nickName;//": "大气的白菜",
        public String  handImg;//": null,
        public long signInTime;//": 1689229884000

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getHandImg() {
            return handImg;
        }

        public void setHandImg(String handImg) {
            this.handImg = handImg;
        }

        public long getSignInTime() {
            return signInTime;
        }

        public void setSignInTime(long signInTime) {
            this.signInTime = signInTime;
        }
    }

}
