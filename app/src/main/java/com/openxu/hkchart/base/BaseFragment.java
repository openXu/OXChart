package com.openxu.hkchart.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openxu.cview.TitleLayout;
import com.openxu.hkchart.R;
import com.openxu.utils.StatusBarUtil;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

/**
 * Author: openX
 * Time: 2019/2/28 16:24
 * class: BaseFragment
 * Description:
 */
public abstract class BaseFragment<V extends ViewDataBinding>
        extends Fragment implements IBaseView {

    protected V binding;
    private boolean lazyLoaded = false;

    protected TitleLayout titleLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleLayout = binding.getRoot().findViewById(R.id.titleLayout);
        if (null != titleLayout) {
            StatusBarUtil.setPaddingSmart(getContext(), titleLayout);
            titleLayout.setOnMenuClickListener((menu, view1) -> {
                onMenuClick(menu, view1);
            });
        }
        initView();
    }

    protected void onMenuClick(TitleLayout.MENU_NAME menu, View view) {
    }
    @Override
    public void onResume() {
        super.onResume();
        // 实现懒加载
        if (!lazyLoaded) {
            initData();
            lazyLoaded = true;
        }
    }

    public void finish() {
        getActivity().finish();
    }

    public void finish(int resultCode, Intent intent) {
        getActivity().setResult(resultCode, intent);
        getActivity().finish();
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        if (binding != null) {
            binding.unbind();
        }
    }

}
