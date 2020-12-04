package com.openxu.hkchart;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;

import com.openxu.hkchart.adapter.CommandRecyclerAdapter;
import com.openxu.hkchart.adapter.ViewHolder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private List<String> datas;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        datas = new ArrayList<>();
        datas.add("南丁格尔玫瑰图");
        datas.add("饼状图");
        datas.add("进度环形图");
        datas.add("纵向柱状图");
        datas.add("横向柱状图");
        datas.add("折线图");
        datas.add("股票信息");
        datas.add("股票信息20201031");
        datas.add("Base64TBitmap");
        datas.add("大图加载");
        datas.add("法之运");
        recyclerView.setAdapter(new CommandRecyclerAdapter<String>(this, R.layout.list_item, datas) {
            @Override
            public void convert(ViewHolder holder, String str) {
                holder.setText(R.id.tv_name, str);
            }
            @Override
            public void onItemClick(String str, int position) {
                switch (position){
                    case 0:
                        startActivity(new Intent(MainActivity.this, RoseActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, PieActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, ProgressPieActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, VerticalBarActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(MainActivity.this, HorizontalBarActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(MainActivity.this, XmStockChartActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(MainActivity.this, XmStockChartActivity191205.class));
                        break;
                    case 7:
                        startActivity(new Intent(MainActivity.this, XmStockChartActivity20201030.class));
                        break;
                    case 8:
                        startActivity(new Intent(MainActivity.this, Base64ToBitmapActivity.class));
                        break;
                    case 9:
                        startActivity(new Intent(MainActivity.this, BigBitmapActivity.class));
                        break;
                    case 10:
                        startActivity(new Intent(MainActivity.this, FpcActivity.class));
                        break;
                }
            }

        });

    }
}
