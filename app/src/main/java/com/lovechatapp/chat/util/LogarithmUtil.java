package com.lovechatapp.chat.util;

/**
 * File description.
 * 求带对数工具 nSampleSize压缩时 需要此工具类
 * @author lihongjun
 * @date 2018/1/12
 */

public class LogarithmUtil {

    /**
     * 求幂数
     * @param value 要求的幂数的值
     * @param base 底数
     * @return 对数
     */
    public static double log(int value, int base) {
        return Math.log(value) / Math.log(base);
    }

    /**
     * 获取 已base为底数的 新的值 大于等于value
     * @param value
     * @param base
     * @return
     */
    public static int getLargePowerSize(int value, int base) {
        double log = log(value,base);
        //强制转换后会丢失精度,如果丢失精度的数和原数相等，说明就是整数
        if((int)log == log){
            return value;
        }else{
            return (int)Math.pow(base,(int)log + 1);
        }
    }
}
