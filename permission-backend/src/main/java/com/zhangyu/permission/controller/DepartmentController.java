package com.zhangyu.permission.controller;

import com.zhangyu.permission.common.Result;
import com.zhangyu.permission.service.DepartmentService;
import com.zhangyu.permission.vo.DepartmentTreeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 部门管理控制器
 * 
 * @author zhangyu
 */
@Slf4j
@RestController
@RequestMapping("/departments")
public class DepartmentController {
    
    @Autowired
    private DepartmentService departmentService;
    
    /**
     * 获取部门树形结构
     * 
     * @return 部门树列表
     */
    @GetMapping("/tree")
    public Result<List<DepartmentTreeVO>> getDepartmentTree() {
        log.info("获取部门树形结构");
        List<DepartmentTreeVO> tree = departmentService.getDepartmentTree();
        return Result.success(tree);
    }
}

