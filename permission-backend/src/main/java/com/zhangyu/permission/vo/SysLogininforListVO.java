package com.zhangyu.permission.vo;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志列表VO
 * 
 * @author zhangyu
 */
@Data
public class SysLogininforListVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 访问ID
     */
    private Long infoId;
    
    /**
     * 用户账号
     */
    private String userName;
    
    /**
     * 登录IP地址
     */
    private String ipaddr;
    
    /**
     * 登录地点
     */
    private String loginLocation;
    
    /**
     * 浏览器类型
     */
    private String browser;
    
    /**
     * 操作系统
     */
    private String os;
    
    /**
     * 登录状态（0成功 1失败）
     */
    private String status;
    
    /**
     * 提示消息
     */
    private String msg;
    
    /**
     * 访问时间
     */
    private LocalDateTime loginTime;
}

