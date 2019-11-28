package com.qianjinzhe.lcd_display.bean;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              QueryResultEntity.java
 * Description：
 *              按票号窗口号查询返回
 * Author:
 *              youngHu
 * Finished：
 *              2018年06月08日
 ********************************************************/

public class QueryResultEntity {
    /**当前窗口的下一个票号*/
    public String TicketNo;
    /**下一票号的业务编号*/
    public int ServiceNo;
    /**呼叫新号码后的等候人数*/
    public int WaitNum;
    /**没有经过减一的等候人数*/
    public int WiatNumNoMinus;
    /**当前窗口的业务组*/
    public String ServiceGroup;

    public QueryResultEntity() {
    }

    @Override
    public String toString() {
        return "QueryResultEntity{" +
                "TicketNo='" + TicketNo + '\'' +
                ", ServiceNo=" + ServiceNo +
                ", WaitNum=" + WaitNum +
                ", WiatNumNoMinus=" + WiatNumNoMinus +
                ", ServiceGroup='" + ServiceGroup + '\'' +
                '}';
    }
}
