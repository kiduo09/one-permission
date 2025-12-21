package com.zhangyu.permission.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.common.Result;
import com.zhangyu.permission.dto.ApplicationCreateDTO;
import com.zhangyu.permission.dto.ApplicationQueryDTO;
import com.zhangyu.permission.dto.ApplicationUpdateDTO;
import com.zhangyu.permission.entity.Application;
import com.zhangyu.permission.entity.LoginUser;
import com.zhangyu.permission.mapper.LoginUserMapper;
import com.zhangyu.permission.service.ApplicationService;
import com.zhangyu.permission.vo.ApplicationListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应用管理控制器
 * 
 * @author zhangyu
 */
@Slf4j
@RestController
@RequestMapping("/applications")
public class ApplicationController {
    
    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
    private LoginUserMapper loginUserMapper;
    
    /**
     * 分页查询应用列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping
    public Result<PageResult<ApplicationListVO>> pageQuery(@Validated ApplicationQueryDTO queryDTO) {
        log.info("分页查询应用列表，参数：{}", queryDTO);
        PageResult<ApplicationListVO> result = applicationService.pageQuery(queryDTO);
        return Result.success(result);
    }
    
    /**
     * 根据ID查询应用详情
     * 
     * @param id 应用ID
     * @return 应用信息
     */
    @GetMapping("/{id}")
    public Result<Application> getById(@PathVariable Long id) {
        log.info("查询应用详情，ID：{}", id);
        Application application = applicationService.getById(id);
        if (application == null) {
            return Result.notFound("应用不存在");
        }
        return Result.success(application);
    }
    
    /**
     * 查询所有应用列表（不分页，用于下拉选择）
     * 如果当前登录用户是系统管理员，则返回所有应用；否则只返回授权的应用
     * 
     * @return 应用列表
     */
    @GetMapping("/all")
    public Result<List<Application>> getAll() {
        log.info("查询应用列表（用于下拉选择）");
        // 从Sa-Token获取当前登录用户ID
        Long loginUserId = null;
        boolean isSystemAdmin = false;
        try {
            loginUserId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
            // 判断是否为系统管理员
            if (loginUserId != null) {
                LoginUser user = loginUserMapper.selectById(loginUserId);
                if (user != null) {
                    isSystemAdmin = user.getAdminType() != null && user.getAdminType() == 2;
                }
            }
        } catch (Exception e) {
            // 如果未登录或获取失败，返回所有应用
            log.debug("未登录或获取用户ID失败，返回所有应用");
        }
        
        // 如果是系统管理员（admin_type = 2），返回所有应用，不限制查询条件（包括状态等）
        // 否则返回授权的应用
        List<Application> list;
        if (isSystemAdmin) {
            // 系统管理员：不限制任何查询条件，返回所有应用
            LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
            wrapper.orderByAsc(Application::getName);
            list = applicationService.list(wrapper);
        } else {
            // 普通管理员：返回授权的应用
            list = applicationService.getAuthorizedApplications(loginUserId);
        }
        return Result.success(list);
    }
    
    /**
     * 查询所有应用列表（不分页，用于分配应用等场景）
     * 不进行权限过滤，返回所有应用
     * 
     * @return 应用列表
     */
    @GetMapping("/all-for-assign")
    public Result<List<Application>> getAllForAssign() {
        log.info("查询所有应用列表（用于分配应用，不进行权限过滤）");
        List<Application> list = applicationService.list();
        // 按名称排序
        list.sort((a, b) -> {
            if (a.getName() == null) return 1;
            if (b.getName() == null) return -1;
            return a.getName().compareTo(b.getName());
        });
        return Result.success(list);
    }
    
    /**
     * 新增应用
     * 
     * @param createDTO 创建信息
     * @return 应用信息
     */
    @PostMapping
    public Result<Application> create(@Validated @RequestBody ApplicationCreateDTO createDTO) {
        log.info("新增应用，参数：{}", createDTO);
        Application application = applicationService.create(createDTO);
        return Result.success("创建成功", application);
    }
    
    /**
     * 更新应用
     * 
     * @param id 应用ID
     * @param updateDTO 更新信息
     * @return 应用信息
     */
    @PutMapping("/{id}")
    public Result<Application> update(@PathVariable Long id, 
                                      @Validated @RequestBody ApplicationUpdateDTO updateDTO) {
        log.info("更新应用，ID：{}，参数：{}", id, updateDTO);
        Application application = applicationService.update(id, updateDTO);
        return Result.success("更新成功", application);
    }
    
    /**
     * 删除应用
     * 
     * @param id 应用ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        log.info("删除应用，ID：{}", id);
        applicationService.delete(id);
        return Result.success("删除成功");
    }
}

