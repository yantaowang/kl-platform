package com.kl.core.util;


import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * IP地址辅助类
 *
 */
@Slf4j
public class IpAddressHelper {

    /**
     * 构造函数
     */
    private IpAddressHelper() {
    }


    /**
     * 获取本地IP地址
     *
     * @return 本地IP地址
     */
    public static String getLocalIpAddress() {
        String ipString = null;
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address && !ip.getHostAddress().equals("127.0.0.1")) {
                        ipString = ip.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            log.warn("获取本地IP地址异常", e);
        }
        return ipString;
    }

}
