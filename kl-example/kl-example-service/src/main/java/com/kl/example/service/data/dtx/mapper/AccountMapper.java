package com.kl.example.service.data.dtx.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kl.example.service.data.dtx.entity.AccountEntity;
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
public interface AccountMapper extends BaseMapper<AccountEntity> {
    /**
     * 账户扣款（AT）
     *
     * @param userId 用户ID
     * @param money  用户扣款金额
     */
    int reduceMoney(@Param("userId") String userId, @Param("money") int money);

    /**
     * 账户锁定金额（TCC）
     *
     * @param userId 用户ID
     * @param money  用户锁定金额
     */
    int lockMoney(@Param("userId") String userId, @Param("money") int money);

    /**
     * 账户从锁定金额扣减金额（TCC）
     *
     * @param userId 用户ID
     * @param money  用户锁定金额
     */
    int reduceMoneyFromLock(@Param("userId") String userId, @Param("money") int money);

    /**
     * 账户锁定金额释放（TCC）
     *
     * @param userId 用户ID
     * @param money  用户锁定金额
     */
    int releaseLock(@Param("userId") String userId, @Param("money") int money);
}
