package com.cloud.library.lightningview;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class RoundImageView extends ImageView {


    //圆角大小，默认为10
    private int mBorderRadius = 20;

    private Paint mPaint, mLightPaint, mLinePaint,mStarPaint;

    // 3x3 矩阵，主要用于缩小放大
    private Matrix mMatrix;

    //渲染图像，使用图像为绘制图形着色
    private BitmapShader mBitmapShader;

    private Bitmap starBitmap;

    //矩形
    private RectF rect;
    private int mViewWidth = 0, mViewHeight = 0;
    private float mTranslateX = 0, mTranslateY = 0;
    private float mLStartx = 30, mLStarty = 20, mLStopx = 50, mLStopy = 20;
    private ValueAnimator valueAnimator, valueAnimator2;
    private boolean autoRun = true; //是否自动运行动画
    private Shader mGradient;
    private Matrix mGradientMatrix;
    private int a = 0;
    private boolean mAnimating = false;

    private Matrix matrix;


    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mMatrix = new Matrix();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        init();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }

        Bitmap bitmap = drawableToBitamp(getDrawable());
        mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = 1.0f;
        if (!(bitmap.getWidth() == getWidth() && bitmap.getHeight() == getHeight())) {
            // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
            scale = Math.max(getWidth() * 1.0f / bitmap.getWidth(),
                    getHeight() * 1.0f / bitmap.getHeight());
        }
        // shader的变换矩阵，我们这里主要用于放大或者缩小
        mMatrix.setScale(scale, scale);
        // 设置变换矩阵
        mBitmapShader.setLocalMatrix(mMatrix);
        // 设置shader
        mPaint.setShader(mBitmapShader);
        canvas.drawRoundRect(new RectF(29, 29, getWidth()-29, getHeight()-29), mBorderRadius, mBorderRadius,
                mPaint);
        canvas.drawRoundRect(rect, mBorderRadius, mBorderRadius,mLightPaint);

        starBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.star);


        //mStarPaint.setColor(Color.parseColor("#FF4081"));
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        int size2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42, getResources().getDisplayMetrics());


        //矩阵

        matrix.setTranslate(getWidth() - 75, 10);
        mStarPaint.setAlpha(a);
        canvas.drawBitmap(starBitmap, matrix, mStarPaint);
        canvas.drawLine(30, mLStarty, getWidth()-43, mLStopy, mLinePaint);

    }

    private Bitmap drawableToBitamp(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        // 当设置不为图片，为颜色时，获取的drawable宽高会有问题，所有当为颜色时候获取控件的宽高
        int w = drawable.getIntrinsicWidth() <= 0 ? getWidth() : drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight() <= 0 ? getHeight() : drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mViewWidth == 0) {
            mViewWidth = getWidth();
            mViewHeight = getHeight();

            mLStartx = 30;
            mLStarty = 25;

            mLStopx = 50;
            mLStopy = 25;


        }

        if (mViewWidth > 0) {

            mGradient = new LinearGradient(0, 0, mViewWidth / 2, mViewHeight,
                    new int[]{0x00ffffff, 0x40ffffff, 0x00ffffff},
                    new float[]{0.2f, 0.90f, 0.91f},
                    Shader.TileMode.CLAMP);

            mLightPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
            mGradientMatrix = new Matrix();
            mGradientMatrix.setTranslate(0, 0);

            //mGradientMatrix.setTranslate(-2 * mViewWidth, mViewHeight);
            mGradient.setLocalMatrix(mGradientMatrix);
            mLightPaint.setShader(mGradient);

            rect.set(29, 29, w - 29, h - 29);

            mLinePaint.setShader(mGradient);

        }
    }

    private void init() {
        rect = new RectF();
        mLightPaint = new Paint();
        mLinePaint = new Paint();
        mLinePaint.setStrokeWidth(8);
        mStarPaint = new Paint();
        matrix = new Matrix();
        initGradientAnimator();
    }


    private void initGradientAnimator() {
        valueAnimator2 = ValueAnimator.ofFloat(0, 1);
        valueAnimator2.setDuration(1000);
        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float v = (Float) valueAnimator.getAnimatedValue();

                if (v <= 0.5) {
                    a = (int) (v * 200);
                } else if (v > 0.5 && v < 0.8) {

                    a = (int) (200 - v * 200);
                } else {

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
                    } else if (0.35 <= v) {

                        mLStartx = 30;
                        mLStopx = 30;
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
                        //valueAnimator2.start();
                    }
                }
            });
        }
    }
}
