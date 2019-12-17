package com.qianjinzhe.lcd_display.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.open.net.client.impl.tcp.bio.BioClient;
import com.open.net.client.structures.BaseMessageProcessor;
import com.open.net.client.structures.IConnectListener;
import com.open.net.client.structures.TcpAddress;
import com.qianjinzhe.lcd_display.R;
import com.qianjinzhe.lcd_display.bean.AppSettingEntity;
import com.qianjinzhe.lcd_display.bean.CallInfoEntity;
import com.qianjinzhe.lcd_display.bean.CallInfoListEntity;
import com.qianjinzhe.lcd_display.bean.CounterEntity;
import com.qianjinzhe.lcd_display.bean.CounterSettingEntity;
import com.qianjinzhe.lcd_display.bean.DateModel;
import com.qianjinzhe.lcd_display.bean.MediaEntity;
import com.qianjinzhe.lcd_display.bean.SocketMsg;
import com.qianjinzhe.lcd_display.bean.TemplateEntity;
import com.qianjinzhe.lcd_display.event.ChangeIpEvent;
import com.qianjinzhe.lcd_display.event.CloseSettingIpEvent;
import com.qianjinzhe.lcd_display.event.NetworkEvent;
import com.qianjinzhe.lcd_display.event.ShutdownEvent;
import com.qianjinzhe.lcd_display.ijkplayer.CustomMediaPlayer;
import com.qianjinzhe.lcd_display.listener.DialogListener;
import com.qianjinzhe.lcd_display.listener.OnLoadFileListener;
import com.qianjinzhe.lcd_display.listener.OnSelectListener;
import com.qianjinzhe.lcd_display.listener.SettingConectListener;
import com.qianjinzhe.lcd_display.manager.ImageManager;
import com.qianjinzhe.lcd_display.receiver.AdminManageReceiver;
import com.qianjinzhe.lcd_display.ui.activity.base.BaseActivity;
import com.qianjinzhe.lcd_display.ui.adapter.LazyAdapter;
import com.qianjinzhe.lcd_display.ui.listener.ScreenListener;
import com.qianjinzhe.lcd_display.ui.widget.MarqueeText;
import com.qianjinzhe.lcd_display.util.AppUtils;
import com.qianjinzhe.lcd_display.util.Constants;
import com.qianjinzhe.lcd_display.util.DialogUtils;
import com.qianjinzhe.lcd_display.util.FileUtils;
import com.qianjinzhe.lcd_display.util.LogUtils;
import com.qianjinzhe.lcd_display.util.PermissionsUtil;
import com.qianjinzhe.lcd_display.util.SPUtils;
import com.qianjinzhe.lcd_display.util.SocketUtils;
import com.qianjinzhe.lcd_display.util.TextUtils;
import com.qianjinzhe.lcd_display.util.ToastUtil;
import com.qianjinzhe.lcd_display.util.ViewHolderUtils;
import com.qianjinzhe.lcd_display.util.ZipUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.OnClick;

import static com.qianjinzhe.lcd_display.R.id.rlyt_all;
import static com.qianjinzhe.lcd_display.util.AppUtils.getTemplateEntityFromTemplateId;
import static com.qianjinzhe.lcd_display.util.DialogUtils.hideNavigation;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              MainActivity.java
 * Description：
 *              主页界面
 * Author:
 *              youngHu
 * Finished：
 *              2018年05月31日
 ********************************************************/
public class MainActivity extends BaseActivity implements CustomMediaPlayer.OnVideoPlayListener, ScreenListener.ScreenStateListener, Handler.Callback, PermissionsUtil.IPermissionsCallback {
    /**
     * 切换历史叫号标识
     */
    private final static int CHANGE_HOSPITAL_WAIT_CALL = 100;
    /**
     * 更新时间标识
     */
    private final static int UPDATE_TIME = 1004;
    /**
     * 申请权限code
     */
    private final static int REQUEST_PERMISSIONS_CODE = 1003;

    private static long mLastActive = 0;

    private static String HEART_BEAT_SECS = "15";

    private static boolean mIsLogin = false;
    /**
     * 多媒体显示区域
     */
    @Bind(R.id.rlyt_media_display_area)
    RelativeLayout rlyt_media_display_area;
    /**
     * 图片轮播控件
     */
    @Bind(R.id.convenientBanner)
    ConvenientBanner convenientBanner;
    /**
     * 视频控件
     */
    @Bind(R.id.mMediaPlayer)
    CustomMediaPlayer mMediaPlayer;

    /**
     * WebView
     */
    @Bind(R.id.mWebView)
    WebView mWebView;

    /**
     * 头像
     */
    @Bind(R.id.iv_head)
    ImageView iv_head;

    /***************本地模板开始*****************/

    /**
     * 本地模板id
     */
    private String local_template_id = "";

    /**********本地模板1**************/
    /**
     * 整个本地模板一的布局
     */
    @Bind(R.id.llyt_local_hospital_template_1)
    LinearLayout llyt_local_hospital_template_1;
    /**
     * 医院名称
     */
    @Bind(R.id.tv_hospital_name_v1)
    TextView tv_hospital_name_v1;
    /**
     * 科室名称
     */
    @Bind(R.id.tv_service_name_v1)
    TextView tv_service_name_v1;
    /**
     * 日期和星期
     */
    @Bind(R.id.tv_date_and_week_v1)
    TextView tv_date_and_week_v1;
    /**
     * 时间
     */
    @Bind(R.id.tv_time_v1)
    TextView tv_time_v1;
    /**
     * 诊室名称
     */
    @Bind(R.id.tv_room_name_v1)
    TextView tv_room_name_v1;
    /**
     * 医生头像
     */
    @Bind(R.id.iv_doc_head_v1)
    ImageView iv_doc_head_v1;
    /**
     * 医生姓名
     */
    @Bind(R.id.tv_doc_name_v1)
    TextView tv_doc_name_v1;
    /**
     * 医生职称
     */
    @Bind(R.id.tv_doc_job_v1)
    TextView tv_doc_job_v1;
//    /**医生简介*/
//    @Bind(R.id.tv_doc_info_v1)
//    TextView tv_doc_info_v1;
    /**
     * 当前叫号
     */
    @Bind(R.id.tv_curr_call_v1)
    TextView tv_curr_call_v1;
    /**
     * 等待叫号1
     */
    @Bind(R.id.tv_wait_call_1_v1)
    TextView tv_wait_call_1_v1;
    /**
     * 等待叫号2
     */
    @Bind(R.id.tv_wait_call_2_v1)
    TextView tv_wait_call_2_v1;
    /**
     * 底部滚动的文字
     */
    @Bind(R.id.tv_marquee_text_v1)
    MarqueeText tv_marquee_text_v1;

    /************本地模板2**************/
    /**
     * 整个模板布局
     */
    @Bind(R.id.llyt_local_template_2)
    LinearLayout llyt_local_template_2;
    /**
     * 标题
     */
    @Bind(R.id.tv_title_h1)
    TextView tv_title_h1;
    /**
     * 头像
     */
    @Bind(R.id.iv_head_h1)
    ImageView iv_head_h1;
    /**
     * 姓名
     */
    @Bind(R.id.tv_name_h1)
    TextView tv_name_h1;
    /**
     * 工号
     */
    @Bind(R.id.tv_job_no_h1)
    TextView tv_job_no_h1;
    /**
     * 窗口号
     */
    @Bind(R.id.tv_counter_no_h1)
    TextView tv_counter_no_h1;
    /**
     * 业务名称
     */
    @Bind(R.id.tv_service_name_h1)
    TextView tv_service_name_h1;
    /**
     * 当前叫号
     */
    @Bind(R.id.tv_curr_call_h1)
    TextView tv_curr_call_h1;
    /**
     * 下一叫号
     */
    @Bind(R.id.tv_next_call_h1)
    TextView tv_next_call_h1;
    /**
     * 滚动文字
     */
    @Bind(R.id.tv_marquee_text_h1)
    MarqueeText tv_marquee_text_h1;


    /**********本地模板3**************/
    /**
     * 整个本地模板一的布局
     */
    @Bind(R.id.llyt_local_template_3)
    LinearLayout llyt_local_template_3;
    /**
     * 医院名称
     */
    @Bind(R.id.tv_hospital_name_v2)
    TextView tv_hospital_name_v2;
    /**
     * 日期和星期
     */
    @Bind(R.id.tv_date_and_week_v2)
    TextView tv_date_and_week_v2;
    /**
     * 时间
     */
    @Bind(R.id.tv_time_v2)
    TextView tv_time_v2;
    /**
     * 诊室名称
     */
    @Bind(R.id.tv_room_name_v2)
    TextView tv_room_name_v2;
    /**
     * 医生头像
     */
    @Bind(R.id.iv_doc_head_v2)
    ImageView iv_doc_head_v2;
    /**
     * 医生姓名
     */
    @Bind(R.id.tv_doc_name_v2)
    TextView tv_doc_name_v2;
    /**
     * 医生职称
     */
    @Bind(R.id.tv_doc_job_v2)
    TextView tv_doc_job_v2;
    /**
     * 当前叫号
     */
    @Bind(R.id.tv_curr_call_v2)
    TextView tv_curr_call_v2;
    /**
     * 等待叫号1
     */
    @Bind(R.id.tv_wait_call_1_v2)
    TextView tv_wait_call_1_v2;
    /**
     * 等待叫号2
     */
    @Bind(R.id.tv_wait_call_2_v2)
    TextView tv_wait_call_2_v2;
    /**
     * 等待叫号3
     */
    @Bind(R.id.tv_wait_call_3_v2)
    TextView tv_wait_call_3_v2;
    /**
     * 等待叫号4
     */
    @Bind(R.id.tv_wait_call_4_v2)
    TextView tv_wait_call_4_v2;
    /**
     * 底部滚动的文字
     */
    @Bind(R.id.tv_marquee_text_v2)
    MarqueeText tv_marquee_text_v2;

    /**********本地模板4**************/
    /**
     * 整个本地模板一的布局
     */
    @Bind(R.id.llyt_local_template_4)
    LinearLayout llyt_local_template_4;
    /**
     * 医院名称
     */
    @Bind(R.id.tv_hospital_name_v3)
    TextView tv_hospital_name_v3;
    /**
     * 日期和星期
     */
    @Bind(R.id.tv_date_and_week_v3)
    TextView tv_date_and_week_v3;
    /**
     * 时间
     */
    @Bind(R.id.tv_time_v3)
    TextView tv_time_v3;
    /**
     * 诊室名称
     */
    @Bind(R.id.tv_room_name_v3)
    TextView tv_room_name_v3;
    /**
     * 医生头像
     */
    @Bind(R.id.iv_doc_head_v3)
    ImageView iv_doc_head_v3;
    /**
     * 医生姓名
     */
    @Bind(R.id.tv_doc_name_v3)
    TextView tv_doc_name_v3;
    /**
     * 医生职称
     */
    @Bind(R.id.tv_doc_job_v3)
    TextView tv_doc_job_v3;
    /**
     * 当前叫号
     */
    @Bind(R.id.tv_curr_call_v3)
    TextView tv_curr_call_v3;
    /**
     * 等待叫号1
     */
    @Bind(R.id.tv_wait_call_1_v3)
    TextView tv_wait_call_1_v3;
    /**
     * 等待叫号2
     */
    @Bind(R.id.tv_wait_call_2_v3)
    TextView tv_wait_call_2_v3;
    /**
     * 底部滚动的文字
     */
    @Bind(R.id.tv_marquee_text_v3)
    MarqueeText tv_marquee_text_v3;

    /**********本地模板5**************/
    /**
     * 整个模板布局
     */
    @Bind(R.id.llyt_local_template_5)
    LinearLayout llyt_local_template_5;
    /**
     * 标题
     */
    @Bind(R.id.tv_title_t5)
    TextView tv_title_t5;
    /**
     * 日期和时间
     */
    @Bind(R.id.tv_date_and_time_t5)
    TextView tv_date_and_time_t5;
    /**
     * 头像
     */
    @Bind(R.id.iv_head_t5)
    ImageView iv_head_t5;
    /**
     * 工号
     */
    @Bind(R.id.tv_job_no_t5)
    TextView tv_job_no_t5;
    /**
     * 职称
     */
    @Bind(R.id.tv_job_title_t5)
    TextView tv_job_title_t5;
    /**
     * 窗口号或窗口别名
     */
    @Bind(R.id.tv_counter_no_t5)
    TextView tv_counter_no_t5;
    /**
     * 业务名称
     */
    @Bind(R.id.tv_service_name_t5)
    TextView tv_service_name_t5;
    /**
     * 当前叫号
     */
    @Bind(R.id.tv_curr_call_t5)
    TextView tv_curr_call_t5;
    /**
     * 下一叫号
     */
    @Bind(R.id.tv_next_call_t5)
    TextView tv_next_call_t5;
    /**
     * 底部滚动的文字
     */
    @Bind(R.id.tv_marquee_text_t5)
    MarqueeText tv_marquee_text_t5;

    /**********本地模板6**************/
    /**
     * 整个模板布局
     */
    @Bind(R.id.llyt_local_template_6)
    LinearLayout llyt_local_template_6;
    /**
     * 标题
     */
    @Bind(R.id.tv_title_t6)
    TextView tv_title_t6;
    /**
     * 业务名称
     */
    @Bind(R.id.tv_service_name_t6)
    TextView tv_service_name_t6;
    /**
     * 日期和星期
     */
    @Bind(R.id.tv_date_and_week_t6)
    TextView tv_date_and_week_t6;
    /**
     * 时间
     */
    @Bind(R.id.tv_time_t6)
    TextView tv_time_t6;
    /**
     * 头像
     */
    @Bind(R.id.iv_head_t6)
    ImageView iv_head_t6;
    /**
     * 医生姓名
     */
    @Bind(R.id.tv_doc_name_t6)
    TextView tv_doc_name_t6;
    /**
     * 职称
     */
    @Bind(R.id.tv_job_title_t6)
    TextView tv_job_title_t6;
    /**
     * 窗口号或窗口名称
     */
    @Bind(R.id.tv_counter_no_t6)
    TextView tv_counter_no_t6;
    /**
     * 当前叫号
     */
    @Bind(R.id.tv_curr_call_t6)
    TextView tv_curr_call_t6;
    /**
     * 下一叫号
     */
    @Bind(R.id.tv_next_call_t6)
    TextView tv_next_call_t6;
    /**
     * 滚动文字
     */
    @Bind(R.id.tv_marquee_text_t6)
    MarqueeText tv_marquee_text_t6;

    /**********本地模板7**************/
    /**
     * 整个模板布局
     */
    @Bind(R.id.llyt_local_template_7)
    LinearLayout llyt_local_template_7;
    /**
     * 标题
     */
    @Bind(R.id.tv_title_t7)
    TextView tv_title_t7;
    /**
     * 业务名称
     */
    @Bind(R.id.tv_service_name_t7)
    TextView tv_service_name_t7;
    /**
     * 日期和星期
     */
    @Bind(R.id.tv_date_and_week_t7)
    TextView tv_date_and_week_t7;
    /**
     * 时间
     */
    @Bind(R.id.tv_time_t7)
    TextView tv_time_t7;
    /**
     * 头像
     */
    @Bind(R.id.iv_head_t7)
    ImageView iv_head_t7;
    /**
     * 医生姓名
     */
    @Bind(R.id.tv_doc_name_t7)
    TextView tv_doc_name_t7;
    /**
     * 职称
     */
    @Bind(R.id.tv_job_title_t7)
    TextView tv_job_title_t7;
    /**
     * 窗口号或窗口名称
     */
    @Bind(R.id.tv_counter_no_t7)
    TextView tv_counter_no_t7;
    /**
     * 当前叫号
     */
    @Bind(R.id.tv_curr_call_t7)
    TextView tv_curr_call_t7;
    /**
     * 下一叫号
     */
    @Bind(R.id.tv_next_call_t7)
    TextView tv_next_call_t7;
    /**
     * 滚动文字
     */
    @Bind(R.id.tv_marquee_text_t7)
    MarqueeText tv_marquee_text_t7;


    /***************本地模板结束*****************/


    /**
     * Socket连接对象
     */
    private BioClient mClient = null;
    /**
     * 连接定时器
     */
    private Timer connectTimer = null;
    /**
     * 文件长度
     */
    private int fileLength = 0;
    /**
     * 开始接收的文件长度
     */
    private int reciverLength = 0;
    /**
     * 接收类型
     */
    private boolean isFile = false;
    /**
     * 文件名
     */
    private String fileName = "";
    /**
     * 接收的文件名称和路径
     */
    private String filePathAndName = "";
    /**
     * 配置文件目录
     */
    public String configDirPath = "";
    /**
     * 视屏图片目录
     */
    public String mediaDirPath = "";
    /**
     * 创建FileOutputStream对象
     */
    private FileOutputStream outputStream = null;
    /**
     * 创建BufferedOutputStream对象
     */
    private BufferedOutputStream bufferedOutputStream = null;

    /**
     * 多媒体文件根目录
     */
    public String root_path = "";
    /**
     * 返回的文件目录
     */
    private String fileDir = "";
    /**
     * 是否创建输出流
     */
    private boolean isCreateStream = false;


    /**
     * 权限工具
     */
    public PermissionsUtil permissionsUtil;

    /*****日期时间相关*****/
    private Timer dateAndTimeTimer;
    private Handler mTimehandler;

    /****Arg3中的参数******/
    /**
     * 下一叫号票号
     */
    private String nextTicketNo = "";
    /**
     * 当前叫号名称
     */
    private String currName = "";
    /**
     * 医生的名称
     */
    private String doctorName = "";
    /**
     * 诊室别名
     */
    private String consultingRoomAlias = "";
    /**
     * 下一叫号名称
     */
    private String nextName = "";
    /**
     * 第二个等待叫号票号
     */
    private String twoTicketNo = "";
    /**
     * 第二个等待叫号名称
     */
    private String twoName = "";
    /**
     * 第三个等待叫号票号
     */
    private String threeTicketNo = "";
    /**
     * 第三个等待叫号名称
     */
    private String threeName = "";
    /**
     * 第四个等待叫号票号
     */
    private String fourTicketNo = "";
    /**
     * 第四个等待叫号名称
     */
    private String fourName = "";


    /**
     * 多媒体文件地址集合
     */
    private ArrayList<MediaEntity> mediaUrlList = new ArrayList<MediaEntity>();
    /**
     * 当前位置
     */
    private int curr_position = 0;
    /**
     * 图片切换时间，单位：秒
     */
    private int picSwitchTime = 10;

    /**
     * 上次保存的配置文件
     */
    private AppSettingEntity settingEntity_old = null;
    /**
     * 本次的更新文件
     */
    private AppSettingEntity settingEntity_new = null;

    /**
     * 版本号
     */
    private String version = "";

    /**
     * 是否自动关机
     */
    private boolean isAutoShutdown = false;
    /**
     * 关机时间
     */
    private String autoShutdown_time = "";
    /**
     * 是否已启动自动关机
     */
    private boolean isAutoShutDownStart = false;

    /**
     * 退出密码
     */
    private String quitPassword = "123";
    /**
     * 是否使用退出密码,true使用，false不使用
     */
    private boolean isUseQuitPassword = true;
    /**
     * 定时锁屏定时器
     */
    private Timer lockSreenTimer;

    /**
     * 锁屏相关
     */
    private ComponentName mAdminName;
    private DevicePolicyManager mDPM;
    /**
     * 锁屏监听
     */
    private ScreenListener mScreenListener;
    /**
     * 是否锁屏,true表示锁屏了
     */
    private boolean isLockScreen = false;

    /**
     * 是否开始轮播状态,true是,false不是
     */
    private boolean isStartPlayState = false;
    /**
     * 是否正在播放视频
     */
    private boolean isPlayVideoMode = false;
    /**
     * 背景名称
     */
    private String bgName = "";
    /**
     * 多媒体目录,默认根目录
     */
    private String mediaDirectory = "\\";
    /**
     * 多媒体切换开关,1切换,0不切换
     */
    private String mediaSwitch = "0";
    /**
     * 是否使用动画,1使用
     */
    private String mediaAnimation = "1";
    /**
     * 更新参数
     */
    private String reqFiles = "";
    /**
     * 多媒体宽高
     */
    private int mediaWidth = 0;
    /**
     * 多媒体高
     */
    private int mediaHeight = 0;

    /**
     * 定时关机线程
     */
    private TimeThread timeThread;

    /**
     * 窗口对象
     */
    private CounterSettingEntity mCounterEntity = null;
    /**
     * 模板对象
     */
    private TemplateEntity mTemplateEntity = null;

    /**
     * 保存历史叫号和当前叫号信息集合，0未当前叫号
     */
    private ArrayList<CallInfoEntity> call_info_list = new ArrayList<CallInfoEntity>();
    /**
     * 标识是否启用叫号模板
     */
    private boolean isEnalbeCallModel = false;
    /**
     * 叫号模板文字
     */
    private String callModel_text = "";
    /**
     * 窗口号的长度
     */
    private int counterNoLen = 1;
    /**
     * 标识是否刚进入主页
     */
    private boolean is_enter_first = true;
    /**
     * 标识是否显示过了窗口ip设置弹窗
     */
    private boolean is_showIp = true;

    /**
     * 模板id
     */
    private String currTemplateId = "";
    /**
     * 医院模板当前叫号
     */
    private ArrayList<CallInfoEntity> currCallInfoList = new ArrayList<CallInfoEntity>();
    /**
     * 医院模板历史叫号列表数据集合
     */
    private ArrayList<CallInfoListEntity> hospitalWaitCallList = new ArrayList<CallInfoListEntity>();
    /**
     * 医院综合屏历史叫号切换定时器
     */
    private Timer waitingCallTimer;
    /**
     * 医院历史叫号切换位置
     */
    private int hospitalWaitCallPosition = 0;
    /**
     * 是否改变了模板id
     */
    private boolean isChangeTemplateId = false;
    /**
     * 多媒体数量
     */
    private int media_count = 0;
    /**
     * html相关文件个数
     */
    private int html_file_count = 0;
    /**
     * 记录文件的名称与大小
     */
    private Map<String, Integer> fileMaps = new HashMap<String, Integer>();
    /**
     * 定时检查配置文件定时器
     */
    private Timer checkSettingFileTmer;

    /**
     * 员工工号
     */
    private String staffId = "";
    /**
     * 员工名称
     */
    private String staffName = "";
    /**
     * 员工职称
     */
    private String staffTitle = "";
    /**
     * 员工星级
     */
    private int star = -1;
    /**
     * 是否存在头像,不存在false,true存在
     */
    private boolean isHasHeadPic = false;
    /**
     * 头像文件路径
     */
    private String headFilePath = "";
    /**
     * 配置文件路径
     */
    private String settingFilePath = "";
    /**
     * 标识是否是手动连接,true手动连接，第一次进入算手动连接
     */
    private boolean isManualConnect = true;
    /**
     * 标识是否被强制退出
     */
    private boolean isForcedOut = false;
    /**
     * 版本更新文件
     */
    private String updateFilePath = "";
    /**
     * 版本更新请求次数
     */
    private int updateVersionNum = 0;

    /**
     * 当前选中的窗口(综合屏窗口过滤的)
     */
    private ArrayList<CounterEntity> currSelectedCounterList = new ArrayList<CounterEntity>();


