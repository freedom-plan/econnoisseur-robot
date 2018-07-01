package com.github.f.plan.econnoisseur.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Map;

/**
 *
 * HttpRequest
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月20日 22:09:00
 */
public class HttpRequest {
    private static Logger LOGGER = LoggerFactory.getLogger(HttpRequest.class);

    private static final int TIMEOUT = 120 * 1000;
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static RequestConfig CONFIG = RequestConfig.custom()
            .setCookieSpec(CookieSpecs.STANDARD_STRICT)
            .setSocketTimeout(TIMEOUT)
            .setConnectTimeout(TIMEOUT)
            .setConnectionRequestTimeout(TIMEOUT)
            .build();
    private static ConnectionKeepAliveStrategy keepAliveStrat = new DefaultConnectionKeepAliveStrategy() {

        public long getKeepAliveDuration(
                HttpResponse response,
                HttpContext context) {
            long keepAlive = super.getKeepAliveDuration(response, context);
            if (keepAlive == -1) {
                keepAlive = 5000;
            }
            return keepAlive;
        }

    };
    private static PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    static {
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);
    }

    private static CloseableHttpClient getClient() {
        return HttpClients.custom()
                .setConnectionManager(cm)
                .setKeepAliveStrategy(keepAliveStrat)
                .build();
    }

    /**
     * Post body json请求
     * @param url
     * @param data
     * @return
     */
    public static String post(String url, String data) {
        HttpPost post = new HttpPost(url);
        post.setConfig(CONFIG);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(data, DEFAULT_CHARSET));
        return execute(post);
    }

    /**
     * 执行http请求并关闭资源（HttpPost 或 HttpGet）返回请求结果
     * @param request 请求信息
     * @return get请求
     */
    public static String execute(HttpUriRequest request) {
        String response = null;
        CloseableHttpResponse httpResponse = null;
        StopWatch sw = new StopWatch();
        sw.start();
        try {
            httpResponse = getClient().execute(request);
            sw.stop();
            HttpEntity entity = httpResponse.getEntity();
            response = EntityUtils.toString(entity, DEFAULT_CHARSET);
            EntityUtils.consume(entity);
        } catch (IOException e) {
            sw.stop();
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return response;
    }

    /**
     * 生成请求的url
     *
     * @param url 不带参数的url字符串
     * @param params 参数
     *
     * @return URI， 请求的uri对象
     */
    public static URI generateURL(String url, Map<String, Object> params) {
        URI uri = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(url).setCharset(DEFAULT_CHARSET);
            if (null != params) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    uriBuilder.addParameter(entry.getKey(), entry.getValue().toString());
                }
            }
            uri = uriBuilder.build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return uri;
    }
}
