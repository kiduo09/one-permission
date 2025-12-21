package com.zhangyu.permission.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhangyu.permission.common.Result;
import com.zhangyu.permission.entity.Application;
import com.zhangyu.permission.entity.LoginUser;
import com.zhangyu.permission.mapper.ApplicationMapper;
import com.zhangyu.permission.mapper.LoginUserMapper;
import com.zhangyu.permission.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统计信息控制器
 *
 * @author zhangyu
 */
@Slf4j
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private LoginUserMapper loginUserMapper;

    @Autowired
    private ApplicationService applicationService;

    /**
     * 获取统计数据
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStatistics() {
        log.info("获取Dashboard统计数据");

        Map<String, Object> statistics = new HashMap<>();

        try {
            // 获取当前登录用户ID
            Long loginUserId = null;
            try {
                loginUserId = StpUtil.getLoginIdAsLong();
            } catch (Exception e) {
                log.warn("获取当前登录用户ID失败，可能未登录", e);
            }

            // 应用数量统计
            long appCount = 0;
            boolean isSystemAdmin = false;
            if (loginUserId != null) {
                // 查询用户信息，判断是否为系统管理员
                LoginUser currentUser = loginUserMapper.selectById(loginUserId);
                if (currentUser != null) {
                    // 判断是否为系统管理员：adminType=2
                    isSystemAdmin = currentUser.getAdminType() != null && currentUser.getAdminType() == 2;
                }
            }
            
            if (isSystemAdmin) {
                // 系统管理员，不进行权限过滤，但需要过滤掉禁用的应用
                LambdaQueryWrapper<Application> appWrapper = new LambdaQueryWrapper<>();
                appWrapper.eq(Application::getStatus, "正常");
                appCount = applicationMapper.selectCount(appWrapper);
            } else {
                // 普通管理员，根据用户权限过滤可见应用，并过滤掉禁用的应用
                List<Application> authorizedApps = applicationService.getAuthorizedApplications(loginUserId);
                appCount = authorizedApps.stream()
                        .filter(app -> "正常".equals(app.getStatus()))
                        .count();
            }
            statistics.put("appCount", appCount);

            // 登录用户数量统计（过滤掉禁用的用户）
            LambdaQueryWrapper<LoginUser> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.eq(LoginUser::getStatus, "正常");
            long loginUserCount = loginUserMapper.selectCount(userWrapper);
            statistics.put("loginUserCount", loginUserCount);

            // 在线用户数统计（使用Sa-Token获取实际在线用户数）
            long onlineUserCount = 0;
            try {
                // 获取所有状态为"正常"的登录用户
                List<LoginUser> normalUsers = loginUserMapper.selectList(userWrapper);
                
                // 遍历用户，检查是否有有效的token（即是否在线）
                for (LoginUser user : normalUsers) {
                    try {
                        // 检查该用户是否在线（是否有有效的token）
                        // StpUtil.isLogin() 会检查指定用户是否登录
                        if (StpUtil.isLogin(user.getId())) {
                            onlineUserCount++;
                        }
                    } catch (Exception e) {
                        // 如果检查失败，说明用户不在线，跳过
                        // 这里不记录日志，因为这是正常的（用户可能未登录）
                    }
                }
            } catch (Exception e) {
                log.warn("获取在线用户数失败，使用登录用户数作为替代", e);
                // 如果获取在线用户数失败，使用登录用户数作为替代
                onlineUserCount = loginUserCount;
            }
            statistics.put("onlineUserCount", onlineUserCount);

            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取统计数据失败", e);
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }
}

