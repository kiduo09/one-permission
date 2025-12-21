package com.zhangyu.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhangyu.permission.entity.NormalUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 普通用户Mapper接口
 * 
 * @author zhangyu
 */
@Mapper
public interface NormalUserMapper extends BaseMapper<NormalUser> {
}

