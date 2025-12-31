package com.zhangyu.permission.util;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP工具类
 * 
 * @author zhangyu
 */
public class IpUtils {
    
    /**
     * 获取客户端IP地址
     * 
     * @param request HTTP请求
     * @return IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = null;
        try {
            ip = request.getHeader("x-forwarded-for");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            // 如果是多级代理，取第一个IP
            if (ip != null && ip.indexOf(",") != -1) {
                ip = ip.substring(0, ip.indexOf(",")).trim();
            }
        } catch (Exception e) {
            // 如果获取失败，返回默认值
            ip = "unknown";
        }
        return ip;
    }
    
    /**
     * 获取IP地址归属地（简化版，实际应该调用IP归属地查询服务）
     * 
     * @param ip IP地址
     * @return 归属地
     */
    public static String getIpLocation(String ip) {
        // 这里简化处理，实际应该调用IP归属地查询API
        // 例如：高德地图API、百度地图API、ip-api.com等
        if (ip == null || "unknown".equals(ip) || ip.startsWith("127.") || ip.startsWith("192.168.") || ip.startsWith("10.")) {
            return "内网IP";
        }
        return "未知";
    }
}

