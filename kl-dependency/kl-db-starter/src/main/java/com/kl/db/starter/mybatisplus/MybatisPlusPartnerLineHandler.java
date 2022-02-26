package com.kl.db.starter.mybatisplus;


import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.kl.core.constants.CommonConstants;
import com.kl.core.constants.RunMode;
import com.kl.core.enums.ResponseCode;
import com.kl.core.exception.KlException;
import com.kl.core.thread.KlThreadLocal;
import com.kl.db.starter.config.IgnorePartnerTableConfig;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

/**
 * 多合作方自定义配置
 * 多租户 != 权限过滤,不要乱用,租户之间是完全隔离的!!!
 * 启用多租户后所有执行的method的sql都会进行处理.
 * 自写的sql请按规范书写(sql涉及到多个表的每个表都要给别名,特别是 inner join 的要写标准的 inner join)
 */
@Slf4j
public class MybatisPlusPartnerLineHandler implements TenantLineHandler {

    private String mode;

    public MybatisPlusPartnerLineHandler(String mode) {
        this.mode = mode;
    }

    /**
     * 获取租户字段名
     *
     * @return 租户字段名
     */
    @Override
    public String getTenantIdColumn() {
        return CommonConstants.TENANT_ID;
    }

    @Override
    public Expression getTenantId() {
        Integer tenantId = KlThreadLocal.getTenantId();
        if (tenantId == null) {
            return null;
        }
        return new LongValue(tenantId);
    }

    // 这是 default 方法,默认返回 false 表示所有表都需要拼多租户条件
    @Override
    public boolean ignoreTable(String tableName) {
        if (KlThreadLocal.isIgnorePartner()) {
            // admin操作不进行sql转换
            return true;
        }
        tableName = tableName.replace("`", "");
        boolean ignore = IgnorePartnerTableConfig.IGNORE_TABLES.stream().anyMatch(tableName::equalsIgnoreCase);
        if (!ignore && KlThreadLocal.getTenantId() == null) {
            if (RunMode.PROD.equals(mode)) {
                log.error("[PartnerCodeCheck]sql语句参数没有partnerCode, tableName: {}", tableName);
                return true;
            }
            throw new KlException(ResponseCode.PARTNER_CODE_NOT_EXISTS.getCode(), ResponseCode.PARTNER_CODE_NOT_EXISTS.getDescription());
        }
        return ignore;
    }
}
