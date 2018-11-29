package com.lin.MyTest.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class TestProcedureNotifyRequest {

    private String version;
    private String eventType;
    private DataBean data;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataBean {

        private String vodTaskId;
        private String status;
        private String message;
        private int errCode;
        private String fileId;
        private MetaDataBean metaData;
        private DrmBean drm;
        private List<ProcessTaskListBean> processTaskList;

        public String getVodTaskId() {
            return vodTaskId;
        }

        public void setVodTaskId(String vodTaskId) {
            this.vodTaskId = vodTaskId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getErrCode() {
            return errCode;
        }

        public void setErrCode(int errCode) {
            this.errCode = errCode;
        }

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }

        public MetaDataBean getMetaData() {
            return metaData;
        }

        public void setMetaData(MetaDataBean metaData) {
            this.metaData = metaData;
        }

        public DrmBean getDrm() {
            return drm;
        }

        public void setDrm(DrmBean drm) {
            this.drm = drm;
        }

        public List<ProcessTaskListBean> getProcessTaskList() {
            return processTaskList;
        }

        public void setProcessTaskList(List<ProcessTaskListBean> processTaskList) {
            this.processTaskList = processTaskList;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class MetaDataBean {

            private long size;
            private String container;
            private int bitrate;
            private int height;
            private int width;
            private String md5;
            private int duration;
            private List<VideoStreamListBean> videoStreamList;
            private List<AudioStreamListBean> audioStreamList;

            public long getSize() {
                return size;
            }

            public void setSize(long size) {
                this.size = size;
            }

            public String getContainer() {
                return container;
            }

            public void setContainer(String container) {
                this.container = container;
            }

            public int getBitrate() {
                return bitrate;
            }

            public void setBitrate(int bitrate) {
                this.bitrate = bitrate;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public String getMd5() {
                return md5;
            }

            public void setMd5(String md5) {
                this.md5 = md5;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }

            public List<VideoStreamListBean> getVideoStreamList() {
                return videoStreamList;
            }

            public void setVideoStreamList(List<VideoStreamListBean> videoStreamList) {
                this.videoStreamList = videoStreamList;
            }

            public List<AudioStreamListBean> getAudioStreamList() {
                return audioStreamList;
            }

            public void setAudioStreamList(List<AudioStreamListBean> audioStreamList) {
                this.audioStreamList = audioStreamList;
            }

            public static class VideoStreamListBean {

                private int bitrate;
                private int height;
                private int width;
                private String codec;
                private int fps;

                public int getBitrate() {
                    return bitrate;
                }

                public void setBitrate(int bitrate) {
                    this.bitrate = bitrate;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public String getCodec() {
                    return codec;
                }

                public void setCodec(String codec) {
                    this.codec = codec;
                }

                public int getFps() {
                    return fps;
                }

                public void setFps(int fps) {
                    this.fps = fps;
                }
            }

            public static class AudioStreamListBean {

                private String codec;
                private int samplingRate;
                private int bitrate;


                public String getCodec() {
                    return codec;
                }

                public void setCodec(String codec) {
                    this.codec = codec;
                }

                public int getSamplingRate() {
                    return samplingRate;
                }

                public void setSamplingRate(int samplingRate) {
                    this.samplingRate = samplingRate;
                }

                public int getBitrate() {
                    return bitrate;
                }

                public void setBitrate(int bitrate) {
                    this.bitrate = bitrate;
                }
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class DrmBean {

            private int definition;
            private String getKeyUrl;
            private String keySource;
            private List<String> edkList;

            public int getDefinition() {
                return definition;
            }

            public void setDefinition(int definition) {
                this.definition = definition;
            }

            public String getGetKeyUrl() {
                return getKeyUrl;
            }

            public void setGetKeyUrl(String getKeyUrl) {
                this.getKeyUrl = getKeyUrl;
            }

            public String getKeySource() {
                return keySource;
            }

            public void setKeySource(String keySource) {
                this.keySource = keySource;
            }

            public List<String> getEdkList() {
                return edkList;
            }

            public void setEdkList(List<String> edkList) {
                this.edkList = edkList;
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ProcessTaskListBean {

            private String taskType;
            private String status;
            private int errCode;
            private String message;

            @JsonProperty("input")
            private InputBean input;

            @JsonProperty("output")
            private OutPutBean output;

            public String getTaskType() {
                return taskType;
            }

            public void setTaskType(String taskType) {
                this.taskType = taskType;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public int getErrCode() {
                return errCode;
            }

            public void setErrCode(int errCode) {
                this.errCode = errCode;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public InputBean getInput() {
                return input;
            }

            public void setInput(InputBean input) {
                this.input = input;
            }

            public OutPutBean getOutPut() {
                return output;
            }

            public void setOutPut(OutPutBean outPut) {
                this.output = outPut;
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class InputBean {

                private int definition;
                private int watermark;
                private String positionType;
                private int position;

                public int getDefinition() {
                    return definition;
                }

                public void setDefinition(int definition) {
                    this.definition = definition;
                }

                public int getWatermark() {
                    return watermark;
                }

                public void setWatermark(int watermark) {
                    this.watermark = watermark;
                }

                public String getPositionType() {
                    return positionType;
                }

                public void setPositionType(String positionType) {
                    this.positionType = positionType;
                }

                public int getPosition() {
                    return position;
                }

                public void setPosition(int position) {
                    this.position = position;
                }
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class OutPutBean {

                private String url;
                private long size;
                private String container;
                private int bitrate;
                private int height;
                private int width;
                private String md5;
                private List<VideoStreamBean> videoStreamList;
                private List<AudioStreamBean> audioStreamList;

                private List<String> imageUrls;
                private String imageUrl;

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public long getSize() {
                    return size;
                }

                public void setSize(long size) {
                    this.size = size;
                }

                public String getContainer() {
                    return container;
                }

                public void setContainer(String container) {
                    this.container = container;
                }

                public int getBitrate() {
                    return bitrate;
                }

                public void setBitrate(int bitrate) {
                    this.bitrate = bitrate;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public String getMd5() {
                    return md5;
                }

                public void setMd5(String md5) {
                    this.md5 = md5;
                }

                public List<VideoStreamBean> getVideoStreamList() {
                    return videoStreamList;
                }

                public void setVideoStreamList(List<VideoStreamBean> videoStreamList) {
                    this.videoStreamList = videoStreamList;
                }

                public List<AudioStreamBean> getAudioStreamList() {
                    return audioStreamList;
                }

                public void setAudioStreamList(List<AudioStreamBean> audioStreamList) {
                    this.audioStreamList = audioStreamList;
                }

                public List<String> getImageUrls() {
                    return imageUrls;
                }

                public void setImageUrls(List<String> imageUrls) {
                    this.imageUrls = imageUrls;
                }

                public String getImageUrl() {
                    return imageUrl;
                }

                public void setImageUrl(String imageUrl) {
                    this.imageUrl = imageUrl;
                }

                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class VideoStreamBean {

                    private int bitrate;
                    private int height;
                    private int width;
                    private String codec;
                    private int fps;

                    public int getBitrate() {
                        return bitrate;
                    }

                    public void setBitrate(int bitrate) {
                        this.bitrate = bitrate;
                    }

                    public int getHeight() {
                        return height;
                    }

                    public void setHeight(int height) {
                        this.height = height;
                    }

                    public int getWidth() {
                        return width;
                    }

                    public void setWidth(int width) {
                        this.width = width;
                    }

                    public String getCodec() {
                        return codec;
                    }

                    public void setCodec(String codec) {
                        this.codec = codec;
                    }

                    public int getFps() {
                        return fps;
                    }

                    public void setFps(int fps) {
                        this.fps = fps;
                    }
                }

                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class AudioStreamBean {

                    private String codec;
                    private int samplingRate;
                    private int bitrate;


                    public String getCodec() {
                        return codec;
                    }

                    public void setCodec(String codec) {
                        this.codec = codec;
                    }

                    public int getSamplingRate() {
                        return samplingRate;
                    }

                    public void setSamplingRate(int samplingRate) {
                        this.samplingRate = samplingRate;
                    }

                    public int getBitrate() {
                        return bitrate;
                    }

                    public void setBitrate(int bitrate) {
                        this.bitrate = bitrate;
                    }
                }
            }
        }
    }
}
