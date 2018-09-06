package com.openxu.cview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.openxu.utils.DensityUtil;


/**
 * autour: openXu
 * date: 2017/3/10 13:32
 * className: TitleLayout
 * version:
 * description: 通用的titlebar
 * Sample：(属性详情参照attrs.xml-TitleView)
 */
public class TitleLayout extends RelativeLayout {

    private String TAG = "TitleLayout";

    private View view_tran_bar;             //状态栏预留占位
    /*总容器*/
    private RelativeLayout titleLayout;              //title总布局
    /*左侧部分*/
    private LinearLayout ll_left;                    //左侧容器
    private LinearLayout ll_back;
    //    private View view_left;
    private ImageView iv_left_back, iv_left_icon;    //返回键、图标
    private TextView tv_left_text;                   //左侧文字
    /*中间*/
    private RelativeLayout rl_center;                //中间容器
    private TextView tv_center_text;                 //中间文字
    /*右侧*/
    private LinearLayout ll_right;                   //右侧容器
    private RelativeLayout rl_right_icon;            //右侧图标（可带气泡）
    private TextView tv_right_text, tv_right_pop;                   //右侧文字
    private ImageView iv_right_icon;           //菜单图标、气泡


    private LinearLayout ll_right_icon_content;       //其他菜单容器

    /**
     * 属性值
     */
    private String textLeft, textcenter, textRight, numRightPop;  //文字
    private int iconBack, iconLeft, iconCenterRow, iconRight;  //图标
    private int[] rightIconArr;            //其他菜单容器

    private float titleHeight;       //文字高度
    private float textSize;          //文字大小
    private int textColor;           //文字颜色
    private float textIconSpace;     //item之间的间距
    private float leftSpace;         //标题左侧的空隙
    private float rightSpace;        //标题右侧的空隙
    private float centerRowSpace;    //中间箭头和文字的距离
    private int background;

    private float leftUsed;
    private float rightUsed;

    public TitleLayout(Context context) {
        this(context, null);
    }

    public TitleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.title_layout, this, true);

        view_tran_bar = findViewById(R.id.view_tran_bar);
        //设置状态栏占位高度
        setStatusBarHeight(getStatusBarHeight(getContext()));

        //title总布局
        titleLayout = (RelativeLayout) findViewById(R.id.titleLayout);
        /*左侧部分*/
        ll_left = (LinearLayout) findViewById(R.id.ll_left);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
//        view_left = findViewById(R.id.view_left);
        iv_left_back = (ImageView) findViewById(R.id.iv_left_back);
        iv_left_icon = (ImageView) findViewById(R.id.iv_left_icon);
        tv_left_text = (TextView) findViewById(R.id.tv_left_text);
        /*中间*/
        rl_center = (RelativeLayout) findViewById(R.id.rl_center);
        tv_center_text = (TextView) findViewById(R.id.tv_center_text);
        /*右侧*/
        ll_right = (LinearLayout) findViewById(R.id.ll_right);
        rl_right_icon = (RelativeLayout) findViewById(R.id.rl_right_icon);
        tv_right_text = (TextView) findViewById(R.id.tv_right_text);
        iv_right_icon = (ImageView) findViewById(R.id.iv_right_icon);
        tv_right_pop = (TextView) findViewById(R.id.tv_right_pop);
        ll_right_icon_content = (LinearLayout) findViewById(R.id.ll_right_icon_content);

        if(attrs!=null){
            //获取自定义属性的值
            TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TitleView, defStyleAttr, 0);

            textLeft = ta.getString(R.styleable.TitleView_textLeft);
            textcenter = ta.getString(R.styleable.TitleView_textcenter);
            textRight = ta.getString(R.styleable.TitleView_textRight);
            iconBack = ta.getResourceId(R.styleable.TitleView_iconBack, 0);
            iconLeft = ta.getResourceId(R.styleable.TitleView_iconLeft, 0);
            iconCenterRow = ta.getResourceId(R.styleable.TitleView_iconCenterRow, 0);
            iconRight = ta.getResourceId(R.styleable.TitleView_iconRight, 0);

            textSize = ta.getDimension(R.styleable.TitleView_android_textSize, 1);
            titleHeight = ta.getDimension(R.styleable.TitleView_titleHeight, 0);
            textColor = ta.getColor(R.styleable.TitleView_android_textColor, Color.WHITE);
            textIconSpace = ta.getDimension(R.styleable.TitleView_textIconSpace, 0);
            leftSpace = ta.getDimension(R.styleable.TitleView_leftSpace, 0);
            rightSpace = ta.getDimension(R.styleable.TitleView_rightSpace, 0);
            centerRowSpace = ta.getDimension(R.styleable.TitleView_centerRowSpace, 0);
