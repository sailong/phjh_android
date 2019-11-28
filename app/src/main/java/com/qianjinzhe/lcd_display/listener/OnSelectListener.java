package com.qianjinzhe.lcd_display.listener;

/******************************************************
 * Copyrights @ 2019，Shenzhen Qianjinzhe Technologies Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              OnSelectListener.java
 * Description：
 *              窗口选择监听
 * Author:
 *              youngHu
 * Finished：
 *              2019年05月06日
 ********************************************************/

public interface OnSelectListener<T> {

    void onSelected(T t);
}
