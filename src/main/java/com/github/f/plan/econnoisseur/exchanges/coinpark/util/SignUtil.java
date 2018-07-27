package com.github.f.plan.econnoisseur.exchanges.coinpark.util;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Copyright (C), 2018
 *
 * @author jimmy
 * @version 0.1.0
 * @desc econnoisseur-robot
 * @date 18-7-16 下午7:25
 */
public class SignUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignUtil.class);

    private static final String CHARSET_UTF8_STR = "UTF-8";
    private static final Charset CHARSET_UTF8 = Charset.forName(CHARSET_UTF8_STR);

    private Mac mac;

    public SignUtil(String secretKey) {
        this.mac = this.setMac(secretKey);
    }

    private Mac setMac(String secretKey) {
        SecretKey key = new SecretKeySpec( secretKey.getBytes(CHARSET_UTF8), "HmacMD5");
        Mac mac = null;
        try {
            mac = Mac.getInstance( "HmacMD5" );
            mac.init(key);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            LOGGER.info("还原密钥失败");
        }
        return mac;
    }

    public String getSign(String data) {
        if (null == this.mac) {
            return null;
        }
        return Hex.encodeHexString(this.mac.doFinal( data.getBytes( CHARSET_UTF8)));
    }

    public Mac getMac() {
        return this.mac;
    }

    public SignUtil setMac(Mac mac) {
        this.mac = mac;
        return this;
    }
}
