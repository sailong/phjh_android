package com.qianjinzhe.lcd_display.bean;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              MediaEntity.java
 * Description：
 *              多媒体文件对象
 * Author:
 *              youngHu
 * Finished：
 *              2018年04月13日
 ********************************************************/

public class MediaEntity {
    /**多媒体文件地址*/
    private String mediaFilePath;
    /**多媒体文件名称*/
    private String mediaFileName;
    /**多媒体类型,1图片，2视频文件*/
    private String mediaType;
    /**文件大小*/
    private long fileSize;

    public MediaEntity() {
    }

    public String getMediaFilePath() {
        return mediaFilePath;
    }

    public void setMediaFilePath(String mediaFilePath) {
        this.mediaFilePath = mediaFilePath;
    }

    public String getMediaFileName() {
        return mediaFileName;
    }

    public void setMediaFileName(String mediaFileName) {
        this.mediaFileName = mediaFileName;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
