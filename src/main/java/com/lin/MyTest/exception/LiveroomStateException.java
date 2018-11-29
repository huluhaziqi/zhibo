package com.lin.MyTest.exception;

public class LiveroomStateException extends AbstractBaseException {

    private static final long serialVersionUID = -6110267739680626200L;

    public LiveroomStateException(LiveroomStateExceptionEnum liveroomStateEnum) {
        super(liveroomStateEnum.code, liveroomStateEnum.message);
    }

    public LiveroomStateException(LiveroomStateExceptionEnum liveroomStateEnum, String logMsg) {
        super(liveroomStateEnum.code, liveroomStateEnum.message, logMsg);
    }

    //11xxxx
    public enum LiveroomStateExceptionEnum {

        LIVEROOM_CREATE_FAILED(110000,"直播间创建失败"),
        LIVEROOM_NOT_EXIST(110001, "该直播不存在，看看其它的吧"),// 该直播间已被删除
        LIVEROOM_NO_HOST_AUTHORITY(110002, "您无权操作这个直播间"),// 您没有该直播间的管理权限
        LIVEROOM_CLOSED(110003, "该直播间已被关闭"),// 该直播间已被关闭
        // 二维码错
        LIVEROOM_DETAIL_REQUEST_ERROR(110004, "二维码错误，请重新扫描"),
        //创建直播间频率超过限制
        LIVEROOM_CREATE_LIMIT_EXCEPTION(110005, "您的操作太频繁，请稍后再试"),
        // 当前直播间已经关联该楼盘了
        LIVEROOM_BUILDING_EXIST(110006, "该楼盘已存在，直接推送即可"),
        // 该关联已被取消
        LIVEROOM_BUILDING_NOT_EXIST(110007, "该关联已被取消"),
        LIVEROOM_ON_SHOW_CAN_NOT_BE_DELETE(110008, "请结束直播后再删除"),

        UPLOAD_LIVEROOM_NOT_EXIST(110009,"该上传直播不存在，看看其他的吧"),
        UPLOAD_LIVEROOM_TRANSCODE_NOT_COMPLETE(110010,"该上传视频转码尚未完成"),

        LIVEROOM_TITLE_FORBIDDEN(110011, "直播间标题涉嫌违规，请遵守直播规范"),
        LIVEROOM_COMMENT_FORBIDDEN(110012, "评论涉嫌违规，请遵守直播规范"),
        USER_NICKNAME_FORBIDDEN(110013, "用户昵称涉嫌违规，请遵守直播规范"),

        UPLOAD_FILE_ALREADY_EXIST(110014,"该视频已经上传，请勿重复上传"),
        UPLOAD_FILE_FAILED(110015,"视频上传失败，请稍后重试"),
        LIVEROOM_FILE_ID_NOT_EXIST(110016,"该直播fileId不存在"),
        LIVEROOM_CUT_TASK__EXIST(110017,"该直播间有未完成的视频剪辑任务"),
        LIVEROOM_CUT_FAILED(110018,"直播间剪切失败"),
        LIVEROOM_IMAGE_ILLEGAL(110019,"直播间封面非法"),

        ;

        int code;
        String message;

        LiveroomStateExceptionEnum(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
