package com.qianjinzhe.lcd_display.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.qianjinzhe.lcd_display.util.TextUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              ImageManager.java
 * Description：
 *              图片加载类
 * Author:
 *              youngHu
 * Finished：
 *              2018年05月31日
 ********************************************************/

public class ImageManager {

    private static ImageManager instance;

    public static ImageManager getInstance(Context context) {
        if (instance == null) {
            instance = new ImageManager(context.getApplicationContext());
        }
        return instance;
    }

    public ImageManager(Context context) {

    }

    public void loadImage(Context context, String url, ImageView image) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (!url.startsWith("http://")) {
            return;
        }
        Glide.with(context).load(url).into(image);
    }

    public void loadImage(Context context, String url, ImageView image, int defaut) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        if (!url.startsWith("http://")) {
            return;
        }
        Glide.with(context).load(url).placeholder(defaut).into(image);
    }

    public void loadImage(Context context, File file, ImageView image, int defaut) {
        if (file == null) {
            return;
        }
        try {
            Glide.with(context)
                    .load(file)
                    .centerCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .error(defaut)
                    .placeholder(defaut)
                    .into(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 描述：缩放图片,不压缩的缩放.
     *
     * @param bitmap the bitmap
     * @param newWidth 新图片的宽
     * @param newHeight 新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap scaleImg(Bitmap bitmap, int newWidth, int newHeight) {
        if(bitmap == null){
            return null;
        }
        if(newHeight<=0 || newWidth<=0){
            return bitmap;
        }
        // 获得图片的宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if(width <= 0 || height <= 0){
            return null;
        }
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        //得到新的图片
        Bitmap newBm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,true);
        return newBm;
    }


    /**
     * 等比例缩放图片
     * @param context
     * @param file
     * @param imageView
     */
    public void loadImageForScal(final Context context, File file, final ImageView imageView, final int width, final int height, int defaut) {
        Glide.with(context)
                .load(file)
                .asBitmap()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(defaut)
                .placeholder(defaut)
                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageView.setImageBitmap(scaleImg(resource, width, height));
                    }
                });
    }

    /**
     * 等比例缩放图片
     * @param context
     * @param file
     * @param imageView
     */
    public void loadImageForScal(final Context context, File file, final ImageView imageView, int defaut) {
        Glide.with(context)
                .load(file)
                .asBitmap()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(defaut)
                .placeholder(defaut)
                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageView.setImageBitmap(scaleImg(resource, imageView.getWidth(), imageView.getHeight()));
                    }
                });
    }


    /**
     * 等比例缩放图片
     * @param context
     * @param file
     * @param imageView
     */
    public void loadImageForScalAllScreen(final Context context, File file, final ImageView imageView, int defaut) {
        Glide.with(context)
                .load(file)
                .asBitmap()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(defaut)
                .placeholder(defaut)
                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageView.setImageBitmap(scaleImg(resource, getScreenWidth(context), getScreenHeight(context)));
                    }
                });
    }


    /**
     * 等比例缩放图片
     * @param context
     * @param file
     * @param imageView
     */
    public void loadImageForScal(final Context context, File file, final ImageView imageView) {
        Glide.with(context)
                .load(file)
                .asBitmap()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageView.setImageBitmap(zoomBitmap(resource, getScreenWidth(context), getScreenHeight(context)));
                    }
                });
    }


    /**
     * 等比例缩放图片
     * @param context 上下文
     * @param file File文件
     * @param imageView 图片控件
     * @param width 显示区宽
     * @param height 显示区高
     */
    public void loadImageForScal(final Context context, File file, final ImageView imageView, final int width, final int height) {
        Glide.with(context)
                .load(file)
                .asBitmap()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageView.setImageBitmap(zoomBitmap(resource, width, height));
                    }
                });
    }


    /**
     * 等比例缩放图片 设置背景
     * @param context
     * @param file
     * @param imageView
     */
    public void loadImageForBg(final Context context, File file, final View imageView, int defaut) {
        Glide.with(context)
                .load(file)
                .asBitmap()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(defaut)
                .placeholder(defaut)
                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageView.setBackgroundDrawable(new BitmapDrawable(resource));
                    }
                });
    }

    /**
     * 等比例缩放图片
     * @param context
     * @param file
     * @param imageView
     */
    public void loadImageForBgScal(final Context context, File file, final View imageView, int defaut) {
        Glide.with(context)
                .load(file)
                .asBitmap()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(defaut)
                .placeholder(defaut)
                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageView.setBackgroundDrawable(new BitmapDrawable(zoomBitmap(resource, getScreenWidth(context), getScreenHeight(context))));
                    }
                });
    }

    /**
     * 等比例缩放图片
     * @param context
     * @param file
     * @param imageView
     */
    public void loadImageForBgScal(final Context context, File file, final View imageView, final int width, final int height, int defaut) {
        Glide.with(context)
                .load(file)
                .asBitmap()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(defaut)
                .placeholder(defaut)
                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (width == 0) {
                            imageView.setBackgroundDrawable(new BitmapDrawable(zoomBitmap(resource, getScreenWidth(context), height)));
                        } else if (height == 0) {
                            imageView.setBackgroundDrawable(new BitmapDrawable(zoomBitmap(resource, width, getScreenHeight(context))));
                        } else {
                            imageView.setBackgroundDrawable(new BitmapDrawable(zoomBitmap(resource, width, height)));
                        }
                    }
                });
    }

    public void loadImage(Context context, File file, ImageView image) {
        if (file == null) {
            return;
        }
        Glide.with(context).load(file).centerCrop().skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE).into(image);
    }


    public void loadCenterImage(Context context, File file, ImageView image, int w, int h) {
        if (file == null) {
            return;
        }
        Picasso.with(context).load(file).resize(w, h).centerCrop().into(image);
    }

    public void loadCenterImage(Context context, String url, ImageView image, int w, int h) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        if (!url.startsWith("http://")) {
            return;
        }

        Picasso.with(context).load(url).resize(w, h).centerCrop().into(image);
    }

    public void loadCenterImage(Context context, String url, ImageView image, int defaut, int w, int h) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        if (!url.startsWith("http://")) {
            return;
        }
        Picasso.with(context).load(url).resize(w, h).centerCrop().placeholder(defaut).into(image);
    }

    public void loadThumbleImage(Context context, String url, ImageView image) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        if (!url.startsWith("http://")) {
            return;
        }

        Glide.with(context).load(url).into(image);
    }

    public void loadThumbleImage(Context context, String url, ImageView image, int defaut) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        if (!url.startsWith("http://")) {
            return;
        }

        Picasso.with(context).load(url).centerCrop().resize(300, 300).placeholder(defaut).into(image);
    }


    public void loadAvatarImage(Context context, String url, ImageView image, int de) {
        if (TextUtils.isEmpty(url)) {
            image.setImageResource(de);
            return;
        }

        if (!url.startsWith("http://")) {
            return;
        }

        Picasso.with(context).load(url).centerCrop().resize(100, 100).placeholder(de).into(image);
    }

    public void loadAvatarImage(Context context, Uri uri, ImageView image, int de) {
        if (uri == null) {
            image.setImageResource(de);
            return;
        }
        Picasso.with(context).load(uri).error(de).placeholder(de).into(image);
    }

    public void loadBitmap(Context context, String url, com.squareup.picasso.Target target) {
        if (!url.startsWith("http://")) {
            return;
        }
        Picasso.with(context).load(url).into(target);
    }


    public Bitmap getLoadBitmap(Context context, String url) throws IOException {


        return Picasso.with(context).load(url).get();
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
     * 获得屏幕高度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

}
