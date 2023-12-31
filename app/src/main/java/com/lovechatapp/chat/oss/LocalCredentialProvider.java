package com.lovechatapp.chat.oss;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.utils.StringUtils;
import com.tencent.qcloud.core.auth.BasicLifecycleCredentialProvider;
import com.tencent.qcloud.core.auth.BasicQCloudCredentials;
import com.tencent.qcloud.core.auth.QCloudLifecycleCredentials;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 方法一：使用永久密钥进行签名
 */
public class LocalCredentialProvider extends BasicLifecycleCredentialProvider {

    private String secretKey;
    private long keyDuration;
    private String secretId;

    public LocalCredentialProvider(String secretId, String secretKey, long keyDuration) {
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.keyDuration = keyDuration;
    }

    /**
     * 返回 BasicQCloudCredentials
     */
    @Override
    public QCloudLifecycleCredentials fetchNewCredentials() throws CosXmlClientException {
        long current = System.currentTimeMillis() / 1000L;
        long expired = current + keyDuration;
        String keyTime = current + ";" + expired;
        return new BasicQCloudCredentials(secretId, secretKeyToSignKey(secretKey, keyTime), keyTime);
    }

    private String secretKeyToSignKey(String secretKey, String keyTime) {
        String signKey = null;
        try {
            if (secretKey == null) {
                throw new IllegalArgumentException("secretKey is null");
            }
            if (keyTime == null) {
                throw new IllegalArgumentException("qKeyTime is null");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        try {
            byte[] byteKey = secretKey.getBytes("utf-8");
            SecretKey hmacKey = new SecretKeySpec(byteKey, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(hmacKey);
            signKey = StringUtils.toHexString(mac.doFinal(keyTime.getBytes("utf-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signKey;
    }
}
