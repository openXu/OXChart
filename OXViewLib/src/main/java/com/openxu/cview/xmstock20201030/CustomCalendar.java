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
import com.openxu.cview.xmstock20201030.bean.DayFinish;
import com.openxu.utils.FontUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author : openXu
 * create at : 2017/1/3 9:56
 * blog : http://blog.csdn.net/xmxkf
 * gitHub : https://github.com/openXu
 * project : CustomCalendar
 * class name : CustomCalendar
 * version : 1.0
 * class describe：自定义日历控件
 */
public class CustomCalendar extends View {

    private String TAG = "CustomCalendar";

    /** 各部分背景*/
    private int mBgMonth, mBgWeek, mBgDay, mBgPre;
    /** 标题的颜色、大小*/
    private int mTextColorMonth;
    private float mTextSizeMonth;
    private int mMonthRowL, mMonthRowR;
    private float mMonthRowSpac;
    private float mMonthSpac;
    /** 星期的颜色、大小*/
    private int mTextColorWeek, mSelectWeekTextColor;
    private float mTextSizeWeek;
    /** 日期文本的颜色、大小*/
    private int mTextColorDay;
    private float mTextSizeDay;
    /** 任务次数文本的颜色、大小*/
    private int mTextColorPreFinish, mTextColorPreUnFinish, mTextColorPreNull;
    private float mTextSizePre;
    /** 选中的文本的颜色*/
    private int mSelectTextColor;
    /** 选中背景*/
    private int mSelectBg, mCurrentBg;
    private float mSelectRadius, mCurrentBgStrokeWidth;
    private float[] mCurrentBgDashPath;

    /** 行间距*/
    private float mLineSpac;
    /** 字体上下间距*/
    private float mTextSpac;

    private Paint mPaint;
    private Paint bgPaint;

    private float titleHeight, weekHeight, dayHeight, preHeight, oneHeight;
    private int columnWidth;       //每列宽度

    private Date month; //当前的月份
    private boolean isCurrentMonth;       //展示的月份是否是当前月
    private int currentDay, selectDay, lastSelectDay;    //当前日期 、 选中的日期 、上一次选中的日期（避免造成重复回调请求）

    private int dayOfMonth;    //月份天数
    private int firstIndex;    //当月第一天位置索引
    private int todayWeekIndex;//今天是星期几
    private int firstLineNum, lastLineNum; //第一行、最后一行能展示多少日期
    private int lineNum;      //日期行数
    private String[] WEEK_STR = new String[]{"Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat", };


