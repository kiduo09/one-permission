package com.zhangyu.permission.controller;

import com.zhangyu.permission.common.Result;
import com.zhangyu.permission.dto.AssignAppsDTO;
import com.zhangyu.permission.entity.Application;
import com.zhangyu.permission.service.LoginUserAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 登录用户应用授权控制器
 * 
 * @author zhangyu
 */
@Slf4j
@RestController
@RequestMapping("/login-users/{loginUserId}/applications")
public class LoginUserAppController {
    
    @Autowired
    private LoginUserAppService loginUserAppService;
    
    /**
     * 查询登录用户可访问的应用列表
     * 
     * @param loginUserId 登录用户ID
     * @return 应用列表
     */
    @GetMapping
    public Result<List<Application>> getApplications(@PathVariable Long loginUserId) {
        log.info("查询登录用户可访问的应用列表，用户ID：{}", loginUserId);
        List<Application> applications = loginUserAppService.getApplicationsByLoginUserId(loginUserId);
        return Result.success(applications);
    }
    
    /**
     * 为登录用户分配应用
     * 
     * @param loginUserId 登录用户ID
     * @param assignDTO 分配信息
     * @return 操作结果
     */
    @PostMapping
    public Result<?> assignApps(@PathVariable Long loginUserId, 
                                @Validated @RequestBody AssignAppsDTO assignDTO) {
        log.info("为登录用户分配应用，用户ID：{}，应用IDs：{}", loginUserId, assignDTO.getAppIds());
        loginUserAppService.assignApps(loginUserId, assignDTO);
        return Result.success("分配成功");
    }
    
    /**
     * 取消登录用户的某个应用权限
     * 
     * @param loginUserId 登录用户ID
     * @param appId 应用ID
     * @return 操作结果
     */
    @DeleteMapping("/{appId}")
    public Result<?> revokeApp(@PathVariable Long loginUserId, 
                                @PathVariable Long appId) {
        log.info("取消登录用户的应用权限，用户ID：{}，应用ID：{}", loginUserId, appId);
        loginUserAppService.revokeApp(loginUserId, appId);
        return Result.success("取消授权成功");
    }
}

