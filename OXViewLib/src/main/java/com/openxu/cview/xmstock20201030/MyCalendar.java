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
import com.openxu.cview.xmstock20201030.bean.Constacts;
import com.openxu.cview.xmstock20201030.bean.DayFinish;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;

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
public class MyCalendar extends View {

    private String TAG = "MyCalendar";

    private boolean debug = false;

    /** 字体颜色、大小*/
    private int textColorMonth = Color.parseColor("#868686");
    private int textSizeMonth = DensityUtil.sp2px(getContext(), 10);
    private int textColorLable = Color.parseColor("#212025");
    private int textSizeLable = DensityUtil.sp2px(getContext(), 14);
    private int textColorWeek = Color.parseColor("#afafaf");
    private int textSizeWeek = DensityUtil.sp2px(getContext(), 14);
    private int textColorDate = Color.parseColor("#3d3d3d");
    private int textColorDateNoStock = Color.parseColor("#dcdcdc");  //没有股票的日期字体颜色
    private int textSizeDate = DensityUtil.sp2px(getContext(), 14);
    private int textColorStock = Color.parseColor("#484848");
    private int textSizeStock = DensityUtil.sp2px(getContext(), 10);

    /**颜色*/
    private int colorDot = Color.parseColor("#5f90c8");
    private int colorSelected = Color.parseColor("#d36647");
    /**间隙*/
    private int lableTextSpace = DensityUtil.dip2px(getContext(), 10);  //lable与上方的间距
    private int dateLineSpace = DensityUtil.dip2px(getContext(), 20);   //日期每行间距
    private int stockTextSpace = DensityUtil.dip2px(getContext(), 4);   //股票字之间的间距
    private int dateTopSpace = DensityUtil.dip2px(getContext(), 5);     //日子与月份、星期的间距
    private int dotSpace = DensityUtil.dip2px(getContext(), 6);         //圆点与字的间距
    private float selectRadius = DensityUtil.dip2px(getContext(), 13);    //选中日期的圆圈半径
    private float dotRadius = DensityUtil.dip2px(getContext(), 3); //小圆点半径
    private float bitmapSpace = DensityUtil.dip2px(getContext(), 8);
    /**常量*/
    private Bitmap bitmapDown, bitmapUp;
    private String[] weekStrs = new String[]{"一", "二", "三", "四", "五", "六", "日", };
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
    private int lastLineDataNum;
    private int startIndex;       //第一行开始绘制索引0-6

    private boolean showCalendar = false;

    public MyCalendar(Context context) {
        this(context, null);
    }
    public MyCalendar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public MyCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        setBackgroundColor(Color.parseColor("#aa000000"));
        bitmapDown = BitmapFactory.decodeResource(getResources(), R.mipmap.calendar_down);
        bitmapUp = BitmapFactory.decodeResource(getResources(), R.mipmap.calendar_up);
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
    List<CalendarData> calendar_data = new ArrayList<>();
    List<CalendarDetail> calendar_detail = new ArrayList<>();
    //上方显示的7条数据
    List<CalendarData> calendar_data_top = new ArrayList<>();
    //焦点数据
    CalendarData focusData;

