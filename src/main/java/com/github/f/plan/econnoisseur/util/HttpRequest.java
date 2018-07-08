package com.github.f.plan.econnoisseur.util;

import com.github.f.plan.econnoisseur.dto.HttpConsumer;
import com.github.f.plan.econnoisseur.exchanges.common.dto.BaseResp;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
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
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
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

    private static CloseableHttpClient getProxyClient() {
        HttpHost proxy = new HttpHost("127.0.0.1", 1087);
        return HttpClients.custom()
                .setConnectionManager(cm)
                .setKeepAliveStrategy(keepAliveStrat)
                .setProxy(proxy)
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
        return execute(request, false);
    }

    public static String execute(HttpUriRequest request, boolean proxy) {
        String response = null;
        CloseableHttpResponse httpResponse = null;
        StopWatch sw = new StopWatch();
        sw.start();
        try {
            CloseableHttpClient client = proxy ? getProxyClient() : getClient();
            httpResponse = client.execute(request);
            sw.stop();
            LOGGER.debug("请求耗时：{}秒", sw.getTotalTimeSeconds());
            HttpEntity entity = httpResponse.getEntity();

            Charset charset = DEFAULT_CHARSET;
            ContentType contentType = ContentType.get(entity);
            if (null != contentType && null != contentType.getCharset()) {
                charset = contentType.getCharset();
            }

            response = EntityUtils.toString(entity, charset);
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
//        LOGGER.info(response);
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

    public static <T extends BaseResp> boolean handler(String response, T model, HttpConsumer<String, T> action) {
        boolean result = false;
        if (StringUtils.isNotBlank(response)) {
            try {
                action.accept(response, model);
                result = true;
            } catch (Exception e) {
                LOGGER.error("返回报文：{}", response);
                LOGGER.error("获取请求失败", e);
            }
        }
        return result;
    }

    public static String request(String url, String method, Map<String, Object> map, Header... headers) {
        HttpRequestBase http;
        switch (method) {
            case "POST":
                HttpPost httpPost = new HttpPost(url);
                if (!CollectionUtils.isEmpty(map)) {
                    List<NameValuePair> params = new ArrayList<>();
                    map.forEach((key, value) -> params.add(new BasicNameValuePair(key, String.valueOf(value))));
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HttpRequest.DEFAULT_CHARSET));
                }
                http = httpPost;
                break;
            default: // "GET"
                HttpGet httpGet = new HttpGet(HttpRequest.generateURL(url, map));
                http = httpGet;
                break;
        }

        http.setConfig(HttpRequest.CONFIG);
        for (Header header : headers) {
            http.addHeader(header);
        }
        return HttpRequest.execute(http);
    }

    public static void main(String[] args) {
        HttpGet get = new HttpGet("https://www.google.com/");
        System.out.println(execute(get, true));
    }
}
