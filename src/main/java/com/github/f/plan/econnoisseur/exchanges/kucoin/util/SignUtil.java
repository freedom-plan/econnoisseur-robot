package com.github.f.plan.econnoisseur.exchanges.kucoin.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Copyright (C), 2018
 *
 * @author jimmy
 * @desc SignUtil
 * @date 2018/7/26
 */
public class SignUtil {
    private static final String CHARSET_UTF8_STR = "UTF-8";
    private static final Charset CHARSET_UTF8 = Charset.forName(CHARSET_UTF8_STR);
    private Mac mac;

    public SignUtil(String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(CHARSET_UTF8), "HmacSHA256" );
        Mac mac = Mac.getInstance( "HmacSHA256");
        mac.init(key);
        this.mac = mac;
    }

    public String getSign(Map<String, Object> map, String end_point, String nonce) {
        List<String> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys);

        StringBuilder signStr = new StringBuilder();
        signStr.append(end_point)
            .append("/")
            .append(nonce)
            .append("/");
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            if (i == keys.size() - 1) { // 拼接时，不包括最后一个&字符
                signStr.append(key)
                    .append("=")
                    .append(map.get(key));
            } else {
                signStr.append(key)
                    .append("=")
                    .append(map.get(key))
                    .append("&");
            }
        }

        String encode = Base64.encodeBase64String(signStr.toString().getBytes(CHARSET_UTF8));
        return Hex.encodeHexString(this.mac.doFinal(encode.getBytes(CHARSET_UTF8)));
    }
}
