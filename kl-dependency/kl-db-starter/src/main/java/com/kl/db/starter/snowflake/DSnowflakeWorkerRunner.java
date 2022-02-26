package com.kl.db.starter.snowflake;


import cn.hutool.core.net.NetUtil;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import com.alibaba.fastjson.JSON;
import com.kl.db.starter.IdGeneratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author : yuezhenyu
 * @since : 2021/09/12 下午10:34
 */
@Slf4j
public class DSnowflakeWorkerRunner {

    @Value("${spring.application.name}")
    private String moduleName;

    @Value("${ewp.snowflake.enable:true}")
    private String snowflakeEnable;

    private final DSnowflakeWorkerAutoService snowflakeWorkerAutoService;

    /**
     * 雪花工具类初始化并发控制
     */
    private static final AtomicBoolean ATOMIC_BOOLEAN = new AtomicBoolean();

    public DSnowflakeWorkerRunner(DSnowflakeWorkerAutoService snowflakeWorkerAutoService) {
        this.snowflakeWorkerAutoService = snowflakeWorkerAutoService;
    }

    /**
     * 生成workid
     */
    private long getWorkerId() {
        long time = System.currentTimeMillis();
        log.info("[getWorkerIdAuto] 开始生成雪花算法workId");
        long workId = snowflakeWorkerAutoService.getWorkerId(SnowflakeIdGenerator.MAX_WORKERID);
        log.info("[getWorkerIdAuto] 结束雪花算法workId:{}，[生成雪花workId耗时]:{}", workId, System.currentTimeMillis() - time);
        snowflakeWorkerAutoService.timerUpdateModifiedTime(workId);
        log.info("[getWorkerIdAuto] 定时更新雪花算法workId:{}", workId);
        return workId;
    }

    /**
     * 初始化雪花workId
     */
    @PostConstruct
    public void initWorkId() {
        if ("false".equals(snowflakeEnable)) {
            log.warn("[getWorkerIdAuto]雪花工具类未启用snowflakeEnable:{},采用默认雪花工具类", snowflakeEnable);
            IdGeneratorUtil.overrideSnowflakeIdGenerator();
            return;
        }
        if (ATOMIC_BOOLEAN.compareAndSet(false, true)) {
            try {
                OsInfo osInfo = SystemUtil.getOsInfo();
                Map<String, Object> map = new HashMap<String, Object>(3) {{
                    put("osArch", osInfo.getArch());
                    put("osName", osInfo.getName());
                    put("osVersion", osInfo.getVersion());
                }};
                String os = JSON.toJSONString(map);
                log.info("[getWorkerIdAuto]当前模块名称:{},ip:{},os:{}", moduleName, NetUtil.getLocalhostStr(), os);
                IdGeneratorUtil.setSnowflakeIdGenerator(new SnowflakeIdGenerator(getWorkerId()));
                log.info("[getWorkerIdAuto]初始化雪花生成器成功.");
            } catch (Exception e) {
                log.error("[getWorkerIdAuto]初始化雪花生成器失败", e);
                ATOMIC_BOOLEAN.set(false);
            }
        } else {
            log.warn("[getWorkerIdAuto][getWorkerIdAutoInitIng]雪花工具类初始化中...");
        }

    }
}