    public void setData(List<CalendarData> calendar, List<CalendarDetail> detail) {
        Log.w(TAG, "设置数据："+calendar.size());
        calendar_data.clear();
        calendar_data.addAll(calendar);
        calendar_detail.clear();
        calendar_detail.addAll(detail);
        calendar_data_top.clear();
        for(int i = 0; i<7; i++){
            if(i<calendar_data.size())
                calendar_data_top.add(calendar_data.get(i));
        }
        if(calendar_data.size()>0)
            focusData = calendar_data.get(0);
        requestLayout();
        invalidate();
    }
    private ItemClickListener listener;
    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    public interface ItemClickListener{
        void onDayClick(CalendarData calendar, List<CalendarDetail> detail);
    }
    /***********************接口API↑↑↑↑↑↑↑**************************/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //宽度 = 填充父窗体
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);   //获取宽的尺寸
        float rowHeight = bitmapDown.getHeight() + bitmapSpace * 2;
        float weekHeight1 = 0;
        float dateHeight1 = 0;
        if(showCalendar && calendar_data!=null && calendar_data.size()>0){
            weekHeight1 = weekHeight;
            //"date":"2020-10-20","week":"2","stock_list"
            startIndex = Integer.parseInt(calendar_data.get(0).getWeek())-1;
            lineNum = 1 + ((calendar_data.size()-(7-startIndex))/7) +
                    ((calendar_data.size()-(7-startIndex))%7>0?1:0);
            //最后一行不足7个
            if((calendar_data.size()-(7-startIndex))%7>0){
                lineNum = 1 + ((calendar_data.size()-(7-startIndex))/7) + 1;
                lastLineDataNum = calendar_data.size() - (7-startIndex) - ((lineNum-2)*7);
            }else{
                lineNum = 1 + ((calendar_data.size()-(7-startIndex))/7);
                lastLineDataNum = 7;
            }
            Log.w(TAG, "行数："+(calendar_data.size()-(7-startIndex))/7);
            Log.w(TAG, "行数："+((calendar_data.size()-(7-startIndex))%7>0?1:0));
            //计算日历高度
            dateHeight1 = dateLineHeight * lineNum;
//            Log.w(TAG, "总共"+calendar_data.size()+"个数据，第一个数据显示索引"+startIndex+"   总行数："+lineNum+"   最后一行有"+lastLineDataNum+"个数据   日历数据部分高度："+dateHeight1);
        }
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                (int)(getPaddingTop() + getPaddingBottom() +
                        topHeight +
                        lableHeight +
                        weekHeight1 +
                        dateHeight1 +
                        rowHeight));
        Log.w(TAG, "测量："+getMeasuredWidth()+"*"+getMeasuredHeight());
        rectTop = new RectF(getPaddingLeft(), getPaddingTop(), getMeasuredWidth()-getPaddingRight(),getPaddingTop()+topHeight);
        rectLable = new RectF(rectTop.left, rectTop.bottom, rectTop.right, rectTop.bottom+lableHeight);
        rectWeek = new RectF(rectTop.left, rectLable.bottom, rectTop.right, rectLable.bottom+weekHeight1);
        rectDate = new RectF(rectTop.left, rectWeek.bottom, rectTop.right, rectWeek.bottom+dateHeight1);
        rectRow = new RectF(rectTop.left, rectDate.bottom, rectTop.right, rectDate.bottom+rowHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.w(TAG, "绘制"+calendar_data);
        if(calendar_data==null || calendar_data.size()<=0)
            return;
        if(debug)
            drawRect(canvas);
        drawTop(canvas);
        drawLable(canvas);
        if(showCalendar){
            drawWeek(canvas);
            drawDayAndPre(canvas);
        }
        drawRow(canvas);
    }
    private void drawRect(Canvas canvas){
        paint.setStyle(Paint.Style.FILL);
//        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.YELLOW);
        canvas.drawRect(rectTop, paint);
        paint.setColor(Color.BLUE);
        canvas.drawRect(rectLable, paint);
        paint.setColor(Color.GREEN);
        canvas.drawRect(rectWeek, paint);
        paint.setColor(Color.WHITE);
        canvas.drawRect(rectDate, paint);
        paint.setColor(Color.BLACK);
        canvas.drawRect(rectRow, paint);
    }

    private void drawRow(Canvas canvas) {
        canvas.drawBitmap(showCalendar?bitmapUp:bitmapDown,
                rectRow.left+(rectRow.right-rectRow.left)/2-bitmapUp.getWidth()/2,
                rectRow.top + bitmapSpace ,paint);
    }
    private void drawTop(Canvas canvas) {
        float top;
        float left = rectTop.left;
        float oneWidth = (rectTop.right-rectTop.left)/7;
        float textWidth;
        String text;
        paint.setStyle(Paint.Style.FILL);
        for(int i = 0; i<calendar_data_top.size(); i++){
            top = rectTop.top;
            CalendarData data = calendar_data_top.get(i);
            //月
            paintText.setTextSize(textSizeMonth);
            paintText.setColor(textColorMonth);
            text = getMonthStr(data.getDate());
            textWidth = FontUtil.getFontlength(paintText, text);
            canvas.drawText(text, left + oneWidth/2 - textWidth/2, top + textMonthLead , paintText);
            //日
            top += textMonthHeight+dateTopSpace;
            paintText.setTextSize(textSizeDate);
            paintText.setColor(textColorDate);
            text = getDateStr(data.getDate());
            textWidth = FontUtil.getFontlength(paintText, text);
            if(focusData!=null && focusData.getDate().equals(data.getDate())){
                //被选中的日子需要标红圈
                paint.setColor(colorSelected);
                canvas.drawCircle(left + oneWidth/2, top+textDateHeight/2, selectRadius, paint);
                paintText.setColor(Color.WHITE);
            }
            canvas.drawText(text, left + oneWidth/2 - textWidth/2, top + textDateLead , paintText);
            //点
            top += textDateHeight+dotSpace;
            paint.setColor(colorDot);
            canvas.drawCircle(left + oneWidth/2, top+dotRadius, dotRadius, paint);
            left+=oneWidth;
            //分割线
            if(debug)
                canvas.drawLine(left, rectTop.top, left, rectTop.bottom,paint);
        }
    }
    private void drawLable(Canvas canvas) {
        int dotLableSpace = DensityUtil.dip2px(getContext(), 4);
        paintText.setTextSize(textSizeLable);
        paintText.setColor(textColorLable);
        String str = "日历效应";
        float textWidth = FontUtil.getFontlength(paintText, str);
        float left = rectLable.right - lableTextSpace - textWidth;
        canvas.drawText(str, left, rectLable.bottom - textLableHeight + textLableLead ,paintText);
        if(debug)
            canvas.drawLine(left, rectLable.top, left, rectLable.bottom,paint);
        //点
        left -= (dotLableSpace+dotRadius);
        if(debug)
            canvas.drawLine(left, rectLable.top, left, rectLable.bottom,paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(colorSelected);
        canvas.drawCircle(left, rectLable.bottom - textLableHeight/2, dotRadius, paint);
        str = "重大事件";
        textWidth = FontUtil.getFontlength(paintText, str);
        left -= (dotRadius+lableTextSpace+textWidth);
        canvas.drawText(str, left, rectLable.bottom - textLableHeight + textLableLead ,paintText);
        if(debug)
            canvas.drawLine(left, rectLable.top, left, rectLable.bottom,paint);
        left -= (dotLableSpace+dotRadius);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(colorDot);
        canvas.drawCircle(left, rectLable.bottom - textLableHeight/2, dotRadius, paint);
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
                    rectWeek.top + dateLineSpace + textWeekLead ,paintText);
            left += oneWidth;
        }
    }
    /**绘制日历主体*/
    private void drawDayAndPre(Canvas canvas){
        if(!showCalendar || calendar_data==null || calendar_data.size()<=0)
            return;
        for(int i = 0 ; i < lineNum; i++){
            //一行行绘制
            drawDayAndPre(canvas, i, i==0?startIndex+1:1);
        }
    }
    /**
     * 绘制某一行的日期
     * @param canvas
     * @param startWeek 此行第一个日期的星期值
     */
    private void drawDayAndPre(Canvas canvas, int line, int startWeek){
        //该行绘制的第一个数据索引
        int firstDataIndex = line == 0 ? 0 : (7-startIndex) + (line-1)*7;
        float top;
        float oneWidth = (rectDate.right-rectDate.left)/7;
        float left = rectDate.left + (startWeek-1)*oneWidth;
        float textWidth;
//        Log.w(TAG, "绘制日期，当前第"+line+"行，第一条数据为星期"+startWeek+", 数据开始索引："+firstDataIndex+", 当前行绘制数量："+(7 - startWeek+1));
        for(int i = 0; i<=7 - startWeek; i++){
            if(firstDataIndex+i>=calendar_data.size())
                return;
            paint.setStyle(Paint.Style.FILL);
            top = rectDate.top + line * dateLineHeight;
            CalendarData data = calendar_data.get(firstDataIndex+i);
            paintText.setTextSize(textSizeDate);
            paintText.setColor((data.getStock_list()==null || data.getStock_list().size()<=0)?textColorDateNoStock:textColorDate);
            textWidth = FontUtil.getFontlength(paintText, getDateStr(data.getDate()));
            //日期
            if(focusData!=null && focusData.getDate().equals(data.getDate())){
                //被选中的日子需要标红圈
                paint.setColor(colorSelected);
                canvas.drawCircle(left + oneWidth/2, top+textDateHeight/2, selectRadius, paint);
                paintText.setColor(Color.WHITE);
            }
            canvas.drawText(getDateStr(data.getDate()), left + oneWidth/2 - textWidth/2, top +textDateLead ,paintText);
            if(data.getStock_list()!=null && data.getStock_list().size()>0){
                top += textDateHeight+dotSpace+dotRadius;
                //点
                paint.setColor(colorDot);
                canvas.drawCircle(left + oneWidth/2, top, dotRadius, paint);
                top += dotRadius+stockTextSpace;
                //股票
                paintText.setTextSize(textSizeStock);
                paintText.setColor(textColorStock);
                for(CalendarDataStock stock : data.getStock_list()){
                    textWidth = FontUtil.getFontlength(paintText, stock.getName());
                    canvas.drawText(stock.getName(), left + oneWidth/2 - textWidth/2, top+textStockLead,paintText);
                    top += textStockHeight+stockTextSpace;
                }
            }
            left += oneWidth;
            if(debug)
                canvas.drawLine(left, rectDate.top + line * dateLineHeight, left, top,paint);
        }
    }



    /****************************事件处理↓↓↓↓↓↓↓****************************/

