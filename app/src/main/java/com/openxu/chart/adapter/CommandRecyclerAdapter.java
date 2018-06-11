package com.openxu.chart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openxu.adapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * autour : openXu
 * date : 2017/9/7 19:05
 * className : CommandRecyclerAdapter
 * version : 1.0
 * description : 通用的CommandRecyclerAdapter
 */
public abstract class CommandRecyclerAdapter<T> extends RecyclerView.Adapter<com.openxu.adapter.ViewHolder> {
    protected Context mContext;
    protected int mLayoutId;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;

    public CommandRecyclerAdapter(Context context, int layoutId, List<T> datas) {
        mDatas = new ArrayList<>();
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        if(datas!=null)
            mDatas.addAll(datas);
    }

    public void setData(List<T> datas){
        mDatas.clear();
        if(datas!=null)
            mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public com.openxu.adapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType){
        com.openxu.adapter.ViewHolder viewHolder = com.openxu.adapter.ViewHolder.get(mContext, parent, mLayoutId);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(com.openxu.adapter.ViewHolder holder, final int position) {
        convert(holder, mDatas.get(position));
        setItemViewLayout(holder, position);
        holder.setOnClickListener(-1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(mDatas.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount(){
        return mDatas.size();
    }

    public List<T> getDatas(){
        return mDatas;
    }
    /**
     * 重写此方法，将数据绑定到控件上
     * @param holder
     * @param t
     */
    public abstract void convert(com.openxu.adapter.ViewHolder holder, T t);

    /**
     * 此方法后面添加，用于根据条目位置position设置item布局样式
     * @param holder
     * @param position
     */
    public void setItemViewLayout(ViewHolder holder, int position){}
    /***
     * item点击
     * @param data
     */
    public abstract void onItemClick(T data, int position);
}