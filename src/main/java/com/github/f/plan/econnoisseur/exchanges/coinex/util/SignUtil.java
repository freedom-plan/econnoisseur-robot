package com.github.f.plan.econnoisseur.exchanges.coinex.util;

import com.github.f.plan.econnoisseur.util.MD5Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * SignUtil
 *
 * @author Kevin Huang
 * @since version
 * 2018年07月05日 10:00:00
 */
public class SignUtil {
    /**
     * 生成签名结果
     *
     * @param sArray 要签名的数组
     * @param secretKey key
     * @return 签名结果字符串
     */
    public static String buildMysignV1(Map<String, Object> sArray, String secretKey) {
        String mysign = "";
        try {
            String prestr = createLinkString(sArray);
            prestr = prestr + "&secret_key=" + secretKey;
            mysign = MD5Util.md5Upper(prestr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mysign;
    }

    /**
     * 把数组所有元素排序，并按照&quot;参数=参数值&quot;的模式用&quot;&amp;&amp;&quot;字符拼接成字符串.
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, Object> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key).toString();
            if (i == keys.size() - 1) { // 拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }
}