/*    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(showCalendar){
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(event);
    }*/

    //焦点坐标
    private PointF downPoint = new PointF();
    private PointF upPoint = new PointF();
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                upPoint.set(event.getX(), event.getY());
                if(Math.abs(upPoint.x - downPoint.x)<20 && Math.abs(upPoint.y - downPoint.y)<20 )
                    touchFocus(upPoint);
                break;
        }
        return true;
    }

    public void touchFocus(final PointF point) {
        Log.e(TAG, "点击坐标：("+point.x+" ，"+point.y);
        /**标题和星期只有在事件结束后才响应*/
        if(point.y<=rectTop.bottom){
//            Log.i(TAG, "事件在标题上，处理");
            //计算点落在哪个日期上
            float oneWidth = (rectTop.right-rectTop.left)/7;
            int xIndex = (int)((point.x - rectTop.left) / oneWidth);
            if(((point.x - rectTop.left) % oneWidth)>0)
                xIndex += 1;
            if(xIndex<=0 || xIndex>calendar_data_top.size())
                return;
            Log.e(TAG, "第"+xIndex+"列,  列宽："+oneWidth+"  x坐标余数："+((point.x - rectTop.left) % oneWidth));
            focusData = calendar_data_top.get(xIndex-1);
            showCalendar = false;
            requestLayout();
            if(listener!=null) {
                List<CalendarDetail> details = getDetail(focusData);
                if(details!=null)
                    listener.onDayClick(focusData, details);
            }
            invalidate();
        }else if(point.y<=rectLable.bottom){
//            Log.i(TAG, "事件在lable栏，不处理");
        }else if(point.y<=rectWeek.bottom){
//            Log.i(TAG, "事件在星期栏，不处理");
        }else if(point.y<=rectDate.bottom){
//            Log.w(TAG, "=============事件在日期上，处理");
            //计算点落在哪个日期上
            float oneWidth = (rectDate.right-rectDate.left)/7;
            int xIndex = (int)((point.x - rectTop.left) / oneWidth);
            int foucsLine = (int)((point.y - rectDate.top)/dateLineHeight);
            if(((point.y - rectDate.top) % dateLineHeight)>0)
                foucsLine += 1;
            Log.e(TAG, "点击到第"+foucsLine+"行  "+xIndex+" 列");
            int dataIndex = 0;
            if(foucsLine == 1){    //第一行
                if(xIndex<startIndex){
                    Log.e(TAG, "点到开始空位了");
                    return;
                }else{
                    dataIndex = xIndex-startIndex;
                }
            }else if(foucsLine == lineNum){
                //最后一行
                if(xIndex>=lastLineDataNum){
                    Log.e(TAG, "点到结尾空位了");
                    return;
                }else{
                    dataIndex = calendar_data.size() - (lastLineDataNum - xIndex);
                }
            }else{
                dataIndex = (7-startIndex) + (foucsLine -1)*7 - (7-xIndex);
            }
            CalendarData data = calendar_data.get(dataIndex);
            if(data.getStock_list()==null||data.getStock_list().size()<=0){
                return;
            }
            focusData = data;
//            Log.e(TAG, "点击的数据索引为"+dataIndex+"   数据为："+focusData);
            selectCanendar(dataIndex);
            showCalendar = false;
            requestLayout();
            if(listener!=null) {
                List<CalendarDetail> details = getDetail(focusData);
                if(details!=null)
                    listener.onDayClick(focusData, details);
            }
        }else if(point.y<=rectRow.bottom){
            Log.i(TAG, "事件在箭头栏，处理");
            float center = rectRow.left + (rectRow.right-rectRow.left)/2;
            if(point.x < center - bitmapDown.getWidth()||
            point.x>center + bitmapDown.getWidth())
                return;
            showCalendar = !showCalendar;
            requestLayout();
        }
    }

    //根据选中的日期，选出7个数据展示在顶部
    private void selectCanendar(int selectIndex){
        calendar_data_top.clear();
        int count = 0;
        int preCount = 0;
        int lastPreIndex = selectIndex;
        int houCount = 0;
        //从开始位置从前取4个
        for(int i = selectIndex; i >=0 ; i--){
            CalendarData data = calendar_data.get(i);
            if(data.getStock_list()!=null && data.getStock_list().size()>0){
                lastPreIndex = i;
                calendar_data_top.add(0, data);
                count ++;
                preCount ++;
                if(preCount >= 4)
                    break;
            }
        }
        //从开始位置向后取3个
        for(int i = selectIndex+1; i < calendar_data.size() ; i++){
            CalendarData data = calendar_data.get(i);
            if(data.getStock_list()!=null && data.getStock_list().size()>0){
                calendar_data_top.add(data);
                count ++;
                houCount ++;
                if(count>=7)
                    break;
                if(preCount == 4){
                    if(houCount >= 3)  //前面取4个后面3个
                        break;
                }else{   //前面没取到4个，继续从后面取，直到取7个，或者遍历到最后
                    continue;
                }
            }
        }
        if(houCount<3){   //后面没取到3个，从前面取
            //从开始位置从前取4个
            for(int i = lastPreIndex-1; i >=0 ; i--){
                CalendarData data = calendar_data.get(i);
                if(data.getStock_list()!=null && data.getStock_list().size()>0){
                    calendar_data_top.add(0, data);
                    count ++;
                    preCount ++;
                    if(count>=7)
                        break;
                }
            }
        }
        Log.w(TAG, "取7个数据：======="+selectIndex+"前面取"+preCount+"个，后面取"+houCount+"个, 总共"+calendar_data_top.size());
    }

    private List<CalendarDetail> getDetail(CalendarData focusData) {
        List<CalendarDetail> details = null;
        for(CalendarDetail detail : calendar_detail){
            if(focusData.getDate().equals(detail.getDate())){
                if(details==null)
                    details = new ArrayList<>();
                details.add(detail);
                return details;
            }
        }
        return details;
    }

    /****************************事件处理↑↑↑↑↑↑↑****************************/
    @Override
    public void invalidate() {
        requestLayout();
        super.invalidate();
    }


    //2020-10-20
    private String getMonthStr(String date){
        return date.substring(5, 7)+"月";
    }
    private String getDateStr(String date){
        return date.substring(8, date.length());
    }
}