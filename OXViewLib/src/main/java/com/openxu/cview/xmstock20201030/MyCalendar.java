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
import com.openxu.cview.xmstock20201030.bean.CalendarDetail;
import com.openxu.cview.xmstock20201030.bean.DayFinish;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;

import java.text.SimpleDateFormat;
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
    private int colorEvent = Color.parseColor("#00ff00");
    private int colorSelected = Color.parseColor("#ff0000");
    /**间隙*/
    private int lableTextSpace = DensityUtil.dip2px(getContext(), 4);  //lable与上方的间距
    private int lineSpace = DensityUtil.dip2px(getContext(), 20); //日期每行间距
    private int stockTextSpace = DensityUtil.dip2px(getContext(), 4);  //股票字之间的间距
    private int dateTopSpace = DensityUtil.dip2px(getContext(), 3);   //日子与月份、星期的间距
    private int dotSpace = DensityUtil.dip2px(getContext(), 8);   //圆点与字的间距
    private float selectRadius = DensityUtil.dip2px(getContext(), 15);    //选中日期的圆圈半径
    private float dotRadius = DensityUtil.dip2px(getContext(), 3);
    /**常量*/
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
    private float dateLineHeight;  //每行日期高度

    private RectF rectTop, rectLable, rectDate, rectRow;
    private int columnWidth;       //每列宽度
    private int lineNum;           //日期行数

    public MyCalendar(Context context) {
        this(context, null);
    }
    public MyCalendar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public MyCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        lableHeight = textLableHeight + lableTextSpace;
        //每行日期高度
        dateLineHeight =

        //每行高度 = 行间距 + 日期字体高度 + 字间距 + 次数字体高度
        oneHeight = mLineSpac + dayHeight + mTextSpac + preHeight;
    }

    /***********************接口API↓↓↓↓↓↓↓**************************/
    //date，CalendarData
    private Map<String, DayFinish> map;

    public void setData(List<CalendarData> calendar, List<CalendarDetail> calendar_detail) {

        evaluatorByData();
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
        mPaint = new Paint();
        bgPaint = new Paint();
        mPaint.setAntiAlias(true); //抗锯齿
        bgPaint.setAntiAlias(true); //抗锯齿

        map = new HashMap<>();

        //标题高度
        mPaint.setTextSize(mTextSizeMonth);
        titleHeight = FontUtil.getFontHeight(mPaint) + 2 * mMonthSpac;
        //星期高度
        mPaint.setTextSize(mTextSizeWeek);
        weekHeight = FontUtil.getFontHeight(mPaint);
        //日期高度
        mPaint.setTextSize(mTextSizeDay);
        dayHeight = FontUtil.getFontHeight(mPaint);
        //次数字体高度
        mPaint.setTextSize(mTextSizePre);
        preHeight = FontUtil.getFontHeight(mPaint);
        //每行高度 = 行间距 + 日期字体高度 + 字间距 + 次数字体高度
        oneHeight = mLineSpac + dayHeight + mTextSpac + preHeight;

        //默认当前月份
        String cDateStr = getMonthStr(new Date());
//        cDateStr = "2015年08月";
        setMonth(cDateStr);
    }

    /**设置月份*/
    private void setMonth(String Month){
        //设置的月份（2017年01月）
        month = str2Date(Month);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //获取今天是多少号
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        todayWeekIndex = calendar.get(Calendar.DAY_OF_WEEK)-1;

        Date cM = str2Date(getMonthStr(new Date()));
        //判断是否为当月
        if(cM.getTime() == month.getTime()){
            isCurrentMonth = true;
            selectDay = currentDay;//当月默认选中当前日
        }else{
            isCurrentMonth = false;
            selectDay = 0;
        }
        Log.d(TAG, "设置月份："+month+"   今天"+currentDay+"号, 是否为当前月："+isCurrentMonth);
        calendar.setTime(month);
        dayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        //第一行1号显示在什么位置（星期几）
        firstIndex = calendar.get(Calendar.DAY_OF_WEEK)-1;
        lineNum = 1;
        //第一行能展示的天数
        firstLineNum = 7-firstIndex;
        lastLineNum = 0;
        int shengyu = dayOfMonth - firstLineNum;
        while (shengyu>7){
            lineNum ++;
            shengyu-=7;
        }
        if(shengyu>0){
            lineNum ++;
            lastLineNum = shengyu;
        }
        Log.i(TAG, getMonthStr(month)+"一共有"+dayOfMonth+"天,第一天的索引是："+firstIndex+"   有"+lineNum+
                "行，第一行"+firstLineNum+"个，最后一行"+lastLineNum+"个");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //宽度 = 填充父窗体
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);   //获取宽的尺寸
        columnWidth = widthSize / 7;
        //高度 = 标题高度 + 星期高度 + 日期行数*每行高度
        float height = titleHeight + weekHeight + (lineNum * oneHeight);
        Log.v(TAG, "标题高度："+titleHeight+" 星期高度："+weekHeight+" 每行高度："+oneHeight+
                " 行数："+ lineNum + "  \n控件高度："+height);
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                (int)height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawMonth(canvas);
        drawWeek(canvas);
        drawDayAndPre(canvas);
    }

    /**绘制月份*/
    private int rowLStart, rowRStart, rowWidth;
    private void drawMonth(Canvas canvas){
    }
    /**绘制绘制星期*/
    private void drawWeek(Canvas canvas){
    }
    /**绘制日期和次数*/
    private void drawDayAndPre(Canvas canvas){
        //某行开始绘制的Y坐标，第一行开始的坐标为标题高度+星期部分高度
        float top = titleHeight+weekHeight;
        //行
        for(int line = 0; line < lineNum; line++){
            if(line == 0){
                //第一行
                drawDayAndPre(canvas, top, firstLineNum, 0, firstIndex);
            }else if(line == lineNum-1){
                //最后一行
                top += oneHeight;
                drawDayAndPre(canvas, top, lastLineNum, firstLineNum+(line-1)*7, 0);
            }else{
                //满行
                top += oneHeight;
                drawDayAndPre(canvas, top, 7, firstLineNum+(line-1)*7, 0);
            }
        }
    }

    /**
     * 绘制某一行的日期
     * @param canvas
     * @param top 顶部坐标
     * @param count 此行需要绘制的日期数量（不一定都是7天）
     * @param overDay 已经绘制过的日期，从overDay+1开始绘制
     * @param startIndex 此行第一个日期的星期索引
     */
    private void drawDayAndPre(Canvas canvas, float top,
                               int count, int overDay, int startIndex){
//        Log.e(TAG, "总共"+dayOfMonth+"天  有"+lineNum+"行"+ "  已经画了"+overDay+"天,下面绘制："+count+"天");
        //背景
        float topPre = top + mLineSpac + dayHeight;
        bgPaint.setColor(mBgDay);
        RectF rect = new RectF(0, top, getWidth(), topPre);
        canvas.drawRect(rect, bgPaint);

        bgPaint.setColor(mBgPre);
        rect = new RectF(0, topPre, getWidth(), topPre + mTextSpac + dayHeight);
        canvas.drawRect(rect, bgPaint);

        mPaint.setTextSize(mTextSizeDay);
        float dayTextLeading = FontUtil.getFontLeading(mPaint);
        mPaint.setTextSize(mTextSizePre);
        float preTextLeading = FontUtil.getFontLeading(mPaint);
//        Log.v(TAG, "当前日期："+currentDay+"   选择日期："+selectDay+"  是否为当前月："+isCurrentMonth);
        for(int i = 0; i<count; i++){
            int left = (startIndex + i)*columnWidth;
            int day = (overDay+i+1);

            mPaint.setTextSize(mTextSizeDay);

            //如果是当前月，当天日期需要做处理
            if(isCurrentMonth && currentDay == day){
                mPaint.setColor(mTextColorDay);
                bgPaint.setColor(mCurrentBg);
                bgPaint.setStyle(Paint.Style.STROKE);  //空心
                PathEffect effect = new DashPathEffect(mCurrentBgDashPath, 1);
                bgPaint.setPathEffect(effect);   //设置画笔曲线间隔
                bgPaint.setStrokeWidth(mCurrentBgStrokeWidth);       //画笔宽度
                //绘制空心圆背景
                canvas.drawCircle(left+columnWidth/2, top + mLineSpac +dayHeight/2,
                        mSelectRadius-mCurrentBgStrokeWidth, bgPaint);
            }
            //绘制完后将画笔还原，避免脏笔
            bgPaint.setPathEffect(null);
            bgPaint.setStrokeWidth(0);
            bgPaint.setStyle(Paint.Style.FILL);

            //选中的日期，如果是本月，选中日期正好是当天日期，下面的背景会覆盖上面绘制的虚线背景
            if(selectDay == day){
                //选中的日期字体白色，橙色背景
                mPaint.setColor(mSelectTextColor);
                bgPaint.setColor(mSelectBg);
                //绘制橙色圆背景，参数一是中心点的x轴，参数二是中心点的y轴，参数三是半径，参数四是paint对象；
                canvas.drawCircle(left+columnWidth/2, top + mLineSpac +dayHeight/2, mSelectRadius, bgPaint);
            }else{
                mPaint.setColor(mTextColorDay);
            }

            int len = (int)FontUtil.getFontlength(mPaint, day+"");
            int x = left + (columnWidth - len)/2;
            canvas.drawText(day+"", x, top + mLineSpac + dayTextLeading, mPaint);

            //绘制次数
            mPaint.setTextSize(mTextSizePre);
            DayFinish finish = map.get(day);
            String preStr = "0/0";
            if(isCurrentMonth){
                if(day>currentDay){
                    mPaint.setColor(mTextColorPreNull);
                }else if(finish!=null){
                    //区分完成未完成
                    if(finish.finish >= finish.all) {
                        mPaint.setColor(mTextColorPreFinish);
                    }else{
                        mPaint.setColor(mTextColorPreUnFinish);
                    }
                    preStr = finish.finish+"/"+finish.all;

                }else{
                    mPaint.setColor(mTextColorPreNull);
                }
            }else{
                if(finish!=null){
                    //区分完成未完成
                    if(finish.finish >= finish.all) {
                        mPaint.setColor(mTextColorPreFinish);
                    }else{
                        mPaint.setColor(mTextColorPreUnFinish);
                    }
                    preStr = finish.finish+"/"+finish.all;

                }else{
                    mPaint.setColor(mTextColorPreNull);
                }
            }

            len = (int)FontUtil.getFontlength(mPaint, preStr);
            x = left + (columnWidth - len)/2;
            canvas.drawText(preStr, x, topPre + mTextSpac + preTextLeading, mPaint);
        }
    }

    /**获取月份标题*/
    private String getMonthStr(Date month){
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月");
        return df.format(month);
    }
    private Date str2Date(String str){
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月");
            return df.parse(str);
        }catch (Exception e){
            e.printStackTrace();
            return null;
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