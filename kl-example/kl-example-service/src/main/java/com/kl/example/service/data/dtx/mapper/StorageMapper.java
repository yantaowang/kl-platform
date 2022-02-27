package com.kl.example.service.data.dtx.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kl.example.service.data.dtx.entity.StorageEntity;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ewp
 * @since 2021-09-29
 */
@DS("first")
public interface StorageMapper extends BaseMapper<StorageEntity> {

    /**
     * 扣减库存
     *
     * @param count         扣减数量
     * @param commodityCode 商品编号
     */
    int reduceStorage(@Param("count") int count,
                      @Param("commodityCode") String commodityCode);

    /**
     * 锁定库存
     *
     * @param count         锁定数量
     * @param commodityCode 商品编号
     */
    int lockStorage(@Param("count") int count,
                    @Param("commodityCode") String commodityCode);

    /**
     * 账户从锁定库存扣减库存（TCC）
     *
     * @param count         锁定数量
     * @param commodityCode 商品编号
     */
    int reduceStorageFromLock(@Param("count") int count,
                              @Param("commodityCode") String commodityCode);

    /**
     * 账户锁定库存释放（TCC）
     *
     * @param count         锁定数量
     * @param commodityCode 商品编号
     */
    int releaseStorage(@Param("count") int count,
                       @Param("commodityCode") String commodityCode);

}
