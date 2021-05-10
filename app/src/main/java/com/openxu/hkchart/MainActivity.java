package com.openxu.hkchart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.openxu.cview.TitleLayout;
import com.openxu.hkchart.adapter.CommandRecyclerAdapter;
import com.openxu.hkchart.adapter.ViewHolder;
import com.openxu.hkchart.bean.MainItem;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private List<MainItem> datas;

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

        TitleLayout titleLayout = findViewById(R.id.title_layout);
        titleLayout.setOnMenuClickListener(new TitleLayout.OnMenuClickListener() {
            @Override
            public void onClick(TitleLayout.MENU_NAME menu, View view) {
                if(menu== TitleLayout.MENU_NAME.MENU_RIGHT_TEXT){
                   startActivity(new Intent(MainActivity.this, SettingActivity.class));
                }
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        datas = new ArrayList<>();
        datas.add(new MainItem("南丁格尔玫瑰图", RoseActivity.class));
        datas.add(new MainItem("饼状图", PieActivity.class));
        datas.add(new MainItem("进度环形图", ProgressPieActivity.class));
        datas.add(new MainItem("纵向柱状图", VerticalBarActivity.class));
        datas.add(new MainItem("横向柱状图", HorizontalBarActivity.class));
        datas.add(new MainItem("折线图", XmStockChartActivity.class));
        datas.add(new MainItem("股票信息", XmStockChartActivity191205.class));
        datas.add(new MainItem("股票信息20201031", XmStockChartActivity20201030.class));
        datas.add(new MainItem("Base64TBitmap", Base64ToBitmapActivity.class));
        datas.add(new MainItem("大图加载", BigBitmapActivity.class));
        datas.add(new MainItem("法之运", FpcActivity.class));
        recyclerView.setAdapter(new CommandRecyclerAdapter<MainItem>(this, R.layout.list_item, datas) {
            @Override
            public void convert(ViewHolder holder, MainItem item) {
                holder.setText(R.id.tv_name, item.getName());
            }
            @Override
            public void onItemClick(MainItem item, int position) {
                startActivity(new Intent(MainActivity.this, item.getGotoClass()));
            }

        });

    }
}
