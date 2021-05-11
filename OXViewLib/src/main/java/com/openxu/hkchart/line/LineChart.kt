package com.openxu.hkchart.line

import android.content.Context
import android.graphics.*
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.ScaleGestureDetector
import com.openxu.hkchart.BaseChart
import com.openxu.hkchart.config.*
import com.openxu.hkchart.data.FocusData
import com.openxu.hkchart.data.FocusPanelText
import com.openxu.hkchart.data.LinePoint
import com.openxu.utils.DensityUtil
import com.openxu.utils.FontUtil
import com.openxu.utils.LogUtil
import com.openxu.utils.NumberFormatUtil
import java.util.*
import java.util.regex.Pattern

/**
 * Author: openXu
 * Time: 2021/5/11 12:43
 * class: LineChart
 * Description:
 */
class LineChart  : BaseChart<MutableList<LinePoint?>> {

    constructor(context: Context) :this(context, null)
    constructor(context: Context, attrs: AttributeSet?) :this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int):super(context, attrs, defStyle){
    }
    /***************************1. API👇👇👇***************************/
    /**设置数据*/
    private var _datas = mutableListOf<MutableList<LinePoint>>()
    var datas: MutableList<MutableList<LinePoint>>
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
    private var lineType : LineType = LineType.BROKEN
    private var lineWidth = DensityUtil.dip2px(context, 1f).toFloat()
    private var lineColor = intArrayOf(
            Color.parseColor("#f46763"),
            Color.parseColor("#3cd595"),
            Color.parseColor("#4d7bff"),
            Color.parseColor("#4d7bff"))
    private var pageShowNum  = 0 //第一次页面总数据量   没有设置展示数据量，则默认为全部展示
    private var dataTotalCount : Int = -1
    /**设置焦点面板显示内容*/
    private var focusPanelText: Array<FocusPanelText>? = null
    /**初步计算*/
    private var maxPointNum = 0 //点最多的线的点数量
    private var maxPointIndex  = 0//点最多的线的索引
    private var pointWidthMin= 0f //最初的每个点占据的宽度，最小缩放值
    private var pointWidthMax = 0f //最初的每个点占据的宽度，最大放大值
    private var pointWidth = 0f //每个点占据的宽度
    override fun initial():Boolean{
        if(super.initial()) return true
        if(_datas.isNullOrEmpty()) return true
        if(chartConfig==null)
            throw RuntimeException("---------请配置图表")
        var config = chartConfig as LineChartConfig
        xAxisMark = config.xAxisMark?:XAxisMark.Builder(context)
                .lableNum(5)
                .build()
        yAxisMark = config.yAxisMark?:YAxisMark.Builder(context)
                .lableNum(6)
                .markType(MarkType.Integer)
                .unit("")
                .build()
        lineType = config.lineType
        lineWidth = config.lineWidth
        lineColor = config.lineColor
        pageShowNum = config.pageShowNum
        dataTotalCount = config.dataTotalCount
        if(dataTotalCount<0)
            dataTotalCount = datas.size
        focusPanelText = config.focusPanelText

        maxPointNum = 0
        maxPointIndex = 0
        for (i in 0 until _datas.size) {
            if (maxPointNum <= _datas[i].size) {
                maxPointNum = _datas[i].size
                maxPointIndex = i
            }
        }
        Log.w(TAG, "点最多的线索引为：$maxPointIndex    点数：$maxPointNum")

        paintText.textSize = xAxisMark.textSize.toFloat()
        xAxisMark.textHeight = FontUtil.getFontHeight(paintText)
        xAxisMark.textLead = FontUtil.getFontLeading(paintText)
        Log.w(TAG, "x轴字体高度：" + xAxisMark.textHeight.toString() + "   textLead：" + xAxisMark.textLead.toString() + "  xAxisMark.textSpace" + xAxisMark.textSpace)
        //确定图表最下放绘制位置
        rectChart.bottom = rectDrawBounds.bottom - xAxisMark.textHeight - xAxisMark.textSpace
        Log.w(TAG, "rectDrawBounds.bottom：" + rectDrawBounds.bottom + "    rectChart.bottom=" + rectChart.bottom)
        xAxisMark.drawPointY = rectChart.bottom + xAxisMark.textSpace + xAxisMark.textLead
        calculateYMark()
        paintText.textSize = yAxisMark.textSize.toFloat()
        yAxisMark.textHeight = FontUtil.getFontHeight(paintText)
        yAxisMark.textLead = FontUtil.getFontLeading(paintText)
        Log.w(TAG, "y轴字体高度：" + yAxisMark.textHeight.toString() + "   textLead：" + yAxisMark.textLead)
        var maxLable = if (yAxisMark.getMarkText(yAxisMark.cal_mark_max).length > yAxisMark.getMarkText(yAxisMark.cal_mark_min).length) yAxisMark.getMarkText(yAxisMark.cal_mark_max) else yAxisMark.getMarkText(yAxisMark.cal_mark_min)
        LogUtil.w(TAG, "Y刻度最大字符串：$maxLable")
        if (!TextUtils.isEmpty(yAxisMark.unit)) maxLable = if (yAxisMark.unit.length > maxLable.length) yAxisMark.unit else maxLable
        LogUtil.w(TAG, "Y刻度最大字符串：$maxLable")
        rectChart.left = paddingLeft + yAxisMark.textSpace + FontUtil.getFontlength(paintText, maxLable)
        rectChart.top = rectDrawBounds.top + yAxisMark.textHeight / 2 +
                if (TextUtils.isEmpty(yAxisMark.unit)) 0f else yAxisMark.textHeight + yAxisMark.textSpace
        rectChart.right = rectDrawBounds.right

        //没有设置展示数据量，则默认为全部展示
        if (pageShowNum <= 0) pageShowNum = maxPointNum
        if (maxPointNum < pageShowNum) //最多的点小于需要显示的点，则全部展示
            pageShowNum = maxPointNum
        Log.w(TAG, "pageShowNum=${pageShowNum}")
        pointWidthMin = rectChart.width() / (maxPointNum - 1) //缩小到全部显示

        pointWidth = rectChart.width() / (pageShowNum - 1)
        pointWidthMax = rectChart.width() / 4 //最大只能放大到每个标签显示5个点

//        pointWidthMax = rectChart.width() / (xAxisMark.lableNum-1) / 5;   //最大只能放大到每个标签显示5个点
        //        pointWidthMax = rectChart.width() / (xAxisMark.lableNum-1) / 5;   //最大只能放大到每个标签显示5个点
        Log.w(TAG, "缩放最小最大宽度=$pointWidthMin     $pointWidthMax")
        //数据没有展示完，说明可以滚动
        scrollXMax = 0f
        if (pageShowNum < maxPointNum) scrollXMax = -(pointWidth * (maxPointNum - 1) - rectChart.width()) //最大滚动距离，是一个负值

        scrollx = if (config.displayScheme==DisplayScheme.SHOW_BEGIN) 0f else scrollXMax

        caculateXMark()
        focusPanelText?.let {
            //计算焦点面板
            //2020-10-16 06：00
            //零序电流:15.2KW
            //A相电流:15.2KW
            //A相电流:15.2KW
            //A相电流:15.2KW
            foucsRectWidth = 0f
            foucsRectHeight = foucsRectSpace * 2f
            var text: String
            maxLable = (if (yAxisMark.getMarkText(yAxisMark.cal_mark_max).length > yAxisMark.getMarkText(yAxisMark.cal_mark_min).length) yAxisMark.getMarkText(yAxisMark.cal_mark_max) else yAxisMark.getMarkText(yAxisMark.cal_mark_min))
                    .toString() + if (TextUtils.isEmpty(yAxisMark.unit)) "" else yAxisMark.unit
            for (i in it.indices) {
                if (it[i].show) {
                    paintText.textSize = it[i].textSize.toFloat()
                    if (i == 0) {
                        //x轴数据
                        foucsRectWidth = Math.max(foucsRectWidth, FontUtil.getFontlength(paintText, it[i].text))
                        foucsRectHeight += FontUtil.getFontHeight(paintText)
                    } else {
//                        text = focusPanelText[i].text+maxLable+ yAxisMark.unit;
                        text = it[i].text + maxLable
                        foucsRectWidth = Math.max(foucsRectWidth, FontUtil.getFontlength(paintText, text))
                        Log.w(TAG, "计算面板：$text    $foucsRectWidth")
                        foucsRectHeight += foucsRectTextSpace + FontUtil.getFontHeight(paintText)
                    }
                }
            }
            foucsRectWidth += foucsRectSpace * 4
        }
        /**计算点坐标 */
        /*  for (int i = 0; i < _datas.size(); i++) {
            List<LinePoint> linePoints = _datas.get(i);
            for (int j = 0; j < linePoints.size(); j++) {
                if (linePoints.get(j).getValuey() == null)
                    continue;
                linePoints.get(j).setPoint(new PointF(
                        rectChart.left + j * pointWidth,
                        rectChart.bottom - (rectChart.bottom - rectChart.top) /
                                (yAxisMark.cal_mark_max - yAxisMark.cal_mark_min) * (linePoints.get(j).getValuey() - yAxisMark.cal_mark_min)
                ));
            }
        }*/Log.w(TAG, "计算scrollXMax=$scrollXMax   scrollx=$scrollx")

        return true
    }


    override fun drawChart(canvas: Canvas?) {
        LogUtil.e(TAG, "-----------开始绘制，当前缩放系数$scalex  偏移量$scrollx")
        if(_datas.isNullOrEmpty())
            return
        val startTime = System.currentTimeMillis()
        val yMarkSpace = (rectChart.bottom - rectChart.top) / (yAxisMark.lableNum - 1)
        paintEffect.style = Paint.Style.STROKE
        paintEffect.strokeWidth = yAxisMark.lineWidth.toFloat()
        paintEffect.color = yAxisMark.lineColor
        paintText.textSize = yAxisMark.textSize.toFloat()
        paintText.color = yAxisMark.textColor
//        canvas.drawLine(rectChart.left, rectChart.top, rectChart.left, rectChart.bottom, paint);
        //        canvas.drawLine(rectChart.left, rectChart.top, rectChart.left, rectChart.bottom, paint);
        val effects: PathEffect = DashPathEffect(floatArrayOf(15f, 6f, 15f, 6f), 0f)
        paintEffect.pathEffect = effects

        paint.style = Paint.Style.STROKE
        paint.color = yAxisMark.lineColor
        for (i in 0 until yAxisMark.lableNum) {
            /**绘制横向线 */
            paint.strokeWidth = if (i == 0) yAxisMark.lineWidth * 2.5f else yAxisMark.lineWidth.toFloat()
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
                    rectChart.top - yAxisMark.textSpace - yAxisMark.textHeight * 3 / 2 + yAxisMark.textLead, paintText)
        }

        /**绘制x轴刻度 */
