package com.qianjinzhe.lcd_display.event;

/******************************************************
 * Copyrights @ 2018，Shenzhen Qianjinzhe Technologies Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              NetworkEvent.java
 * Description：
 *              网络状态事假
 * Author:
 *              youngHu
 * Finished：
 *              2018年12月01日
 ********************************************************/

public class NetworkEvent {

    /**是否有网络,true有，false没有*/
    private boolean isHasNet;

    public NetworkEvent(boolean isHasNet) {
        this.isHasNet = isHasNet;
    }

    public boolean isHasNet() {
        return isHasNet;
    }
}
