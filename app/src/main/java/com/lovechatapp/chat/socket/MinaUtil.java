package com.lovechatapp.chat.socket;

/**
 *
 */
public class MinaUtil {

    /**
     * byte[]转int
     */
    static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        //由高位到低位
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;//往高位游
        }
        return value;
    }

}
