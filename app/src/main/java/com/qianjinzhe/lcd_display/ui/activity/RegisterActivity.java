package com.qianjinzhe.lcd_display.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qianjinzhe.lcd_display.R;
import com.qianjinzhe.lcd_display.ui.activity.base.BaseActivity;
import com.qianjinzhe.lcd_display.util.EncryptUtils;
import com.qianjinzhe.lcd_display.util.LogUtils;
import com.qianjinzhe.lcd_display.util.SPUtils;
import com.qianjinzhe.lcd_display.util.TextUtils;
import com.qianjinzhe.lcd_display.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;

/******************************************************
 * Copyrights @ 2019，Shenzhen Qianjinzhe Technologies Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              RegisterActivity.java
 * Description：
 *              注册页面
 * Author:
 *              youngHu
 * Finished：
 *              2019年03月16日
 ********************************************************/

public class RegisterActivity extends BaseActivity {
    /**注册码*/
    @Bind(R.id.et_reg_code)
    EditText et_reg_code;
    /**注册提示*/
    @Bind(R.id.tv_reg_tips)
    TextView tv_reg_tips;

    /**机器码*/
    private String machineCode = "";

    /**
     * 进入注册页面
     * @param context
     */
    public static void startRegisterActivity(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initView() {
        //设置提示文字
        setTipsText();
    }


    @OnClick({R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_register://注册
                //注册码
                String reg_code = et_reg_code.getText().toString();
                if (TextUtils.isEmpty(reg_code)) {
                    ToastUtil.showShort(mContext, R.string.input_reg_code);
                    return;
                }
                //如果注册码不为29或不包含-
                if (reg_code.length() != 29 || !reg_code.contains("-")) {
                    ToastUtil.showShort(mContext, R.string.input_reg_format_error);
                    return;
                }
                //如果注册码一致
                if(EncryptUtils.getRegCode(mContext).equalsIgnoreCase(reg_code)){
                    //记住已经注册
                    SPUtils.put(mContext,SPUtils.IS_REGISTER,true);
                    //进入主页
                    MainActivity.startMainActivity(mContext);
                    ToastUtil.showShort(mContext,R.string.reg_success);
                    finish();
                }else {
                    //提示注册码错误
                    ToastUtil.showShort(mContext,R.string.reg_fail);
                }

                break;
        }
    }


    /**
     * 设置提示文字
     */
    private void setTipsText(){
        try {
            //获取注册码
            machineCode = EncryptUtils.getMachineCode(mContext);
            LogUtils.d("机器码", machineCode);
            //获取提示文字
            String tips = String.format(getString(R.string.reg_tips_2), machineCode);
            //设置提示文字
            tv_reg_tips.setText(Html.fromHtml(tips));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
