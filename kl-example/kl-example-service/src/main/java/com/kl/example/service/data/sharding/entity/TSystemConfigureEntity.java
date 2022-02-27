package com.kl.example.service.data.sharding.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 
 *
 * @author xinminghao
 * @since 2021-09-17 20:10:46
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_system_configure")
public class TSystemConfigureEntity implements Serializable {

    private static final long serialVersionUID = 655004901232399909L;

    
    private Long id;

    /**
    * 编码唯一
    */
    private String configureCode;

    
    private String configureName;

    
    private String configureType;

    
    private String configureValue;

    /**
    * 描述
    */
    private String configureDesc;

    /**
    * 创建时间
    */
    private LocalDateTime createdTime;

    
    private LocalDateTime modifiedTime;

    /**
    * 是否对外 1对外，0不对外
    */
    private Integer open;

}
