package com.openxu.cview.chart.rosechart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.openxu.cview.R;
import com.openxu.cview.chart.anim.AngleEvaluator;
import com.openxu.utils.DensityUtil;
import com.openxu.utils.FontUtil;
import com.openxu.utils.LogUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * autour : openXu
 * date : 2017/7/24 10:46
 * className : NightingaleRoseChart
 * version : 1.0
 * description : 南丁格尔玫瑰图
 */
public class NightingaleRoseChart extends View {

    private String TAG = "NightingaleRoseChart";
    private int ScrWidth,ScrHeight;
    private int backColor = Color.WHITE;

    private PointF centerPoint;    //chart中心点坐标
    private int startAngle = -90;  //开始的角度
    private int chartRaidusInner = DensityUtil.dip2px(getContext(), 15);  //内圈半径
    private int chartRaidusOuter;  //最大部分扇形区域半径
    private int outSpace = DensityUtil.dip2px(getContext(), 30);          //图表与边界距离
    private int lineLenth = DensityUtil.dip2px(getContext(), 8);
    private int lineWidth = 1;     //线宽度

    private RectF rectChart, rectLable;

    private int rightLableTop;
    private int oneLableHeight;    //右侧标签单个高度
    private int rightRectItemW = DensityUtil.dip2px(getContext(), 16);
    private int rightRectItemH = DensityUtil.dip2px(getContext(), 8);
    private int rightRectSpace = DensityUtil.dip2px(getContext(), 8);   //右侧标签上下间距
    private int textSpace = DensityUtil.dip2px(getContext(), 5);

    private int lableTextSize = (int)getResources().getDimension(R.dimen.text_size_level_mid);   //右侧标注字体大小
    private int tagTextSize = (int)getResources().getDimension(R.dimen.text_size_level_small);

    private String nullStr = "暂无数据";
    private List<RoseChartBean> dataList;
    private float max;

    private Paint paintArc , paintSelected;
    private Paint paintLabel;
    private Paint mLinePaint, mlableLinePaint;

    private int selectedIndex = -1;   //被选中的索引
    /**正在加载*/
    boolean isLoading = true;
    /**是否在图表上显示指示lable*/
    boolean showChartLable = false;
    /**是否在图表上显示指示num*/
    boolean showChartNum = true;
    /**点击显示数量*/
    boolean showNumTouched = false;
    /**右侧显示数量*/
    boolean showRightNum = false;

    //RGB颜色数组
    //#D95F5B红   #7189E6蓝   #5AB9C7蓝1  #B096D5紫   #6BBA97绿1  #DCAA61黄   #7DAB58绿2  #DC7F68橙
    private final int arrColorRgb[][] = {
            {113, 137, 230},   //    UIColorFromRGB(0xD95F5B),
            {217, 95, 91},     //    UIColorFromRGB(0x7189E6),
            {90, 185, 199},    //    UIColorFromRGB(0x5AB9C7),
            {170, 150, 213},   //   UIColorFromRGB(0xB096D5),
            {107, 186, 151},   //    UIColorFromRGB(0x6BBA97),
            {91, 164, 231},    //    UIColorFromRGB(0x5BA4E7),
            {220, 170, 97},//    UIColorFromRGB(0xDCAA61),
            {125, 171, 88},//    UIColorFromRGB(0x7DAB58),
            {233, 200, 88},//    UIColorFromRGB(0xE9C858),
            {213, 150, 196},//    UIColorFromRGB(0xd596c4)
            {220, 127, 104}//    UIColorFromRGB(0xDC7F68),
    };

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void setShowNumTouched(boolean showNumTouched) {
        this.showNumTouched = showNumTouched;
    }
    public void setShowRightNum(boolean showRightNum) {
        this.showRightNum = showRightNum;
    }
    public void setChartRaidusInner(int chartRaidusInner) {
        this.chartRaidusInner = DensityUtil.dip2px(getContext(), chartRaidusInner);  //内圈半径
    }

    public void setChartRaidusOuter(int chartRaidusOuter) {
        this.chartRaidusOuter = DensityUtil.dip2px(getContext(), chartRaidusOuter);          //图表与边界距离
    }

    public NightingaleRoseChart(Context context) {
        this(context, null);
    }

