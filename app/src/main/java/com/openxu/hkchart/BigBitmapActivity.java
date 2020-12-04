package com.openxu.hkchart;

import android.os.Bundle;
import android.view.ViewGroup;
import com.openxu.view.BigBitmapView;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class BigBitmapActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BigBitmapView bigBitmapView = new BigBitmapView(this);
        ((ViewGroup)getWindow().getDecorView()).addView(bigBitmapView);

        try {
//            bigBitmapView.setImage(getResources().getAssets().open("qmsht.png"));
            bigBitmapView.setImage(getResources().getAssets().open("world6.png"));
//            bigBitmapView.setImage(getResources().getAssets().open("image2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
