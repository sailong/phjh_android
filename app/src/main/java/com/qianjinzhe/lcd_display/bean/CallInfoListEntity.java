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
 *              CallInfoListEntity.java
 * Description：
 *              叫号信息集合(方便动态分类)
 * Author:
 *              youngHu
 * Finished：
 *              2018年08月13日
 ********************************************************/

public class CallInfoListEntity implements Parcelable{

    /**科室名称,就是xml中的ServiceName*/
    private String departmentName;
    /**历史叫号或等待叫号集合*/
    private ArrayList<CallInfoEntity> historyCallList;
    /**最多等候数据的条数*/
    private int limitPerService;
    /**排序编号*/
    private String serviceOrder;
    /**业务编号*/
    private int serviceNo;
    /**业务类型*/
    private String serviceType;

    public CallInfoListEntity() {
    }

    public CallInfoListEntity(String departmentName, ArrayList<CallInfoEntity> historyCallList) {
        this.departmentName = departmentName;
        this.historyCallList = historyCallList;
    }

    protected CallInfoListEntity(Parcel in) {
        departmentName = in.readString();
        limitPerService = in.readInt();
        serviceOrder = in.readString();
        serviceNo = in.readInt();
        serviceType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(departmentName);
        dest.writeInt(limitPerService);
        dest.writeString(serviceOrder);
        dest.writeInt(serviceNo);
        dest.writeString(serviceType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CallInfoListEntity> CREATOR = new Creator<CallInfoListEntity>() {
        @Override
        public CallInfoListEntity createFromParcel(Parcel in) {
            return new CallInfoListEntity(in);
        }

        @Override
        public CallInfoListEntity[] newArray(int size) {
            return new CallInfoListEntity[size];
        }
    };

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public ArrayList<CallInfoEntity> getHistoryCallList() {
        return historyCallList;
    }

    public void setHistoryCallList(ArrayList<CallInfoEntity> historyCallList) {
        this.historyCallList = historyCallList;
    }

    public int getLimitPerService() {
        return limitPerService;
    }

    public void setLimitPerService(int limitPerService) {
        this.limitPerService = limitPerService;
    }

    public String getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(String serviceOrder) {
        this.serviceOrder = serviceOrder;
    }

    public int getServiceNo() {
        return serviceNo;
    }

    public void setServiceNo(int serviceNo) {
        this.serviceNo = serviceNo;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    @Override
    public String toString() {
        return "CallInfoListEntity{" +
                "departmentName='" + departmentName + '\'' +
                ", historyCallList=" + historyCallList +
                ", limitPerService=" + limitPerService +
                ", serviceOrder='" + serviceOrder + '\'' +
                ", serviceNo=" + serviceNo +
                ", serviceType='" + serviceType + '\'' +
                '}';
    }
}
