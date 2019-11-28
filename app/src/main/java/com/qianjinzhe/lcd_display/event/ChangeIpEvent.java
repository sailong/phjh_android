package com.qianjinzhe.lcd_display.event;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              ChangeIpEvent.java
 * Description：
 *              修改ip Event
 * Author:
 *              youngHu
 * Finished：
 *              2018年06月06日
 ********************************************************/

public class ChangeIpEvent {
    /**服务器ip*/
    private String serviceIp;
    /**窗口号*/
    private int counterNo;

    public ChangeIpEvent(String serviceIp,int counterNo) {
        this.serviceIp = serviceIp;
        this.counterNo = counterNo;
    }

    public String getServiceIp() {
        return serviceIp;
    }


    public int getCounterNo() {
        return counterNo;
    }
}
