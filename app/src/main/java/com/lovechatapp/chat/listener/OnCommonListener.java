package com.lovechatapp.chat.listener;

/**
 * 公共回调接口
 *
 * @param <T>
 */
public interface OnCommonListener<T> {
    void execute(T t);
}