package com.github.f.plan.econnoisseur.service;

import com.github.f.plan.econnoisseur.util.HttpRequest;
import com.github.f.plan.econnoisseur.util.JacksonUtil;
import com.github.f.plan.econnoisseur.util.HttpRequest;
import com.github.f.plan.econnoisseur.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * DingTalkService
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月27日 22:55:00
 */
public class DingTalkService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DingTalkService.class);
    private String dingTalkPath;

    public DingTalkService(String dingTalkPath) {
        this.dingTalkPath = dingTalkPath;
    }

    public void send(DingTalkMsg dingTalkMsg) {
        String response = HttpRequest.post(dingTalkPath, JacksonUtil.toJson(dingTalkMsg));
        if (response != null) {
            // 请求成功
            LOGGER.info("发送钉钉成功");
        } else {
            LOGGER.info("发送钉钉失败");
        }
    }

    public static class DingTalkMsg {
        private String msgtype = "markdown";
        private Map<String, String> markdown = new HashMap<>();

        public DingTalkMsg(String title, String text) {
            this.markdown.put("title", title);
            this.markdown.put("text", text);
        }

        public String getMsgtype() {
            return msgtype;
        }

        public DingTalkMsg setMsgtype(String msgtype) {
            this.msgtype = msgtype;
            return this;
        }

        public Map<String, String> getMarkdown() {
            return markdown;
        }
    }
}
