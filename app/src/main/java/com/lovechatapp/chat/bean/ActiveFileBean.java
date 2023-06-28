package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：动态List 图片 Bean
 * 作者：
 * 创建时间：2018/12/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActiveFileBean extends BaseBean {

    public int t_id;//	文件编号
    public int t_file_type;//文件地址0.图片1.视频
    public String t_file_url;//	文件地址
    public int t_gold;
    public int t_is_private;//	是否私密 0.否1.是
    public int isConsume;//是否消费: 0.未消费 1.已消费
    public String t_cover_img_url;//视频封面
    public String t_video_time;//视频时长

    public boolean judgePrivate(int actorId) {
        return t_gold > 0
                && isConsume == 0
                && !AppManager.getInstance().getUserInfo().isVip()
                && AppManager.getInstance().getUserInfo().t_id != actorId;
    }

}
