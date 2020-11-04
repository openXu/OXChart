package com.openxu.cview.xmstock20201030;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.openxu.cview.R;
import com.openxu.cview.xmstock20201030.bean.CalendarData;
import com.openxu.cview.xmstock20201030.bean.CalendarDataStock;
import com.openxu.cview.xmstock20201030.bean.CalendarDetail;
import com.openxu.cview.xmstock20201030.bean.DayFinish;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author : xm
 * create at : 2020/11/3 9:56
 * class name : MyCalendar
 * version : 1.0
 * class describe：自定义日历控件
 */
//https://openxu.blog.csdn.net/article/details/54020386
public class MyCalendar extends View {

    private String TAG = "MyCalendar";

    /** 字体颜色、大小*/
    private int textColorMonth = Color.parseColor("#939393");
    private int textSizeMonth = DensityUtil.sp2px(getContext(), 12);
    private int textColorLable = Color.parseColor("#5E5E5E");
    private int textSizeLable = DensityUtil.sp2px(getContext(), 14);
    private int textColorWeek = Color.parseColor("#939393");
    private int textSizeWeek = DensityUtil.sp2px(getContext(), 14);
    private int textColorDate = Color.parseColor("#000000");
    private int textSizeDate = DensityUtil.sp2px(getContext(), 1);
    private int textColorStock = Color.parseColor("#5E5E5E");
    private int textSizeStock = DensityUtil.sp2px(getContext(), 12);

    /**颜色*/
    private int colorDot = Color.parseColor("#00ff00");
    private int colorSelected = Color.parseColor("#ff0000");
    /**间隙*/
    private int lableTextSpace = DensityUtil.dip2px(getContext(), 4);  //lable与上方的间距
    private int dateLineSpace = DensityUtil.dip2px(getContext(), 20); //日期每行间距
    private int stockTextSpace = DensityUtil.dip2px(getContext(), 4);  //股票字之间的间距
    private int dateTopSpace = DensityUtil.dip2px(getContext(), 3);   //日子与月份、星期的间距
    private int dotSpace = DensityUtil.dip2px(getContext(), 8);   //圆点与字的间距
    private float selectRadius = DensityUtil.dip2px(getContext(), 15);    //选中日期的圆圈半径
    private float dotRadius = DensityUtil.dip2px(getContext(), 3);
    /**常量*/
    private Bitmap bitmapRow;
    private String[] weekStrs = new String[]{"一", "二", "三", "四", "五", "六", "七", };
    private Paint paint, paintText;
    /**计算*/
    private float textMonthHeight, textMonthLead;
    private float textLableHeight, textLableLead;
    private float textWeekHeight, textWeekLead;
    private float textDateHeight, textDateLead;
    private float textStockHeight, textStockLead;
    private float topHeight;   //上方简要日期高度
    private float lableHeight; //lable高度
    private float weekHeight;
    private float dateLineHeight;  //每行日期高度，包括下间隙

    private RectF rectTop, rectLable, rectWeek, rectDate, rectRow;
    private int lineNum;           //日期行数
    private int startIndex;       //第一行开始绘制索引0-6

    private boolean showCalendar = true;