/*
            Drawable backgroundDrawable = getBackground();
            LogUtil.i(TAG, "backgroundDrawable="+backgroundDrawable);
            int defTitleColor = getResources().getColor(R.color.title_bar_color);
            //background包括color和Drawable,这里分开取值
            if(null == backgroundDrawable){
            }else if(backgroundDrawable instanceof ColorDrawable){
                background = ta.getColor(R.styleable.TitleView_android_background, defTitleColor);
                LogUtil.i(TAG, "设置title背景色"+background);
                setBackgroundColor(background);
            }else{
                background = ta.getResourceId(R.styleable.TitleView_android_background, -1);
                LogUtil.i(TAG, "设置title背景资源图片"+background);
                setBackgroundResource(background);
            }
            LogUtil.i(TAG, "background = "+background);*/

//        LogUtil.i(TAG, "textLeft = "+textLeft);
//        LogUtil.i(TAG, "textcenter = "+textcenter);
//        LogUtil.i(TAG, "textRight = "+textRight);
//        LogUtil.i(TAG, "iconBack = " + iconBack);
//        LogUtil.i(TAG, "iconLeft = "+iconLeft);
//        LogUtil.i(TAG, "iconCenterRow = "+iconCenterRow);
//        LogUtil.i(TAG, "iconRight = "+iconRight);
//        LogUtil.i(TAG, "iconRight = "+iconRight);
//        LogUtil.i(TAG, "textSize = "+textSize);
//        LogUtil.i(TAG, "titleHeight = "+titleHeight);
//        LogUtil.i(TAG, "textColor = "+textColor);
//        LogUtil.i(TAG, "textIconSpace = "+textIconSpace);
//        LogUtil.i(TAG, "leftSpace = " + leftSpace);
//        LogUtil.i(TAG, "rightSpace = "+rightSpace);
//        LogUtil.i(TAG, "centerRowSpace = "+centerRowSpace);

            ta.recycle();
        }

        show();
    }

    /**
     * 显示，当设置属性值后，调用此方法
     */
    public void show() {
        setParams();
        setGone();
        setVisibleAndLayout();
    }

    private void setParams() {
        tv_left_text.setTextColor(textColor);
        tv_left_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        tv_center_text.setTextColor(textColor);
        tv_center_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        tv_right_text.setTextColor(textColor);
        tv_right_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        tv_right_pop.setTextColor(textColor);
//        LogUtil.d(TAG, "设置字体");

        //设置标题高度
        ViewGroup.LayoutParams params = titleLayout.getLayoutParams();
        if (params == null)
            return;

        params.height = (int) titleHeight;
        titleLayout.setLayoutParams(params);

        /*设置左边距*/
                /*设置左边距*/
/*        RelativeLayout.LayoutParams rl_params = (RelativeLayout.LayoutParams) ll_left.getLayoutParams();
        rl_params.leftMargin = (int)leftSpace;
        ll_left.setLayoutParams(rl_params);*/

        LinearLayout.LayoutParams ll_params = (LinearLayout.LayoutParams) iv_left_back.getLayoutParams();
        ll_params.leftMargin = (int) leftSpace;
        ll_params.rightMargin = (int) leftSpace;
        iv_left_back.setLayoutParams(ll_params);
//        iv_left_back.setPadding((int)leftSpace, 0, 0, 0);
//        ll_back.setPadding((int)leftSpace, ll_back.getPaddingTop(), ll_back.getPaddingRight(), ll_back.get);

        /*设置右边距*/
        LayoutParams rl_params1 = (LayoutParams) ll_right.getLayoutParams();
        rl_params1.rightMargin = (int) rightSpace;
        ll_right.setLayoutParams(rl_params1);

        leftUsed = leftSpace;
        rightUsed = rightSpace;
    }

    public TitleLayout setStatusBarHeight(int height) {
        if(view_tran_bar!=null){
            LinearLayout.LayoutParams barparams = (LinearLayout.LayoutParams) view_tran_bar.getLayoutParams();
            barparams.height = height;
            view_tran_bar.setLayoutParams(barparams);
        }
        return this;
    }

    public static int getStatusBarHeight(Context context){
        int barHeight = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            /**由于app的标题栏背景是图片而不是纯色，所以version>4.4的版本都显示在状态栏之上，需要添加一个状态栏高度的占位控件*/
//            LogUtil.i(TAG, "版本："+Build.VERSION.SDK_INT+" >= "+Build.VERSION_CODES.KITKAT+"，需要显示状态栏占位");
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                barHeight = context.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return barHeight;
    }

    private void setGone() {
        tv_left_text.setVisibility(View.GONE);
        ll_back.setVisibility(View.GONE);
        iv_left_icon.setVisibility(View.GONE);
        ll_left.setVisibility(View.GONE);

        tv_center_text.setVisibility(View.GONE);
        rl_center.setVisibility(View.GONE);

        tv_right_text.setVisibility(View.GONE);
        iv_right_icon.setVisibility(View.GONE);
        tv_right_pop.setVisibility(View.GONE);
        rl_right_icon.setVisibility(View.GONE);
        ll_right_icon_content.setVisibility(View.GONE);
        ll_right.setVisibility(View.GONE);
    }

    public OnMenuClickListener getOnMenuClickListener() {
        return listener;
    }

    /**
     * 控制item显示
     */
    private void setVisibleAndLayout() {
//        LogUtil.d(TAG, "设置可见");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        if (!TextUtils.isEmpty(textLeft) || iconBack != 0 || iconLeft != 0) {
//            LogUtil.d(TAG, "左侧可见");
            ll_left.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(textLeft)) {
                tv_left_text.setVisibility(View.VISIBLE);
                tv_left_text.setText(textLeft);
                tv_left_text.measure(0, 0);
//                LogUtil.d(TAG, "左侧文字可见"+ tv_left_text.getMeasuredWidth());
//                LogUtil.d(TAG, "左侧文字可见"+ FontUtil.getFontlength(textPaint, textLeft));
                leftUsed += tv_left_text.getMeasuredWidth();

                tv_left_text.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onClick(MENU_NAME.MENU_LEFT_TEXT, tv_left_text);
                    }
                });
            }
            if (iconBack != 0) {
                ll_back.setVisibility(View.VISIBLE);
                iv_left_back.setImageResource(iconBack);
//                iv_left_back.setPadding(20, 20, 20, 20);

                BitmapFactory.decodeResource(getResources(), iconBack, options);
                int width = options.outWidth;
//                LogUtil.d(TAG, "左侧返回可见"+width);
                leftUsed += width;
                ll_back.setOnClickListener(v -> {
                    ((Activity) getContext()).onBackPressed();
                });
            }
            if (iconLeft != 0) {
                iv_left_icon.setVisibility(View.VISIBLE);
                iv_left_icon.setImageResource(iconLeft);
                BitmapFactory.decodeResource(getResources(), iconLeft, options);
                int width = options.outWidth;
//                LogUtil.d(TAG, "左侧图标可见"+width);
                leftUsed += width;
                /*iv_left_icon.setOnClickListener(v->{
                    if(listener!=null)
                        listener.onClick(MENU_NAME.MENU_LEFT_ICON);
                });*/
            }

            if (!TextUtils.isEmpty(textLeft) && (iconLeft != 0 || iconBack != 0)) {
                LinearLayout.LayoutParams ll_params = (LinearLayout.LayoutParams) tv_left_text.getLayoutParams();
                ll_params.leftMargin = (int) textIconSpace;
                leftUsed += textIconSpace;
                tv_left_text.setLayoutParams(ll_params);
            }

            if (iconBack == 0 && (!TextUtils.isEmpty(textLeft) || iconLeft != 0)) {
                //返回键不显示时，流出左边空隙
                LayoutParams rl_params = (LayoutParams) ll_left.getLayoutParams();
                rl_params.leftMargin = (int) leftSpace;
                ll_left.setLayoutParams(rl_params);
            }

        }
        if (!TextUtils.isEmpty(textcenter) || iconCenterRow != 0) {
            rl_center.setVisibility(View.VISIBLE);
//            LogUtil.d(TAG, "中间可见");
            if (!TextUtils.isEmpty(textcenter)) {
//                LogUtil.d(TAG, "中间文字可见：" + textcenter);
                tv_center_text.setVisibility(View.VISIBLE);
                tv_center_text.setText(textcenter);

                if (iconCenterRow != 0) {
//                    LogUtil.d(TAG, "中间箭头可见");
                    Drawable iconCenterDraw = getResources().getDrawable(iconCenterRow);
                    iconCenterDraw.setBounds(0, 0, iconCenterDraw.getMinimumWidth(), iconCenterDraw.getMinimumHeight());
                    tv_center_text.setCompoundDrawables(null, null, iconCenterDraw, null);
                    tv_center_text.setCompoundDrawablePadding((int) centerRowSpace);
                    rl_center.setOnClickListener(v -> {
                        if (listener != null)
                            listener.onClick(MENU_NAME.MENU_CENTER, rl_center);
                    });
                }
            }

        }
        if (!TextUtils.isEmpty(textRight) || iconRight != 0 || !TextUtils.isEmpty(numRightPop)) {
            ll_right.setVisibility(View.VISIBLE);
//            LogUtil.d(TAG, "右侧可见");
            if (!TextUtils.isEmpty(textRight)) {
                tv_right_text.setVisibility(View.VISIBLE);
                tv_right_text.setText(textRight);
                tv_right_text.measure(0, 0);
//                LogUtil.d(TAG, "右侧文字可见"+tv_left_text.getMeasuredWidth());
//                LogUtil.d(TAG, "右侧文字可见"+FontUtil.getFontlength(textPaint, textRight));
                rightUsed += tv_left_text.getMeasuredWidth();
                tv_right_text.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onClick(MENU_NAME.MENU_RIGHT_TEXT, tv_right_text);
                    }
                });
            }
            if (iconRight != 0) {
                rl_right_icon.setVisibility(View.VISIBLE);
                iv_right_icon.setVisibility(View.VISIBLE);
                iv_right_icon.setImageResource(iconRight);
                rl_right_icon.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onClick(MENU_NAME.MENU_RIGHT_ICON, rl_right_icon);
                    }
                });
