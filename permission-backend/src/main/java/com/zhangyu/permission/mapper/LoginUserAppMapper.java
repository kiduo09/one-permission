package com.zhangyu.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhangyu.permission.entity.LoginUserApp;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录用户应用授权Mapper接口
 * 
 * @author zhangyu
 */
@Mapper
public interface LoginUserAppMapper extends BaseMapper<LoginUserApp> {
}

