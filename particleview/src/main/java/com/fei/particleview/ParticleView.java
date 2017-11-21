package com.fei.particleview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2017/11/20.
 */

public class ParticleView extends View {

    //绘制类型
    public enum ParticleStyle {
        CIRCLE, RECT, IMGE;
    }

    private final int DEFAULT_TRASLATE = 20;//默认位移值
    private final int DEFAULT_ROTATE = 10;//默认旋转值
    private final int DEFAULT_NUM = 25;//默认个数
    private final int DEFAULT_CIRCLE_RADIUS = 5;//默认圆半径
    private final int DEFAULT_RECT_WIDTH = 40;//默认方形宽度
    private final int DEFAULT_RECT_HEIGHT = 30;//默认方形高度

    private int particleStyle;//类型
    private Paint mPaint;
    private int vWidth;//当前view宽度
    private int vHeight;//当前view高度
    private Bitmap particleBitmap;
    private int particlesNum = 30;
    private ValueAnimator valueAnimator;
    private Bitmap backBitmap;//背景
    private List<Particle> particles = new ArrayList<>();
    private Matrix mMatrix;
    private Random random = new Random();
    private int traslate;//位移距离
    private int rotate;//旋转角度
    private int circleRadius;
    private int rectWidth;
    private int rectHeight;
    private Bitmap rectBitmap;

    public ParticleView(Context context) {
        this(context, null);
    }

    public ParticleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ParticleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ParticleView);
        particleStyle = typedArray.getInt(R.styleable.ParticleView_style, ParticleStyle.CIRCLE.ordinal());
        int resourceId = typedArray.getResourceId(R.styleable.ParticleView_src, R.drawable.fire_star);
        particlesNum = typedArray.getInt(R.styleable.ParticleView_num, DEFAULT_NUM);
        traslate = typedArray.getInt(R.styleable.ParticleView_traslate, DEFAULT_TRASLATE);
        rotate = typedArray.getInt(R.styleable.ParticleView_rotate, DEFAULT_ROTATE);
        circleRadius = typedArray.getInt(R.styleable.ParticleView_circleRadius, DEFAULT_CIRCLE_RADIUS);
        rectWidth = typedArray.getInt(R.styleable.ParticleView_rectWidth, DEFAULT_RECT_WIDTH);
        rectHeight = typedArray.getInt(R.styleable.ParticleView_rectHeight, DEFAULT_RECT_HEIGHT);
        BitmapDrawable drawable = (BitmapDrawable) typedArray.getDrawable(R.styleable.ParticleView_background);
        if (drawable != null) {
            backBitmap = drawable.getBitmap();
        } else {
            backBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ico_background);
        }
        typedArray.recycle();
        rectBitmap = Bitmap.createBitmap(rectWidth,rectHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(rectBitmap);
        canvas.drawColor(Color.WHITE);
        mMatrix = new Matrix();
        particleBitmap = getThumbnail(context, resourceId);
        initPaint();
        initAnimator();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setDither(true);
        // 设置外围模糊效果
        mPaint.setMaskFilter(new BlurMaskFilter(5.0f, BlurMaskFilter.Blur.SOLID));
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    private void initAnimator() {
        valueAnimator = ValueAnimator.ofFloat(0);
        valueAnimator.setDuration(10).setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                Log.i("tag", value + "");
                invalidate();
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (vWidth == 0) {
            vWidth = getMeasuredWidth();
            vHeight = getMeasuredHeight();
        }
        if (particles.size() == 0) {
            for (int i = 1; i <= particlesNum; i++) {
                int x = (int) (getF() * vWidth);
                int y = (int) (getF() * vHeight);
                Particle particle = new Particle(particleStyle, mMatrix, mPaint, x, y, particleBitmap, vWidth, vHeight, rotate, traslate, circleRadius,rectBitmap);
                particles.add(particle);
            }
        }
        if (!valueAnimator.isRunning()) {
            valueAnimator.start();
        }
    }

    private float getF() {
        float v = random.nextFloat();
        if (v < 0.15f) {
            return v + 0.15f;
        } else if (v >= 0.9f) {
            return v - 0.15f;
        } else {
            return v;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(backBitmap, 0, 0, null);
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).drawItem(canvas);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.end();
            valueAnimator = null;
        }
        particleBitmap = null;
        backBitmap = null;
    }

    /**
     * 获取缩略图
     */
    private Bitmap getThumbnail(Context context, int resourceId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeResource(getResources(), resourceId, options), dip2px(context, 30),
                dip2px(context, 30), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void stop() {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
    }
}
