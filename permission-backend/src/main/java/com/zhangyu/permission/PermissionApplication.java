package com.zhangyu.permission;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * one-permission 权限管理系统启动类
 * 
 * @author zhangyu
 */
@SpringBootApplication
@MapperScan("com.zhangyu.permission.mapper")
public class PermissionApplication {

    public static void main(String[] args) {
        SpringApplication.run(PermissionApplication.class, args);
    }
}

