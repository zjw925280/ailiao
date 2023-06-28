package com.lovechatapp.chat.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.listener.OnLuCompressListener;
import com.lovechatapp.chat.util.FileUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.util.permission.PermissionUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：图片处理公共页面
 * 作者：
 * 创建时间：2018/6/26
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ImageHelper {

    /**
     * 打开知乎图片视频选择
     */
    public static void openPictureVideoChoosePage(Activity activity, int requestCode) {

        PermissionUtil.requestPermissions(activity, new PermissionUtil.OnPermissionListener() {
            @Override
            public void onPermissionGranted() {
                //已授权
                Matisse.from(activity)
                        .choose(MimeType.ofAll(), true)//照片
                        .countable(true)//有序选择图片
                        .maxSelectable(1)//最大选择数量为9
                        .gridExpectedSize(activity.getResources().getDimensionPixelSize(R.dimen.grid_expected_size))//图片显示表格的大小getResources()
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//图像选择和预览活动所需的方向。
                        .thumbnailScale(0.65f)//缩放比例
                        .imageEngine(new GlideEngine())//加载方式
                        .showSingleMediaType(true)
                        .forResult(requestCode);//请求码
            }

            @Override
            public void onPermissionDenied() {
                ToastUtil.INSTANCE.showToast("没有文件读写权限，无法选择裁剪");
            }
        }, PermissionUtil.filePermission);

    }

    public static void openPictureChoosePage(Activity activity, int requestCode, int count) {

        PermissionUtil.requestPermissions(activity, new PermissionUtil.OnPermissionListener() {

            @Override
            public void onPermissionGranted() {
                //已授权
                Matisse.from(activity)
                        .choose(MimeType.ofImage())//照片
                        .countable(true)//有序选择图片
                        .maxSelectable(count)//最大选择数量为count
                        .gridExpectedSize(activity.getResources().getDimensionPixelSize(R.dimen.grid_expected_size))//图片显示表格的大小getResources()
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//图像选择和预览活动所需的方向。
                        .thumbnailScale(0.65f)//缩放比例
                        .imageEngine(new GlideEngine())//加载方式
                        .showSingleMediaType(true)
                        .forResult(requestCode);//请求码
            }

            @Override
            public void onPermissionDenied() {
                ToastUtil.INSTANCE.showToast("没有文件读写权限，无法选择裁剪");
            }
        }, PermissionUtil.filePermission);

    }

    /**
     * 打开知乎图片选择
     */
    public static void openPictureChoosePage(Activity activity, int requestCode) {
        openPictureChoosePage(activity, requestCode, 1);
    }

    /**
     * 选择视频
     */
    public static void openVideoChoosePage(Activity activity, int requestCode) {

        PermissionUtil.requestPermissions(activity, new PermissionUtil.OnPermissionListener() {
            @Override
            public void onPermissionGranted() {

                //已授权
                Matisse.from(activity)
                        .choose(MimeType.ofVideo())//视频
                        .countable(true)//有序选择图片
                        .maxSelectable(1)//最大选择数量为9
                        .gridExpectedSize(activity.getResources().getDimensionPixelSize(R.dimen.grid_expected_size))//图片显示表格的大小getResources()
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//图像选择和预览活动所需的方向。
                        .thumbnailScale(0.65f)//缩放比例
                        .imageEngine(new GlideEngine())//加载方式
                        .showSingleMediaType(true)
                        .forResult(requestCode);//请求码

            }

            @Override
            public void onPermissionDenied() {
                ToastUtil.INSTANCE.showToast("没有文件读写权限，无法选择裁剪");
            }
        }, PermissionUtil.filePermission);

    }

    /**
     * 使用LuBan压缩图片
     *
     * @param context            上下文
     * @param filePath           选择后的图片,一般是Matisse选择后
     * @param targetDir          压缩后的目标目录
     * @param luCompressListener 回调
     */
    public static void compressImageWithLuBan(Context context, String filePath, String targetDir, final OnLuCompressListener luCompressListener) {
        File pFile = new File(FileUtil.YCHAT_DIR);
        if (!pFile.exists()) {
            pFile.mkdir();
        }
        File file = new File(targetDir);
        if (!file.exists()) {
            file.mkdir();
        } else {
            FileUtil.deleteFiles(targetDir);
        }
        Luban.with(context)
                .load(filePath)
                .ignoreBy(50)
                .setTargetDir(targetDir)
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        luCompressListener.onStart();
                    }

                    @Override
                    public void onSuccess(File file) {
                        luCompressListener.onSuccess(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        luCompressListener.onError(e);
                    }
                }).launch();
    }

    /**
     * 使用LuBan压缩图片,不删除前一张
     *
     * @param context            上下文
     * @param filePath           选择后的图片,一般是Matisse选择后
     * @param targetDir          压缩后的目标目录
     * @param luCompressListener 回调
     */
    public static void compressImageWithLuBanNotDelete(Context context, String filePath, String targetDir,
                                                       final OnLuCompressListener luCompressListener) {
        File pFile = new File(FileUtil.YCHAT_DIR);
        if (!pFile.exists()) {
            pFile.mkdir();
        }
        File file = new File(targetDir);
        if (!file.exists()) {
            file.mkdir();
        }
        Luban.with(context)
                .load(filePath)
                .ignoreBy(50)
                .setTargetDir(targetDir)
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        luCompressListener.onStart();
                    }

                    @Override
                    public void onSuccess(File file) {
                        luCompressListener.onSuccess(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        luCompressListener.onError(e);
                    }
                }).launch();
    }

}
