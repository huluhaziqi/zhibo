package com.lin.MyTest.enums;

public enum ChannelIdEnum {
    NEW_HOUSE(1),SECOND_HOUSE(2),DECORATE(3),EXPERT(4),ESTATE_KNOWLEDGE(5),OVERSEAS(6);
    private byte value;
    ChannelIdEnum(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }
}
