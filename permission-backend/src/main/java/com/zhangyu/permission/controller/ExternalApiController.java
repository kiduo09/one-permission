package com.zhangyu.permission.controller;

import com.zhangyu.permission.common.Result;
import com.zhangyu.permission.dto.MenuPermissionQueryDTO;
import com.zhangyu.permission.dto.TokenRequestDTO;
import com.zhangyu.permission.service.ExternalApiService;
import com.zhangyu.permission.vo.MenuPermissionVO;
import com.zhangyu.permission.vo.TokenResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 对外接口控制器
 * 
 * @author zhangyu
 */
@Slf4j
@RestController
@RequestMapping("/api/external")
public class ExternalApiController {
    
    @Autowired
    private ExternalApiService externalApiService;
    
    /**
     * 获取访问令牌
     * 
     * @param tokenRequest 令牌请求
     * @return 令牌响应
     */
    @PostMapping("/token")
    public Result<TokenResponseVO> getToken(@Validated @RequestBody TokenRequestDTO tokenRequest) {
        log.info("获取访问令牌，clientId: {}", tokenRequest.getClientId());
        TokenResponseVO response = externalApiService.getToken(tokenRequest);
        return Result.success("获取token成功", response);
    }
    
    /**
     * 根据应用ID和工号查询菜单权限
     * 
     * @param queryDTO 查询条件
     * @return 菜单权限列表（树形结构）
     */
    @PostMapping("/menu-permissions")
    public Result<List<MenuPermissionVO>> getMenuPermissions(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Validated @RequestBody MenuPermissionQueryDTO queryDTO) {
        
        // 验证Token
        String token = extractToken(authorization);
        if (token == null) {
            return Result.error(401, "请携带令牌请求");
        }
        
        if (!externalApiService.validateToken(token)) {
            return Result.error(401, "Token无效或已过期");
        }
        
        log.info("查询菜单权限，应用ID: {}，工号: {}", queryDTO.getAppId(), queryDTO.getWorkNo());
        List<MenuPermissionVO> permissions = externalApiService.getMenuPermissions(token, queryDTO);
        return Result.success("查询成功", permissions);
    }
    
    /**
     * 验证Token是否有效
     * 
     * @param authorization 授权头
     * @return 验证结果
     */
    @GetMapping("/validate-token")
    public Result<Boolean> validateToken(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = extractToken(authorization);
        if (token == null) {
            return Result.error(401, "请携带令牌请求");
        }
        
        boolean valid = externalApiService.validateToken(token);
        return Result.success(valid ? "Token有效" : "Token无效", valid);
    }
    
    /**
     * 从Authorization头中提取token
     * 
     * @param authorization Authorization头
     * @return token
     */
    private String extractToken(String authorization) {
        if (authorization == null || authorization.trim().isEmpty()) {
            return null;
        }
        
        // 支持 "Bearer token" 格式
        if (authorization.startsWith("Bearer ")) {
            return authorization.substring(7).trim();
        }
        
        return authorization.trim();
    }
}

