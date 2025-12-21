package com.zhangyu.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhangyu.permission.entity.LoginUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录用户Mapper接口
 * 
 * @author zhangyu
 */
@Mapper
public interface LoginUserMapper extends BaseMapper<LoginUser> {
}

