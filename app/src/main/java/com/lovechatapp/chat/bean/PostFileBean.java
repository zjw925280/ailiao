package com.lovechatapp.chat.bean;


import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：发布动态文件bean
 * 作者：
 * 创建时间：2018/12/21
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PostFileBean extends BaseBean {

    public int fileType = 0;//文件类型 (0.图片 1.视频),
    public String t_cover_img_url = "";//视频封面地址
    public String fileUrl = "";//文件url地址,
    public int gold = 0;//私密时的约豆数(公开为0),
    public String fileId = "";//文件号(腾讯云) 图片是null
    public int t_is_private = 0;//是否私密(0.否 1.是)
    public String t_video_time = "";//	视频时长

}
