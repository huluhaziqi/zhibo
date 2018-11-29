package com.lin.MyTest.exception;

public class TestStateException extends AbstractBaseException {

    private static final long serialVersionUID = -6110267739680626200L;

    public TestStateException(TestStateExceptionEnum TestStateEnum) {
        super(TestStateEnum.code, TestStateEnum.message);
    }

    public TestStateException(TestStateExceptionEnum TestStateEnum, String logMsg) {
        super(TestStateEnum.code, TestStateEnum.message, logMsg);
    }

    //11xxxx
    public enum TestStateExceptionEnum {

        Test_CREATE_FAILED(110000,"xxxx创建失败"),
        Test_NOT_EXIST(110001, "该xx不存在，看看其它的吧"),// 该xxxx已被删除
        Test_NO_HOST_AUTHORITY(110002, "您无权操作这个xxxx"),// 您没有该xxxx的管理权限
        Test_CLOSED(110003, "该xxxx已被关闭"),// 该xxxx已被关闭
        // 二维码错
        Test_DETAIL_REQUEST_ERROR(110004, "二维码错误，请重新扫描"),
        //创建xxxx频率超过限制
        Test_CREATE_LIMIT_EXCEPTION(110005, "您的操作太频繁，请稍后再试"),
        // 当前xxxx已经关联该xx了
        Test_BUILDING_EXIST(110006, "该xx已存在，直接推送即可"),
        // 该关联已被取消
        Test_BUILDING_NOT_EXIST(110007, "该关联已被取消"),
        Test_ON_SHOW_CAN_NOT_BE_DELETE(110008, "请结束xx后再删除"),

        UPLOAD_Test_NOT_EXIST(110009,"该上传xx不存在，看看其他的吧"),
        UPLOAD_Test_TRANSCODE_NOT_COMPLETE(110010,"该上传视频转码尚未完成"),

        Test_TITLE_FORBIDDEN(110011, "xxxx标题涉嫌违规，请遵守xx规范"),
        Test_COMMENT_FORBIDDEN(110012, "评论涉嫌违规，请遵守xx规范"),
        USER_NICKNAME_FORBIDDEN(110013, "用户昵称涉嫌违规，请遵守xx规范"),

        UPLOAD_FILE_ALREADY_EXIST(110014,"该视频已经上传，请勿重复上传"),
        UPLOAD_FILE_FAILED(110015,"视频上传失败，请稍后重试"),
        Test_FILE_ID_NOT_EXIST(110016,"该xxfileId不存在"),
        Test_CUT_TASK__EXIST(110017,"该xxxx有未完成的视频剪辑任务"),
        Test_CUT_FAILED(110018,"xxxx剪切失败"),
        Test_IMAGE_ILLEGAL(110019,"xxxx封面非法"),

        ;

        int code;
        String message;

        TestStateExceptionEnum(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
