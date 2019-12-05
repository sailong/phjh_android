package com.qianjinzhe.lcd_display.util;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qianjinzhe.lcd_display.bean.AppSettingEntity;
import com.qianjinzhe.lcd_display.bean.CallInfoEntity;
import com.qianjinzhe.lcd_display.bean.CallInfoListEntity;
import com.qianjinzhe.lcd_display.bean.CounterEntity;
import com.qianjinzhe.lcd_display.bean.CounterSettingEntity;
import com.qianjinzhe.lcd_display.bean.HistoryShowEntity;
import com.qianjinzhe.lcd_display.bean.TemplateEntity;
import com.qianjinzhe.lcd_display.listener.DialogListener;
import com.qianjinzhe.lcd_display.manager.AppManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              AppUtils.java
 * Description：
 *              app工具类
 * Author:
 *              youngHu
 * Finished：
 *              2018年05月31日
 ********************************************************/

public class AppUtils {

    /**
     * 获取应用程序版本名称信息
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 判断是否是中文
     * @param str
     * @return true表示是中文
     */
    public static boolean isIIIegalCharacter(String str) {
        Pattern p = Pattern.compile("[\\u4e00-\\u9fa5)]+");
        Matcher m = p.matcher(str);
        return m.matches();
    }


    /**
     * 设置语言
     * @param mContext 上下文
     * @param languageCode 语言code
     */
    public static void setLanguage(Context mContext, String languageCode) {

        Resources resources = mContext.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        //如果是简体中文
        if (Constants.LANGUAGE_CN.equals(languageCode)) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
            resources.updateConfiguration(config, dm);
        }
        //如果是繁体中文
        else if (Constants.LANGUAGE_TW.equals(languageCode)) {
            config.locale = Locale.TAIWAN;
            resources.updateConfiguration(config, dm);
        }
        //如果是英文
        else if (Constants.LANGUAGE_EN.equals(languageCode)) {
            config.locale = Locale.UK;
            resources.updateConfiguration(config, dm);
        }
    }


    /**
     * 获取当前小时和分钟数据
     * @return
     */
    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    /**
     * 由日期字符串获取毫秒数
     *
     * @param dateAndTimeStr 日期字符串 如:2015-11-10
     * @param formatStr      时间的格式 如:yyyy-MM-dd
     * @return
     */
    public static long dateAndTimeStrToTimeMillis(String dateAndTimeStr, String formatStr) {
        long mill = 0;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
            ParsePosition pos = new ParsePosition(0);
            Date strtodate = formatter.parse(dateAndTimeStr, pos);
            mill = strtodate.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mill;
    }


    /**
     * 隐藏虚拟按键
     */
    public static void hideBottomUIMenu(Activity activity) {
        if (hasNavBar(activity)) {
            //隐藏虚拟按键，并且全屏
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) { // lower api
                View v = activity.getWindow().getDecorView();
                v.setSystemUiVisibility(View.GONE);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window mWindow = activity.getWindow();
                WindowManager.LayoutParams params = mWindow.getAttributes();
                params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN;
                mWindow.setAttributes(params);
                //设置状态栏透明
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
        }
    }


    /**
     * 检查是否存在虚拟按键栏
     * @param context
     * @return
     */
    public static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 判断虚拟按键栏是否重写
     * @return
     */
    public static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }


    /**
     * bitmap缩放
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    /**
     * 屏幕解锁
     * @param mContext 上下文
     */
    public static void screenUnlock(Context mContext) {
        //开机后一般会停留在锁屏页面且短时间内没有进行解锁操作屏幕会进入休眠状态，此时就需要先唤醒屏幕和解锁屏幕
        //屏幕唤醒
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        //最后的参数是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "BootReceiver");
        wl.acquire();
        //屏幕解锁
        KeyguardManager km = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("BootReceiver");//参数是LogCat里用的Tag
        kl.disableKeyguard();
    }

    /**
     * 退出app
     */
    public static void exitApp() {
        AppManager.getInstance().closeAllActivity();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }


    /**
     * 判断是否为平板
     *
     * @return true为pad
     */
    public static boolean isPad(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        // 屏幕宽度
        float screenWidth = display.getWidth();
        // 屏幕高度
        float screenHeight = display.getHeight();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        // 屏幕尺寸
        double screenInches = Math.sqrt(x + y);
        // 大于6尺寸则为Pad
        if (screenInches >= 6.0) {
            return true;
        }
        return false;
    }

    /**
     * 判断手机是否ROOT,true已root
     */
    public static boolean isRoot() {
        Process process = null;
        DataOutputStream os = null;
        boolean root = false;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                root = true;
            } else {
                root = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            root = false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return root;
    }

    /**
     * 从assets中读取txt
     * @param context
     * @param textName text文件名称包含后缀名
     * @return
     */
    public static String readFromAssets(Context context, String textName) {
        String text = "";
        try {
            InputStream is = context.getAssets().open(textName);
            text = readTextFromSDcard(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return text;
    }

    /**
     * 从raw中读取txt
     * @param context
     * @param mTxt 文件id,如R.raw.qq
     * @return
     */
    public static String readFromRaw(Context context, int mTxt) {
        String text = "";
        try {
            InputStream is = context.getResources().openRawResource(mTxt);
            text = readTextFromSDcard(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return text;
    }

    /**
     * 按行读取txt
     * @param is
     * @throws Exception
     */
    public static String readTextFromSDcard(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer("");
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
            buffer.append("\n");
        }
        return buffer.toString();
    }


    /**
     * 读取指定目录下的所有TXT文件的文件内容
     * @param path 文件路径
     * @return
     */
    public static String readTextFromSdcard(String path) {
        String content = "";
        try {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                //文件格式为txt文件
                if (file.getName().endsWith(".txt")) {
                    InputStream instream = new FileInputStream(file);
                    if (instream != null) {
                        content = readTextFromSDcard(instream);
                        //关闭输入流
                        instream.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 获取配置文件
     */
    public static AppSettingEntity getAppSettingEntity(String configPath) {
        File file = new File(configPath);
        if (!file.exists()) {
            return null;
        }
        AppSettingEntity settingEntity = new AppSettingEntity();
        try {
            //获取本地流
//            InputStream is = mContext.getAssets().open("Setting.xml");
            FileInputStream is = new FileInputStream(configPath);
            //Document解析xml
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(is);
            document.normalize();
            if (document.getElementsByTagName("AppSetting") != null && document.getElementsByTagName("AppSetting").getLength() > 0) {
                //最外层节点列表
                Element element = (Element) document.getElementsByTagName("AppSetting").item(0);

                //版本节点
                if (element.getElementsByTagName("Version") != null
                        && element.getElementsByTagName("Version").getLength() > 0) {
                    Element versionelement = (Element) element.getElementsByTagName("Version").item(0);
                    //获取版本号
                    String version = versionelement.getFirstChild().getNodeValue();
                    //设置版本号
                    settingEntity.setVersion(version);
                }

                //自动关机标签
                if (element.getElementsByTagName("AutoShutdown") != null
                        && element.getElementsByTagName("AutoShutdown").getLength() > 0) {
                    Element autoshutdown_ement = (Element) element.getElementsByTagName("AutoShutdown").item(0);
                    //获取是否强制关机
                    String autoshutdown_enable = autoshutdown_ement.getAttribute("Enable");
                    settingEntity.setAutoShutdownEnable(autoshutdown_enable);
                    //获取关机时间
                    String time = autoshutdown_ement.getAttribute("Time");
                    settingEntity.setAutoShutdownTime(time);
                }

                //退出密码节点
                if (element.getElementsByTagName("QuitPassword") != null
                        && element.getElementsByTagName("QuitPassword").getLength() > 0) {
                    Element quitpassword_ement = (Element) element.getElementsByTagName("QuitPassword").item(0);
                    //获取是否需要退出密码
                    String quitpassword_enable = quitpassword_ement.getAttribute("Enable");
                    settingEntity.setQuitPasswordEnable(quitpassword_enable);
                    //获取退出密码
                    String password = quitpassword_ement.getAttribute("Password");
                    settingEntity.setQuitPassword(password);
                }

                //窗口集合节点
                if (element.getElementsByTagName("CounterSetting") != null
                        && element.getElementsByTagName("CounterSetting").getLength() > 0) {
                    Element counter_set_element = (Element) element.getElementsByTagName("CounterSetting").item(0);
                    //获取窗口节点集合
                    NodeList counterNodeList = counter_set_element.getElementsByTagName("Counter");
                    if (counterNodeList != null) {
                        //窗口设置集合
                        ArrayList<CounterSettingEntity> counterSettingList = new ArrayList<CounterSettingEntity>();
                        for (int i = 0; i < counterNodeList.getLength(); i++) {
                            CounterSettingEntity counterSettingEntity = new CounterSettingEntity();
                            Element counter_element = (Element) counterNodeList.item(i);
                            //窗口号
                            String counterNo = counter_element.getAttribute("CounterNo");
                            counterSettingEntity.setCounterNo(counterNo);
                            //模板id
                            String templateId = counter_element.getAttribute("TemplateID");
                            counterSettingEntity.setTemplateId(templateId);
                            if (!"Default".equalsIgnoreCase(counterNo)) {
                                //标题
                                String title = counter_element.getAttribute("Title");
                                counterSettingEntity.setTitle(title);
                                //业务名称
                                String serviceName = counter_element.getAttribute("ServiceName");
                                counterSettingEntity.setServiceName(serviceName);
                                //窗口别名
                                String counterAlias = counter_element.getAttribute("CounterAlias");
                                counterSettingEntity.setCounterAlias(counterAlias);
                                //设置提示文字
                                String tipText = counter_element.getAttribute("TipText");
                                counterSettingEntity.setTipText(tipText);
                            }
                            //添加窗口到窗口集合
                            counterSettingList.add(counterSettingEntity);
                        }
                        //设置窗口集合
                        settingEntity.setCountersList(counterSettingList);
                    }
                }

                //模板集合节点
                if (element.getElementsByTagName("Templates") != null
                        && element.getElementsByTagName("Templates").getLength() > 0) {
                    Element template_list_element = (Element) element.getElementsByTagName("Templates").item(0);
                    //获取模板节点
                    NodeList template_nodeList = template_list_element.getElementsByTagName("Template");
                    //模板集合
                    ArrayList<TemplateEntity> templateList = new ArrayList<TemplateEntity>();
                    if (template_nodeList != null) {
                        for (int i = 0; i < template_nodeList.getLength(); i++) {
                            //模板对象
                            TemplateEntity templateEntity = new TemplateEntity();
                            //模板节点
                            Element template_element = (Element) template_nodeList.item(i);
                            //模板id
                            String templateId = template_element.getAttribute("ID");
                            templateEntity.setTemplateId(templateId);
                            //模板描述
                            String description = template_element.getAttribute("Description");
                            templateEntity.setTemplateDescription(description);

                            //模板叫号是否弹窗
                            if (template_element.getElementsByTagName("CallPopupWindow") != null
                                    && template_element.getElementsByTagName("CallPopupWindow").getLength() > 0) {
                                Element callPopupWindow_element = (Element) template_element.getElementsByTagName("CallPopupWindow").item(0);
                                //是否开启叫号弹窗
                                String callPopupWindow_enable = callPopupWindow_element.getAttribute("Enable");
                                templateEntity.setCallPopupWindow_enable(callPopupWindow_enable);
                                //弹窗时间
                                if (!TextUtils.isEmpty(callPopupWindow_element.getAttribute("Duration"))) {
                                    int callPopupWindow_duration = Integer.parseInt(callPopupWindow_element.getAttribute("Duration"));
                                    templateEntity.setCallPopupWindow_duration(callPopupWindow_duration);
                                }
                                //弹窗模板
                                String callPopupWindow_text = callPopupWindow_element.getAttribute("Text");
                                templateEntity.setCallPopupWindow_text(callPopupWindow_text);
                            }

                            //模板分辨率节点
                            if (template_element.getElementsByTagName("Resolution") != null
                                    && template_element.getElementsByTagName("Resolution").getLength() > 0) {
                                Element resolution_element = (Element) template_element.getElementsByTagName("Resolution").item(0);
                                //宽度
                                if (!TextUtils.isEmpty(resolution_element.getAttribute("Width"))) {
                                    int width = Integer.parseInt(resolution_element.getAttribute("Width"));
                                    templateEntity.setWidth(width);
                                }
                                //高度
                                if (!TextUtils.isEmpty(resolution_element.getAttribute("Height"))) {
                                    int height = Integer.parseInt(resolution_element.getAttribute("Height"));
                                    templateEntity.setHeight(height);
                                }
                            }

                            //模板背景节点
                            if (template_element.getElementsByTagName("Background") != null
                                    && template_element.getElementsByTagName("Background").getLength() > 0) {
                                Element temp_bg_element = (Element) template_element.getElementsByTagName("Background").item(0);
                                //模板背景类型
                                String bg_type = temp_bg_element.getAttribute("type");
                                templateEntity.setBackgroundType(bg_type);
                                //模板背景图片名称
                                String bg_name = temp_bg_element.getAttribute("Name");
                                templateEntity.setBackgroundName(bg_name);
                                //模板背景颜色
                                String bgColor = temp_bg_element.getAttribute("Color");
                                templateEntity.setBgColor(bgColor);
                                //模板更新日期
                                String temp_bg_modifyDate = temp_bg_element.getAttribute("ModifyDate");
                                templateEntity.setBackgroundModifyDate(temp_bg_modifyDate);
                            }

                            //模板三html文件节点
                            if (template_element.getElementsByTagName("BgFileList") != null
                                    && template_element.getElementsByTagName("BgFileList").getLength() > 0) {
                                Element filelist_element = (Element) template_element.getElementsByTagName("BgFileList").item(0);
                                //文件目录
                                String fileListDirectory = filelist_element.getAttribute("Directory");
                                templateEntity.setFileListDirectory(fileListDirectory);
                                //文件名称
                                String fileNames = filelist_element.getAttribute("Names");
                                templateEntity.setFileListNames(fileNames);
                                //更新时间
                                String fileListModifyDate = filelist_element.getAttribute("ModifyDate");
                                templateEntity.setFileListModifyDate(fileListModifyDate);
                                //文件数量
                                String countStr = filelist_element.getAttribute("Count");
                                if (!TextUtils.isEmpty(countStr)) {
                                    templateEntity.setFileListCount(Integer.parseInt(countStr));
                                }
                            }

                            //多媒体节点
                            if (template_element.getElementsByTagName("Media") != null
                                    && template_element.getElementsByTagName("Media").getLength() > 0) {
                                Element media_element = (Element) template_element.getElementsByTagName("Media").item(0);
                                //多媒体目录
                                String media_directory = media_element.getAttribute("Directory");
                                templateEntity.setMediaDirectory(media_directory);
                                //获取对媒体数量
                                if (!TextUtils.isEmpty(media_element.getAttribute("Count"))) {
                                    int media_count = Integer.parseInt(media_element.getAttribute("Count"));
                                    //设置视频数量
                                    templateEntity.setMediaCount(media_count);
                                }
                                //获取多媒体类型
                                String mediaType = media_element.getAttribute("type");
                                templateEntity.setMediaType(mediaType);
                                //获取视频更新日期
                                String media_modifyDate = media_element.getAttribute("ModifyDate");
                                //设置视频更新日期
                                templateEntity.setMediaModifyDate(media_modifyDate);
                                //获取并设置更新事件
                                String media_update_evenempty = media_element.getAttribute("UpdateEvenEmpty");
                                templateEntity.setMediaUpdateEvenEmpty(media_update_evenempty);
                                //多媒体开关
                                String media_switch = media_element.getAttribute("Switch");
                                templateEntity.setMediaSwitch(media_switch);
                                //轮播时间间隔
                                if (!TextUtils.isEmpty(media_element.getAttribute("Interval"))) {
                                    int media_interval = Integer.parseInt(media_element.getAttribute("Interval"));
                                    templateEntity.setMediaInterval(media_interval);
                                }
                                //是否使用动画
                                String media_animation = media_element.getAttribute("Animation");
                                templateEntity.setMediaAnimation(media_animation);
                            }

                            //叫号模板部分
                            if (template_element.getElementsByTagName("CallTextModel") != null
                                    && template_element.getElementsByTagName("CallTextModel").getLength() > 0) {
                                Element callTextModel_element = (Element) template_element.getElementsByTagName("CallTextModel").item(0);
                                //获取启用状态
                                String calltextModel_enable = callTextModel_element.getAttribute("Enable");
                                templateEntity.setCallTextModel_enable(calltextModel_enable);
                                //如果启用叫号模板
                                if ("1".equals(calltextModel_enable)) {
                                    //叫号模板文字
                                    String callText = callTextModel_element.getAttribute("Text");
                                    templateEntity.setCallTextModel_text(callText);
                                    //票号长度
                                    String ticketLenStr = callTextModel_element.getAttribute("TicketLen").trim();
                                    if (!TextUtils.isEmpty(ticketLenStr)) {
                                        templateEntity.setCallTextModel_ticketLen(Integer.parseInt(ticketLenStr));
                                    }
                                    //窗口号长度
                                    String counterLenStr = callTextModel_element.getAttribute("CounterLen").trim();
                                    if (!TextUtils.isEmpty(counterLenStr)) {
                                        templateEntity.setCallTextModel_counterLen(Integer.parseInt(counterLenStr));
                                    }
                                    //历史叫号显示方式
                                    String historyCallShowType = callTextModel_element.getAttribute("HistoryCallShow");
                                    templateEntity.setHistoryCallShowType(historyCallShowType);
                                }
                            }

                            //添加模板集合
                            templateList.add(templateEntity);
                        }
                    }
                    //设置模板集合
                    settingEntity.setTemplatesList(templateList);
                }
            }

            /**初始化的数据*/
            if (document.getElementsByTagName("InitData") != null && document.getElementsByTagName("InitData").getLength() > 0) {
                //最外层节点列表
                Element element = (Element) document.getElementsByTagName("InitData").item(0);

                //版本节点
                if (element.getElementsByTagName("NextCallList") != null
                        && element.getElementsByTagName("NextCallList").getLength() > 0) {
                    Element nextCallList_element = (Element) element.getElementsByTagName("NextCallList").item(0);
                    //最多等候数据的条数
                    int num = 1;
                    String maxNum = nextCallList_element.getAttribute("LimitPerService");
                    if (!TextUtils.isEmpty(maxNum)) {
                        num = Integer.parseInt(maxNum);
                    }

                    //业务节点集合
                    ArrayList<CallInfoListEntity> waitCallInfoList = new ArrayList<CallInfoListEntity>();
                    //获取业务节点
                    if (nextCallList_element.getElementsByTagName("Service") != null
                            && nextCallList_element.getElementsByTagName("Service").getLength() > 0) {
                        //获取业务节点集合
                        NodeList service_element_list = nextCallList_element.getElementsByTagName("Service");
                        for (int i = 0; i < service_element_list.getLength(); i++) {
                            //等待叫号实体
                            CallInfoListEntity callInfoListEntity = new CallInfoListEntity();
                            //设置最大等待的数量
                            callInfoListEntity.setLimitPerService(num);
                            //业务节点
                            Element service_element = (Element) service_element_list.item(i);
                            //获取排序序号
                            String serviceOrder = service_element.getAttribute("Order");
                            callInfoListEntity.setServiceOrder(serviceOrder);

                            //业务编号
                            String serviceNoStr = service_element.getAttribute("ServiceNo");
                            if (!TextUtils.isEmpty(serviceNoStr)) {
                                callInfoListEntity.setServiceNo(Integer.parseInt(serviceNoStr));
                            }

                            //业务类型
                            String serviceType = service_element.getAttribute("ServiceType");
                            callInfoListEntity.setServiceType(serviceType);

                            //业务名称(科室名称)
                            String serviceName = service_element.getAttribute("ServiceName");
                            callInfoListEntity.setDepartmentName(serviceName);

                            //等待叫号集合
                            ArrayList<CallInfoEntity> callInfoList = new ArrayList<CallInfoEntity>();
                            //等待叫号集合
                            if (service_element.getElementsByTagName("NextCall") != null
                                    && service_element.getElementsByTagName("NextCall").getLength() > 0) {
                                //获取等待叫号集合
                                NodeList call_element_list = service_element.getElementsByTagName("NextCall");
                                for (int j = 0; j < call_element_list.getLength(); j++) {
                                    //叫号实体
                                    CallInfoEntity callInfoEntity = new CallInfoEntity();
                                    Element call_element = (Element) call_element_list.item(j);
                                    //叫号序号
                                    String order = call_element.getAttribute("Order");
                                    callInfoEntity.setOrder(order);

                                    //票号
                                    String ticketNo = call_element.getAttribute("TicketNo");
                                    callInfoEntity.setTicketNo(ticketNo);

                                    //患者名称
                                    String name = call_element.getAttribute("Name");
                                    callInfoEntity.setName(name);
                                    //以下是隐藏名字中间的字
//                                    callInfoEntity.setName(formatName(name));

                                    //医生名称
                                    String doctorName = call_element.getAttribute("Doctor");
                                    callInfoEntity.setDoctorName(doctorName);

                                    //预约标识
                                    String reservation = call_element.getAttribute("Reservation");
                                    callInfoEntity.setReservation(reservation);

                                    callInfoList.add(callInfoEntity);
                                }
                            }
                            //设置叫号集合
                            callInfoListEntity.setHistoryCallList(callInfoList);
                            waitCallInfoList.add(callInfoListEntity);
                        }
                    }
                    //设置模板3(即医院模板)的等候初始化数据
                    settingEntity.setWaitCallInfoList(waitCallInfoList);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            settingEntity = null;
        }

        return settingEntity;
    }


    /**
     * 获取模板对象
     * @param settingEntity 配置文件对象
     * @param counterNo 窗口号
     * @return 返回模板对象
     */
    public static TemplateEntity getTemplateEntityFromCounter(AppSettingEntity settingEntity, String counterNo) {
        if (settingEntity == null) {
            return null;
        }
        //获取模板id
        String templateId = getTemplateId(settingEntity, counterNo);
        //获取模板集合
        ArrayList<TemplateEntity> list = settingEntity.getTemplatesList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                //获取模板对象
                TemplateEntity entity = list.get(i);
                //如果id相同
                if (templateId.equalsIgnoreCase(entity.getTemplateId())) {
                    return entity;
                }
            }
        }

        return null;
    }

    /**
     * 获取模板对象
     * @param settingEntity 配置文件对象
     * @param templateId  模板id
     * @return 返回模板对象
     */
    public static TemplateEntity getTemplateEntityFromTemplateId(AppSettingEntity settingEntity, String templateId) {
        if (settingEntity == null) {
            return null;
        }
        //获取模板集合
        ArrayList<TemplateEntity> list = settingEntity.getTemplatesList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                //获取模板对象
                TemplateEntity entity = list.get(i);
                //如果id相同
                if (templateId.equalsIgnoreCase(entity.getTemplateId())) {
                    return entity;
                }
            }
        }

        return null;
    }


    /**
     * 获取模板id
     * @param settingEntity 配置文件对象
     * @param counterNo 窗口号
     * @return 模板id
     */
    public static String getTemplateId(AppSettingEntity settingEntity, String counterNo) {
        //模板id
        String templateId = "";
        try {
            //获取窗口设置集合
            ArrayList<CounterSettingEntity> list = settingEntity.getCountersList();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    CounterSettingEntity entity = list.get(i);
                    if ("Default".equals(entity.getCounterNo())) {
                        templateId = entity.getTemplateId();
                    }
                    //如果窗口号相同
                    if (counterNo.equalsIgnoreCase(entity.getCounterNo())) {
                        templateId = entity.getTemplateId();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return templateId;
    }

    /**
     * 获取窗口对象
     * @param settingEntity 配置文件对象
     * @param counterNo 窗口号
     * @return 模板id
     */
    public static CounterSettingEntity getCounterSettingEntity(AppSettingEntity settingEntity, String counterNo) {
        //获取窗口设置集合
        CounterSettingEntity counterSettingEntity = null;
        try {
            ArrayList<CounterSettingEntity> list = settingEntity.getCountersList();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    CounterSettingEntity entity = list.get(i);
                    if ("Default".equals(entity.getCounterNo())) {
                        counterSettingEntity = entity;
                    }
                    //如果窗口号相同
                    if (counterNo.equalsIgnoreCase(entity.getCounterNo())) {
                        counterSettingEntity = entity;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return counterSettingEntity;
    }

    /**
     * 设置某个控件的宽高
     * @param view 控件
     * @param left 左边距
     * @param top 上边距
     * @param width 宽
     * @param height 高
     */
    public static void setViewWidthAndHeightAndMargin(final View view, int left, int top, int width, int height) {
        try {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
            params.width = width;
            params.height = height;
            params.setMargins(left, top, 0, 0);
            view.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 设置TextView边距和宽高
     * @param textView TextView
     * @param width 宽
     * @param height 高
     * @param left 左边距
     * @param top 上边距
     */
    public static void setTextViewMargin(final TextView textView, final int width, final int height, final int left, final int top) {
        try {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textView.getLayoutParams();
            params.width = width;
            params.height = height;
            params.setMargins(left, top, 0, 0);
            textView.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 字体路径包含名称和后缀
     * @param context 上下文
     * @param textView TextView
     * @param fontPath 字体
     */
    public static void setTextViewFont(Context context, TextView textView, String fontPath) {
        try {
            //获取隶书字体
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontPath);
            //设置公司名称和授权字体
            textView.setTypeface(typeface);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }


    /**
     * 设置LinearLayout的边距和宽高
     * @param relativeLayout
     */
    public static void setRelativeLayoutMarginWidHeight(final RelativeLayout relativeLayout, int left, int top, int width, int height) {
        try {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
            //如果是设置日期时间，不需要设置宽高
            params.width = width;
            params.height = height;
            params.setMargins(left, top, 0, 0);
            relativeLayout.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 获取更新字符串
     * @param entity_old 旧配置文件对象
     * @param entity 新的配置文件对象
     * @param templateId 模板id
     * @param bgName 背景名称
     * @param root_path 液晶显示根目录
     * @param mediaDirectory 多媒体文件路径(服务器的)
     * @param mediaDirPath   多媒体本地目录
     * @param configDirPath  配置文件目录
     * @return
     */
    public static String getUpdateString(AppSettingEntity entity_old,
                                         AppSettingEntity entity,
                                         String templateId,
                                         String bgName,
                                         String root_path,
                                         String mediaDirectory,
                                         String mediaDirPath,
                                         String configDirPath,
                                         boolean isChangeTemplateId) {
        //更新文件字符串
        String reqFiles = "";
        try {
            //是否清空媒体
            boolean isCleanMedia = false;
            //媒体数量
            int media_count = 0;
            //如果没有配置文件
            if (entity_old == null || entity == null) {
                reqFiles += "[All]";
            }
            //如果有配置文件
            else {
                //检查版本
                int curr_version = 0;
                int new_version = 0;
                //旧版本
                if (!TextUtils.isEmpty(entity_old.getVersion())) {
                    curr_version = Integer.parseInt(entity_old.getVersion().replace(".", ""));
                }
                //新版本
                if (!TextUtils.isEmpty(entity.getVersion())) {
                    new_version = Integer.parseInt(entity.getVersion().replace(".", ""));
                }

                //如果没有版本号
                if (TextUtils.isEmpty(entity.getVersion())) {
                    reqFiles += "[Update]";
                }
                //如果有高于当前版本号的
                else if (new_version > curr_version) {
                    reqFiles += "[Update]";
                }
                //获取之前旧模板对象
                TemplateEntity templateEntity_old = getTemplateEntityFromTemplateId(entity_old, templateId);
                //获取新模板对象
                TemplateEntity templateEntity_new = getTemplateEntityFromTemplateId(entity, templateId);
                //如果改变了窗口或ip或改变了模板id
                if (!SocketUtils.SERCIVER_IP.equals(SocketUtils.SERCIVER_IP_OLD) || isChangeTemplateId) {
                    //如果改变了服务器Ip
                    if (!SocketUtils.SERCIVER_IP.equals(SocketUtils.SERCIVER_IP_OLD)) {
                        reqFiles += "[Bg:" + bgName + "][BgFileList:" + templateEntity_new.getFileListDirectory() + "]";
                        //如果存在多媒体
                        if (Constants.MEDIA_HAS_TYPE_YES.equals(templateEntity_new.getMediaType())) {
                            reqFiles += "[Media:" + mediaDirectory + "]";
                        }
                        //清空当前所有的媒体
                        File file = new File(root_path + "/Config/" + templateEntity_new.getFileListDirectory());
                        FileUtils.deleteFile(file);
                    }
                    //如果改变了模板id
                    else if (isChangeTemplateId) {
                        reqFiles += "[Bg:" + bgName + "][BgFileList:" + templateEntity_new.getFileListDirectory() + "]";
                        //如果多媒体目录改变了,并且存在多媒体
                        if (!TextUtils.isEmpty(templateEntity_old.getMediaDirectory())
                                && !templateEntity_old.getMediaDirectory().equals(mediaDirectory)
                                && Constants.MEDIA_HAS_TYPE_YES.equals(templateEntity_new.getMediaType())) {
                            reqFiles += "[Media:" + mediaDirectory + "]";
                        }
                    }
                } else {
                    /**检查媒体*/
                    //获取媒体数量
                    media_count = templateEntity_new.getMediaCount();
                    //判断是否需要清空媒体
                    isCleanMedia = "1".equals(templateEntity_new.getMediaUpdateEvenEmpty()) ? true : false;
                    //如果背景更新时间为空
                    if (TextUtils.isEmpty(templateEntity_new.getBackgroundModifyDate())) {
                        reqFiles += "[Bg:" + bgName + "]";
                    }
                    //如果背景名称不相同
                    else if (!TextUtils.isEmpty(templateEntity_old.getBackgroundName())
                            && !TextUtils.isEmpty(templateEntity_new.getBackgroundName())
                            && !templateEntity_old.getBackgroundName().equals(templateEntity_new.getBackgroundName())) {
                        reqFiles += "[Bg:" + bgName + "]";
                    }
                    //如果之前的配置背景名称为空，新配置文件背景名称不为空
                    else if (TextUtils.isEmpty(templateEntity_old.getBackgroundName())
                            && !TextUtils.isEmpty(templateEntity_new.getBackgroundName())) {
                        reqFiles += "[Bg:" + bgName + "]";
                    } else {
                        //旧的背景更新时间
                        long time_old = 0;
                        if (!TextUtils.isEmpty(templateEntity_old.getBackgroundModifyDate())) {
                            time_old = dateAndTimeStrToTimeMillis(templateEntity_old.getBackgroundModifyDate(), "yyyy-MM-dd HH:mm:ss");
                        }
                        //新的背景更新时间
                        long time_new = dateAndTimeStrToTimeMillis(templateEntity_new.getBackgroundModifyDate(), "yyyy-MM-dd HH:mm:ss");
                        //如果更新时间大于之前的时间
                        if (time_new > time_old) {
                            reqFiles += "[Bg:" + bgName + "]";
                        }
                    }
                    //获取html相关为文件名称
                    String bgFileDirectory = templateEntity_new.getFileListDirectory();

                    //如果文件目录都不为空
                    if (!TextUtils.isEmpty(templateEntity_new.getFileListDirectory())
                            && !TextUtils.isEmpty(templateEntity_old.getFileListDirectory())) {
                        //如果文件目录改变了
                        if (!templateEntity_new.getFileListDirectory().equalsIgnoreCase(templateEntity_old.getFileListDirectory())) {
                            reqFiles += "[BgFileList:" + bgFileDirectory + "]";
                            //清空当前所有的html相关文件
                            File file = new File(root_path + "/Config/" + templateEntity_new.getFileListDirectory());
                            FileUtils.deleteFile(file);
                        }
                    }
                    //比对更新日期,如果日期都不为空
                    if (!TextUtils.isEmpty(templateEntity_old.getFileListModifyDate())
                            && !TextUtils.isEmpty(templateEntity_new.getFileListModifyDate())) {
                        //旧的背景更新时间
                        long time_old = 0;
                        if (!TextUtils.isEmpty(templateEntity_old.getFileListModifyDate())) {
                            time_old = dateAndTimeStrToTimeMillis(templateEntity_old.getFileListModifyDate(), "yyyy-MM-dd HH:mm:ss");
                        }
                        //新的背景更新时间
                        long time_new = dateAndTimeStrToTimeMillis(templateEntity_new.getFileListModifyDate(), "yyyy-MM-dd HH:mm:ss");
                        //如果更新时间大于之前的时间
                        if (time_new > time_old) {
                            reqFiles += "[BgFileList:" + bgFileDirectory + "]";
                            //清空当前所有的媒体
                            File file = new File(root_path + "/Config/" + templateEntity_new.getFileListDirectory());
                            FileUtils.deleteFile(file);
                        }
                    } else {
                        //否则重新请求html相关文件
                        reqFiles += "[BgFileList:" + bgFileDirectory + "]";
                        //清空当前所有的媒体
                        File file = new File(root_path + "/Config/" + templateEntity_new.getFileListDirectory());
                        FileUtils.deleteFile(file);
                    }

                    //如果媒体更新时间为空
                    if (TextUtils.isEmpty(templateEntity_new.getMediaModifyDate())) {
                        reqFiles += "[Media:" + mediaDirectory + "]";
                    }
                    //如果多媒体地址不一致
                    else if (!templateEntity_new.getMediaDirectory().equals(templateEntity_old.getMediaDirectory())) {
                        reqFiles += "[Media:" + mediaDirectory + "]";
                    } else {
                        //多媒体旧的更新时间
                        long time_old = 0;
                        if (!TextUtils.isEmpty(templateEntity_old.getMediaModifyDate())) {
                            time_old = dateAndTimeStrToTimeMillis(templateEntity_old.getMediaModifyDate(), "yyyy-MM-dd HH:mm:ss");
                        }

                        //多媒体最新更新时间
                        long time_new = dateAndTimeStrToTimeMillis(templateEntity_new.getMediaModifyDate(), "yyyy-MM-dd HH:mm:ss");
                        //如果更新时间大于之前的时间
                        if (time_new > time_old) {
                            reqFiles += "[Media:" + mediaDirectory + "]";
                        }
                    }
                }
            }

            //如果媒体数量大于0，并且媒体需要更新，或媒体数量为0且需要清空
            if (reqFiles.contains("[Media:") || (media_count == 0 && isCleanMedia)) {
                //清空当前所有的媒体
                File file = new File(mediaDirPath);
                FileUtils.deleteFile(file);
            }

            //如果背景需要更新，删除之前的背景
            if (reqFiles.contains("[Bg:")) {
                File file = new File(configDirPath + "/" + bgName);
                if (file.exists()) {
                    file.delete();
                }
            }

            //如果需要更新所有,删除之前的更新包
            if (reqFiles.contains("[All]") || reqFiles.contains("[Update]")) {
                //删除更新包
                deleteUpDatePackage(root_path);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return reqFiles;
    }

    /**
     * 删除更新包
     * @param root_path 液晶显示根目录
     */
    public static void deleteUpDatePackage(final String root_path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //删除zip文件
                    File file = new File(root_path + "/update.zip");
                    if (file.exists()) {
                        file.delete();
                    }
                    //删除评价器根目录下的apk文件
                    FileUtils.delApkFile(root_path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 格式化窗口号
     * @param counterNo 窗口号
     * @param counterNoLen 窗口号的长度
     * @return
     */
    public static String formatCounterNo(String counterNo, int counterNoLen) {
        StringBuffer buffer = new StringBuffer();
        if (counterNo.length() < counterNoLen) {
            int len = counterNoLen - counterNo.length();
            for (int i = 0; i < len; i++) {
                buffer.append("0");
            }
        }
        buffer.append(counterNo);
        return buffer.toString();
    }

    /**
     * 删除重复叫号信息
     * @param call_info_list 叫号信息集合
     * @param curr_ticket_num 当前票号
     */
    public static void delHasReCall(ArrayList<CallInfoEntity> call_info_list, String curr_ticket_num) {
        for (int i = 0; i < call_info_list.size(); i++) {
            //如果票号相同
            if (call_info_list.get(i).getTicketNo().equals(curr_ticket_num)) {
                call_info_list.remove(i);
                break;
            }
        }
    }

    /**
     * 检查重复叫号信息
     * @param call_info_list 叫号信息集合
     * @param curr_ticket_num 当前票号
     */
    public static Boolean checkHasReCall(ArrayList<CallInfoEntity> call_info_list, String curr_ticket_num) {
        for (int i = 0; i < call_info_list.size(); i++) {
            //如果票号相同
            if (call_info_list.get(i).getTicketNo().equals(curr_ticket_num)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 安装apk文件
     * @param context
     * @param file 安装文件
     * @param versionName 版本号
     */
    public static void installApkFile(final Context context, final File file, final String versionName) {
        try {
            DialogUtils.showInstallApkDialog(context, versionName, new DialogListener() {
                @Override
                public void onComplete() {
                    //安装
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    //兼容7.0
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
                        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    } else {
                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    if (context.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                        context.startActivity(intent);
                    }
                }

                @Override
                public void onFail() {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取业务名称（即科室名称）
     * @param hospitalWaitCallList
     * @param currTicketNo
     * @return
     */
    public static String getServiceName(ArrayList<CallInfoListEntity> hospitalWaitCallList, String currTicketNo) {
        String serviceName = "";
        try {
            if (hospitalWaitCallList != null) {
                for (int i = 0; i < hospitalWaitCallList.size(); i++) {
                    //获取科室叫号信息集合对象
                    CallInfoListEntity listEntity = hospitalWaitCallList.get(i);
                    //如果科室名称相同
                    if (!TextUtils.isEmpty(currTicketNo) && currTicketNo.startsWith(listEntity.getServiceType())) {
                        serviceName = listEntity.getDepartmentName();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return serviceName;
    }


    /**
     * 添加等候叫号集合
     * @param hospitalWaitCallList 医院模板历史叫号列表数据集合
     * @param waitCallList 每个科室下的等待叫号集合
     * @param currTicketNo 当前票号
     */
    public static void addWaitCall(ArrayList<CallInfoListEntity> hospitalWaitCallList, ArrayList<CallInfoEntity> waitCallList, String currTicketNo) {
        try {
            if (hospitalWaitCallList != null && waitCallList != null) {
                for (int i = 0; i < hospitalWaitCallList.size(); i++) {
                    //获取科室叫号信息集合对象
                    CallInfoListEntity listEntity = hospitalWaitCallList.get(i);
                    //历史叫号集合
                    ArrayList<CallInfoEntity> callInfoList = listEntity.getHistoryCallList();
                    //如果科室名称相同
                    if (!TextUtils.isEmpty(currTicketNo) && currTicketNo.startsWith(listEntity.getServiceType())) {
                        //清空叫号集合
                        callInfoList.clear();
                        //添加最新的叫号集合
                        callInfoList.addAll(waitCallList);
                        break;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取医院模板历史叫号列表对象集合
     * @param hospitalHistoryCallList 医院模板历史叫号列表数据集合
     * @return 返回历史叫号列表对象集合
     */
    public static ArrayList<HistoryShowEntity> getHistoryCallJsonString(ArrayList<CallInfoListEntity> hospitalHistoryCallList) {
        ArrayList<HistoryShowEntity> list = new ArrayList<HistoryShowEntity>();
        try {
            for (int i = 0; i < hospitalHistoryCallList.size(); i++) {
                CallInfoListEntity entity = hospitalHistoryCallList.get(i);
                //实例化历史叫号显示对象
                HistoryShowEntity showEntity = new HistoryShowEntity();
                //设置科室名称
                showEntity.setDepartmentName(entity.getDepartmentName());
                ArrayList<CallInfoEntity> callList = entity.getHistoryCallList();
                StringBuffer stringBuffer = new StringBuffer();
                if (callList != null && callList.size() > 0) {
                    for (int j = 0; j < callList.size(); j++) {
                        //获取票号
                        String ticketNo = callList.get(j).getTicketNo();
                        //名称
                        String name = callList.get(j).getName();
                        //拼接票号和姓名
                        stringBuffer.append(ticketNo + "|" + name);
                        if (j < callList.size() - 1) {
                            stringBuffer.append(",");
                        }
                    }
                    //设置历史叫号列表字符串
                    showEntity.setValue(stringBuffer.toString());
                } else {
                    showEntity.setValue("|");
                }
                list.add(showEntity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    /**
     * 获取分组后的json集合
     * @param hospitalHistoryCallList 医院模板历史叫号列表数据集合
     * @return 分组好的历史叫号json集合
     */
    public static ArrayList<String> getWaitCallJsonStrArr(ArrayList<CallInfoListEntity> hospitalHistoryCallList) {
        //分组后的json集合
        ArrayList<String> jsonList = new ArrayList<String>();
        try {
            //获取历史叫号列表对象集合
            ArrayList<HistoryShowEntity> list = getHistoryCallJsonString(hospitalHistoryCallList);
            if (list.size() > 7) {
                //每一组的集合
                ArrayList<HistoryShowEntity> itemJsonList = new ArrayList<HistoryShowEntity>();
                int n = 0;
                for (int i = 0; i < list.size(); i++) {
                    //添加到每一组的集合中
                    itemJsonList.add(list.get(i));
                    //如果能被7整除,分为一组
                    if (i != 0 && i % 7 == 6) {
                        jsonList.add(new Gson().toJson(itemJsonList));
                        itemJsonList.clear();
                        n++;
                    }
                }
                itemJsonList.clear();
                for (int i = n * 7; i < list.size(); i++) {
                    //添加到每一组的集合中
                    itemJsonList.add(list.get(i));
                    if (i == list.size() - 1) {
                        jsonList.add(new Gson().toJson(itemJsonList));
                    }
                }

            } else {
                jsonList.add(new Gson().toJson(list));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonList;
    }


    /**
     * 从过号中移除当前重复呼叫号
     * @param currCallInfoList 当前呼叫号集合
     * @param called_info_list 过号集合
     */
    public static void removeCurrCallFromCalled(ArrayList<CallInfoEntity> currCallInfoList, ArrayList<CallInfoEntity> called_info_list) {

        if (currCallInfoList != null && currCallInfoList.size() > 0 && called_info_list != null && called_info_list.size() > 0) {
            for (int i = 0; i < called_info_list.size(); i++) {
                //票号
                String ticketNo = called_info_list.get(i).getTicketNo();
                for (int k = 0; k < currCallInfoList.size(); k++) {
                    String ticketNo2 = currCallInfoList.get(k).getTicketNo();
                    //如果票号相同
                    if (ticketNo.equalsIgnoreCase(ticketNo2)) {
                        called_info_list.remove(i);
                        break;
                    }
                }
            }
        }

    }


    /**
     * 隐藏中间字
     * @param name
     * @return
     */
    public static String formatName(String name) {
        if (TextUtils.isEmpty(name)) {
            return "";
        }

        StringBuffer stringBuffer = new StringBuffer();
        if (name.length() == 2) {
            stringBuffer.append(name.charAt(0) + "*");
        } else if (name.length() > 2) {
            for (int i = 0; i < name.length(); i++) {
                if (i == 0) {
                    stringBuffer.append(name.charAt(0));
                } else if (i > 0 && i < name.length() - 1) {
                    stringBuffer.append("*");
                } else {
                    stringBuffer.append(name.charAt(name.length() - 1));
                }
            }
        } else {
            stringBuffer.append(name);
        }

        return stringBuffer.toString();

    }


    /**
     * 判断是否在选择的窗口中
     * @param selectList 选中的窗口集合
     * @param counterNo 需要判断是否选中的窗口号
     * @return 选中返回true,未选中返回false
     */
    public static boolean isCounterSelected(ArrayList<CounterEntity> selectList, int counterNo) {
        boolean isChecked = false;

        if(selectList.size()==0){
            isChecked = true;
        }

        for (int k = 0; k < selectList.size(); k++) {
            CounterEntity entity = selectList.get(k);
            if(entity.getCounterNo() == counterNo){
                isChecked = true;
                break;
            }
        }
        return isChecked;
    }


}
