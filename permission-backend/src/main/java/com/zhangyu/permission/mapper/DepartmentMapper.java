package com.zhangyu.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhangyu.permission.entity.Department;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 部门Mapper接口
 * 
 * @author zhangyu
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
    
    /**
     * 手动指定ID插入部门（用于外部接口推送时指定ID）
     * 
     * @param department 部门实体
     */
    @Insert("INSERT INTO departments " +
            "(id, name, parent_id, ancestors, code, level, sort, leader_work_no, status, remark, create_time, update_time) " +
            "VALUES " +
            "(#{id}, #{name}, #{parentId}, #{ancestors}, #{code}, #{level}, #{sort}, #{leaderWorkNo}, #{status}, #{remark}, " +
            "#{createTime}, #{updateTime})")
    void insertWithId(Department department);
}

