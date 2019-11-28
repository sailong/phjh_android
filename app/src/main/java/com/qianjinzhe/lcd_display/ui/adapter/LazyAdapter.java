package com.qianjinzhe.lcd_display.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;
/******************************************************
 * Copyrights @ 2018，Qianjinzhe Technology Co., Ltd.
 *               深圳市前进者科技有限公司
 * All rights reserved.
 *
 * Filename：
 *              LazyAdapter.java
 * Description：
 *              公用的适配器
 * Author:
 *              youngHu
 * Finished：
 *              2018年06月28日
 ********************************************************/

public abstract class LazyAdapter<T> extends BaseAdapter {
    private ArrayList<T> mList = null;
    protected Context activity;
    private String[] data;
    protected LayoutInflater inflater = null;

    public LazyAdapter(Context a) {
        mList = new ArrayList<T>();
        initData(a);
    }

    public LazyAdapter(Context a, ArrayList<T> list) {
        mList =list;
        initData(a);
    }

    public LazyAdapter(Context a, List<T> list) {
        mList = new ArrayList<>();
        mList.addAll(list);
        initData(a);
    }

    private void initData(Context a){
        activity = a;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public abstract View layoutView(ArrayList<?> list,int position,View view, ArrayList<T> t);


    public void updateList(ArrayList<T> list){
        mList = list;
        notifyDataSetChanged();
    }

    public void updateList(List<T> list){
        if (mList == null){
            mList = new ArrayList<>();
        }else{
            mList.clear();
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addList(ArrayList<T> list){
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addList(List<T> list){
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addList(T t){
        mList.add(t);
        notifyDataSetChanged();
    }

    public void removeList(T t){
        mList.remove(t);
        notifyDataSetChanged();
    }

    public void removeList(int t){
        mList.remove(t);
        notifyDataSetChanged();
    }

    public void clear(){
        mList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return layoutView(mList,position,convertView, mList);
    }

    public ArrayList<T> getmList() {
        return mList;
    }
}
