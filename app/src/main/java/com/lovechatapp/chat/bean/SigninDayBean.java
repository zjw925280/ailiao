package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

import java.util.List;

public class SigninDayBean extends BaseBean {
    private int day;
    private boolean isSignIn;
    private int total;//": 20,
    private List<RowsBean> rows;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public boolean isSignIn() {
        return isSignIn;
    }

    public void setSignIn(boolean signIn) {
        isSignIn = signIn;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<RowsBean> getRows() {
        return rows;
    }

    public void setRows(List<RowsBean> rows) {
        this.rows = rows;
    }

    public class RowsBean{
        private int  id;//": 12,
        private int day;//": 1,
        private int gold;//": 40.00
        private boolean signIn=false;

        public boolean isSignIn() {
            return signIn;
        }

        public void setSignIn(boolean signIn) {
            this.signIn = signIn;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getGold() {
            return gold;
        }

        public void setGold(int gold) {
            this.gold = gold;
        }
    }

}
