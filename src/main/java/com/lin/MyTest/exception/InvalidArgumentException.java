package com.lin.MyTest.exception;

public class InvalidArgumentException extends AbstractClientException {

    private static final long serialVersionUID = 4788801654291325375L;

    public InvalidArgumentException(InvalidArgumentExceptionEnum invalidArgumentEnum) {
        super(invalidArgumentEnum.code, invalidArgumentEnum.message);
    }

    public enum InvalidArgumentExceptionEnum {

        COMMON_INPUT_INVALID(100001, "服务器开小差了,请稍后再试"),// 输入数据错误
        COMMON_ENTITY_NOT_EXIST(100003, "服务器开小差了,请稍后再试"),// 无法找到指定的数据
        HOST_APPLICATION_NOT_EXIST(100401, "申请表id有误"),// 申请表id有误
        LIVEROOM_DETAIL_REQUEST_ERROR(100210, "二维码错误，请重新扫描"),// 二维码错
        USER_SMSCODE_ERROR(100112, "短信验证码有误，请重新输入"),// 短信验证码有误
        USER_UPLOAD_VIDEO_NOT_FOUND(100120, "该视频不存在");

        int code;
        String message;

        InvalidArgumentExceptionEnum(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
