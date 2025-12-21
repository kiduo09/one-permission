package com.zhangyu.permission.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 菜单权限查询DTO
 * 
 * @author zhangyu
 */
@Data
public class MenuPermissionQueryDTO {
    
    /**
     * 应用ID
     */
    @NotNull(message = "应用ID不能为空")
    private Long appId;
    
    /**
     * 工号
     */
    @NotBlank(message = "工号不能为空")
    private String workNo;
}

