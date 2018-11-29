package com.lin.MyTest.enums;

import java.util.HashMap;
import java.util.Map;

public enum TestStatusEnum {

	STATUS_DELETED(-1), STATUS_ONSHOW(1), STATUS_READY_TO_PLAY(2), STATUS_CLOSED(4), STATUS_SCHEDULE_EXPIRED(6), STATUS_BANNED(7),
	STATUS_UPLOAD_READY(21),STATUS_UPLOAD_TRANSCODED(22);

	private byte status;

	public static Map<Byte, TestStatusEnum> TestStatusEnumMap = new HashMap<>();

	static {
		for (TestStatusEnum TestStatusEnum : TestStatusEnum.values()) {
			TestStatusEnumMap.put(TestStatusEnum.getValue(), TestStatusEnum);
		}
	}

	TestStatusEnum(int status) {
		this.status = (byte) status;
	}

	public byte getValue() {
		return this.status;
	}
}
