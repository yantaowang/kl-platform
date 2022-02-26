package com.kl.core.thread;


import com.kl.core.constants.CommonConstants;
import com.kl.core.enums.ResponseCode;
import com.kl.core.exception.KlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class KlThreadLocal {
    private static final Logger log = LoggerFactory.getLogger(KlThreadLocal.class);
    private static final ThreadLocal<Integer> partnerThreadLocal = new ThreadLocal();
    private static String enablePartner = null;
    private static String mode = null;

    public KlThreadLocal() {
    }

    public static void initConfig(String enablePartner, String mode) {
        KlThreadLocal.enablePartner = enablePartner;
        KlThreadLocal.mode = mode;
    }

    public static void setTenantId(Integer tenantId) {
        partnerThreadLocal.set(tenantId);
    }

    public static Integer getTenantId() {
        return (Integer)partnerThreadLocal.get();
    }

    public static Integer getTenantIdNotNull() {
        Integer tenantId = (Integer)partnerThreadLocal.get();
        if (tenantId == null) {
            if (!"true".equals(enablePartner)) {
                return null;
            } else if ("prod".equals(mode)) {
                log.error("[KlThreadLocalGetTenantIdNotNull]没有合作方id");
                return null;
            } else {
                throw new KlException(ResponseCode.PARTNER_CODE_NOT_EXISTS.getCode(), ResponseCode.PARTNER_CODE_NOT_EXISTS.getDescription());
            }
        } else {
            return tenantId;
        }
    }

    public static void remove() {
        partnerThreadLocal.remove();
    }

    public static void ignorePartner() {
        partnerThreadLocal.set(CommonConstants.ADMIN_PARTNER_CODE);
    }

    public static boolean isIgnorePartner() {
        return CommonConstants.ADMIN_PARTNER_CODE.equals(getTenantId());
    }

    /**
     * 临时指定合作方id
     *
     * @param supplier
     * @param <T>
     * @return
     */
    public static <T> T tempIgnorePartner(Supplier<T> supplier) {
        Integer partnerId = getTenantId();

        T t;
        try {
            ignorePartner();
            t = supplier.get();
        } finally {
            setTenantId(partnerId);
        }

        return t;
    }

    public static <T> T tempAssignPartner(Integer partnerId, Supplier<T> supplier) {
        Integer oldPartnerCode = getTenantId();

        T t;
        try {
            setTenantId(partnerId);
            t = supplier.get();
        } finally {
            setTenantId(oldPartnerCode);
        }

        return t;
    }
}
