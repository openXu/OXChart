package com.openxu.hkchart;

import android.widget.CompoundButton;

import com.openxu.hkchart.base.BaseActivity;
import com.openxu.hkchart.base.BaseFragment;
import com.openxu.hkchart.databinding.ActivitySettingBinding;

public class SettingActivity extends BaseActivity<ActivitySettingBinding> {

    private String TAG = "SettingActivity";

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        binding.debugOnOff.checkbox.setChecked(spUtil.getData(spUtil.KEY_DEBUG, Boolean.class));
        binding.debugOnOff.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> spUtil.saveData(spUtil.KEY_DEBUG, isChecked));
    }

    @Override
    public void initData() {

    }
}