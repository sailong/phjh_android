package com.qianjinzhe.lcd_display.util;

import android.os.AsyncTask;
import android.util.Log;

import com.qianjinzhe.lcd_display.bean.FileEntity;
import com.qianjinzhe.lcd_display.listener.OnLoadFileListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              LogUtils.java
 * Description：
 *              日志打印工具类
 * Author:
 *              youngHu
 * Finished：
 *              2018年05月31日
 ********************************************************/
public class LogUtils {
    private static String MYLOG_PATH_SDCARD_DIR = "QQSLcdDisplay_Log";// 日志文件在sdcard中的路径
    private static int SDCARD_LOG_FILE_SAVE_DAYS = 10;// sd卡中日志文件的最多保存天
    private static String MYLOGFILEName = "qqslcddisplay_log.txt";// 本类输出的日志文件名
    private static SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 日志的输出格式
    private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式

    /**
     * isDebug :是用来控制，是否打印日志,true打印，false不打印日志
     */
    public static boolean isDeBug = true;

    /**
     * verbose等级的日志输出
     * @param tag
     *            日志标识
     * @param msg
     *            要输出的内容
     * @return void 返回类型
     * @throws
     */
    public static void v(String tag, String msg) {
        // 是否开启日志输出
        if (isDeBug) {
            Log.v(tag, msg);
        }
    }

    /**
     * debug等级的日志输出
     *
     * @param tag
     *            标识
     * @param msg
     *            内容
     * @return void 返回类型
     * @throws
     */
    public static void d(String tag, String msg) {
        if (isDeBug) {
            Log.d(tag, msg);
        }
    }

    /**
     * info等级的日志输出
     *
     * @param  tag 标识
     * @param  msg 内容
     * @return void 返回类型
     * @throws
     */
    public static void i(String tag, String msg) {
        if (isDeBug) {
            Log.i(tag, msg);
        }
    }

    /**
     * warn等级的日志输出
     *
     * @param tag 标识
     * @param msg 内容
     * @return void 返回类型
     * @throws
     */
    public static void w(String tag, String msg) {
        if (isDeBug) {
            Log.w(tag, msg);
        }
    }

    /**
     * error等级的日志输出
     *
     * @param  tag 标识
     * @param  msg 内容
     * @return void 返回类型
     */
    public static void e(String tag, String msg) {
        if (isDeBug) {
            Log.w(tag, msg);
        }
    }

    /**
     * 获取日志文件
     * @return
     */
    public static String getLogPath() {
        String path = "";
        // 获取扩展SD卡设备状
        String sDStateString = android.os.Environment.getExternalStorageState();
        // 拥有可读可写权限
        if (sDStateString.equals(android.os.Environment.MEDIA_MOUNTED)) {
            // 获取扩展存储设备的文件目录
            File SDFile = android.os.Environment.getExternalStorageDirectory();
            path = SDFile.getAbsolutePath() + File.separator + MYLOG_PATH_SDCARD_DIR;
        } else {
            //获取U盘地址
            ArrayList<String> usbPathList = FileUtils.getExternalStorageDirectory();
            if (usbPathList.size() > 0) {
                path = usbPathList.get(0) + "/" + MYLOG_PATH_SDCARD_DIR;
            }
        }
        return path;
    }


