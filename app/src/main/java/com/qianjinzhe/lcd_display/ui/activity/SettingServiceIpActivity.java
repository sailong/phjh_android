package com.qianjinzhe.lcd_display.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.qianjinzhe.lcd_display.R;
import com.qianjinzhe.lcd_display.bean.CounterSettingEntity;
import com.qianjinzhe.lcd_display.event.ChangeIpEvent;
import com.qianjinzhe.lcd_display.event.CloseSettingIpEvent;
import com.qianjinzhe.lcd_display.ui.activity.base.BaseActivity;
import com.qianjinzhe.lcd_display.ui.adapter.LazyAdapter;
import com.qianjinzhe.lcd_display.util.KeyBoardUtils;
import com.qianjinzhe.lcd_display.util.LogUtils;
import com.qianjinzhe.lcd_display.util.SPUtils;
import com.qianjinzhe.lcd_display.util.ToastUtil;
import com.qianjinzhe.lcd_display.util.ViewHolderUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

import static com.qianjinzhe.lcd_display.util.DialogUtils.hideNavigation;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              SettingServiceIpActivity.java
 * Description：
 *              设置服务ip弹窗
 * Author:
 *              youngHu
 * Finished：
 *              2018年06月06日
 ********************************************************/

public class SettingServiceIpActivity extends BaseActivity implements View.OnFocusChangeListener {
    /**标题*/
    @Bind(R.id.tv_title)
    TextView tv_title;
    /**服务器ip输入框*/
    @Bind(R.id.et_ip_1)
    EditText et_ip_1;
    @Bind(R.id.et_ip_2)
    EditText et_ip_2;
    @Bind(R.id.et_ip_3)
    EditText et_ip_3;
    @Bind(R.id.et_ip_4)
    EditText et_ip_4;

    /**窗口编号*/
    @Bind(R.id.tv_counter_no)
    TextView tv_counter_no;
    /**窗口编号选择箭头*/
    @Bind(R.id.iv_arrow)
    ImageView iv_arrow;

    /**连接按钮*/
    @Bind(R.id.btn_confirm)
    TextView btn_confirm;
    /**取消按钮*/
    @Bind(R.id.btn_cancel)
    TextView btn_cancel;

    /**服务器ip输入布局*/
    @Bind(R.id.llyt_service_ip)
    LinearLayout llyt_service_ip;
    /**窗口号选择布局*/
    @Bind(R.id.llyt_counter_no)
    LinearLayout llyt_counter_no;

    /**服务器IP*/
    private String serviceIp = "192.168.0.100";
    /**窗口号集合*/
    private ArrayList<CounterSettingEntity> counterList = new ArrayList<CounterSettingEntity>();
    /**窗口编号*/
    private int counterNo = 1;
    /**标题*/
    private String title = "";

