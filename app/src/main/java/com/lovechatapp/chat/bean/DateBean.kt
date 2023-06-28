package com.lovechatapp.chat.bean

import com.lovechatapp.chat.base.AppManager
import com.lovechatapp.chat.base.BaseBean
import com.lovechatapp.chat.ext.getPhoneMasked

class DateBean : BaseBean() {

    companion object {
        /**
         * 邀请状态——未接受
         */
        const val INVITE_TYPE_NEW = 0

        /**
         * 邀请状态——已接受
         */
        const val INVITE_TYPE_ACCEPTED = 1

        /**
         * 邀请状态——已拒绝
         */
        const val INVITE_TYPE_REFUSED = 2

        /**
         * 约会状态——已取消
         */
        const val INVITE_TYPE_CANCELED = 3

        /**
         * 约会状态——约会暗号已确认
         */
        const val INVITE_TYPE_CODE_VERIFY = 4

        /**
         * 约会状态——约会暗号自动确认
         */
        const val INVITE_TYPE_AUTO_VERIFY = 5

        /**
         * 约会状态——失效
         */
        const val INVITE_TYPE_INACTIVE = 7

        /**
         * 约会状态——未赴约
         */
        const val DATE_STATUS_NOT_GO = 0

        /**
         * 约会状态——已赴约
         */
        const val DATE_STATUS_MEET = 1

        /**
         * 约会状态——已失效
         */
        const val DATE_STATUS_INACTIVE = 2
    }

    /**
     * 约会id
     */
    var appointmentId = 0

    /**
     * 礼物图片
     */
    var giftImg = ""

    /**
     * 邀请者id
     */
    var inviterId = 0

    /**
     * 被邀请者id
     */
    var inviteeId = 0

    /**
     * 对方姓名
     */
    var name = ""

    /**
     * 对方手机号码
     */
    var phone = ""

    /**
     * 对方性别
     */
    var sex = 0

    /**
     * 对方年龄
     */
    var age = 0

    /**
     * 邀请者性别
     */
    var inviterSex = 0

    /**
     * 邀请者年龄
     */
    var inviterAge = 0

    /**
     * 邀请者姓名
     */
    var inviterName = ""

    /**
     * 邀请者电话
     */
    var inviterPhone = ""

    /**
     * 约会地点
     */
    var appointmentAddress = ""

    /**
     * 约会时间
     */
    var appointmentTime = 0L

    /**
     * 约会备注
     */
    var remarks = ""

    /**
     * 约会状态 0:发起约会 1：同意约会 2：拒绝约会 3:取消约会 4:验证码确认 5 自动确认 6申诉
     */
    var appointmentStatus = INVITE_TYPE_NEW

    /**
     * 发送时间
     */
    var createTime = 0L

    /**
     * 礼物图片地址
     */
    var giftPath = ""


    /**
     * 约会暗语
     */
    var appointmentCode = ""

    /**通过当前用户id与邀请者id[inviterId]对比判断是否为自己发起的约会*/
    fun isSelf(): Boolean {
        return inviterId == AppManager.getInstance().userInfo.t_id
    }

    /**
     * 通过当前约会的状态[appointmentStatus]和是否为自己发出的邀请[isSelf]，判断当前约会是否能够删除
     */
    fun canCancel(): Boolean {
        return when (appointmentStatus) {
            INVITE_TYPE_NEW -> {
                isSelf()
            }
            INVITE_TYPE_ACCEPTED -> {
                true
            }
            else -> {
                false
            }
        }
    }

    fun getPhoneText(): String {
        return if (appointmentStatus == INVITE_TYPE_ACCEPTED
            || appointmentStatus == INVITE_TYPE_CODE_VERIFY
            || appointmentStatus == INVITE_TYPE_AUTO_VERIFY
        ) {
            phone
        } else {
            phone.getPhoneMasked()
        }
    }
}