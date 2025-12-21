package com.zhangyu.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyu.permission.common.exception.BusinessException;
import com.zhangyu.permission.dto.AssignAppsDTO;
import com.zhangyu.permission.entity.Application;
import com.zhangyu.permission.entity.LoginUser;
import com.zhangyu.permission.entity.LoginUserApp;
import com.zhangyu.permission.mapper.LoginUserAppMapper;
import com.zhangyu.permission.service.ApplicationService;
import com.zhangyu.permission.service.LoginUserAppService;
import com.zhangyu.permission.service.LoginUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录用户应用授权服务实现类
 * 
 * @author zhangyu
 */
@Service
public class LoginUserAppServiceImpl extends ServiceImpl<LoginUserAppMapper, LoginUserApp> implements LoginUserAppService {
    
    @Autowired
    private LoginUserService loginUserService;
    
    @Autowired
    private ApplicationService applicationService;
    
    @Override
    public List<Application> getApplicationsByLoginUserId(Long loginUserId) {
        // 查询用户授权的应用ID列表
        LambdaQueryWrapper<LoginUserApp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoginUserApp::getLoginUserId, loginUserId);
        List<LoginUserApp> userApps = this.list(wrapper);
        
        if (userApps.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        // 获取应用ID列表
        List<Long> appIds = userApps.stream()
                .map(LoginUserApp::getAppId)
                .collect(Collectors.toList());
        
        // 查询应用信息
        return applicationService.listByIds(appIds);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignApps(Long loginUserId, AssignAppsDTO assignDTO) {
        // 验证登录用户是否存在
        LoginUser loginUser = loginUserService.getById(loginUserId);
        if (loginUser == null) {
            throw new BusinessException("登录用户不存在");
        }
        
        // 删除用户原有的应用授权
        LambdaQueryWrapper<LoginUserApp> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(LoginUserApp::getLoginUserId, loginUserId);
        this.remove(deleteWrapper);
        
        // 如果应用列表为空，只删除原有授权，不插入新授权
        if (assignDTO.getAppIds() == null || assignDTO.getAppIds().isEmpty()) {
            return;
        }
        
        // 验证应用是否存在
        List<Application> applications = applicationService.listByIds(assignDTO.getAppIds());
        if (applications.size() != assignDTO.getAppIds().size()) {
            throw new BusinessException("部分应用不存在");
        }
        
        // 批量插入新的应用授权
        List<LoginUserApp> userApps = assignDTO.getAppIds().stream()
                .map(appId -> {
                    LoginUserApp userApp = new LoginUserApp();
                    userApp.setLoginUserId(loginUserId);
                    userApp.setAppId(appId);
                    return userApp;
                })
                .collect(Collectors.toList());
        
        this.saveBatch(userApps);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeApp(Long loginUserId, Long appId) {
        LambdaQueryWrapper<LoginUserApp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoginUserApp::getLoginUserId, loginUserId)
                .eq(LoginUserApp::getAppId, appId);
        this.remove(wrapper);
    }
    
    @Override
    public List<Long> getLoginUserIdsByAppId(Long appId) {
        LambdaQueryWrapper<LoginUserApp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoginUserApp::getAppId, appId);
        List<LoginUserApp> userApps = this.list(wrapper);
        
        return userApps.stream()
                .map(LoginUserApp::getLoginUserId)
                .collect(Collectors.toList());
    }
}

