package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主播资料页下方主播视频/照片bean
 * 作者：
 * 创建时间：2018/7/6
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class AlbumBean extends BaseBean {

    public int t_is_private;//是否私密：0.否1.是
    public String t_handImg;//头像地址
    public int t_money;//	约豆(可能为空)
    public String t_addres_url;//	图片或者视频地址
    public String t_video_img;//视频地址封面
    public String t_nickName;//		昵称
    public String t_title;//	标题
    public int t_file_type;//	文件类型 0.图片 1.视频
    public int t_id;//	数据编号
    public int is_see;//是否查看过：0.未查看1.已查看
    public int t_user_id;//	视频发布人编号
    public int t_auditing_type = 3;//	文件状态 0.审核中1.已通过2.审核失败 (私密图片存在)

    public int laudtotal;//	总的点赞数
    public int t_is_first;

    public boolean isLock() {
        return t_is_private == 1;
    }

    /**
     * 是否可观看
     */
    public boolean canSee() {
        return !(t_is_private == 1
                && is_see == 0
                && AppManager.getInstance().getUserInfo().t_id != t_user_id
                && !AppManager.getInstance().getUserInfo().isVip()
        );
    }

}