    public CustomCalendar(Context context) {
        this(context, null);
    }
    public CustomCalendar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public CustomCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取自定义属性的值
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomCalendar, defStyleAttr, 0);

        mBgMonth = a.getColor(R.styleable.CustomCalendar_mBgMonth, Color.TRANSPARENT);
        mBgWeek = a.getColor(R.styleable.CustomCalendar_mBgWeek, Color.TRANSPARENT);
        mBgDay = a.getColor(R.styleable.CustomCalendar_mBgDay, Color.TRANSPARENT);
        mBgPre = a.getColor(R.styleable.CustomCalendar_mBgPre, Color.TRANSPARENT);

        mMonthRowL = a.getResourceId(R.styleable.CustomCalendar_mMonthRowL, R.mipmap.custom_calendar_row_left);
        mMonthRowR = a.getResourceId(R.styleable.CustomCalendar_mMonthRowR, R.mipmap.custom_calendar_row_right);
        mMonthRowSpac = a.getDimension(R.styleable.CustomCalendar_mMonthRowSpac, 20);
        mTextColorMonth = a.getColor(R.styleable.CustomCalendar_mTextColorMonth, Color.BLACK);
        mTextSizeMonth = a.getDimension(R.styleable.CustomCalendar_mTextSizeMonth, 100);
        mMonthSpac = a.getDimension(R.styleable.CustomCalendar_mMonthSpac, 20);
        mTextColorWeek = a.getColor(R.styleable.CustomCalendar_mTextColorWeek, Color.BLACK);
        mSelectWeekTextColor = a.getColor(R.styleable.CustomCalendar_mSelectWeekTextColor, Color.BLACK);

        mTextSizeWeek = a.getDimension(R.styleable.CustomCalendar_mTextSizeWeek, 70);
        mTextColorDay = a.getColor(R.styleable.CustomCalendar_mTextColorDay, Color.GRAY);
        mTextSizeDay = a.getDimension(R.styleable.CustomCalendar_mTextSizeDay, 70);
        mTextColorPreFinish = a.getColor(R.styleable.CustomCalendar_mTextColorPreFinish, Color.BLUE);
        mTextColorPreUnFinish = a.getColor(R.styleable.CustomCalendar_mTextColorPreUnFinish, Color.BLUE);
        mTextColorPreNull  = a.getColor(R.styleable.CustomCalendar_mTextColorPreNull, Color.BLUE);
        mTextSizePre = a.getDimension(R.styleable.CustomCalendar_mTextSizePre, 40);
        mSelectTextColor = a.getColor(R.styleable.CustomCalendar_mSelectTextColor, Color.YELLOW);
        mCurrentBg = a.getColor(R.styleable.CustomCalendar_mCurrentBg, Color.GRAY);
        try {
            int dashPathId = a.getResourceId(R.styleable.CustomCalendar_mCurrentBgDashPath, R.array.customCalendar_currentDay_bg_DashPath);
            int[] array = getResources().getIntArray(dashPathId);
            mCurrentBgDashPath = new float[array.length];
            for(int i=0;i<array.length;i++){
                mCurrentBgDashPath[i]=array[i];
            }
        }catch (Exception e){
            e.printStackTrace();
            mCurrentBgDashPath = new float[]{2, 3, 2, 3};
        }
        mSelectBg = a.getColor(R.styleable.CustomCalendar_mSelectBg, Color.YELLOW);
        mSelectRadius = a.getDimension(R.styleable.CustomCalendar_mSelectRadius, 20);
        mCurrentBgStrokeWidth = a.getDimension(R.styleable.CustomCalendar_mCurrentBgStrokeWidth, 5);
        mLineSpac = a.getDimension(R.styleable.CustomCalendar_mLineSpac, 20);
        mTextSpac = a.getDimension(R.styleable.CustomCalendar_mTextSpac, 20);
        a.recycle();  //注意回收

        initCompute();

    }
    /**计算相关常量，构造方法中调用*/
    private void initCompute(){
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
        //背景
        bgPaint.setColor(mBgMonth);
        RectF rect = new RectF(0, 0, getWidth(), titleHeight);
        canvas.drawRect(rect, bgPaint);
        //绘制月份
        mPaint.setTextSize(mTextSizeMonth);
        mPaint.setColor(mTextColorMonth);
        float textLen = FontUtil.getFontlength(mPaint, getMonthStr(month));
        float textStart = (getWidth() - textLen)/ 2;
        canvas.drawText(getMonthStr(month), textStart,
                mMonthSpac+FontUtil.getFontLeading(mPaint), mPaint);
        /*绘制左右箭头*/
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mMonthRowL);
        int h = bitmap.getHeight();
        rowWidth = bitmap.getWidth();
        //float left, float top
        rowLStart = (int)(textStart-2*mMonthRowSpac-rowWidth);
        canvas.drawBitmap(bitmap, rowLStart+mMonthRowSpac , (titleHeight - h)/2, new Paint());
        bitmap = BitmapFactory.decodeResource(getResources(), mMonthRowR);
        rowRStart = (int)(textStart+textLen);
        canvas.drawBitmap(bitmap, rowRStart+mMonthRowSpac, (titleHeight - h)/2, new Paint());
    }
    /**绘制绘制星期*/
    private void drawWeek(Canvas canvas){
        //背景
        bgPaint.setColor(mBgWeek);
        RectF rect = new RectF(0, titleHeight, getWidth(), titleHeight + weekHeight);
        canvas.drawRect(rect, bgPaint);
        //绘制星期：七天
        mPaint.setTextSize(mTextSizeWeek);

        for(int i = 0; i < WEEK_STR.length; i++){
            if(todayWeekIndex == i && isCurrentMonth){
                mPaint.setColor(mSelectWeekTextColor);
            }else{
                mPaint.setColor(mTextColorWeek);
            }
            int len = (int)FontUtil.getFontlength(mPaint, WEEK_STR[i]);
            int x = i * columnWidth + (columnWidth - len)/2;
            canvas.drawText(WEEK_STR[i], x, titleHeight + FontUtil.getFontLeading(mPaint), mPaint);
        }
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
    //焦点坐标
    private PointF focusPoint = new PointF();
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                focusPoint.set(event.getX(), event.getY());
                touchFocusMove(focusPoint, false);
                break;
            case MotionEvent.ACTION_MOVE:
                focusPoint.set(event.getX(), event.getY());
                touchFocusMove(focusPoint, false);
                break;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                focusPoint.set(event.getX(), event.getY());
                touchFocusMove(focusPoint, true);
                break;
        }
        return true;
    }

    /**焦点滑动*/
    public void touchFocusMove(final PointF point, boolean eventEnd) {
        Log.e(TAG, "点击坐标：("+point.x+" ，"+point.y+"),事件是否结束："+eventEnd);
        /**标题和星期只有在事件结束后才响应*/
        if(point.y<=titleHeight){
            //事件在标题上
            if(eventEnd && listener!=null){
                if(point.x>=rowLStart && point.x<(rowLStart+2*mMonthRowSpac+rowWidth)){
                    Log.w(TAG, "点击左箭头");
                    listener.onLeftRowClick();
                }else if(point.x>rowRStart && point.x<(rowRStart + 2*mMonthRowSpac+rowWidth)){
                    Log.w(TAG, "点击右箭头");
                    listener.onRightRowClick();
                }else if(point.x>rowLStart && point.x <rowRStart){
                    listener.onTitleClick(getMonthStr(month), month);
                }
            }
        }else if(point.y<=(titleHeight+weekHeight)){
            //事件在星期部分
            if(eventEnd && listener!=null){
                //根据X坐标找到具体的焦点日期
                int xIndex = (int)point.x / columnWidth;
                Log.e(TAG, "列宽："+columnWidth+"  x坐标余数："+(point.x / columnWidth));
                if((point.x / columnWidth-xIndex)>0){
                    xIndex += 1;
                }
                if(listener!=null){
                    listener.onWeekClick(xIndex-1, WEEK_STR[xIndex-1]);
                }
            }
        }else{
            /**日期部分按下和滑动时重绘，只有在事件结束后才响应*/
            touchDay(point, eventEnd);
        }
    }

    //控制事件是否响应
    private boolean responseWhenEnd = false;
    /**事件点在 日期区域 范围内*/
    private void touchDay(final PointF point, boolean eventEnd){
        //根据Y坐标找到焦点行
        boolean availability = false;  //事件是否有效
        //日期部分
        float top = titleHeight+weekHeight+oneHeight;
        int foucsLine = 1;
        while(foucsLine<=lineNum){
            if(top>=point.y){
                availability = true;
                break;
            }
            top += oneHeight;
            foucsLine ++;
        }
        if(availability){
            //根据X坐标找到具体的焦点日期
            int xIndex = (int)point.x / columnWidth;
            if((point.x / columnWidth-xIndex)>0){
                xIndex += 1;
            }
//            Log.e(TAG, "列宽："+columnWidth+"  x坐标余数："+(point.x / columnWidth));
            if(xIndex<=0)
                xIndex = 1;   //避免调到上一行最后一个日期
            if(xIndex>7)
                xIndex = 7;   //避免调到下一行第一个日期
//            Log.e(TAG, "事件在日期部分，第"+foucsLine+"/"+lineNum+"行, "+xIndex+"列");
            if(foucsLine == 1){
                //第一行
                if(xIndex<=firstIndex){
                    Log.e(TAG, "点到开始空位了");
                    setSelectedDay(selectDay, true);
                }else{
                    setSelectedDay(xIndex-firstIndex, eventEnd);
                }
            }else if(foucsLine == lineNum){
                //最后一行
                if(xIndex>lastLineNum){
                    Log.e(TAG, "点到结尾空位了");
                    setSelectedDay(selectDay, true);
                }else{
                    setSelectedDay(firstLineNum + (foucsLine-2)*7+ xIndex, eventEnd);
                }
            }else{
                setSelectedDay(firstLineNum + (foucsLine-2)*7+ xIndex, eventEnd);
            }
        }else{
            //超出日期区域后，视为事件结束，响应最后一个选择日期的回调
            setSelectedDay(selectDay, true);
        }
    }
    /**设置选中的日期*/
    private void setSelectedDay(int day, boolean eventEnd){
        Log.w(TAG, "选中："+day+"  事件是否结束"+eventEnd);
        selectDay = day;
        invalidate();
        if(listener!=null && eventEnd && responseWhenEnd && lastSelectDay!=selectDay) {
            lastSelectDay = selectDay;
            listener.onDayClick(selectDay, getMonthStr(month) + selectDay + "日", map.get(selectDay));
        }
        responseWhenEnd = !eventEnd;
    }

    /****************************事件处理↑↑↑↑↑↑↑****************************/


    @Override
    public void invalidate() {
        requestLayout();
        super.invalidate();
    }

    /***********************接口API↓↓↓↓↓↓↓**************************/
    private Map<Integer, DayFinish> map;
    public void setRenwu(String month, List<DayFinish> list){
        setMonth(month);

        if(list!=null && list.size()>0){
            map.clear();
            for(DayFinish finish : list){
                map.put(finish.day, finish);
            }
        }
        invalidate();
    }
    public void setRenwu(List<DayFinish> list){
        if(list!=null && list.size()>0){
            map.clear();
            for(DayFinish finish : list){
                map.put(finish.day, finish);
            }
        }
        invalidate();
    }
    /**月份增减*/
    public void monthChange(int change){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(month);
        calendar.add(Calendar.MONTH, change);
        setMonth(getMonthStr(calendar.getTime()));
        map.clear();
        invalidate();
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

}