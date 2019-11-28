package com.qianjinzhe.lcd_display.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/******************************************************
 * Copyrights @ 2018，Shenzhen Qianjinzhe Technologies Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              AppSettingEntity.java
 * Description：
 *              配置文件实体
 * Author:
 *              youngHu
 * Finished：
 *              2018年06月16日
 ********************************************************/

public class AppSettingEntity implements Parcelable {
    /**版本号*/
    private String version;
    /**是否自动关机,0不自动，1自动*/
    private String autoShutdownEnable;
    /**自动关机时间,格式如19:00*/
    private String autoShutdownTime;
    /**退出是否使用密码,1使用，0不使用*/
    private String quitPasswordEnable;
    /**退出密码,默认123*/
    private String quitPassword;
    /**窗口集合*/
    private ArrayList<CounterSettingEntity> countersList;
    /**模板集合*/
    private ArrayList<TemplateEntity> templatesList;
    /**模板3(即医院模板)的等候初始化数据*/
    private ArrayList<CallInfoListEntity> waitCallInfoList;

    public AppSettingEntity() {
    }

    protected AppSettingEntity(Parcel in) {
        version = in.readString();
        autoShutdownEnable = in.readString();
        autoShutdownTime = in.readString();
        quitPasswordEnable = in.readString();
        quitPassword = in.readString();
        countersList = in.createTypedArrayList(CounterSettingEntity.CREATOR);
        templatesList = in.createTypedArrayList(TemplateEntity.CREATOR);
        waitCallInfoList = in.createTypedArrayList(CallInfoListEntity.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(version);
        dest.writeString(autoShutdownEnable);
        dest.writeString(autoShutdownTime);
        dest.writeString(quitPasswordEnable);
        dest.writeString(quitPassword);
        dest.writeTypedList(countersList);
        dest.writeTypedList(templatesList);
        dest.writeTypedList(waitCallInfoList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AppSettingEntity> CREATOR = new Creator<AppSettingEntity>() {
        @Override
        public AppSettingEntity createFromParcel(Parcel in) {
            return new AppSettingEntity(in);
        }

        @Override
        public AppSettingEntity[] newArray(int size) {
            return new AppSettingEntity[size];
        }
    };

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAutoShutdownEnable() {
        return autoShutdownEnable;
    }

    public void setAutoShutdownEnable(String autoShutdownEnable) {
        this.autoShutdownEnable = autoShutdownEnable;
    }

    public String getAutoShutdownTime() {
        return autoShutdownTime;
    }

    public void setAutoShutdownTime(String autoShutdownTime) {
        this.autoShutdownTime = autoShutdownTime;
    }

    public String getQuitPasswordEnable() {
        return quitPasswordEnable;
    }

    public void setQuitPasswordEnable(String quitPasswordEnable) {
        this.quitPasswordEnable = quitPasswordEnable;
    }

    public String getQuitPassword() {
        return quitPassword;
    }

    public void setQuitPassword(String quitPassword) {
        this.quitPassword = quitPassword;
    }

    public ArrayList<CounterSettingEntity> getCountersList() {
        return countersList;
    }

    public void setCountersList(ArrayList<CounterSettingEntity> countersList) {
        this.countersList = countersList;
    }

    public ArrayList<TemplateEntity> getTemplatesList() {
        return templatesList;
    }

    public void setTemplatesList(ArrayList<TemplateEntity> templatesList) {
        this.templatesList = templatesList;
    }

    public ArrayList<CallInfoListEntity> getWaitCallInfoList() {
        return waitCallInfoList;
    }

    public void setWaitCallInfoList(ArrayList<CallInfoListEntity> waitCallInfoList) {
        this.waitCallInfoList = waitCallInfoList;
    }

    @Override
    public String toString() {
        return "AppSettingEntity{" +
                "version='" + version + '\'' +
                ", autoShutdownEnable='" + autoShutdownEnable + '\'' +
                ", autoShutdownTime='" + autoShutdownTime + '\'' +
                ", quitPasswordEnable='" + quitPasswordEnable + '\'' +
                ", quitPassword='" + quitPassword + '\'' +
                ", countersList=" + countersList +
                ", templatesList=" + templatesList +
                ", waitCallInfoList=" + waitCallInfoList +
                '}';
    }
}
