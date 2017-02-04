package com.ftd2009.panelviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangyuan on 2016/12/15.
 */
public class PanelView extends View {
    private Context mContext;
    private int mWidth;
    private int mHeight;
    //刻度的个数
    private int mTikeCount = 7;
    //旋转的角度
    float rAngle = 45;
    private int mArcColor = Color.parseColor("#80ffffff");
    //刻度宽度
    private float mTikeWidth;

    private Paint paintCircleAndTike;
    private Paint paintProgressBackground;
    private Paint paintProgress;
    private Paint paintText;
    //刻度数
    private Paint paintTikeText;

    private int mAqiNumTextColor = Color.parseColor("#ffffff");
    private int mAqiStatusTextColor = Color.parseColor("#ffffff");
    private int mAqiSuggestTextColor = Color.parseColor("#a6ffffff");

    private float mAqiNumTextSize;//dpToPx(20,mContext);
    private float mAqiStatusTextSize;//dpToPx(15,mContext);
    private float mAqiSuggestTextSize;//dpToPx(12,mContext);

    private int mAqiNumText;
    private String mAqiStatusText;
    private String mAqiSuggestText = "建议佩戴口罩";
    //刻度的数值
    private String[] mTexts = {"0", "50", "100", "150", "200", "300", "500"};
    //外层刻度占用的宽度
    private float mTextWidth;
    //外层刻度与最外层圈的间距
    private float mTextPadding;
    //最外层粗圈的宽度
    private float mBigCircleWidth;
    //最外层细圈的宽度
    private float mSmallCircleWidth;

    private RectF mBigRect;
    //起始角度
    private float mStartAngle = 135;
    //扫过的角度
    private float mSweepAngle = 270;


    private int[] colors = {Color.parseColor("#8de864"), Color.parseColor("#dedf53"), Color.parseColor("#fed012"), Color.parseColor("#ffa000"), Color.parseColor("#f6633b"), Color.parseColor("#d32795"), Color.parseColor("#a21482")};
    private float[] position = {0f, 0.125f, 0.25f, 0.375f, 0.5f, 0.625f, 0.75f};
    private List<Integer> mColors = new ArrayList<>();
    private List<Float> mPostions = new ArrayList<>();
    //实际滑动的角度
    private float sweepAngle;
    //处在第几个区间
    private int atWhichSection;


    public PanelView(Context context) {
        this(context, null);
    }

