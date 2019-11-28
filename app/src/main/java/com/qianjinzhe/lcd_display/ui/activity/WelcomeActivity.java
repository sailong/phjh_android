package com.qianjinzhe.lcd_display.ui.activity;

import android.os.Handler;

import com.qianjinzhe.lcd_display.R;
import com.qianjinzhe.lcd_display.ui.activity.base.BaseActivity;
import com.qianjinzhe.lcd_display.util.SPUtils;

/******************************************************
 * Copyrights @ 2018，Shenzhen Qianjinzhe Technologies Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              WelcomeActivity.java
 * Description：
 *              欢迎页面
 * Author:
 *              youngHu
 * Finished：
 *              2018年09月20日
 ********************************************************/

public class WelcomeActivity extends BaseActivity {


    @Override
    public int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    public void initView() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //判断是否注册
                boolean isReg = (boolean) SPUtils.get(mContext, SPUtils.IS_REGISTER, false);
                if (isReg) {
                    //进入主页
                    MainActivity.startMainActivity(mContext);
                } else {
                    //进入注册页面
                    RegisterActivity.startRegisterActivity(mContext);
                }
                finish();

            }
        }, 1000);

    }


}