    /**
     * 进入主页
     *
     * @param context
     */
    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }


    @Override
    public void initData() {
        super.initData();
        try {
            fileMaps.clear();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //删除10天前的日志
                    LogUtils.delFile();
                }
            }).start();
            //清空医院模板1，id为3模板的当前叫号
            currCallInfoList.clear();
            call_info_list.clear();
            setEvent();
            //获取上一次选中显示的窗口号，针对窗口过滤
            getSelectedCounter();
            //获取服务器ip
            SocketUtils.SERCIVER_IP = (String) SPUtils.get(mContext, SPUtils.SERVICE_IP, "192.168.0.100");
            SocketUtils.SERCIVER_IP_OLD = SocketUtils.SERCIVER_IP;
            //获取数据库ip
            SocketUtils.DATABASE_IP = SocketUtils.SERCIVER_IP;
            //获取窗口号
            SocketUtils.COUNTER_NO = (int) SPUtils.get(mContext, SPUtils.COUNTER_NO, 1);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        try {
            //设置安卓本地模板1的标题字体
            AppUtils.setTextViewFont(mContext, tv_hospital_name_v1, "fonts/lishu.ttf");
            //设置本地模板2标题字体
            AppUtils.setTextViewFont(mContext, tv_title_h1, "fonts/lishu.ttf");
            //设置安卓本地模板3的标题字体
            AppUtils.setTextViewFont(mContext, tv_hospital_name_v2, "fonts/lishu.ttf");
            //设置安卓本地模板4的标题字体
            AppUtils.setTextViewFont(mContext, tv_hospital_name_v3, "fonts/lishu.ttf");
            //设置安卓本地模板5的标题字体
            AppUtils.setTextViewFont(mContext, tv_title_t5, "fonts/lishu.ttf");
            //设置安卓本地模板6的标题字体
            AppUtils.setTextViewFont(mContext, tv_title_t6, "fonts/lishu.ttf");
            //设置安卓本地模板7的标题字体
            AppUtils.setTextViewFont(mContext, tv_title_t7, "fonts/lishu.ttf");
            //设置播放监听
            mMediaPlayer.setOnVideoPlayListener(this);
            //设置锁屏开屏监听
            mScreenListener = new ScreenListener(this);
            mScreenListener.begin(this);
            //初始化时间handler
            mTimehandler = new Handler(this);
            /**********初始化锁屏***********/
            mAdminName = new ComponentName(this, AdminManageReceiver.class);
            mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            //如果没有管理权限，使用隐式意图调用系统方法来激活指定的设备管理器
//        if (!mDPM.isAdminActive(mAdminName)) {
//            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
//            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "activity device");
//            startActivityForResult(intent, 1001);
//        } else {
            //判断android版本进行权限申请,6.0或以上版本需要
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                permissionsUtil = PermissionsUtil
                        .with(this)
                        .requestCode(REQUEST_PERMISSIONS_CODE)
                        .isDebug(true)//开启log
                        .permissions(PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE, PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE)
                        .request();
            } else {
                //初始化创建文件夹、开始轮播和Socket
                init();
            }