//        if(xAxisMark.lables!=null){
//            //绘制固定的
//            drawFixedXLable(canvas);
//            lables = xAxisMark.lables;
//        }else{
//            drawXLable(canvas);
//        }
        /**绘制折线 */
        if (maxPointNum <= 0) return
        paintText.textSize = xAxisMark.textSize.toFloat()
        paintText.color = xAxisMark.textColor
        paint.strokeWidth = lineWidth
        val radius = DensityUtil.dip2px(context, 3f).toFloat()

        val path = Path()
        val lastPoint = PointF()
        val currentPoint = PointF()
        var startIndex = (-scrollx / pointWidth).toInt()
        var endIndex = ((-scrollx + rectChart.width()) / pointWidth + 1).toInt()
        startIndex = Math.max(startIndex, 0)
        endIndex = Math.min(endIndex, maxPointNum - 1)
//        Log.w(TAG, "绘制索引："+startIndex+" 至  "+endIndex+"   scrollx="+scrollx);
        //        Log.w(TAG, "绘制索引："+startIndex+" 至  "+endIndex+"   scrollx="+scrollx);
        val clipRect = RectF(rectChart.left - radius - lineWidth / 2, rectChart.top,
                rectChart.right + radius + lineWidth / 2,
                rectChart.bottom + xAxisMark.textSpace + xAxisMark.textHeight)
        canvas!!.saveLayer(clipRect.left, clipRect.top, clipRect.right, clipRect.bottom, paint, Canvas.ALL_SAVE_FLAG)
        var drawXLable = false
        for (i in _datas.indices) {
            val linePoints: List<LinePoint> = _datas[i]
            if (linePoints.isNullOrEmpty()) break
            path.reset()
            for (j in startIndex..endIndex) {
                if (j > startIndex + (endIndex - startIndex) * chartAnimValue) break
                //每条线的点数量可能不一样
                if (j >= linePoints.size /* || linePoints.get(j).getValuey() == null*/) continue
                currentPoint.x = scrollx + rectChart.left + j * pointWidth
                currentPoint.y = rectChart.bottom - (rectChart.bottom - rectChart.top) /
                        (yAxisMark.cal_mark_max - yAxisMark.cal_mark_min) * (linePoints[j].valuey - yAxisMark.cal_mark_min)
                if (lineType == LineType.BROKEN) {
                    if (path.isEmpty) {
                        path.moveTo(currentPoint.x, currentPoint.y)
                    } else {
                        path.lineTo(currentPoint.x, currentPoint.y)
                    }
                } else {
                    if (j == startIndex) {
                        path.moveTo(currentPoint.x, currentPoint.y)
                    } else if (j == startIndex + 1) {   //二阶
                        val x = lastPoint.x + (currentPoint.x - lastPoint.x) / 2
                        val y = currentPoint.y
                        path.quadTo(x, y, currentPoint.x, currentPoint.y)
                    } else if (j <= endIndex - 1) {  //三阶
                        val x1 = lastPoint.x + (currentPoint.x - lastPoint.x) / 2
                        val y1 = lastPoint.y
                        val y2 = currentPoint.y
                        path.cubicTo(x1, y1, x1, y2, currentPoint.x, currentPoint.y)
                    } else if (j == endIndex) {   //最后一个 二阶
                        val x = lastPoint.x + (currentPoint.x - lastPoint.x) / 2
                        val y = lastPoint.y
                        path.quadTo(x, y, currentPoint.x, currentPoint.y)
                    }
                    lastPoint.x = currentPoint.x
                    lastPoint.y = currentPoint.y
                }
                drawXLable = false
                if (i == maxPointIndex) {
                    drawXLable = if (xlables.size > 0 && xlables.contains(linePoints[j].valuex)) {
                        true
                    } else {
                        if (chartConfig!!.displayScheme==DisplayScheme.SHOW_BEGIN) {
                            j % xindexSpace === 0
                        } else {
                            (j - (maxPointNum - 1)) % xindexSpace === 0
                        }
                    }
                    if (drawXLable) {
//                        FLog.v(xlables.size()+"绘制x轴刻度"+linePoints.get(j).getValuex());
                        var x: Float
                        x = if (j == 0) {
                            currentPoint.x
                        } else if (j == maxPointNum - 1) {
                            currentPoint.x - FontUtil.getFontlength(paintText, linePoints[j].valuex)
                        } else {
                            currentPoint.x - FontUtil.getFontlength(paintText, linePoints[j].valuex) / 2
                        }
                        canvas!!.drawText(linePoints[j].valuex, x,
                                rectChart.bottom + xAxisMark.textSpace + xAxisMark.textLead, paintText)
                    }
                }
            }
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = lineWidth
            paint.color = lineColor[i]
            /**
             * Xfermode 有三个实现类: AvoidXfermode,PixelXorXfermode,PorterDuffXfermode
             *
             * 1. AvoidXfermode：对原来的像素进行处理，AvoidXfermode不支持硬件加速，使用它需要关闭硬件加速。其次，最好在新建图层上绘制. 构造方法参数分别代表:
             * opColor被处理的像素颜色
             * 容差值（原像素在一定范围内与传入的像素相似则处理）
             * 模式: TARGET模式判断画布上是否有与opColor相似（容差）的颜色，如果有，则把该区域“染”上一层我们”画笔的颜色“，
             * AVOID与TARGET相反，将画布上与传入opColor不相似的染上画笔颜色
             * 比如下面的代码中首先绘制一个图片，然后对图片上的白色像素进行处理，染色为画笔的红色
             * canvas.drawBitmap(mBmp,null,new Rect(0,0,width,height),mPaint);
             * mPaint.setXfermode(new AvoidXfermode(Color.WHITE,100, AvoidXfermode.Mode.TARGET));
             * mPaint.setColor(Color.RED)
             * canvas.drawRect(0,0,width,height,mPaint);
             *
             * 2. PixelXofXermode 没设么用，不支持硬件加速
             *
             * 3. PorterDuffXfermode是最常用的，它用于描述2D图像图像合成的模式，一共有12中模式描述数字图像合成的基本手法，包括
             * Clear、Source Only、Destination Only、Source Over、Source In、Source
             * Out、Source Atop、Destination Over、Destination In、Destination
             * Out、Destination Atop、XOR。通过组合使用 Porter-Duff 操作，可完成任意 2D
             * 图像的合成。在绘图时会先检查该画笔Paint对象有没有设置Xfermode，如果没有设置Xfermode，那么直接将绘制的图形覆盖Canvas对应位置原有的像素；
             * 如果设置了Xfermode，那么会按照Xfermode具体的规则来更新Canvas中对应位置的像素颜色。
             *
             * 使用时通常结合canvas.saveLayer(clipRect.left, clipRect.top, clipRect.right, clipRect.bottom, paint, Canvas.ALL_SAVE_FLAG)创建一个全透明的layer层，否则会产生不可预期的结果
             *
             * 使用它时要搞清楚两个概念，DST表示在画笔设置它之前画布上已经绘制的内容，SRC表示设置之后绘制的内容，PorterDuffXfermode就是将两个部分的像素按照一定的模式进行合并
             */
            //这里设置DST_OVER，目的是将绘制path之前已经绘制的线上的点显示的线之上，要不然线会遮住小圆点
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OVER)
            canvas!!.drawPath(path, paint)
            paint.xfermode = null
        }
