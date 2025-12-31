package com.zhangyu.permission.service;

import com.zhangyu.permission.dto.ChangePasswordDTO;
import com.zhangyu.permission.dto.LoginDTO;
import com.zhangyu.permission.vo.LoginResultVO;
import com.zhangyu.permission.vo.LoginUserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 认证服务接口
 * 
 * @author zhangyu
 */
public interface AuthService {
    
    /**
     * 用户登录
     * 
     * @param loginDTO 登录信息
     * @param request HTTP请求
     * @return 登录结果（包含token和用户信息）
     */
    LoginResultVO login(LoginDTO loginDTO, HttpServletRequest request);
    
    /**
     * 记录登录失败日志
     * 
     * @param loginAccount 登录账户
     * @param message 错误消息
     * @param request HTTP请求
     */
    void recordLoginFail(String loginAccount, String message, HttpServletRequest request);
    
    /**
     * 获取当前登录用户信息
     * 
     * @return 用户信息
     */
    LoginUserVO getCurrentUser();
    
    /**
     * 退出登录
     */
    void logout();
    
    /**
     * 修改密码
     * 
     * @param changePasswordDTO 修改密码信息
     */
    void changePassword(ChangePasswordDTO changePasswordDTO);
}

