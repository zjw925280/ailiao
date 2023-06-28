package com.lovechatapp.chat.util;

import android.text.TextUtils;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;

import java.text.SimpleDateFormat;

public class BeanParamUtil {

    //        0.图文 1.视频 2.语音
    public static final int FileTypePicture = 0;

    public static final int FileTypeVideo = 1;

    public static final int FileTypeAudio = 2;

    public static String getMyRank(int rank) {
        if (rank > 50) {
            return "50名以后";
        }
        return rank + "";
    }

    /**
     * "isFollow"://0:未关注 1:已关注
     * "isCoverFollow"://0:未关注 1:已关注
     *
     * @param isFollow
     * @param isCoverFollow
     * @return
     */
    public static String getFocus(int isFollow, int isCoverFollow) {
        if (isFollow == 1 && isCoverFollow == 1) {
            return "互相关注";
        } else if (isFollow == 1) {
            return "已关注";
        }
        return "关注";
    }

    public static String getSex(int sex) {
        return sex == 0 ? "女" : "男";
    }

    public static String getFocus(int isFollow) {
        return isFollow == 1 ? "已关注" : "关注";
    }

    public static String generateFileName(String fileName) {
        return "android_" + AppManager.getInstance().getUserInfo().t_id + "_" + System.currentTimeMillis() + "_" + fileName;
    }

    /**
     * 拼接关键字
     *
     * @param keyword
     * @return
     */
    public static String appendKeyword(String keyword) {
        return "【" + keyword + "】";
    }

    /**
     * 拼接话题
     *
     * @param keyword
     * @return
     */
    public static String appendTopic(String keyword) {
        return "#" + keyword + "#";
    }

    public static String getTA(int sex, int actorId) {
        if (AppManager.getInstance().getUserInfo().t_id == actorId)
            return "我";
        return sex == 0 ? "她" : "他";
    }

    public static String getStrIfNull(String s) {
        if (!TextUtils.isEmpty(s)) {
            return s;
        }
        return "";
    }

    public static String getAge(String age) {
        if (!TextUtils.isEmpty(age)) {
            return String.format(AppManager.getInstance().getString(R.string.data_age), age);
        }
        return "";
    }

    public static String getAge(int age) {
        if (age != 0) {
            return String.format(AppManager.getInstance().getString(R.string.data_age), age + "");
        }
        return "";
    }

    public static String getSign(String sign) {
        if (TextUtils.isEmpty(sign)) {
            return AppManager.getInstance().getString(R.string.lazy);
        }
        return sign;
    }

    //0=在线 1=离线
    public static String getOnlineText(int online) {
        int[] ints = {R.string.free, R.string.offline};
        if (online > ints.length - 1)
            return "";
        return AppManager.getInstance().getString(ints[online]);
    }

    /**
     * 根据时间戳来判断当前的时间是几天前,几分钟,刚刚
     *
     * @param long_time
     * @return
     */
    public static String getTimeState(long long_time) {

        SimpleDateFormat format = new SimpleDateFormat("MM-dd");

        long min_conver = 1000 * 60;
        long hour_conver = min_conver * 60;
        long day_conver = hour_conver * 24;

        long time_conver = System.currentTimeMillis() - long_time;

        long temp_conver;

        if ((time_conver / day_conver) < 3) {
            temp_conver = time_conver / day_conver;
            if (temp_conver <= 2 && temp_conver >= 1) {
                return temp_conver + "天前";
            } else {
                temp_conver = (time_conver / hour_conver);
                if (temp_conver >= 1) {
                    return temp_conver + "小时前";
                } else {
                    temp_conver = (time_conver / min_conver);
                    if (temp_conver >= 1) {
                        return temp_conver + "分钟前";
                    } else {
                        return "刚刚";
                    }
                }
            }
        } else {
            return format.format(long_time);
        }
    }
}