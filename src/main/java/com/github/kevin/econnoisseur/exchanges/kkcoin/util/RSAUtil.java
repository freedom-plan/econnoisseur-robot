package com.github.kevin.econnoisseur.exchanges.kkcoin.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;

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
    public static final int RESERVE_BYTES = 11;
    public final static String KEY_PKCS12 = "PKCS12";

    public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding"; // 加密block需要预留11字节
    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";// sign值生成方式
    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    private String algorithm;// 密钥生成模式
    private String signature;// 签名sign生成模式
    private Charset charset;// 编码格式

    private int keySize;// RSA密钥长度必须是64的倍数，在512~65536之间
    private int decryptBlock; // 默认keySize=2048的情况下, 256 bytes
    private int encryptBlock; // 默认keySize=2048的情况下, 245 bytes

    private static KeyFactory keyFactory;
    static {
        try {
            keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.info("生成RSA密钥对异常，" + e);
        }
    }

    public RSAUtil() {
        this(CIPHER_ALGORITHM);
    }

    public RSAUtil(String algorithm) {
        this(algorithm, CHARSET_UTF8);
    }

    public RSAUtil(int keySize) {
        this(CIPHER_ALGORITHM, keySize, CHARSET_UTF8, SIGNATURE_ALGORITHM);
    }

    public RSAUtil(String algorithm, Charset charset) {
        this(algorithm, KEY_SIZE, charset, SIGNATURE_ALGORITHM);
    }

    public RSAUtil(String algorithm, int keySize, Charset charset, String signature) {
        this.algorithm = algorithm;
        this.signature = signature;
        this.charset = charset;
        this.keySize = keySize;

        this.decryptBlock = this.keySize / 8;
        this.encryptBlock = decryptBlock - RESERVE_BYTES;
    }

    /**
     * 根据keyFactory生成Cipher
     * @return cipher
     */
    private Cipher getCipher() {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(this.algorithm);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            LOGGER.info("生成RSA Cipher异常", e);
        }
        return cipher;
    }

    /**
     * 公钥还原，将公钥转化为PublicKey对象
     * @param publicKeyStr 公钥字符串
     * @return PublicKey对象
     */
    public PublicKey restorePublicKey(String publicKeyStr) {
        PublicKey publicKey = null;

        byte[] keyBytes = Base64.decodeBase64(publicKeyStr.getBytes(charset));
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        try {
            publicKey = keyFactory.generatePublic(x509KeySpec);
        } catch (InvalidKeySpecException e) {
            LOGGER.info("公钥还原，将公钥转化为PublicKey对象异常", e);
        }

        return publicKey;
    }

    /**
     * 私钥还原，将私钥转化为privateKey对象
     * @param privateKeyStr 私钥字符串
     * @return PrivateKey对象
     */
    public PrivateKey restorePrivateKey(String privateKeyStr) {
        PrivateKey privateKey = null;

        byte[] keyBytes = Base64.decodeBase64(privateKeyStr.getBytes(charset));
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (InvalidKeySpecException e) {
            LOGGER.info("私钥还原，将私钥转化为privateKey对象异常", e);
        }

        return privateKey;
    }

    /**
     * 生成密钥对
     * @param keySize 密钥长度
     * @return 密钥对
     */
    public static KeyPair generateKeyPair(Integer keySize) {
        KeyPair keyPair = null;
        if (null == keySize) {
            keySize = KEY_SIZE;
        }
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGen.initialize(keySize);

            keyPair = keyPairGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.info("RSA公私钥对生成异常：", e);
        }
        return keyPair;
    }

    /**
     * RSA生成sign值
     * @param privateKeyStr 私钥字符串
     * @param data 数据
     * @return sign值
     * @throws Exception 异常
     */
    public String generateSign(String privateKeyStr, String data) throws Exception {
        return generateSign(restorePrivateKey(privateKeyStr), data);
    }

    /**
     * RSA生成sign值
     * @param privateKey 私钥
     * @param data 数据
     * @return sign值
     * @throws Exception 异常
     */
    public String generateSign(PrivateKey privateKey, String data) throws Exception {
        return generateSign(privateKey, data.getBytes());
    }

    /**
     * RSA生成sign值
     * @param privateKeyStr 私钥字符串
     * @param data 数据
     * @return sign值
     * @throws Exception 异常
     */
    public String generateSign(String privateKeyStr, byte[] data) throws Exception {
        return generateSign(restorePrivateKey(privateKeyStr), data);
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
     * @param publicKeyStr 公钥字符串
     * @param data 数据
     * @param sign sign值
     * @return 验签是否成功
     * @throws Exception 异常
     */
    public boolean verifyRSA(String publicKeyStr, String data, String sign) throws Exception {
        return verifyRSA(restorePublicKey(publicKeyStr), data.getBytes(charset), Base64.decodeBase64(sign));
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
     * @param publicKeyStr 公钥字符串
     * @param data 数据
     * @param sign sign值
     * @return 验签是否成功
     * @throws Exception 异常
     */
    public boolean verifyRSA(String publicKeyStr, byte[] data, byte[] sign) throws Exception {
        return verifyRSA(restorePublicKey(publicKeyStr), data, sign);
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

    /**
     * 读取密钥对
     *
     * @param file 密钥文件
     * @param password password
     * @return 密钥对
     */
    public static KeyPair generateKeyPair(File file, String password) {
        KeyPair keyPair = null;

        if (null == file) {
            return keyPair;
        }

        // 文件内容
        byte[] reads = null;

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            reads = new byte[inputStream.available()];
            inputStream.read(reads);
        } catch (FileNotFoundException e) {
            LOGGER.error("公钥文件不存在:", e);
        } catch (IOException e) {
            LOGGER.error("公钥文件读取失败:", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    LOGGER.error("关闭文件流失败:", e);
                }
            }
        }


        String line = null;
        // 生成公钥
        PublicKey publicKey = null;

        // 生成私钥
        PrivateKey privateKey = null;
        try {
            KeyStore ks = KeyStore.getInstance(KEY_PKCS12);
            char[] charPriKeyPass = password.toCharArray();
            ks.load(new ByteArrayInputStream(reads), charPriKeyPass);
            Enumeration<String> aliasEnum = ks.aliases();
            String keyAlias = null;
            if (aliasEnum.hasMoreElements()) {
                keyAlias = aliasEnum.nextElement();
            }
            privateKey = (PrivateKey) ks.getKey(keyAlias, charPriKeyPass);
        } catch (IOException e) {
            // 加密失败
            LOGGER.error("解析文件，读取私钥失败:", e);
        } catch (KeyStoreException e) {
            LOGGER.error("私钥存储异常:", e);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("不存在的解密算法:", e);
        } catch (CertificateException e) {
            LOGGER.error("证书异常:", e);
        } catch (UnrecoverableKeyException e) {
            LOGGER.error("不可恢复的秘钥异常", e);
        }

        //        if (null != publicKey && null != privateKey) {
        if (null != privateKey) {
            keyPair = new KeyPair(publicKey, privateKey);
        } else {
            throw new RuntimeException("加载密钥失败");
        }

        return keyPair;
    }

    /**
     * 生成私钥 base64加密
     * @param key 密钥
     * @param charset 编码格式
     * @return 私钥
     */
    public static String convertToString(Key key, Charset charset) {
        byte[] keyBytes = key.getEncoded();
        return StringUtils.newString(Base64.encodeBase64(keyBytes), charset.name());
    }

    public int getKeySize() {
        return keySize;
    }

    public Charset getCharset() {
        return charset;
    }
}
