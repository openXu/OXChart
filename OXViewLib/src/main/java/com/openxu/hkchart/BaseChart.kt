package com.openxu.hkchart

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Scroller
import com.openxu.cview.chart.anim.AngleEvaluator
import com.openxu.hkchart.config.ChartConfigBase
import com.openxu.hkchart.config.MultipartBarConfig
import com.openxu.hkchart.config.MultipartBarData
import com.openxu.hkchart.data.FocusData
import com.openxu.hkchart.loading.BallPulseIndicator
import com.openxu.hkchart.loading.LoadingIndicator
import com.openxu.utils.DensityUtil
import com.openxu.utils.LogUtil
import com.openxu.utils.SharedData
import kotlin.math.abs

/**
 * Author: openXu
 * Time: 2021/5/11 11:01
 * class: BaseChart
 * Description:
 */
open abstract class BaseChart<T> : View, View.OnTouchListener {

    protected var TAG = javaClass.simpleName
    protected var debug = SharedData.getInstance().sp.getBoolean(SharedData.KEY_DEBUG, false)
    //画笔
    protected var paint: Paint
    protected var paintText: Paint
    protected var paintEffect: Paint
    //屏幕宽高
    protected var screenWidth = 0
    protected var screenHeight = 0
    protected lateinit var rectDrawBounds : RectF    //图表绘制矩形区域(每次刷新数据固定不变)
    protected lateinit var rectChart : RectF         //图表主体绘制矩形（刷新数据时会重新计算）
    protected lateinit var centerPoint : Point  //chart中心点坐标
    //坐标轴辅助线宽度
    protected var axisLineWidth = DensityUtil.dip2px(context, 0.8f)
    /**设置是否正在加载*/
    var loading = true
        set(value){
            field = value
            if (value) {
                loadingIndicator.start()
            } else {
                loadingIndicator.stop()
                postInvalidate()
            }
        }

    //动画
    private lateinit var chartAnim: ValueAnimator
    protected var chartAnimValue = 1f //动画值
    protected var chartAnimStarted = false
    private lateinit var loadingIndicator: LoadingIndicator

