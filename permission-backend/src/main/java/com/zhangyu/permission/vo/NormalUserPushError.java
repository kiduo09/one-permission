package com.zhangyu.permission.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * 普通用户推送错误信息
 * 
 * @author zhangyu
 */
@Data
public class NormalUserPushError implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 工号
     */
    private String workNo;
    
    /**
     * 错误信息
     */
    private String errorMessage;
}

