package com.qianjinzhe.lcd_display.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.qianjinzhe.lcd_display.util.TextUtils;

/******************************************************
 * Copyrights @ 2018，Shenzhen Qianjinzhe Technologies Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              TemplateEntity.java
 * Description：
 *              模板实体类
 * Author:
 *              youngHu
 * Finished：
 *              2018年06月16日
 ********************************************************/
public class TemplateEntity implements Parcelable {
    /**模板id*/
    private String templateId;
    /**模板描述*/
    private String templateDescription;
    /**模板宽*/
    private int width;
    /**模板高*/
    private int height;
    /**模板背景类型*/
    private String backgroundType;
    /**模板背景图片名称或html名称*/
    private String backgroundName;
    /**背景颜色*/
    private String bgColor;
    /**背景更新时间*/
    private String backgroundModifyDate;
    /**多媒体目录*/
    private String mediaDirectory;
    /**多媒体数量*/
    private int mediaCount;
    /**多媒体更新日期*/
    private String mediaModifyDate;
    /**多媒体清空多媒体,0不清空*/
    private String mediaUpdateEvenEmpty;
    /**多媒体是否显示,0不显示，1显示*/
    private String mediaSwitch;
    /**多媒体图片轮播时间间隔,秒*/
    private int mediaInterval;
    /**多媒体是否执行动画,1是,0否*/
    private String mediaAnimation;
    /**多媒体类型,0没有多媒体，1员工照片,2多媒体文件*/
    private String mediaType;

    /**叫号模板,是否启用叫号模板*/
    private String callTextModel_enable;
    /**叫号文字模板*/
    private String callTextModel_text;
    /**叫号票号长度*/
    private int callTextModel_ticketLen;
    /**叫号窗口长度*/
    private int callTextModel_counterLen;
    /**历史叫号显示方式,UpToBottom表示从上到下显示，BottomToUp表示从下至上显示*/
    private String historyCallShowType;

    /***********医院模板，html相关文件部分**********/
    /**文件目录*/
    private String fileListDirectory;
    /**文件名称,分号隔开*/
    private String fileListNames;
    /**文件更新日期*/
    private String fileListModifyDate;
    /**文件数量*/
    private int fileListCount;

    /**是否选中*/
    private boolean isSelect;

    /**叫号弹窗,是否显示叫号弹窗*/
    private String callPopupWindow_enable;
    /**叫号弹窗持续时间*/
    private int callPopupWindow_duration;
    /**弹窗文字模板*/
    private String callPopupWindow_text;

    /**安卓本地模板id*/
    private String local_template_id;


    public TemplateEntity() {
    }

    public TemplateEntity( String templateDescription,String templateId, String local_template_id) {
        this.templateId = templateId;
        this.templateDescription = templateDescription;
        this.local_template_id = local_template_id;
    }

