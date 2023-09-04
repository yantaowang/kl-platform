package com.kl.monitor.starter.utils;

import lombok.Getter;

/**
 * 必须要按顺序写，否则会出现顺序错乱，最终导致统计的数量分配错延时桶
 */
@Getter
public enum RequestBucketEnum {
	LT_1ms(1),
	LT_5ms(5),
	LT_10ms(10),
	LT_50ms(50),
	LT_80ms(80),
	LT_100ms(100),
	LT_200ms(200),
	LT_300ms(300),
	LT_400ms(400),
	LT_500ms(500),
	LT_600ms(600),
	LT_700ms(700),
	LT_800ms(800),
	LT_900ms(900),
	LT_1000ms(1000),
	LT_1500ms(1500),
	LT_2000ms(2000),
	LT_3000ms(3000);
	private final int timerBucket;

	RequestBucketEnum(int timerBucket) {
		this.timerBucket = timerBucket;
	}

	public static String getEnumByTime(long timer) {
		RequestBucketEnum[] values = values();
		for (RequestBucketEnum thisEnum : values) {
			int timerBucket = thisEnum.timerBucket;
			if (timer <= timerBucket) {
				return String.valueOf(timerBucket);
			}
		}
		return "gt_3000ms";
	}

}
