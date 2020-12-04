package com.openxu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Scroller;

import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.Nullable;

/**
 * Author: openXu
 * Time: 2020/11/6 10:13
 * class: BigBitmapView
 * Description:
 */
public class BigBitmapView extends View implements View.OnTouchListener, GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener {

    private String TAG = "BigBitmapView";

    private Rect mRect;
    private BitmapFactory.Options mOptions;
    private BitmapRegionDecoder mDecoder;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private Scroller mScroller;

    private float mScale, minScale;

    private Bitmap mBitmap;
    private int mImageWidth, mImageHeight;
    private int mViewWidth, mViewHeight;
    private Paint paint;

    public BigBitmapView(Context context) {
        this(context, null);
    }
    public BigBitmapView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public BigBitmapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRect = new Rect();
        mOptions = new BitmapFactory.Options();
        mGestureDetector = new GestureDetector(context, this);
        scaleGestureDetector = new ScaleGestureDetector(context, this);
        mScroller = new Scroller(context);
        setOnTouchListener(this);
        paint = new Paint();
        paint.setAntiAlias(true);
      /*  try {
            mBitmap = BitmapFactory.decodeStream(getResources().getAssets().open("world6.png"));
            Log.w(TAG, "大图："+bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void setImage(InputStream is){
        //获取图片宽高
        mOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, mOptions);
        mImageWidth = mOptions.outWidth;
        mImageHeight = mOptions.outHeight;
        //开启复用
        mOptions.inMutable = true;
        //每个像素存储在2个字节上，只有RGB通道；ARGB_8888占4个字节；ARGB_4444也是占2个字节，但是保存颜色可能存在精度丢失
        //再说可能有些图片就没有透明度通道，所以使用ARGB也没必要
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        mOptions.inJustDecodeBounds = false;
        //创建区域解码器，用于解析图片的某一块矩形区域
        try {
            mDecoder = BitmapRegionDecoder.newInstance(is, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cal();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.w(TAG, "onMeasure："+getMeasuredWidth()+"*"+getMeasuredHeight());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.w(TAG, "onSizeChanged："+getMeasuredWidth()+"*"+getMeasuredHeight());
        cal();
    }

    private void cal(){
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
        if(mViewWidth==0 || mViewHeight==0)
            return;
        //确定图片初始加载区域
        float scaleX = (float) mViewWidth / mImageWidth;
        float scaleY = (float) mViewHeight / mImageHeight;
        if(scaleX < 0.5f && scaleY < 0.5f){
            //大图
            mScale = 1;
        }else if(scaleX < 0.5f){
            //横长图
            mScale = scaleY;
        }else if(scaleY < 0.5f){
            //竖长图
            mScale = scaleX;
        }else {
            //普通图，直接加载
            mScale = Math.min(scaleX, scaleY);
        }
        minScale = mScale;
        mRect.left = 0;
        mRect.top = 0;
        mRect.right = (int)(mViewWidth/mScale);
        mRect.bottom = (int)(mViewHeight/mScale);
        Log.i(TAG, "scaleX="+scaleX+"   scaleY="+scaleY+"   mScale="+mScale);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mDecoder == null)
            return;
        //复用内存
        mOptions.inBitmap = mBitmap;
        mBitmap = mDecoder.decodeRegion(mRect, mOptions);
        Matrix matrix = new Matrix();
        matrix.setScale(mScale, mScale);
        canvas.drawBitmap(mBitmap, matrix,paint);
    }

    /**
     * 事件处理时可重写onTouchEvent()方法，或者设置OnTouchListener，重写onTouch()
     * OnTouchListener的优先级比onTouchEvent()高，这两个都是在dispatchTouchEvent()中调用的onTouch()会先执行
     *
     * onTouchEvent()或者onTouch()在接受到第一个事件ACTION_DOWN后，只要其中一个返回true，则下次事件还是会传过来
     * 如果都返回false，则表示该view不需要处理事件，事件就不会被传过来了
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.i(TAG, "========onTouchEvent"+event.getAction());
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
//                Log.i(TAG, "========onTouchEvent按下");
                return true;
            case MotionEvent.ACTION_MOVE:
//                Log.i(TAG, "========onTouchEvent滑动");
                return false;
            case MotionEvent.ACTION_UP:
//                Log.i(TAG, "========onTouchEvent抬起");
                return false;
        }
        return super.onTouchEvent(event);
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        Log.i(TAG, "========onTouch"+event.getAction());
        scaleGestureDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if(!mScroller.isFinished())
            mScroller.forceFinished(true);
        return true;   //事件被消费，下次才能继续收到事件
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //改变矩形坐标
        mRect.offset((int)distanceX, (int)distanceY);
        //边界处理
        if(mRect.bottom > mImageHeight){
            mRect.bottom = mImageHeight;
            mRect.top = mImageHeight - (int)(mViewHeight/mScale);
        }
        if(mRect.top < 0){
            mRect.top = 0;
            mRect.bottom = (int)(mViewHeight/mScale);
        }
        if(mRect.right > mImageWidth){
            mRect.right = mImageWidth;
            mRect.left = mImageWidth - (int)(mViewWidth/mScale);
        }
        if(mRect.left < 0){
            mRect.left = 0;
            mRect.right = (int)(mViewWidth/mScale);
        }
        invalidate();
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //int startX, int startY, int velocityX, int velocityY,
        // int minX, int maxX, int minY, int maxY
        mScroller.fling(mRect.left, mRect.top,
                (int)-velocityX, (int)-velocityY,
            0, mImageWidth - (int)(mViewWidth/mScale),
            0, mImageHeight - (int)(mViewHeight/mScale)
        );
        return false;
    }

    @Override
    public void computeScroll() {
        if(mScroller.isFinished())
            return;
        if(mScroller.computeScrollOffset()){
            mRect.left = mScroller.getCurrX();
            mRect.top = mScroller.getCurrY();
            mRect.right = mRect.left+(int)(mViewWidth/mScale);
            mRect.bottom = mRect.top+(int)(mViewHeight/mScale);
            invalidate();
        }
    }

    /**************************/
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
//        detector.getFocusX()
        mScale *= detector.getScaleFactor();
        Log.i(TAG, "focusX = " + detector.getFocusX());       // 缩放中心，x坐标
        Log.i(TAG, "focusY = " + detector.getFocusY());       // 缩放中心y坐标
        Log.i(TAG, "scale = " + detector.getScaleFactor());   // 缩放因子
        Log.i(TAG, "缩放："+detector.getScaleFactor()+"   mscale= "+mScale);
        if(mScale*detector.getScaleFactor()<minScale){
            mScale = minScale;
        }
        //改变矩形坐标
        mRect.offset((int)(detector.getCurrentSpanX() - detector.getPreviousSpanX()),
                (int)(detector.getCurrentSpanY() - detector.getPreviousSpanY()));
        //边界处理
        if(mRect.bottom > mImageHeight){
            mRect.bottom = mImageHeight;
            mRect.top = mImageHeight - (int)(mViewHeight/mScale);
        }
        if(mRect.top < 0){
            mRect.top = 0;
            mRect.bottom = (int)(mViewHeight/mScale);
        }
        if(mRect.right > mImageWidth){
            mRect.right = mImageWidth;
            mRect.left = mImageWidth - (int)(mViewWidth/mScale);
        }
        if(mRect.left < 0){
            mRect.left = 0;
            mRect.right = (int)(mViewWidth/mScale);
        }
        Log.i(TAG, "当前矩形："+mRect+"   mscale= "+mScale);
        invalidate();
        return true;
    }
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }
    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }
    /**************************/

}
