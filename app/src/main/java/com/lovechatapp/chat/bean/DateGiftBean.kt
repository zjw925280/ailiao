package com.lovechatapp.chat.bean

import com.lovechatapp.chat.base.BaseBean

/**
 * 约会礼物数据对象
 */
class DateGiftBean : BaseBean() {
    /**礼物名称*/
    var t_gift_name = ""

    /**礼物图片*/
    var t_gift_still_url = ""

    /**礼物价格（约豆）*/
    var t_gift_gold = 0

    /**礼物id*/
    var t_gift_id = 0

    /**是否被选择*/
    var isSelected = false
}