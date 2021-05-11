package com.openxu.hkchart.bar

import android.content.Context
import android.graphics.*
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ScaleGestureDetector
import com.openxu.hkchart.BaseChart
import com.openxu.hkchart.config.*
import com.openxu.hkchart.data.FocusData
import com.openxu.hkchart.data.FocusPanelText
import com.openxu.utils.DensityUtil
import com.openxu.utils.FontUtil
import com.openxu.utils.LogUtil
import java.util.regex.Pattern

/**
 * Author: openXu
 * Time: 2021/5/9 12:00
 * class: MultipartBarChart
 * Description: 一个柱子分为多种颜色多部份的柱状图
 *
 * 特色：支持 缩放、滚动、惯性滚动、y坐标缩放变化、x坐标滚动变化
 *
 * 缩放因子：scalex  范围默认1~2
 * 滑动距离：scrollx  范围：scrollXMax ~ 0 （scrollXMax会自动计算）
 *
 */

class MultipartBarChart : BaseChart<MultipartBarData>{

    constructor(context: Context) :this(context, null)
    constructor(context: Context, attrs: AttributeSet?) :this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int):super(context, attrs, defStyle){
    }
    /***************************1. API👇👇👇***************************/
    /**设置数据*/
    private var _datas = mutableListOf<MultipartBarData>()
    var datas: MutableList<MultipartBarData>
        get() {return _datas}
        set(value) {
            _datas.clear()
            _datas.addAll(value)
            initial()
            chartConfig?.let {
                if (it.showAnim) chartAnimStarted = false
            }
            loading = false
        }

    /***************************1. API👆👆👆***************************/
    /***************************2. 子类重写👇👇👇***************************/
    /**配置*/
    private lateinit var yAxisMark : YAxisMark
    private lateinit var xAxisMark : XAxisMark
    private lateinit var barColor : IntArray
    private var focusPanelText: Array<FocusPanelText>? = null  //焦点面板显示内容
    private var dataTotalCount : Int = -1
    /**初步计算*/
    private var barWidth : Float = 0f  //柱宽度
    private var spacingRatio = 1f
    private var barSpace : Float = 0f  //柱间的间距
    private var oneDataWidth : Float = 0f  //单个柱子+间距 的宽度  ...(  | |)
    private var allDataWidth : Float = 0f  //所有柱子+间距 的宽度  (  | |  | |...| | )
    private var foucsRectWidth = 0f           //焦点面板矩形宽高
    private var foucsRectHeight = 0f

    override fun initial():Boolean{
        if(super.initial()) return true
        if(_datas.isNullOrEmpty()) return true
        if(chartConfig==null)
            throw RuntimeException("---------请配置图表")
        var config = chartConfig as MultipartBarConfig
        if(null==config.xAxisMark)
            throw RuntimeException("---------请设置x坐标")
        if(null==config.yAxisMark)
            throw RuntimeException("---------请设置y坐标")
        xAxisMark = config.xAxisMark!!
        yAxisMark = config.yAxisMark!!
        barColor = config.barColor
        barWidth = config.barWidth
        spacingRatio = config.spacingRatio
        barSpace = barWidth * spacingRatio

        dataTotalCount = config.dataTotalCount
        if(dataTotalCount<0)
            dataTotalCount = datas.size
        focusPanelText = config.focusPanelText

        /**重新确定表体矩形rectChart*/
        paintText.textSize = xAxisMark.textSize.toFloat()
        xAxisMark.textHeight = FontUtil.getFontHeight(paintText)
        xAxisMark.textLead = FontUtil.getFontLeading(paintText)
        //确定图表最下放绘制位置
        rectChart.bottom = rectDrawBounds.bottom - (xAxisMark.textHeight + xAxisMark.textSpace)
        xAxisMark.drawPointY = rectChart.bottom + xAxisMark.textSpace + xAxisMark.textLead
        LogUtil.e(TAG, "--------------设置数据后第一次计算所有数据y轴刻度，以确定图标左侧位置")
        calculateYMark(true)
        paintText.textSize = yAxisMark.textSize.toFloat()
        yAxisMark.textHeight = FontUtil.getFontHeight(paintText)
        yAxisMark.textLead = FontUtil.getFontLeading(paintText)
        var maxLable: String = yAxisMark.getMarkText(yAxisMark.cal_mark_max)
        val minLable: String = yAxisMark.getMarkText(yAxisMark.cal_mark_min)
        val maxYWidth = FontUtil.getFontlength(paintText, if(maxLable.length>minLable.length) maxLable else minLable)
        rectChart.left = rectDrawBounds.left + yAxisMark.textSpace + maxYWidth
        LogUtil.w(TAG, "原始顶部：${rectChart.top}  单位高度${if (TextUtils.isEmpty(yAxisMark.unit)) 0f else (yAxisMark.textHeight + yAxisMark.textSpace)}   y一半：${yAxisMark.textHeight / 2}")
        rectChart.top = rectDrawBounds.top + yAxisMark.textHeight / 2 + (if (TextUtils.isEmpty(yAxisMark.unit)) 0f else (yAxisMark.textHeight + yAxisMark.textSpace))
        rectChart.right = rectDrawBounds.right
        LogUtil.v(TAG, "确定表格矩形 $rectChart  宽度 ${rectChart.width()}  高度${rectChart.height()}")
        /**重新计算柱子宽度 和 间距*/
        LogUtil.e(TAG, "--------------根据显示配置和数据，计算柱子宽度和间距")
        //根据设置的柱子宽度和间距，计算所有数据宽度
        allDataWidth = dataTotalCount * barWidth + (dataTotalCount+1) * barSpace
        when(config.displayScheme){
            DisplayScheme.SHOW_ALL->{  //全部显示
                if(allDataWidth > rectChart.width()){  //超出时，重新计算barWidth
//                    barWidth * dataTotalCount + barWidth*config.spacingRatio*(dataTotalCount+1) = rectChart.width()
                    barWidth = rectChart.width()/(dataTotalCount + spacingRatio*(dataTotalCount+1))
                    barSpace = barWidth * spacingRatio
                    LogUtil.w(TAG, "全部展示时宽度超过，重新计算柱子宽度$barWidth  间距 $barSpace")
                }
            }
            DisplayScheme.SHOW_BEGIN->{}//从第一条数据开始展示，柱子宽度就是设置的宽度
            DisplayScheme.SHOW_END->{}  //从最后一条数据开始展示，柱子宽度就是设置的宽度
        }
        LogUtil.v(TAG, "确定柱子宽度 $barWidth  间距 $barSpace")
        /**确定第一条数据的绘制x坐标   计算滚动最大值*/
        oneDataWidth = barWidth + barSpace
        allDataWidth = dataTotalCount * barWidth + (dataTotalCount+1) * barSpace

        scrollx = 0f
        scrollXMax = 0f
        scalex = 1f
        if(allDataWidth>rectChart.width()){
            scrollXMax = rectChart.width() -allDataWidth //最大滚动距离，是一个负值
        }
        when(config.displayScheme){
            DisplayScheme.SHOW_ALL->{ }//全部显示
            DisplayScheme.SHOW_BEGIN->{
                scrollx = 0f
            }
            DisplayScheme.SHOW_END->{
                scrollx = scrollXMax
            }
        }
        LogUtil.v(TAG, "单个柱子+间距 $oneDataWidth  所有数据宽度 $allDataWidth")

        focusPanelText?.let {
            //计算焦点面板
            //2020-10-16 06：00
            //零序电流:15.2KW
            //A相电流:15.2KW
            //A相电流:15.2KW
            //A相电流:15.2KW
            foucsRectWidth = 0f
            foucsRectHeight = foucsRectSpace * 2.toFloat()
            var text: String
            maxLable = ((if (yAxisMark.getMarkText(yAxisMark.cal_mark_max).length >
                    yAxisMark.getMarkText(yAxisMark.cal_mark_min).length)
                yAxisMark.getMarkText(yAxisMark.cal_mark_max) else
                yAxisMark.getMarkText(yAxisMark.cal_mark_min))
                    + if (TextUtils.isEmpty(yAxisMark.unit)) "" else yAxisMark.unit)
            for (i in it.indices) {
                if (it[i].show) {
                    paintText.textSize = it[i].textSize.toFloat()
                    if (i == 0) {//x轴数据
                        foucsRectWidth = Math.max(foucsRectWidth, FontUtil.getFontlength(paintText, it[i].text))
                        foucsRectHeight += FontUtil.getFontHeight(paintText)
                    } else {
                        text = it[i].text + maxLable
                        foucsRectWidth = Math.max(foucsRectWidth, FontUtil.getFontlength(paintText, text))
                        LogUtil.w(TAG, "计算面板：$text    $foucsRectWidth")
                        foucsRectHeight += foucsRectTextSpace + FontUtil.getFontHeight(paintText)
                    }
                }
            }
            foucsRectWidth += foucsRectSpace * 4.toFloat()
        }
        return true
    }


    override fun drawChart(canvas: Canvas?) {
        LogUtil.e(TAG, "-----------开始绘制，当前缩放系数$scalex  偏移量$scrollx")
        if(_datas.isNullOrEmpty())
            return
        //预算需要绘制的组的开始和结尾index，避免不必要的计算浪费性能
//        caculateIndex()
        //计算Y轴刻度值
        calculateYMark(false)
        //计算x轴刻度值
        caculateXMark()

        val yMarkSpace = (rectChart.bottom - rectChart.top) / (yAxisMark.lableNum - 1)
        paintEffect.style = Paint.Style.STROKE
        paintEffect.strokeWidth = yAxisMark.lineWidth.toFloat()
        paintEffect.color = yAxisMark.lineColor
        paintText.textSize = yAxisMark.textSize.toFloat()
        paintText.color = yAxisMark.textColor
        val effects: PathEffect = DashPathEffect(floatArrayOf(15f, 6f, 15f, 6f), 0f)
        paintEffect.pathEffect = effects
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = yAxisMark.lineWidth.toFloat()
        paint.color = yAxisMark.lineColor
        for (i in 0 until yAxisMark.lableNum) {
            /**绘制横向线 */
            canvas!!.drawLine(rectChart.left, rectChart.bottom - yMarkSpace * i,
                    rectChart.right, rectChart.bottom - yMarkSpace * i, paint)
            /**绘制y刻度 */
            val text = yAxisMark.getMarkText(yAxisMark.cal_mark_min + i * yAxisMark.cal_mark)
            canvas!!.drawText(text,
                    rectChart.left - yAxisMark.textSpace - FontUtil.getFontlength(paintText, text),
                    rectChart.bottom - yMarkSpace * i - yAxisMark.textHeight / 2 + yAxisMark.textLead, paintText)
        }
        //绘制Y轴单位
        if (!TextUtils.isEmpty(yAxisMark.unit)) {
            canvas!!.drawText(yAxisMark.unit,
                    rectChart.left - yAxisMark.textSpace - FontUtil.getFontlength(paintText, yAxisMark.unit),
                    //y = 图表顶部 - 单位文字距离 - 单位文字高度 + 最上方y刻度高度/2
                    rectChart.top - yAxisMark.textSpace - yAxisMark.textHeight * 3 / 2 + yAxisMark.textLead, paintText)
        }

        val rect = RectF()
        paint.style = Paint.Style.FILL

        for(index:Int in startIndex..endIndex){
            //计算柱体x,y坐标
            if(allDataWidth*scalex<=rectChart.width()){
                rect.left = rectChart.left + (rectChart.width() - allDataWidth*scalex)/2 +
                        index * oneDataWidth*scalex + barSpace*scalex
//                LogUtil.v(TAG, "数据不够填充表，居中显示，当前数据x坐标 ${rect.left}")
            }else{
                rect.left = scrollx + rectChart.left + index * oneDataWidth*scalex + barSpace*scalex
//                LogUtil.v(TAG, "数据超过表，当前数据x坐标 ${rect.left}")
            }
            rect.right = rect.left + barWidth*scalex
            rect.bottom = rectChart.bottom
            rect.top = rectChart.bottom

            //★★★顺便为焦点数据设置x坐标，方便下一步绘制焦点
            focusData?.let {
                if(focusIndex == index)
                    it.point.x = rect.left + barWidth*scalex/2
            }

//            LogUtil.d(TAG, "$i 绘制："+_datas[i].valuey)
            /**绘制柱状 */
            //过滤掉绘制区域外的柱
            var barLayer:Int? = null
            if(index == startIndex || index == endIndex){
                /**
                 * Canvas有两种坐标系：
                 * 1. Canvas自己的坐标系：(0,0,canvas.width,canvas.height)，它是固定不变的
                 * 2. 绘图坐标系：用于绘制，通过Matrix让Canvas平移translate，旋转rotate，缩放scale 等时实际上操作的是绘图坐标系
                 * 由于绘图坐标系中Matrix的改变是不可逆的，所以产生了状态栈和Layer栈，它们分别运用于save方法和saveLayer方法，使得绘图坐标系恢复到保存时的状态
                 * 1. 状态栈：save()、restore()保存和还原变换操作Matrix以及Clip剪裁，也可以restoretoCount()直接还原到对应栈的保存状态
                 * 2. Layer栈:saveLayer()时会新建一个透明图层（离屏Bitmap-离屏缓冲），并且将saveLayer之前的一些Canvas操作延续过来，
                 *            后续的绘图操作都在新建的layer上面进行，当调用restore或者restoreToCount时更新到对应的图层和画布上
                 *
                 * 需要注意的是saveLayer会造成过渡绘制，可以考虑用 canvas?.save() canvas?.clipRect(rectChart)组合代替
                 */
                /*        barLayer = canvas?.saveLayer(rectChart.left, rectChart.top, rectChart.right,
                                rectChart.bottom*//* + xAxisMark.textSpace + xAxisMark.textHeight*//*
                        , paint, Canvas.ALL_SAVE_FLAG)*/
                //裁剪画布，避免x刻度超出
                barLayer = canvas?.save()
                canvas?.clipRect(rectChart)
            }
            for(vindex : Int in _datas[index].valuey.indices){
                paint.color = barColor[vindex]
                if (_datas[index].valuey[vindex] != null) {
                    val vh = rectChart.height() / (yAxisMark.cal_mark_max - yAxisMark.cal_mark_min) *
                            (_datas[index].valuey[vindex] - yAxisMark.cal_mark_min) * chartAnimValue
                    rect.top -= vh
                    canvas?.drawRect(rect, paint)
                    rect.bottom = rect.top
                }
            }
            if(barLayer!=null)
                canvas?.restoreToCount(barLayer)//还原画布，将柱子更新到画布上
            /**绘制x坐标 */
            //测试：绘制索引
//            canvas?.drawText("$i", rect.left + (barWidth*scalex) / 2 - FontUtil.getFontlength(paintText, "$i") / 2, xAxisMark.drawPointY, paintText)
            //从第一条数据开始每隔xIndexSpace绘制一个x刻度
            if((index - startIndex) % xIndexSpace == 0){
                val x = rect.left + (barWidth*scalex) / 2 - FontUtil.getFontlength(paintText, _datas[index].valuex) / 2
                //过滤掉超出图表范围的x值绘制，通常是第一条和最后一条
                if(x < paddingLeft || x+FontUtil.getFontlength(paintText, _datas[index].valuex) > measuredWidth - paddingRight)
                    continue
                canvas?.drawText(_datas[index].valuex, x,xAxisMark.drawPointY, paintText)
            }
        }

        drawFocus(canvas)
    }

    /**绘制焦点 */
    private val focusLineColor = Color.parseColor("#319A5A")
    private val focusLineSize = DensityUtil.dip2px(context, 1f)
    private val foucsRectTextSpace = DensityUtil.dip2px(context, 3f)
    private val foucsRectSpace = DensityUtil.dip2px(context, 6f)
    private fun drawFocus(canvas: Canvas?) {
        if (null == focusData || null==canvas) return
        if(focusData!!.point.x<rectChart.left ||focusData!!.point.x>rectChart.right)   //上次获取的焦点因为滑出矩形，不显示
            return
        //绘制竖直虚线
        val effects: PathEffect = DashPathEffect(floatArrayOf(8f, 5f, 8f, 5f), 0f)
        paintEffect.style = Paint.Style.STROKE
        paintEffect.strokeWidth = focusLineSize.toFloat()
        paintEffect.color = focusLineColor
        paintEffect.pathEffect = effects
        val path = Path()
        path.moveTo(focusData!!.point.x, rectChart.bottom)
        path.lineTo(focusData!!.point.x, rectChart.top)
        canvas.drawPath(path, paintEffect)
        //面板
        val showLeft: Boolean = focusData!!.point.x - rectChart.left > (rectChart.right - rectChart.left) / 2
        val rect = RectF(
                if (showLeft) focusData!!.point.x - foucsRectWidth - 30 else focusData!!.point.x + 30,
                rectChart.top /*+ (rectChart.bottom - rectChart.top)/2 - foucsRectHeight/2*/,
                if (showLeft) focusData!!.point.x - 30 else focusData!!.point.x + foucsRectWidth + 30,
                rectChart.top + foucsRectHeight /*+ (rectChart.bottom - rectChart.top)/2 + foucsRectHeight/2*/
        )
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.alpha = 230
        canvas.drawRect(rect, paint)
        //面板中的文字
        //2020-10-16 06：00
        //零序电流:15.2KW
        //A相电流:15.2KW
        //A相电流:15.2KW
        //A相电流:15.2KW
        var text = ""
        var top: Float = rect.top + foucsRectSpace
        val currentPoint = PointF()
        val radius = DensityUtil.dip2px(context, 2.5f).toFloat()
        focusPanelText?.let {
            for (i in it.indices) {
                if (it[i].show) {
                    paintText.textSize = it[i].textSize.toFloat()
                    paintText.color = it[i].textColor
                    if (i == 0) {  //x轴数据
                        text = focusData!!.data.valuex
                    } else {
                        top += foucsRectTextSpace.toFloat()
                        text = (it[i].text +
                                (if (focusData!!.data.valuey[i - 1] == null) ""
                                else YAxisMark.formattedDecimal(focusData!!.data.valuey[i - 1].toDouble(), 2))
                                + yAxisMark.unit)
                    }
                    canvas.drawText(text,
                            rect.left + foucsRectSpace,
                            top + FontUtil.getFontLeading(paintText), paintText)
                    top += FontUtil.getFontHeight(paintText)
                }
            }
        }
    }

    /***************************事件👇👇👇***************************/
    override fun onScaleBegin(detector: ScaleGestureDetector) {
        val width = -scrollx + (detector.focusX - rectChart.left)
        val zs = (width / (oneDataWidth*scalex)).toInt()
        val ys = width % (oneDataWidth*scalex)
        focusIndex = zs + if(ys>(barWidth/2+barSpace)*scalex)1 else 0
        LogUtil.i(TAG, "缩放开始了，焦点索引为$focusIndex") // 缩放因子
    }

    override fun onScale(detector: ScaleGestureDetector, beginScrollx: Float) {
        scalex *= detector.scaleFactor
        LogUtil.e(TAG, "--------------------当前缩放值$scalex  缩放${detector.scaleFactor}   缩放之后${scalex*detector.scaleFactor}")
        //缩放范围约束
        scalex = scalex.coerceAtMost(2f)
        scalex = scalex.coerceAtLeast(1f)
        LogUtil.e(TAG, "--------------------最终值$scalex ")
        //重新计算最大偏移量
        if(allDataWidth * scalex > rectChart.width()){
            scrollXMax = rectChart.width() - allDataWidth * scalex
            //为了保证焦点对应的点位置不变，是使用公式： beginScrollx + rectChart.left + focusIndex*beginPointWidth = scrollx + rectChart.left + focusIndex*pointWidth
            scrollx = beginScrollx + focusIndex * (oneDataWidth - oneDataWidth*scalex)
            scrollx = Math.min(scrollx, 0f)
            scrollx = Math.max(scrollXMax, scrollx)
            LogUtil.i(TAG, "缩放后偏移："+scrollx);
        }else{
            scrollXMax = 0f  //数据不能填充时，居中展示
            scrollx = 0f
        }
    }

    override fun onFocusTouch(point: PointF?) {
        try {
            focusData = null
            point?.let {
                if (!_datas.isNullOrEmpty()) {
                    val scaleOneWidth = oneDataWidth*scalex
                    val allWidth = allDataWidth*scalex
                    //避免滑出
                    var left = rectChart.left
                    var right = rectChart.right
                    if(allWidth<rectChart.width()){
                        left = rectChart.left + (rectChart.width()-allWidth)/2
                        right = rectChart.left + allWidth
                    }
                    LogUtil.e(TAG, "========焦点位置${point.x}")
                    point.x = Math.max(point.x, left)
                    point.x = Math.min(point.x, right)
                    LogUtil.e(TAG, "========左右范围：${left}*${right}   焦点纠正后$point.x")
                    val width = if(allWidth<rectChart.width()){
                        point.x - left
                    }else{
                        -scrollx + (point.x - left)
                    }
                    //获取焦点对应的数据的索引
                    focusIndex = (width / scaleOneWidth).toInt() - 1  //计算的是索引（从0开始），所以-1
                    LogUtil.e(TAG, "========单个宽度$scaleOneWidth  宽度$width   整数索引$focusIndex")
                    if(width % scaleOneWidth > barSpace*scalex/2) {
                        focusIndex += 1
                        LogUtil.e(TAG, "========焦点在下一个点范围了：$focusIndex")
                    }
                    focusIndex = Math.max(0, Math.min(focusIndex, _datas.size - 1))
                    LogUtil.e(TAG, "========焦点索引：$focusIndex")
                    focusData = FocusData(_datas[focusIndex], it)
                }
            }
//            postInvalidate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    /***************************事件👆👆👆***************************/
    /***************************2. 子类重写👆👆👆***************************/

    /***************************3. 特殊👇👇👇***************************/
    private var startIndex = 0
    private var endIndex = 0
    /**计算当前缩放、移动状态下，需要绘制的数据的起始和结束索引*/
    private fun caculateIndex(){
        //预算需要绘制的组的开始和结尾index，避免不必要的计算浪费性能
        val scaleOneWidth = oneDataWidth*scalex
        if(allDataWidth*scalex<=rectChart.width()){
            startIndex = 0
            endIndex = _datas.size-1
        }else{
            startIndex = (-scrollx / scaleOneWidth).toInt()
            endIndex = ((-scrollx + rectChart.width()) / scaleOneWidth).toInt() - 1
//            LogUtil.w(TAG, "总宽度：${-scrollx + rectChart.width()}  当前状态下一个柱子及间隙宽度$scaleOneWidth   最后一条数据索引取整$endIndex")
            val nextVisible = (-scrollx + rectChart.width()) % scaleOneWidth>=barSpace*scalex
            endIndex += if(nextVisible)1 else 0
//            LogUtil.w(TAG, "取余：${(-scrollx + rectChart.width()) % scaleOneWidth}  柱子宽度${barSpace*scalex}   是否可见$nextVisible   结束索引$endIndex")
            endIndex = endIndex.coerceAtMost(_datas.size - 1)
        }
    }
    /**y值累加*/
    private fun getTotalValuey(data : MultipartBarData) : Float{
        var valuey = 0f
        for(v in data.valuey)
            valuey+=v
        return valuey
    }

    /**获取startIndex~endIndex的数据最大最小值，并根据需要显示几个y刻度计算出递增值*/
    private fun calculateYMark(all:Boolean) {
        val redundance = 1.1f //y轴最大和最小值冗余
        yAxisMark.cal_mark_max = -Float.MAX_VALUE //Y轴刻度最大值
        yAxisMark.cal_mark_min = Float.MAX_VALUE  //Y轴刻度最小值
        var startIdx = 0
        var endIdx = _datas.size-1
        if(!all){
            caculateIndex()
            startIdx = startIndex
            endIdx = endIndex
        }
        for(index in startIdx..endIdx){
            val valuey = getTotalValuey(_datas[index])
            yAxisMark.cal_mark_max = Math.max(yAxisMark.cal_mark_max, valuey)
            yAxisMark.cal_mark_min = Math.min(yAxisMark.cal_mark_min, valuey)
        }
        LogUtil.w(TAG, "$startIdx ~ $endIdx 真实最小最大值：" + yAxisMark.cal_mark_min + "  " + yAxisMark.cal_mark_max)
        //只有一个点的时候
        if (yAxisMark.cal_mark_min == yAxisMark.cal_mark_max) {
            when {
                yAxisMark.cal_mark_min > 0 -> {
                    yAxisMark.cal_mark_min = 0f
                }
                yAxisMark.cal_mark_min == 0f -> {
                    yAxisMark.cal_mark_max = 1f
                }
                yAxisMark.cal_mark_min < 0 -> {
                    yAxisMark.cal_mark_max = 0f
                }
            }
        }
        if (yAxisMark.markType == MarkType.Integer) {
            val min = if (yAxisMark.cal_mark_min > 0) 0 else yAxisMark.cal_mark_min.toInt()
            val max = yAxisMark.cal_mark_max.toInt()
            var mark = (max - min) / (yAxisMark.lableNum - 1) + if ((max - min) % (yAxisMark.lableNum - 1) > 0) 1 else 0
            mark = if (mark == 0) 1 else mark //最大值和最小值都为0的情况
            val first = (mark.toString() + "").substring(0, 1).toInt() + 1
            if ((mark.toString() + "").length == 1) {
                //YMARK = 1、2、5、10
                mark = if (mark == 3 || mark == 4 || mark == 6 || mark == 7 || mark == 8 || mark == 9) if (mark == 3 || mark == 4) 5 else 10 else mark
            } else if ((mark.toString() + "").length == 2) {
                mark = first * 10
            } else if ((mark.toString() + "").length == 3) {
                mark = first * 100
            } else if ((mark.toString() + "").length == 4) {
                mark = first * 1000
            } else if ((mark.toString() + "").length == 5) {
                mark = first * 10000
            } else if ((mark.toString() + "").length == 6) {
                mark = first * 100000
            }
            yAxisMark.cal_mark_min = 0f
            yAxisMark.cal_mark_max = mark * (yAxisMark.lableNum - 1).toFloat()
            yAxisMark.cal_mark = mark.toFloat()
        } else {   //Float   //Percent
            yAxisMark.cal_mark_max = if (yAxisMark.cal_mark_max < 0) yAxisMark.cal_mark_max / redundance else yAxisMark.cal_mark_max * redundance
//            yAxisMark.cal_mark_min = if (yAxisMark.cal_mark_min < 0) yAxisMark.cal_mark_min * redundance else yAxisMark.cal_mark_min / redundance

            yAxisMark.cal_mark_min = 0f

            yAxisMark.cal_mark = (yAxisMark.cal_mark_max - yAxisMark.cal_mark_min) / (yAxisMark.lableNum - 1)
        }
        //小数点位
        if (yAxisMark.digits == 0) {
            val mark = (yAxisMark.cal_mark_max - yAxisMark.cal_mark_min) / (yAxisMark.lableNum - 1)
            if (mark < 1) {
                val pattern = "[1-9]"
                val p = Pattern.compile(pattern)
                val m = p.matcher(mark.toString() + "") // 获取 matcher 对象
                m.find()
                val index = m.start()
                yAxisMark.digits = index - 1
                LogUtil.w(TAG, mark.toString() + "第一个大于0的数字位置：" + index + "   保留小数位数：" + yAxisMark.digits)
            }
        }
//        LogUtil.w(TAG, "最终最小最大值：" + yAxisMark.cal_mark_min + "  " + yAxisMark.cal_mark_max + "   " + yAxisMark.cal_mark)
    }

    /**根据startIndex~endIndex计算x标签间隔数量*/
    //从当前绘制的第一条数据开始，每隔多少展示一个x标签
    private var xIndexSpace: Int = 0
    private fun caculateXMark() {
        caculateIndex()
        paintText.textSize = xAxisMark.textSize.toFloat()
        //计算当前显示的数据的x轴文字长度最大值
        var xTextMaxLength = 0f
        for(index in startIndex..endIndex){
            xTextMaxLength = xTextMaxLength.coerceAtLeast(FontUtil.getFontlength(paintText, _datas[index].valuex))
        }
        var xNumber = (rectChart.width() / xTextMaxLength).toInt()
        val dataNumber = endIndex - startIndex + 1
        LogUtil.e(TAG, "绘制的数据条数${endIndex-startIndex+1}  X轴文字最长长度$xTextMaxLength   理论最多可显示$xNumber 个")
        xNumber = Math.min(xNumber, xAxisMark.lableNum)
        when(xNumber){
            1->xIndexSpace = endIndex - startIndex + 10   //只显示第一个
            2->xIndexSpace = endIndex - startIndex   //显示第一个和最后一个
            3->{   //取中点
                when(dataNumber % 2){
                    0->xIndexSpace = (dataNumber-1)/2   //数据条数为偶数 变为奇数取中点
                    1->xIndexSpace = dataNumber/2   //数据条数为奇数取中点
                }
            }
            else->{
                xIndexSpace = when(dataNumber%xNumber){
                    0-> dataNumber/xNumber        //数据条数 整除 lable数 时，取除数
                    else-> dataNumber/xNumber + 1 //不能整除时 +1
                }
            }
        }
    }
    /***************************3. 特殊👆👆👆***************************/






}

