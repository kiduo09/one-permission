package com.zhangyu.permission.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 更新应用DTO
 * 
 * @author zhangyu
 */
@Data
public class ApplicationUpdateDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 应用名称
     */
    @NotBlank(message = "应用名称不能为空")
    private String name;
    
    /**
     * 应用Key（唯一标识）
     */
    @NotBlank(message = "应用Key不能为空")
    private String appKey;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 状态：正常/禁用
     */
    private String status;
}

