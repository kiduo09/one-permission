package com.zhangyu.permission.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyu.permission.common.PageResult;
import com.zhangyu.permission.common.exception.BusinessException;
import com.zhangyu.permission.dto.NormalUserQueryDTO;
import com.zhangyu.permission.entity.Department;
import com.zhangyu.permission.entity.NormalUser;
import com.zhangyu.permission.mapper.NormalUserMapper;
import com.zhangyu.permission.service.NormalUserService;
import com.zhangyu.permission.vo.NormalUserDetailVO;
import com.zhangyu.permission.vo.NormalUserListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 普通用户服务实现类
 * 
 * @author zhangyu
 */
@Service
public class NormalUserServiceImpl extends ServiceImpl<NormalUserMapper, NormalUser> implements NormalUserService {
    
    @Autowired
    private com.zhangyu.permission.service.DepartmentService departmentService;
    
    @Override
    public PageResult<NormalUserListVO> pageQuery(NormalUserQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<NormalUser> wrapper = new LambdaQueryWrapper<>();
        
        // 工号模糊查询（过滤null、空字符串和"undefined"）
        String workNo = queryDTO.getWorkNo();
        if (StringUtils.hasText(workNo) && !"undefined".equals(workNo)) {
            wrapper.like(NormalUser::getWorkNo, workNo.trim());
        }
        
        // 姓名模糊查询（过滤null、空字符串和"undefined"）
        String name = queryDTO.getName();
        if (StringUtils.hasText(name) && !"undefined".equals(name)) {
            wrapper.like(NormalUser::getName, name.trim());
        }
        
        // 部门ID精确查询
        if (queryDTO.getDepartmentId() != null) {
            wrapper.eq(NormalUser::getDepartmentId, queryDTO.getDepartmentId());
        }
        
        // 状态精确查询（过滤null、空字符串和"undefined"）
        String status = queryDTO.getStatus();
        if (StringUtils.hasText(status) && !"undefined".equals(status)) {
            wrapper.eq(NormalUser::getStatus, status.trim());
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(NormalUser::getCreateTime);
        
        // 分页查询
        Page<NormalUser> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        IPage<NormalUser> pageResult = this.page(page, wrapper);
        
        // 获取所有部门ID
        List<Long> departmentIds = pageResult.getRecords().stream()
                .map(NormalUser::getDepartmentId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询部门信息
        Map<Long, String> departmentMap;
        if (departmentIds.isEmpty()) {
            departmentMap = new java.util.HashMap<>();
        } else {
            departmentMap = departmentService.listByIds(departmentIds).stream()
                .collect(Collectors.toMap(Department::getId, Department::getName));
        }
        
        // 转换为VO列表
        List<NormalUserListVO> voList = pageResult.getRecords().stream()
                .map(user -> {
                    NormalUserListVO vo = new NormalUserListVO();
                    BeanUtil.copyProperties(user, vo);
                    // 设置部门名称
                    if (user.getDepartmentId() != null) {
                        vo.setDepartmentName(departmentMap.get(user.getDepartmentId()));
                    }
                    return vo;
                })
                .collect(Collectors.toList());
        
        return PageResult.of(voList, pageResult.getTotal(), queryDTO.getPage(), queryDTO.getPageSize());
    }
    
    @Override
    public NormalUserDetailVO getDetailById(Long id) {
        // 查询用户
        NormalUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 转换为VO
        NormalUserDetailVO vo = new NormalUserDetailVO();
        BeanUtil.copyProperties(user, vo);
        
        // 查询部门名称
        if (user.getDepartmentId() != null) {
            Department department = departmentService.getById(user.getDepartmentId());
            if (department != null) {
                vo.setDepartmentName(department.getName());
            }
        }
        
        return vo;
    }
    
    @Override
    public NormalUser getByWorkNo(String workNo) {
        LambdaQueryWrapper<NormalUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NormalUser::getWorkNo, workNo);
        return this.getOne(wrapper);
    }
}

