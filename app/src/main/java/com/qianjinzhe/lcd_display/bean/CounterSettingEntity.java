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
 *              CounterSettingEntity.java
 * Description：
 *              窗口设置实体
 * Author:
 *              youngHu
 * Finished：
 *              2018年06月16日
 ********************************************************/
public class CounterSettingEntity implements Parcelable{
    /**窗口号,默认Default，1,2*/
    private String counterNo;
    /**模板id*/
    private String templateId;
    /**标题*/
    private String title;
    /**业务名称*/
    private String serviceName;
    /**窗口别名*/
    private String counterAlias;
    /**提示文字*/
    private String tipText;
    /**是否选中*/
    private boolean isSelect;

    public CounterSettingEntity() {
    }

    protected CounterSettingEntity(Parcel in) {
        counterNo = in.readString();
        templateId = in.readString();
        title = in.readString();
        serviceName = in.readString();
        counterAlias = in.readString();
        tipText = in.readString();
        isSelect = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(counterNo);
        dest.writeString(templateId);
        dest.writeString(title);
        dest.writeString(serviceName);
        dest.writeString(counterAlias);
        dest.writeString(tipText);
        dest.writeByte((byte) (isSelect ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CounterSettingEntity> CREATOR = new Creator<CounterSettingEntity>() {
        @Override
        public CounterSettingEntity createFromParcel(Parcel in) {
            return new CounterSettingEntity(in);
        }

        @Override
        public CounterSettingEntity[] newArray(int size) {
            return new CounterSettingEntity[size];
        }
    };

    public String getCounterNo() {
        return counterNo;
    }

    public void setCounterNo(String counterNo) {
        this.counterNo = counterNo;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTitle() {
        if(TextUtils.isEmpty(title)){
            return "";
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getServiceName() {
        if(TextUtils.isEmpty(serviceName)){
            return "";
        }
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCounterAlias() {
        if(TextUtils.isEmpty(counterAlias)){
            return "";
        }
        return counterAlias;
    }

    public void setCounterAlias(String counterAlias) {
        this.counterAlias = counterAlias;
    }

    public String getTipText() {
        if(TextUtils.isEmpty(tipText)){
            return "";
        }
        return tipText;
    }

    public void setTipText(String tipText) {
        this.tipText = tipText;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public String toString() {
        return "CounterSettingEntity{" +
                "counterNo='" + counterNo + '\'' +
                ", templateId='" + templateId + '\'' +
                ", title='" + title + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", counterAlias='" + counterAlias + '\'' +
                ", tipText='" + tipText + '\'' +
                '}';
    }
}
