package com.zhangyu.permission.controller;

import com.zhangyu.permission.common.Result;
import com.zhangyu.permission.dto.AssignAppsDTO;
import com.zhangyu.permission.service.LoginUserAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应用用户授权控制器（从应用角度管理授权）
 * 
 * @author zhangyu
 */
@Slf4j
@RestController
@RequestMapping("/applications/{appId}/users")
public class ApplicationUserController {
    
    @Autowired
    private LoginUserAppService loginUserAppService;
    
    /**
     * 查询应用被授权给了哪些登录用户
     * 
     * @param appId 应用ID
     * @return 登录用户ID列表
     */
    @GetMapping
    public Result<List<Long>> getAuthorizedUsers(@PathVariable Long appId) {
        log.info("查询应用被授权给了哪些登录用户，应用ID：{}", appId);
        List<Long> userIds = loginUserAppService.getLoginUserIdsByAppId(appId);
        return Result.success(userIds);
    }
    
    /**
     * 为应用分配登录用户
     * 
     * @param appId 应用ID
     * @param userIds 登录用户ID列表
     * @return 操作结果
     */
    @PostMapping
    public Result<?> assignUsers(@PathVariable Long appId, 
                                  @Validated @RequestBody List<Long> userIds) {
        log.info("为应用分配登录用户，应用ID：{}，用户IDs：{}", appId, userIds);
        // 这里需要为每个用户分配应用
        // 实现逻辑：遍历用户列表，为每个用户添加该应用的授权
        for (Long userId : userIds) {
            // 先获取用户已有的应用授权
            List<com.zhangyu.permission.entity.Application> existingApps = 
                loginUserAppService.getApplicationsByLoginUserId(userId);
            List<Long> allAppIds = existingApps.stream()
                .map(com.zhangyu.permission.entity.Application::getId)
                .collect(java.util.stream.Collectors.toList());
            
            // 如果应用不在已有列表中，则添加
            if (!allAppIds.contains(appId)) {
                allAppIds.add(appId);
                AssignAppsDTO assignDTO = new AssignAppsDTO();
                assignDTO.setAppIds(allAppIds);
                loginUserAppService.assignApps(userId, assignDTO);
            }
        }
        return Result.success("分配成功");
    }
    
    /**
     * 取消应用的某个登录用户授权
     * 
     * @param appId 应用ID
     * @param userId 登录用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{userId}")
    public Result<?> revokeUser(@PathVariable Long appId, 
                                 @PathVariable Long userId) {
        log.info("取消应用的登录用户授权，应用ID：{}，用户ID：{}", appId, userId);
        loginUserAppService.revokeApp(userId, appId);
        return Result.success("取消授权成功");
    }
}

