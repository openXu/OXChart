package com.openxu.hkchart;

import android.content.SharedPreferences;
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
        binding.debugOnOff.checkbox.setChecked(spUtil.getSp().getBoolean(spUtil.KEY_DEBUG, false));
        binding.debugOnOff.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = spUtil.getSp().edit();
            editor.putBoolean(spUtil.KEY_DEBUG, isChecked);
            editor.commit();
        });
    }

    @Override
    public void initData() {

    }
}