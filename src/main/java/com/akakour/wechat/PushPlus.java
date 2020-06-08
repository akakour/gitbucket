package com.akakour.wechat;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;

public class PushPlus {
    public static final String PUSH_PLUS_URL = "http://pushplus.hxtrip.com/send";
    public String PUSH_PLUS_TOKEN;
    public static CloseableHttpClient httpclient;

    static {
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(5000)
            .setConnectionRequestTimeout(1000)
            .build();
        httpclient = HttpClientBuilder.create()
            .setDefaultRequestConfig(requestConfig)
            .build();
        //TODO
//        PUSH_PLUS_TOKEN = "67d549dc63a54ae680b6a2a5feffad4a";
    }

    public String sendMessage(String title, String content, String topic, String type) {
        PushPlusReqData pushPlusReqData;
        BufferedReader in;
        CloseableHttpResponse response;
        PushPlusRepData parse ;

        if (!StringUtils.isBlank(type)) {
            pushPlusReqData = new PushPlusReqData(PUSH_PLUS_TOKEN, title, content,
                PushPlusTemplete.valueOf(type).name(), topic);
        } else {
            pushPlusReqData = new PushPlusReqData(PUSH_PLUS_TOKEN, title, content,
                PushPlusTemplete.JSON.name(), topic);
        }
        HttpPost postMethod = new HttpPost(PUSH_PLUS_URL);
        postMethod.setEntity(new StringEntity(JSON.toJSONString(pushPlusReqData), ContentType.APPLICATION_JSON));
        try {
            response = httpclient.execute(postMethod);

            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            while ((line = in.readLine()) != null) {
                sb.append(line + System.getProperty("line.separator"));
            }
            ObjectMapper mapper = new ObjectMapper();
            parse = mapper.readValue(sb.toString(), PushPlusRepData.class);
            if ("200".equals(parse.getCode())) {
                return "Send Success";
            }
            in.close();
        } catch (Exception e) {
            return "Send Fail";
        }
        return "Send Fail";
    }

    public void setToken(String token) {
        this.PUSH_PLUS_TOKEN = token;
    }

    @Data
    @AllArgsConstructor
    static
    class PushPlusReqData implements Serializable {
        private String token;
        private String title;
        private String content;
        private String template;
        private String topic;
    }

    @Data
    static
    class PushPlusRepData implements Serializable {
        private String code;
        private String msg;
        private String data;
        private String count;
    }

}
