package com.qianjinzhe.lcd_display.util;

import android.util.SparseArray;
import android.view.View;

/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              ViewHolderUtils.java
 * Description：
 *              简化ViewHolder工具类
 * Author:
 *              youngHu
 * Finished：
 *              2018年05月31日
 ********************************************************/
public class ViewHolderUtils {

    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}
