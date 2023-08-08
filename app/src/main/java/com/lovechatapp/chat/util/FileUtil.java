package com.lovechatapp.chat.util;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述:  文件相关工具类
 * 作者：
 * 创建时间：2018/6/25
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class FileUtil {

    //SD卡目录
    public static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath();
    //存放文件到ychat中
    public static final String YCHAT_DIR = SDCARD_PATH + File.separator + "jiayuan/";

    /**
     * 适配7.0获取Uri
     */
    public static Uri getUriAdjust24(Context context, File file) {
        Uri pictureUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//如果是7.0android系统
            pictureUri = FileProvider.getUriForFile(context, "com.lovechatapp.chat.fileProvider", file);
        } else {
            pictureUri = Uri.fromFile(file);
        }
        return pictureUri;
    }


    /**
     * 获取大于api19时获取相册中图片真正的uri
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPathAbove19(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * 删除目录
     */
    public static boolean deleteFiles(String path) {
        try {
            File file = new File(path);
            boolean flag = false;
            if (!file.exists()) {
                return true;
            }
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] fileList = file.listFiles();
                if (fileList != null && fileList.length > 0) {
                    for (int i = 0; i < fileList.length; i++) {
                        flag = deleteFiles(fileList[i].getAbsolutePath());
                    }
                }
            }
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void checkDirection(String path) {
        File file = new File(path);
        if (!file.exists()) {
            boolean ok = file.mkdirs();
            LogUtil.d("==-", path + ": create " + ok);
        }
    }

    /**
     * 视频50M文件限制
     */
    public static boolean checkVideoFileSize(File file) {
        return 50 * 1024 * 1024 - file.length() > 0;
    }

    public static boolean copyFile(String fromPath, String toPath) {
        File fromFile = new File(fromPath);
        File toFile = new File(toPath);
        if (!fromFile.exists()) {
            return false;
        }
        if (!fromFile.isFile()) {
            return false;
        }
        if (!fromFile.canRead()) {
            return false;
        }
        try {
            if (!toFile.getParentFile().exists()) {
                toFile.getParentFile().mkdirs();
            }
            if (toFile.exists()) {
                toFile.delete();
            }
            FileInputStream fosFrom = new FileInputStream(fromFile);
            FileOutputStream fosTo = new FileOutputStream(toFile);
            byte[] bt = new byte[1024];
            int c;
            while ((c = fosFrom.read(bt)) > 0) {
                fosTo.write(bt, 0, c);
            }
            //关闭输入、输出流
            fosFrom.close();
            fosTo.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String parseFloatToString(float num) {
        String temp = String.valueOf(num);
        if (temp.contains(".") && temp.split("\\.")[1].equals("0")) {
            return temp.substring(0, temp.indexOf("."));
        } else {
            return temp;
        }
    }
}