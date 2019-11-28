package com.qianjinzhe.lcd_display.bean;

import android.os.Parcel;
import android.os.Parcelable;

/******************************************************
 * Copyrights @ 2019，Shenzhen Qianjinzhe Technologies Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              CounterEntity.java
 * Description：
 *               窗口实体类,用于窗口过滤
 * Author:
 *              youngHu
 * Finished：
 *              2019年05月06日
 ********************************************************/

public class CounterEntity implements Parcelable{
    /**窗口号*/
    private int counterNo;
    /**是否选中*/
    private boolean isSelected;

    public CounterEntity() {
    }

    protected CounterEntity(Parcel in) {
        counterNo = in.readInt();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(counterNo);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CounterEntity> CREATOR = new Creator<CounterEntity>() {
        @Override
        public CounterEntity createFromParcel(Parcel in) {
            return new CounterEntity(in);
        }

        @Override
        public CounterEntity[] newArray(int size) {
            return new CounterEntity[size];
        }
    };

    public int getCounterNo() {
        return counterNo;
    }

    public void setCounterNo(int counterNo) {
        this.counterNo = counterNo;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
