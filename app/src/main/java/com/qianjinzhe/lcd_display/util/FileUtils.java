package com.qianjinzhe.lcd_display.util;

import android.os.AsyncTask;
import android.util.Log;

import com.qianjinzhe.lcd_display.bean.MediaEntity;
import com.qianjinzhe.lcd_display.listener.OnLoadFileListener;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              FileUtils.java
 * Description：
 *              文件工具类
 * Author:
 *              youngHu
 * Finished：
 *              2018年05月31日
 ********************************************************/

public class FileUtils {

    /**
     * 删除文件
     * @param file
     */
    public static void deleteFile(File file) {
        if (file == null) {
            return;
        }
        if (file.exists()) { //判断文件是否存在
            if (file.isFile()) { //判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        } else {
            Log.d("FileUtils", "文件不存在！" + "\n");
        }
    }

    /**
     * 获取指定路径中的视频文件
     * @param list 装扫描出来的视频文件实体类
     * @param file 指定的文件
     */
    public static void getVideoFile(final List<MediaEntity> list, File file) {// 获得视频文件
        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String name = file.getName();
                MediaEntity mediaEntity = new MediaEntity();
                //设置文件名称
                mediaEntity.setMediaFileName(name);
                //设置文件地址
                mediaEntity.setMediaFilePath(file.getAbsolutePath());
                int i = name.lastIndexOf('.');
                if (i != -1) {
                    name = name.substring(i);//获取文件后缀名
                    //如果是获取图片
                    if (name.equalsIgnoreCase(".jpg")
                            || name.equalsIgnoreCase(".png")
                            || name.equalsIgnoreCase(".bmp")
                            || name.equalsIgnoreCase(".jpeg")
//                            || name.equalsIgnoreCase(".gif")//暂时当作视频处理
                            || name.equalsIgnoreCase(".tif")) {
                        //设置文件类型为图片
                        mediaEntity.setMediaType(Constants.MEDIA_PLAY_TYPE_IMAGE);
                        //添加文件地址
                        list.add(mediaEntity);
                        return true;
                    }
                    //如果是获取视频
                    else if (name.equalsIgnoreCase(".mp4")
                            || name.equalsIgnoreCase(".avi")
                            || name.equalsIgnoreCase(".rm")
                            || name.equalsIgnoreCase(".mov")
                            || name.equalsIgnoreCase(".mpg")
                            || name.equalsIgnoreCase(".rmvb")
                            || name.equalsIgnoreCase(".wmv")
                            || name.equalsIgnoreCase(".mkv")
                            || name.equalsIgnoreCase(".gif")
                            || name.equalsIgnoreCase(".3gp")
                            || name.equalsIgnoreCase(".flv")
                            || name.equalsIgnoreCase(".vob")) {
                        //设置文件类型为视频文件
                        mediaEntity.setMediaType(Constants.MEDIA_PLAY_TYPE_VIDEO);
                        //添加文件地址
                        list.add(mediaEntity);
                        return true;
                    }


                } else if (file.isDirectory()) {
                    getVideoFile(list, file);
                }
                return false;
            }
        });
    }

    /**
     * 获取所有的多媒体文件并存入集合中
     * @return
     */
    public static ArrayList<MediaEntity> getMediaFileList(String path) {
        //获得外部存储的根目录
        File dir = new File(path);
        ArrayList<MediaEntity> list = new ArrayList<MediaEntity>();
        //调用遍历所有文件的方法
        if (dir.exists()) {
            getVideoFile(list, dir);
        }

        //首字母为中文的文件集合
        ArrayList<MediaEntity> chineseList = new ArrayList<MediaEntity>();
        //首字母不为中文的文件集合
        ArrayList<MediaEntity> unChineseList = new ArrayList<MediaEntity>();
        //筛选文件
        for (MediaEntity entity : list) {
            //如果第一个字符是中文
            if (AppUtils.isIIIegalCharacter(entity.getMediaFileName().charAt(0) + "")) {
                chineseList.add(entity);
            }
            //如果是中文以外的字符
            else {
                unChineseList.add(entity);
            }
        }

        //对中文排序,Eclipse中只要下面中文排序就全部ok了,但是Studio不行，不知道为什么
        if (chineseList.size() > 1) {
            Collections.sort(chineseList, new Comparator<MediaEntity>() {
                @Override
                public int compare(MediaEntity o1, MediaEntity o2) {
                    //正序
                    String s1 = o1.getMediaFileName();
                    String s2 = o2.getMediaFileName();
                    return Collator.getInstance(Locale.CHINESE).compare(s1, s2);
                }
            });
        }

        //对中文以外的文件名排序
        if (unChineseList.size() > 1) {
            Collections.sort(unChineseList, new Comparator<MediaEntity>() {
                @Override
                public int compare(MediaEntity o1, MediaEntity o2) {
                    //正序
                    String s1 = o1.getMediaFileName();
                    String s2 = o2.getMediaFileName();
                    return s1.compareTo(s2);
                }
            });
        }

        //合并集合
        ArrayList<MediaEntity> resultList = new ArrayList<>();
        resultList.addAll(unChineseList);
        resultList.addAll(chineseList);

        //返回文件路径集合
        return resultList;
    }


    /**
     *  获取多媒体文件
     * @param mediaDirPath 多媒体目录
     */
    public static void getMediaEntityList(final String mediaDirPath, final OnLoadFileListener listener) {
        new AsyncTask<Object, Object, ArrayList<MediaEntity>>() {

            @Override
            protected ArrayList<MediaEntity> doInBackground(Object... params) {
                //获取多媒体文件集合
                return FileUtils.getMediaFileList(mediaDirPath);
            }

            @Override
            protected void onPostExecute(ArrayList<MediaEntity> mediaEntities) {
                if (mediaEntities != null) {
                    listener.onLoadFile(mediaEntities);
                }
            }

        }.execute();
    }


    /**
     * 获取某个目录下文件的个数
     * @param dirPath 目录
     * @return
     */
    public static int getFileNum(String dirPath) {
        int num = 0;
        try {
            File file = new File(dirPath);
            if (file.exists()) {
                File[] files = file.listFiles();
                num = files.length;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return num;
    }


    /**
     * 获取指定路径中的apk文件
     * @param filePath 指定的文件路径
     */
    public static String getApkFile(String filePath) {
        final List<String> list = new ArrayList<>();
        String apkPath = "";
        File file = new File(filePath);
        if (file.exists()) {
            file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    String name = file.getName();
                    //设置文件地址
                    int i = name.lastIndexOf('.');
                    if (i != -1) {
                        name = name.substring(i);//获取文件后缀名
                        if (name.equalsIgnoreCase(".apk")) {
                            //添加文件地址
                            list.add(file.getAbsolutePath());
                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        if (list.size() > 0) {
            apkPath = list.get(0);
        }

        return apkPath;
    }


    /**
     * 获取指定路径中的apk文件
     * @param filePath 指定的文件路径
     */
    public static void delApkFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        String name = file.getName();
                        //设置文件地址
                        int i = name.lastIndexOf('.');
                        if (i != -1) {
                            name = name.substring(i);//获取文件后缀名
                            if (name.equalsIgnoreCase(".apk")) {
                                file.delete();
                                return true;
                            }
                        }
                        return false;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断是否是视频文件
     * @param filePath
     * @return true是视频
     */
    public static boolean isVideoFile(String filePath) {
        boolean isVideo = false;
        if (filePath.endsWith(".avi")
                || filePath.endsWith(".3gp")
                || filePath.endsWith(".flv")
                || filePath.endsWith(".mp4")
                || filePath.endsWith(".rm")
                || filePath.endsWith(".mov")
                || filePath.endsWith(".mpg")
                || filePath.endsWith(".rmvb")
                || filePath.endsWith(".wmv")
                || filePath.endsWith(".mkv")
                || filePath.endsWith(".vob")) {
            isVideo = true;
        }
        return isVideo;
    }


    /**
     * 判断是否是图片
     * @param filePath
     * @return true是图片
     */
    public static boolean isPicFile(String filePath) {
        boolean isPic = false;
        if (filePath.endsWith(".jpg")
                || filePath.endsWith(".png")
                || filePath.endsWith(".jpeg")
                || filePath.endsWith(".gif")
                || filePath.endsWith(".tip")
                || filePath.endsWith(".bmp")) {
            isPic = true;
        }
        return isPic;
    }


    /**
     * 根据byte数组生成文件
     * @param bytes 生成文件用到的byte数组
     */
    public static boolean createFileWithByte(byte[] bytes, String filePathAndName) {
        boolean isSuccess = false;
        // 创建FileOutputStream对象
        FileOutputStream outputStream = null;
        // 创建BufferedOutputStream对象
        BufferedOutputStream bufferedOutputStream = null;
        try {
            //新建一个File对象，把我们要建的文件路径传进去。
            File file = new File(filePathAndName);
            //判断文件是否存在，如果存在就删除。
            if (file.exists()) {
                file.delete();
            }
            // 在文件系统中根据路径创建一个新的空文件
            file.createNewFile();
            // 获取FileOutputStream对象
            outputStream = new FileOutputStream(file);
            // 获取BufferedOutputStream对象
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            // 往文件所在的缓冲输出流中写byte数据
            bufferedOutputStream.write(bytes);
            // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
            bufferedOutputStream.flush();
            isSuccess = true;
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
            isSuccess = false;
        } finally {
            // 关闭创建的流对象
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }

        return isSuccess;
    }

    /**
     *  读取指定文件中的数据，将数据读取为byte[]类型
     *  参数：要读取数据的文件路径
     */
    public static long getFileLength(String path) {
        long size = 0;
        try {
            try {
                File file = new File(path);
                if (file.exists()) {
                    size = file.length();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
        return size;
    }

    /**
     * 检查是否存在未下载完整的文件
     * @param mediaEntities
     * @param mapFiles
     * @return
     */
    public static boolean isHasDownErrorFile(ArrayList<MediaEntity> mediaEntities, Map<String, Integer> mapFiles) {

        boolean ishas = false;

        try {
            for (int i = 0; i < mediaEntities.size(); i++) {
                //文件名称
                String fileName = mediaEntities.get(i).getMediaFileName();
                //文件大小
                long fileLength = mediaEntities.get(i).getFileSize();
                LogUtils.d("文件大小比对", "文件名称:" + fileName + ",下载后的文件大小=" + fileLength + "，文件的本来大小==" + mapFiles.get(fileName));
                if (null != mapFiles.get(fileName) && fileLength != mapFiles.get(fileName)) {
                    ishas = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
        return ishas;
    }


    /**
     * 获取指定路径中的文件名、大小和路径
     * @param list 装扫描出来的视频文件实体类
     * @param file 指定的文件
     */
    public static void getFilesInfo(final List<MediaEntity> list, File file) {

        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                try {
                    if (file.isDirectory()) {
                        getFilesInfo(list, file);
                    } else {
                        String name = file.getName();
                        MediaEntity mediaEntity = new MediaEntity();
                        //设置文件名称
                        mediaEntity.setMediaFileName(name);
                        //设置文件地址
                        mediaEntity.setMediaFilePath(file.getAbsolutePath());
                        //设置文件大小
                        mediaEntity.setFileSize(file.length());
                        //添加到集合
                        list.add(mediaEntity);
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
    public static ArrayList<MediaEntity> getFileInfo(String path) {
        //获得外部存储的根目录
        File dir = new File(path);
        ArrayList<MediaEntity> list = new ArrayList<MediaEntity>();
        //调用遍历所有文件的方法
        if (dir.exists()) {
            getFilesInfo(list, dir);
        }
        return list;
    }

    /**
     *  获取某个目录下的所有文件信息
     * @param mediaDirPath 多媒体目录
     */
    public static void getFileInfoList(final String mediaDirPath, final String htmlFilePath, final OnLoadFileListener<Map<String, ArrayList<MediaEntity>>> listener) {
        new AsyncTask<Object, Object, Map<String, ArrayList<MediaEntity>>>() {

            @Override
            protected Map<String, ArrayList<MediaEntity>> doInBackground(Object... params) {
                Map<String, ArrayList<MediaEntity>> map = new HashMap<String, ArrayList<MediaEntity>>();
                //获取多媒体的文件
                map.put(Constants.KEY_MEDIA, FileUtils.getFileInfo(mediaDirPath));
                //获取html相关文件
                map.put(Constants.KEY_HTML_FILE, FileUtils.getFileInfo(htmlFilePath));
                //获取多媒体文件集合
                return map;
            }

            @Override
            protected void onPostExecute(Map<String, ArrayList<MediaEntity>> map) {
                if (map != null) {
                    listener.onLoadFile(map);
                }
            }

        }.execute();
    }


    /**
     * 获取扩展存储路径，TF卡、U盘
     */
    public static ArrayList<String> getExternalStorageDirectory(){
        ArrayList<String> usbPathList = new ArrayList<String>();
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure")) continue;
                if (line.contains("asec")) continue;

                if (line.contains("fat")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        usbPathList.add(columns[1]);
                    }
                } else if (line.contains("fuse")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        usbPathList.add(columns[1]);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return usbPathList;
    }



}
