package com.lovechatapp.chat.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ThumbnailUtils;

import com.lovechatapp.chat.constant.Constant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述:  Bitmap工具类
 * 作者：
 * 创建时间：2018/6/25
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class BitmapUtil {

    // 分享图片的最大缩放数据大小
    public static final int MAX_THUMBNAIL_DATA_LENGTH = 32 * 1024;

    /**
     * 将文件生成缩略图文件
     */
    public static File saveImageThumbnail(File file) {
        Bitmap bitmap = getBitmapByFile(file);
        Bitmap thumbBitmap = getImageThumbnail(bitmap, MAX_THUMBNAIL_DATA_LENGTH);
        bitmap.recycle();
        if (file.getName().toLowerCase().endsWith(".png"))
            return saveBitmapAsPng(thumbBitmap, Constant.SHARE_PATH + "thumb.png");
        else
            return saveBitmapAsPng(thumbBitmap, Constant.SHARE_PATH + "thumb.jpg");
    }

    /**
     * 将文件生成缩略图文件
     */
    public static File saveImageThumbnail(Bitmap bitmap) {
        Bitmap tnumbBitmap = getImageThumbnail(bitmap, MAX_THUMBNAIL_DATA_LENGTH);
        return saveBitmapAsPng(tnumbBitmap, Constant.SHARE_PATH + "thumb.png");
    }

    /**
     * 获取缩略图
     *
     * @return 缩略图数组
     */
    public static Bitmap getBitmapByFile(File file) {
        return BitmapFactory.decodeFile(file.getPath());
    }

    /**
     * 缩略图数组
     *
     * @param bitmap    传入的bitmap
     * @param maxLength 压缩到多少
     * @return byte 数组
     */
    public static Bitmap getImageThumbnail(Bitmap bitmap, int maxLength) {
        if (bitmap == null) {
            return null;
        }
        double scale = getThumbScale(bitmap.getWidth(), bitmap.getHeight(), maxLength);
        return getImageThumbnail(bitmap, (int) (bitmap.getWidth() * scale), (int) (bitmap.getHeight() * scale));
    }


    /**
     * 获取压缩到指定大小时图片的缩放比例
     *
     * @param srcWidth
     * @param srcHeight
     * @param maxSize
     * @return
     */
    private static double getThumbScale(int srcWidth, int srcHeight, int maxSize) {
        return Math.sqrt((float) maxSize / (float) (4 * srcWidth * srcHeight));
    }


    /**
     * 获取缩略图
     *
     * @param bitmap 文件路径
     * @param width  缩略图宽度
     * @param height 缩略图高度
     * @return 图像byte数组
     */
    private static Bitmap getImageThumbnail(Bitmap bitmap, int width, int height) {
        Bitmap result = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //获取图片高度
        int h = bitmap.getWidth();
        int w = bitmap.getHeight();
        int scaleWidth = w / width;
        int scaleHeight = h / height;
        int scale = 1;
        if (scaleWidth < scaleHeight) {
            scale = scaleWidth;
        } else {
            scale = scaleHeight;
        }
        //判断缩放比是否符合条件
        if (scale <= 0) {
            scale = 1;
        }
        options.inSampleSize = LogarithmUtil.getLargePowerSize(scale, 2);
        // 重新读入图片，读取缩放后的bitmap，注意这次要把inJustDecodeBounds 设为 false
        options.inJustDecodeBounds = false;
        try {
            byte[] data = bitmap2Byte(bitmap);
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
//            result = bitmap2Byte(bitmap);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * bitmap 转byte数组
     *
     * @param bitmap
     * @return
     * @throws IOException
     */
    private static byte[] bitmap2Byte(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] data = bos.toByteArray();
        bos.close();
        return data;
    }


    /**
     * 获取缩略图
     *
     * @param imagePath:文件路径
     * @param width:缩略图宽度
     * @param height:缩略图高度
     */
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //关于inJustDecodeBounds的作用将在下文叙述
        Bitmap bitmap;
        BitmapFactory.decodeFile(imagePath, options);
        int h = options.outHeight;//获取图片高度
        int w = options.outWidth;//获取图片宽度
        int scaleWidth = w / width; //计算宽度缩放比
        int scaleHeight = h / height; //计算高度缩放比
        int scale;//初始缩放比
        if (scaleWidth < scaleHeight) {//选择合适的缩放比
            scale = scaleWidth;
        } else {
            scale = scaleHeight;
        }
        if (scale <= 0) {//判断缩放比是否符合条件
            scale = 1;
        }
        options.inSampleSize = scale;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把inJustDecodeBounds 设为 false
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 缩放bitmap
     */
    public static Bitmap zoomSmallBitmap(Bitmap sourceBitmap, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = sourceBitmap.getWidth();
        int height = sourceBitmap.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        return Bitmap.createBitmap(sourceBitmap, 0, 0, width, height, matrix, true);
    }

    /**
     * 将bitmap转成png
     */
    public static File saveBitmapAsPng(Bitmap bitmap, String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            FileUtil.checkDirection(file.getParent());
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将bitmap转成png
     */
    public static File saveBitmapAsJpeg(Bitmap bitmap, String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将bitmap转成png
     */
    public static File saveBitmapAsJpg(Bitmap bitmap, String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap setBackGroundColor(Bitmap orginBitmap) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        Bitmap bitmap = Bitmap.createBitmap(orginBitmap.getWidth(),
                orginBitmap.getHeight(), orginBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, orginBitmap.getWidth(), orginBitmap.getHeight(), paint);
        canvas.drawBitmap(orginBitmap, 0, 0, paint);
        return bitmap;
    }
}
