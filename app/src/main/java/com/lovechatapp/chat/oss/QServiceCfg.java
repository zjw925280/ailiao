package com.lovechatapp.chat.oss;

import android.content.Context;

import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.lovechatapp.chat.constant.Constant;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：腾讯云oss
 * 作者：
 * 创建时间：2018/7/3
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class QServiceCfg {

    private CosXmlService mCosXmlService;

    private static volatile QServiceCfg mInstance;

    public static QServiceCfg instance(Context context) {
        if (mInstance == null) {
            synchronized (QServiceCfg.class) {
                mInstance = new QServiceCfg(context);
            }
        }
        return mInstance;
    }

    private QServiceCfg(Context context) {
        //初始化服务配置 CosXmlServiceConfig
        CosXmlServiceConfig cosXmlServiceConfig = new CosXmlServiceConfig.Builder()
                .isHttps(false)
//                .setAppidAndRegion(Constant.TENCENT_CLOUD_APP_ID, Constant.TENCENT_CLOUD_REGION)
                .setAppidAndRegion(Constant.TENCENT_CLOUD_APP_ID, Constant.TENCENT_CLOUD_REGION)
                .setDebuggable(true)
                .builder();
        //创建获取签名类(请参考下面的生成签名示例，或者参考 sdk中提供的ShortTimeCredentialProvider类）
        LocalCredentialProvider localCredentialProvider = new LocalCredentialProvider(Constant.TENCENT_CLOUD_SECRET_ID,
                Constant.TENCENT_CLOUD_SECRET_KEY, 600);
        // 初始化服务类 CosXmlService
        mCosXmlService = new CosXmlService(context, cosXmlServiceConfig, localCredentialProvider);
    }

    public CosXmlService getCosCxmService() {
        return mCosXmlService;
    }

}
