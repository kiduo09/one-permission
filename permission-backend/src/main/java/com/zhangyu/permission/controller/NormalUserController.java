package com.zhangyu.permission.controller;

import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.common.Result;
import com.zhangyu.permission.dto.NormalUserQueryDTO;
import com.zhangyu.permission.service.NormalUserService;
import com.zhangyu.permission.vo.NormalUserDetailVO;
import com.zhangyu.permission.vo.NormalUserListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 普通用户管理控制器
 * 
 * @author zhangyu
 */
@Slf4j
@RestController
@RequestMapping("/normal-users")
public class NormalUserController {
    
    @Autowired
    private NormalUserService normalUserService;
    
    /**
     * 分页查询普通用户列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping
    public Result<PageResult<NormalUserListVO>> pageQuery(@Validated NormalUserQueryDTO queryDTO) {
        log.info("分页查询普通用户列表，参数：{}", queryDTO);
        PageResult<NormalUserListVO> result = normalUserService.pageQuery(queryDTO);
        return Result.success(result);
    }
    
    /**
     * 根据ID查询普通用户详情
     * 
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    public Result<NormalUserDetailVO> getById(@PathVariable Long id) {
        log.info("查询普通用户详情，ID：{}", id);
        NormalUserDetailVO detail = normalUserService.getDetailById(id);
        return Result.success(detail);
    }
    
    /**
     * 根据工号查询用户
     * 
     * @param workNo 工号
     * @return 用户信息
     */
    @GetMapping("/work-no/{workNo}")
    public Result<NormalUserDetailVO> getByWorkNo(@PathVariable String workNo) {
        log.info("根据工号查询用户，工号：{}", workNo);
        com.zhangyu.permission.entity.NormalUser user = normalUserService.getByWorkNo(workNo);
        if (user == null) {
            return Result.notFound("用户不存在");
        }
        NormalUserDetailVO detail = normalUserService.getDetailById(user.getId());
        return Result.success(detail);
    }
}