    /**
     * 进入输入ip弹窗页面
     * @param context
     */
    public static void startSettingServiceIpActivity(Context context, String title) {
        Intent intent = new Intent(context, SettingServiceIpActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void initData() {
        super.initData();
        try {
            setEvent();
            //获取标题
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                title = bundle.getString("title", "");
            }
            //服务器ip
            serviceIp = (String) SPUtils.get(mContext, SPUtils.SERVICE_IP, "192.168.0.100");
            //窗口号
            counterNo = (int) SPUtils.get(mContext, SPUtils.COUNTER_NO, 1);
            counterList.clear();
            //获取窗口号
            for (int i = 0; i <= 998; i++) {
                CounterSettingEntity entity = new CounterSettingEntity();
                entity.setCounterNo(i + "");
                entity.setSelect(i == counterNo ? true : false);
                counterList.add(entity);
            }
        }catch (Exception e){
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting_service_ip;
    }

    @Override
    public void initView() {
        try {
            //设置标题
            tv_title.setText(title);
            //标识已经显示该页面
            isShowSettingIp = true;
            //设置服务器ip1输入监听
            et_ip_1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String ip1 = s.toString().trim();
                    if (ip1.length() == 3) {
                        if (Integer.parseInt(ip1) <= 255) {
                            et_ip_2.requestFocus();
                            et_ip_2.setSelection(et_ip_2.getText().toString().length());
                        } else {
                            et_ip_1.setText("255");
                        }
                    }
                }
            });

            //设置服务器ip2输入监听
            et_ip_2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String ip2 = s.toString().trim();
                    if (ip2.length() == 3) {
                        if (Integer.parseInt(ip2) <= 255) {
                            et_ip_3.requestFocus();
                            et_ip_3.setSelection(et_ip_3.getText().toString().length());
                        } else {
                            et_ip_2.setText("255");
                        }
                    }
                }
            });
            //设置服务器ip3输入监听
            et_ip_3.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String ip3 = s.toString().trim();
                    if (ip3.length() == 3) {
                        if (Integer.parseInt(ip3) <= 255) {
                            et_ip_4.requestFocus();
                            et_ip_3.setSelection(et_ip_3.getText().toString().length());
                        } else {
                            et_ip_3.setText("255");
                        }
                    }

                }
            });
            //设置服务器ip4输入监听
            et_ip_4.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String ip4 = s.toString().trim();
                    if (ip4.length() == 3) {
                        if (Integer.parseInt(ip4) > 255) {
                            et_ip_4.setText("255");
                        }
                    }

                }
            });

            //如果服务器ip不为空
            if (!TextUtils.isEmpty(serviceIp)) {
                String[] s_ips = serviceIp.split("\\.");
                //设置段
                et_ip_1.setText(s_ips[0].trim());
                et_ip_2.setText(s_ips[1].trim());
                et_ip_3.setText(s_ips[2].trim());
                et_ip_4.setText(s_ips[3].trim());
            }

            //设置窗口号
            tv_counter_no.setText(counterNo + "");

            //设置焦点监听事件
            et_ip_1.setOnFocusChangeListener(this);
            et_ip_2.setOnFocusChangeListener(this);
            et_ip_3.setOnFocusChangeListener(this);
            et_ip_4.setOnFocusChangeListener(this);
            llyt_counter_no.setOnFocusChangeListener(this);
            btn_confirm.setOnFocusChangeListener(this);
            btn_cancel.setOnFocusChangeListener(this);
            //刚进入时，设置服务器ip输入布局选中，其中的ip1获取焦点
            et_ip_1.requestFocus();
            et_ip_1.setFocusableInTouchMode(true);
            et_ip_1.setSelection(et_ip_1.getText().toString().length());
        }catch (Exception e){
            e.printStackTrace();
            LogUtils.writeLogtoFile("异常日志", Log.getStackTraceString(e));
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        switch (v.getId()) {

            case R.id.et_ip_1://服务器ip
            case R.id.et_ip_2:
            case R.id.et_ip_3:
            case R.id.et_ip_4:
                if (hasFocus) {
                    llyt_service_ip.setSelected(true);
                }

                break;

            case R.id.llyt_counter_no://窗口号
            case R.id.btn_confirm://确定按钮
            case R.id.btn_cancel://取消按钮
                if (hasFocus) {
                    llyt_service_ip.setSelected(false);
                }

                break;

        }

    }

    @OnClick({R.id.btn_confirm, R.id.btn_cancel, R.id.llyt_counter_no})
    public void onClick(View view) {
        //关闭软键盘
        KeyBoardUtils.closeKeybord(mContext);
        switch (view.getId()) {
            case R.id.btn_confirm://连接按钮
                //服务器ip
                String ip1 = et_ip_1.getText().toString().trim();
                String ip2 = et_ip_2.getText().toString().trim();
                String ip3 = et_ip_3.getText().toString().trim();
                String ip4 = et_ip_4.getText().toString().trim();
                if (TextUtils.isEmpty(ip1)
                        || TextUtils.isEmpty(ip2)
                        || TextUtils.isEmpty(ip3)
                        || TextUtils.isEmpty(ip4)) {
                    ToastUtil.showShort(mContext, R.string.input_service_ip_tips);
                    return;
                }
                serviceIp = ip1 + "." + ip2 + "." + ip3 + "." + ip4;

                //发送Event到首页，连接服务器
                EventBus.getDefault().post(new ChangeIpEvent(serviceIp, counterNo));
                finish();

                break;

            case R.id.btn_cancel://取消按钮
                //关闭页面
                finish();

                break;

            case R.id.llyt_counter_no://选择窗口编号
                //显示窗口弹窗
                showSelectCounterNoDialog();

                break;
        }
    }


    /**
     * 选择窗口弹窗
     */
    private void showSelectCounterNoDialog() {
        final Dialog dialog = new Dialog(mContext, R.style.dialog_style_in_middle);
        //隐藏home导航栏
        hideNavigation(dialog);
        dialog.show();
        Window window = dialog.getWindow();
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_select_counter_no, null);
        window.setContentView(view);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //窗口列表
        ListView mListView = (ListView) view.findViewById(R.id.mListView);
        final LazyAdapter mAdapter = new LazyAdapter<CounterSettingEntity>(mContext, counterList) {

            @Override
            public View layoutView(ArrayList<?> list, final int position, View mView, ArrayList<CounterSettingEntity> t) {
                if (mView == null) {
                    mView = getLayoutInflater().inflate(R.layout.item_counter_no, null);
                }
                //窗口号
                TextView tv_counter = ViewHolderUtils.get(mView, R.id.tv_counter);
                //选中图标
                ImageView iv_check = ViewHolderUtils.get(mView, R.id.iv_check);
                //获取窗口对象
                final CounterSettingEntity entity = counterList.get(position);
                //设置显示窗口号
                tv_counter.setText("窗口" + entity.getCounterNo());
                //设置是否选择中
                iv_check.setVisibility(entity.isSelect() ? View.VISIBLE : View.GONE);

                return mView;
            }
        };
        //设置适配
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CounterSettingEntity entity = counterList.get(position);
                if (!entity.isSelect()) {
                    entity.setSelect(true);
                    for (int i = 0; i < counterList.size(); i++) {
                        if (i != position) {
                            counterList.get(i).setSelect(false);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        //设置选中焦点位置
        mListView.setSelection(counterNo);
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
                    //获取选中的窗口
                    CounterSettingEntity entity = getSelectCounter();
                    if (entity != null) {
                        counterNo = Integer.parseInt(entity.getCounterNo());
                        tv_counter_no.setText(entity.getCounterNo());
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
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {

            case KeyEvent.KEYCODE_HOME://
                LogUtils.d("onKeyDown", "keycode_home--->");

                return true;
        }

        return super.onKeyDown(keyCode, event);

    }

    /**
     * 获取选中的窗口
     * @return
     */
    private CounterSettingEntity getSelectCounter() {
        for (CounterSettingEntity entity : counterList) {
            if (entity.isSelect()) {
                return entity;
            }
        }

        return null;
    }


    /**
     * 接收连接成功Event,关闭弹窗
     * @param event
     */
    @SuppressWarnings("UnusedDeclaration")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CloseSettingIpEvent event) {
        if (event != null) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isShowSettingIp = false;
    }
}
