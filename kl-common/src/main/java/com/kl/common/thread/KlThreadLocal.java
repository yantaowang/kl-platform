package com.kl.common.thread;

import java.util.function.Supplier;

import com.kl.common.constants.CommonConstants;
import com.kl.common.constants.RunMode;
import com.kl.common.enums.ResponseCode;
import com.kl.common.exception.KlException;

import lombok.extern.slf4j.Slf4j;

/**
 * 保存合作方id的线程池
 */
@Slf4j
public class KlThreadLocal {

	private static final ThreadLocal<Integer> PARTNER_THREAD_LOCAL = new ThreadLocal<>();
	private static final ThreadLocal<Integer> PARTNER_THREAD_NO_IGNORE_LOCAL = new ThreadLocal<>();

	private static boolean enablePartner;

	private static String mode = null;

	public static void initConfig(boolean enablePartner, String mode) {
		KlThreadLocal.enablePartner = enablePartner;
		KlThreadLocal.mode = mode;
	}

	/**
	 * 新增合作方信息<br/>
	 * 注意：受 ignorePartner() 方法影响
	 */
	public static void setPartnerCode(Integer partnerId) {
		PARTNER_THREAD_LOCAL.set(partnerId);
		PARTNER_THREAD_NO_IGNORE_LOCAL.set(partnerId);
	}

	/**
	 * 获取合作方信息<br/>
	 * 注意：受 ignorePartner() 方法影响
	 */
	public static Integer getPartnerCode() {
		return PARTNER_THREAD_LOCAL.get();
	}

	/**
	 * 获取合作方信息<br/>
	 * 注意：不受 ignorePartner() 方法影响
	 */
	public static Integer getPartnerCodeNoIgnore() {
		return PARTNER_THREAD_NO_IGNORE_LOCAL.get();
	}

	/**
	 * 获取合作方信息,返回不为空 如果为空则抛出异常<br/>
	 * 注意：受 ignorePartner() 方法影响
	 */
	public static Integer getPartnerCodeNotNull() {
		Integer partnerCode = PARTNER_THREAD_LOCAL.get();
		if (partnerCode == null) {
			if (!enablePartner) {
				// 没有启用合作方插件，忽略异常
				return null;
			}
			if (RunMode.PROD.equals(mode)) {
				log.error("[EwpThreadLocalGetPartnerCodeNotNull]没有合作方id");
				return null;
			}
			throw new KlException(ResponseCode.PARTNER_CODE_NOT_EXISTS.getCode(),
					ResponseCode.PARTNER_CODE_NOT_EXISTS.getDescription());
		}
		return partnerCode;
	}

	public static boolean isEnablePartner() {
		return enablePartner;
	}

	/**
	 * 移除合作方信息
	 */
	public static void remove() {
		PARTNER_THREAD_LOCAL.remove();
		PARTNER_THREAD_NO_IGNORE_LOCAL.remove();
	}

	/**
	 * 不区分合作方
	 */
	public static void ignorePartner() {
		PARTNER_THREAD_LOCAL.set(CommonConstants.ADMIN_PARTNER_CODE);
	}

	/**
	 * 判断是否为admin操作
	 */
	public static boolean isIgnorePartner() {
		if (CommonConstants.ADMIN_PARTNER_CODE.equals(getPartnerCode())) {
			// admin操作不进行sql转换
			return true;
		}
		return false;
	}

	/**
	 * 临时忽略合作方id
	 *
	 * @param supplier
	 * @param <T>
	 * @return
	 */
	public static <T> T tempIgnorePartner(Supplier<T> supplier) {
		Integer partnerId = getPartnerCode();
		try {
			KlThreadLocal.ignorePartner();
			return supplier.get();
		} finally {
			KlThreadLocal.setPartnerCode(partnerId);
		}

	}

	/**
	 * 临时指定合作方id
	 *
	 * @param supplier
	 * @param <T>
	 * @return
	 */
	public static <T> T tempAssignPartner(Integer partnerId, Supplier<T> supplier) {
		Integer oldPartnerCode = getPartnerCode();
		try {
			KlThreadLocal.setPartnerCode(partnerId);
			return supplier.get();
		} finally {
			KlThreadLocal.setPartnerCode(oldPartnerCode);
		}
	}

}
