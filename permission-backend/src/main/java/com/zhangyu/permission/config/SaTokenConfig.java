package com.zhangyu.permission.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token配置类
 * 
 * @author zhangyu
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    
    /**
     * 注册Sa-Token拦截器，定义认证规则
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
            // 指定拦截的路由与放行条件
            SaRouter.match("/**")                    // 拦截所有路由
                    .notMatch("/auth/login")         // 排除登录接口
                    .notMatch("/auth/logout")        // 排除退出接口
                    .notMatch("/api/external/**")    // 排除对外接口（使用自己的token验证）
                    .check(r -> StpUtil.checkLogin()); // 检查是否登录
        })).addPathPatterns("/**");
    }
}

