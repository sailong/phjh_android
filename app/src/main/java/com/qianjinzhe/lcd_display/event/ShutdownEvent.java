package com.qianjinzhe.lcd_display.event;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              ShutdownEvent.java
 * Description：
 *              执行关机Event,通知关闭更新或取消关机时，重新更新
 * Author:
 *              youngHu
 * Finished：
 *              2018年06月20日
 ********************************************************/

public class ShutdownEvent {
    /**标识，1关闭更新弹窗，2取消关机，显示更新*/
    private int tag;

    public ShutdownEvent(int tag) {
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }
}
