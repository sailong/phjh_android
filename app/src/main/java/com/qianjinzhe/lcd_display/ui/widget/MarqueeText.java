package com.qianjinzhe.lcd_display.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/******************************************************
 * Copyrights @ 2018，Shenzhen Qianjinzhe Technologies Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              MarqueeText.java
 * Description：
 *              滚动TextView
 * Author:
 *              youngHu
 * Finished：
 *              2018年06月22日
 ********************************************************/
@SuppressLint("AppCompatCustomView")
public class MarqueeText extends TextView implements Runnable {

    private int currentScrollX = 0;// 当前滚动的位置
    private boolean isStop = false;
    private int textWidth;
    private boolean isMeasure = false;
    private String myContext = "";
    private int vWidth;
    private int mySpeed = 5;
    private Boolean l2r = true;

    //getPaint()获取系统画笔
    public MarqueeText(Context context) {
        super(context);
    }

    public MarqueeText(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public MarqueeText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (!isMeasure) {// 文字宽度只需获取一次就可以了
                textWidth = (int) getPaint().measureText(myContext);
                vWidth = getWidth();
                isMeasure = true;
            }
            float baseline = getHeight() / 2 + getPaint().getTextSize() / 2 - getPaint().getFontMetrics().descent / 2;
            canvas.drawText(myContext, currentScrollX, baseline, getPaint());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setGravity(int gravity) {
        super.setGravity(gravity);
    }

    @Override
    public void run() {
        if (isStop) {
            return;
        }
        if (!l2r) {//向左运动
            currentScrollX -= mySpeed;// 滚动速度
            if (currentScrollX < 0) {
                if (Math.abs(currentScrollX) >= textWidth) {
                    currentScrollX = vWidth;
                }
            }
        }
        if (l2r) {//由左向右运动
            currentScrollX += mySpeed;// 滚动速度
            if (currentScrollX >= vWidth) {
                currentScrollX = -textWidth;
            }
        }
        invalidate();
        postDelayed(this, 5);
    }

    // 开始滚动
    public void startScroll() {
        isStop = false;
        this.removeCallbacks(this);
        post(this);
    }

    // 停止滚动
    public void stopScroll() {
        isStop = true;
    }


    public String getMyContext() {
        return myContext;
    }

    public void setMyContext(String myContext) {
        this.myContext = myContext;
        textWidth = (int) getPaint().measureText(myContext);
    }

    public int getMySpeed() {
        return mySpeed;
    }

    public void setMySpeed(int mySpeed) {
        this.mySpeed = mySpeed;
        if (mySpeed <= 0) {
            this.mySpeed = 1;
        }
        if (mySpeed >= 15) {
            this.mySpeed = 15;
        }
    }

    public Boolean getL2r() {
        return l2r;
    }

    public void setL2r(Boolean l2r) {
        this.l2r = l2r;
    }

    public void setCurrentScrollX(int currentScrollX) {
        this.currentScrollX = currentScrollX;
    }
}
