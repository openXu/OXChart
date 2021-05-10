package com.openxu.hkchart.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.openxu.cview.TitleLayout;
import com.openxu.hkchart.R;
import com.openxu.utils.FBarUtils;
import com.openxu.utils.SharedData;
import com.openxu.utils.StatusBarUtil;
import com.yanzhenjie.permission.AndPermission;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * Author: openX
 * Time: 2019/2/27 15:51
 * class: BaseActivity
 * Description:
 */
public abstract class BaseActivity<V extends ViewDataBinding>
        extends AppCompatActivity implements IBaseView {

    protected String TAG;
    protected Context mContext;

    protected V binding;
    protected TitleLayout titleLayout;

    protected SharedData spUtil;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        mContext = this;
        //DataBindingUtil类需要在project的build中配置 dataBinding {enabled true }, 同步后会自动关联android.databinding包
        binding = DataBindingUtil.setContentView(this, getLayoutId());

        //状态栏透明和间距处理
//            StatusBarUtil.darkMode(this);   //状态栏字体变黑色（透明状态栏）
        StatusBarUtil.immersive(this);//全透明状态栏(状态栏字体默认白色)
//            StatusBarUtil.immersive(this, getResources().getColor(R.color.colorPrimary), 1);
        //导航栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && FBarUtils.isSupportNavBar()) {
            FBarUtils.setNavBarVisibility(this, true);
            FBarUtils.setNavBarColor(this, Color.parseColor("#dddddd"));
        }
        titleLayout = findViewById(R.id.titleLayout);
        if (null != titleLayout) {
            StatusBarUtil.setPaddingSmart(this, titleLayout);
            titleLayout.setOnMenuClickListener((menu, view) -> {
                onMenuClick(menu, view);
            });
        }
        spUtil = SharedData.getInstance();
        initView();
        initData();
    }

    protected void onMenuClick(TitleLayout.MENU_NAME menu, View view) {
    }

    /**
     * 隐藏输入法
     */
    public void hideSoftInputFromWindow() {
        try {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binding != null)
            binding.unbind();
    }


}
