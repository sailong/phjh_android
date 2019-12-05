package com.qianjinzhe.lcd_display.util;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               前进者科技
 * All rights reserved.
 *
 * Filename：
 *              Constants.java
 * Description：
 *              常量
 * Author:
 *              youngHu
 * Finished：
 *             2018年05月31日
 ********************************************************/

public class Constants {

    /************************语言标识常量*****************************/
    /**简体中文code*/
    public static final String LANGUAGE_CN = "CN";
    /**繁体中文code*/
    public static final String LANGUAGE_TW = "TW";
    /**繁体中文code香港*/
    public static final String LANGUAGE_HK = "HK";
    /**英文文code*/
    public static final String LANGUAGE_EN = "EN";

    /*****************************消息类型***********************************/
    /**电视登录*/
    public static final int QQS_TVD_LOGIN = 101;
    /**电视向服务器请求更新文件*/
    public static final int QQS_TVD_REQUESTFILE = 102;

    /**服务器向电视发送数据开始*/
    public static final int QQS_TVD_SENDSTART = 103;
    /**服务器向电视发送文件，Arg1文件长度，Arg3目标目录，Arg4文件名称*/
    public static final int QQS_TVD_SENDFILE = 104;
    /**服务器向电视发送数据结束*/
    public static final int QQS_TVD_SENDEND = 105;
    /**服务器发送叫号信息至电视*/
    public static final int QQS_TVD_CALLINFO = 106;
    /**服务器发送员工信息至电视*/
    public static final int QQS_TVD_USERINFO = 107;
    /**服务器发送暂停服务或取消暂停服务,1暂停服务，0取消暂停服务*/
    public static final int QQS_TVD_PAUSE_SERVICE = 108;
    /**被踢下线,被强制退出*/
    public static final int QQS_TVD_FORCED_OFFLINE = 14;

    public static final int QQS_TVD_HEARTBEAT = 0x999;

    /**************************多媒体播放类型***********************************/
    /**轮播图片*/
    public static final String MEDIA_PLAY_TYPE_IMAGE = "1";
    /**播放视频*/
    public static final String MEDIA_PLAY_TYPE_VIDEO = "2";

    /*************************开机和关机标识****************************/
    /**锁屏或关机标识*/
    public static final int SCREEN_TAG_LOCK = 0;
    /**开机或解锁标识*/
    public static final int SCREEN_TAG_BOOT = 1;

    /*******************************多媒体舒服存在类型，0没有多媒体，1员工照片，2多媒体****************************/
    /**没有多媒体*/
    public static final String MEDIA_HAS_TYPE_NOT = "0";
    /**存在员工照片*/
    public static final String MEDIA_HAS_TYPE_HEADPIC = "1";
    /**存在多媒体*/
    public static final String MEDIA_HAS_TYPE_YES = "2";

    /***********************多媒体文件和html文件Map Key************************/
    /**多媒体Key*/
    public static final String KEY_MEDIA = "MediaKey";
    /**Html相关文件key*/
    public static final String KEY_HTML_FILE = "HtmlFileKey";


}
