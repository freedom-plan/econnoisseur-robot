package com.github.f.plan.econnoisseur.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5Util
 *
 * @author Kevin Huang
 * @since version
 * 2018年07月05日 09:47:00
 */
public class MD5Util {

    /**
     * 加密(MD5)
     *
     * @param data
     * @return 返回加密字串（小写）
     */
    public static String md5Lower(String data) {
        return DigestUtils.md5Hex(data);
    }

    /**
     * 加密(MD5)
     *
     * @param data
     * @return 返回加密字串（大写）
     */
    public static String md5Upper(String data) {
        return md5Lower(data).toUpperCase();
    }
}
