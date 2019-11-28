package com.bigkoo.convenientbanner;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.PageTransformer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bigkoo.convenientbanner.adapter.CBPageAdapter;
import com.bigkoo.convenientbanner.animation.AccordionTransformer;
import com.bigkoo.convenientbanner.animation.BackgroundToForegroundTransformer;
import com.bigkoo.convenientbanner.animation.CubeInTransformer;
import com.bigkoo.convenientbanner.animation.CubeOutTransformer;
import com.bigkoo.convenientbanner.animation.DefaultTransformer;
import com.bigkoo.convenientbanner.animation.DepthPageTransformer;
import com.bigkoo.convenientbanner.animation.FlipHorizontalTransformer;
import com.bigkoo.convenientbanner.animation.FlipVerticalTransformer;
import com.bigkoo.convenientbanner.animation.ForegroundToBackgroundTransformer;
import com.bigkoo.convenientbanner.animation.RotateDownTransformer;
import com.bigkoo.convenientbanner.animation.RotateUpTransformer;
import com.bigkoo.convenientbanner.animation.StackTransformer;
import com.bigkoo.convenientbanner.animation.TabletTransformer;
import com.bigkoo.convenientbanner.animation.ZoomInTransformer;
import com.bigkoo.convenientbanner.animation.ZoomOutSlideTransformer;
import com.bigkoo.convenientbanner.animation.ZoomOutTranformer;
import com.bigkoo.convenientbanner.listener.CBPageChangeListener;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.bigkoo.convenientbanner.listener.OnItemLongClickListener;
import com.bigkoo.convenientbanner.view.CBLoopViewPager;
import com.qianjinzhe.lcd_display.R;
import com.qianjinzhe.lcd_display.bean.MediaEntity;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 页面翻转控件，极方便的广告栏
 * 支持无限循环，自动翻页，翻页特效
 * @author Sai 支持自动翻页
 */
public class ConvenientBanner extends LinearLayout {
    private List<MediaEntity> mDatas;
    private int[] page_indicatorId;
    private ArrayList<ImageView> mPointViews = new ArrayList<ImageView>();
    private CBPageChangeListener pageChangeListener;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private CBPageAdapter pageAdapter;
    private CBLoopViewPager viewPager;
    private ViewPagerScroller scroller;
    private ViewGroup loPageTurningPoint;
    private int autoTurningTime;
    private boolean turning;
    private boolean canTurn = false;
    private boolean manualPageable = true;
    private boolean canLoop = true;
    private Context mContext;
    /**长按弹窗的*/
    private ImageView btn_long_click;
    /**长按监听*/
    private OnItemLongClickListener onItemLongClickListener;

    public enum PageIndicatorAlign {
        ALIGN_PARENT_LEFT, ALIGN_PARENT_RIGHT, CENTER_HORIZONTAL
    }

    private AdSwitchTask adSwitchTask;

    public ConvenientBanner(Context context) {
        super(context);
        init(context);
    }

