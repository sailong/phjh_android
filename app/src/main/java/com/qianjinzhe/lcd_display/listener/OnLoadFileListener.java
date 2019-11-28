package com.qianjinzhe.lcd_display.listener;

/******************************************************
 * Copyrights @ 2018，Shenzhen Qianjinzhe Technologies Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              OnLoadFileListener.java
 * Description：
 *              加载文件监听
 * Author:
 *              youngHu
 * Finished：
 *              2018年11月08日
 ********************************************************/

public interface OnLoadFileListener<T> {

    void onLoadFile(T t);
}
