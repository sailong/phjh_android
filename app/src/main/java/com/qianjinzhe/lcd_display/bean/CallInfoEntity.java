package com.qianjinzhe.lcd_display.bean;

import com.qianjinzhe.lcd_display.util.TextUtils;

/******************************************************
 * Copyrights @ 2018，Shenzhen Qianjinzhe Technologies Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              CallInfoEntity.java
 * Description：
 *              叫号信息实体
 * Author:
 *              youngHu
 * Finished：
 *              2018年06月13日
 ********************************************************/

public class CallInfoEntity {
    /**窗口号*/
    private String counterNo;
    /**票号*/
    private String ticketNo;
    /**姓名*/
    private String name;
    /**医生名称*/
    private String doctorName;
    /**诊室名称*/
    private String consultingRoomName;
    /**科室名*/
    private String departmentName;
    /**排序序号*/
    private String order;
    /**预约标识,1已预约，0未预约*/
    private String reservation;

    public CallInfoEntity() {
    }

    public CallInfoEntity(String ticketNo, String name) {
        this.ticketNo = ticketNo;
        this.name = name;
    }

    public CallInfoEntity(String counterNo, String ticketNo, String name) {
        this.counterNo = counterNo;
        this.ticketNo = ticketNo;
        this.name = name;
    }

    public CallInfoEntity(String counterNo, String ticketNo, String name, String consultingRoomName) {
        this.counterNo = counterNo;
        this.ticketNo = ticketNo;
        this.name = name;
        this.consultingRoomName = consultingRoomName;
    }

    public CallInfoEntity(String counterNo, String ticketNo, String name, String doctorName, String consultingRoomName, String departmentName) {
        this.counterNo = counterNo;
        this.ticketNo = ticketNo;
        this.name = name;
        this.doctorName = doctorName;
        this.consultingRoomName = consultingRoomName;
        this.departmentName = departmentName;
    }

    public String getCounterNo() {
        if(TextUtils.isEmpty(counterNo)){
            return "";
        }
        return counterNo;
    }

    public void setCounterNo(String counterNo) {
        this.counterNo = counterNo;
    }

    public String getTicketNo() {
        if(TextUtils.isEmpty(ticketNo)){
            return "";
        }
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getName() {
        if(TextUtils.isEmpty(name)){
            return "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getConsultingRoomName() {
        if (TextUtils.isEmpty(consultingRoomName)) {
            return "";
        }
        return consultingRoomName;
    }

    public void setConsultingRoomName(String consultingRoomName) {
        this.consultingRoomName = consultingRoomName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getReservation() {
        return reservation;
    }

    public void setReservation(String reservation) {
        this.reservation = reservation;
    }

    @Override
    public String toString() {
        return "CallInfoEntity{" +
                "counterNo='" + counterNo + '\'' +
                ", ticketNo='" + ticketNo + '\'' +
                ", name='" + name + '\'' +
                ", doctorName='" + doctorName + '\'' +
                ", consultingRoomName='" + consultingRoomName + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", order='" + order + '\'' +
                ", reservation='" + reservation + '\'' +
                '}';
    }
}