//        }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            //判断android版本进行权限申请,6.0或以上版本需要
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                permissionsUtil = PermissionsUtil
                        .with(this)
                        .requestCode(REQUEST_PERMISSIONS_CODE)
                        .isDebug(true)//开启log
                        .permissions(PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE, PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE)
                        .request();
            } else {
                //初始化创建文件夹、开始轮播和Socket
                init();
            }
        }

    }

    /**
     * 初始化创建文件夹和开始轮播
     */
    private void init() {
        try {
            //创建文件目录
            createFileDir();
            //初始化Socket
            initSocket();
            //获取配置文件
            String json = (String) SPUtils.get(mContext, SPUtils.SETTINGS_ENTITY, "");
            //获取当前模板id
            currTemplateId = (String) SPUtils.get(mContext, SPUtils.TEMPLATE_ID, "");
            //如果是本地模板
            if ("0".equals(currTemplateId)) {
                //获取本地模板id
                local_template_id = (String) SPUtils.get(mContext, SPUtils.LOCAL_TEMPLATE_ID, "1");
                //设置本地模板1隐藏
                llyt_local_hospital_template_1.setVisibility(View.GONE);
                //设置本地模板2隐藏
                llyt_local_template_2.setVisibility(View.GONE);
                //设置本地模板3隐藏
                llyt_local_template_3.setVisibility(View.GONE);
                //设置本地模板4隐藏
                llyt_local_template_4.setVisibility(View.GONE);
                //设置本地模板5隐藏
                llyt_local_template_5.setVisibility(View.GONE);
                //设置本地模板6隐藏
                llyt_local_template_6.setVisibility(View.GONE);
                //设置本地模板7隐藏
                llyt_local_template_7.setVisibility(View.GONE);
                //如果是本地模板1
                if ("1".equals(local_template_id)) {
                    //设置竖屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    //设置本地模板1显示
                    llyt_local_hospital_template_1.setVisibility(View.VISIBLE);
                    //启动日期时间定时器
                    startDateTimer();
                }
                //如果是本地模板2
                else if ("2".equals(local_template_id)) {
                    //设置横屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    //设置本地模板2显示
                    llyt_local_template_2.setVisibility(View.VISIBLE);
                } else if ("3".equals(local_template_id)) {
                    //设置竖屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    //设置本地模板3显示
                    llyt_local_template_3.setVisibility(View.VISIBLE);
                    //启动日期时间定时器
                    startDateTimer();
                } else if ("4".equals(local_template_id)) {
                    //设置竖屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    //设置本地模板4显示
                    llyt_local_template_4.setVisibility(View.VISIBLE);
                    //启动日期时间定时器
                    startDateTimer();
                } else if ("5".equals(local_template_id)) {
                    //设置横屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    //设置本地模板5显示
                    llyt_local_template_5.setVisibility(View.VISIBLE);
                } else if ("6".equals(local_template_id)) {
                    //设置横屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    //设置本地模板6显示
                    llyt_local_template_6.setVisibility(View.VISIBLE);
                } else if ("7".equals(local_template_id)) {
                    //设置横屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    //设置本地模板7显示
                    llyt_local_template_7.setVisibility(View.VISIBLE);
                }
            } else {
                //设置本地模板1隐藏
                llyt_local_hospital_template_1.setVisibility(View.GONE);
                //设置本地模板2隐藏
                llyt_local_template_2.setVisibility(View.GONE);
                //设置本地模板3隐藏
                llyt_local_template_3.setVisibility(View.GONE);
                //设置本地模板4隐藏
                llyt_local_template_4.setVisibility(View.GONE);
                //设置本地模板5隐藏
                llyt_local_template_5.setVisibility(View.GONE);
                //设置本地模板6隐藏
                llyt_local_template_6.setVisibility(View.GONE);
                //设置本地模板7隐藏
                llyt_local_template_7.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(json)) {
                settingEntity_old = new Gson().fromJson(json, AppSettingEntity.class);
                //获取背景名称,设置背景的时候需要
                if (settingEntity_old != null && !"0".equals(currTemplateId)) {
                    //获取模板
                    TemplateEntity templateEntity = getTemplateEntityFromTemplateId(settingEntity_old, currTemplateId);
                    //获取背景图名称
                    bgName = templateEntity.getBackgroundName();
                }
            }
            //初始化WebView
            initWebView();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }

    /**
     * 获取上一次选中显示的窗口号，针对窗口过滤
     */
    private void getSelectedCounter() {
        try {
            //获取当前选中的窗口
            String selectCounterStr = (String) SPUtils.get(mContext, SPUtils.CURR_SELECTED_COUNTER_LIST, "[]");
            ArrayList<CounterEntity> list = new Gson().fromJson(selectCounterStr, new TypeToken<ArrayList<CounterEntity>>() {
            }.getType());
            if (list != null) {
                currSelectedCounterList.clear();
                currSelectedCounterList.addAll(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化WevView
     */
    private void initWebView() {
        try {
            mWebView.getSettings().setJavaScriptEnabled(true);
            //设置可以访问文件
            mWebView.getSettings().setAllowFileAccess(true);
            //设置支持缩放
            mWebView.getSettings().setBuiltInZoomControls(true);
            //缩放至屏幕的大小
            mWebView.getSettings().setLoadWithOverviewMode(true);// 缩放至屏幕的大小
            mWebView.getSettings().setDefaultTextEncodingName("utf-8");
            mWebView.getSettings().setDomStorageEnabled(true);
            mWebView.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
            mWebView.getSettings().setBuiltInZoomControls(false); // 设置不显示缩放按钮
            mWebView.getSettings().setSupportZoom(true);// 不支持缩放
//            mWebView.getSettings().setUseWideViewPort(true);// 将图片调整到适合webview大小
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
            DisplayMetrics dm = getResources().getDisplayMetrics();
            mWebView.setInitialScale(100);//缩放限制100%
            // 获取当前界面的高度
            int scale = dm.densityDpi;
            if (scale == 240) {
                mWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
            } else if (scale == 160) {
                mWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
            } else {
                mWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }


    /**
     * 初始化Socket
     */
    private void initSocket() {
        try {
            //启动socket连接定时器
            if (connectTimer != null) {
                connectTimer.cancel();
                connectTimer = null;
            }
            connectTimer = new Timer();
            connectTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //如果是强制退出
                    if (isForcedOut) {
                        return;
                    }
                    if (mClient == null) {
                        LogUtils.d("测试", "重新生成连接");
                        //连接Socket
                        connectSocket();
                    }
                    //如果已连接服务器
                    if (mClient != null && mClient.isConnected()) {
                        LogUtils.d("测试", "已连接");
                        if (mIsLogin) {
                            SocketMsg msg = new SocketMsg();
                            msg.MsgType = Constants.QQS_TVD_HEARTBEAT;
                            msg.CounterNo = SocketUtils.COUNTER_NO;
                            sendMessage(msg);
                        }
                        if ((System.currentTimeMillis() - mLastActive) > 30 * 1000) {
                            mClient.disconnect();
                        }
                    }

                    //如果已断开，重连
                    if (!mClient.isConnected()) {
                        LogUtils.d("测试", "断开重连");
                        mClient.connect();
                    }
                }
            }, 200, 10 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //需要调用onRequestPermissionsResult
        permissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionsGranted(final int requestCode, String... permission) {
        //获取权限成功
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (requestCode == REQUEST_PERMISSIONS_CODE) {

                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        //初始化创建文件夹、开始轮播和Socket
                        init();
                    }
                }
            }
        });
    }

    @Override
    public void onPermissionsDenied(int requestCode, String... permission) {
        //获取权限失败
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showShort(mContext, R.string.permission_tips);
            }
        });
    }

    /**
     * 创建文件目录
     */
    private void createFileDir() {
        try {
            //先通过Environment（环境）的getExternalStorageState()方法来获取手机环境下的外置存储卡的状态，判断是否为挂载状态。
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                //如果为挂载状态，那么就通过Environment的getExternalStorageDirectory()方法来获取
                //外置存储卡的目录，然后加上我们自己要创建的文件名（记住文件名前要加一个"/"）
                root_path = Environment.getExternalStorageDirectory() + "/QQSLcdDisplay";
                //配置文件目录
                configDirPath = root_path + "/Config";
                File pathConfigDir_file = new File(configDirPath);
                if (!pathConfigDir_file.exists()) {
                    pathConfigDir_file.mkdirs();
                }
                //视屏图片目录
                mediaDirPath = root_path + "/Media";
                File pathMediaDir_file = new File(mediaDirPath);
                if (!pathMediaDir_file.exists()) {
                    pathMediaDir_file.mkdirs();
                }
                //配置文件Setting.xml路径
                settingFilePath = configDirPath + "/Setting.xml";
                //头像路径
                if(staffId == ""){
                    staffId = "head";
                }
                headFilePath = configDirPath + "/" + staffId + ".jpg";
                //版本更新文件
                updateFilePath = root_path + "/update.zip";
            } else {
                //获取U盘地址
                ArrayList<String> usbPathList = FileUtils.getExternalStorageDirectory();
                if (usbPathList.size() > 0) {
                    LogUtils.writeLogtoFile("usb路径", usbPathList.get(0));
                    root_path = usbPathList.get(0) + "/QQSLcdDisplay";
                    //配置文件目录
                    configDirPath = root_path + "/Config";
                    File pathConfigDir_file = new File(configDirPath);
                    if (!pathConfigDir_file.exists()) {
                        pathConfigDir_file.mkdirs();
                    }
                    //视屏图片目录
                    mediaDirPath = root_path + "/Media";
                    File pathMediaDir_file = new File(mediaDirPath);
                    if (!pathMediaDir_file.exists()) {
                        pathMediaDir_file.mkdirs();
                    }
                    //配置文件Setting.xml路径
                    settingFilePath = configDirPath + "/Setting.xml";
                    //头像路径
                    if(staffId == ""){
                        staffId = "head";
                    }
                    headFilePath = configDirPath + "/" + staffId + ".jpg";
                    //版本更新文件
                    updateFilePath = root_path + "/update.zip";
                } else {
                    LogUtils.writeLogtoFile("usb路径", "未插入U盘");
                    ToastUtil.showLong(mContext, "请插入U盘后，重新开启app");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }


    /**
     * 连接socket
     */
    private void connectSocket() {
        try {
            //Bio模式
            if (mClient != null) {
                //断开连接
                mClient.disconnect();
                mClient = null;
            }
            mClient = new BioClient(mMessageProcessor, mConnectResultListener);
            //设置连接超时
            mClient.setConnectTimeout(10 * 1000);
            //设置服务ip和端口号
            mClient.setConnectAddress(new TcpAddress[]{new TcpAddress(SocketUtils.SERCIVER_IP, SocketUtils.SERCIVER_PORT)});
            //执行连接
            mClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }


    /**
     * 连接监听
     */
    private IConnectListener mConnectResultListener = new IConnectListener() {
        @Override
        public void onConnectionSuccess() {
            mLastActive = System.currentTimeMillis();
            //连接成功
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        //标识不是刚进入页面
                        is_enter_first = false;
                        //标识连接成功
                        //发送Event到弹窗页面，关闭弹窗页面
                        EventBus.getDefault().post(new CloseSettingIpEvent());
                        LogUtils.d("连接服务器", "连接服务器成功");
                        LogUtils.writeLogtoFile("连接服务器", "连接服务器成功");
                        //如果是4号模板，清空员工信息
                        if ("4".equals(currTemplateId) || "5".equals(currTemplateId)
                                || "7".equals(currTemplateId)
                                || ("0".equals(currTemplateId) && "1".equals(local_template_id))
                                || ("0".equals(currTemplateId) && "2".equals(local_template_id))
                                || ("0".equals(currTemplateId) && "3".equals(local_template_id))
                                || ("0".equals(currTemplateId) && "4".equals(local_template_id))
                                || ("0".equals(currTemplateId) && "5".equals(local_template_id))
                                || ("0".equals(currTemplateId) && "6".equals(local_template_id))
                                || ("0".equals(currTemplateId) && "7".equals(local_template_id))) {
                            //清空员工信息
                            cleanEmployeeInfo();
                            //标识默认无头像
                            isHasHeadPic = false;
                        }
                        //发送Lcd登录消息
                        sendLoginMessage();
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                    }
                }
            });
        }


        @Override
        public void onConnectionFailed() {
            //连接失败
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        LogUtils.d("连接服务器", "连接服务器失败");
                        dismissProgressDialog();
                        //标识默认没有头像
                        isHasHeadPic = false;
                        //标识连接失败
                        //关闭定时检查配置文件定时器
                        if (checkSettingFileTmer != null) {
                            checkSettingFileTmer.cancel();
                            checkSettingFileTmer = null;
                        }
                        //如果没有显示设置ip弹窗,并且之前从未连接成功
                        if (!isShowSettingIp && is_showIp) {
                            LogUtils.writeLogtoFile("连接服务器", "连接服务器失败");
                            enterSettingIp(mContext.getResources().getString(R.string.connect_error_tips));
                            is_showIp = false;
                        }
                        isUpdateFile = false;
                        //如果之前配置文件不为空并且是第一次进入页面
                        if (settingEntity_old != null && is_enter_first) {
                            //标识不是刚进入页面
                            is_enter_first = false;
                            //设置默认参数
                            setDefaultParam(settingEntity_old);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                    }
                }
            });
        }
    };


    /**
     * 进入设置ip页面
     */
    private void enterSettingIp(String title) {
        //进入ip设置弹窗页面
        SettingServiceIpActivity.startSettingServiceIpActivity(mContext, title);
    }


    /**
     * 发送Lcd登录消息
     */
    private void sendLoginMessage() {
        try {
            SocketMsg msg = new SocketMsg();
            //消息类型
            msg.MsgType = Constants.QQS_TVD_LOGIN;
            //窗口号
            msg.CounterNo = SocketUtils.COUNTER_NO;

            msg.Arg4 = HEART_BEAT_SECS;
            mIsLogin = false;
            sendMessage(msg);
            LogUtils.writeLogtoFile("登录", "发送登录消息" + msg.toString());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }


    /**
     * 发送消息
     *
     * @param msg
     */
    public void sendMessage(SocketMsg msg) {
        try {
            LogUtils.d("send", msg.toString());
            if (mClient != null) {
                //发送消息
                mMessageProcessor.send(mClient, SocketUtils.objectToBytes(msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }


    /******************************Socket接收和处理部分开始*********************************/
    /**
     * Socket数据接收监听
     */
    private BaseMessageProcessor mMessageProcessor = new BaseMessageProcessor() {

        @Override
        public void onReceiveMessagesData(final byte[] bytes, int offset, final int length) {
            LogUtils.e("接收", "data长度=" + bytes.length + ",offset=" + offset + ", length=" + length);
            try {
                //如果是通知消息
                if (length == SocketUtils.BUFFER_SIZE) {
                    //处理消息
                    final byte[] mss = new byte[length];
                    System.arraycopy(bytes, 0, mss, 0, length);
                    //解析处理消息
                    handleServerData(SocketUtils.bytesToObject(mss));
                }
                //如果是文件
                else if (isFile) {
                    //创建文件输出流
                    createFilePathAndSream();
                    //接收配置文件
                    if ("Setting.xml".equals(fileName)) {
                        //如果配置文件下载成功
                        if (receiverConfigOrHeadFile(bytes, length)) {
                            //判断配置文件的大小
                            long configFileSize = FileUtils.getFileLength(settingFilePath);
                            //如果未下载完成配置文件,则重新登录
                            LogUtils.d("settings", "Setting.xml下载大小" + configFileSize + ",应该下载大小=" + fileMaps.get("Setting.xml"));
                            if (null == fileMaps.get("Setting.xml") || configFileSize != fileMaps.get("Setting.xml")) {
                                //断开重新登录
                                disConnectReLogin();
                            } else {
                                LogUtils.writeLogtoFile("配置文件解析", "下载配置文件成功 ");
                                //解析配置文件
                                parseConfig();
                            }
                        }
                    } else if ("QQS_Staff_Photo".equals(fileName)) {
                        //如果配置文件下载成功
                        if (receiverConfigOrHeadFile(bytes, length)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        long headFileSize = FileUtils.getFileLength(headFilePath);
                                        LogUtils.d("settings", "头像下载大小" + headFileSize + ",应该下载大小=" + fileMaps.get("QQS_Staff_Photo"));
                                        if (null == fileMaps.get("QQS_Staff_Photo") || headFileSize != fileMaps.get("QQS_Staff_Photo")) {
                                            LogUtils.writeLogtoFile("下载头像", "头像下载大小不一致，退出连接重新登录");
                                            //断开重新登录
                                            disConnectReLogin();
                                        } else {
                                            LogUtils.writeLogtoFile("下载完成头像", "设置头像");
                                            //只有模板4才显示头像
                                            if ("4".equals(currTemplateId) || "5".equals(currTemplateId)) {
                                                if (headFileSize == 0) {
                                                    headFilePath = "";
                                                }
                                                mWebView.loadUrl("javascript:SetValue('StaffHeadPath','" + headFilePath + "')");
                                            } else if ("5".equals(currTemplateId)) {
                                                ImageManager.getInstance(mContext).loadImageForScal(mContext, new File(headFilePath), iv_head, R.mipmap.jingcha_default);
                                            } else if ("4".equals(currTemplateId) || "7".equals(currTemplateId)) {
                                                LogUtils.d("照片显示框", "宽=" + iv_head.getWidth() + ",高=" + iv_head.getHeight());
                                                ImageManager.getInstance(mContext).loadImageForScal(mContext, new File(headFilePath), iv_head, R.mipmap.person);
                                            }
                                            //如果是安卓本地模板
                                            else if ("0".equals(currTemplateId)) {
                                                //如果是本地模板1
                                                if ("1".equals(local_template_id)) {
                                                    ImageManager.getInstance(mContext).loadImageForScal(mContext, new File(headFilePath), iv_doc_head_v1, R.mipmap.person);
                                                }
                                                //如果是本地模板2
                                                else if ("2".equals(local_template_id)) {
                                                    ImageManager.getInstance(mContext).loadImageForScal(mContext, new File(headFilePath), iv_head_h1, R.mipmap.person);
                                                }
                                                //如果是本地模板3
                                                else if ("3".equals(local_template_id)) {
                                                    ImageManager.getInstance(mContext).loadImageForScal(mContext, new File(headFilePath), iv_doc_head_v2, R.mipmap.person);
                                                }
                                                //如果是本地模板4
                                                else if ("4".equals(local_template_id)) {
                                                    ImageManager.getInstance(mContext).loadImageForScal(mContext, new File(headFilePath), iv_doc_head_v3, R.mipmap.person);
                                                }
                                                //如果是本地模板5
                                                else if ("5".equals(local_template_id)) {
                                                    ImageManager.getInstance(mContext).loadImageForScal(mContext, new File(headFilePath), iv_head_t5, R.mipmap.person);
                                                }
                                                //如果是本地模板6
                                                else if ("6".equals(local_template_id)) {
                                                    ImageManager.getInstance(mContext).loadImageForScal(mContext, new File(headFilePath), iv_head_t6, R.mipmap.person);
                                                }
                                                //如果是本地模板6
                                                else if ("7".equals(local_template_id)) {
                                                    ImageManager.getInstance(mContext).loadImageForScal(mContext, new File(headFilePath), iv_head_t7, R.mipmap.person);
                                                }

                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                    //如果是多媒体文件、背景、更新文件等
                    else {
                        //接收多媒体文件,背景等
                        receiverMeidaFile(bytes, length);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
            }
        }
    };


    /**
     * 断开连接，重新登录
     */
    private void disConnectReLogin() {
        try {
            //关闭自动连接定时器
            Log.d("settings", "配置文件为下载不成功,断开重新登录");
            LogUtils.writeLogtoFile("settings", "配置文件为下载不成功,断开重新登录");
            if (connectTimer != null) {
                connectTimer.cancel();
                connectTimer = null;
            }
            //断开连接
            if (mClient != null) {
                mClient.disconnect();
                mClient = null;
            }
            mTimehandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //重新连接
                    initSocket();
                }
            }, 5 * 1000);

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }

    }


    /**
     * 定时检查配置文件
     */
    private void checkSettingFile() {
        try {
            if (checkSettingFileTmer != null) {
                checkSettingFileTmer.cancel();
                checkSettingFileTmer = null;
            }
            checkSettingFileTmer = new Timer();
            checkSettingFileTmer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //判断配置文件的大小
                    long configFileSize = FileUtils.getFileLength(settingFilePath);
                    //如果未下载完成配置文件,则重新登录
                    LogUtils.d("定时检查配置文件", "Setting.xml下载大小" + configFileSize + ",应该下载大小=" + fileMaps.get("Setting.xml"));
                    if (null == fileMaps.get("Setting.xml") || configFileSize != fileMaps.get("Setting.xml")) {
                        //断开重新登录
                        disConnectReLogin();
                    }
                    //如果有头像,并且是模板4
                    else if (isHasHeadPic && ("4".equals(currTemplateId) || "5".equals(currTemplateId)) || "7".equals(currTemplateId)
                            || ("0".equals(currTemplateId) && ("1".equals(local_template_id)
                            || "2".equals(local_template_id)
                            || "3".equals(local_template_id)
                            || "4".equals(local_template_id)
                            || "5".equals(local_template_id)
                            || "6".equals(local_template_id)
                            || "7".equals(local_template_id)))) {
                        long headFileSize = FileUtils.getFileLength(headFilePath);
                        LogUtils.d("定时检查头像文件", "头像下载大小" + headFileSize + ",应该下载大小=" + fileMaps.get("QQS_Staff_Photo"));
                        if (null != fileMaps.get("QQS_Staff_Photo") && headFileSize != fileMaps.get("QQS_Staff_Photo")) {
                            //断开重新登录
                            disConnectReLogin();
                        }
                    }
                }
            }, 60 * 1000, 5 * 60 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }

    }


    /**
     * 创建输出流
     */
    private void createFilePathAndSream() {
        try {
            //如果未创建输入流
            if (!isCreateStream) {
                isCreateStream = true;
                //判断创建文件目录
                createFileDir();
                //如果是配置文件
                if ("Setting.xml".equalsIgnoreCase(fileName)) {
                    filePathAndName = settingFilePath;
                }
                //如果是头像
                else if ("QQS_Staff_Photo".equals(fileName)) {
                    filePathAndName = headFilePath;
                }
                //如果更新包
                else if ("update.zip".equalsIgnoreCase(fileName)) {
                    //更新文件
                    filePathAndName = updateFilePath;

                } else {//如果是显示文件
                    //如果是放在配置目录
                    if ("Config".equalsIgnoreCase(fileDir)) {
                        filePathAndName = configDirPath + "/" + fileName;
                    }
                    //如果是多媒体文件
                    else if ("Media\\".equalsIgnoreCase(fileDir)
                            || "Media".equalsIgnoreCase(fileDir)
                            || ("Media" + mediaDirectory).equalsIgnoreCase(fileDir)) {
                        filePathAndName = mediaDirPath + "/" + fileName;
                    } else {
                        String path = root_path + "/" + fileDir.replace("\\", "/");
                        File file = new File(path);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        filePathAndName = path + "/" + fileName;
                    }
                }
                //创建文件输出流
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

                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }


    /**
     * 写入多媒体文件
     *
     * @param bytes
     */
    private void receiverMeidaFile(byte[] bytes, int length) {
        try {
            if (reciverLength < fileLength) {
                reciverLength += length;
                //设置文件更新进度
                setProgress(fileName, reciverLength, fileLength);
                // 往文件所在的缓冲输出流中写byte数据
                bufferedOutputStream.write(bytes, 0, length);
            }
            if (reciverLength >= fileLength) {
                //设置文件更新进度
                setProgress(fileName, reciverLength, fileLength);
                LogUtils.d("receiveMessage", "文件长度=" + fileLength + ",接收文件长度:" + reciverLength);
                isFile = false;
                // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
                bufferedOutputStream.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        } finally {
            if (reciverLength >= fileLength) {
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
        }
    }

    /**
     * 写入配置文件
     *
     * @param bytes
     */
    private boolean receiverConfigOrHeadFile(byte[] bytes, int length) {
        boolean isSuccess = false;
        try {
            if (reciverLength < fileLength) {
                reciverLength += length;
                // 往文件所在的缓冲输出流中写byte数据
                bufferedOutputStream.write(bytes, 0, length);
            }
            if (reciverLength >= fileLength) {
                LogUtils.d("receiveMessage", "文件长度=" + fileLength + ",接收文件长度:" + reciverLength);
                isFile = false;
                // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
                bufferedOutputStream.flush();
                isSuccess = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        } finally {
            if (reciverLength >= fileLength) {
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
        }

        return isSuccess;
    }


    /**
     * 发送强制登录消息
     */
    private void sendForcedLoginMessage() {
        try {
            SocketMsg msg = new SocketMsg();
            //消息类型
            msg.MsgType = Constants.QQS_TVD_LOGIN;
            msg.CounterNo = SocketUtils.COUNTER_NO;
            msg.Arg1 = 4;
            msg.Arg4 = HEART_BEAT_SECS;
            mIsLogin = false;
            sendMessage(msg);
            LogUtils.writeLogtoFile("强制登录", "发送登录消息" + msg.toString());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }


    /**
     * 处理服务端发过来的数据
     *
     * @param Msg 消息对象
     */
    private void handleServerData(final SocketMsg Msg) {
        try {
            LogUtils.d("接收到消息", "接收到消息:" + Msg.toString());
            mLastActive = System.currentTimeMillis();
            switch (Msg.MsgType) {
                case Constants.QQS_TVD_LOGIN://电视登录
                    LogUtils.d("receiveMessage", "电视登录成功" + Msg.toString());
                    LogUtils.writeLogtoFile("电视登录成功返回", Msg.toString());
                    mIsLogin = true;
                    //Arg1 = 1禁止登录，2 重复登录，3正常登录
                    if (Msg.Arg1 == 3 || Msg.Arg1 == 4) {
                        //标识不是强制退出
                        isForcedOut = false;
                        //标识下次不是手动连接
                        isManualConnect = false;
                        //定时检查配置文件
                        checkSettingFile();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (Msg.Arg1 == 1) {
                                    ToastUtil.showShort(mContext, "服务器禁止登录");
                                } else if (Msg.Arg1 == 2) {
                                    //如果是手动连接,手动连接才弹窗
                                    if (isManualConnect) {
                                        LogUtils.writeLogtoFile("重复登录", Msg.toString());
                                        DialogUtils.showForcedLoginDialog(mContext, new DialogListener() {
                                            @Override
                                            public void onComplete() {
                                                //发送强制登录消息
                                                sendForcedLoginMessage();
                                            }

                                            @Override
                                            public void onFail() {
                                                //断开连接
                                                if (mClient != null) {
                                                    mClient.disconnect();
                                                }
                                                //标识为强制退出
                                                isForcedOut = true;
                                            }
                                        });
                                    } else {
                                        //发送强制登录消息
                                        sendForcedLoginMessage();
                                    }
                                }
                            }
                        });
                    }

                    break;

                case Constants.QQS_TVD_CALLINFO://服务器发送叫号信息至电视
                    try {
                        LogUtils.d("receiveMessage", "当前模板id==" + currTemplateId);
                        LogUtils.d("receiveMessage", "服务器发送叫号信息至电视" + Msg.toString());
                        LogUtils.writeLogtoFile("服务器发送叫号信息", "当前窗口号=" + SocketUtils.COUNTER_NO + ",当前模板id=" + currTemplateId + ",消息:" + Msg.toString());
                        //清空之前信息
                        currName = "";
                        doctorName = "";
                        consultingRoomAlias = "";
                        nextTicketNo = "";
                        nextName = "";
                        twoTicketNo = "";
                        twoName = "";
                        threeTicketNo = "";
                        threeName = "";
                        fourTicketNo = "";
                        fourName = "";
                        LogUtils.writeLogtoFile("服务器发送叫号信息到电视", Msg.toString());
                        isFile = false;
                        /****Arg3、Arg4中的参数******/
                        //Arg3='{A001|占三三|张志洞|0|内科}{A002|李思思||0}', Arg4=''
                        String argStr = Msg.Arg3 + Msg.Arg4;
                        if (!TextUtils.isEmpty(argStr)) {
                            String[] callListStr = argStr.replaceAll("\\{", "").split("\\}");
                            //当前叫号
                            String[] callStr = callListStr[0].split("\\|");
                            if (callStr != null && callStr.length >= 5) {
                                //当前叫号名称
                                currName = !TextUtils.isEmpty(callStr[1]) ? callStr[1] : "";
                                //医生名称
                                doctorName = !TextUtils.isEmpty(callStr[2]) ? callStr[2] : "";
                                //诊室别名
                                consultingRoomAlias = !TextUtils.isEmpty(callStr[4]) ? callStr[4] : "";
                            }

                            if (callListStr.length >= 2) {
                                String[] nextCallStr = callListStr[1].split("\\|");
                                //下一叫号票号
                                nextTicketNo = !TextUtils.isEmpty(nextCallStr[0]) ? nextCallStr[0] : "";
                                //下一叫号名称
                                nextName = !TextUtils.isEmpty(nextCallStr[1]) ? nextCallStr[1] : "";
                            } else {
                                nextTicketNo = "";
                                nextName = "";
                            }

                            if (callListStr.length >= 3) {
                                String[] twoCallStr = callListStr[2].split("\\|");
                                //第二个等待叫号票号
                                twoTicketNo = !TextUtils.isEmpty(twoCallStr[0]) ? twoCallStr[0] : "";
                                //第三个叫号名称
                                twoName = !TextUtils.isEmpty(twoCallStr[1]) ? twoCallStr[1] : "";
                            } else {
                                twoTicketNo = "";
                                twoName = "";
                            }

                            if (callListStr.length >= 4) {
                                String[] threeCallStr = callListStr[3].split("\\|");
                                //第三个等待叫号票号
                                threeTicketNo = !TextUtils.isEmpty(threeCallStr[0]) ? threeCallStr[0] : "";
                                //第三个叫号名称
                                threeName = !TextUtils.isEmpty(threeCallStr[1]) ? threeCallStr[1] : "";
                            } else {
                                threeTicketNo = "";
                                threeName = "";
                            }

                            if (callListStr.length >= 5) {
                                String[] fourCallStr = callListStr[4].split("\\|");
                                //第4个等待叫号票号
                                fourTicketNo = !TextUtils.isEmpty(fourCallStr[0]) ? fourCallStr[0] : "";
                                //第4个等待叫号名称
                                fourName = !TextUtils.isEmpty(fourCallStr[1]) ? fourCallStr[1] : "";
                            } else {
                                fourTicketNo = "";
                                fourName = "";
                            }

                            //如果需要叫号弹窗
                            if (mTemplateEntity != null && "1".equals(mTemplateEntity.getCallPopupWindow_enable()) && !"0".equals(currTemplateId)) {
                                CallInfoEntity callInfo = new CallInfoEntity(Msg.CounterNo < 10 ? "0" + Msg.CounterNo : Msg.CounterNo + "", Msg.TicketNo, currName, consultingRoomAlias);
                                //如果是2号模板
                                if ("2".equals(currTemplateId) || "8".equals(currTemplateId)) {
                                    //如果窗口号未在选中的集合中,跳出
                                    if (AppUtils.isCounterSelected(currSelectedCounterList, Msg.CounterNo)) {
                                        //显示叫号弹窗
                                        showcurrCallDialog(mContext, mTemplateEntity, callInfo);
                                    }

                                } else {
                                    //显示叫号弹窗
                                    showcurrCallDialog(mContext, mTemplateEntity, callInfo);
                                }

                            }

                            //如果是医院综合模板
                            if ("3".equals(currTemplateId)) {
                                try {
                                    //添加数据
                                    CallInfoEntity callInfo = new CallInfoEntity(Msg.CounterNo + "", Msg.TicketNo, currName, doctorName, consultingRoomAlias, AppUtils.getServiceName(hospitalWaitCallList, Msg.TicketNo));
                                    //以下是隐藏名字中间的字
//                                    CallInfoEntity callInfo = new CallInfoEntity(Msg.CounterNo + "", Msg.TicketNo, AppUtils.formatName(currName), doctorName, consultingRoomAlias, AppUtils.getServiceName(hospitalWaitCallList, Msg.TicketNo));
                                    //删除重复叫号
                                    AppUtils.delHasReCall(currCallInfoList, Msg.TicketNo);
                                    //添加当前叫号
                                    currCallInfoList.add(0, callInfo);
                                    //如果数据为12条，移除最早的那一条
                                    if (currCallInfoList.size() == 7) {
                                        //添加过号
                                        CallInfoEntity callInfoEntity = currCallInfoList.get(6);
                                        //删除重复叫号
                                        AppUtils.delHasReCall(call_info_list, callInfoEntity.getTicketNo());
                                        //添加当前叫号
                                        call_info_list.add(0, callInfoEntity);
                                        //如果数据为12条，移除最早的那一条
                                        if (call_info_list.size() == 12) {
                                            call_info_list.remove(11);
                                        }
                                        currCallInfoList.remove(6);
                                    }
                                    //过号中移除当前的叫号(重复呼叫可能出现过号和当前叫号中都出现)
                                    AppUtils.removeCurrCallFromCalled(currCallInfoList, call_info_list);

                                    //当前叫号科室的等候叫号集合
                                    ArrayList<CallInfoEntity> list = new ArrayList<CallInfoEntity>();
                                    if (callListStr.length > 1) {
                                        for (int i = 1; i < callListStr.length; i++) {
                                            String[] callStr_child = callListStr[i].split("\\|");
                                            //下一叫号票号
                                            String ticketNo = !TextUtils.isEmpty(callStr_child[0]) ? callStr_child[0] : "";
                                            //下一叫号名称
                                            String name = !TextUtils.isEmpty(callStr_child[1]) ? callStr_child[1] : "";
                                            CallInfoEntity callInfoEntity = new CallInfoEntity(ticketNo, name);
                                            //以下是隐藏名字中间文字
//                                            CallInfoEntity callInfoEntity = new CallInfoEntity(ticketNo, AppUtils.formatName(name));
                                            list.add(callInfoEntity);
                                        }
                                    }
                                    //添加等待叫号
                                    AppUtils.addWaitCall(hospitalWaitCallList, list, Msg.TicketNo);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                //显示当前叫号信息
                                                mWebView.loadUrl("javascript:SetCurrentCall('" + new Gson().toJson(currCallInfoList) + "')");
                                                //过号的
                                                mWebView.loadUrl("javascript:SetCalled('" + new Gson().toJson(call_info_list) + "')");
                                                //获取历史叫号分组后的json字符串集合
                                                ArrayList<String> jsonList = AppUtils.getWaitCallJsonStrArr(hospitalWaitCallList);
                                                if (jsonList.size() > 0) {
                                                    //显示历史叫号
                                                    LogUtils.d("json数据", jsonList.get(hospitalWaitCallPosition));
                                                    mWebView.loadUrl("javascript:SetHistoryCall('" + jsonList.get(hospitalWaitCallPosition) + "')");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                                }

                            }
                            //如果是综合屏屏
                            else if ("2".equals(currTemplateId)) {
                                try {
                                    //如果窗口号未在选中的集合中,跳出
                                    if (!AppUtils.isCounterSelected(currSelectedCounterList, Msg.CounterNo)) {
                                        return;
                                    }

                                    // simon检查是否有重复叫号
                                    if (!AppUtils.checkHasReCall(call_info_list, Msg.TicketNo)) {
                                        //添加数据
                                        CallInfoEntity callInfo = new CallInfoEntity(AppUtils.formatCounterNo(Msg.CounterNo + "", counterNoLen), Msg.TicketNo, currName, consultingRoomAlias);
                                        //添加当前叫号
                                        call_info_list.add(0, callInfo);
                                        //如果数据为100条，移除最早的那一条
                                        if (call_info_list.size() == 100) {
                                            call_info_list.remove(99);
                                        }
                                    }

//                                    //添加数据
//                                    CallInfoEntity callInfo = new CallInfoEntity(AppUtils.formatCounterNo(Msg.CounterNo + "", counterNoLen), Msg.TicketNo, currName, consultingRoomAlias);
//                                    //删除重复叫号
//                                    AppUtils.delHasReCall(call_info_list, Msg.TicketNo);
//                                    //添加当前叫号
//                                    call_info_list.add(0, callInfo);
//                                    //如果数据为12条，移除最早的那一条
//                                    if (call_info_list.size() == 12) {
//                                        call_info_list.remove(11);
//                                    }
                                    //如果启用了模板
                                    if (isEnalbeCallModel) {
                                        //显示叫号信息
                                        showCallInfoList();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                                }
                            }
                            //如果是竖屏综合屏
                            else if ("8".equals(currTemplateId)) {
                                try {
                                    //如果窗口号未在选中的集合中,跳出
                                    if (!AppUtils.isCounterSelected(currSelectedCounterList, Msg.CounterNo)) {
                                        return;
                                    }
                                    //添加数据
                                    CallInfoEntity callInfo = new CallInfoEntity(AppUtils.formatCounterNo(Msg.CounterNo + "", counterNoLen), Msg.TicketNo, currName, consultingRoomAlias);
                                    //删除重复叫号
                                    AppUtils.delHasReCall(call_info_list, Msg.TicketNo);
                                    //添加当前叫号
                                    call_info_list.add(0, callInfo);
                                    //如果数据为12条，移除最早的那一条
                                    if (call_info_list.size() == 12) {
                                        call_info_list.remove(11);
                                    }
                                    //如果启用了模板
                                    if (isEnalbeCallModel) {
                                        //显示叫号信息
                                        //显示叫号
                                        ShowCallInfoFor8Template(mTemplateEntity);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                                }

                            }
                            //如果是单窗口屏
                            else if ("1".equals(currTemplateId) || "4".equals(currTemplateId) || "5".equals(currTemplateId)
                                    || "6".equals(currTemplateId) || "7".equals(currTemplateId)) {
                                //如果不是登录窗口号
                                if (SocketUtils.COUNTER_NO != Msg.CounterNo) {
                                    return;
                                }
                                //显示叫号信息
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if ("1".equals(currTemplateId) || "4".equals(currTemplateId)) {
                                                //当前呼叫票号
                                                mWebView.loadUrl("javascript:SetValue('CurrCallTicket','" + Msg.TicketNo + "')");
                                                //当前呼叫名称
                                                String c_name = TextUtils.isEmpty(currName) ? "\\&nbsp;" : currName;
                                                mWebView.loadUrl("javascript:SetValue('CurrCallName','" + c_name + "')");
                                                //等待呼叫票号
                                                String n_ticket = TextUtils.isEmpty(nextTicketNo) ? "\\&nbsp;" : nextTicketNo;
                                                mWebView.loadUrl("javascript:SetValue('NextCallTicket','" + n_ticket + "')");
                                                //等待呼叫名称
                                                String n_name = TextUtils.isEmpty(nextName) ? "\\&nbsp;" : nextName;
                                                mWebView.loadUrl("javascript:SetValue('NextCallName','" + n_name + "')");
                                            } else if ("5".equals(currTemplateId)) {
                                                //设置叫号
                                                String callInfo = "请" + Msg.TicketNo + "到" + (Msg.CounterNo < 10 ? "0" + Msg.CounterNo : Msg.CounterNo) + "号窗口办理";
                                                mWebView.loadUrl("javascript:SetValue('CallInfo','" + callInfo + "')");
                                            } else if ("6".equals(currTemplateId)) {
                                                String callInfo = "请" + Msg.TicketNo + "号";
                                                mWebView.loadUrl("javascript:SetValue('CallInfo','" + callInfo + "')");
                                            } else if ("7".equals(currTemplateId)) {
                                                String callInfo = "请" + Msg.TicketNo + "号到" + (Msg.CounterNo < 10 ? "0" + Msg.CounterNo : Msg.CounterNo) + "号窗口";
                                                mWebView.loadUrl("javascript:SetValue('CurrCallTicket','" + callInfo + "')");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                                        }
                                    }
                                });
                            }
                            //如果是安卓本地模板
                            else if ("0".equals(currTemplateId)) {
                                //如果不是登录窗口号
                                if (SocketUtils.COUNTER_NO != Msg.CounterNo) {
                                    return;
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //如果是安卓本地模板1
                                        if ("1".equals(local_template_id)) {
                                            //设置当前叫号
                                            String currStr = TextUtils.isEmpty(currName) ? Msg.TicketNo : currName + "(" + Msg.TicketNo + ")";
                                            tv_curr_call_v1.setText(currStr);
                                            //设置下一叫号
                                            String nextStr = TextUtils.isEmpty(nextName) ? nextTicketNo : nextName + "(" + nextTicketNo + ")";
                                            tv_wait_call_1_v1.setText(nextStr);
                                            //设置第三个叫号
                                            String twoStr = TextUtils.isEmpty(twoName) ? twoTicketNo : twoName + "(" + twoTicketNo + ")";
                                            tv_wait_call_2_v1.setText(twoStr);
                                        }

                                        //以下是隐藏名字中间字体,如果是安卓本地模板1
//                                        if ("1".equals(local_template_id)) {
//                                            //设置当前叫号
//                                            String currStr = TextUtils.isEmpty(currName) ? Msg.TicketNo : AppUtils.formatName(currName) + "(" + Msg.TicketNo + ")";
//                                            tv_curr_call_v1.setText(currStr);
//                                            //设置下一叫号
//                                            String nextStr = TextUtils.isEmpty(nextName) ? nextTicketNo : AppUtils.formatName(nextName) + "(" + nextTicketNo + ")";
//                                            tv_wait_call_1_v1.setText(nextStr);
//                                            //设置第二个叫号
//                                            String twoStr = TextUtils.isEmpty(twoName) ? twoTicketNo : AppUtils.formatName(twoName) + "(" + twoTicketNo + ")";
//                                            tv_wait_call_2_v1.setText(twoStr);
//                                        }
                                        //如果是本地模板2
                                        else if ("2".equals(local_template_id)) {
                                            //设置当前叫号
                                            tv_curr_call_h1.setText(Msg.TicketNo);
                                            //设置下一叫号
                                            tv_next_call_h1.setText(nextTicketNo);
                                        }
                                        //如果是本地模板3
                                        else if ("3".equals(local_template_id)) {
                                            //设置当前叫号
                                            String currStr = TextUtils.isEmpty(currName) ? Msg.TicketNo : currName + "(" + Msg.TicketNo + ")";
                                            tv_curr_call_v2.setText(currStr);

                                            //设置下一叫号
                                            String nextStr = TextUtils.isEmpty(nextName) ? nextTicketNo : nextName + "(" + nextTicketNo + ")";
                                            tv_wait_call_1_v2.setText(nextStr);

                                            //设置第二个叫号
                                            String twoStr = TextUtils.isEmpty(twoName) ? twoTicketNo : twoName + "(" + twoTicketNo + ")";
                                            tv_wait_call_2_v2.setText(twoStr);

                                            //设置第三个叫号
                                            String threeStr = TextUtils.isEmpty(threeName) ? threeTicketNo : threeName + "(" + threeTicketNo + ")";
                                            tv_wait_call_3_v2.setText(threeStr);

                                            //设置第四个叫号
                                            String fourStr = TextUtils.isEmpty(fourName) ? fourTicketNo : fourName + "(" + fourTicketNo + ")";
                                            tv_wait_call_4_v2.setText(fourStr);
                                        }
                                        //以下是隐藏名字中间字体,如果是安卓本地模板3
//                                        else if ("3".equals(local_template_id)) {
//                                            //设置当前叫号
//                                            String currStr = TextUtils.isEmpty(currName) ? Msg.TicketNo : AppUtils.formatName(currName) + "(" + Msg.TicketNo + ")";
//                                            tv_curr_call_v2.setText(currStr);
//
//                                            //设置下一叫号
//                                            String nextStr = TextUtils.isEmpty(nextName) ? nextTicketNo : AppUtils.formatName(nextName) + "(" + nextTicketNo + ")";
//                                            tv_wait_call_1_v2.setText(nextStr);
//
//                                            //设置第二个叫号
//                                            String twoStr = TextUtils.isEmpty(twoName) ? twoTicketNo : AppUtils.formatName(twoName) + "(" + twoTicketNo + ")";
//                                            tv_wait_call_2_v2.setText(twoStr);
//
//                                            //设置第三个叫号
//                                            String threeStr = TextUtils.isEmpty(threeName) ? threeTicketNo : AppUtils.formatName(threeName) + "(" + threeTicketNo + ")";
//                                            tv_wait_call_3_v2.setText(threeStr);
//
//                                            //设置第四个叫号
//                                            String fourStr = TextUtils.isEmpty(fourName) ? fourTicketNo : AppUtils.formatName(fourName) + "(" + fourTicketNo + ")";
//                                            tv_wait_call_4_v2.setText(fourStr);
//                                        }

                                        //如果是本地模板4
                                        else if ("4".equals(local_template_id)) {
                                            //设置当前叫号
                                            String currStr = TextUtils.isEmpty(currName) ? Msg.TicketNo : currName + "(" + Msg.TicketNo + ")";
                                            tv_curr_call_v3.setText(currStr);

                                            //设置下一叫号
                                            String nextStr = TextUtils.isEmpty(nextName) ? nextTicketNo : nextName + "(" + nextTicketNo + ")";
                                            tv_wait_call_1_v3.setText(nextStr);

                                            //设置第二个叫号
                                            String twoStr = TextUtils.isEmpty(twoName) ? twoTicketNo : twoName + "(" + twoTicketNo + ")";
                                            tv_wait_call_2_v3.setText(twoStr);
                                        }
                                        //如果是本地模板5
                                        else if ("5".equals(local_template_id)) {
                                            //设置当前叫号
                                            String currStr = TextUtils.isEmpty(currName) ? Msg.TicketNo : currName + "(" + Msg.TicketNo + ")";
                                            tv_curr_call_t5.setText(currStr);

                                            //设置下一叫号
                                            String nextStr = TextUtils.isEmpty(nextName) ? nextTicketNo : nextName + "(" + nextTicketNo + ")";
                                            tv_next_call_t5.setText(nextStr);
                                        }
                                        //如果是本地模板6
                                        else if ("6".equals(local_template_id)) {
                                            //设置当前叫号
                                            String currStr = TextUtils.isEmpty(currName) ? Msg.TicketNo : currName + "-" + Msg.TicketNo;
                                            tv_curr_call_t6.setText(currStr);

                                            //设置下一叫号
                                            String nextStr = TextUtils.isEmpty(nextName) ? nextTicketNo : nextName + "-" + nextTicketNo;
                                            tv_next_call_t6.setText(nextStr);
                                        }
                                        //如果是本地模板7
                                        else if ("7".equals(local_template_id)) {
                                            //设置当前叫号
                                            String currStr = TextUtils.isEmpty(currName) ? Msg.TicketNo : currName + "-" + Msg.TicketNo;
                                            tv_curr_call_t7.setText(currStr);

                                            //设置下一叫号
                                            String nextStr = TextUtils.isEmpty(nextName) ? nextTicketNo : nextName + "-" + nextTicketNo;
                                            tv_next_call_t7.setText(nextStr);
                                        }
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                    }

                    break;

                case Constants.QQS_TVD_SENDSTART://事件：开始发送文件
//                    Log.e("send====start", Msg.toString());
                    try {
                        LogUtils.d("receiveMessage", "开始发送文件" + Msg.toString());
                        LogUtils.writeLogtoFile("服务器开始发送文件", Msg.toString());
                        isFile = true;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //标识正在根更新文件
                                    isUpdateFile = true;
                                    //停止播放对媒体
                                    startOrStopRotationPic(mediaDirPath, picSwitchTime, mediaWidth, mediaHeight, false);
                                    //显示加载进度条
                                    showProgressDialog();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                    }

                    break;

                case Constants.QQS_TVD_SENDEND:  //事件：结束发送文件
//                    Log.e("send=======end", Msg.toString());
                    try {
                        LogUtils.d("receiveMessage", "结束发送文件" + Msg.toString());
                        LogUtils.writeLogtoFile("服务器结束发送文件", Msg.toString());
                        isFile = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //标识结束更新文件
                                    isUpdateFile = false;
                                    //如果是改变了ip或窗口号
                                    if (!SocketUtils.SERCIVER_IP.equals(SocketUtils.SERCIVER_IP_OLD)) {
                                        SocketUtils.SERCIVER_IP_OLD = SocketUtils.SERCIVER_IP;
                                    }
                                    //如果是调试,才打印
                                    if (LogUtils.isDeBug) {
                                        for (String key : fileMaps.keySet()) {
                                            LogUtils.d("receiveMessage", "key=" + key + ",值=" + fileMaps.get(key));
                                        }
                                    }
                                    //html先关文件路径
                                    final String htmlFilePath = root_path + "/Config/" + mTemplateEntity.getFileListDirectory();
                                    FileUtils.getFileInfoList(mediaDirPath, htmlFilePath, new OnLoadFileListener<Map<String, ArrayList<MediaEntity>>>() {
                                        @Override
                                        public void onLoadFile(Map<String, ArrayList<MediaEntity>> map) {
                                            try {
                                                String reqFiles_reReq = "";
                                                //如果存在多媒体
                                                if (Constants.MEDIA_HAS_TYPE_YES.equals(mTemplateEntity.getMediaType())) {
                                                    //多媒体文件集合
                                                    ArrayList<MediaEntity> mediaList = map.get(Constants.KEY_MEDIA);
                                                    //多媒体个数
                                                    int m_count = mediaList.size();
                                                    LogUtils.d("receiveMessage", "多媒体文件个数实际下载个数=" + m_count + ",应下载个数=" + media_count);
                                                    //如果多媒体的个数不一致
                                                    if ((media_count != 0 && m_count != media_count) || FileUtils.isHasDownErrorFile(mediaList, fileMaps)) {
                                                        reqFiles_reReq += "[Media:" + mediaDirectory + "]";
                                                        //清空当前所有的媒体
                                                        File file2 = new File(mediaDirPath);
                                                        FileUtils.deleteFile(file2);
                                                    }
                                                }

                                                //获取模板界面html文件路径
                                                String htmlMianFilePath = configDirPath + "/" + bgName;
                                                File htmlMianFile = new File(htmlMianFilePath);
                                                //获取html模板文件大小
                                                long htmlMianFileSize = FileUtils.getFileLength(htmlMianFilePath);
                                                LogUtils.d("receiveMessage", "Html模板文件下载大小=" + htmlMianFileSize + ",应下载大小=" + fileMaps.get(bgName));
                                                if (!htmlMianFile.exists() || (null != fileMaps.get(bgName) && htmlMianFileSize != fileMaps.get(bgName))) {
                                                    LogUtils.d("receiveMessage", "模板Html文件不存在或大小不一致");
                                                    reqFiles_reReq += "[Bg:" + bgName + "]";
                                                }

                                                //Html相关文件集合
                                                ArrayList<MediaEntity> htmlFileList = map.get(Constants.KEY_HTML_FILE);
                                                int h_count = htmlFileList.size();
                                                LogUtils.d("receiveMessage", "Html文件个数实际下载个数=" + h_count + ",应下载个数=" + html_file_count);
                                                //如果html文件个数不一致
                                                if (reqFiles.contains("[BgFileList:") && ((html_file_count != 0 && html_file_count != h_count) || FileUtils.isHasDownErrorFile(htmlFileList, fileMaps))) {
                                                    reqFiles_reReq += "[BgFileList:" + mTemplateEntity.getFileListDirectory() + "]";
                                                    //清空当前所有的html相关文件
                                                    File file = new File(htmlFilePath);
                                                    FileUtils.deleteFile(file);
                                                }

                                                //检查更新包的大小
                                                if (reqFiles.contains("[Update]") || reqFiles.contains("[All]")) {
                                                    File updateFile = new File(updateFilePath);
                                                    long updateFileSize = FileUtils.getFileLength(updateFilePath);
                                                    LogUtils.d("receiveMessage", "更新文件下载大小=" + updateFileSize + ",应下载大小=" + fileMaps.get("update.zip"));
                                                    if (!updateFile.exists() || (null != fileMaps.get("update.zip") && updateFileSize != fileMaps.get("update.zip"))) {
                                                        reqFiles_reReq += "[Update]";
                                                        //删除未下载完整的更新文件
                                                        FileUtils.deleteFile(updateFile);
                                                        //请求版本更新次数加1
                                                        updateVersionNum++;
                                                    }
                                                }

                                                //如果请求了三次版本更新文件
                                                if (updateVersionNum == 3) {
                                                    //清空版本更新请求次数
                                                    updateVersionNum = 0;
                                                    reqFiles_reReq.replace("[Update]", "");
                                                }

                                                //如果需要重新请求下载
                                                if (!TextUtils.isEmpty(reqFiles_reReq)) {
                                                    //重新发送多媒体请求
                                                    SocketMsg Msg = new SocketMsg();
                                                    Msg.MsgType = Constants.QQS_TVD_REQUESTFILE;
                                                    Msg.Arg3 = reqFiles_reReq;
                                                    sendMessage(Msg);
                                                    LogUtils.writeLogtoFile("重新发送多媒体或html请求，发送文件", Msg.toString());
                                                } else {
                                                    //清空版本更新请求次数
                                                    updateVersionNum = 0;
                                                    //如果更新的配置文件不为空
                                                    if (settingEntity_new != null) {
                                                        //保存配置文件
                                                        SPUtils.put(mContext, SPUtils.SETTINGS_ENTITY, new Gson().toJson(settingEntity_new));
                                                        LogUtils.d("receiveMessage", "配置文件不为空，设置默认参数");
                                                        //设置默认参数
                                                        setDefaultParam(settingEntity_new);
                                                    } else {
                                                        //关闭加载进度条
                                                        dismissProgressDialog();
                                                        //更新配置,解决当最新配置文件为空时不解压缩更新
                                                        upgrade();
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                    }

                    break;

                case Constants.QQS_TVD_SENDFILE://服务器发送文件
//                    Log.e("send======file", Msg.toString());
                    try {
                        LogUtils.d("receiveMessage", "服务器发送文件" + Msg.toString());
                        LogUtils.writeLogtoFile("服务器发送文件", Msg.toString());
                        isFile = true;
                        //标识为创建流
                        isCreateStream = false;
                        //文件目录
                        fileDir = Msg.Arg3;
                        //将接收长度置空
                        reciverLength = 0;
                        //文件长度
                        fileLength = Msg.Arg1;
                        //文件名
                        fileName = Msg.Arg4;
                        fileMaps.put(fileName, fileLength);
                        if ("setting.xml".equals(fileName)) {
                            dismissProgressDialog();
                        }
                        //设置更新进度
                        setProgress(fileName, reciverLength, fileLength);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                    }

                    break;

                case Constants.QQS_TVD_USERINFO://服务器发送员工信息
                    LogUtils.d("receiveMessage", "服务器发送员工信息" + Msg.toString());
                    LogUtils.writeLogtoFile("服务器发送员工信息", Msg.toString());
//                    Log.e("emp====info", Msg.toString());
                    try {//[1][张三][大唐经理][0][a]
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (!TextUtils.isEmpty(Msg.Arg3)) {
                                        String[] userInfo = Msg.Arg3.substring(1, Msg.Arg3.length() - 1).split("]\\[");
                                        //如果是安卓本地模板
                                        if ("0".equals(currTemplateId)) {
                                            //员工工号
                                            staffId = TextUtils.isEmpty(userInfo[0]) ? "" : userInfo[0];
                                            //员工名称
                                            staffName = TextUtils.isEmpty(userInfo[1]) ? "" : userInfo[1];
                                            //员工职称
                                            staffTitle = TextUtils.isEmpty(userInfo[2]) ? "" : userInfo[2];
                                        } else {
                                            //员工工号
                                            staffId = TextUtils.isEmpty(userInfo[0]) ? "\\&nbsp;" : userInfo[0];
                                            //员工名称
                                            staffName = TextUtils.isEmpty(userInfo[1]) ? "\\&nbsp;" : userInfo[1];
                                            //员工职称
                                            staffTitle = TextUtils.isEmpty(userInfo[2]) ? "\\&nbsp;" : userInfo[2];
                                        }
                                        //员工星级
                                        star = TextUtils.isEmpty(userInfo[3]) ? -1 : Integer.parseInt(userInfo[3]);
                                        //是否有头像
                                        isHasHeadPic = !TextUtils.isEmpty(userInfo[4]);

                                        //如果是模板4或模板5,才有员工信息
                                        if ("4".equals(currTemplateId) || "5".equals(currTemplateId)) {
                                            //设置员工名称
                                            mWebView.loadUrl("javascript:SetValue('StaffName','" + staffName + "')");
                                            //设置员工工号
                                            mWebView.loadUrl("javascript:SetValue('StaffNum','" + staffId + "')");
                                            //设置员工职称
                                            if ("4".equals(currTemplateId) || "5".equals(currTemplateId)) {
                                                mWebView.loadUrl("javascript:SetValue('StaffTitle','" + staffTitle + "')");
                                            }
                                            //如果没有头像
                                            if (!isHasHeadPic) {
                                                //清除头像文件
                                                FileUtils.deleteFile(new File(headFilePath));
                                                if ("4".equals(currTemplateId) || "5".equals(currTemplateId)) {
                                                    //iv_head.setImageResource(R.mipmap.person);
                                                    mWebView.loadUrl("javascript:SetValue('StaffHeadPath','StaffHorizontal1/default-head.png')");
                                                } else if ("5".equals(currTemplateId)) {
                                                    iv_head.setImageResource(R.mipmap.jingcha_default);
                                                }
                                            }
                                        }
                                        //如果是7号模板
                                        else if ("7".equals(currTemplateId)) {
                                            //设置业务员姓名
                                            mWebView.loadUrl("javascript:SetValue('StaffName','" + staffName + "')");
                                            //设置员工职称
                                            mWebView.loadUrl("javascript:SetValue('StaffTitle','" + staffTitle + "')");
                                            //如果没有头像
                                            if (!isHasHeadPic) {
                                                //清除头像文件
                                                FileUtils.deleteFile(new File(headFilePath));
                                                iv_head.setImageResource(R.mipmap.person);
                                            }
                                        }
                                        //如果是安作自定义模板
                                        else if ("0".equals(currTemplateId)) {
                                            //如果是本地模板1
                                            if ("1".equals(local_template_id)) {
                                                //设置医生姓名
                                                tv_doc_name_v1.setText(staffName.replace("\\&nbsp;", ""));
                                                //设置医生职称
                                                tv_doc_job_v1.setText(staffTitle.replace("\\&nbsp;", ""));
                                                //如果没有头像
                                                if (!isHasHeadPic) {
                                                    //清除头像文件
                                                    FileUtils.deleteFile(new File(headFilePath));
                                                    iv_doc_head_v1.setImageResource(R.mipmap.person);
                                                }
                                            }
                                            //如果是本地模板2
                                            else if ("2".equals(local_template_id)) {
                                                //设置员工姓名
                                                tv_name_h1.setText(staffName.replace("\\&nbsp;", ""));
                                                //设置员工工号
                                                tv_job_no_h1.setText(staffId.replace("\\&nbsp;", ""));
                                                //如果没有头像
                                                if (!isHasHeadPic) {
                                                    //清除头像文件
                                                    FileUtils.deleteFile(new File(headFilePath));
                                                    iv_head_h1.setImageResource(R.mipmap.person);
                                                }
                                            }
                                            //如果是本地模板3
                                            else if ("3".equals(local_template_id)) {
                                                //设置医生姓名
                                                tv_doc_name_v2.setText(staffName.replace("\\&nbsp;", ""));
                                                //设置医生职称
                                                tv_doc_job_v2.setText(staffTitle.replace("\\&nbsp;", ""));
                                                //如果没有头像
                                                if (!isHasHeadPic) {
                                                    //清除头像文件
                                                    FileUtils.deleteFile(new File(headFilePath));
                                                    iv_doc_head_v2.setImageResource(R.mipmap.person);
                                                }
                                            }
                                            //如果是本地模板4
                                            else if ("4".equals(local_template_id)) {
                                                //设置医生姓名
                                                tv_doc_name_v3.setText(staffName.replace("\\&nbsp;", ""));
                                                //设置医生职称
                                                tv_doc_job_v3.setText(staffTitle.replace("\\&nbsp;", ""));
                                                //如果没有头像
                                                if (!isHasHeadPic) {
                                                    //清除头像文件
                                                    FileUtils.deleteFile(new File(headFilePath));
                                                    iv_doc_head_v3.setImageResource(R.mipmap.person);
                                                }
                                            }

                                            //如果是本地模板5
                                            else if ("5".equals(local_template_id)) {
                                                //设置工号
                                                tv_job_no_t5.setText(staffId.replace("\\&nbsp;", ""));
                                                //设置职称
                                                tv_job_title_t5.setText(staffTitle.replace("\\&nbsp;", ""));
                                                //如果没有头像
                                                if (!isHasHeadPic) {
                                                    //清除头像文件
                                                    FileUtils.deleteFile(new File(headFilePath));
                                                    iv_head_t5.setImageResource(R.mipmap.person);
                                                }
                                            }
                                            //如果是本地模板6
                                            else if ("6".equals(local_template_id)) {
                                                //设置姓名
                                                tv_doc_name_t6.setText(staffName.replace("\\&nbsp;", ""));
                                                //设置职称
                                                tv_job_title_t6.setText(staffTitle.replace("\\&nbsp;", ""));
                                                //如果没有头像
                                                if (!isHasHeadPic) {
                                                    //清除头像文件
                                                    FileUtils.deleteFile(new File(headFilePath));
                                                    iv_head_t6.setImageResource(R.mipmap.person);
                                                }
                                            }
                                            //如果是本地模板7
                                            else if ("7".equals(local_template_id)) {
                                                //设置姓名
                                                tv_doc_name_t7.setText(staffName.replace("\\&nbsp;", ""));
                                                //设置职称
                                                tv_job_title_t7.setText(staffTitle.replace("\\&nbsp;", ""));
                                                //如果没有头像
                                                if (!isHasHeadPic) {
                                                    //清除头像文件
                                                    FileUtils.deleteFile(new File(headFilePath));
                                                    iv_head_t7.setImageResource(R.mipmap.person);
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                    }

                    break;

                case Constants.QQS_TVD_PAUSE_SERVICE://服务器发送暂停服务或取消暂停服务消息
                    LogUtils.d("receiveMessage", "服务器发送暂停服务或取消暂停服务消息" + Msg.toString());
                    LogUtils.writeLogtoFile("服务器发送暂停服务或取消暂停服务消息", Msg.toString());
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //如果模板1或模板4或模板5才有暂停服务
                                    if ("1".equals(currTemplateId)
                                            || "4".equals(currTemplateId)
                                            || "5".equals(currTemplateId)
                                            || "6".equals(currTemplateId)
                                            || "7".equals(currTemplateId)
                                            || "0".equals(currTemplateId)) {
                                        //暂停服务
                                        if (Msg.Arg1 == 1) {
                                            if ("1".equals(currTemplateId) || "4".equals(currTemplateId)) {
                                                //当前呼叫票号
                                                mWebView.loadUrl("javascript:SetValue('CurrCallTicket','" + "暂停" + "')");
                                                //当前呼叫名称
                                                mWebView.loadUrl("javascript:SetValue('CurrCallName','" + "服务" + "')");
                                                //等待呼叫票号
                                                mWebView.loadUrl("javascript:SetValue('NextCallTicket','" + "\\&nbsp;" + "')");
                                                //等待呼叫名称
                                                mWebView.loadUrl("javascript:SetValue('NextCallName','" + "\\&nbsp;" + "')");
                                            } else if ("5".equals(currTemplateId)) {
                                                mWebView.loadUrl("javascript:SetValue('CallInfo','暂停服务')");
                                            } else if ("6".equals(currTemplateId)) {
                                                mWebView.loadUrl("javascript:SetValue('CallInfo','暂停服务')");
                                            } else if ("7".equals(currTemplateId)) {
                                                mWebView.loadUrl("javascript:SetValue('CurrCallTicket','暂停服务')");
                                            }
                                            //如果是本地模板
                                            else if ("0".equals(currTemplateId)) {
                                                //如果是本地模板1
                                                if ("1".equals(local_template_id)) {
                                                    tv_curr_call_v1.setText("暂停服务");
                                                    tv_wait_call_1_v1.setText("");
                                                    tv_wait_call_2_v1.setText("");
                                                }
                                                //如果是本地模板2
                                                else if ("2".equals(local_template_id)) {
                                                    tv_curr_call_h1.setText("暂停");
                                                    tv_next_call_h1.setText("服务");
                                                }
                                                //如果是本地模板3
                                                else if ("3".equals(local_template_id)) {
                                                    tv_curr_call_v2.setText("暂停服务");
                                                    tv_wait_call_1_v2.setText("");
                                                    tv_wait_call_2_v2.setText("");
                                                    tv_wait_call_3_v2.setText("");
                                                    tv_wait_call_4_v2.setText("");
                                                }
                                                //如果是本地模板4
                                                else if ("4".equals(local_template_id)) {
                                                    tv_curr_call_v3.setText("暂停服务");
                                                    tv_wait_call_1_v3.setText("");
                                                    tv_wait_call_2_v3.setText("");
                                                }
                                                //如果是本地模板5
                                                else if ("5".equals(local_template_id)) {
                                                    tv_curr_call_t5.setText("暂停服务");
                                                    tv_next_call_t5.setText("");
                                                }
                                                //如果是本地模板6
                                                else if ("6".equals(local_template_id)) {
                                                    tv_curr_call_t6.setText("暂停服务");
                                                    tv_next_call_t6.setText("");
                                                }
                                                //如果是本地模板7
                                                else if ("7".equals(local_template_id)) {
                                                    tv_curr_call_t7.setText("暂停服务");
                                                    tv_next_call_t7.setText("");
                                                }
                                            }
                                        }
                                        //取消暂停服务
                                        else if (Msg.Arg1 == 0) {
                                            //清除之前的叫号信息,如果1号模板或4号模板
                                            if ("1".equals(currTemplateId) || "4".equals(currTemplateId)) {
                                                //当前呼叫票号
                                                mWebView.loadUrl("javascript:SetValue('CurrCallTicket','" + "\\&nbsp;" + "')");
                                                //当前呼叫名称
                                                mWebView.loadUrl("javascript:SetValue('CurrCallName','" + "\\&nbsp;" + "')");
                                                //等待呼叫票号
                                                mWebView.loadUrl("javascript:SetValue('NextCallTicket','" + "\\&nbsp;" + "')");
                                                //等待呼叫名称
                                                mWebView.loadUrl("javascript:SetValue('NextCallName','" + "\\&nbsp;" + "')");
                                            } else if ("5".equals(currTemplateId)) {
                                                mWebView.loadUrl("javascript:SetValue('CallInfo','" + "\\&nbsp;" + "')");
                                            } else if ("6".equals(currTemplateId)) {
                                                mWebView.loadUrl("javascript:SetValue('CallInfo','" + "\\&nbsp;" + "')");
                                            } else if ("7".equals(currTemplateId)) {
                                                mWebView.loadUrl("javascript:SetValue('CurrCallTicket','" + "\\&nbsp;" + "')");
                                            }
                                            //如果是本地模板
                                            else if ("0".equals(currTemplateId)) {
                                                //如果是本地模板1
                                                if ("1".equals(local_template_id)) {
                                                    tv_curr_call_v1.setText("");
                                                    tv_wait_call_1_v1.setText("");
                                                    tv_wait_call_2_v1.setText("");
                                                }
                                                //如果是本地模板2
                                                else if ("2".equals(local_template_id)) {
                                                    tv_curr_call_h1.setText("");
                                                    tv_next_call_h1.setText("");
                                                }
                                                //如果是本地模板3
                                                else if ("3".equals(local_template_id)) {
                                                    tv_curr_call_v2.setText("");
                                                    tv_wait_call_1_v2.setText("");
                                                    tv_wait_call_2_v2.setText("");
                                                    tv_wait_call_3_v2.setText("");
                                                    tv_wait_call_4_v2.setText("");
                                                }
                                                //如果是本地模板4
                                                else if ("4".equals(local_template_id)) {
                                                    tv_curr_call_v2.setText("");
                                                    tv_wait_call_1_v2.setText("");
                                                    tv_wait_call_2_v2.setText("");
                                                }
                                                //如果是本地模板5
                                                else if ("5".equals(local_template_id)) {
                                                    tv_curr_call_t5.setText("");
                                                    tv_next_call_t5.setText("");
                                                }
                                                //如果是本地模板6
                                                else if ("6".equals(local_template_id)) {
                                                    tv_curr_call_t6.setText("");
                                                    tv_next_call_t6.setText("");
                                                }
                                                //如果是本地模板7
                                                else if ("7".equals(local_template_id)) {
                                                    tv_curr_call_t7.setText("");
                                                    tv_next_call_t7.setText("");
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                                }

                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case Constants.QQS_TVD_FORCED_OFFLINE://被踢下线
                    LogUtils.writeLogtoFile("设备被强制退出", Msg.toString());
                    LogUtils.d("receiveMessage", "被强制下线" + Msg.toString());
                    //标识是被强制退出
                    isForcedOut = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //如果显示了加载进度条，关闭
                            dismissProgressDialog();
                            //断开连接
                            if (mClient != null) {
                                mClient.disconnect();
                            }
                            //标识默认没有头像
                            isHasHeadPic = false;
                            //标识未在更新
                            isUpdateFile = false;
                            //关闭定时检查配置文件定时器
                            if (checkSettingFileTmer != null) {
                                checkSettingFileTmer.cancel();
                                checkSettingFileTmer = null;
                            }
                            //进入Ip设置页面
                            enterSettingIp("设备被强制退出，请重新连接");
                        }
                    });
                case Constants.QQS_TVD_HEARTBEAT:
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }

    /**
     * 清空员工信息
     */
    private void cleanEmployeeInfo() {
        try {
            //工号清空
            staffId = "";
            //姓名清空*/
            staffName = "";
            //职称清空*/
            staffTitle = "";
            //清空星级
            star = -1;
            //清除头像文件
            FileUtils.deleteFile(new File(headFilePath));
            //设置默认没有头像
            isHasHeadPic = false;
            //设置默认头像
            if ("5".equals(currTemplateId)) {
                iv_head.setImageResource(R.mipmap.jingcha_default);
            }
            //如果是安卓本地模板
            else if ("0".equals(currTemplateId)) {
                //如果安卓本地模板1
                if ("1".equals(local_template_id)) {
                    //设置医生姓名为空
                    tv_doc_name_v1.setText("");
                    //设置职称为空
                    tv_doc_job_v1.setText("");
                    //设置正在就诊为空
                    tv_curr_call_v1.setText("");
                    //设置等候就诊为空
                    tv_wait_call_1_v1.setText("");
                    tv_wait_call_2_v1.setText("");
                    iv_doc_head_v1.setImageResource(R.mipmap.person);
                }
                //如果是本地模板2
                else if ("2".equals(local_template_id)) {
                    //清空员工姓名
                    tv_name_h1.setText("");
                    //清空工号
                    tv_job_no_h1.setText("");
                    //清空当前叫号
                    tv_curr_call_h1.setText("");
                    //清空下一叫号
                    tv_next_call_h1.setText("");
                    //设置显示默认头像
                    iv_head_h1.setImageResource(R.mipmap.person);
                }
                //如果是本地模板3
                else if ("3".equals(local_template_id)) {
                    //设置医生姓名为空
                    tv_doc_name_v2.setText("");
                    //设置职称为空
                    tv_doc_job_v2.setText("");
                    //设置正在就诊为空
                    tv_curr_call_v2.setText("");
                    //设置等候就诊为空
                    tv_wait_call_1_v2.setText("");
                    tv_wait_call_2_v2.setText("");
                    tv_wait_call_3_v2.setText("");
                    tv_wait_call_4_v2.setText("");
                    iv_doc_head_v2.setImageResource(R.mipmap.person);
                }
                //如果是本地模板4
                else if ("4".equals(local_template_id)) {
                    //设置医生姓名为空
                    tv_doc_name_v3.setText("");
                    //设置职称为空
                    tv_doc_job_v3.setText("");
                    //设置正在就诊为空
                    tv_curr_call_v3.setText("");
                    //设置等候就诊为空
                    tv_wait_call_1_v3.setText("");
                    tv_wait_call_2_v3.setText("");
                    iv_doc_head_v3.setImageResource(R.mipmap.person);
                }
                //如果是本地模板5
                else if ("5".equals(local_template_id)) {
                    //设置工号为空
                    tv_job_no_t5.setText("");
                    //设置职称为空
                    tv_job_title_t5.setText("");
                    //设置正在就诊为空
                    tv_curr_call_t5.setText("");
                    //设置等候就诊为空
                    tv_next_call_t5.setText("");
                    iv_head_t5.setImageResource(R.mipmap.person);
                }
                //如果是本地模板6
                else if ("6".equals(local_template_id)) {
                    //设置医生姓名为空
                    tv_doc_name_t6.setText("");
                    //设置职称为空
                    tv_job_title_t6.setText("");
                    //设置正在就诊为空
                    tv_curr_call_t6.setText("");
                    //设置等候就诊为空
                    tv_next_call_t6.setText("");
                    iv_head_t6.setImageResource(R.mipmap.person);
                }
                //如果是本地模板7
                else if ("7".equals(local_template_id)) {
                    //设置医生姓名为空
                    tv_doc_name_t7.setText("");
                    //设置职称为空
                    tv_job_title_t7.setText("");
                    //设置正在就诊为空
                    tv_curr_call_t7.setText("");
                    //设置等候就诊为空
                    tv_next_call_t7.setText("");
                    iv_head_t7.setImageResource(R.mipmap.person);
                }
            } else {
                iv_head.setImageResource(R.mipmap.person);
            }

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }

    /**
     * 显示当前叫号信息
     */
    private void showCallInfoList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<CallInfoEntity> call_info_list1 = null;
                    if (call_info_list.size() > 12) {
                        call_info_list1 = new ArrayList(call_info_list.subList(0, 12));
                    } else {
                        call_info_list1 = call_info_list;
                    }

                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("[{");
                    stringBuffer.append("\"count" + "\":\"" + call_info_list1.size() + "\",");
                    stringBuffer.append("\"value\":\"");
                    for (int i = 0; i < call_info_list1.size(); i++) {
                        CallInfoEntity entity = call_info_list1.get(i);
                        String callStr = callModel_text.replace("{TicketNo.}", entity.getTicketNo())
                                .replace("{CounterNo.}", entity.getCounterNo())
                                .replace("{Name}", entity.getName())
                                .replace("{Counter Alias}", entity.getConsultingRoomName());
                        stringBuffer.append(callStr);
                        if (i < call_info_list1.size() - 1) {
                            stringBuffer.append(",");
                        }
                    }
                    stringBuffer.append("\"}]");

                    LogUtils.d("传值", stringBuffer.toString());
                    mWebView.loadUrl("javascript:SetCallInfo('" + stringBuffer.toString() + "')");

                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                }
            }
        });

    }


    /**
     * 获取弹窗的文字信息
     *
     * @param entity               叫号信息对象
     * @param callPopupWindowModel 叫号弹窗模板
     * @return 返回完整的叫号信息
     */
    private String getCallPopupWindowText(CallInfoEntity entity, String callPopupWindowModel) {
        String callText = "";
        try {
            if (entity != null && !TextUtils.isEmpty(callPopupWindowModel)) {
                callText = callPopupWindowModel.replace("{TicketNo.}", entity.getTicketNo())
                        .replace("{CounterNo.}", entity.getCounterNo())
                        .replace("{Name}", entity.getName())
                        .replace("{Counter Alias}", entity.getConsultingRoomName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return callText;
    }


    /**
     * 叫号弹窗
     *
     * @param context
     * @param templateEntity 模板对象
     * @param callInfoEntity 当前叫号信息对象
     */
    private Dialog currCallDialog;
    private CountDownTimer timer = null;

    private void showcurrCallDialog(final Context context, final TemplateEntity templateEntity,
                                    final CallInfoEntity callInfoEntity) {
        //如果需要叫号弹窗
        if ("1".equals(templateEntity.getCallPopupWindow_enable())) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (currCallDialog == null) {
                            currCallDialog = new Dialog(context, R.style.call_dialog_style_in_middle);
                        }
                        //隐藏home导航栏
                        hideNavigation(currCallDialog);
                        currCallDialog.show();
                        Window window = currCallDialog.getWindow();
                        View view = LayoutInflater.from(context).inflate(R.layout.dialog_curr_call, null);
                        window.setContentView(view);
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        //当前叫号信息
                        TextView tv_call = (TextView) view.findViewById(R.id.tv_call);
                        //设置叫号信息
                        tv_call.setText(getCallPopupWindowText(callInfoEntity, templateEntity.getCallPopupWindow_text()));
                        //倒计时开始
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        int countTime = templateEntity.getCallPopupWindow_duration();
                        if (countTime <= 0) {
                            countTime = 15;
                        }
                        timer = new CountDownTimer(countTime * 1000, 1000) {

                            @Override
                            public void onTick(long millisUntilFinished) {
                            }

                            @Override
                            public void onFinish() {
                                if (currCallDialog != null) {
                                    currCallDialog.dismiss();
                                }
                            }

                        }.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.writeLogtoFile("异常日志", "叫号弹窗," + Log.getStackTraceString(e));
                    }
                }
            });
        }

    }


    /**
     * 设置默认参数
     *
     * @param settingEntity 配置文件实体
     */
    private void setDefaultParam(final AppSettingEntity settingEntity) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    if (settingEntity != null) {
                        /**当前版本号*/
                        version = settingEntity.getVersion();
                        /**自动关机参数*/
                        //获取是否自动关机标识
                        isAutoShutdown = "1".equals(settingEntity.getAutoShutdownEnable()) ? true : false;
                        //获取关机时间
                        autoShutdown_time = settingEntity.getAutoShutdownTime();
                        // 如果需要关机,暂时先注释掉不使用
//                        if (isAutoShutdown) {
//                            if (timeThread != null && timeThread.isAlive()) {
//                                timeThread.stop();
//                            }
//                            timeThread = new TimeThread();
//                            timeThread.start();
//                        } else {
//                            //如果已经启动定时锁屏，取消
//                            if (isAutoShutDownStart) {
//                                lockSreenTimer.cancel();
//                                lockSreenTimer = null;
//                                //标识已关闭自动关机
//                                isAutoShutDownStart = false;
//                            }
//                        }

                        /**退出密码参数*/
                        //获取退出密码
                        if (!TextUtils.isEmpty(settingEntity.getQuitPassword())) {
                            quitPassword = settingEntity.getQuitPassword();
                        }
                        //是否使用退出密码标识
                        isUseQuitPassword = "1".equals(settingEntity.getQuitPasswordEnable()) ? true : false;

                        //窗口对象
                        if (mCounterEntity == null) {
                            mCounterEntity = AppUtils.getCounterSettingEntity(settingEntity, SocketUtils.COUNTER_NO + "");
                        }
                        //如果不为安卓本地模板,获取模板对象
                        if (mTemplateEntity == null && !"0".equals(currTemplateId)) {
                            mTemplateEntity = AppUtils.getTemplateEntityFromTemplateId(settingEntity, currTemplateId);
                            //设置屏幕方向
                            setScreenOrientation(mTemplateEntity);
                        }

                        //安卓本地模板
                        if (mCounterEntity != null && "0".equals(currTemplateId)) {
                            //设置本地叫号为空
                            setEmptyLocalTempCallInfo();
                            tv_marquee_text_h1.stopScroll();
                            tv_marquee_text_v1.stopScroll();
                            tv_marquee_text_v2.stopScroll();
                            tv_marquee_text_v3.stopScroll();
                            tv_marquee_text_t5.stopScroll();
                            tv_marquee_text_t6.stopScroll();
                            tv_marquee_text_t7.stopScroll();
                            //设置本地模板1隐藏
                            llyt_local_hospital_template_1.setVisibility(View.GONE);
                            //设置本地模板2隐藏
                            llyt_local_template_2.setVisibility(View.GONE);
                            //设置本地模板3隐藏
                            llyt_local_template_3.setVisibility(View.GONE);
                            //设置本地模板4隐藏
                            llyt_local_template_4.setVisibility(View.GONE);
                            //设置本地模板5隐藏
                            llyt_local_template_5.setVisibility(View.GONE);
                            //设置本地模板6隐藏
                            llyt_local_template_6.setVisibility(View.GONE);
                            //设置本地模板7隐藏
                            llyt_local_template_7.setVisibility(View.GONE);
                            //如果是本地模板1
                            if ("1".equals(local_template_id)) {
                                //设置竖屏
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                //设置安卓本地模板1显示
                                llyt_local_hospital_template_1.setVisibility(View.VISIBLE);
                                //启动日期时间定时器
                                startDateTimer();
                                //设置医院标题
                                tv_hospital_name_v1.setText(mCounterEntity.getTitle());
                                //设置科室名称
                                tv_service_name_v1.setText(mCounterEntity.getServiceName());
                                //设置显示诊室名称
                                tv_room_name_v1.setText(mCounterEntity.getCounterAlias());
                                //设置医生姓名
                                tv_doc_name_v1.setText(staffName);
                                //设置职称为空
                                tv_doc_job_v1.setText(staffTitle);
                                //设置显示滚动文字
                                tv_marquee_text_v1.setMyContext(mCounterEntity.getTipText());
                                if (!TextUtils.isEmpty(mCounterEntity.getTipText())) {
                                    tv_marquee_text_v1.startScroll();
                                    tv_marquee_text_v1.setL2r(false);
                                    tv_marquee_text_v1.setMySpeed(3);
                                }
                                //如果有头像
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isHasHeadPic) {
                                            ImageManager.getInstance(mContext).loadImageForScal(mContext, new File(headFilePath), iv_doc_head_v1, R.mipmap.person);
                                        } else {
                                            iv_doc_head_v1.setImageResource(R.mipmap.person);
                                        }
                                    }
                                }, 500);
                            }
                            //如果是本地模板2
                            else if ("2".equals(local_template_id)) {
                                //设置横屏
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                                //设置安卓本地模板2显示
                                llyt_local_template_2.setVisibility(View.VISIBLE);
                                //设置标题
                                tv_title_h1.setText(mCounterEntity.getTitle());
                                //设置窗口号
                                tv_counter_no_h1.setText(SocketUtils.COUNTER_NO < 10 ? "0" + SocketUtils.COUNTER_NO : String.valueOf(SocketUtils.COUNTER_NO));
                                //设置业务名称
                                tv_service_name_h1.setText(mCounterEntity.getServiceName());
                                //清空员工姓名
                                tv_name_h1.setText(staffName);
                                //清空工号
                                tv_job_no_h1.setText(staffId);
                                //设置显示滚动文字
                                tv_marquee_text_h1.setMyContext(mCounterEntity.getTipText());
                                if (!TextUtils.isEmpty(mCounterEntity.getTipText())) {
                                    tv_marquee_text_h1.startScroll();
                                    tv_marquee_text_h1.setL2r(false);
                                    tv_marquee_text_h1.setMySpeed(3);
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //如果有头像
                                        if (isHasHeadPic) {
                                            ImageManager.getInstance(mContext).loadImageForScal(mContext, new File(headFilePath), iv_head_h1, R.mipmap.person);
                                        } else {
                                            iv_head_h1.setImageResource(R.mipmap.person);
                                        }
                                    }
                                }, 500);
                            }
                            //如果是本地模板3
                            else if ("3".equals(local_template_id)) {
                                //设置竖屏
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                //设置安卓本地模板3显示
                                llyt_local_template_3.setVisibility(View.VISIBLE);
                                //启动日期时间定时器
                                startDateTimer();
                                //设置医院标题
                                tv_hospital_name_v2.setText(mCounterEntity.getTitle());
                                //设置显示诊室名称
                                tv_room_name_v2.setText(mCounterEntity.getCounterAlias());
                                //设置医生姓名
                                tv_doc_name_v2.setText(staffName);
                                //设置职称为空
                                tv_doc_job_v2.setText(staffTitle);
                                //设置显示滚动文字
                                tv_marquee_text_v2.setMyContext(mCounterEntity.getTipText());
                                if (!TextUtils.isEmpty(mCounterEntity.getTipText())) {
                                    tv_marquee_text_v2.startScroll();
                                    tv_marquee_text_v2.setL2r(false);
                                    tv_marquee_text_v2.setMySpeed(3);
                                }
                                //如果有头像
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isHasHeadPic) {
                                            ImageManager.getInstance(mContext).loadImageForScal(mContext, new File(headFilePath), iv_doc_head_v2, R.mipmap.person);
                                        } else {
                                            iv_doc_head_v2.setImageResource(R.mipmap.person);
                                        }
                                    }
                                }, 500);
                            }
                            //如果是本地模板4
                            else if ("4".equals(local_template_id)) {
                                //设置竖屏
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                //设置本地模板4显示
                                llyt_local_template_4.setVisibility(View.VISIBLE);
                                //启动日期时间定时器
                                startDateTimer();
                                //设置医院标题
                                tv_hospital_name_v3.setText(mCounterEntity.getTitle());
                                //设置显示诊室名称
                                tv_room_name_v3.setText(mCounterEntity.getCounterAlias());
                                //设置医生姓名
                                tv_doc_name_v3.setText(staffName);
                                //设置职称为空
                                tv_doc_job_v3.setText(staffTitle);
                                //设置显示滚动文字
                                tv_marquee_text_v3.setMyContext(mCounterEntity.getTipText());
                                if (!TextUtils.isEmpty(mCounterEntity.getTipText())) {
                                    tv_marquee_text_v3.startScroll();
                                    tv_marquee_text_v3.setL2r(false);
                                    tv_marquee_text_v3.setMySpeed(3);
                                }
                                //如果有头像
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isHasHeadPic) {
                                            ImageManager.getInstance(mContext).loadImageForScal(mContext, new File(headFilePath), iv_doc_head_v3, R.mipmap.person);
                                        } else {
                                            iv_doc_head_v3.setImageResource(R.mipmap.person);
                                        }
                                    }
                                }, 500);
                            }
                            //如果是本地模板5
                            else if ("5".equals(local_template_id)) {
                                //设置竖屏
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                                //设置本地模板5显示
                                llyt_local_template_5.setVisibility(View.VISIBLE);
                                //启动日期时间定时器
                                startDateTimer();
                                //设置标题
                                tv_title_t5.setText(mCounterEntity.getTitle());
                                //设置窗口别名
                                tv_counter_no_t5.setText(mCounterEntity.getCounterAlias());
                                //设置业务名称
                                tv_service_name_t5.setText(mCounterEntity.getServiceName());
                                //设置工号
                                tv_job_no_t5.setText(staffId);
                                //设置职称
                                tv_job_title_t5.setText(staffTitle);
                                //设置显示滚动文字
                                tv_marquee_text_t5.setMyContext(mCounterEntity.getTipText());
                                if (!TextUtils.isEmpty(mCounterEntity.getTipText())) {
                                    tv_marquee_text_t5.setL2r(false);
                                    tv_marquee_text_t5.setMySpeed(2);
                                    tv_marquee_text_t5.startScroll();
                                }
                                //如果有头像
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isHasHeadPic) {
                                            ImageManager.getInstance(mContext).loadImageForScal(mContext, new File(headFilePath), iv_head_t5, R.mipmap.person);
                                        } else {
                                            iv_head_t5.setImageResource(R.mipmap.person);
                                        }
                                    }
                                }, 500);

                            }
                            //如果是本地模板6
                            else if ("6".equals(local_template_id)) {
                                //设置竖屏
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                                //设置本地模板6显示
                                llyt_local_template_6.setVisibility(View.VISIBLE);
                                //启动日期时间定时器
                                startDateTimer();
                                //设置标题
                                tv_title_t6.setText(mCounterEntity.getTitle());
                                //设置窗口别名
                                tv_counter_no_t6.setText(mCounterEntity.getCounterAlias());
                                //设置业务名称
                                tv_service_name_t6.setText(mCounterEntity.getServiceName());
                                //设置姓名
                                tv_doc_name_t6.setText(staffName);
                                //设置职称
                                tv_job_title_t6.setText(staffTitle);
                                //如果需要滚动，注释这行，放开下面的注册内容
                                tv_marquee_text_t6.setText(mCounterEntity.getTipText());
                                //设置显示滚动文字
//                                tv_marquee_text_t6.setMyContext(mCounterEntity.getTipText());
                                //设置跑马灯
//                                if (!TextUtils.isEmpty(mCounterEntity.getTipText())) {
//                                    tv_marquee_text_t6.startScroll();
//                                    tv_marquee_text_t6.setL2r(false);
//                                    tv_marquee_text_t6.setMySpeed(3);
//                                }
                                //如果有头像
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isHasHeadPic) {
                                            ImageManager.getInstance(mContext).loadImageForScal(mContext, new File(headFilePath), iv_head_t6, R.mipmap.person);
                                        } else {
                                            iv_head_t6.setImageResource(R.mipmap.person);
                                        }
                                    }
                                }, 500);

                            }
                            //如果是本地模板7
                            else if ("7".equals(local_template_id)) {
                                //设置竖屏
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                                //设置本地模板7显示
                                llyt_local_template_7.setVisibility(View.VISIBLE);
                                //启动日期时间定时器
                                startDateTimer();
                                //设置标题
                                tv_title_t7.setText(mCounterEntity.getTitle());
                                //设置窗口别名
                                tv_counter_no_t7.setText(mCounterEntity.getCounterAlias());
                                //设置业务名称
                                tv_service_name_t7.setText(mCounterEntity.getServiceName());
                                //设置姓名
                                tv_doc_name_t7.setText(staffName);
                                //设置职称
                                tv_job_title_t7.setText(staffTitle);
                                //如果需要滚动，注释这行，放开下面的注册内容
                                tv_marquee_text_t7.setText(mCounterEntity.getTipText());
                                //设置显示滚动文字
//                                tv_marquee_text_t7.setMyContext(mCounterEntity.getTipText());
                                //设置跑马灯
//                                if (!TextUtils.isEmpty(mCounterEntity.getTipText())) {
//                                    tv_marquee_text_t7.startScroll();
//                                    tv_marquee_text_t7.setL2r(false);
//                                    tv_marquee_text_t7.setMySpeed(3);
//                                }
                                //如果有头像
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isHasHeadPic) {
                                            ImageManager.getInstance(mContext).loadImageForScal(mContext, new File(headFilePath), iv_head_t7, R.mipmap.person);
                                        } else {
                                            iv_head_t7.setImageResource(R.mipmap.person);
                                        }
                                    }
                                }, 500);

                            }

                        }
                        //服务器模板，如果模板对象不为空,并且不为安卓本地模板
                        else if (mTemplateEntity != null && mCounterEntity != null && !"0".equals(currTemplateId)) {
                            //获取背景名称
                            bgName = mTemplateEntity.getBackgroundName();
                            //多媒体目录
                            mediaDirectory = mTemplateEntity.getMediaDirectory();
                            //多媒体切换开关
                            mediaSwitch = mTemplateEntity.getMediaSwitch();
                            //如果需要切换
                            if ("1".equals(mediaSwitch)) {
                                //开始轮播
                                convenientBanner.startTurning(picSwitchTime * 1000);
                            } else {
                                if (convenientBanner.isTurning()) {
                                    convenientBanner.stopTurning();
                                }
                            }
                            //轮播动画
                            mediaAnimation = mTemplateEntity.getMediaAnimation();

                            //如果开始轮播,如果正在轮播并且轮播时间改变了，且不为0
                            if (isStartPlayState && picSwitchTime != mTemplateEntity.getMediaInterval() && mTemplateEntity.getMediaInterval() != 0) {
                                //设置轮播暂停时间
                                convenientBanner.setAutoTurningTime(mTemplateEntity.getMediaInterval() * 1000);
                            }
                            //如果时间间隔不为0
                            if (mTemplateEntity.getMediaInterval() != 0) {
                                //获取轮播切换时间
                                picSwitchTime = mTemplateEntity.getMediaInterval();
                            }

                            //获取html路径
                            String web_url = configDirPath + "/" + bgName;
                            File webFile = new File(web_url);
                            //如果html文件存在
                            if (webFile.exists()) {
                                mWebView.setVisibility(View.VISIBLE);
                                //如果是模板1
                                mWebView.setWebViewClient(new WebViewClient() {
                                    @Override
                                    public void onPageFinished(WebView view, String url) {
                                        //停止多媒体
                                        stopMediaPlay();
                                        //关闭加载进度条
                                        dismissProgressDialog();
                                        //解压缩升级
                                        upgrade();

                                        /**模板1的*/
                                        if ("1".equals(currTemplateId)) {
                                            //设置标题
                                            mWebView.loadUrl("javascript:SetValue('Title','" + mCounterEntity.getTitle() + "')");
                                            //设置业务名称
                                            mWebView.loadUrl("javascript:SetValue('ServiceName','" + mCounterEntity.getServiceName() + "')");
                                            //设置提示文字
                                            mWebView.loadUrl("javascript:SetValue('TipText','" + mCounterEntity.getTipText() + "')");
                                            //设置当前票号
                                            mWebView.loadUrl("javascript:SetValue('CurrCallTicket','" + "\\&nbsp;" + "')");
                                            //设置当前票号名称
                                            mWebView.loadUrl("javascript:SetValue('CurrCallName','" + "\\&nbsp;" + "')");
                                            //设置显示下一叫号
                                            mWebView.loadUrl("javascript:SetValue('NextCallTicket','" + "\\&nbsp;" + "')");
                                            //设置显示下一叫号名称
                                            mWebView.loadUrl("javascript:SetValue('NextCallName','" + "\\&nbsp;" + "')");
                                        }

                                        /**模板2的*/
                                        if ("2".equals(currTemplateId)) {
                                            //设置标题
                                            mWebView.loadUrl("javascript:SetValue('Title','" + mCounterEntity.getTitle() + "')");
                                            //设置提示文字
                                            mWebView.loadUrl("javascript:SetValue('TipText','" + mCounterEntity.getTipText() + "')");
                                            //如果启动了叫号模板
                                            if ("1".equals(mTemplateEntity.getCallTextModel_enable())) {
                                                //设置叫号模板
                                                setHistoryCallModel(mTemplateEntity);
                                            }
                                        }

                                        /**模板3的*/
                                        if ("3".equals(currTemplateId)) {
                                            hospitalWaitCallPosition = 0;
                                            //获取初始化数据(等候叫号数据)
                                            hospitalWaitCallList.clear();
                                            hospitalWaitCallList.addAll(settingEntity.getWaitCallInfoList());
                                            //设置标题
                                            mWebView.loadUrl("javascript:SetValue('Title','" + mCounterEntity.getTitle() + "')");
                                            //设置滚动文字
                                            mWebView.loadUrl("javascript:SetValue('TipText','" + mCounterEntity.getTipText() + "')");
                                            //显示当前叫号信息
                                            mWebView.loadUrl("javascript:SetCurrentCall('" + new Gson().toJson(currCallInfoList) + "')");
                                            //过号的
                                            mWebView.loadUrl("javascript:SetCalled('" + new Gson().toJson(call_info_list) + "')");
                                            //定时显示医院等待叫号
                                            showChangeHospitalWaitingCallTimer();
                                        }

                                        /**模板4的*/
                                        if ("4".equals(currTemplateId)) {
                                            //设置标题
                                            mWebView.loadUrl("javascript:SetValue('Title','" + mCounterEntity.getTitle() + "')");
                                            //设置业务名称
                                            mWebView.loadUrl("javascript:SetValue('ServiceName','" + mCounterEntity.getServiceName() + "')");
                                            //设置提示文字
                                            mWebView.loadUrl("javascript:SetValue('TipText','" + mCounterEntity.getTipText() + "')");
                                            //设置员工名称
                                            mWebView.loadUrl("javascript:SetValue('StaffName','" + staffName + "')");
                                            //设置员工工号
                                            mWebView.loadUrl("javascript:SetValue('StaffNum','" + staffId + "')");
                                            //设置员工职称
                                            mWebView.loadUrl("javascript:SetValue('StaffTitle','" + staffTitle + "')");
                                            //当前呼叫票号
                                            mWebView.loadUrl("javascript:SetValue('CurrCallTicket','" + "\\&nbsp;" + "')");
                                            //当前呼叫名称
                                            mWebView.loadUrl("javascript:SetValue('CurrCallName','" + "\\&nbsp;" + "')");
                                            //等待呼叫票号
                                            mWebView.loadUrl("javascript:SetValue('NextCallTicket','" + "\\&nbsp;" + "')");
                                            //等待呼叫名称
                                            mWebView.loadUrl("javascript:SetValue('NextCallName','" + "\\&nbsp;" + "')");
                                            //设置窗口号
                                            mWebView.loadUrl("javascript:SetValue('Counter','" + SocketUtils.COUNTER_NO + "')");
                                            //设置窗口别名
                                            mWebView.loadUrl("javascript:SetValue('CounterAlias','" + mCounterEntity.getCounterAlias() + "')");
                                            //设置头像
                                            long headFileSize = FileUtils.getFileLength(headFilePath);
                                            if (headFileSize == 0) {
                                                headFilePath = "";
                                            }
                                            mWebView.loadUrl("javascript:SetValue('StaffHeadPath','" + headFilePath + "')");
                                        }

                                        /**如果是模板5*/
                                        if ("5".equals(currTemplateId)) {
                                            //设置标题
                                            mWebView.loadUrl("javascript:SetValue('Title','" + mCounterEntity.getTitle() + "')");
                                            //设置业务名称
                                            mWebView.loadUrl("javascript:SetValue('ServiceName','" + mCounterEntity.getServiceName() + "')");
                                            //设置提示文字
                                            mWebView.loadUrl("javascript:SetValue('TipText','" + mCounterEntity.getTipText() + "')");
                                            //设置员工名称
                                            mWebView.loadUrl("javascript:SetValue('StaffName','" + staffName + "')");
                                            //设置员工工号
                                            mWebView.loadUrl("javascript:SetValue('StaffNum','" + staffId + "')");
                                            //设置员工职称
                                            mWebView.loadUrl("javascript:SetValue('StaffTitle','" + staffTitle + "')");
                                            //当前呼叫票号
                                            mWebView.loadUrl("javascript:SetValue('CallInfo','" + "\\&nbsp;" + "')");
                                            //设置窗口号
                                            mWebView.loadUrl("javascript:SetValue('Counter','" + SocketUtils.COUNTER_NO + "')");
                                            //设置窗口别名
                                            mWebView.loadUrl("javascript:SetValue('CounterAlias','" + mCounterEntity.getCounterAlias() + "')");
                                            //设置头像
                                            long headFileSize = FileUtils.getFileLength(headFilePath);
                                            if (headFileSize == 0) {
                                                headFilePath = "";
                                            }
                                            mWebView.loadUrl("javascript:SetValue('StaffHeadPath','" + headFilePath + "')");
                                        }

                                        /**如果是模板6*/
                                        if ("6".equals(currTemplateId)) {
                                            //设置标题
                                            mWebView.loadUrl("javascript:SetValue('Title','" + mCounterEntity.getTitle() + "')");
                                            //设置窗口号
                                            mWebView.loadUrl("javascript:SetValue('Counter','" + SocketUtils.COUNTER_NO + "号窗口')");
                                            //当前呼叫票号
                                            mWebView.loadUrl("javascript:SetValue('CallInfo','" + "\\&nbsp;" + "')");
                                        }

                                        /**如果是7号模板*/
                                        if ("7".equals(currTemplateId)) {
                                            //设置标题，
                                            mWebView.loadUrl("javascript:SetValue('Title','" + mCounterEntity.getTitle() + "')");
                                            //设置业务类型
                                            mWebView.loadUrl("javascript:SetValue('ServiceName','" + mCounterEntity.getCounterAlias() + "')");
                                            //设置业务员名称为空
                                            mWebView.loadUrl("javascript:SetValue('StaffName','" + staffName + "')");
                                            //设置职称为空
                                            mWebView.loadUrl("javascript:SetValue('StaffTitle','" + staffTitle + "')");
                                            //设置叫号为空
                                            mWebView.loadUrl("javascript:SetValue('CurrCallTicket','" + "\\&nbsp;" + "')");
                                        }

                                        /**如果8号模板，竖屏综合屏*/
                                        if ("8".equals(currTemplateId)) {
                                            //设置标题
                                            mWebView.loadUrl("javascript:SetValue('Title','" + mCounterEntity.getTitle() + "')");
                                            //如果启动了叫号模板
                                            if ("1".equals(mTemplateEntity.getCallTextModel_enable())) {
                                                //显示叫号
                                                ShowCallInfoFor8Template(mTemplateEntity);
                                            }
                                        }

                                        //如果有多媒体
                                        if (Constants.MEDIA_HAS_TYPE_YES.equals(mTemplateEntity.getMediaType())) {

                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                //设置多媒体
                                                mWebView.evaluateJavascript("javascript:GetMediaRegion()", new ValueCallback<String>() {
                                                    @Override
                                                    public void onReceiveValue(final String value) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    if (!TextUtils.isEmpty(value)) {
                                                                        LogUtils.d("多媒体位置", value);
                                                                        String[] str = value.replace("\"", "").split(",");
                                                                        rlyt_media_display_area.setVisibility(View.VISIBLE);
                                                                        int m_left = 0;
                                                                        if ("auto".equalsIgnoreCase(str[0])) {
                                                                            m_left = 0;
                                                                        } else {
                                                                            m_left = (int) Math.round(Double.parseDouble(str[0]));
                                                                        }
                                                                        //获取多媒体的宽高
                                                                        mediaWidth = (int) Math.round(Double.parseDouble(str[2]));
                                                                        mediaHeight = (int) Math.round(Double.parseDouble(str[3]));
                                                                        //设置多媒体边距和宽高
                                                                        AppUtils.setRelativeLayoutMarginWidHeight(rlyt_media_display_area, m_left, (int) Math.round(Double.parseDouble(str[1])), mediaWidth, mediaHeight);
                                                                        //显示轮播图
                                                                        startOrStopRotationPic(mediaDirPath, picSwitchTime, mediaWidth, mediaHeight, true);
                                                                    } else {
                                                                        rlyt_media_display_area.setVisibility(View.GONE);
                                                                        //停止多媒体
                                                                        stopMediaPlay();
                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });

                                            } else {
                                                mWebView.setWebChromeClient(
                                                        new WebChromeClient() {
                                                            @Override
                                                            public boolean onJsAlert(WebView view, String url, final String message, JsResult result) {
                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        try {
                                                                            LogUtils.d("WebView返回", message);
                                                                            if (!TextUtils.isEmpty(message)) {
                                                                                LogUtils.d("多媒体位置", message);
                                                                                String[] str = message.replace("\"", "").split(",");
                                                                                rlyt_media_display_area.setVisibility(View.VISIBLE);
                                                                                int m_left = 0;
                                                                                if ("auto".equalsIgnoreCase(str[0])) {
                                                                                    m_left = 0;
                                                                                } else {
                                                                                    m_left = (int) Math.round(Double.parseDouble(str[0]));
                                                                                }
                                                                                //获取多媒体的宽高
                                                                                mediaWidth = (int) Math.round(Double.parseDouble(str[2]));
                                                                                mediaHeight = (int) Math.round(Double.parseDouble(str[3]));
                                                                                //设置多媒体边距和宽高
                                                                                AppUtils.setRelativeLayoutMarginWidHeight(rlyt_media_display_area, m_left, (int) Math.round(Double.parseDouble(str[1])), mediaWidth, mediaHeight);
                                                                                //显示轮播图
                                                                                startOrStopRotationPic(mediaDirPath, picSwitchTime, mediaWidth, mediaHeight, true);
                                                                            } else {
                                                                                rlyt_media_display_area.setVisibility(View.GONE);
                                                                                //停止多媒体
                                                                                stopMediaPlay();
                                                                            }
                                                                        } catch (Exception e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                });
                                                                result.confirm();
                                                                return true;
                                                            }
                                                        });
                                                mWebView.loadUrl("javascript:alert(GetMediaRegion())");
                                            }

                                        }
                                        //如果有头像
                                        else if (Constants.MEDIA_HAS_TYPE_HEADPIC.equals(mTemplateEntity.getMediaType())) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                //设置头像位置
                                                mWebView.evaluateJavascript("javascript:GetMediaRegion()", new ValueCallback<String>() {
                                                    @Override
                                                    public void onReceiveValue(final String value) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    if (!TextUtils.isEmpty(value)) {
                                                                        LogUtils.d("头像位置", value);
                                                                        String[] str = value.replace("\"", "").split(",");
                                                                        iv_head.setVisibility(View.VISIBLE);
                                                                        //获取头像的宽高
                                                                        mediaWidth = (int) Math.round(Double.parseDouble(str[2]));
                                                                        mediaHeight = (int) Math.round(Double.parseDouble(str[3]));
                                                                        //设置多媒体边距和宽高
                                                                        AppUtils.setViewWidthAndHeightAndMargin(iv_head, (int) Math.round(Double.parseDouble(str[0])), (int) Math.round(Double.parseDouble(str[1])), mediaWidth, mediaHeight);
                                                                        //显示头像
                                                                        if ("5".equals(currTemplateId)) {
                                                                            ImageManager.getInstance(mContext).loadImageForScal(mContext, new File(headFilePath), iv_head, mediaWidth, mediaHeight, R.mipmap.jingcha_default);
                                                                        } else {
                                                                            ImageManager.getInstance(mContext).loadImageForScal(mContext, new File(headFilePath), iv_head, mediaWidth, mediaHeight, R.mipmap.person);
                                                                        }
                                                                    }

                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            } else {
                                                mWebView.setWebChromeClient(
                                                        new WebChromeClient() {
                                                            @Override
                                                            public boolean onJsAlert(WebView view, String url, final String message, JsResult result) {
                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        try {
                                                                            if (!TextUtils.isEmpty(message)) {
                                                                                LogUtils.d("头像位置", message);
                                                                                String[] str = message.replace("\"", "").split(",");
                                                                                iv_head.setVisibility(View.VISIBLE);
                                                                                //获取头像的宽高
                                                                                mediaWidth = (int) Math.round(Double.parseDouble(str[2]));
                                                                                mediaHeight = (int) Math.round(Double.parseDouble(str[3]));
                                                                                //设置多媒体边距和宽高
                                                                                AppUtils.setViewWidthAndHeightAndMargin(iv_head, (int) Math.round(Double.parseDouble(str[0])), (int) Math.round(Double.parseDouble(str[1])), mediaWidth, mediaHeight);
                                                                                //显示头像
                                                                                if ("5".equals(currTemplateId)) {
                                                                                    ImageManager.getInstance(mContext).loadImage(mContext, new File(headFilePath), iv_head, R.mipmap.jingcha_default);
                                                                                } else {
                                                                                    ImageManager.getInstance(mContext).loadImage(mContext, new File(headFilePath), iv_head, R.mipmap.person);
                                                                                }
                                                                            }

                                                                        } catch (Exception e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                });
                                                                result.confirm();
                                                                return true;
                                                            }
                                                        });
                                                mWebView.loadUrl("javascript:alert(GetMediaRegion())");

                                            }
                                        }
                                        //如果没有多媒体和头像
                                        else if (Constants.MEDIA_HAS_TYPE_NOT.equals(mTemplateEntity.getMediaType())) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //设置多媒体显示区域隐藏
                                                    rlyt_media_display_area.setVisibility(View.GONE);
                                                    //设置头像隐藏
                                                    iv_head.setVisibility(View.GONE);
                                                }
                                            });
                                        }
                                    }
                                });
                                //加载html模板
                                mWebView.clearHistory();
                                mWebView.clearCache(true);
                                mWebView.loadUrl("file:///" + web_url);
                            }
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                }

            }
        });

    }

    /**
     * 定时显示医院等待叫号
     */
    private void showChangeHospitalWaitingCallTimer() {
        try {
            if (waitingCallTimer != null) {
                waitingCallTimer.cancel();
                waitingCallTimer = null;
            }
            waitingCallTimer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    mTimehandler.sendEmptyMessage(CHANGE_HOSPITAL_WAIT_CALL);
                }
            };
            waitingCallTimer.schedule(timerTask, 1000, 10 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }


    /**
     * 启动日期时间定时器
     */
    private void startDateTimer() {
        try {
            LogUtils.writeLogtoFile("时间定时器", "启动时间定时器");
            //启动时间显示
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            dateAndTimeTimer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    mTimehandler.sendEmptyMessage(UPDATE_TIME);
                }
            };
            dateAndTimeTimer.schedule(timerTask, 1000, 1000);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case CHANGE_HOSPITAL_WAIT_CALL://更新切换医院历史叫号
                try {
                    //获取历史叫号分组后的json字符串集合
                    ArrayList<String> jsonList = AppUtils.getWaitCallJsonStrArr(hospitalWaitCallList);
                    if (jsonList.size() > 0) {
                        //显示历史叫号
                        mWebView.loadUrl("javascript:SetHistoryCall('" + jsonList.get(hospitalWaitCallPosition) + "')");
                        //组数位置加1
                        hospitalWaitCallPosition++;
                        if (hospitalWaitCallPosition == jsonList.size()) {
                            hospitalWaitCallPosition = 0;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                }

                break;

            case UPDATE_TIME://更新时间
                try {
                    DateModel date = new DateModel();
                    //日期
                    String dateString = date.getDateString();
                    //星期
                    String weekString = date.getToday();
                    //时间,date.getTimeString()或date.getShortTimeString()
                    String timeString = date.getTimeString();
                    if (timeString.startsWith(" ")) {
                        //格式为HH:mm:ss
                        timeString = timeString.substring(1, timeString.length());
                    }
                    String timeString_short = date.getShortTimeString();
                    if ("1".equals(local_template_id)) {
                        //设置显示日期、星期和时间
                        tv_date_and_week_v1.setText(dateString + "   " + weekString);
                        //设置时间
                        tv_time_v1.setText(timeString_short);
                    } else if ("3".equals(local_template_id)) {
                        //设置显示日期、星期和时间
                        tv_date_and_week_v2.setText(dateString + "   " + weekString);
                        //设置时间
                        tv_time_v2.setText(timeString_short);
                    } else if ("4".equals(local_template_id)) {
                        //设置显示日期、星期和时间
                        tv_date_and_week_v3.setText(dateString + "   " + weekString);
                        //设置时间
                        tv_time_v3.setText(timeString_short);
                    } else if ("5".equals(local_template_id)) {
                        tv_date_and_time_t5.setText(date.getDateStringforChinese() + "   " + weekString + "   " + timeString_short);
                    } else if ("6".equals(local_template_id)) {
                        //设置显示日期、星期和时间
                        tv_date_and_week_t6.setText(date.getDateStringforChinese() + "   " + weekString);
                        //设置时间
                        tv_time_t6.setText(timeString_short);
                    } else if ("7".equals(local_template_id)) {
                        //设置显示日期、星期和时间
                        tv_date_and_week_t7.setText(date.getDateStringforChinese() + "   " + weekString);
                        //设置时间
                        tv_time_t7.setText(timeString_short);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                }
                break;
        }
        return true;
    }

    /**
     * 设置叫号模板
     *
     * @param templateEntity 模板对象
     */
    private void setHistoryCallModel(TemplateEntity templateEntity) {
        try {
            //获取窗口号长度
            if (templateEntity.getCallTextModel_counterLen() != 0) {
                counterNoLen = templateEntity.getCallTextModel_counterLen();
            }
            //获取叫号模板
            callModel_text = templateEntity.getCallTextModel_text();
            isEnalbeCallModel = true;
            //显示当前叫号
            showCallInfoList();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }


    /**
     * 显示8号叫号模板
     *
     * @param templateEntity 模板对象
     */
    private void ShowCallInfoFor8Template(TemplateEntity templateEntity) {
        try {
            //获取窗口号长度
            if (templateEntity.getCallTextModel_counterLen() != 0) {
                counterNoLen = templateEntity.getCallTextModel_counterLen();
            }
            //获取叫号模板
            callModel_text = templateEntity.getCallTextModel_text();
            isEnalbeCallModel = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("[");
                        for (int i = 0; i < call_info_list.size(); i++) {
                            CallInfoEntity entity = call_info_list.get(i);
                            stringBuffer.append("{\"ticket" + "\":\"" + entity.getTicketNo() + "\",\"counter\":\"");
                            String callStr = callModel_text.replace("{TicketNo.}", "<label class=\\'counter\\'>" + entity.getTicketNo() + "</label>")
                                    .replace("{CounterNo.}", "<label class=\\'counter\\'>" + entity.getCounterNo() + "</label>")
                                    .replace("{Name}", "<label class=\\'counter\\'>" + entity.getName() + "</label>")
                                    .replace("{Counter Alias}", "<label class=\\'counter\\'>" + entity.getConsultingRoomName() + "</label>");
                            stringBuffer.append(callStr + "\"}");
                            if (i < call_info_list.size() - 1) {
                                stringBuffer.append(",");
                            }
                        }
                        stringBuffer.append("]");

                        LogUtils.d("传值", stringBuffer.toString());
                        mWebView.loadUrl("javascript:SetCallInfo('" + stringBuffer.toString() + "')");

                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }


    /**
     * 解析配置检查更新
     */
    private void parseConfig() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    reqFiles = "";
                    LogUtils.d("settings", "下载配置文件成功");
                    //设置webView隐藏
                    mWebView.setVisibility(View.GONE);
                    //设置多媒体显示区域隐藏
                    rlyt_media_display_area.setVisibility(View.GONE);
                    //设置头像隐藏
                    iv_head.setVisibility(View.GONE);
                    //停止播放
                    stopMediaPlay();
                    //获取配置文件对象
                    settingEntity_new = AppUtils.getAppSettingEntity(settingFilePath);
                    if (settingEntity_new != null) {
                        //窗口对象
                        mCounterEntity = null;
                        mCounterEntity = AppUtils.getCounterSettingEntity(settingEntity_new, SocketUtils.COUNTER_NO + "");
                        //获取当前模板id
                        if (TextUtils.isEmpty(currTemplateId)) {
                            currTemplateId = mCounterEntity.getTemplateId();
                        }
                        if (TextUtils.isEmpty(currTemplateId)) {
                            currTemplateId = "1";
                        }
                        //记住当前模板
                        SPUtils.put(mContext, SPUtils.TEMPLATE_ID, currTemplateId);
                        //如果不为安卓本地模板
                        if (!"0".equals(currTemplateId)) {
                            //设置本地模板1隐藏
                            llyt_local_hospital_template_1.setVisibility(View.GONE);
                            //设置本地模板2隐藏
                            llyt_local_template_2.setVisibility(View.GONE);
                            //设置本地模板3隐藏
                            llyt_local_template_3.setVisibility(View.GONE);
                            //设置本地模板4隐藏
                            llyt_local_template_4.setVisibility(View.GONE);
                            //设置本地模板5隐藏
                            llyt_local_template_5.setVisibility(View.GONE);
                            //设置本地模板6隐藏
                            llyt_local_template_6.setVisibility(View.GONE);
                            //设置本地模板7隐藏
                            llyt_local_template_7.setVisibility(View.GONE);
                            //获取模板对象
                            mTemplateEntity = null;
                            mTemplateEntity = AppUtils.getTemplateEntityFromTemplateId(settingEntity_new, currTemplateId);
                            //设置模板显示方向
                            setScreenOrientation(mTemplateEntity);
                            //背景名称
                            bgName = mTemplateEntity.getBackgroundName();
                            //获取多媒体目录
                            mediaDirectory = mTemplateEntity.getMediaDirectory();
                            //获取多媒体个数
                            media_count = mTemplateEntity.getMediaCount();
                            //获取html先关文件个数
                            html_file_count = mTemplateEntity.getFileListCount();
                            //如果是第一次进入该app,false是第一次
                            if (!(boolean) SPUtils.get(mContext, SPUtils.IS_FIRST_INSTALL, false)) {
                                LogUtils.d("settings", "第一次安装本app");
                                LogUtils.writeLogtoFile("配置文件解析", "第一次进入app");
                                //记住不是第一次安装
                                SPUtils.put(mContext, SPUtils.IS_FIRST_INSTALL, true);
                                //保存配置文件
                                SPUtils.put(mContext, SPUtils.SETTINGS_ENTITY, new Gson().toJson(settingEntity_new));
                                //清空当前所有的媒体
                                File file = new File(mediaDirPath);
                                FileUtils.deleteFile(file);
                                //删除背景文件
                                File file_bg = new File(configDirPath + "/" + bgName);
                                if (file_bg.exists()) {
                                    file_bg.delete();
                                }
                                //发送文件请求传送消息
                                reqFiles = "[Bg:" + bgName + "][BgFileList:" + mTemplateEntity.getFileListDirectory() + "]";
                                //如果有多媒体
                                if (Constants.MEDIA_HAS_TYPE_YES.equals(mTemplateEntity.getMediaType())) {
                                    reqFiles += "[Media:" + mediaDirectory + "]";
                                }
                                SocketMsg Msg = new SocketMsg();
                                Msg.MsgType = Constants.QQS_TVD_REQUESTFILE;
                                Msg.Arg3 = reqFiles;
                                sendMessage(Msg);
                                LogUtils.writeLogtoFile("配置文件解析", "第一次进入app请求多媒体:" + Msg.toString());
                            }
                            //如果不是第一次进入本app
                            else {
                                LogUtils.d("settings", "检查更新");
                                LogUtils.writeLogtoFile("配置文件解析", "不是第一次进入，检查更新");
                                //获取配置文件数据
                                String json = (String) SPUtils.get(mContext, SPUtils.SETTINGS_ENTITY, "");
                                if (!TextUtils.isEmpty(json)) {
                                    settingEntity_old = null;
                                    settingEntity_old = new Gson().fromJson(json, AppSettingEntity.class);
                                    //检查更新
                                    checkUpdateFile(settingEntity_old, settingEntity_new);
                                }
                            }
                        } else {
                            currTemplateId = "0";
                            SPUtils.put(mContext, SPUtils.TEMPLATE_ID, "0");
                            if (TextUtils.isEmpty(local_template_id)) {
                                local_template_id = "1";
                                SPUtils.put(mContext, SPUtils.LOCAL_TEMPLATE_ID, local_template_id);
                            }
                            //设置显示默认参数
                            setDefaultParam(settingEntity_new);
                        }
                    } else {
                        //断开重新登录
                        disConnectReLogin();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                }
            }

        });
    }


    /**
     * 检查更新
     *
     * @param entity_old 之前保存在本地的配置文件
     * @param entity     现下载下来的服务器最新配置文件
     */
    private void checkUpdateFile(AppSettingEntity entity_old, final AppSettingEntity entity) {
        try {
            LogUtils.d("settings", "entity_old==" + entity_old.toString());
            LogUtils.d("settings", "entity_new==" + entity);
            //获取更新字符串
            reqFiles = AppUtils.getUpdateString(entity_old, entity, currTemplateId, bgName, root_path, mediaDirectory, mediaDirPath, configDirPath, isChangeTemplateId);
            //标识未改变模板id
            isChangeTemplateId = false;
            LogUtils.d("settings", "检查更新的文件有:" + reqFiles);
            LogUtils.writeLogtoFile("配置文件解析", "检查更新的文件有" + reqFiles);
            //如果需要请求文件
            if (!TextUtils.isEmpty(reqFiles)) {
                //发送请求文件消息
                SocketMsg Msg = new SocketMsg();
                Msg.MsgType = Constants.QQS_TVD_REQUESTFILE;
                Msg.Arg3 = reqFiles;
                sendMessage(Msg);
                LogUtils.writeLogtoFile("配置文件解析", "发送文件更新请求" + Msg.toString());
            } else {
                LogUtils.d("settings", "不需要更新文件:");
                LogUtils.writeLogtoFile("解析配置文件", "不需要更新文件");
                //如果存在多媒体
                if (Constants.MEDIA_HAS_TYPE_YES.equals(mTemplateEntity.getMediaType())) {
                    //多媒体个数
                    int m_count = FileUtils.getFileNum(mediaDirPath);
                    LogUtils.d("settings", "多媒体文件个数实际下载个数=" + m_count + ",应下载个数=" + media_count);
                    //如果多媒体的个数不一致
                    if (media_count != 0 && m_count != media_count) {
                        reqFiles += "[Media:" + mediaDirectory + "]";
                        //清空当前所有的媒体
                        File file2 = new File(mediaDirPath);
                        FileUtils.deleteFile(file2);
                    }
                }

                //获取模板界面html文件路径
                String htmlMianFilePath = configDirPath + "/" + bgName;
                File htmlMianFile = new File(htmlMianFilePath);
                if (!htmlMianFile.exists()) {
                    LogUtils.d("settings", "模板Html文件不存在");
                    reqFiles += "[Bg:" + bgName + "]";
                }

                //html文件个数
                String htmlFilePath = root_path + "/Config/" + mTemplateEntity.getFileListDirectory();
                int h_count = FileUtils.getFileNum(htmlFilePath);
                LogUtils.d("settings", "Html相关文件个数实际下载个数=" + h_count + ",应下载个数=" + html_file_count);
                //如果html相关文件个数不一致
                if (html_file_count != 0 && html_file_count != h_count) {
                    reqFiles += "[BgFileList:" + mTemplateEntity.getFileListDirectory() + "]";
                    //清空当前所有的html相关文件
                    File file2 = new File(htmlFilePath);
                    FileUtils.deleteFile(file2);
                }

                LogUtils.d("settings", "需要下载缺少的文件==" + reqFiles);
                //如果需要请求文件
                if (!TextUtils.isEmpty(reqFiles)) {
                    //重新发送多媒体请求
                    SocketMsg Msg = new SocketMsg();
                    Msg.MsgType = Constants.QQS_TVD_REQUESTFILE;
                    Msg.Arg3 = reqFiles;
                    sendMessage(Msg);
                    LogUtils.writeLogtoFile("文件个数不对，重新发送多媒体请求，发送文件", Msg.toString());
                } else {
                    //保存配置文件
                    SPUtils.put(mContext, SPUtils.SETTINGS_ENTITY, new Gson().toJson(settingEntity_new));
                    LogUtils.d("settings", "配置文件不为空,设置默认参数");
                    LogUtils.writeLogtoFile("解析配置文件", "不需要更新文件,设置默认参数与界面");
                    //设置默认参数
                    setDefaultParam(settingEntity_new);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }


    /**
     * 解压缩升级
     */
    private void upgrade() {
        try {
            //软件升级
            if (reqFiles.contains("[All]") || reqFiles.contains("[Update]")) {
                final File updateFile = new File(updateFilePath);
                //如果文件存在
                if (updateFile.exists()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LogUtils.d("settings", "apk文件进入解压");
                                boolean isSucc = ZipUtils.UnZipFolder(updateFilePath, root_path);
                                //如果解压缩成功,删除zip文件
                                if (isSucc) {
                                    LogUtils.d("settings", "apk解压缩成功");
                                    FileUtils.deleteFile(updateFile);
                                    //获取apk文件
                                    String apkPath = FileUtils.getApkFile(root_path);
                                    //如果文件地址不为空
                                    if (!TextUtils.isEmpty(apkPath)) {
                                        //发送消息弹窗更新
                                        updateHandler.obtainMessage(0, apkPath).sendToTarget();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                            }
                        }
                    }).start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }


    /**
     * 版本更新Handler,安装apk
     */
    Handler updateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            try {
                //获取apk路径
                String apkPath = (String) msg.obj;
                File apkFile = new File(apkPath);
                //如果文件存在
                if (apkFile.exists()) {
                    //安装apk
                    AppUtils.installApkFile(mContext, apkFile, version);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
            }
        }
    };

    @OnClick({rlyt_all})
    public void onClick(View view) {
        switch (view.getId()) {
            case rlyt_all://测试使用，点击单位名称 弹窗网络设置

//                //如果有声音,实现静音
//                if (!mMediaPlayer.getVolumeState()) {
//                    mMediaPlayer.closeVolume();
//                }
//                //开启声音
//                else {
//                    mMediaPlayer.openVolume();
//                }

                //显示退出或设置网络选项弹窗
//                showExitOrSettingNetWorkDialog();

                break;
        }
    }


    /**
     * 开始或停止轮播图片
     *
     * @param mediaDirPath  对媒体路径
     * @param picSwitchTime 轮播间隔时间，秒
     * @param advertWidth   多媒体宽
     * @param advertHeight  多媒体高
     * @param isStartOrStop true开始播放，false停止
     */
    private void startOrStopRotationPic(final String mediaDirPath, final int picSwitchTime,
                                        final int advertWidth, final int advertHeight,
                                        final boolean isStartOrStop) {
        try {
            //如果是锁屏状态
            if (isLockScreen) {
                return;
            }
            isStartPlayState = isStartOrStop;
            //如果可以开始轮播
            if (isStartOrStop) {
                showMediaView(mediaDirPath, picSwitchTime, advertWidth, advertHeight);
            } else {
                //停止多媒体轮播和播放视频
                stopMediaPlay();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }

    }

    /**
     * 显示轮播图片
     *
     * @param mediaDirPath  多媒体本地路径
     * @param picSwitchTime 轮播时间间隔
     * @param advertWidth   轮播显示区宽
     * @param advertHeight  轮播显示区高
     */
    private void showMediaView(String mediaDirPath, final int picSwitchTime, final int advertWidth, final int advertHeight) {
        try {
            //如果在播放视频
            if (isPlayVideoMode) {
                //标识未播放视频
                isPlayVideoMode = false;
                //停止播放视频
                mMediaPlayer.onStop();
            }
            //加载目录下的本地图片
            FileUtils.getMediaEntityList(mediaDirPath, new OnLoadFileListener<ArrayList<MediaEntity>>() {
                @Override
                public void onLoadFile(final ArrayList<MediaEntity> list) {
                    try {
                        mediaUrlList.clear();
                        curr_position = 0;
                        mediaUrlList.addAll(list);
                        //如果没有多媒体文件
                        if (mediaUrlList.size() <= 0) {
                            rlyt_media_display_area.setVisibility(View.GONE);
                            //隐藏图片轮播控件
                            convenientBanner.setVisibility(View.GONE);
                            //停止轮播
                            convenientBanner.stopTurning();
                            convenientBanner.clearDisappearingChildren();
                            //标识未播放视频
                            isPlayVideoMode = false;
                            isStartPlayState = false;
                            return;
                        }
                        //轮播控件显示
                        rlyt_media_display_area.setVisibility(View.VISIBLE);
                        convenientBanner.setVisibility(View.VISIBLE);
                        if (convenientBanner.isTurning()) {
                            convenientBanner.stopTurning();
                        }
                        convenientBanner.clearDisappearingChildren();
                        //设置每次翻页滚动的时间，不是轮播时间
                        convenientBanner.setScrollDuration(3000);
                        //设置动画方式
                        convenientBanner.setScrollerAnimation(mediaAnimation);
                        convenientBanner.setPages(list, advertWidth, advertHeight)
                                .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                    @Override
                                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                    }

                                    @Override
                                    public void onPageSelected(int position) {
                                        try {
                                            curr_position = position;
                                            //设置动画方式,因为只有16个动画
                                            convenientBanner.setScrollerAnimation(mediaAnimation);
                                            //如果是播放视频
                                            if (Constants.MEDIA_PLAY_TYPE_VIDEO.equals(list.get(position).getMediaType())) {
                                                //如果在轮播,停止轮播
                                                if (convenientBanner.isTurning()) {
                                                    convenientBanner.stopTurning();
                                                }
                                                isPlayVideoMode = true;
                                                //播放视频
                                                playVideo(list.get(position));
                                            }
                                            //如果是图片
                                            else {
                                                isPlayVideoMode = false;
                                                //隐藏播放控件
                                                mMediaPlayer.setVisibility(View.GONE);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                                        }
                                    }

                                    @Override
                                    public void onPageScrollStateChanged(int state) {
                                    }
                                });
                        //如果第一个是视频
                        if (Constants.MEDIA_PLAY_TYPE_VIDEO.equals(list.get(0).getMediaType())) {
                            //如果正在轮播，则停止轮播
                            if (convenientBanner.isTurning()) {
                                convenientBanner.stopTurning();
                            }
                            stopMediaPlay();
                            isPlayVideoMode = true;
                            //播放视频
                            playVideo(list.get(0));
                        } else {
                            isPlayVideoMode = false;
                            //超过一个多媒体才翻页
                            if (list.size() > 1) {
                                //如果需要切换
                                if ("1".equals(mediaSwitch)) {
                                    //开始轮播
                                    convenientBanner.startTurning(picSwitchTime * 1000);
                                } else {
                                    if (convenientBanner.isTurning()) {
                                        convenientBanner.stopTurning();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }

    }

    /**
     * 停止多媒体轮播和停止播放视频
     */
    private void stopMediaPlay() {
        try {
            //停止轮播
            if (convenientBanner.isTurning()) {
                convenientBanner.stopTurning();
            }
            //如果在播放视频
            if (isPlayVideoMode) {
                //标识未播放视频
                isPlayVideoMode = false;
                //停止播放视频
                mMediaPlayer.onStop();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }


    @Override
    public void onCompletion() {
        //播放完成监听回调
        palyCompletionOrError(true);
    }

    @Override
    public void onPlayError() {
        //播放错误,监听回调
        LogUtils.d("播放", "播放错误");
        palyCompletionOrError(false);
    }

    /**
     * 播放视频
     *
     * @param entity 视频对象
     */
    private void playVideo(final MediaEntity entity) {
        try {
            //如果有视频文件
            if (entity != null && !TextUtils.isEmpty(entity.getMediaFilePath())) {
                //视频显示
                mMediaPlayer.setVisibility(View.VISIBLE);
                File file = new File(entity.getMediaFilePath());
                //如果文件存在
                if (file.exists()) {
                    //开始播放视频
                    mMediaPlayer.startPlayVideo(entity.getMediaFilePath());
                }
                //如果不存在
                else {
                    //处理播放错误
                    palyCompletionOrError(false);
                }
            } else {
                LogUtils.d("视频", "无视频文件");
                //处理播放错误
                palyCompletionOrError(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            //处理播放错误
            palyCompletionOrError(false);
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }

    }


    /**
     * 播放完成或播放错误
     *
     * @param isCompletionOrError true播放完成,false播放错误
     */
    private void palyCompletionOrError(final boolean isCompletionOrError) {
        //播放视频完成监听
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtils.d("play", "播放完成");
                    //如果只有一个视频
                    if (mediaUrlList.size() == 1 && Constants.MEDIA_PLAY_TYPE_VIDEO.equals(mediaUrlList.get(0).getMediaType())) {
                        //如果播放完成,实现重播
                        if (isCompletionOrError) {
                            mMediaPlayer.reStart();
                        }
                        //播放错误
                        else {

                        }

                    } else {
                        //如果需要切换
                        if ("1".equals(mediaSwitch)) {
                            //隐藏播放控件
                            mMediaPlayer.setVisibility(View.GONE);
                            int index = 0;
                            if (curr_position == mediaUrlList.size() - 1) {
                                index = 0;
                            } else {
                                index = curr_position + 1;
                            }
                            //如果下一个是视频
                            if (Constants.MEDIA_PLAY_TYPE_VIDEO.equals(mediaUrlList.get(index).getMediaType())) {
                                convenientBanner.startTurning_video(picSwitchTime * 1000);
                            }
                            //如果下一个是图片
                            else {
                                convenientBanner.startTurning_image(picSwitchTime * 1000);
                            }
                        } else {
                            //如果播放完成,实现重播
                            if (isCompletionOrError) {
                                mMediaPlayer.reStart();
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                }
            }
        });
    }

    /**
     * 暂停后继续播放多媒体
     */
    private void pausedContinuePlayMedia() {
        try {
            //如果在更新文件
            if (isUpdateFile) {
                return;
            }
            //如果锁屏了
            if (isLockScreen) {
                return;
            }

            //如果之前是在播放视频
            if (isPlayVideoMode) {
                //继续播放视频
                mMediaPlayer.onStart();
            } else {
                //如果多媒体数量大于1才轮播
                if (!convenientBanner.isTurning() && mediaUrlList.size() > 1) {
                    //如果需要切换
                    if ("1".equals(mediaSwitch)) {
                        //开始轮播
                        convenientBanner.startTurning(picSwitchTime * 1000);
                    } else {
                        if (convenientBanner.isTurning()) {
                            convenientBanner.stopTurning();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }

    /**
     * 暂停多媒体播放
     */
    private void pauseMediaPlay() {
        try {
            //如果是在播放视频
            if (isPlayVideoMode) {
                //暂停播放视频
                mMediaPlayer.onPause();
            } else {
                //如果正在轮播图片
                if (convenientBanner.isTurning()) {
                    //停止轮播图片
                    convenientBanner.stopTurning();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }

/************************定时锁屏部分开始*************************/

    /**
     * 自动关机线程
     */
    class TimeThread extends Thread implements Runnable {
        @Override
        public void run() {
            try {
                //定时锁屏或关机
                if (isAutoShutDownStart && lockSreenTimer != null) {
                    lockSreenTimer.cancel();
                    lockSreenTimer = null;
                }
                //标识未自动关机线程已经开启状态
                isAutoShutDownStart = true;
                lockSreenTimer = new Timer();
                //执行定时关机,定时检查关机，1秒钟1次
                lockSreenTimer.schedule(new LockScreenTask(), 0, 1000);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
            }
        }
    }


    /**
     * 锁屏定时线程
     */
    class LockScreenTask extends TimerTask {
        @Override
        public void run() {
            //如果当前时间等于关机时间
            if (AppUtils.getCurrentTime().equals(autoShutdown_time + ":00")) {
                //执行锁屏Handler
                bootOrlockScreenHandler.obtainMessage(Constants.SCREEN_TAG_LOCK).sendToTarget();
            }
        }

    }


    /**
     * 锁屏handler
     */
    private Handler bootOrlockScreenHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case Constants.SCREEN_TAG_LOCK://锁屏或关机
                    try {
                        //如果手机系统root了,执行关机
                        if (AppUtils.isRoot()) {
                            LogUtils.d("setting", "执行了关机");
                            //进入倒计时弹窗页面
                            ShutdownCountdownActivity.startShutdownCountdownActivity(mContext);

                        }
                        //如果没有root,执行锁屏
                        else {
                            LogUtils.d("setting", "执行了锁屏");
                            //执行锁屏
                            if (mDPM.isAdminActive(mAdminName)) {
                                mDPM.lockNow();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                    }

                    break;

            }
        }
    };


    @Override
    public void onScreenOn() {
        try {
            LogUtils.d("screen", "屏幕打开了");
            //屏幕解锁
            AppUtils.screenUnlock(mContext);
            //如果是在更新文件
            if (isUpdateFile) {
                return;
            }
            //标识未锁屏
            isLockScreen = false;
            //如果之前是在播放多媒体
            if (isPlayVideoMode) {
                //暂停后继续播放多媒体
                pausedContinuePlayMedia();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }

    @Override
    public void onScreenOff() {
        try {
            LogUtils.d("screen", "屏幕关闭了");
            //如何是在更新文件
            if (isUpdateFile) {
                return;
            }
            //标识锁屏了
            isLockScreen = true;
            //暂停多媒体轮播
            pauseMediaPlay();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }

    @Override
    public void onUserPresent() {
        LogUtils.d("screen", "解锁了");
    }

    /************************定时锁屏部分结束**************************/


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {

            case KeyEvent.KEYCODE_BACK:    //返回键
                LogUtils.d("onKeyDown", "back--->");
                //显示退出或设置网络选项弹窗
                showExitOrSettingNetWorkDialog(currTemplateId);

                return true;   //这里由于break会退出，所以我们自己要处理掉 不返回上一层

            case KeyEvent.KEYCODE_SETTINGS: //设置键
                LogUtils.d("onKeyDown", "setting--->");
                //显示退出或设置网络选项弹窗
                showExitOrSettingNetWorkDialog(currTemplateId);

                break;

            case KeyEvent.KEYCODE_MENU: //菜单键
                LogUtils.d("onKeyDown", "keycode_volume_mute--->");
                //显示退出或设置网络选项弹窗
                showExitOrSettingNetWorkDialog(currTemplateId);

                break;
            case KeyEvent.KEYCODE_HOME://
                LogUtils.d("onKeyDown", "keycode_home--->");

                return true;
        }

        return super.onKeyDown(keyCode, event);

    }

    /**
     * 显示退出或设置网络选项弹窗
     */
    private void showExitOrSettingNetWorkDialog(String templateId) {
        //关闭网络设置弹窗
        if (isShowSettingIp) {
            EventBus.getDefault().post(new CloseSettingIpEvent());
        }
        //显示设置
        DialogUtils.showOptionsDialog(mContext, templateId, quitPassword, isUseQuitPassword, new SettingConectListener() {
            @Override
            public void onConnectSet() {
                //进入网络设置页面
                enterSettingIp(mContext.getResources().getString(R.string.network_settings));
            }

            @Override
            public void onTemplateSelected() {
                //进入模板选择
                showTemplateSelectDialog();
            }

            @Override
            public void onCounterSelect() {
                //选择需要显示的窗口(窗口过滤)
                DialogUtils.showSelectCounterNoDialog(mContext, currSelectedCounterList, new OnSelectListener<ArrayList<CounterEntity>>() {
                    @Override
                    public void onSelected(ArrayList<CounterEntity> selectList) {
                        currSelectedCounterList.clear();
                        currSelectedCounterList.addAll(selectList);
                        //记住当前选择的窗口
                        SPUtils.put(mContext, SPUtils.CURR_SELECTED_COUNTER_LIST, new Gson().toJson(currSelectedCounterList));
                        if (currSelectedCounterList.size() > 0) {
                            //清除不在选中窗口的叫号信息
                            ArrayList<CallInfoEntity> list = new ArrayList<CallInfoEntity>();
                            for (int i = 0; i < call_info_list.size(); i++) {
                                CallInfoEntity entity = call_info_list.get(i);
                                int counterNo = 0;
                                if (!TextUtils.isEmpty(entity.getCounterNo())) {
                                    counterNo = Integer.parseInt(entity.getCounterNo());
                                }
                                if (AppUtils.isCounterSelected(currSelectedCounterList, counterNo)) {
                                    list.add(entity);
                                }
                            }
                            call_info_list.clear();
                            call_info_list.addAll(list);
                            list.clear();
                            //如果启用了模板
                            if (isEnalbeCallModel) {
                                //显示叫号信息
                                showCallInfoList();
                            }
                        }
                    }
                });
            }
        });
    }


    int templatePosition = 0;

    /**
     * 模板选择弹窗
     */
    private void showTemplateSelectDialog() {
        try {
            if (settingEntity_new == null) {
                ToastUtil.showShort(mContext, "配置文件为空");
                return;
            }
            final ArrayList<TemplateEntity> templateList = settingEntity_new.getTemplatesList();
            if (templateList == null || templateList.size() == 0) {
                ToastUtil.showShort(mContext, "配置文件模板为空");
                return;
            }

            if (!isAddTemplate(templateList, "1")) {
                //添加安卓本地模板1,名称、模板id、安卓本地模板id
                templateList.add(new TemplateEntity("医院窗口竖屏模板1", "0", "1"));
            }

            if (!isAddTemplate(templateList, "2")) {
                //添加安卓本地模板2,名称、模板id、安卓本地模板id
                templateList.add(new TemplateEntity("员工信息模板4", "0", "2"));
            }

            if (!isAddTemplate(templateList, "3")) {
                //添加安卓本地模板3,名称、模板id、安卓本地模板id
                templateList.add(new TemplateEntity("医院窗口竖屏模板2", "0", "3"));
            }

            if (!isAddTemplate(templateList, "4")) {
                //添加安卓本地模板4,名称、模板id、安卓本地模板id
                templateList.add(new TemplateEntity("员工信息竖屏模板1", "0", "4"));
            }

            if (!isAddTemplate(templateList, "5")) {
                //添加安卓本地模板5,名称、模板id、安卓本地模板id
                templateList.add(new TemplateEntity("员工信息模板5", "0", "5"));
            }

            if (!isAddTemplate(templateList, "6")) {
                //添加安卓本地模板5,名称、模板id、安卓本地模板id
                templateList.add(new TemplateEntity("医院窗口叫号模板", "0", "6"));
            }

            if (!isAddTemplate(templateList, "7")) {
                //添加安卓本地模板5,名称、模板id、安卓本地模板id
                templateList.add(new TemplateEntity("员工信息模板6", "0", "7"));
            }

            //设置选中状态
            for (int i = 0; i < templateList.size(); i++) {
                TemplateEntity entity = templateList.get(i);
                if (currTemplateId.equals(entity.getTemplateId())) {
                    //如果是本地模板
                    if ("0".equals(currTemplateId)) {
                        if (local_template_id.equals(entity.getLocal_template_id())) {
                            entity.setSelect(true);
                        } else {
                            entity.setSelect(false);
                        }
                    } else {
                        entity.setSelect(true);
                    }
                    templatePosition = i;
                } else {
                    entity.setSelect(false);
                }
            }

            final Dialog dialog = new Dialog(mContext, R.style.dialog_style_in_middle);
            //隐藏home导航栏
            hideNavigation(dialog);
            dialog.show();
            Window window = dialog.getWindow();
            final View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_template_select, null);
            window.setContentView(view);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //窗口列表
            ListView mListView = (ListView) view.findViewById(R.id.mListView);
            final LazyAdapter mAdapter = new LazyAdapter<TemplateEntity>(mContext, templateList) {

                @Override
                public View layoutView(ArrayList<?> list, final int position, View mView, ArrayList<TemplateEntity> t) {
                    if (mView == null) {
                        mView = getLayoutInflater().inflate(R.layout.item_template, null);
                    }
                    //模板图片
                    ImageView iv_template = ViewHolderUtils.get(mView, R.id.iv_template);
                    //模板名称
                    TextView tv_template = ViewHolderUtils.get(mView, R.id.tv_template);
                    //选中图标
                    ImageView iv_check = ViewHolderUtils.get(mView, R.id.iv_check);
                    //获取模板对象
                    final TemplateEntity entity = templateList.get(position);
                    //设置模板图片
                    if ("1".equals(entity.getTemplateId())) {
                        iv_template.setImageResource(R.mipmap.ic_template_1);
                    } else if ("2".equals(entity.getTemplateId())) {
                        iv_template.setImageResource(R.mipmap.ic_template_2);
                    } else if ("3".equals(entity.getTemplateId())) {
                        iv_template.setImageResource(R.mipmap.ic_template_3);
                    } else if ("4".equals(entity.getTemplateId())) {
                        iv_template.setImageResource(R.mipmap.ic_template_4);
                    } else if ("5".equals(entity.getTemplateId())) {
                        iv_template.setImageResource(R.mipmap.ic_template_5);
                    } else if ("6".equals(entity.getTemplateId())) {
                        iv_template.setImageResource(R.mipmap.ic_template_6);
                    } else if ("7".equals(entity.getTemplateId())) {
                        iv_template.setImageResource(R.mipmap.ic_template_7);
                    } else if ("8".equals(entity.getTemplateId())) {
                        iv_template.setImageResource(R.mipmap.ic_template_8);
                    } else if ("0".equals(entity.getTemplateId())) {
                        String local_temp_id = entity.getLocal_template_id();
                        if ("1".equals(local_temp_id)) {
                            iv_template.setImageResource(R.mipmap.ic_template_local_1);
                        } else if ("2".equals(local_temp_id)) {
                            iv_template.setImageResource(R.mipmap.ic_template_local_2);
                        } else if ("3".equals(local_temp_id)) {
                            iv_template.setImageResource(R.mipmap.ic_template_local_3);
                        } else if ("4".equals(local_temp_id)) {
                            iv_template.setImageResource(R.mipmap.ic_template_local_4);
                        } else if ("5".equals(local_temp_id)) {
                            iv_template.setImageResource(R.mipmap.ic_template_local_5);
                        } else if ("6".equals(local_temp_id)) {
                            iv_template.setImageResource(R.mipmap.ic_template_local_6);
                        } else if ("7".equals(local_temp_id)) {
                            iv_template.setImageResource(R.mipmap.ic_template_local_7);
                        } else {
                            iv_template.setImageResource(0);
                        }
                    } else {
                        iv_template.setImageResource(0);
                    }
                    //设置显示窗口号
                    tv_template.setText(entity.getTemplateDescription());
                    //设置是否选择中
                    iv_check.setVisibility(entity.isSelect() ? View.VISIBLE : View.GONE);

                    return mView;
                }
            };
            //设置适配
            mListView.setAdapter(mAdapter);
            mListView.scrollTo(0, 0);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TemplateEntity entity = templateList.get(position);
                    if (!entity.isSelect()) {
                        entity.setSelect(true);
                        for (int i = 0; i < templateList.size(); i++) {
                            if (i != position) {
                                templateList.get(i).setSelect(false);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                    templatePosition = position;
                }
            });
            //设置选中焦点位置
            mListView.setSelection(templatePosition);
            //确定按钮
            TextView btn_confirm = (TextView) view.findViewById(R.id.btn_confirm);
            //取消按钮
            TextView btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);
            //设置确定按钮的点击事件
            btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    try {
                        //获取选中模板对象
                        TemplateEntity entity = templateList.get(templatePosition);
                        //保存选中的模板id
                        if (entity != null && ((!"0".equals(currTemplateId) && !currTemplateId.equals(entity.getTemplateId()))
                                || ("0".equals(currTemplateId) && !local_template_id.equals(entity.getLocal_template_id())))) {
                            //设置当前模板id
                            currTemplateId = entity.getTemplateId();
                            //清空过号数据
                            if ("2".equals(currTemplateId) || "3".equals(currTemplateId) || "8".equals(currTemplateId)) {
                                //清空医院模板2，模板3模板的当前叫号 模板8
                                currCallInfoList.clear();
                                call_info_list.clear();
                            }
                            //记住当前模板id
                            SPUtils.put(mContext, SPUtils.TEMPLATE_ID, currTemplateId);
                            isChangeTemplateId = true;
                            if ("0".equals(currTemplateId)) {
                                //记住安卓本地模板id
                                local_template_id = entity.getLocal_template_id();
                                SPUtils.put(mContext, SPUtils.LOCAL_TEMPLATE_ID, local_template_id);
                                //设置本地模板1隐藏
                                llyt_local_hospital_template_1.setVisibility(View.GONE);
                                //设置本地模板2隐藏
                                llyt_local_template_2.setVisibility(View.GONE);
                                //设置本地模板3隐藏
                                llyt_local_template_3.setVisibility(View.GONE);
                                //设置本地模板4隐藏
                                llyt_local_template_4.setVisibility(View.GONE);
                                //设置本地模板5隐藏
                                llyt_local_template_5.setVisibility(View.GONE);
                                //设置本地模板6隐藏
                                llyt_local_template_6.setVisibility(View.GONE);
                                //设置本地模板7隐藏
                                llyt_local_template_7.setVisibility(View.GONE);
                                //如果是本地模板1
                                if ("1".equals(local_template_id)) {
                                    //设置竖屏
                                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                    llyt_local_hospital_template_1.setVisibility(View.VISIBLE);
                                } else if ("2".equals(local_template_id)) {
                                    //设置横屏
                                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                                    llyt_local_template_2.setVisibility(View.VISIBLE);
                                } else if ("3".equals(local_template_id)) {
                                    //设置竖屏
                                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                    llyt_local_template_3.setVisibility(View.VISIBLE);
                                } else if ("4".equals(local_template_id)) {
                                    //设置竖屏
                                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                    //设置本地模板4显示
                                    llyt_local_template_4.setVisibility(View.VISIBLE);
                                } else if ("5".equals(local_template_id)) {
                                    //设置竖屏
                                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                                    llyt_local_template_5.setVisibility(View.VISIBLE);
                                } else if ("6".equals(local_template_id)) {
                                    //设置竖屏
                                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                                    //设置本地模板6显示
                                    llyt_local_template_6.setVisibility(View.VISIBLE);
//                                    //设置本地模板7隐藏
                                } else if ("7".equals(local_template_id)) {
                                    //设置竖屏
                                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                                    //设置本地模板7显示
                                    llyt_local_template_7.setVisibility(View.VISIBLE);
                                }

                            } else {
                                //隐藏本地所有模板
                                llyt_local_hospital_template_1.setVisibility(View.GONE);
                                llyt_local_template_2.setVisibility(View.GONE);
                                llyt_local_template_3.setVisibility(View.GONE);
                                llyt_local_template_4.setVisibility(View.GONE);
                                llyt_local_template_5.setVisibility(View.GONE);
                                llyt_local_template_6.setVisibility(View.GONE);
                                llyt_local_template_7.setVisibility(View.GONE);
                            }
                            //解析配置文件
                            parseConfig();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            //设置取消按钮的点击事件
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setCanceledOnTouchOutside(false);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }

    /**
     * 判断是否已经添加模板
     *
     * @param templateList 模板集合
     * @param localTempId  本地模板id
     * @return
     */
    private boolean isAddTemplate(ArrayList<TemplateEntity> templateList, String localTempId) {
        boolean isAdd = false;
        if (templateList != null) {
            for (int i = 0; i < templateList.size(); i++) {
                TemplateEntity entity = templateList.get(i);
                if ("0".equals(entity.getTemplateId())
                        && !TextUtils.isEmpty(localTempId)
                        && localTempId.equals(entity.getLocal_template_id())) {
                    isAdd = true;
                }
            }

        }
        return isAdd;
    }


    /**
     * 接收关机倒计时Event,如果在更新，关闭更新窗口，
     * 取消关机时，显示更新弹窗
     *
     * @param event
     */
    @SuppressWarnings("UnusedDeclaration")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ShutdownEvent event) {
        if (event != null) {
            try {
                //如果在更新文件
                if (isUpdateFile) {
                    int tag = event.getTag();
                    //弹出关机倒计时
                    if (tag == 1) {
                        dismissProgressDialog();
                    }
                    //取消关机
                    else if (tag == 2) {
                        showProgressDialog();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
            }
        }
    }


    /**
     * 接收连接Event,连接服务器
     *
     * @param event
     */
    @SuppressWarnings("UnusedDeclaration")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ChangeIpEvent event) {
        try {
            if (event != null) {
                //获取服务器IP
                String serviceIp = event.getServiceIp();
                /*窗口号*/
                int counterNo = event.getCounterNo();
                //如果窗口号改变了或ip改变了,清空员工信息
                if (!SocketUtils.SERCIVER_IP.equals(serviceIp)
                        || SocketUtils.COUNTER_NO != counterNo) {
                    //标识是手动连接
                    isManualConnect = true;
                    //清空医院模板2，id为3模板的当前叫号
                    currCallInfoList.clear();
                    call_info_list.clear();
                    //清空员工信息
                    cleanEmployeeInfo();
                    //标识默认无头像
                    isHasHeadPic = false;
                    //如果改变了ip
                    if (!SocketUtils.SERCIVER_IP.equals(serviceIp)) {
                        LogUtils.writeLogtoFile("连接服务器", "改变了IP");
                        //清空叫号信息
                        setEmptyTicketInfo();
                    }

                    //如果改变窗口号
                    if (SocketUtils.COUNTER_NO != counterNo) {
                        LogUtils.writeLogtoFile("连接服务器", "改变了窗口号,改变的窗口号为" + counterNo);
                        //清空叫号信息
                        setEmptyTicketInfo();
                    }

                    //记住服务器ip
                    SPUtils.put(mContext, SPUtils.SERVICE_IP, serviceIp);
                    //记住窗口号
                    SPUtils.put(mContext, SPUtils.COUNTER_NO, counterNo);
                    //记住之前的服务ip
                    SocketUtils.SERCIVER_IP_OLD = SocketUtils.SERCIVER_IP;
                    //设置连接服务ip
                    SocketUtils.SERCIVER_IP = serviceIp;
                    //设置数据库ip
                    SocketUtils.DATABASE_IP = serviceIp;
                    //设置连接窗口号
                    SocketUtils.COUNTER_NO = counterNo;
                    //重连Socket
                    connectSocket();
                }
                //窗口号和Ip未发生改变
                else if ((mClient != null && mClient.isColsed()) || mClient == null) {
                    //标识是手动连接
                    isManualConnect = true;
                    //连接Socket
                    connectSocket();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }

    /**
     * 接收网络变化Socket消息Event
     *
     * @param event
     */
    @SuppressWarnings("UnusedDeclaration")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final NetworkEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (event != null) {
                        //如果网络断开
                        if (!event.isHasNet()) {
                            dismissProgressDialog();
                            LogUtils.writeLogtoFile("连接服务", "网络已断开");
                            ToastUtil.showLong(mContext, "网络已断开");
                            //断开连接
                            if (mClient != null) {
                                mClient.disconnect();
                            }
                            //关闭检查配置文件定时器
                            if (checkSettingFileTmer != null) {
                                checkSettingFileTmer.cancel();
                                checkSettingFileTmer = null;
                            }
                            //标识默认没有头像
                            isHasHeadPic = false;
                            //标识不是在更新
                            isUpdateFile = false;
                            //标识不是手动
                            isManualConnect = false;
                            //如果没有显示设置ip弹窗并且不是第一次进入
                            if (!isShowSettingIp && !is_enter_first) {
                                enterSettingIp("网络已断开，连接服务器失败");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                }
            }
        });

    }


    /**
     * 清空叫号信息
     */
    private void setEmptyTicketInfo() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    //如果是模板1或模板4
                    if ("1".equals(currTemplateId) || "4".equals(currTemplateId)) {
                        //设置当前票号为空
                        mWebView.loadUrl("javascript:SetValue('CurrCallTicket','" + "\\&nbsp;" + "')");
                        //设置当前票号名称为空
                        mWebView.loadUrl("javascript:SetValue('CurrCallName','" + "\\&nbsp;" + "')");
                        //设置显示下一叫号为空
                        mWebView.loadUrl("javascript:SetValue('NextCallTicket','" + "\\&nbsp;" + "')");
                        //设置显示下一叫号名称为空
                        mWebView.loadUrl("javascript:SetValue('NextCallName','" + "\\&nbsp;" + "')");
                        //设置默认头像
                        mWebView.loadUrl("javascript:SetValue('StaffHeadPath','" + "\\&nbsp;" + "')");
                    } else if ("5".equals(currTemplateId) || "6".equals(currTemplateId)) {
                        //当前呼叫票号
                        mWebView.loadUrl("javascript:SetValue('CallInfo','" + "\\&nbsp;" + "')");
                        //设置默认头像
                        mWebView.loadUrl("javascript:SetValue('StaffHeadPath','" + "\\&nbsp;" + "')");
                    } else if ("7".equals(currTemplateId)) {
                        //设置职称为空
                        mWebView.loadUrl("javascript:SetValue('StaffTitle','" + "\\&nbsp;" + "')");
                        //设置业务员名称为空
                        mWebView.loadUrl("javascript:SetValue('StaffName','" + "\\&nbsp;" + "')");
                        //设置叫号为空
                        mWebView.loadUrl("javascript:SetValue('CurrCallTicket','" + "\\&nbsp;" + "')");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
                }
            }
        });
    }

    /**
     * 设置本地模板叫号为空
     */
    private void setEmptyLocalTempCallInfo() {

        //如果是安卓本地模板
        if ("0".equals(currTemplateId)) {
            //如果安卓本地模板1
            if ("1".equals(local_template_id)) {
                //设置正在就诊为空
                tv_curr_call_v1.setText("");
                //设置等候就诊为空
                tv_wait_call_1_v1.setText("");
                tv_wait_call_2_v1.setText("");
            }
            //如果是本地模板2
            else if ("2".equals(local_template_id)) {
                //清空当前叫号
                tv_curr_call_h1.setText("");
                //清空下一叫号
                tv_next_call_h1.setText("");
            }
            //如果是本地模板3
            else if ("3".equals(local_template_id)) {
                //设置正在就诊为空
                tv_curr_call_v2.setText("");
                //设置等候就诊为空
                tv_wait_call_1_v2.setText("");
                tv_wait_call_2_v2.setText("");
                tv_wait_call_3_v2.setText("");
                tv_wait_call_4_v2.setText("");
            }
            //如果是本地模板4
            else if ("4".equals(local_template_id)) {
                //设置正在就诊为空
                tv_curr_call_v3.setText("");
                //设置等候就诊为空
                tv_wait_call_1_v3.setText("");
                tv_wait_call_2_v3.setText("");
            }
            //如果是本地模板5
            else if ("5".equals(local_template_id)) {
                //设置正在就诊为空
                tv_curr_call_t5.setText("");
                //设置等候就诊为空
                tv_next_call_t5.setText("");
            }
            //如果是本地模板6
            else if ("6".equals(local_template_id)) {
                //设置正在就诊为空
                tv_curr_call_t6.setText("");
                //设置等候就诊为空
                tv_next_call_t6.setText("");
            }
            //如果是本地模板7
            else if ("7".equals(local_template_id)) {
                //设置正在就诊为空
                tv_curr_call_t7.setText("");
                //设置等候就诊为空
                tv_next_call_t7.setText("");
            }
        }

    }


    /**
     * 设置屏幕方向
     *
     * @param entity
     */
    private void setScreenOrientation(TemplateEntity entity) {
        if (entity != null) {
            //如果分辨率的高度大于宽度
            if (entity.getHeight() > entity.getWidth()) {
                //设置竖屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                //设置横屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }


    /**
     * 退出连接
     */
    private void exitConnect() {
        try {
            //标识不需要自动连接socket
            //关闭自动连接定时器
            if (connectTimer != null) {
                connectTimer.cancel();
                connectTimer = null;
            }
            //断开连接
            if (mClient != null) {
                mClient.disconnect();
            }
            //关闭检查配置文件定时器
            if (checkSettingFileTmer != null) {
                checkSettingFileTmer.cancel();
                checkSettingFileTmer = null;
            }

            if (waitingCallTimer != null) {
                waitingCallTimer.cancel();
                waitingCallTimer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.d("state", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d("state", "onStop");
        //关闭自动连接定时器
        if (connectTimer != null) {
            connectTimer.cancel();
            connectTimer = null;
        }
        //断开连接
        if (mClient != null) {
            mClient.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d("state", "onDestroy");
        try {
            //退出连接
            exitConnect();
            //清空视频播放
            if (mMediaPlayer != null) {
                mMediaPlayer.onDestroy();
            }
            //注销屏幕监听
            if (mScreenListener != null) {
                mScreenListener.unregisterListener();
            }
            //退出app
            AppUtils.exitApp();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }


}