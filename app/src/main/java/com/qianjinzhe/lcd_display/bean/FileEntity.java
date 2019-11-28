package com.qianjinzhe.lcd_display.bean;

/******************************************************
 * Copyrights @ 2018，Shenzhen Qianjinzhe Technologies Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              FileEntity.java
 * Description：
 *              文件实体类
 * Author:
 *              youngHu
 * Finished：
 *              2018年12月29日
 ********************************************************/

public class FileEntity {
    /**文件名称*/
    private String fileName;
    /**文件路径*/
    private String filePath;

    public FileEntity() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