    public ConvenientBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ConvenientBanner);
        canLoop = a.getBoolean(R.styleable.ConvenientBanner_canLoop, true);
        a.recycle();
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ConvenientBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ConvenientBanner);
        canLoop = a.getBoolean(R.styleable.ConvenientBanner_canLoop, true);
        a.recycle();
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ConvenientBanner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ConvenientBanner);
        canLoop = a.getBoolean(R.styleable.ConvenientBanner_canLoop, true);
        a.recycle();
        init(context);
    }

    private void init(Context context) {
        View hView = LayoutInflater.from(context).inflate(R.layout.include_viewpager, this, true);
        viewPager = (CBLoopViewPager) hView.findViewById(R.id.cbLoopViewPager);
        btn_long_click = (ImageView) hView.findViewById(R.id.btn_long_click);
        loPageTurningPoint = (ViewGroup) hView.findViewById(R.id.loPageTurningPoint);
        initViewPagerScroll();
        //设置长按事件
        btn_long_click.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.onItemLongClick(viewPager.getRealItem());
                }
                return true;
            }
        });
        adSwitchTask = new AdSwitchTask(this);
    }

    /**
     * 定时线程
     */
    static class AdSwitchTask implements Runnable {

        private final WeakReference<ConvenientBanner> reference;

        AdSwitchTask(ConvenientBanner convenientBanner) {
            this.reference = new WeakReference<ConvenientBanner>(convenientBanner);
        }

        @Override
        public void run() {
            ConvenientBanner convenientBanner = reference.get();

            if (convenientBanner != null) {
                if (convenientBanner.viewPager != null && convenientBanner.turning) {
                    int page = convenientBanner.viewPager.getCurrentItem() + 1;
                    convenientBanner.viewPager.setCurrentItem(page);
                    convenientBanner.postDelayed(convenientBanner.adSwitchTask, convenientBanner.autoTurningTime);
                }
            }
        }
    }

    /**
     * 设置Viewpager
     * @param datas
     * @return
     */
    public ConvenientBanner setPages(List<MediaEntity> datas, int advertWith, int advertHeight) {
        this.mDatas = datas;
        pageAdapter = new CBPageAdapter(mContext, mDatas, advertWith, advertHeight);
        viewPager.setAdapter(pageAdapter, canLoop);
        viewPager.setCurrentItem(0);
        if (page_indicatorId != null) {
            setPageIndicator(page_indicatorId);
        }
        return this;
    }

    /**
     * 设置Viewpager
     * @param datas
     * @return
     */
    public ConvenientBanner setPages(List<MediaEntity> datas) {
        this.mDatas = datas;
        pageAdapter = new CBPageAdapter(mContext, mDatas);
        viewPager.setAdapter(pageAdapter, canLoop);
        viewPager.setCurrentItem(0);
        if (page_indicatorId != null) {
            setPageIndicator(page_indicatorId);
        }
        return this;
    }

    /**
     * 通知数据变化
     * 如果只是增加数据建议使用 notifyDataSetAdd()
     */
    public void notifyDataSetChanged() {
        viewPager.getAdapter().notifyDataSetChanged();
        if (page_indicatorId != null)
            setPageIndicator(page_indicatorId);
    }

    /**
     * 设置轮播暂停时间
     * @param autoTurningTime
     */
    public void setAutoTurningTime(int autoTurningTime) {
        this.autoTurningTime = autoTurningTime;
    }

    /**
     * 设置底部指示器是否可见
     *
     * @param visible
     */
    public ConvenientBanner setPointViewVisible(boolean visible) {
        loPageTurningPoint.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * 底部指示器资源图片
     *
     * @param page_indicatorId
     */
    public ConvenientBanner setPageIndicator(int[] page_indicatorId) {
        loPageTurningPoint.removeAllViews();
        mPointViews.clear();
        this.page_indicatorId = page_indicatorId;
        if (mDatas == null) return this;
        for (int count = 0; count < mDatas.size(); count++) {
            // 翻页指示的点
            ImageView pointView = new ImageView(getContext());
            LayoutParams params = new LayoutParams(25, 25);
            pointView.setLayoutParams(params);
            pointView.setPadding(5, 0, 5, 0);
            if (mPointViews.isEmpty())
                pointView.setImageResource(page_indicatorId[1]);
            else
                pointView.setImageResource(page_indicatorId[0]);
            mPointViews.add(pointView);
            loPageTurningPoint.addView(pointView);
        }
        pageChangeListener = new CBPageChangeListener(mPointViews,
                page_indicatorId);
        viewPager.setOnPageChangeListener(pageChangeListener);
        pageChangeListener.onPageSelected(viewPager.getRealItem());
        if (onPageChangeListener != null)
            pageChangeListener.setOnPageChangeListener(onPageChangeListener);

        return this;
    }

    /**
     * 指示器的方向
     * @param align  三个方向：居左 （RelativeLayout.ALIGN_PARENT_LEFT），居中 （RelativeLayout.CENTER_HORIZONTAL），居右 （RelativeLayout.ALIGN_PARENT_RIGHT）
     * @return
     */
    public ConvenientBanner setPageIndicatorAlign(PageIndicatorAlign align) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) loPageTurningPoint.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, align == PageIndicatorAlign.ALIGN_PARENT_LEFT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, align == PageIndicatorAlign.ALIGN_PARENT_RIGHT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, align == PageIndicatorAlign.CENTER_HORIZONTAL ? RelativeLayout.TRUE : 0);
        loPageTurningPoint.setLayoutParams(layoutParams);
        return this;
    }

    /***
     * 是否开启了翻页
     * @return
     */
    public boolean isTurning() {
        return turning;
    }

    /***
     * 开始翻页
     * @param autoTurningTime 自动翻页时间
     * @return
     */
    public ConvenientBanner startTurning(int autoTurningTime) {
        //如果是正在翻页的话先停掉
        if (turning) {
            stopTurning();
        }
        //设置可以翻页并开启翻页
        canTurn = true;
        this.autoTurningTime = autoTurningTime;
        turning = true;
        postDelayed(adSwitchTask, autoTurningTime);
        return this;
    }

    /***
     * 开始翻页
     * @return 播放完视频调用
     */
    public ConvenientBanner startTurning_video(int autoTurningTime) {
        //如果是正在翻页的话先停掉
        if (turning) {
            stopTurning();
        }
        //设置可以翻页并开启翻页
        canTurn = true;
        this.autoTurningTime = autoTurningTime;
        turning = true;
        int page = viewPager.getCurrentItem() + 1;
        viewPager.setCurrentItem(page);
        return this;
    }

    /***
     * 开始翻页
     * @return 播放完视频调用
     */
    public ConvenientBanner startTurning_image(int autoTurningTime) {
        //如果是正在翻页的话先停掉
        if (turning) {
            stopTurning();
        }
        //设置可以翻页并开启翻页
        canTurn = true;
        this.autoTurningTime = autoTurningTime;
        turning = true;
        postDelayed(adSwitchTask, 0);
        return this;
    }

    public void stopTurning() {
        turning = false;
        removeCallbacks(adSwitchTask);
    }

    /**
     * 自定义翻页动画效果
     *
     * @param transformer
     * @return
     */
    public ConvenientBanner setPageTransformer(PageTransformer transformer) {
        viewPager.setPageTransformer(true, transformer);
        return this;
    }


    /**
     * 设置ViewPager的滑动速度
     * */
    private void initViewPagerScroll() {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            scroller = new ViewPagerScroller(viewPager.getContext());
            mScroller.set(viewPager, scroller);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean isManualPageable() {
        return viewPager.isCanScroll();
    }

    public void setManualPageable(boolean manualPageable) {
        viewPager.setCanScroll(manualPageable);
    }

    //触碰控件的时候，翻页应该停止，离开的时候如果之前是开启了翻页的话则重新启动翻页
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

//        int action = ev.getAction();
//        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
//            // 开始翻页
//            if (canTurn) startTurning(autoTurningTime);
//        } else if (action == MotionEvent.ACTION_DOWN) {
//            // 停止翻页
//            if (canTurn) stopTurning();
//        }
        return super.dispatchTouchEvent(ev);
    }

    //获取当前的页面index
    public int getCurrentItem() {
        if (viewPager != null) {
            return viewPager.getRealItem();
        }
        return -1;
    }

    //设置当前的页面index
    public void setcurrentitem(int index) {
        if (viewPager != null) {
            viewPager.setCurrentItem(index);
        }
    }

    public ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return onPageChangeListener;
    }

    /**
     * 设置翻页监听器
     * @param onPageChangeListener
     * @return
     */
    public ConvenientBanner setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
        //如果有默认的监听器（即是使用了默认的翻页指示器）则把用户设置的依附到默认的上面，否则就直接设置
        if (pageChangeListener != null)
            pageChangeListener.setOnPageChangeListener(onPageChangeListener);
        else viewPager.setOnPageChangeListener(onPageChangeListener);
        return this;
    }

    public boolean isCanLoop() {
        return viewPager.isCanLoop();
    }

    /**
     * 监听item点击
     * @param onItemClickListener
     */
    public ConvenientBanner setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (onItemClickListener == null) {
            viewPager.setOnItemClickListener(null);
            return this;
        }
        viewPager.setOnItemClickListener(onItemClickListener);
        return this;
    }

    /**
     * 监听长按
     * @param onItemLongClickListener
     * @return
     */
    public ConvenientBanner setOnMyItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
        return this;
    }

    /**
     * 设置ViewPager的滚动速度
     * @param scrollDuration
     */
    public void setScrollDuration(int scrollDuration) {
        scroller.setScrollDuration(scrollDuration);
    }

    public int getScrollDuration() {
        return scroller.getScrollDuration();
    }

    public CBLoopViewPager getViewPager() {
        return viewPager;
    }

    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
        viewPager.setCanLoop(canLoop);
    }

    /**
     * 设置滑动动画 mViewPager.setPageTransformer(true,new CubeOutTransformer());
     */
    public void setScrollerAnimation(String animationNum) {
        if ("1".equals(animationNum)) {
            //产生1-16的随机数
            int num = (int) (Math.random() * 16 + 1);
            setAnimation(num);
        }
    }

    /**
     * 设置翻页动画
     * @param animationNum
     */
    private void setAnimation(int animationNum) {
        if (animationNum == 1) {
            viewPager.setPageTransformer(true, new AccordionTransformer());
        } else if (animationNum == 2) {
            viewPager.setPageTransformer(true, new BackgroundToForegroundTransformer());
        } else if (animationNum == 3) {
            viewPager.setPageTransformer(true, new CubeInTransformer());
        } else if (animationNum == 4) {
            viewPager.setPageTransformer(true, new CubeOutTransformer());
        } else if (animationNum == 5) {
            viewPager.setPageTransformer(true, new DefaultTransformer());
        } else if (animationNum == 6) {
            viewPager.setPageTransformer(true, new DepthPageTransformer());
        } else if (animationNum == 7) {
            viewPager.setPageTransformer(true, new FlipHorizontalTransformer());
        } else if (animationNum == 8) {
            viewPager.setPageTransformer(true, new FlipVerticalTransformer());
        } else if (animationNum == 9) {
            viewPager.setPageTransformer(true, new ForegroundToBackgroundTransformer());
        } else if (animationNum == 10) {
            viewPager.setPageTransformer(true, new RotateDownTransformer());
        } else if (animationNum == 11) {
            viewPager.setPageTransformer(true, new RotateUpTransformer());
        } else if (animationNum == 12) {
            viewPager.setPageTransformer(true, new StackTransformer());
        } else if (animationNum == 13) {
            viewPager.setPageTransformer(true, new TabletTransformer());
        } else if (animationNum == 14) {
            viewPager.setPageTransformer(true, new ZoomInTransformer());
        } else if (animationNum == 15) {
            viewPager.setPageTransformer(true, new ZoomOutSlideTransformer());
        } else if (animationNum == 16) {
            viewPager.setPageTransformer(true, new ZoomOutTranformer());
        }
    }


}