    public PanelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PanelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        initPaint();

    }

    private void initPaint() {
        mTikeWidth = dpToPx(6, mContext);
        mAqiNumTextSize = dpToPx(40, mContext);
        mAqiStatusTextSize = dpToPx(17, mContext);
        mAqiSuggestTextSize = dpToPx(12, mContext);

        mTextWidth = dpToPx(18, mContext);
        mTextPadding = dpToPx(2, mContext);
        mBigCircleWidth = dpToPx(6.5f, mContext);
        mSmallCircleWidth = dpToPx(1.5f, mContext);


        mBigRect = new RectF();

        paintCircleAndTike = new Paint();
        paintCircleAndTike.setStrokeWidth(mSmallCircleWidth);
        paintCircleAndTike.setAntiAlias(true);
        paintCircleAndTike.setStyle(Paint.Style.STROKE);
        paintCircleAndTike.setColor(mArcColor);


        paintTikeText = new Paint();
        paintTikeText.setAntiAlias(true);
        paintTikeText.setColor(mArcColor);
        paintTikeText.setTextSize(dpToPx(10, mContext));
        paintTikeText.setTextAlign(Paint.Align.CENTER);
        paintTikeText.setStyle(Paint.Style.FILL);

        paintProgressBackground = new Paint();
        paintProgressBackground.setAntiAlias(true);
        paintProgressBackground.setStrokeWidth(mBigCircleWidth);
        paintProgressBackground.setStyle(Paint.Style.STROKE);
        paintProgressBackground.setStrokeCap(Paint.Cap.ROUND);
        paintProgressBackground.setColor(Color.TRANSPARENT);
        paintProgressBackground.setDither(true);


        paintProgress = new Paint();
        paintProgress.setAntiAlias(true);
        paintProgress.setStrokeWidth(mBigCircleWidth);
        paintProgress.setStyle(Paint.Style.STROKE);
        //paintProgress.setStrokeCap(Paint.Cap.ROUND);
        paintProgress.setDither(true);

        paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setColor(mArcColor);
        paintText.setStrokeWidth(1);
        paintText.setStyle(Paint.Style.FILL);//实心画笔
        paintText.setDither(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            mWidth = dpToPx(133, mContext) + (int) (mTextPadding + mTextWidth)*2;
        }


        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = dpToPx(133, mContext) + (int) (mTextPadding + mTextWidth)*2;
        }
        setMeasuredDimension(mWidth, dpToPx(150, mContext));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBigRect.set(mBigCircleWidth / 2 + mTextWidth + mTextPadding, mBigCircleWidth / 2 + mTextWidth + mTextPadding, mWidth - mBigCircleWidth / 2 - mTextWidth - mTextPadding, mHeight - mBigCircleWidth / 2 - mTextWidth - mTextPadding);

        //最外面线条
        canvas.drawArc(mBigRect, mStartAngle, mSweepAngle, false, paintCircleAndTike);

        //绘制刻度线
        drawTike(canvas);


        //绘制粗圆弧
        drawStripe(canvas);

        //绘制空气质量数值
        drawAqiNumText(canvas);
        //绘制空气质量程度
        drawAqiStatusText(canvas);
        //绘制空气质量建议
        drawAqiSuggestText(canvas);

        //绘制刻度读数
        drawText(canvas);
    }


    /**
     * 画刻度线
     */
    private void drawTike(Canvas canvas) {
        canvas.drawLine(mWidth / 2, mBigCircleWidth / 2 + mTextWidth + mTextPadding, mWidth / 2, mBigCircleWidth / 2 + mTextWidth + mTextPadding + mTikeWidth, paintCircleAndTike);
        //通过旋转画布 绘制右面的刻度
        canvas.save(); //记录画布状态
        for (int i = 0; i < mTikeCount / 2; i++) {
            canvas.rotate(rAngle, mWidth / 2, mHeight / 2);
            canvas.drawLine(mWidth / 2, mBigCircleWidth / 2 + mTextWidth + mTextPadding, mWidth / 2, mBigCircleWidth / 2 + mTextWidth + mTextPadding + mTikeWidth, paintCircleAndTike);
        }
        canvas.restore();
        canvas.save();
        //通过旋转画布 绘制左面的刻度
        for (int i = 0; i < mTikeCount / 2; i++) {
            canvas.rotate(-rAngle, mWidth / 2, mHeight / 2);
            canvas.drawLine(mWidth / 2, mBigCircleWidth / 2 + mTextWidth + mTextPadding, mWidth / 2, mBigCircleWidth / 2 + mTextWidth + mTextPadding + mTikeWidth, paintCircleAndTike);
        }
        canvas.restore();
    }


    /**
     * 画刻度读数
     */
    private void drawText(Canvas canvas) {
        Rect mRectText = new Rect();
        for (int i = 0; i < mTexts.length; i++) {
            paintTikeText.getTextBounds(mTexts[i], 0, mTexts[i].length(), mRectText);
            float angle = i * rAngle + mStartAngle;

            float[] numberPoint = getCoordinatePoint(mWidth / 2 - (int) mTextWidth / 2, angle);
            //细微调整刻度数值相对于刻度线的位置
            if (i == 0 || i == mTikeCount - 1) {
                canvas.drawText(mTexts[i], numberPoint[0], numberPoint[1], paintTikeText);
            } else if (i == 1 || i == mTikeCount - 2) {
                canvas.drawText(mTexts[i], numberPoint[0], numberPoint[1] + (mRectText.height() / 2), paintTikeText);
            } else if (i==2){
                canvas.drawText(mTexts[i], numberPoint[0]-mRectText.width()/3, numberPoint[1] + mRectText.height(), paintTikeText);
            }else if (i==4){
                canvas.drawText(mTexts[i], numberPoint[0]+mRectText.width()/3, numberPoint[1] + mRectText.height(), paintTikeText);
            }else {
                canvas.drawText(mTexts[i], numberPoint[0], numberPoint[1] + mRectText.height(), paintTikeText);
            }

        }
    }


    /**
     * 依圆心坐标，半径，扇形角度，计算出扇形终射线与圆弧交叉点的xy坐标
     */
    public float[] getCoordinatePoint(int radius, float cirAngle) {
        float mCenterX = mWidth / 2;
        float mCenterY = mHeight / 2;
        float[] point = new float[2];

        double arcAngle = Math.toRadians(cirAngle); //将角度转换为弧度
        if (cirAngle < 90) {
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (cirAngle == 90) {
            point[0] = mCenterX;
            point[1] = mCenterY + radius;
        } else if (cirAngle > 90 && cirAngle < 180) {
            arcAngle = Math.PI * (180 - cirAngle) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (cirAngle == 180) {
            point[0] = mCenterX - radius;
            point[1] = mCenterY;
        } else if (cirAngle > 180 && cirAngle < 270) {
            arcAngle = Math.PI * (cirAngle - 180) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        } else if (cirAngle == 270) {
            point[0] = mCenterX;
            point[1] = mCenterY - radius;
        } else {
            arcAngle = Math.PI * (360 - cirAngle) / 180.0;
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        }

        return point;
    }

    /**
     * 整体绘制色带
     *
     * @param canvas
     */
    private void drawStripe(Canvas canvas) {
        paintProgress.setStrokeCap(Paint.Cap.ROUND);
        paintProgress.setStrokeWidth(mBigCircleWidth);
        int[] c = new int[mColors.size() + 1];
        float[] p = new float[mPostions.size() + 1];
        for (int i = 0; i < mColors.size(); i++) {
            c[i] = mColors.get(i);
            p[i] = mPostions.get(i);
        }
        //由于round的原因加上最后一个位置，该位置颜色和起点颜色一致。
        c[mColors.size()] = c[0];
        p[mColors.size()] = 1.0f;

        paintProgress.setShader(new SweepGradient(mWidth / 2, mHeight / 2, c, p));
        canvas.save();
        canvas.rotate(mStartAngle, mWidth / 2, mHeight / 2);
        if (sweepAngle>5f){
            //端点对齐刻度
            canvas.drawArc(mBigRect, 2.5f, sweepAngle-5f, false, paintProgress);
        }else if (sweepAngle>2.7f){
            canvas.drawArc(mBigRect, 2.5f, -0.1f, false, paintProgress);
        }else{
            paintProgress.setStrokeCap(Paint.Cap.BUTT);
            paintProgress.setStrokeWidth(dpToPx(3.25f,mContext));
            canvas.drawArc(mBigRect, 0f, sweepAngle, false, paintProgress);
        }

        canvas.restore();
    }


    private void drawAqiNumText(Canvas canvas) {
        float length;
        paintText.setTextSize(mAqiNumTextSize);
        paintText.setStrokeWidth(1);

        paintText.setColor(mAqiNumTextColor);
        length = paintText.measureText(mAqiNumText + "");
        canvas.drawText(mAqiNumText + "", mWidth / 2 - length / 2, mHeight / 2 + dpToPx(5, mContext), paintText);
    }

    private void drawAqiStatusText(Canvas canvas) {
        float length;
        paintText.setTextSize(mAqiStatusTextSize);
        paintText.setColor(mAqiStatusTextColor);
        length = paintText.measureText(mAqiStatusText);
        canvas.drawText(mAqiStatusText, mWidth / 2 - length / 2, mHeight / 2 + dpToPx(28, mContext), paintText);
    }


    private void drawAqiSuggestText(Canvas canvas) {
        float length;
        paintText.setTextSize(mAqiSuggestTextSize);
        paintText.setColor(mAqiSuggestTextColor);
        length = paintText.measureText(mAqiSuggestText);
        canvas.drawText(mAqiSuggestText, mWidth / 2 - length / 2, mHeight * 5.7f / 7, paintText);
    }


    public void setAqiNumText(int mAqiNumText) {
        this.mAqiNumText = mAqiNumText;
        setSweepAngle(mAqiNumText);
        invalidate();
    }


    /**
     * 设置终点在第几个区间,计算出要转过的角度,设置不同状态要显示的空气质量text。
     *
     * @param value
     */
    private void setSweepAngle(int value) {
        if (value <= 50) {
            atWhichSection = 1;
            sweepAngle = rAngle * value / 50;
            mAqiStatusText = "空气优";
            mAqiSuggestText = "";

        } else if (value <= 100) {
            atWhichSection = 2;
            sweepAngle = rAngle + rAngle * (value - 50) / 50;
            mAqiStatusText = "空气良";
            mAqiSuggestText = "";
        } else if (value <= 150) {
            atWhichSection = 3;
            sweepAngle = rAngle * 2 + rAngle * (value - 100) / 50;
            mAqiStatusText = "轻度污染";
            mAqiSuggestText = "建议戴口罩";
        } else if (value <= 200) {
            atWhichSection = 4;
            sweepAngle = rAngle * 3 + rAngle * (value - 150) / 50;
            mAqiStatusText = "中度污染";
            mAqiSuggestText = "需戴口罩";
        } else if (value <= 300) {
            atWhichSection = 5;
            sweepAngle = rAngle * 4 + rAngle * (value - 200) / 100;
            mAqiStatusText = "重度污染";
            mAqiSuggestText = "需戴口罩";
        } else if (value <= 500) {
            atWhichSection = 6;
            sweepAngle = rAngle * 5 + rAngle * (value - 300) / 200;
            mAqiStatusText = "严重污染";
            mAqiSuggestText = "必须戴口罩";
        } else {
            atWhichSection = 6;
            sweepAngle = rAngle * 6;
            mAqiStatusText = "污染爆表";
            mAqiSuggestText = "必须戴口罩";
        }
        for (int i = 0; i <= atWhichSection; i++) {
            mColors.add(i, colors[i]);
            mPostions.add(i, position[i]);
        }

    }


    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }


}