    constructor(context: Context) :this(context, null)
    constructor(context: Context, attrs: AttributeSet?) :this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int):super(context, attrs, defStyle){
        mGestureDetector = GestureDetector(getContext(), MyOnGestureListener())
        mScaleGestureDetector = ScaleGestureDetector(context, MyOnScaleGestureListener())
        mScroller = Scroller(context)
        setOnTouchListener(this)

        val dm = resources.displayMetrics
        screenHeight = dm.heightPixels
        screenWidth = dm.widthPixels
        paint = Paint()
        paintText = Paint()
        paintEffect = Paint()
        paint.isAntiAlias = true
        paintText.isAntiAlias = true
        paintEffect.isAntiAlias = true
        //加载动画
        setLoadingIndicator("BallPulseIndicator")
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerPoint = Point(measuredWidth / 2, measuredHeight / 2)
        rectDrawBounds = RectF(paddingLeft.toFloat(), paddingTop.toFloat(), (measuredWidth - paddingRight).toFloat(),
                (measuredHeight - paddingBottom).toFloat())
        rectChart = RectF(rectDrawBounds.left, rectDrawBounds.top, rectDrawBounds.right,rectDrawBounds.bottom)
        loadingIndicator.setBounds(rectDrawBounds.left.toInt(), rectDrawBounds.top.toInt(),
                rectDrawBounds.right.toInt(), rectDrawBounds.bottom.toInt())
        initial()
    }
    override fun onDraw(canvas: Canvas) {
//        Log.e(TAG, "=================绘制图表");
        if (debug) drawDebug(canvas)
        if (loading) {
            val saveCount = canvas.save()
            //            canvas.translate(getPaddingLeft(), getPaddingTop());
            loadingIndicator!!.draw(canvas)
            canvas.restoreToCount(saveCount)
            return
        }
        if(chartConfig==null){
            drawChart(canvas)
        }else{
            if (chartConfig!!.showAnim && !chartAnimStarted) {
                chartAnimStarted = true
                startChartAnimation()
            } else {
                drawChart(canvas)
            }
        }

    }

    private fun drawDebug(canvas: Canvas) {
        //绘制debug辅助线
        paint.style = Paint.Style.STROKE //设置空心
        paint.strokeWidth = axisLineWidth.toFloat()
        //绘制边界--chart区域
        paint.color = Color.BLACK
        var r = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
        canvas.drawRect(r, paint)
        paint.color = Color.RED
        r = RectF(paddingLeft.toFloat(), paddingTop.toFloat(), (measuredWidth - paddingRight).toFloat(), (measuredHeight - paddingBottom).toFloat())
        canvas.drawRect(r, paint)
        paint.color = Color.GREEN
        canvas.drawRect(rectDrawBounds, paint)
    }

    /***************************1. API👇👇👇***************************/
    var chartConfig : ChartConfigBase? = null
    /***************************1. API👆👆👆***************************/

    /***************************2. 子类重写👇👇👇***************************/
    /**初步计算，当设置数据 & size发生变化时调用*/
    open fun initial() :Boolean{
        if(!this::rectDrawBounds.isInitialized)
            return true
        return false
    }
    /**绘制图表 */
    open abstract fun drawChart(canvas: Canvas?)
    /**焦点坐标*/
    open abstract fun onFocusTouch(point: PointF?)
    /**缩放开始*/
    open abstract fun onScaleBegin(detector: ScaleGestureDetector)
    /**缩放*/
    open abstract fun onScale(detector: ScaleGestureDetector, beginScrollx: Float)
    /**缩放结束*/
    open fun onScaleEnd(detector: ScaleGestureDetector){}
    /***************************2. 子类重写👆👆👆***************************/

    /***************************3. 事件👇👇👇***************************/
    private var mGestureDetector : GestureDetector   //手势监听，处理滑动
    private var mScroller : Scroller                 //滚动帮助类，处理快速滚动(惯性)
    private var mScaleGestureDetector : ScaleGestureDetector //缩放手势监听，处理缩放
    /*计算*/
    protected var scrollXMax = 0f //最大滚动距离，是一个负值
    protected var scrollx = 0f    //当前滚动距离，范围：scrollXMax ~ 0 （scrollXMax会自动计算）。从第一条数据绘制（scrollx==0），如果从最后一条数据绘制（scrollx==scrollXMax）
    protected var scalex = 1f     //x方向缩放系数（范围默认1~2，可根据需求在MyOnScaleGestureListener类中修改）

    protected var focusData: FocusData<T>? = null  //焦点数据
    protected var focusIndex = 0                //焦点落在数据集合的索引值

    /**
     * 重写dispatchTouchEvent，并调用requestDisallowInterceptTouchEvent申请父控件不要拦截事件，将事件处理权交给图表
     *
     * 这对图表来说是非常重要的，比如图表放在ScrollerView里面时，如果不调用requestDisallowInterceptTouchEvent(true)，
     * 图表接受的事件将由ScrollerView决定，一旦ScrollerView发现竖直滚动则会拦截事件，导致图表不能再接受到事件
     *
     * 此处首先申请父控件不要拦截事件，所有事件都将传到图表中，由图表决定自己是否处理事件，如果不需要处理（竖直方向滑动距离大于水平方向）则让父控件处理
     * 需要注意的是一旦放弃处理，剩下的事件将不会被收到
     */
    private var mDownX = 0f        //dispatchTouchEvent方法中手指按下时的坐标，用于决定什么时候拦截事件
    private var mDownY = 0f
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = event.x
                mDownY = event.y
                parent.requestDisallowInterceptTouchEvent(true) //ACTION_DOWN的时候，赶紧把事件hold住
            }
            //不处于缩放状态，竖直滑动的距离大于阈值，将事件还给父控件
            MotionEvent.ACTION_MOVE -> if (!scaleing && abs(event.y - mDownY) > abs(event.x - mDownX) * 1.5) {
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    protected val focusPoint = PointF()                //焦点坐标
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        mScaleGestureDetector.onTouchEvent(event)
        mGestureDetector.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                focusPoint.x = event.x
                focusPoint.y = event.y
                onFocusTouch(focusPoint)
            }
            MotionEvent.ACTION_MOVE -> {
                focusPoint.x = event.x
                focusPoint.y = event.y
                onFocusTouch(focusPoint)
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> onFocusTouch(null)
        }
        postInvalidate()
        return true
    }

    inner class MyOnGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            if (!mScroller.isFinished) mScroller.forceFinished(true)
            return true //事件被消费，下次才能继续收到事件
        }
        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            if (!scaleing) {
                scrollx -= distanceX //distanceX左正右负
                scrollx = Math.min(scrollx, 0f)
                scrollx = Math.max(scrollXMax, scrollx)
                LogUtil.i(TAG, "---------滚动：$distanceX   $scrollx")
                postInvalidate()
            }
            return false
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            //          LogUtil.e(TAG,"onFling------------>velocityX="+velocityX+"    velocityY="+velocityY);
            /**
             * 从当前位置scrollx开始滚动，
             * 最小值为scrollXMax -- 滚动到最后
             * 最大值为0 -- 滚动到开始
             */
            mScroller.fling(scrollx.toInt(), 0, velocityX.toInt(), 0,
                    scrollXMax.toInt(), 0, 0, 0
            )
            return false
        }
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mScroller.isFinished) return
        if (mScroller.computeScrollOffset()) {
//            Log.d(TAG, "滚动后计算："+mScroller.getCurrX());
            scrollx = mScroller.currX.toFloat()
            invalidate()
        }
    }

    /**缩放监听*/
    private var scaleing = false   //是否正在缩放
    private inner class MyOnScaleGestureListener : ScaleGestureDetector.OnScaleGestureListener {
        private var beginScrollx = 0f
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            this@BaseChart.onScale(detector, beginScrollx)
            postInvalidate()
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            LogUtil.i(TAG, ">>>>>>>>>>>>>>>>>>>缩放开始了")
            beginScrollx = scrollx
            scaleing = true
            this@BaseChart.onScaleBegin(detector)
            return true
        }
        override fun onScaleEnd(detector: ScaleGestureDetector) {
            LogUtil.i(TAG, "<<<<<<<<<<<<<<<<<<<<缩放结束了")
            scaleing = false
            this@BaseChart.onScaleEnd(detector)
        }
    }

    /***************************3. 事件👆👆👆***************************/

    /***************************4. 动画👇👇👇***************************/
    private fun setLoadingIndicator(indicatorName: String) {
        var indicatorName = indicatorName
        if (TextUtils.isEmpty(indicatorName)) return
        loadingIndicator = BallPulseIndicator(context)
        indicatorName = "com.openxu.hkchart.loading.$indicatorName"
        try {
            loadingIndicator = Class.forName(indicatorName).getConstructor(Context::class.java).newInstance(context) as LoadingIndicator
            loadingIndicator.callback = this
        } catch (e: Exception) {
            Log.e(TAG, "Didn't find your class , check the name again !")
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if(loading && this::loadingIndicator.isInitialized)
            loadingIndicator.start()
    }
    override fun onDetachedFromWindow() {
        if(this::loadingIndicator.isInitialized)
            loadingIndicator.stop()
        if(this::chartAnim.isInitialized)
            chartAnim.cancel()
        super.onDetachedFromWindow()
    }
    override fun verifyDrawable(who: Drawable): Boolean {
        return who === loadingIndicator || super.verifyDrawable(who)
    }
    override fun invalidateDrawable(dr: Drawable) {
        if (verifyDrawable(dr)) {
            val dirty = dr.bounds
            invalidate(dirty)
        } else {
            super.invalidateDrawable(dr)
        }
    }

    private fun startChartAnimation() {
        if(!this::chartAnim.isInitialized){
            chartAnim = ValueAnimator.ofObject(AngleEvaluator(), 0f, 1f)
            chartAnim.duration = 1000
            chartAnim.interpolator = AccelerateDecelerateInterpolator()
            chartAnim.addUpdateListener { animation ->
                chartAnimValue = animation.animatedValue as Float
                postInvalidate()
            }
        }
        chartAnim.reverse()
        chartAnim.start()
        LogUtil.i(TAG, "-----------------开始动画")
    }
    /***************************4. 动画👆👆👆***************************/



}