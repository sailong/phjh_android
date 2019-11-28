package com.qianjinzhe.lcd_display.bean;

/******************************************************
 * Copyrights @ 2018，Shenzhen Qianjinzhe Technologies Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              HistoryShowEntity.java
 * Description：
 *              历史叫号或等待叫号信息对象(方便与html js而创建)
 * Author:
 *              youngHu
 * Finished：
 *              2018年08月13日
 ********************************************************/

public class HistoryShowEntity {
    /**科室名称*/
    private String departmentName;
    /**历史叫号*/
    private String value;

    public HistoryShowEntity() {
    }

    public HistoryShowEntity(String departmentName, String value) {
        this.departmentName = departmentName;
        this.value = value;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