    /**
     * 打开日志文件并写入日志
     *
     * @return
     * **/
    public static void writeLogtoFile(String tag, String text) {
        Date nowtime = new Date();
        String needWriteFiel = logfile.format(nowtime);
        String needWriteMessage = myLogSdf.format(nowtime) + "    " + "    " + tag + "    " + text;

        // 取得日志存放目录
        String path = getLogPath();
        if (path != null && !"".equals(path)) {
            try {
                // 创建目录
                File dir = new File(path);
                if (!dir.exists())
                    dir.mkdir();
                // 打开文件
                File file = new File(path + File.separator + needWriteFiel + MYLOGFILEName);
                FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
                BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                bufWriter.write(needWriteMessage);
                bufWriter.newLine();
                bufWriter.close();
                filerWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除制定的日志文件
     * */
    public static void delFile() {// 删除日志文件
        try {
            // 取得日志存放目录
            String path = getLogPath();
            if (path != null && !"".equals(path)) {
                getFileInfoList(path, new OnLoadFileListener<ArrayList<FileEntity>>() {
                    @Override
                    public void onLoadFile(ArrayList<FileEntity> fileList) {
                        LogUtils.writeLogtoFile("删除10天前日志", "删除10天前日志");
                        //获取开始日期
                        String startDate = logfile.format(getDateBefore());
                        //获取当前日期
                        Date nowtime = new Date();
                        String currDate = logfile.format(nowtime);
                        //获取两个日期之间所有的天数
                        ArrayList<String> dateList = findDates(startDate, currDate);
                        for (int i = 0; i < fileList.size(); i++) {
                            FileEntity fileEntity = fileList.get(i);
                            //如果不存在10天的日期中
                            if (!isHas(fileEntity.getFileName(), dateList)) {
                                File file = new File(fileEntity.getFilePath());
                                if (file.exists()) {
                                    file.delete();
                                }
                            }
                        }
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 得到现在时间前的几天日期，用来得到需要删除的日志文件
     * */
    public static Date getDateBefore() {
        Date nowtime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(nowtime);
        now.set(Calendar.DATE, now.get(Calendar.DATE)
                - SDCARD_LOG_FILE_SAVE_DAYS);
        return now.getTime();
    }


    /**
     * 获取时间段的所有日期时间
     * @param startDate 开始日期,格式如：yyyy-MM-dd
     * @param endDate 结束日期,格式如：yyyy-MM-dd
     * @return
     */
    public static ArrayList<String> findDates(String startDate, String endDate) {
        ArrayList<String> dateList = new ArrayList<String>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //添加开始日期对象
            dateList.add(startDate);
            Date dBegin = sdf.parse(startDate);// 开始date
            Date dEnd = sdf.parse(endDate);// 结束date
            Calendar calBegin = Calendar.getInstance();
            // 使用给定的 Date 设置此 Calendar 的时间
            calBegin.setTime(dBegin);
            Calendar calEnd = Calendar.getInstance();
            // 使用给定的 Date 设置此 Calendar 的时间
            calEnd.setTime(dEnd);
            // 测试此日期是否在指定日期之后
            while (dEnd.after(calBegin.getTime())) {
                // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
                calBegin.add(Calendar.DAY_OF_MONTH, 1);
                //添加开始日期对象
                dateList.add(sdf.format(calBegin.getTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return dateList;
    }


    /**
     * 获取指定路径中的文件名、大小和路径
     * @param list 装扫描出来的视频文件实体类
     * @param file 指定的文件
     */
    public static void getFilesInfo(final List<FileEntity> list, File file) {

        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                try {
                    if (file.isDirectory()) {
                        getFilesInfo(list, file);
                    } else {
                        String name = file.getName();
                        FileEntity fileEntity = new FileEntity();
                        //设置文件名称
                        fileEntity.setFileName(name);
                        //设置文件地址
                        fileEntity.setFilePath(file.getAbsolutePath());
                        list.add(fileEntity);
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });
    }

    /**
     * 获取某个目录下所有文件信息
     * @param path
     * @return
     */
    public static ArrayList<FileEntity> getFileInfo(String path) {
        //获得外部存储的根目录
        File dir = new File(path);
        ArrayList<FileEntity> list = new ArrayList<FileEntity>();
        //调用遍历所有文件的方法
        if (dir.exists()) {
            getFilesInfo(list, dir);
        }
        return list;
    }

    /**
     * 获取所有的日期文件
     * @param filePath 日志路径
     * @param listener
     */
    public static void getFileInfoList(final String filePath, final OnLoadFileListener<ArrayList<FileEntity>> listener) {

        new AsyncTask<Object, Object, ArrayList<FileEntity>>() {
            @Override
            protected ArrayList<FileEntity> doInBackground(Object... params) {
                return getFileInfo(filePath);
            }

            @Override
            protected void onPostExecute(ArrayList<FileEntity> fileEntities) {
                //返回
                listener.onLoadFile(fileEntities);
            }
        }.execute();
    }

    /**
     * 判断是否在10天中
     * @param fileName
     * @param dateList
     * @return false不在, true在
     */
    public static boolean isHas(String fileName, ArrayList<String> dateList) {
        boolean isHas = false;
        for (int i = 0; i < dateList.size(); i++) {
            if (fileName != null && fileName.startsWith(dateList.get(i))) {
                isHas = true;
                break;
            }
        }

        return isHas;
    }


}
