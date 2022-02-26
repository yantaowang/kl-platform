package com.kl.db.starter.snowflake.data.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 雪花工作者表
 * </p>
 *
 * @author erwankeji
 * @since 2021-09-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TSnowflakeWorkerAuto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 雪花标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 主机地址
     */
    private String hostAddress;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 修改时间
     */
    private LocalDateTime modifiedTime;


}
