package com.lin.MyTest.enums;

public enum VideoTypeEnum {
    Test(1),UPLOAD(2);

    private byte value;

    public byte getValue() {
        return value;
    }

    VideoTypeEnum(int value) {
        this.value = (byte)value;
    }
}
