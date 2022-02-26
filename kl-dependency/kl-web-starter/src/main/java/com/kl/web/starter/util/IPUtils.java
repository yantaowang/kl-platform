package com.kl.web.starter.util;


import cn.hutool.core.util.StrUtil;

import javax.servlet.http.HttpServletRequest;

/**
 *
 *
 * @className : IPUtils
 * @since : 2021/08/11 下午12:00
 */
public class IPUtils {
    public static final String UNKNOW_IP = "unknown";

    /**
     * 获取IP地址
     *
     * @param request 请求
     * @return request发起客户端的IP地址
     */
    public static String getIP(HttpServletRequest request) {
        if (request == null)  return "0.0.0.0";
        String xip = request.getHeader("X-Real-IP");
        String xfor = request.getHeader("X-Forwarded-For");

        if (StrUtil.isNotEmpty(xfor) && !UNKNOW_IP.equalsIgnoreCase(xfor)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = xfor.indexOf(",");
            if (index != -1) {
                return xfor.substring(0, index);
            } else {
                return xfor;
            }
        }

        xfor = xip;
        if (StrUtil.isNotEmpty(xfor) && !UNKNOW_IP.equalsIgnoreCase(xfor)) {
            return xfor;
        }

        if (StrUtil.isBlank(xfor) || UNKNOW_IP.equalsIgnoreCase(xfor)) {
            xfor = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(xfor) || UNKNOW_IP.equalsIgnoreCase(xfor)) {
            xfor = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(xfor) || UNKNOW_IP.equalsIgnoreCase(xfor)) {
            xfor = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StrUtil.isBlank(xfor) || UNKNOW_IP.equalsIgnoreCase(xfor)) {
            xfor = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StrUtil.isBlank(xfor) || UNKNOW_IP.equalsIgnoreCase(xfor)) {
            xfor = request.getRemoteAddr();
        }
        return xfor;
    }
}
