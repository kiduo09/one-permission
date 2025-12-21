package com.zhangyu.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhangyu.permission.entity.Department;
import org.apache.ibatis.annotations.Mapper;

/**
 * 部门Mapper接口
 * 
 * @author zhangyu
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
}

