package com.bigkoo.convenientbanner.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.view.CBLoopViewPager;
import com.qianjinzhe.lcd_display.R;
import com.qianjinzhe.lcd_display.bean.MediaEntity;
import com.qianjinzhe.lcd_display.manager.ImageManager;
import com.qianjinzhe.lcd_display.util.Constants;
import com.qianjinzhe.lcd_display.util.ScreenUtils;
import com.qianjinzhe.lcd_display.util.ViewHolderUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Sai on 15/7/29.
 */
public class CBPageAdapter extends PagerAdapter {
    protected List<MediaEntity> mDatas;
    private boolean canLoop = true;
    private CBLoopViewPager viewPager;
    private final int MULTIPLE_COUNT = 300;
    private Context mContext;
    private int advertWith = 0; //广告宽度
    private int advertHeight = 0;//广告高度

    public int toRealPosition(int position) {
        int realCount = getRealCount();
        if (realCount == 0)
            return 0;
        int realPosition = position % realCount;
        return realPosition;
    }

    @Override
    public int getCount() {
        return canLoop ? getRealCount() * MULTIPLE_COUNT : getRealCount();
    }

    public int getRealCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int realPosition = toRealPosition(position);

        View view = getView(realPosition, null, container);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        int position = viewPager.getCurrentItem();
        if (position == 0) {
            position = viewPager.getFristItem();
        } else if (position == getCount() - 1) {
            position = viewPager.getLastItem();
        }
        try {
            viewPager.setCurrentItem(position, false);
        } catch (IllegalStateException e) {
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
    }

    public void setViewPager(CBLoopViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public CBPageAdapter(Context mContext, List<MediaEntity> datas, int advertWith, int advertHeight) {
        this.mDatas = datas;
        this.mContext = mContext;
        this.advertWith = advertWith;
        this.advertHeight = advertHeight;
    }

    public CBPageAdapter(Context mContext, List<MediaEntity> datas) {
        this.mDatas = datas;
        this.mContext = mContext;
    }

    public View getView(int position, View view, ViewGroup container) {

        if (view == null) {
            //加载多媒体页面
            view = LayoutInflater.from(mContext).inflate(R.layout.item_media, null);
        }
        //图片
        final ImageView imageView = ViewHolderUtils.get(view, R.id.iv_img);

        if (mDatas != null && mDatas.size() > 0) {
            MediaEntity entity = mDatas.get(position);
            if (advertWith == 0 || advertHeight == 0) {
                advertWith = ScreenUtils.getScreenWidth(mContext);
                advertHeight = ScreenUtils.getScreenHeight(mContext);
            }
            //如果是图片
            if (Constants.MEDIA_PLAY_TYPE_IMAGE.equals(entity.getMediaType())) {
                //加载广告图片,按全屏比例缩放
                ImageManager.getInstance(mContext).loadImageForScal(mContext, new File(entity.getMediaFilePath()), imageView,advertWith, advertHeight);
            }
            //如果视频文件
            else if (Constants.MEDIA_PLAY_TYPE_VIDEO.equals(entity.getMediaType())) {
                //加载缩略图
                File file = new File(entity.getMediaFilePath());
                if (file.exists()) {
//                    Glide.with(mContext).load(file).into(imageView);
                    //加载缩略图
                    ImageManager.getInstance(mContext).loadImageForScal(mContext, file, imageView, advertWith, advertHeight);
                }
            }
        }

        return view;
    }


}
