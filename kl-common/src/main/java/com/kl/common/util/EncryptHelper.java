package com.kl.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密辅助类
 *
 */
@Slf4j
public class EncryptHelper {

    /**
     * 构造函数
     */
    private EncryptHelper() {}

    /**
     * 转化MD5编码
     *
     * @param value 字符串值
     * @return MD5编码
     */
    public static String toMD5(String value) {
        try {
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                digest.update(value.getBytes("UTF-8"));
                return HexHelper.toHexFromBytes(digest.digest());
            } catch (UnsupportedEncodingException e) {
                log.warn("转化MD5编码异常", e);
            }
        } catch (NoSuchAlgorithmException e) {
            log.warn("转化MD5编码异常", e);
        }
        return "";
    }

}
