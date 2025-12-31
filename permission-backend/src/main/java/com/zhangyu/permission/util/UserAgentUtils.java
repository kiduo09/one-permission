package com.zhangyu.permission.util;

import javax.servlet.http.HttpServletRequest;

/**
 * User-Agent工具类
 * 
 * @author zhangyu
 */
public class UserAgentUtils {
    
    /**
     * 获取浏览器类型
     * 
     * @param request HTTP请求
     * @return 浏览器类型
     */
    public static String getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "未知";
        }
        
        userAgent = userAgent.toLowerCase();
        
        if (userAgent.contains("edg")) {
            return "Edge";
        } else if (userAgent.contains("chrome")) {
            return "Chrome";
        } else if (userAgent.contains("firefox")) {
            return "Firefox";
        } else if (userAgent.contains("safari") && !userAgent.contains("chrome")) {
            return "Safari";
        } else if (userAgent.contains("opera") || userAgent.contains("opr")) {
            return "Opera";
        } else if (userAgent.contains("msie") || userAgent.contains("trident")) {
            return "IE";
        } else {
            return "未知";
        }
    }
    
    /**
     * 获取操作系统类型
     * 
     * @param request HTTP请求
     * @return 操作系统类型
     */
    public static String getOs(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "未知";
        }
        
        userAgent = userAgent.toLowerCase();
        
        if (userAgent.contains("windows")) {
            if (userAgent.contains("windows nt 10.0")) {
                return "Windows 10";
            } else if (userAgent.contains("windows nt 6.3")) {
                return "Windows 8.1";
            } else if (userAgent.contains("windows nt 6.2")) {
                return "Windows 8";
            } else if (userAgent.contains("windows nt 6.1")) {
                return "Windows 7";
            } else if (userAgent.contains("windows nt 6.0")) {
                return "Windows Vista";
            } else if (userAgent.contains("windows nt 5.1")) {
                return "Windows XP";
            } else {
                return "Windows";
            }
        } else if (userAgent.contains("mac")) {
            return "Mac OS";
        } else if (userAgent.contains("linux")) {
            return "Linux";
        } else if (userAgent.contains("android")) {
            return "Android";
        } else if (userAgent.contains("iphone") || userAgent.contains("ipad")) {
            return "iOS";
        } else {
            return "未知";
        }
    }
}