    public NightingaleRoseChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NightingaleRoseChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }


    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        dataList = new ArrayList<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        ScrHeight = dm.heightPixels;
        ScrWidth = dm.widthPixels;

        //画笔初始化
        paintArc = new Paint();
        paintArc.setAntiAlias(true);

        paintLabel = new Paint();
        paintLabel.setAntiAlias(true);

        paintSelected = new Paint();
        paintSelected.setColor(Color.LTGRAY);
        paintSelected.setStyle(Paint.Style.STROKE);//设置空心
        paintSelected.setStrokeWidth(lineWidth*5);
        paintSelected.setAntiAlias(true);

        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(lineWidth);
        mLinePaint.setAntiAlias(true);


        mlableLinePaint = new Paint();
        mlableLinePaint.setStyle(Paint.Style.STROKE);
        mlableLinePaint.setColor(Color.DKGRAY);
        mlableLinePaint.setStrokeWidth(3);
        // PathEffect是用来控制绘制轮廓(线条)的方式
        // 代码中的float数组,必须是偶数长度,且>=2,指定了多少长度的实线之后再画多少长度的空白
        // .如本代码中,绘制长度5的实线,再绘制长度5的空白,再绘制长度5的实线,再绘制长度5的空白,依次重复.1是偏移量,可以不用理会.
        PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
        mlableLinePaint.setPathEffect(effects);


    }

    public void setData(Class clazz, String per, String name, List<Object> dataList){
        this.dataList.clear();
        max = 0;
        LogUtil.i(TAG, "玫瑰图设置数据"+dataList);
        if(dataList!=null){
            try{
                Field filedPer = clazz.getDeclaredField(per);
                Field filedName = clazz.getDeclaredField(name);
                filedPer.setAccessible(true);
                filedName.setAccessible(true);
                for(Object obj : dataList){
                    String perStr = filedPer.get(obj).toString();
                    this.dataList.add(new RoseChartBean(Float.parseFloat(perStr), (String)filedName.get(obj) ));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            for(RoseChartBean bean : this.dataList){
                max = max>bean.getPer()?max:bean.getPer();
            }
            if(getMeasuredHeight()>0){
                evaluatorLable();
            }
        }
        Log.i(TAG, "玫瑰图设置数据dataList"+this.dataList);
        startDraw = false;
        invalidate();
    }

    boolean startDraw = false;
    public void setData(List<RoseChartBean> dataList){
        this.dataList.clear();
        max = 0;
        if(dataList!=null){
            this.dataList.addAll(dataList);
            for(RoseChartBean bean : dataList){
                max = max>bean.getPer()?max:bean.getPer();
            }
            evaluatorLable();
        }
        startDraw = false;
        invalidate();
    }

    /**计算右侧标签相关坐标值*/
    private void evaluatorLable(){
        paintLabel.setTextSize(lableTextSize);
        oneLableHeight = (int) FontUtil.getFontHeight(paintLabel);
        //字和矩形中高度的较大值
        oneLableHeight = oneLableHeight>rightRectItemH?oneLableHeight:rightRectItemH;

        int allHeight = (oneLableHeight+rightRectSpace)*dataList.size() +rightRectSpace;
        LogUtil.e(TAG, "测量高度"+getMeasuredHeight()+"   allHeight="+allHeight);
        if(allHeight>getMeasuredHeight()){
            //超出总高度了
            oneLableHeight = getMeasuredHeight() / ((dataList==null || dataList.size()<=0)?1:dataList.size());
            rightLableTop = -1;
        }else{
            rightLableTop = (getMeasuredHeight()-allHeight)/2;
        }
    }

    public void setShowChartLable(boolean showChartLable) {
        this.showChartLable = showChartLable;
    }
    public void setShowChartNum(boolean showChartNum) {
        this.showChartNum = showChartNum;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = ScrWidth/3 * 2;   //3分之2的位置画图，3分1的位置画标注
        }
        int chartWidth = width/3*2;
        int chartSize = chartWidth>height?height:chartWidth;
        //外圈半径=size/2-最大部分lable线长度-边距
        chartRaidusOuter = chartSize/2-lineLenth-outSpace;
        centerPoint = new PointF(chartWidth/2, height/2);

        rectChart = new RectF(0,0,chartWidth, height);
        rectLable = new RectF(chartWidth,0,width,  height);
        setMeasuredDimension(width, height);
        if(dataList.size()>0){
            evaluatorLable();
        }
        LogUtil.i(TAG, "图表总宽高="+width+"*"+height);
        LogUtil.i(TAG, "chart宽高="+chartWidth+"*"+height);
        LogUtil.i(TAG, "chart直径="+chartSize);
        LogUtil.i(TAG, "lineLenth="+lineLenth+"   outSpace="+outSpace);

        LogUtil.i(TAG, "chartRaidusOuter="+chartRaidusOuter);
        LogUtil.i(TAG, "centerPoint="+centerPoint.x+"*"+ centerPoint.y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                PointF point = new PointF(event.getX(),event.getY());
                sureSelectedIndex(point);
                break;
            case MotionEvent.ACTION_MOVE:
                // LogUtil.w(TAG, "长按后的移动时间");
                point = new PointF(event.getX(),event.getY());
                sureSelectedIndex(point);
                break;
            case MotionEvent.ACTION_UP:
//                selectedIndex = -1;
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean sureSelectedIndex(PointF point){
        for(int i = 0; i < dataList.size(); i++) {
            RoseChartBean bean = dataList.get(i);
            if(bean.getRegion()!=null && bean.getRegion().contains((int)point.x, (int)point.y)){
//                LogUtil.e(TAG, "在第"+i+"个扇形区域");
                selectedIndex = i;
                invalidate();
                return true;
            }
            if(bean.getRectLable()!=null && bean.getRectLable().contains((int)point.x, (int)point.y)){
//                LogUtil.e(TAG, "在第"+i+"个lable区域");
                selectedIndex = i;
                invalidate();
                return true;
            }
        }
        return false;
    }

    public void onDraw(Canvas canvas){

        //画布背景
        canvas.drawColor(backColor);

        if(isLoading){
            paintLabel.setTextSize(lableTextSize);
            float NullTextLead = FontUtil.getFontLeading(paintLabel);
            float NullTextHeight = FontUtil.getFontHeight(paintLabel);
            float textY = centerPoint.y-NullTextHeight/2+NullTextLead;
            paintLabel.setColor(getContext().getResources().getColor(R.color.text_color_def));
            canvas.drawText("loading...", centerPoint.x- FontUtil.getFontlength(paintLabel, "loading...")/2,  textY, paintLabel);
            return;
        }
        if(dataList==null || dataList.size()<=0) {
//            paintArc.setStyle(Paint.Style.STROKE);//设置空心
//            canvas.drawCircle(centerPoint.x, centerPoint.y, chartRaidusOuter, paintArc);
            paintLabel.setTextSize(lableTextSize);
            float NullTextLead = FontUtil.getFontLeading(paintLabel);
            float NullTextHeight = FontUtil.getFontHeight(paintLabel);
            float textY = centerPoint.y-NullTextHeight/2+NullTextLead;
            canvas.drawText(nullStr, centerPoint.x- FontUtil.getFontlength(paintLabel, nullStr)/2,  textY, paintLabel);
            return;
        }

//        drawDebug(canvas);

       if(!startDraw){
           startDraw = true;
           startAnimation();
       }else{
           drawChart(canvas);
       }
    }

    private void drawDebug(Canvas canvas){
        paintArc.setStyle(Paint.Style.STROKE);//设置空心
        //绘制边界--chart区域
        paintArc.setColor(Color.YELLOW);
        canvas.drawRect(rectChart, paintArc);
        //绘制边界--标签区域
        paintArc.setColor(Color.RED);
        canvas.drawRect(rectLable, paintArc);
        //绘制边界--chart圆边界
        paintArc.setColor(Color.BLUE);
        canvas.drawCircle(centerPoint.x, centerPoint.y, chartRaidusOuter+outSpace+lineLenth, paintArc);
        //绘制边界--chart lable边缘
        paintArc.setColor(Color.GRAY);
        //绘制边界--chart 扇形边缘
        canvas.drawCircle(centerPoint.x, centerPoint.y, chartRaidusOuter+lineLenth, paintArc);
        paintArc.setColor(Color.LTGRAY);
        canvas.drawCircle(centerPoint.x, centerPoint.y, chartRaidusOuter, paintArc);
    }

    public int STYLE_1 = 1;
    public int STYLE_2 = 2;
    private int STYLE = STYLE_2;
    public void setAnimStyle(int style){
        STYLE = style;
    }
    private long duration = 1000;
    public void setAnimDuration(long duration){
        this.duration = duration;
    }
    ValueAnimator anim;
    private void startAnimation() {
        if(anim!=null){
            anim.cancel();
        }
        Log.w(TAG, "开始动画");
        //将百分比转换为扇形半径长度
        float percentage = 360.0f / dataList.size();
        anim = ValueAnimator.ofObject(new AngleEvaluator(), 0f, percentage);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float percentage = (float) animation.getAnimatedValue();
                    evaluatorData(percentage);
                    invalidate();
                }
            });
        anim.setDuration(duration);
        anim.start();
    }

    /**
     * 计算各种绘制坐标
     * @param percentage 当前扇形度数
     */
    private void evaluatorData(float percentage){
        paintLabel.setTextSize(tagTextSize);
        float chartTextLead = FontUtil.getFontLeading(paintLabel);
        float chartTextHeight = FontUtil.getFontHeight(paintLabel);
        paintLabel.setTextSize(lableTextSize);
        float lableTextLead = FontUtil.getFontLeading(paintLabel);
        float lableTextHeight = FontUtil.getFontHeight(paintLabel);
        float radius;    //每部分扇形半径
        for(int i = 0; i < dataList.size(); i++){
            RoseChartBean bean = dataList.get(i);
            /**1、绘制扇形*/
            //将百分比转换为扇区的半径
            radius = chartRaidusInner+(chartRaidusOuter-chartRaidusInner)* (bean.getPer()*1.0f / max*1.0f);
            float arcLeft = centerPoint.x - radius;
            float arcTop = centerPoint.y - radius ;
            float arcRight = centerPoint.x + radius ;
            float arcBottom = centerPoint.y + radius ;

            float oneStartAngle = startAngle + i * percentage;
            if(STYLE == STYLE_1){
                oneStartAngle = startAngle + i * percentage;
            }else{
                //将百分比转换为扇形半径长度
                float partPercentage = 360.0f / dataList.size();
                oneStartAngle = startAngle + i * partPercentage;
            }
            bean.setArcRect(new RectF(arcLeft ,arcTop, arcRight, arcBottom));
            bean.setStartAngle(oneStartAngle);
            bean.setSweepAngle(percentage);
            /**计算扇形区域*/
            float Allradius = chartRaidusOuter+ lineLenth + outSpace;
            arcLeft = centerPoint.x - Allradius;
            arcTop = centerPoint.y - Allradius;
            arcRight = centerPoint.x + Allradius;
            arcBottom = centerPoint.y + Allradius;
            Path allPath = new Path();
            allPath.moveTo(centerPoint.x, centerPoint.y);//添加原始点
            float ovalX = centerPoint.x+(float) (radius* Math.cos(Math.toRadians(oneStartAngle)));
            float ovalY = centerPoint.y+(float) (radius* Math.sin(Math.toRadians(oneStartAngle)));
            allPath.lineTo(ovalX, ovalY);
            RectF touchOval = new RectF(arcLeft, arcTop, arcRight, arcBottom);
            allPath.addArc(touchOval, oneStartAngle, percentage);
            allPath.lineTo(centerPoint.x, centerPoint.y);
            allPath.close();
            RectF r = new RectF();
            allPath.computeBounds(r, true);
            Region region = new Region();
            region.setPath(allPath, new Region((int)r.left, (int) r.top, (int) r.right,(int)r.bottom));
            bean.setRegion(region);

            /**2、绘制直线*/
            //确定直线的起始和结束的点的位置
            float startX = centerPoint.x + (float) (radius * Math.cos(Math.toRadians(oneStartAngle + percentage / 2)));
            float startY = centerPoint.y + (float) (radius * Math.sin(Math.toRadians(oneStartAngle + percentage / 2)));
            float endX = centerPoint.x + (float) ((chartRaidusOuter + lineLenth) * Math.cos(Math.toRadians(oneStartAngle + percentage / 2)));
            float endY = centerPoint.y + (float) ((chartRaidusOuter + lineLenth) * Math.sin(Math.toRadians(oneStartAngle + percentage / 2)));
            boolean isRight = true;
            float lineAngle = startAngle + i * percentage + percentage / 2;
            if (lineAngle > 90 && lineAngle < 270) {
                isRight = false;
            }

//            LogUtil.i(TAG, "直线坐标：start=("+startX+","+startY +")  end=("+endX+","+endY+")"+"   lineAngle="+lineAngle+"  isRight="+isRight);
            List<PointF> tagLinePoints = new ArrayList<>();
            tagLinePoints.add(new PointF(startX, startY));
            tagLinePoints.add(new PointF(endX, endY));
            float textX = isRight ? (endX + 20) : (endX - 20);
            tagLinePoints.add(new PointF(textX, endY));
            bean.setTagLinePoints(tagLinePoints);

            /**3、绘制指示标签*/
            paintLabel.setTextSize(tagTextSize);

            String lableText = "";
            if(showChartLable){
                lableText = bean.getName();
            }else if(showChartNum){
                lableText = bean.getPer()+"";
            }
            if(showNumTouched){
                lableText = bean.getPer()+"";
            }

            //标签字体长度
            float textW = FontUtil.getFontlength(paintLabel, lableText);
            //字体绘制X坐标
            textX = isRight ? textX : (textX - textW);
            float textY = endY - chartTextHeight / 2 + chartTextLead;
            bean.setTagTextPoint(new PointF(textX, textY));

            /**4、绘制右侧item矩形*/
            float rectL, rectT, rectR, centerY, DashPathL;

            if(rightLableTop<0){
                //超出总高度了
                bean.setRectLable(new RectF(rectLable.left, i * oneLableHeight, rectLable.right, (i+1) * oneLableHeight));

                rectL = rectLable.left+textSpace;
                rectT = i * oneLableHeight + (oneLableHeight-rightRectItemH)/2;
                rectR = rectL + rightRectItemW;
                centerY = i * oneLableHeight + oneLableHeight/2.0f;
                RectF colorRect = new RectF(rectL, rectT, rectR, rectT+ rightRectItemH);
                bean.setColorRect(colorRect);
            }else{
                rectT = rightLableTop +rightRectSpace+ i * (oneLableHeight+rightRectSpace);
                bean.setRectLable(new RectF(rectLable.left, rectT, rectLable.right, rectT+oneLableHeight));

                centerY = rectT + oneLableHeight/2.0f;

                rectL = rectLable.left+textSpace;
                rectT = rectT + (oneLableHeight-rightRectItemH)/2;
                rectR = rectL + rightRectItemW;
                RectF colorRect = new RectF(rectL, rectT, rectR, rectT+ rightRectItemH);
                bean.setColorRect(colorRect);
            }

            /**5、绘制指示标签*/
            paintLabel.setTextSize(lableTextSize);
            //标签字体长度
            textW = FontUtil.getFontlength(paintLabel, bean.getName());
            //字体绘制X坐标
            rectL = rectR+textSpace;
            textY = centerY -lableTextHeight/2 +lableTextLead;
            bean.setNameTextPoint(new PointF(rectL, textY));
            DashPathL = rectL + textW + textSpace;

            /**6.绘制占比*/
            textW = FontUtil.getFontlength(paintLabel, bean.getPer()+"");
            rectL = rectLable.right - textW - textSpace;
            bean.setPerTextPoint(new PointF(rectL, textY));
            /**7、绘制虚线*/
            if(DashPathL<(rectL-textSpace)){
                bean.setDashPathPointStart(new PointF(DashPathL, centerY));
                bean.setDashPathPointEnd(new PointF(rectL-textSpace, centerY));
            }

        }
    }
    private void drawChart(Canvas canvas){
        paintArc.setStyle(Paint.Style.FILL);//设置实心

        for(int i = 0; i < dataList.size(); i++){
            paintArc.setARGB(255, arrColorRgb[i%arrColorRgb.length][0], arrColorRgb[i%arrColorRgb.length][1], arrColorRgb[i%arrColorRgb.length][2]);
            mLinePaint.setARGB(255, arrColorRgb[i%arrColorRgb.length][0],
                    arrColorRgb[i%arrColorRgb.length][1], arrColorRgb[i%arrColorRgb.length][2]);
            paintLabel.setARGB(255, arrColorRgb[i%arrColorRgb.length][0],
                    arrColorRgb[i%arrColorRgb.length][1], arrColorRgb[i%arrColorRgb.length][2]);
            if(selectedIndex == i){
                paintLabel.setTypeface(Typeface.DEFAULT_BOLD);
                mLinePaint.setTypeface(Typeface.DEFAULT_BOLD);
            }else{
                paintLabel.setTypeface(Typeface.DEFAULT);
                mLinePaint.setTypeface(Typeface.DEFAULT);
            }
            paintLabel.setTextSize(tagTextSize);

            RoseChartBean bean = dataList.get(i);

            /**1、绘制扇形*/

            canvas.drawArc(bean.getArcRect(), bean.getStartAngle(), bean.getSweepAngle(), true, paintArc);
//            canvas.drawPath(allPath,mLinePaint);
            if(selectedIndex == i){
                //被选中的，绘制边界
                paintSelected.setStyle(Paint.Style.STROKE);//设置空心
                canvas.drawArc(bean.getArcRect(), bean.getStartAngle(), bean.getSweepAngle(), true, paintSelected);
            }
            if(showChartLable || showChartNum){
                /**2、绘制直线*/
                List<PointF> tagLinePoints = bean.getTagLinePoints();
                if (tagLinePoints != null && tagLinePoints.size() > 0) {
                    for (int p = 1; p < tagLinePoints.size(); p++) {
                        canvas.drawLine(tagLinePoints.get(p - 1).x, tagLinePoints.get(p - 1).y,
                                tagLinePoints.get(p).x, tagLinePoints.get(p).y, mLinePaint);
                    }
                }
                if(showChartLable){
                    /**3、绘制指示标签*/
                    canvas.drawText(bean.getName(), bean.getTagTextPoint().x, bean.getTagTextPoint().y, paintLabel);
                }else if(showChartNum){
                    /**3、绘制指示标签*/
                    canvas.drawText(bean.getPer()+"", bean.getTagTextPoint().x, bean.getTagTextPoint().y, paintLabel);
                }

            }

            if(showNumTouched && selectedIndex == i) {
                /**2、绘制直线*/
                List<PointF> tagLinePoints = bean.getTagLinePoints();
                if (tagLinePoints != null && tagLinePoints.size() > 0) {
                    for (int p = 1; p < tagLinePoints.size(); p++) {
                        canvas.drawLine(tagLinePoints.get(p - 1).x, tagLinePoints.get(p - 1).y,
                                tagLinePoints.get(p).x, tagLinePoints.get(p).y, mLinePaint);
                    }
                }
                /**3、绘制数量*/
                canvas.drawText(bean.getPer()+"", bean.getTagTextPoint().x, bean.getTagTextPoint().y, paintLabel);
                LogUtil.i(TAG, "绘制bean="+bean.getName()+"   "+bean.getPer());
            }


            /**4、绘制右侧item矩形*/
            canvas.drawRoundRect(bean.getColorRect(), 8, 8, paintArc);//第二个参数是x半径，第三个参数是y半径
//            LogUtil.i(TAG, "绘制lable矩形"+bean.getColorRect());
            if(selectedIndex == i){
                canvas.drawRoundRect(bean.getColorRect(), 8, 8, paintSelected);
            }


            /**5、绘制指示标签*/
            paintLabel.setTextSize(lableTextSize);
            paintLabel.setColor(getContext().getResources().getColor(R.color.text_color_def));
            canvas.drawText(bean.getName(), bean.getNameTextPoint().x, bean.getNameTextPoint().y , paintLabel);

            if(showRightNum){
                /**6.绘制占比*/
                canvas.drawText(bean.getPer()+"", bean.getPerTextPoint().x, bean.getPerTextPoint().y , paintLabel);
                /**7、绘制虚线*/
                if(bean.getDashPathPointStart()!=null){
                    Path path = new Path();
                    path.moveTo(bean.getDashPathPointStart().x,bean.getDashPathPointStart().y);
                    path.lineTo( bean.getDashPathPointEnd().x,bean.getDashPathPointEnd().y);
                    canvas.drawPath(path, mlableLinePaint);
                }
            }

        }

        //绘制中心内圆
        paintArc.setColor(backColor);
        canvas.drawCircle(centerPoint.x, centerPoint.y, chartRaidusInner, paintArc);
    }



}
