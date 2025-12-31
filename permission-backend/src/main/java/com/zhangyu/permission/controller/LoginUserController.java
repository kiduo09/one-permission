package com.zhangyu.permission.controller;

import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.common.Result;
import com.zhangyu.permission.dto.LoginUserCreateDTO;
import com.zhangyu.permission.dto.LoginUserQueryDTO;
import com.zhangyu.permission.dto.LoginUserUpdateDTO;
import com.zhangyu.permission.entity.LoginUser;
import com.zhangyu.permission.service.LoginUserService;
import com.zhangyu.permission.vo.LoginUserListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 登录用户管理控制器
 * 
 * @author zhangyu
 */
@Slf4j
@RestController
@RequestMapping("/login-users")
public class LoginUserController {
    
    @Autowired
    private LoginUserService loginUserService;
    
    /**
     * 分页查询登录用户列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping
    public Result<PageResult<LoginUserListVO>> pageQuery(@Validated LoginUserQueryDTO queryDTO) {
        log.info("分页查询登录用户列表，参数：{}", queryDTO);
        PageResult<LoginUserListVO> result = loginUserService.pageQuery(queryDTO);
        return Result.success(result);
    }
    
    /**
     * 根据ID查询登录用户详情
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public Result<LoginUser> getById(@PathVariable Long id) {
        log.info("查询登录用户详情，ID：{}", id);
        LoginUser user = loginUserService.getById(id);
        if (user == null) {
            return Result.notFound("用户不存在");
        }
        // 不返回密码
        user.setPassword(null);
        return Result.success(user);
    }
    
    /**
     * 新增登录用户
     * 
     * @param createDTO 创建信息
     * @return 用户信息
     */
    @PostMapping
    public Result<LoginUser> create(@Validated @RequestBody LoginUserCreateDTO createDTO) {
        log.info("新增登录用户，参数：{}", createDTO);
        LoginUser user = loginUserService.create(createDTO);
        // 不返回密码
        user.setPassword(null);
        return Result.success("创建成功", user);
    }
    
    /**
     * 更新登录用户
     * 
     * @param id 用户ID
     * @param updateDTO 更新信息
     * @return 用户信息
     */
    @PutMapping("/{id}")
    public Result<LoginUser> update(@PathVariable Long id, 
                                    @Validated @RequestBody LoginUserUpdateDTO updateDTO) {
        log.info("更新登录用户，ID：{}，参数：{}", id, updateDTO);
        LoginUser user = loginUserService.update(id, updateDTO);
        // 不返回密码
        user.setPassword(null);
        return Result.success("更新成功", user);
    }
    
    /**
     * 删除登录用户
     * 
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        log.info("删除登录用户，ID：{}", id);
        loginUserService.delete(id);
        return Result.success("删除成功");
    }
    
    /**
     * 批量删除登录用户
     * 
     * @param ids 用户ID列表
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    public Result<?> batchDelete(@RequestBody List<Long> ids) {
        log.info("批量删除登录用户，IDs：{}", ids);
        loginUserService.batchDelete(ids);
        return Result.success("批量删除成功");
    }
    
    /**
     * 重置密码
     * 
     * @param id 用户ID
     * @return 操作结果
     */
    @PostMapping("/{id}/reset-password")
    public Result<?> resetPassword(@PathVariable Long id) {
        log.info("重置用户密码，ID：{}", id);
        loginUserService.resetPassword(id);
        return Result.success("密码重置成功，新密码为：onepermission");
    }
}

