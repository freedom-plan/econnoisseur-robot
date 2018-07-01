package com.github.kevin.econnoisseur.exchanges.kkcoin.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSAUtil
 *
 * @author qianjc
 * @version 0.0.1
 * @desc rsa加密工具类
 * @date 2017-12-25 09:31:48
 */
public class RSAUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(RSAUtil.class);

    private static String KEY_ALGORITHM = "RSA";

    public static final int KEY_SIZE = 2048; // 密钥长度, 一般2048
    public final static String KEY_PKCS12 = "PKCS12";

    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";// sign值生成方式
    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    private String signature;// 签名sign生成模式
    private Charset charset;// 编码格式

    private static KeyFactory keyFactory;
    static {
        try {
            keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.info("生成RSA密钥对异常，" + e);
        }
    }

    public RSAUtil(Charset charset, String signature) {
        this.signature = signature;
        this.charset = charset;
    }

    /**
     * 公钥还原，将公钥转化为PublicKey对象
     * @param publicKeyStr 公钥字符串
     * @return PublicKey对象
     */
    public PublicKey restorePublicKey(String publicKeyStr) throws InvalidKeySpecException {
        PublicKey publicKey = null;

        byte[] keyBytes = Base64.decodeBase64(publicKeyStr.getBytes(charset));
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        try {
            publicKey = keyFactory.generatePublic(x509KeySpec);
        } catch (InvalidKeySpecException e) {
            LOGGER.info("公钥还原，将公钥转化为PublicKey对象异常", e);
            throw e;
        }

        return publicKey;
    }

    /**
     * 私钥还原，将私钥转化为privateKey对象
     * @param privateKeyStr 私钥字符串
     * @return PrivateKey对象
     */
    public PrivateKey restorePrivateKey(String privateKeyStr) throws InvalidKeySpecException {
        PrivateKey privateKey = null;

        byte[] keyBytes = Base64.decodeBase64(privateKeyStr.getBytes(charset));
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (InvalidKeySpecException e) {
            LOGGER.info("私钥还原，将私钥转化为privateKey对象异常", e);
            throw e;
        }

        return privateKey;
    }

    /**
     * RSA生成sign值
     * @param privateKey 私钥
     * @param data 数据
     * @return sign值
     * @throws Exception 异常
     */
    public String generateSign(PrivateKey privateKey, String data) throws Exception {
        return generateSign(privateKey, data.getBytes(charset));
    }

    /**
     * RSA生成sign值
     * @param privateKey 私钥
     * @param data 数据
     * @return sign值
     * @throws Exception 异常
     */
    public String generateSign(PrivateKey privateKey, byte[] data) throws Exception {
        Signature signature = Signature.getInstance(this.signature);
        signature.initSign(privateKey);
        signature.update(data);
        return Base64.encodeBase64String(signature.sign());
    }

    /**
     * RSA验签
     * @param publicKey 公钥
     * @param data 数据
     * @param sign sign值
     * @return 验签是否成功
     * @throws Exception 异常
     */
    public boolean verifyRSA(PublicKey publicKey, String data, String sign) throws Exception {
        return verifyRSA(publicKey, data.getBytes(charset), Base64.decodeBase64(sign));
    }

    /**
     * RSA验签
     * @param publicKey 公钥
     * @param data 数据
     * @param sign sign值
     * @return 验签是否成功
     * @throws Exception 异常
     */
    public boolean verifyRSA(PublicKey publicKey, byte[] data, byte[] sign) throws Exception {
        Signature signature = Signature.getInstance(this.signature);
        signature.initVerify(publicKey);
        signature.update(data);

        return signature.verify(sign);
    }
}
