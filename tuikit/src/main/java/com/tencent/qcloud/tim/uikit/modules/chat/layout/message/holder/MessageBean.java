package com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder;

public class MessageBean {
//    	        "inviterAge": "18",
//                "giftImg": "https:/mru-123-1257023374.cos.ap-shanghai.myqcloud.com/backstage/20230625/202306250448224302.gif",
//                "appointmentTime": 1690192320000,
//                "inviterId": 19316,
//                "appointmentId": "636",
//                "appointmentStatus": 0,
//                "inviterSex": "1",
//                "appointmentAddress": "尽我所能",
//                "inviterName": "心动用户29316",
//                "inviterPhone": "59689665856",
//                "remarks": "同我讲啦"


    private  int inviterAge;
    private  String giftImg;
    private  long appointmentTime;
    private  int inviterId;
    private  int appointmentId;
    private  int appointmentStatus;
    private  int inviterSex;
    private  String appointmentAddress;
    private  String inviterName;
    private  String inviterPhone;
    private  String remarks;

    public int getInviterAge() {
        return inviterAge;
    }

    public void setInviterAge(int inviterAge) {
        this.inviterAge = inviterAge;
    }

    public String getGiftImg() {
        return giftImg;
    }

    public void setGiftImg(String giftImg) {
        this.giftImg = giftImg;
    }

    public long getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(long appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public int getInviterId() {
        return inviterId;
    }

    public void setInviterId(int inviterId) {
        this.inviterId = inviterId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(int appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public int getInviterSex() {
        return inviterSex;
    }

    public void setInviterSex(int inviterSex) {
        this.inviterSex = inviterSex;
    }

    public String getAppointmentAddress() {
        return appointmentAddress;
    }

    public void setAppointmentAddress(String appointmentAddress) {
        this.appointmentAddress = appointmentAddress;
    }

    public String getInviterName() {
        return inviterName;
    }

    public void setInviterName(String inviterName) {
        this.inviterName = inviterName;
    }

    public String getInviterPhone() {
        return inviterPhone;
    }

    public void setInviterPhone(String inviterPhone) {
        this.inviterPhone = inviterPhone;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
