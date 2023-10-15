package com.kl.web.starter.filter.wrapper;


import com.kl.common.util.JsonUtils;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 解析request里面的参数
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;
    private final String bodyStr;
    private final String contentType;
    private static final Logger logger = LoggerFactory.getLogger(RequestWrapper.class);
    private Map<String, String[]> params = new HashMap<>();

    /**
     * 对request进行包装
     */
    public RequestWrapper(HttpServletRequest request, String contentType) {
        super(request);
        this.contentType = contentType;
        if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(this.contentType)) {
            bodyStr = specialUrlParams();
        } else if (MediaType.APPLICATION_JSON_VALUE.equals(this.contentType)) {
            specialUrlParams();
            bodyStr = getBodyStringUTF8(request);
        } else {
            specialUrlParams();
            bodyStr = getBodyString(request);
        }
        body = bodyStr.getBytes(Charset.forName("UTF-8"));
    }

    /**
     * 获取可以多次进行读取的流
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    /**
     * 封装原来request中的流
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {

        final ByteArrayInputStream bais = new ByteArrayInputStream(body);

        return new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                //this is a empty method
            }

            @Override
            public boolean isFinished() {
                return true;
            }
        };
    }

    /**
     * 重写获取参数方法，可以在保证参数转换的同时获取流信息
     */
    @Override
    public String getParameter(String name) {
        if (params.get(name) != null && params.get(name).length >= 1) {
            return params.get(name)[0];
        }
        return super.getParameter(name);
    }

    /**
     * 重写获取参数列表方法，可以在保证参数转换的同时获取流信息
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        return params;
    }

    /**
     * 重写获取参数名称方法，可以在保证参数转换的同时获取流信息
     */
    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(params.keySet());
    }

    /**
     * 重写获取参数方法，可以在保证参数转换的同时获取流信息
     */
    @Override
    public String[] getParameterValues(final String name) {
        return params.get(name);
    }

    /**
     * 初始化参数列表，可以在保证参数转换的同时获取流信息
     */
    public String specialUrlParams() {
        params = super.getParameterMap();
        return JsonUtils.objectToJson(params);
    }

    /**
     * 不是特殊request的情况下，以utf-8编码格式通过读取流的形式获取流信息
     */
    public static String getBodyStringUTF8(ServletRequest request) {
        StringBuilder sb = new StringBuilder();
        String line = "";
        try (
                InputStream inputStream = request.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, Charset.forName("UTF-8")));
        ) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (ClientAbortException e) {
            // 客户端终止请求导致 https://blog.csdn.net/z69183787/article/details/85109959
            logger.warn("get body string error:", e);
        } catch (IOException e) {
            logger.error("get body string error:", e);
        }
        return sb.toString();
    }

    /**
     * 不是特殊request的情况下，通过读取流的形式获取流信息
     */
    public static String getBodyString(ServletRequest request) {
        StringBuilder sb = new StringBuilder();
        String line = "";
        try (
                InputStream inputStream = request.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
        ) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (ClientAbortException e) {
            // 客户端终止请求导致 https://blog.csdn.net/z69183787/article/details/85109959
            logger.warn("get body string error:", e);
        } catch (IOException e) {
            logger.error("get body string error:", e);
        }
        return sb.toString();
    }

    /**
     * 读取body字符
     */
    public String getBodyStr(String url, String contentType) {

        return bodyStr;
    }
}
