package com.kl.db.starter.snowflake.data.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kl.db.starter.snowflake.data.entity.TSnowflakeWorkerAuto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * <p>
 * 雪花工作者表 Mapper 接口
 * </p>
 *
 * @author erwankeji
 * @since 2021-09-09
 */
public interface TSnowflakeWorkerMapper extends BaseMapper<TSnowflakeWorkerAuto> {
    /**
     * 创建雪花工作者
     *
     * @param create 雪花工作者创建
     * @return 创建行数
     */
    Integer create(@Param("create") TSnowflakeWorkerAuto create);

    /**
     * 获取最大工作者标识
     *
     * @return 工作者标识
     */
    void update(@Param("update") TSnowflakeWorkerAuto create);


    /**
     * 更新修改时间
     *
     * @param id
     */
    void updateModifiedTime(Long id);

    /**
     * 获取最大工作者标识
     *
     * @return 工作者标识
     */
    Long getMaxWorkerId();

    /**
     * 获取早工作者标识
     *
     * @return 工作者标识
     */
    TSnowflakeWorkerAuto getOlderWorkerId();

    /**
     * 获取最早的一条数据
     *
     * @return
     */
    TSnowflakeWorkerAuto getOlderData(@Param("modifiedTime") Date modifiedTime);
}
