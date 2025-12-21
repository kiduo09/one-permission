package com.zhangyu.permission.controller;

import com.zhangyu.permission.common.Result;
import com.zhangyu.permission.dto.LoginDTO;
import com.zhangyu.permission.service.AuthService;
import com.zhangyu.permission.vo.LoginResultVO;
import com.zhangyu.permission.vo.LoginUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 
 * @author zhangyu
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * 用户登录
     * 
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<LoginResultVO> login(@Validated @RequestBody LoginDTO loginDTO) {
        log.info("用户登录：{}", loginDTO.getLoginAccount());
        LoginResultVO result = authService.login(loginDTO);
        return Result.success("登录成功", result);
    }
    
    /**
     * 获取当前登录用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("/current")
    public Result<LoginUserVO> getCurrentUser() {
        LoginUserVO userInfo = authService.getCurrentUser();
        return Result.success(userInfo);
    }
    
    /**
     * 退出登录
     * 
     * @return 操作结果
     */
    @PostMapping("/logout")
    public Result<?> logout() {
        authService.logout();
        return Result.success("退出登录成功");
    }
}

