package com.openxu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * autour : openXu
 * date : 2017/9/7 19:04
 * className : ViewHolder
 * version : 1.0
 * description : 通用的ViewHolder
 */
public class ViewHolder  extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;
    private View mConvertView;
    private Context mContext;

    public ViewHolder(Context context, View itemView, ViewGroup parent) {
        super(itemView);
        mContext = context;
        mConvertView = itemView;
        mViews = new SparseArray<>();
    }

    public static ViewHolder get(Context context, ViewGroup parent, int layoutId) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder holder = new ViewHolder(context, itemView, parent);
        return holder;
    }

    /**
     * 通过viewId获取控件
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }
    public ViewHolder setVisible(int viewId, int visible) {
        getView(viewId).setVisibility(visible);
        return this;
    }
    public ViewHolder setTextColor(int viewId, int color) {
        TextView tv = getView(viewId);
        tv.setTextColor(color);
        return this;
    }
    public ViewHolder setText(int viewId, CharSequence text) {
        TextView tv = getView(viewId);
        tv.setText(TextUtils.isEmpty(text)?"":text);
        return this;
    }
    public ViewHolder setBackgroundResource(int viewId, int id) {
        View view = getView(viewId);
        view.setBackgroundResource(id);
        return this;
    }
    public ViewHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }
    public ViewHolder setCheckBoxChecked(int viewId, boolean check) {
        CheckBox cb = getView(viewId);
        cb.setChecked(check);
        return this;
    }
    public ViewHolder setLinearLayoutBgIcon(int viewId, int  iconResourse) {
        LinearLayout ll = getView(viewId);
        ll.setBackgroundResource(iconResourse);
        return this;
    }
    public ViewHolder setOnClickListener(int viewId,  View.OnClickListener listener) {
        if(viewId==-1){
            mConvertView.setOnClickListener(listener);
        }else{
            View view = getView(viewId);
            view.setOnClickListener(listener);
        }
        return this;
    }
}