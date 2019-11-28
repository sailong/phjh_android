package com.qianjinzhe.lcd_display.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.qianjinzhe.lcd_display.R;
import com.qianjinzhe.lcd_display.bean.CounterEntity;
import com.qianjinzhe.lcd_display.listener.DialogListener;
import com.qianjinzhe.lcd_display.listener.OnSelectListener;
import com.qianjinzhe.lcd_display.listener.SettingConectListener;
import com.qianjinzhe.lcd_display.manager.AppManager;
import com.qianjinzhe.lcd_display.ui.adapter.LazyAdapter;

import java.util.ArrayList;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              DialogUtils.java
 * Description：
 *              dialog弹窗工具类
 * Author:
 *              youngHu
 * Finished：
 *              2018年06月23日
 ********************************************************/

public class DialogUtils {

    /**
     * 显示安装弹窗
     * @param context
     * @param versionName
     * @param listener
     */
    public static void showInstallApkDialog(Context context, String versionName, final DialogListener listener) {

        //判断如果activity已经关闭，不运行dialog
        if (((Activity) context).isFinishing())
            return;
        final Dialog dialog = new Dialog(context, R.style.dialog_style_in_middle);
        //隐藏home导航栏
        hideNavigation(dialog);
        dialog.show();
        Window window = dialog.getWindow();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_install_apk, null);
        window.setContentView(view);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //版本提示
        TextView tv_install_tip = (TextView) dialog.findViewById(R.id.tv_install_tip);
        //取消按钮
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        //立即安装按钮
        Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
        //设置内容
        if (!TextUtils.isEmpty(versionName)) {
            tv_install_tip.setText(context.getString(R.string.update_install_text) + "V" + versionName + context.getString(R.string.update_install_text_2));
        }
        //设置立即安装按钮点击事件
        btn_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listener.onComplete();
                dialog.cancel();
            }
        });
        //设置取消按钮点击事件
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listener.onFail();
                dialog.cancel();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 隐藏底部home导航栏
     * @param dialog
     */
    public static void hideNavigation(final Dialog dialog) {
        dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        //布局位于状态栏下方
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        //全屏
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        //隐藏导航栏
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    uiOptions |= 0x00001000;
                } else {
                    uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                }
                dialog.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
            }
        });
    }


    /**
     * 选项弹窗
     * @param context
     * @param templateId 模板id
     * @param pwd 退出密码
     * @param isUseQuitPwd 是否使用退出密码
     * @param listener 操作回调
     */
    public static void showOptionsDialog(final Context context, String templateId, final String pwd, final boolean isUseQuitPwd, final SettingConectListener listener) {
        final Dialog dialog = new Dialog(context, R.style.dialog_style_in_middle);
        //隐藏home导航栏
        hideNavigation(dialog);
        dialog.show();
        Window window = dialog.getWindow();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_options, null);
        window.setContentView(view);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tv_left_op = (TextView) window.findViewById(R.id.tv_left_op);
        TextView tv_right_op = (TextView) window.findViewById(R.id.tv_right_op);
        tv_left_op.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_right_op.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //选项按钮
        TextView btn_network = (TextView) window.findViewById(R.id.btn_network);
        btn_network.setFocusable(true);
        btn_network.setFocusableInTouchMode(true);
        //模板选择按钮
        TextView btn_template_select = (TextView) window.findViewById(R.id.btn_template_select);
        //窗口过滤按钮
        TextView btn_counter_select = (TextView) window.findViewById(R.id.btn_counter_select);
        //中间分割线
        View v_space = window.findViewById(R.id.v_space);
        //退出按钮
        TextView btn_exit = (TextView) window.findViewById(R.id.btn_exit);
        //如果是综合屏显示模板
        if ("2".equals(templateId) || "8".equals(templateId)) {
            v_space.setVisibility(View.VISIBLE);
            btn_counter_select.setVisibility(View.VISIBLE);
            btn_template_select.setNextFocusDownId(R.id.btn_counter_select);
            btn_exit.setNextFocusUpId(R.id.btn_counter_select);
        } else {
            v_space.setVisibility(View.GONE);
            btn_counter_select.setVisibility(View.GONE);
            btn_template_select.setNextFocusDownId(R.id.btn_exit);
            btn_exit.setNextFocusUpId(R.id.btn_template_select);
        }
        //设置选项按钮的点击事件
        btn_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onConnectSet();
            }
        });
        //设置模板选择按钮的点击事件
        btn_template_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onTemplateSelected();
            }
        });
        //设置窗口过滤按钮的点击事件
        btn_counter_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onCounterSelect();
            }
        });
        //设置退出按钮的点击事件
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //如果需要密码
                if (isUseQuitPwd) {

                    showInputPwdDialog(context, pwd, new DialogListener() {
                        @Override
                        public void onComplete() {
                            AppManager.getInstance().closeAllActivity();
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(0);
                        }

                        @Override
                        public void onFail() {

                        }
                    });
                } else {
                    AppManager.getInstance().closeAllActivity();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                }
            }
        });
    }


    /**
     * 输入密码弹窗
     * @param context
     */
    public static void showInputPwdDialog(Context context, final String pwd, final DialogListener listener) {
        final Dialog dialog = new Dialog(context, R.style.dialog_style_in_middle);
        //隐藏home导航栏
        hideNavigation(dialog);
        dialog.show();
        Window window = dialog.getWindow();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_input_pwd, null);
        window.setContentView(view);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //密码输入框
        final TextView et_password = (EditText) window.findViewById(R.id.et_password);
        //确定按钮
        TextView btn_confirm = (TextView) window.findViewById(R.id.btn_confirm_pwd);
        //取消按钮
        TextView btn_cancel = (TextView) window.findViewById(R.id.btn_cancel_pwd);
        //设置确定按钮点击事件
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = et_password.getText().toString().trim();
                if (pwd.equals(input)) {
                    dialog.dismiss();
                    listener.onComplete();
                } else {
                    dialog.dismiss();
                }

            }
        });
        //取消按钮点击事件
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onFail();
            }
        });
    }


    /**
     * 强制登录弹窗
     * @param context
     * @param listener
     */
    /**是否点击了确定按钮*/
    private static boolean isConfirm = false;
    private static Dialog forcedLoginDialog;

    public static void showForcedLoginDialog(Context context, final DialogListener listener) {
        if (forcedLoginDialog != null && forcedLoginDialog.isShowing()) {
            return;
        }
        forcedLoginDialog = new Dialog(context, R.style.dialog_style_in_middle);
        //隐藏home导航栏
        hideNavigation(forcedLoginDialog);
        forcedLoginDialog.show();
        isConfirm = false;
        Window window = forcedLoginDialog.getWindow();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_forced_login, null);
        window.setContentView(view);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //确定按钮
        TextView btn_confirm = (TextView) window.findViewById(R.id.btn_confirm_pwd);
        btn_confirm.setFocusable(true);
        btn_confirm.setFocusableInTouchMode(true);
        //取消按钮
        TextView btn_cancel = (TextView) window.findViewById(R.id.btn_cancel_pwd);
        //设置确定按钮点击事件
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConfirm = true;
                forcedLoginDialog.dismiss();
                listener.onComplete();

            }
        });
        //取消按钮点击事件
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forcedLoginDialog.dismiss();

            }
        });

        forcedLoginDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!isConfirm) {
                    listener.onFail();
                }
            }
        });

    }


    /**
     * 选择窗口弹窗
     * @param mContext
     * @param selectList 之前选中的窗口列表
     * @param listener 选择回调
     */
    public static void showSelectCounterNoDialog(final Context mContext, final ArrayList<CounterEntity> selectList, final OnSelectListener listener) {
        final ArrayList<CounterEntity> counterList = new ArrayList<>();
        //获取窗口号
        for (int i = 1; i <= 999; i++) {
            CounterEntity entity = new CounterEntity();
            entity.setCounterNo(i);
            entity.setSelected(isCounterSelected(selectList, entity.getCounterNo()));
            counterList.add(entity);
        }
        final Dialog dialog = new Dialog(mContext, R.style.dialog_style_in_middle);
        //隐藏home导航栏
        hideNavigation(dialog);
        dialog.show();
        Window window = dialog.getWindow();
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_select_counter_no, null);
        window.setContentView(view);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //标题
        TextView tv_dialog_title = (TextView) window.findViewById(R.id.tv_dialog_title);
        tv_dialog_title.setText("请选择需要显示的窗口号");
        //窗口列表
        ListView mListView = (ListView) view.findViewById(R.id.mListView);
        final LazyAdapter mAdapter = new LazyAdapter<CounterEntity>(mContext, counterList) {

            @Override
            public View layoutView(ArrayList<?> list, final int position, View mView, ArrayList<CounterEntity> t) {
                if (mView == null) {
                    mView = LayoutInflater.from(mContext).inflate(R.layout.item_counter_no, null);
                }
                //窗口号
                TextView tv_counter = ViewHolderUtils.get(mView, R.id.tv_counter);
                //选中图标
                ImageView iv_check = ViewHolderUtils.get(mView, R.id.iv_check);
                //获取窗口对象
                final CounterEntity entity = counterList.get(position);
                //设置显示窗口号
                tv_counter.setText("窗口" + entity.getCounterNo());
                //设置是否选择中
                iv_check.setVisibility(entity.isSelected() ? View.VISIBLE : View.GONE);

                return mView;
            }
        };
        //设置适配
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CounterEntity entity = counterList.get(position);
                if (entity.isSelected()) {
                    entity.setSelected(false);
                } else {
                    entity.setSelected(true);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        //设置选中焦点位置
        if (selectList.size() > 0) {
            mListView.setSelection(selectList.get(0).getCounterNo());
        } else {
            mListView.setSelection(0);
        }
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
                    listener.onSelected(getSelectCounter(counterList));
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

    /**
     * 获取选择的窗口号
     * @param counterList
     * @return
     */
    public static ArrayList<CounterEntity> getSelectCounter(ArrayList<CounterEntity> counterList) {
        ArrayList<CounterEntity> list = new ArrayList<CounterEntity>();
        try {
            for (int i = 0; i < counterList.size(); i++) {
                CounterEntity entity = counterList.get(i);
                if (entity.isSelected()) {
                    list.add(entity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 判断是否选择
     * @param selectList 选中的窗口集合
     * @param counterNo 需要判断是否选中的窗口号
     * @return 选中返回true, 未选中返回false
     */
    public static boolean isCounterSelected(ArrayList<CounterEntity> selectList, int counterNo) {
        boolean isChecked = false;

        for (int k = 0; k < selectList.size(); k++) {
            CounterEntity entity = selectList.get(k);
            if (entity.getCounterNo() == counterNo) {
                isChecked = true;
                break;
            }
        }
        return isChecked;
    }


}
