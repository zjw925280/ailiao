package com.lovechatapp.chat.helper;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Looper;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.lovechatapp.chat.glide.GlideCircleTransform;
import com.lovechatapp.chat.glide.GlideRoundTransform;

import jp.wasabeef.glide.transformations.BlurTransformation;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述:  Glide图片加载工具类工具类
 * 作者：
 * 创建时间：2018/6/26
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ImageLoadHelper {

    /**
     * 是否能使用Glide加载图片
     */
    private static boolean isCanLoadGlideImg(Context ctx) {
        if (ctx instanceof Activity) {
            Activity ac = (Activity) ctx;
            if (ac.isDestroyed() && Looper.myLooper() == Looper.getMainLooper()) {
                return false;
            }
        }
        return true;
    }

    /**
     * glide加载url
     */
    public static void glideShowImageWithUrl(Context context, String url, ImageView imageView) {
        if (!isCanLoadGlideImg(context)) {
            return;
        }
        Glide.with(context).load(url).into(imageView);
    }

    /**
     * glide加载url
     */
    public static void glideShowImageWithResource(Context context, int resourceId, ImageView imageView) {
        if (!isCanLoadGlideImg(context)) {
            return;
        }
        Glide.with(context).load(resourceId).centerCrop().into(imageView);
    }

    /**
     * glide加载url
     */
    public static void glideShowImageWithUrl(Context context, String url, ImageView imageView,
                                             int overWidth, int overHeight) {
        if (!isCanLoadGlideImg(context)) {
            return;
        }
        Glide.with(context).load(url).override(overWidth, overHeight).centerCrop().into(imageView);
    }

    /**
     * glide圆角加载url,resize
     */
    public static void glideShowCornerImageWithUrl(Context context, String url, ImageView imageView,
                                                   int cornerDp, int overWidth, int overHeight) {
        if (!isCanLoadGlideImg(context)) {
            return;
        }
        Glide.with(context)
                .load(url)
                .override(overWidth, overHeight)
                .transform(new CenterCrop(), new GlideRoundTransform(cornerDp))
                .into(imageView);
    }

    /**
     * glide圆角加载url,resize
     */
    public static void glideShowCornerImageWithUrl(Context context, String url, ImageView imageView) {
        if (!isCanLoadGlideImg(context)) {
            return;
        }
        Glide.with(context).load(url).transform(new CenterCrop(), new GlideRoundTransform(5)).into(imageView);
    }

    /**
     * glide圆角加载url,resize
     */
    public static void glideShowCorner15ImageWithUrl(Context context, String url, ImageView imageView) {
        if (!isCanLoadGlideImg(context)) {
            return;
        }
        Glide.with(context).load(url).transform(new CenterCrop(), new GlideRoundTransform(6)).into(imageView);
    }

    /**
     * glide圆形加载url
     */
    public static void glideShowCircleImageWithUrl(Context context, String url, ImageView imageView) {
        if (!isCanLoadGlideImg(context)) {
            return;
        }
        Glide.with(context).load(url).transform(new GlideCircleTransform(context)).into(imageView);
    }

    /**
     * glide圆形加载url,resize
     */
    public static void glideShowCircleImageWithUrl(Context context, String url, ImageView imageView,
                                                   int overWidth, int overHeight) {
        if (!isCanLoadGlideImg(context)) {
            return;
        }
        Glide.with(context).load(url).override(overWidth, overHeight).transform(new GlideCircleTransform(context)).into(imageView);
    }

    /**
     * glide圆形加载url,resize
     */
    public static void glideShowCircleImageWithByte(Context context, byte[] bytes, ImageView imageView, int overWidth, int overHeight) {
        if (!isCanLoadGlideImg(context)) {
            return;
        }
        Glide.with(context).load(bytes).override(overWidth, overHeight).transform(new GlideCircleTransform(context)).into(imageView);
    }

    /**
     * glide加载本地uri,resize
     */
    public static void glideShowImageWithUri(Context context, Uri file, ImageView imageView,
                                             int overWidth, int overHeight) {
        if (!isCanLoadGlideImg(context)) {
            return;
        }
        Glide.with(context).load(file).override(overWidth, overHeight).centerCrop().into(imageView);
    }

    /**
     * glide加载圆形本地uri,resize
     */
    public static void glideShowCircleImageWithUri(Context context, Uri file, ImageView imageView) {
        if (!isCanLoadGlideImg(context)) {
            return;
        }
        Glide.with(context).load(file).transform(new GlideCircleTransform(context)).into(imageView);
    }

    /**
     * glide加载圆形本地uri,resize
     */
    public static void glideShowCircleImageWithUri(Context context, Uri file, ImageView imageView,
                                                   int overWidth, int overHeight) {
        if (!isCanLoadGlideImg(context)) {
            return;
        }
        Glide.with(context).load(file).override(overWidth, overHeight).transform(
                new GlideCircleTransform(context)).into(imageView);
    }

    /**
     * glide加载圆角本地uri,resize
     */
    public static void glideShowCornerImageWithUri(Context context, Uri file, ImageView imageView,
                                                   int cornerDp, int overWidth, int overHeight) {
        if (!isCanLoadGlideImg(context)) {
            return;
        }
        Glide.with(context).load(file).override(overWidth, overHeight).transform(
                new GlideRoundTransform(cornerDp)).into(imageView);
    }

    /**
     * 高斯模糊
     */
    public static void glideShowImageWithFade(Context context, String url, ImageView imageView,
                                              int overWidth, int overHeight) {
        if (!isCanLoadGlideImg(context)) {
            return;
        }
        Glide.with(context)
                .load(url)
                .override(overWidth, overHeight)
                .transition(DrawableTransitionOptions.withCrossFade(1000))
                // “23”：设置模糊度(在0.0到25.0之间)，默认”25";"4":图片缩放比例,默认“1”。
                .transform(new CenterCrop(), new BlurTransformation(100, 2))
                .into(imageView);
    }

    /**
     * 高斯模糊
     */
    public static void glideShowImageWithFade(Context context, String url, ImageView imageView) {
        if (!isCanLoadGlideImg(context)) {
            return;
        }
        Glide.with(context)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade(1000))
                // “23”：设置模糊度(在0.0到25.0之间)，默认”25";"4":图片缩放比例,默认“1”。
                .transform(new CenterCrop(), new BlurTransformation(100, 5))
                .into(imageView);
    }

    /**
     * 高斯模糊
     */
    public static void glideShowCornerImageWithFade(Context context, String url, ImageView imageView) {
        if (!isCanLoadGlideImg(context)) {
            return;
        }
        Glide.with(context)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade(1000))
                // “23”：设置模糊度(在0.0到25.0之间)，默认”25";"4":图片缩放比例,默认“1”。
                .transform(new CenterCrop(), new BlurTransformation(100, 2),
                        new GlideRoundTransform(2))
                .into(imageView);
    }

    /**
     * 高斯模糊
     */
    public static void glideShowCircleImageWithFade(Context context, String url, ImageView imageView, int overSize) {
        if (!isCanLoadGlideImg(context)) {
            return;
        }
        Glide.with(context)
                .load(url)
                .override(overSize, overSize)
                .transition(DrawableTransitionOptions.withCrossFade(1000))
                .transform(
                        new CenterCrop(),
                        new BlurTransformation(100, 2),
                        new GlideCircleTransform(context))
                .into(imageView);
    }
}