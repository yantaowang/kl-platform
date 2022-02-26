package com.kl.db.starter.snowflake;


import com.kl.core.util.DateHelper;
import com.kl.core.util.IpAddressHelper;
import com.kl.db.starter.snowflake.data.entity.TSnowflakeWorkerAuto;
import com.kl.db.starter.snowflake.data.mapper.TSnowflakeWorkerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author : yuezhenyu
 * @className : DSnowflakeWorkerAutoService
 * @since : 2021/09/12 下午10:34
 */
@Slf4j
public class DSnowflakeWorkerAutoService {
    /**
     * 雪花工作者DAO
     */
    private final TSnowflakeWorkerMapper dSnowflakeWorkerAutoDAO;

    public DSnowflakeWorkerAutoService(TSnowflakeWorkerMapper dSnowflakeWorkerAutoDAO) {
        this.dSnowflakeWorkerAutoDAO = dSnowflakeWorkerAutoDAO;
    }

    /**
     * 模块名称
     */
    @Value("${spring.application.name:}")
    private String moduleName;

    /**
     * 获取workId
     *
     * @param maxWorkId 雪花算法支持的最大workId
     * @return workId
     */
    @Transactional(timeout = 5, rollbackFor = Exception.class)
    public long getWorkerId(long maxWorkId) {
        Date now = DateHelper.getYesterday(null);
        // 获取本机地址
        String hostAddress = IpAddressHelper.getLocalIpAddress();
        // 查询一天之前的workId
        TSnowflakeWorkerAuto snowflakeWorkerAuto = dSnowflakeWorkerAutoDAO.getOlderData(now);
        log.info("[getWorkerIdAuto][getOlderData]:{}", snowflakeWorkerAuto);
        if (snowflakeWorkerAuto != null) {
            // 存在符合条件的workId
            snowflakeWorkerAuto.setModuleName(moduleName);
            snowflakeWorkerAuto.setHostAddress(hostAddress);
            snowflakeWorkerAuto.setModifiedTime(LocalDateTime.now());
            dSnowflakeWorkerAutoDAO.update(snowflakeWorkerAuto);
            return snowflakeWorkerAuto.getId();
        }
        Long nowMaxWorkId = dSnowflakeWorkerAutoDAO.getMaxWorkerId();
        if (nowMaxWorkId != null && nowMaxWorkId >= maxWorkId) {
            // 已经超过最大workId，取最早的一条数据
            snowflakeWorkerAuto = dSnowflakeWorkerAutoDAO.getOlderWorkerId();
            snowflakeWorkerAuto.setModuleName(moduleName);
            snowflakeWorkerAuto.setHostAddress(hostAddress);
            if (System.currentTimeMillis() - snowflakeWorkerAuto.getModifiedTime().toInstant(ZoneOffset.of("+8")).toEpochMilli() < TimeUnit.MINUTES.toMillis(10)) {
                // 该数据修改时间距离当前时间不足10分钟
                log.error("[getWorkerIdAuto]最大workId已超过最大值且该数据修改时间距离当前时间不足10分钟 当前WorkId:{},最大workId:{},取最终workIdData:{}", nowMaxWorkId, maxWorkId, snowflakeWorkerAuto);
            } else {
                log.warn("[getWorkerIdAuto]最大workId已超过最大值 当前WorkId:{},最大workId:{},取最终workIdData:{}", nowMaxWorkId, maxWorkId, snowflakeWorkerAuto);
            }
            dSnowflakeWorkerAutoDAO.update(snowflakeWorkerAuto);
            return snowflakeWorkerAuto.getId();
        }
        // 不存在，或者最大workId没有超过限制，则创建一条新的workId数据
        snowflakeWorkerAuto = new TSnowflakeWorkerAuto();
        snowflakeWorkerAuto.setModuleName(moduleName);
        snowflakeWorkerAuto.setHostAddress(hostAddress);
        dSnowflakeWorkerAutoDAO.create(snowflakeWorkerAuto);
        log.info("[getWorkerIdAuto]新增雪花表数据:{}", snowflakeWorkerAuto);
        return snowflakeWorkerAuto.getId();

    }

    /**
     * 更新修改时间
     *
     * @param id
     */
    public boolean updateModifiedTime(Long id) {
        dSnowflakeWorkerAutoDAO.updateModifiedTime(id);
        return true;
    }

    /**
     * 定时更新修改时间
     *
     * @param workId
     */
    public void timerUpdateModifiedTime(Long workId) {
        String hostAddress = IpAddressHelper.getLocalIpAddress();
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    log.info("[getWorkerIdAuto] 开始更新时间workId:{}", workId);
                    dSnowflakeWorkerAutoDAO.updateModifiedTime(workId);
                    log.info("[getWorkerIdAuto] 结束更新时间workId:{}", workId);
                } catch (Exception e) {
                    log.error("[getWorkerIdAuto] 更新时间失败workId:{}", workId, e);
                }
            }
        };
        log.info("[getWorkerIdAuto] 创建定时任务workId:{},moduleName:{}, hostAddress:{}", workId, moduleName, hostAddress);
        timer.schedule(timerTask, TimeUnit.MINUTES.toMillis(5), TimeUnit.MINUTES.toMillis(5));
    }
}