    public MyCalendar(Context context) {
        this(context, null);
    }
    public MyCalendar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public MyCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(Color.parseColor("#aa000000"));
        bitmapRow = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_back);
        paint = new Paint();
        paintText = new Paint();
        paint.setAntiAlias(true);
        paintText.setAntiAlias(true);
        paintText.setTextSize(textSizeMonth);
        textMonthHeight = FontUtil.getFontHeight(paintText);
        textMonthLead =  FontUtil.getFontLeading(paintText);
        paintText.setTextSize(textSizeLable);
        textLableHeight = FontUtil.getFontHeight(paintText);
        textLableLead =  FontUtil.getFontLeading(paintText);
        paintText.setTextSize(textSizeWeek);
        textWeekHeight = FontUtil.getFontHeight(paintText);
        textWeekLead =  FontUtil.getFontLeading(paintText);
        paintText.setTextSize(textSizeDate);
        textDateHeight = FontUtil.getFontHeight(paintText);
        textDateLead =  FontUtil.getFontLeading(paintText);
        paintText.setTextSize(textSizeStock);
        textStockHeight = FontUtil.getFontHeight(paintText);
        textStockLead =  FontUtil.getFontLeading(paintText);
        //上方简要日期高度
        topHeight = textMonthHeight + dateTopSpace + textDateHeight + dotSpace + dotRadius*2;
        //lable高度
        lableHeight = lableTextSpace + textLableHeight;
        weekHeight = dateLineSpace + textWeekHeight + dateTopSpace;
        //每行日期高度
        dateLineHeight = textDateHeight + dotSpace+dotRadius*2+ (stockTextSpace+textStockHeight)*2+dateLineSpace;
    }

    /***********************接口API↓↓↓↓↓↓↓**************************/
    //date，CalendarData
    private Map<String, DayFinish> map;
    List<CalendarData> calendar_data = new ArrayList<>();
    List<CalendarDetail> calendar_detail = new ArrayList<>();
    List<CalendarData> getCalendar_data_top = new ArrayList<>();

    public void setData(List<CalendarData> calendar, List<CalendarDetail> detail) {
        calendar_data.clear();
        calendar_data.addAll(calendar);
        calendar_detail.clear();
        calendar_detail.addAll(detail);
        getCalendar_data_top.clear();
        for(int i = 0; i<7; i++){
            if(i<calendar_data.size())
                getCalendar_data_top.add(calendar_data.get(i));
        }

        evaluatorByData();
        requestLayout();
    }
    private onClickListener listener;
    public void setOnClickListener(onClickListener listener){
        this.listener = listener;
    }

    public interface onClickListener{

        public abstract void onLeftRowClick();
        public abstract void onRightRowClick();
        public abstract void onTitleClick(String monthStr, Date month);
        public abstract void onWeekClick(int weekIndex, String weekStr);
        public abstract void onDayClick(int day, String dayStr, DayFinish finish);
    }
    /***********************接口API↑↑↑↑↑↑↑**************************/

    private void evaluatorByData(){
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //宽度 = 填充父窗体
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);   //获取宽的尺寸
        int rowHeight = bitmapRow.getHeight() * 3;
        float dateHeightSize = weekHeight;
        if(showCalendar && calendar_data!=null && calendar_data.size()>0){
            //"date":"2020-10-20","week":"2","stock_list"
            startIndex = Integer.parseInt(calendar_data.get(0).getWeek())-1;
            lineNum = 1 + ((calendar_data.size()-(7-startIndex))/7) +
                    ((calendar_data.size()-(7-startIndex))%7>0?1:0);
            Log.w(TAG, "行数："+(calendar_data.size()-(7-startIndex))/7);
            Log.w(TAG, "行数："+((calendar_data.size()-(7-startIndex))%7>0?1:0));
            Log.w(TAG, "总共"+calendar_data.size()+"个数据，第一个数据显示索引"+startIndex+"   总行数："+lineNum);
            //计算日历高度
            dateHeightSize += dateHeightSize * lineNum;
        }
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                (int)(getPaddingTop() + getPaddingBottom() +
                        topHeight +
                        lableHeight +
                        dateHeightSize +
                        rowHeight));
        Log.w(TAG, "测量："+getMeasuredWidth()+"*"+getMeasuredHeight());
        rectTop = new RectF(getPaddingLeft(), getPaddingTop(), getMeasuredWidth()-getPaddingRight(),getPaddingTop()+topHeight + lableHeight);
        rectLable = new RectF(rectTop.left, rectTop.bottom, rectTop.right, rectTop.bottom+lableHeight);
        rectWeek = new RectF(rectTop.left, rectLable.bottom, rectTop.right, rectLable.bottom+weekHeight);
        rectDate = new RectF(rectTop.left, rectWeek.bottom, rectTop.right, rectWeek.bottom+dateHeightSize);
        rectRow = new RectF(rectTop.left, rectDate.bottom, rectTop.right, rectDate.bottom+rowHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawRect(canvas);
        if(calendar_data==null || calendar_data.size()<=0)
            return;
        drawTop(canvas);
        drawLable(canvas);
        if(showCalendar){
            drawWeek(canvas);
            drawDayAndPre(canvas);
        }
    }

    private void drawTop(Canvas canvas) {
        float top;
        float left = rectTop.left;
        float oneWidth = (rectTop.right-rectTop.left)/7;
        float textWidth;
        for(int i = 0; i<getCalendar_data_top.size(); i++){
            top = rectTop.top;
            CalendarData data = getCalendar_data_top.get(i);
            paintText.setTextSize(textSizeMonth);
            paintText.setColor(textColorMonth);
            textWidth = FontUtil.getFontlength(paintText, data.getDate());
            //月
            canvas.drawText(data.getDate(), left + oneWidth/2 - textWidth/2, top + textMonthLead , paintText);
            paintText.setTextSize(textSizeDate);
            paintText.setColor(textColorDate);
            textWidth = FontUtil.getFontlength(paintText, data.getDate());
            top += textMonthHeight+dateTopSpace;
            canvas.drawText(data.getDate(), left + oneWidth/2 - textWidth/2, top + textMonthLead , paintText);
            //点
            top += textDateHeight+dotSpace;
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(colorDot);
            canvas.drawCircle(left + oneWidth/2-dotRadius, top+dotRadius, dotRadius, paint);
            left+=oneWidth;
        }

    }
    private void drawLable(Canvas canvas) {
        paintText.setTextSize(textSizeLable);
        paintText.setColor(textColorLable);
        String str = "日历效应";
        float textWidth = FontUtil.getFontlength(paintText, str);
        float right = rectLable.right - textWidth;
        canvas.drawText(str, right,
                rectLable.bottom - textLableHeight + textLableLead ,paintText);
        //点
        right -= lableTextSpace;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(colorDot);
        canvas.drawCircle(right + dotRadius, rectLable.bottom - textLableHeight/2, dotRadius, paint);
        str = "重大事件";
        textWidth = FontUtil.getFontlength(paintText, str);
        right -= dotRadius*2+lableTextSpace+textWidth;
        canvas.drawText(str, right,
                rectLable.bottom - textLableHeight + textLableLead ,paintText);
        right -= lableTextSpace;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(colorDot);
        canvas.drawCircle(right + dotRadius, rectLable.bottom - textLableHeight/2, dotRadius, paint);
    }


    private void drawRect(Canvas canvas){
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.RED);
        canvas.drawRect(rectTop, paint);
        paint.setColor(Color.GREEN);
        canvas.drawRect(rectLable, paint);
        paint.setColor(Color.BLUE);
        canvas.drawRect(rectWeek, paint);
        paint.setColor(Color.BLACK);
        canvas.drawRect(rectDate, paint);
        paint.setColor(Color.RED);
        canvas.drawRect(rectRow, paint);
    }
    private void drawWeek(Canvas canvas){
        float left = rectWeek.left;
        float oneWidth = (rectWeek.right-rectWeek.left)/7;
        float textWidth;
        for(int i = 0 ;i<weekStrs.length; i++){
            paintText.setTextSize(textSizeWeek);
            paintText.setColor(textColorWeek);
            textWidth = FontUtil.getFontlength(paintText, weekStrs[i]);
            canvas.drawText(weekStrs[i], left + oneWidth/2 - textWidth/2,
                    rectWeek.top - dateLineSpace + textWeekLead ,paintText);

        }


    }
    /**绘制日期和次数*/
    private void drawDayAndPre(Canvas canvas){

        if(!showCalendar || calendar_data==null || calendar_data.size()<=0)
            return;
        for(int i = 0 ; i < lineNum; i++){
            //一行行绘制
            drawDayAndPre(canvas, i, i==0?startIndex:0);
        }
    }

    /**
     * 绘制某一行的日期
     * @param canvas
     * @param startIndex 此行第一个日期的星期索引
     */
    private void drawDayAndPre(Canvas canvas, int line, int startIndex){

        int dataIndex = line==0?0 : (7-startIndex) + (line-1)*7;//该行绘制的第一个数据索引
        float top;
        float left = rectDate.left;
        float oneWidth = (rectDate.right-rectDate.left)/7;
        float textWidth;
        for(int i = 0; i<7; i++){
            top = rectDate.top;
            if(dataIndex+i>=calendar_data.size())
                return;
            CalendarData data = calendar_data.get(dataIndex+i);
            paintText.setTextSize(textSizeDate);
            paintText.setColor(textColorDate);
            textWidth = FontUtil.getFontlength(paintText, data.getDate());
            //日期
            canvas.drawText(data.getDate(), left + oneWidth/2 - textWidth/2, top +textDateLead ,paintText);
            if(data.getStock_list()!=null && data.getStock_list().size()>0){
                top += textDateHeight+dotSpace;
                //点
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(colorDot);
                canvas.drawCircle(left + oneWidth/2-dotRadius, top+dotRadius, dotRadius, paint);
                top += dotRadius*2+stockTextSpace;
                //股票
                paintText.setTextSize(textSizeStock);
                paintText.setColor(textColorStock);
                for(CalendarDataStock stock : data.getStock_list()){
                    textWidth = FontUtil.getFontlength(paintText, data.getDate());
                    canvas.drawText(stock.getName(), left + oneWidth/2 - textWidth/2, top+textStockLead,paintText);
                    top += textStockHeight+stockTextSpace;
                }
            }
            left += oneWidth;
        }
    }



    /****************************事件处理↓↓↓↓↓↓↓****************************/

    /****************************事件处理↑↑↑↑↑↑↑****************************/
    @Override
    public void invalidate() {
        requestLayout();
        super.invalidate();
    }


}