//        canvas.restore();
//        Log.w(TAG, "绘制一次需要："+(System.currentTimeMillis() - startTime)+ " ms");

        //        canvas.restore();
//        Log.w(TAG, "绘制一次需要："+(System.currentTimeMillis() - startTime)+ " ms");
        drawFocus(canvas)
    }
    /**绘制焦点 */
    private val focusLineColor = Color.parseColor("#319A5A")
    private val focusLineSize = DensityUtil.dip2px(context, 1f)
    private val foucsRectTextSpace = DensityUtil.dip2px(context, 3f)
    private val foucsRectSpace = DensityUtil.dip2px(context, 6f)
    //焦点面板矩形宽高
    private var foucsRectWidth = 0f
    private var foucsRectHeight = 0f
    private fun drawFocus(canvas: Canvas?) {
        try {
            if (null == focusData || maxPointNum == 0 || null==canvas) return
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
            var top = rect.top + foucsRectSpace
            val currentPoint = PointF()
            val radius = DensityUtil.dip2px(context, 2.5f).toFloat()
            focusPanelText?.let {
                for (i in it.indices) {
                    if (it[i].show) {
                        paintText.textSize = it[i].textSize.toFloat()
                        paintText.color = it[i].textColor
                        if (i == 0) {
                            //x轴数据
                            for (point in focusData!!.data) {
                                if (point != null) {
                                    text = if (TextUtils.isEmpty(point.valuexfocus)) point.valuex else point.valuexfocus
                                    break
                                }
                            }
                        } else {
                            top += foucsRectTextSpace.toFloat()
                            /*   text = focusPanelText[i].text+
                                (focusData!!.data.get(i-1)==null?"":getFocusYText(focusData!!.data.get(i-1).getValuey()))
                                + yAxisMark.unit;*/
                            text = (it[i].text +
                                    (if (focusData!!.data[i - 1] == null) "" else YAxisMark.formattedDecimal(focusData!!.data[i - 1]!!.valuey.toDouble(), 2))
                                    + yAxisMark.unit)

                            //绘制焦点圆圈
                            if (focusData!!.data[i - 1] != null) {
                                currentPoint.x = focusData!!.point.x
                                currentPoint.y = rectChart.bottom - (rectChart.bottom - rectChart.top) /
                                        (yAxisMark.cal_mark_max - yAxisMark.cal_mark_min) * (focusData!!.data[i - 1]!!.valuey - yAxisMark.cal_mark_min)
                                paint.style = Paint.Style.STROKE
                                paint.color = lineColor.get(i - 1)
                                canvas.drawCircle(currentPoint.x, currentPoint.y, radius, paint)
                                paint.style = Paint.Style.FILL
                                paint.color = Color.WHITE
                                canvas.drawCircle(currentPoint.x, currentPoint.y, radius - lineWidth / 2, paint)
                            }
                        }
                        canvas.drawText(text,
                                rect.left + foucsRectSpace,
                                top + FontUtil.getFontLeading(paintText), paintText)
                        top += FontUtil.getFontHeight(paintText)
                    }
                }
            }
           
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /***************************事件👇👇👇***************************/
    private var beginScrollx = 0f
    private var beginPointWidth = 0f
    override fun onScaleBegin(detector: ScaleGestureDetector) {
        beginScrollx = scrollx
        beginPointWidth = pointWidth
    }

    override fun onScale(detector: ScaleGestureDetector, beginScrollx: Float) {
        pointWidth *= detector.scaleFactor
        //缩放范围约束
//            pointWidthMin = rectChart.width() / (maxPointNum-1);   //缩小到全部显示
//            pointWidth = rectChart.width() / (pageShowNum-1);
//            pointWidthMax = rectChart.width() / 4;   //最大只能放大到每个标签显示5个点
        pointWidth = Math.min(pointWidth, pointWidthMax)
        pointWidth = Math.max(pointWidth, pointWidthMin)
        //重新计算最大偏移量
        scrollXMax = -(pointWidth * (maxPointNum - 1) - rectChart.width()) //最大滚动距离，是一个负值
        //计算当前偏移量
        Log.i(TAG, "=============================当前偏移：$scrollx    两点宽度 = $pointWidth")
        //为了保证焦点对应的点位置不变，是使用公式： beginScrollx + rectChart.left + focusIndex*beginPointWidth = scrollx + rectChart.left + focusIndex*pointWidth
        scrollx = beginScrollx + focusIndex * (beginPointWidth - pointWidth)
        scrollx = Math.min(scrollx, 0f)
        scrollx = Math.max(scrollXMax, scrollx)
        caculateXMark()
//            Log.i(TAG, "缩放后偏移："+scrollx);
    }

    override fun onFocusTouch(point: PointF?) {
        focusData = null
        point?.let {
            if (!_datas.isNullOrEmpty() && focusPanelText!=null) {
                //避免滑出
                point.x = Math.max(point.x, rectChart.left);
                point.x = Math.min(point.x, rectChart.right);
                //获取焦点对应的数据的索引
                focusIndex =((-scrollx + (point.x - rectChart.left)) / pointWidth).toInt()
                if ((-scrollx + (point.x - rectChart.left)) - focusIndex * pointWidth > pointWidth / 2) {
                    LogUtil.e(TAG, "========焦点在下一个点范围了：" + focusIndex);
                    focusIndex += 1;
                }
                LogUtil.e(TAG, "========焦点索引：" + focusIndex);
                focusIndex = Math.max(0, Math.min(focusIndex, maxPointNum - 1));
                point.x = rectChart.left + (focusIndex * pointWidth + scrollx);
                val datas = mutableListOf<LinePoint?>()
                for (line in _datas) {
                    if (focusIndex < line.size) datas.add(line[focusIndex])
                    else datas.add(null)
                }
                focusData = FocusData(datas, it)
            }
        }

    }

    /***************************事件👆👆👆***************************/
    /***************************2. 子类重写👆👆👆***************************/

    /***************************3. 特殊👇👇👇***************************/

    private var xindexSpace: Int = 0
    private val xlables: MutableList<String> = ArrayList()
    private fun caculateXMark() {
        xlables.clear()
        if (xAxisMark.lables != null && xAxisMark.lables.size > 0) {
            xlables.addAll(Arrays.asList(*xAxisMark.lables))
            return
        }
        val markSpace = rectChart.width() / (xAxisMark.lableNum - 1)
        //每隔多少展示一个标签
        xindexSpace = (markSpace / pointWidth).toInt()
        xindexSpace = Math.max(xindexSpace, 1)
    }
    private fun calculateYMark() {
        if (maxPointNum == 0) {
            //没有数据
            yAxisMark.cal_mark_min = 0f
            yAxisMark.cal_mark_max = yAxisMark.lableNum - 1.toFloat()
            yAxisMark.cal_mark = 1f
            return
        }
        val redundance = 1.01f //y轴最大和最小值冗余
        yAxisMark.cal_mark_max = -Float.MAX_VALUE //Y轴刻度最大值
        yAxisMark.cal_mark_min = Float.MAX_VALUE //Y轴刻度最小值
        for (linePoints in _datas) {
            for (point in linePoints) {
                yAxisMark.cal_mark_max = Math.max(yAxisMark.cal_mark_max, point.valuey)
                yAxisMark.cal_mark_min = Math.min(yAxisMark.cal_mark_min, point.valuey)
            }
        }
        LogUtil.w(TAG, "真实最小最大值：" + yAxisMark.cal_mark_min + "  " + yAxisMark.cal_mark_max)
        //只有一个点的时候
        if (yAxisMark.cal_mark_min == yAxisMark.cal_mark_max) {
            if (yAxisMark.cal_mark_min > 0) {
                yAxisMark.cal_mark_min = 0f
            } else if (yAxisMark.cal_mark_min == 0f) {
                yAxisMark.cal_mark_max = 1f
            } else if (yAxisMark.cal_mark_min < 0) {
                yAxisMark.cal_mark_max = 0f
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
            yAxisMark.cal_mark_min = if (yAxisMark.cal_mark_min < 0) yAxisMark.cal_mark_min * redundance else yAxisMark.cal_mark_min / redundance
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
        LogUtil.w(TAG, "最终最小最大值：" + yAxisMark.cal_mark_min + "  " + yAxisMark.cal_mark_max + "   " + yAxisMark.cal_mark)
    }


    /**绘制焦点 */
    /**********************************3. 测量和绘制👇 */
    /**获取焦点面板上数值展示字符串 */
    fun getFocusYText(value: Float): String? {
        when (yAxisMark.markType) {
            MarkType.Integer -> return (value as Long).toString()
            MarkType.Float -> return NumberFormatUtil.formattedDecimal(value.toDouble())
            MarkType.Percent -> return NumberFormatUtil.formattedDecimalToPercentage(value.toDouble(), 2)
        }
        return value.toString() + ""
    }

    /**绘制 XAxisMark.lables 设置的固定x刻度， */
    private fun drawFixedXLable(canvas: Canvas) {
        val oneWidth = (-scrollXMax + rectChart.width()) / (xAxisMark.lables.size - 1)
        Log.w(TAG, "最大滚动：" + scrollXMax + "  图表宽度" + rectChart.width() + "  lable数量" + xAxisMark.lables.size + "   单个跨度：" + oneWidth)
        paintText.textSize = xAxisMark.textSize.toFloat()
        paintText.color = xAxisMark.textColor
        var x: Float
        val restoreCount = canvas.save()
        canvas.clipRect(RectF(rectChart.left, rectChart.bottom, rectChart.right, rectChart.bottom + xAxisMark.textSpace + xAxisMark.textHeight))
        for (i in xAxisMark.lables.indices) {
            val text = xAxisMark.lables[i]
            x = if (i == 0) {
                scrollx + rectChart.left + i * oneWidth
            } else if (i == xAxisMark.lables.size - 1) {
                scrollx + rectChart.left + i * oneWidth - FontUtil.getFontlength(paintText, text)
            } else {
                scrollx + rectChart.left + i * oneWidth - FontUtil.getFontlength(paintText, text) / 2
            }
            canvas.drawText(text, x,
                    rectChart.bottom + xAxisMark.textSpace + xAxisMark.textLead, paintText)
        }
        canvas.restoreToCount(restoreCount)
    }


    /***************************3. 特殊👆👆👆***************************/

}

