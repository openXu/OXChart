package com.openxu.hkchart.base;

/**
 * Author: openX
 * Time: 2019/3/15 9:42
 * class: IBaseView
 * Description: 定义View(Activity、Fragment)的模板方法
 */
public interface IBaseView {
    /**获取布局id*/
    int getLayoutId();
    /**初始化控件*/
    void initView();
    /**初始化数据*/
    void initData();

}
