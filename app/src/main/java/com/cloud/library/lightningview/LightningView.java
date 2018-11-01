package com.cloud.library.lightningview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

public class LightningView extends View {
    /**
     * 梯形距离左上角的长度
     */
    private static final int LABEL_LENGTH = 100;
    /**
     * 梯形斜边的长度
     */
    private static final int LABEL_HYPOTENUSE_LENGTH = 100;
    private Shader mGradient;
    private Matrix mGradientMatrix;
    private Matrix mGradientMatrix2;
    private Paint mPaint;
    private int mViewWidth = 0, mViewHeight = 0;
    private float mTranslateX = 0, mTranslateY = 0;
    private float mttX = 0, mttY = 0;
    private boolean mAnimating = false;
    private Rect rect;
    private ValueAnimator valueAnimator;
    private boolean autoRun = true; //是否自动运行动画
    private Paint textPaint;
    private Path pathText;
    private Paint paint;
    private Paint paintN;
    private Paint paintL;

    private float mCx = 0, mCY = 0;
    private float bj = 0;

    private float mLStartx = 30, mLStarty = 10, mLStopx = 50, mLStopy = 10;

    private Bitmap bitmap;
    private Matrix matrix;
    private int a = 0;
    private ValueAnimator valueAnimator2;


    public LightningView(Context context) {
        super(context);
        init();
        initP();
    }

    private void init() {
        rect = new Rect();
        mPaint = new Paint();
        paint = new Paint();
        paint.setColor(Color.WHITE);

        initP();
        initGradientAnimator();
    }

    private void initP() {
        pathText = new Path();
        textPaint = new Paint();
        paintL = new Paint();
        //paintL.setColor(Color.WHITE);
        paintL.setStrokeWidth(10);


        textPaint.setTextSize(50);
        textPaint.setFakeBoldText(true);
        textPaint.setColor(Color.WHITE);

    }

    private void initGradientAnimator() {
        valueAnimator2 = ValueAnimator.ofFloat(0, 1);
        valueAnimator2.setDuration(1000);
        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float v = (Float) valueAnimator.getAnimatedValue();
                //Log.i("cloud", "======a======"+v);

                if (v <= 0.5) {
                    a = (int) (v * 200);
                } else if (v > 0.5 && v < 0.8) {

                    a = (int) (200 - v * 200);
                } else {
                    //Log.i("cloud", "======asssssss======");
                    a = 0;
                }


            }
        });

        valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(5000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (Float) animation.getAnimatedValue();
                //Log.i("cloud", "======animation======" + v);
                //❶ 改变每次动画的平移x、y值，范围是[-2mViewWidth, 2mViewWidth]
                mTranslateX = 4 * mViewWidth * v - mViewWidth * 2;
                //mTranslateX = mViewWidth * v;
                mTranslateY = mViewHeight * v;
                //❷ 平移matrix, 设置平移量
                if (mGradientMatrix != null) {
                    mGradientMatrix.setTranslate(mTranslateX, mTranslateY);
                }
                //❸ 设置线性变化的matrix
                if (mGradient != null) {
                    mGradient.setLocalMatrix(mGradientMatrix);
                }

                if (v <= 0.5) {

                    if (v < 0.25) {
                        valueAnimator2.start();
                        mLStartx = (float) (getWidth() * v * 2 * 1.6 + 30);
                        mLStopx = (float) (getWidth() * v * 2 * 1.6 + 50);

                        //Log.i("cloud", "======mLStartx======" + mLStartx + "            " + getWidth());

                        //bj = 30 * v * 2;
                    } else if (0.35 <= v) {

                        mLStartx = 30;
                        mLStopx = 30;

//                        mLStartx = (float) (getWidth() * v * 2 );
//                        mLStopx = (float) (getWidth() * v * 2 +10);

                        //bj = 30 * (1 - v * 2);
                    }

                } else {
                    valueAnimator2.cancel();
                }
                //❹ 重绘
                invalidate();
            }
        });

        if (autoRun) {
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            valueAnimator2.setRepeatCount(ValueAnimator.INFINITE);

            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    mAnimating = true;
                    if (valueAnimator != null) {
                        valueAnimator.start();
                        valueAnimator2.start();

                    }
                }
            });


        }
    }

    public LightningView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        initP();
    }

    public LightningView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initP();
    }

    public void setAutoRun(boolean autoRun) {
        this.autoRun = autoRun;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i("cloud", "======onSizeChanged========");
        if (mViewWidth == 0) {
            mViewWidth = getWidth();
            mViewHeight = getHeight();

            mCx = getWidth();
            mCY = getHeight();

            mLStartx = 30;
            mLStarty = 30;

            mLStopx = 50;
            mLStopy = 30;


        }
        //0x00ffffff, 0x73ffffff, 0x00ffffff, 0x99ffffff, 0x00ffffff
        //0.2f, 0.35f, 0.45f, 0.5f, 0.8f

        //0x10ffffff, 0x45ffffff, 0x50ffffff, 0x60ffffff, 0x50ffffff
        //0.0f, 0.25f, 0.65f, 0.80f, 0.81f

        //0x10ffffff, 0x45ffffff, 0x50ffffff, 0x60ffffff, 0x50ffffff
        //0.0f, 0.25f, 0.65f, 0.80f, 0.81f


        if (mViewWidth > 0) {
            //亮光闪过
            mGradient = new LinearGradient(0, 0, mViewWidth / 2, mViewHeight,
                    new int[]{0x00ffffff, 0x40ffffff, 0x00ffffff},
                    new float[]{0.2f, 0.90f,0.91f},
                    Shader.TileMode.CLAMP);

            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
            mGradientMatrix = new Matrix();
            mGradientMatrix.setTranslate(0, 0);

            //mGradientMatrix.setTranslate(-2 * mViewWidth, mViewHeight);
            mGradient.setLocalMatrix(mGradientMatrix);
            mPaint.setShader(mGradient);

            rect.set(29, 29, w - 30, h - 29);



            paintL.setShader(mGradient);


        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mAnimating && mGradientMatrix != null) {
            canvas.drawRect(rect, mPaint);


            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.star);
            //画笔
            paintN = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintN.setColor(Color.parseColor("#FF4081"));
            paintN.setAlpha(a);
            //矩阵
            matrix = new Matrix();
            //Log.i("cloud", "======dx========" + getWidth());
            matrix.setTranslate(getWidth() - 80, 10);

            canvas.drawBitmap(bitmap, matrix, paintN);
            //canvas.drawCircle(getWidth() - 20, 10, bj, paint);
            canvas.drawLine(30, mLStarty, getWidth()-30, mLStopy, paintL);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        rect.set(0, 0, getWidth(), getHeight());
    }

    //停止动画
    public void stopAnimation() {
        if (mAnimating && valueAnimator != null) {
            mAnimating = false;
            valueAnimator.cancel();
            invalidate();
        }
    }

    //开始动画
    public void startAnimation() {
        if (!mAnimating && valueAnimator != null) {
            mAnimating = true;
            valueAnimator.start();
            //valueAnimator2.start();
        }
    }

    private void calculatePath(int measuredWidth, int measuredHeight) {

        int top = 185;
        int right = measuredWidth;

        float x1 = right - LABEL_LENGTH - LABEL_HYPOTENUSE_LENGTH;
        float x2 = right - LABEL_HYPOTENUSE_LENGTH;
        float y1 = top + LABEL_LENGTH;
        float y2 = top + LABEL_LENGTH + LABEL_HYPOTENUSE_LENGTH;

        pathText.reset();
        pathText.moveTo(x1, top);
        pathText.lineTo(right, y2);
        pathText.close();

    }





}
