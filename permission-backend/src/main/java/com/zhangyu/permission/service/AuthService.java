package com.zhangyu.permission.service;

import com.zhangyu.permission.dto.LoginDTO;
import com.zhangyu.permission.vo.LoginResultVO;
import com.zhangyu.permission.vo.LoginUserVO;

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
     * @return 登录结果（包含token和用户信息）
     */
    LoginResultVO login(LoginDTO loginDTO);
    
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
}

