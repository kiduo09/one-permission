package com.zhangyu.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhangyu.permission.entity.AppRoleUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应用角色用户关联Mapper
 * 
 * @author zhangyu
 */
@Mapper
public interface AppRoleUserMapper extends BaseMapper<AppRoleUser> {
}

