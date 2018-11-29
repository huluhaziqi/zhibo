package com.lin.MyTest.controller;

import com.lin.MyTest.enums.VideoTypeEnum;
import com.lin.MyTest.exception.LiveroomStateException;
import com.lin.MyTest.model.request.LiveroomCreateRequest;
import com.lin.MyTest.model.request.LiveroomUpdateRequest;
import com.lin.MyTest.service.LiveroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class LiveroomController {

    @Autowired
    private LiveroomService liveroomService;

    /**
     * 创建直播间，videoType创建类型，1创建直播间，2上传直播间
     */
    @RequestMapping(value = "/liverooms/create", method = POST)
    public Long create(@Valid @RequestBody LiveroomCreateRequest request) {
        Long liveroomId = null;
        if (request.getTitle() != null){
            if(request.getTitle().toLowerCase().contains("script")){
                throw new LiveroomStateException(LiveroomStateException.LiveroomStateExceptionEnum.LIVEROOM_TITLE_FORBIDDEN);
            }
        }
        if (request.getImagePath() != null){
            if(request.getImagePath().toLowerCase().contains("script")){
                throw new LiveroomStateException(LiveroomStateException.LiveroomStateExceptionEnum.LIVEROOM_IMAGE_ILLEGAL);
            }
        }
        if (request.getVideoType().equals(VideoTypeEnum.LIVEROOM.getValue())) {
            liveroomId = liveroomService.create(request.getUserId(), request.getTitle(),
                    request.getImagePath(), request.getCityId(), request.getType(), request.getScheduledTime(),
                    request.getTagId(), request.getBuildingId(), request.getBuildingType());
        } else if (request.getVideoType().equals(VideoTypeEnum.UPLOAD.getValue())) {
            liveroomId = liveroomService.createPlayBack(request.getUserId(), request.getTitle(), request.getImagePath(),
                    request.getCityId(), request.getType(), request.getTagId(), request.getBuildingId(),
                    request.getBuildingType(), request.getFileId());
        }
        return liveroomId;
    }

    /**
     * 直播间更新接口
     */
    @RequestMapping(value = "/liverooms/{id}/update", method = POST)
    public void update(@PathVariable("id") long liveroomId, @Valid @RequestBody LiveroomUpdateRequest request) {
        liveroomService.update(liveroomId, request.getTitle(), request.getImagePath(), request.getCityId(), request.getScheduledTime(),
                request.getTagIds(), request.getBuildingId(), request.getBuildingType(), request.getCountryCodes());
    }


    /**
     * 直播间开始
     */
    @RequestMapping(value = "/liverooms/{id}/start", method = POST)
    public void start(@PathVariable("id") long liveroomId) {
        liveroomService.start(liveroomId);
    }

    /**
     * 直播间关闭
     */
    @RequestMapping(value = "/liverooms/{id}/close", method = POST)
    public void close(@PathVariable("id") long liveroomId) {
        liveroomService.close(liveroomId);
    }

    /**
     * 直播间删除
     */
    @RequestMapping(value = "/liverooms/{id}/delete", method = POST)
    public void delete(@PathVariable("id") long liveroomId) {
        liveroomService.delete(liveroomId);
    }

    /**
     * 直播间ban
     */
    @RequestMapping(value = "/liverooms/{id}/ban", method = POST)
    public void ban(@PathVariable("id") long liveroomId, @RequestParam String banReason) {
        liveroomService.ban(liveroomId, banReason);
    }

    /**
     * 直播间警告
     */
    @RequestMapping(value = "/liverooms/{id}/warn", method = POST)
    public void warn(@PathVariable("id") long liveroomId, @RequestParam String warnMsg) {
        liveroomService.warn(liveroomId, warnMsg);
    }

    /**
     * 恢复直播间
     */
    @RequestMapping(value = "/liverooms/{id}/resume", method = POST)
    public void resume(@PathVariable("id") long liveroomId) {
        liveroomService.resume(liveroomId);
    }

    /**
     * 通过上传直播间
     */
    @RequestMapping(value = "/liverooms/{id}/passUpload", method = POST)
    public void passUpload(@PathVariable("id") long liveroomId) {
        liveroomService.passUpload(liveroomId);
    }

    /**
     * 过期预告
     */
    @RequestMapping(value = "/liverooms/{id}/expire", method = POST)
    public void expire(@PathVariable("id") long liveroomId) {
        liveroomService.expire(liveroomId);
    }



}