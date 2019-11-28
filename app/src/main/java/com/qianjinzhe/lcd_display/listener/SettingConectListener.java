package com.qianjinzhe.lcd_display.listener;

/******************************************************
 * Copyrights @ 2018，Shenzhen Qianjinzhe Technologies Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              SettingConectListener.java
 * Description：
 *              设置连接按钮监听
 * Author:
 *              youngHu
 * Finished：
 *              2018年06月27日
 ********************************************************/

public interface SettingConectListener {
    //网络连接
    void onConnectSet();

    //模板选择
    void onTemplateSelected();

    //选择窗口
    void onCounterSelect();
}
