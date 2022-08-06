package com.kl.example.service.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;

import java.util.HashMap;
import java.util.Map;

public class HttpsUtil {

    public static String doPost(String url, String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        HttpRequest httpRequest = HttpUtil.createPost(url);
        httpRequest.addHeaders(headers);
        httpRequest.body(body);
        HttpResponse httpResponse = httpRequest.execute();
        return httpResponse.body();
    }

    public static String doGet(String url) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        HttpRequest httpRequest = HttpUtil.createGet(url);
        httpRequest.addHeaders(headers);
        HttpResponse httpResponse = httpRequest.execute();
        return httpResponse.body();
    }
}
