package com.github.f.plan.econnoisseur.exchanges.gate.io.util;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;

/**
 * SignUtil
 *
 * @author Kevin Huang
 * @since version
 * 2018年07月05日 10:00:00
 */
public class SignUtil {
    public static final String CHARSET_UTF8_STR = "UTF-8";
    public static final Charset CHARSET_UTF8 = Charset.forName(CHARSET_UTF8_STR);

    public static Mac mac(String secretKey) throws Exception {
        SecretKeySpec key = new SecretKeySpec( secretKey.getBytes(CHARSET_UTF8), "HmacSHA512" );
        Mac mac = Mac.getInstance( "HmacSHA512" );
        mac.init(key);
        return mac;
    }

    public static String getSign(Mac mac, String data) {
        return Hex.encodeHexString( mac.doFinal( data.getBytes( CHARSET_UTF8)));
    }
}
