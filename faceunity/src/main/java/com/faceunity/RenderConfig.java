package com.faceunity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.faceunity.entity.Filter;

import java.io.Serializable;

/**
 * 美颜参数
 */
public class RenderConfig implements Serializable {

    public String mFilterName = Filter.Key.FENNEN_1;//粉嫩
    public float mFilterLevel = 1.0f;//滤镜强度
    public float mSkinDetect = 1.0f;//肤色检测开关
    public float mHeavyBlur = 0.0f;//重度磨皮开关
    public float mBlurLevel = 0.7f;//磨皮程度
    public float mColorLevel = 0.3f;//美白
    public float mRedLevel = 0.3f;//红润
    public float mEyeBright = 0.0f;//亮眼
    public float mToothWhiten = 0.0f;//美牙
    public float mFaceShape = FURenderer.BeautificationParams.FACE_SHAPE_CUSTOM;//脸型
    public float mFaceShapeLevel = 1.0f;//程度
    public float mCheekThinning = 0f;//瘦脸
    public float mCheekV = 0.5f;//V脸
    public float mCheekNarrow = 0f;//窄脸
    public float mCheekSmall = 0f;//小脸
    public float mEyeEnlarging = 0.4f;//大眼
    public float mIntensityChin = 0.3f;//下巴
    public float mIntensityForehead = 0.3f;//额头
    public float mIntensityMouth = 0.4f;//嘴形
    public float mIntensityNose = 0.5f;//瘦鼻
    public float mChangeFrames = 0f;//渐变帧数

    /**
     * 保存美颜参数
     *
     * @param context
     */
    public final void save(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String saveData = JSON.toJSONString(this);
        sharedPreferences.edit().putString("RenderConfig", saveData).apply();
    }

    /**
     * 获取美颜参数
     *
     * @param context
     * @return
     */
    public static RenderConfig get(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String saveData = sharedPreferences.getString("RenderConfig", null);
        if (TextUtils.isEmpty(saveData)) {
            return new RenderConfig();
        } else {
            return JSON.parseObject(saveData, RenderConfig.class);
        }
    }
}