//                LogUtil.d(TAG, "右侧图标可见");
                if (!TextUtils.isEmpty(numRightPop)) {
                    tv_right_pop.setVisibility(View.VISIBLE);
                    tv_right_pop.setText(numRightPop + "");
                }
            }
            /*设置右侧menu图标和气泡的间距*/
            BitmapFactory.decodeResource(getResources(), iconRight, options);
            int width = options.outWidth;
            int height = options.outHeight;
//            LogUtil.i(TAG, "右侧图标的宽高："+width+" * "+height);
            LinearLayout.LayoutParams ll_params = (LinearLayout.LayoutParams) rl_right_icon.getLayoutParams();
            if (!TextUtils.isEmpty(numRightPop)) {
                tv_right_pop.measure(0, 0);
//                LogUtil.d(TAG, "气泡大小"+tv_right_pop.getMeasuredWidth());
                int widthPop = tv_right_pop.getMeasuredWidth();
                int heightPop = tv_right_pop.getMeasuredHeight();
//                LogUtil.i(TAG, "气泡的宽高："+widthPop+" * "+heightPop);
                ll_params.width = width + widthPop;
                ll_params.height = height + heightPop;
            } else {
                ll_params.width = width;
                ll_params.height = height;
            }

            rl_right_icon.setLayoutParams(ll_params);
            rightUsed += ll_params.width;

            if (!TextUtils.isEmpty(textRight) && iconRight != 0) {
                ll_params = (LinearLayout.LayoutParams) rl_right_icon.getLayoutParams();
                ll_params.leftMargin = (int) textIconSpace;
                rl_right_icon.setLayoutParams(ll_params);
                rightUsed += textIconSpace;
            }

            if(null!=rightIconArr && rightIconArr.length>0){
                ll_right_icon_content.setVisibility(View.VISIBLE);
                for(int id : rightIconArr){
                    LinearLayout.LayoutParams content_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    content_params.leftMargin = DensityUtil.dip2px(getContext(), 10);
                    ImageView iv = new ImageView(getContext());
                    iv.setImageResource(id);
                    ll_right_icon_content.addView(iv ,content_params);
                    iv.setOnClickListener(v -> {
                        if (contentIconListener != null) {
                            contentIconListener.onClick(id, v);
                        }
                    });
                }
            }

        }


        if (!TextUtils.isEmpty(textcenter)) {
             /*避免中间的内容超出范围*/
//            LogUtil.e(TAG, "左边占用：" + leftUsed + "   右边占用：" + rightUsed);
            float max = leftUsed > rightUsed ? leftUsed : rightUsed;
            int widthRow = 0;
            if (iconCenterRow != 0) {
                BitmapFactory.decodeResource(getResources(), iconCenterRow, options);
                widthRow = options.outWidth;
            }

            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            int centerTextMaxWidth = wm.getDefaultDisplay().getWidth() -
                    (int) (2 * (max + textIconSpace)) - widthRow
                    - (int) (widthRow == 0 ? 0 : centerRowSpace + widthRow);
//            LogUtil.e(TAG, "   剩余：" + centerTextMaxWidth);
            tv_center_text.measure(0, 0);
//            LogUtil.d(TAG, "中间标题长度：" + tv_center_text.getMeasuredWidth());
            float centerTextWidth = tv_center_text.getMeasuredWidth() > centerTextMaxWidth ?
                    centerTextMaxWidth : tv_center_text.getMeasuredWidth();

            LayoutParams rl_params = (LayoutParams) tv_center_text.getLayoutParams();
            rl_params.width = (int) centerTextWidth;
            tv_center_text.setLayoutParams(rl_params);
        }
        requestLayout();
    }


    /**
     * 点击事件类型
     */
    public enum MENU_NAME {
        MENU_BACK,
        //        MENU_LEFT_ICON,
        MENU_LEFT_TEXT,
        MENU_CENTER,
        MENU_RIGHT_ICON,
        MENU_RIGHT_TEXT,
        MENU_RIGHT_CONTENTICON
    }

    public TitleLayout setRightIconArr(int[] rightIconArr) {
        this.rightIconArr = rightIconArr;
        return this;
    }

    public void setLeftIconVisible(boolean showIcon){
        if(showIcon){
            ll_back.setVisibility(VISIBLE);
        }else{
            ll_back.setVisibility(GONE);
        }
    }

    public interface OnMenuClickListener {
        void onClick(MENU_NAME menu, View view);
    }
    public interface OnContentIconClickListener {
        void onClick(int srcId, View view);
    }

    private OnMenuClickListener listener;
    private OnContentIconClickListener contentIconListener;

    public TitleLayout setOnMenuClickListener(OnMenuClickListener listener) {
        this.listener = listener;
        return this;
    }
    public TitleLayout setOnContentIconClickListener(OnContentIconClickListener listener) {
        this.contentIconListener = listener;
        return this;
    }

    public TitleLayout setBackgroundColor1(int Color) {
        titleLayout.setBackgroundColor(Color);
        view_tran_bar.setBackgroundColor(Color);
        return this;
    }

    /**
     * 设置内容
     */

    public TitleLayout setTextLeft(String textLeft) {
        this.textLeft = textLeft;
        tv_left_text.setText(textLeft);
        return this;
    }

    public TitleLayout setTextcenter(String textcenter) {
        this.textcenter = textcenter;
        tv_center_text.setText(textcenter);
        return this;
    }

    public TitleLayout setTextRight(String textRight) {
        this.textRight = textRight;
        tv_right_text.setText(textRight);
        return this;
    }

    public TitleLayout setIconBack(int iconBack) {
        this.iconBack = iconBack;
        iv_left_back.setImageResource(iconBack);
        return this;
    }

    public TitleLayout setIconLeft(int iconLeft) {
        this.iconLeft = iconLeft;
        iv_left_icon.setImageResource(iconLeft);
        return this;
    }

    public TitleLayout setIconCenterRow(int iconCenterRow) {
        this.iconCenterRow = iconCenterRow;
        return this;
    }

    public TitleLayout setIconRight(int iconRight) {
        this.iconRight = iconRight;
        iv_right_icon.setImageResource(iconRight);
        return this;
    }

    public TitleLayout setNumRightPop(String numRightPop) {
        this.numRightPop = numRightPop;
        tv_right_pop.setText(numRightPop);
        return this;
    }

    public TitleLayout setTitleHeight(int titleHeight) {
        this.titleHeight = DensityUtil.dip2px(getContext(), titleHeight);
        return this;
    }

    public TitleLayout setTextSize(int textSize) {
        this.textSize = textSize;
        return this;
    }

    public TitleLayout setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public TitleLayout setTextIconSpace(int textIconSpace) {
        this.textIconSpace = DensityUtil.dip2px(getContext(), textIconSpace);
        return this;
    }

    public TitleLayout setLeftSpace(int leftSpace) {
        this.leftSpace = DensityUtil.dip2px(getContext(), leftSpace);
        return this;
    }

    public TitleLayout setRightSpace(int rightSpace) {
        this.rightSpace = DensityUtil.dip2px(getContext(), rightSpace);
        return this;
    }

    public TitleLayout setCenterRowSpace(int centerRowSpace) {
        this.centerRowSpace = DensityUtil.dip2px(getContext(), centerRowSpace);
        return this;
    }

    public String getTextLeft() {
        return textLeft;
    }

    public String getTextcenter() {
        return textcenter;
    }

    public String getTextRight() {
        return textRight;
    }
}