    protected TemplateEntity(Parcel in) {
        templateId = in.readString();
        templateDescription = in.readString();
        width = in.readInt();
        height = in.readInt();
        backgroundType = in.readString();
        backgroundName = in.readString();
        bgColor = in.readString();
        backgroundModifyDate = in.readString();
        mediaDirectory = in.readString();
        mediaCount = in.readInt();
        mediaModifyDate = in.readString();
        mediaUpdateEvenEmpty = in.readString();
        mediaSwitch = in.readString();
        mediaInterval = in.readInt();
        mediaAnimation = in.readString();
        mediaType = in.readString();
        callTextModel_enable = in.readString();
        callTextModel_text = in.readString();
        callTextModel_ticketLen = in.readInt();
        callTextModel_counterLen = in.readInt();
        historyCallShowType = in.readString();
        fileListDirectory = in.readString();
        fileListNames = in.readString();
        fileListModifyDate = in.readString();
        fileListCount = in.readInt();
        isSelect = in.readByte() != 0;
        callPopupWindow_enable = in.readString();
        callPopupWindow_duration = in.readInt();
        callPopupWindow_text = in.readString();
        local_template_id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(templateId);
        dest.writeString(templateDescription);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(backgroundType);
        dest.writeString(backgroundName);
        dest.writeString(bgColor);
        dest.writeString(backgroundModifyDate);
        dest.writeString(mediaDirectory);
        dest.writeInt(mediaCount);
        dest.writeString(mediaModifyDate);
        dest.writeString(mediaUpdateEvenEmpty);
        dest.writeString(mediaSwitch);
        dest.writeInt(mediaInterval);
        dest.writeString(mediaAnimation);
        dest.writeString(mediaType);
        dest.writeString(callTextModel_enable);
        dest.writeString(callTextModel_text);
        dest.writeInt(callTextModel_ticketLen);
        dest.writeInt(callTextModel_counterLen);
        dest.writeString(historyCallShowType);
        dest.writeString(fileListDirectory);
        dest.writeString(fileListNames);
        dest.writeString(fileListModifyDate);
        dest.writeInt(fileListCount);
        dest.writeByte((byte) (isSelect ? 1 : 0));
        dest.writeString(callPopupWindow_enable);
        dest.writeInt(callPopupWindow_duration);
        dest.writeString(callPopupWindow_text);
        dest.writeString(local_template_id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TemplateEntity> CREATOR = new Creator<TemplateEntity>() {
        @Override
        public TemplateEntity createFromParcel(Parcel in) {
            return new TemplateEntity(in);
        }

        @Override
        public TemplateEntity[] newArray(int size) {
            return new TemplateEntity[size];
        }
    };

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateDescription() {
        return templateDescription;
    }

    public void setTemplateDescription(String templateDescription) {
        this.templateDescription = templateDescription;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getBackgroundName() {
        return backgroundName;
    }

    public void setBackgroundName(String backgroundName) {
        this.backgroundName = backgroundName;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getBackgroundModifyDate() {
        return backgroundModifyDate;
    }

    public void setBackgroundModifyDate(String backgroundModifyDate) {
        this.backgroundModifyDate = backgroundModifyDate;
    }

    public String getMediaDirectory() {
        return mediaDirectory;
    }

    public void setMediaDirectory(String mediaDirectory) {
        this.mediaDirectory = mediaDirectory;
    }

    public int getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(int mediaCount) {
        this.mediaCount = mediaCount;
    }

    public String getMediaModifyDate() {
        return mediaModifyDate;
    }

    public void setMediaModifyDate(String mediaModifyDate) {
        this.mediaModifyDate = mediaModifyDate;
    }

    public String getMediaUpdateEvenEmpty() {
        return mediaUpdateEvenEmpty;
    }

    public void setMediaUpdateEvenEmpty(String mediaUpdateEvenEmpty) {
        this.mediaUpdateEvenEmpty = mediaUpdateEvenEmpty;
    }

    public String getMediaSwitch() {
        return mediaSwitch;
    }

    public void setMediaSwitch(String mediaSwitch) {
        this.mediaSwitch = mediaSwitch;
    }

    public int getMediaInterval() {
        return mediaInterval;
    }

    public void setMediaInterval(int mediaInterval) {
        this.mediaInterval = mediaInterval;
    }

    public String getMediaAnimation() {
        return mediaAnimation;
    }

    public void setMediaAnimation(String mediaAnimation) {
        this.mediaAnimation = mediaAnimation;
    }

    public String getCallTextModel_enable() {
        return callTextModel_enable;
    }

    public void setCallTextModel_enable(String callTextModel_enable) {
        this.callTextModel_enable = callTextModel_enable;
    }

    public String getCallTextModel_text() {
        return callTextModel_text;
    }

    public void setCallTextModel_text(String callTextModel_text) {
        this.callTextModel_text = callTextModel_text;
    }

    public int getCallTextModel_ticketLen() {
        return callTextModel_ticketLen;
    }

    public void setCallTextModel_ticketLen(int callTextModel_ticketLen) {
        this.callTextModel_ticketLen = callTextModel_ticketLen;
    }

    public int getCallTextModel_counterLen() {
        return callTextModel_counterLen;
    }

    public void setCallTextModel_counterLen(int callTextModel_counterLen) {
        this.callTextModel_counterLen = callTextModel_counterLen;
    }

    public String getHistoryCallShowType() {
        return historyCallShowType;
    }

    public void setHistoryCallShowType(String historyCallShowType) {
        this.historyCallShowType = historyCallShowType;
    }

    public String getBackgroundType() {
        return backgroundType;
    }

    public void setBackgroundType(String backgroundType) {
        this.backgroundType = backgroundType;
    }

    public String getFileListDirectory() {
        if (!TextUtils.isEmpty(fileListDirectory)) {
            fileListDirectory = fileListDirectory.replace("\\", "/");
        } else {
            return "";
        }
        return fileListDirectory;
    }

    public void setFileListDirectory(String fileListDirectory) {
        this.fileListDirectory = fileListDirectory;
    }

    public String getFileListNames() {
        return fileListNames;
    }

    public void setFileListNames(String fileListNames) {
        this.fileListNames = fileListNames;
    }

    public String getFileListModifyDate() {
        return fileListModifyDate;
    }

    public void setFileListModifyDate(String fileListModifyDate) {
        this.fileListModifyDate = fileListModifyDate;
    }

    public int getFileListCount() {
        return fileListCount;
    }

    public void setFileListCount(int fileListCount) {
        this.fileListCount = fileListCount;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getCallPopupWindow_enable() {
        if (TextUtils.isEmpty(callPopupWindow_enable)) {
            return "0";
        }
        return callPopupWindow_enable;
    }

    public void setCallPopupWindow_enable(String callPopupWindow_enable) {
        this.callPopupWindow_enable = callPopupWindow_enable;
    }

    public int getCallPopupWindow_duration() {
        return callPopupWindow_duration;
    }

    public void setCallPopupWindow_duration(int callPopupWindow_duration) {
        this.callPopupWindow_duration = callPopupWindow_duration;
    }

    public String getCallPopupWindow_text() {
        return callPopupWindow_text;
    }

    public void setCallPopupWindow_text(String callPopupWindow_text) {
        this.callPopupWindow_text = callPopupWindow_text;
    }

    public String getLocal_template_id() {
        return local_template_id;
    }

    public void setLocal_template_id(String local_template_id) {
        this.local_template_id = local_template_id;
    }

    @Override
    public String toString() {
        return "TemplateEntity{" +
                "templateId='" + templateId + '\'' +
                ", templateDescription='" + templateDescription + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", backgroundType='" + backgroundType + '\'' +
                ", backgroundName='" + backgroundName + '\'' +
                ", bgColor='" + bgColor + '\'' +
                ", backgroundModifyDate='" + backgroundModifyDate + '\'' +
                ", mediaDirectory='" + mediaDirectory + '\'' +
                ", mediaCount=" + mediaCount +
                ", mediaModifyDate='" + mediaModifyDate + '\'' +
                ", mediaUpdateEvenEmpty='" + mediaUpdateEvenEmpty + '\'' +
                ", mediaSwitch='" + mediaSwitch + '\'' +
                ", mediaInterval=" + mediaInterval +
                ", mediaAnimation='" + mediaAnimation + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", callTextModel_enable='" + callTextModel_enable + '\'' +
                ", callTextModel_text='" + callTextModel_text + '\'' +
                ", callTextModel_ticketLen=" + callTextModel_ticketLen +
                ", callTextModel_counterLen=" + callTextModel_counterLen +
                ", historyCallShowType='" + historyCallShowType + '\'' +
                ", fileListDirectory='" + fileListDirectory + '\'' +
                ", fileListNames='" + fileListNames + '\'' +
                ", fileListModifyDate='" + fileListModifyDate + '\'' +
                ", fileListCount=" + fileListCount +
                ", isSelect=" + isSelect +
                ", callPopupWindow_enable='" + callPopupWindow_enable + '\'' +
                ", callPopupWindow_duration=" + callPopupWindow_duration +
                ", callPopupWindow_text='" + callPopupWindow_text + '\'' +
                ", local_template_id='" + local_template_id + '\'' +
                '}';
    }
}
