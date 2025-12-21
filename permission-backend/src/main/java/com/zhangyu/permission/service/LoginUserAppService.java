package com.zhangyu.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangyu.permission.dto.AssignAppsDTO;
import com.zhangyu.permission.entity.Application;
import com.zhangyu.permission.entity.LoginUserApp;

import java.util.List;

/**
 * 登录用户应用授权服务接口
 * 
 * @author zhangyu
 */
public interface LoginUserAppService extends IService<LoginUserApp> {
    
    /**
     * 查询登录用户可访问的应用列表
     * 
     * @param loginUserId 登录用户ID
     * @return 应用列表
     */
    List<Application> getApplicationsByLoginUserId(Long loginUserId);
    
    /**
     * 为登录用户分配应用
     * 
     * @param loginUserId 登录用户ID
     * @param assignDTO 分配信息
     */
    void assignApps(Long loginUserId, AssignAppsDTO assignDTO);
    
    /**
     * 取消登录用户的某个应用权限
     * 
     * @param loginUserId 登录用户ID
     * @param appId 应用ID
     */
    void revokeApp(Long loginUserId, Long appId);
    
    /**
     * 查询应用被授权给了哪些登录用户
     * 
     * @param appId 应用ID
     * @return 登录用户ID列表
     */
    List<Long> getLoginUserIdsByAppId(Long appId);
}

