package com.kl.core.util;


import lombok.extern.slf4j.Slf4j;

/**
 * httputil 代理类
 */
@Slf4j
public class HttpUtil extends cn.hutool.http.HttpUtil {

    public static int timeout = 3000;

    /**
     * 发送post请求<br>
     * 请求体body参数支持两种类型：
     *
     * <pre>
     * 1. 标准参数，例如 a=1&amp;b=2 这种格式
     * 2. Rest模式，此时body需要传入一个JSON或者XML字符串，Hutool会自动绑定其对应的Content-Type
     * </pre>
     *
     * @param urlString 网址
     * @param body      post表单数据
     * @param timeout   超时时长，-1表示默认超时，单位毫秒
     * @return 返回数据
     * @since 3.2.0
     */
    public static String post(String urlString, String body, int timeout) {
        log.info("[CommonHttpUtil]request url:" + urlString + ", body:" + body);
        String result = cn.hutool.http.HttpUtil.post(urlString, body, timeout);
        log.info("request result:" + result);
        return result;
    }

    /**
     * 发送get请求
     *
     * @param urlString 网址
     * @param timeout   超时时长，-1表示默认超时，单位毫秒
     * @return 返回内容，如果只检查状态码，正常只返回 ""，不正常返回 null
     * @since 3.2.0
     */
    public static String get(String urlString, int timeout) {
        log.info("[CommonHttpUtil]request url:" + urlString);
        String result = cn.hutool.http.HttpUtil.get(urlString, timeout);
        log.info("request result:" + result);
        return result;
    }

    /**
     * 发送post请求<br>
     * 请求体body参数支持两种类型：
     *
     * <pre>
     * 1. 标准参数，例如 a=1&amp;b=2 这种格式
     * 2. Rest模式，此时body需要传入一个JSON或者XML字符串，Hutool会自动绑定其对应的Content-Type
     * </pre>
     * @param urlString 网址
     * @param body      post表单数据
     * @return 返回数据
     * @since 3.2.0
     */
    public static String post(String urlString, String body) {
        return post(urlString, body, timeout);
    }
}
