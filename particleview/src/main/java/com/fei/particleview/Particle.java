package com.fei.particleview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.util.Random;

/**
 * Created by Administrator on 2017/11/20.
 */

public class Particle {

    private Matrix mMatrix;
    private Paint mPaint;
    private int startX;//开始位置
    private int startY;
    private int distX;//位移位置
    private int distY;
    private int centerX;//中心位置
    private int centerY;
    private int mDegree;//角度
    private Bitmap particleBitmap;
    private boolean isAddX;
    private boolean isAddY;
    private int particleWidth;
    private int particleHeight;
    private int vWidth;
    private int vHeight;
    private Random random = new Random();
    private int rotate;
    private int traslate;
    private int particleStyle;
    private int circleRadius;//圆形
    private int rectWidth;//方形宽
    private int rectHeight;//方形高
    private Bitmap rectBitmap;

    public Particle(int particleStyle, Matrix mMatrix, Paint mPaint, int startX, int startY, Bitmap particleBitmap, int vWidth, int vHeight, int rotate, int traslate, int circleRadius, Bitmap rectBitmap) {
        this.particleStyle = particleStyle;
        this.mMatrix = mMatrix;
        this.mPaint = mPaint;
        this.startX = startX;
        this.startY = startY;
        this.particleBitmap = particleBitmap;
        this.vWidth = vWidth;
        this.vHeight = vHeight;
        particleWidth = particleBitmap.getWidth();
        particleHeight = particleBitmap.getHeight();
        isAddX = false;
        isAddY = false;
        centerX = (startX + particleWidth) / 2;
        centerY = (startY + particleHeight) / 2;
        mDegree = random.nextInt(20);
        isAddX = random.nextBoolean();
        isAddY = random.nextBoolean();
        this.rotate = rotate;
        this.traslate = traslate;
        this.circleRadius = circleRadius;
        this.rectBitmap = rectBitmap;
        if (rectBitmap != null) {
            rectWidth = rectBitmap.getWidth();
            rectHeight = rectBitmap.getHeight();
        }
    }

    public void drawItem(Canvas canvas) {
        startX = startX + getDistValue(isAddX, distX);
        startY = startY + getDistValue(isAddY, distY);
        centerX = (startX + particleWidth) / 2;
        centerY = (startY + particleHeight) / 2;
        if (particleStyle == ParticleView.ParticleStyle.CIRCLE.ordinal()) {
            drawCircle(canvas);
        } else if (particleStyle == ParticleView.ParticleStyle.RECT.ordinal()) {
            drawRect(canvas);
        } else if (particleStyle == ParticleView.ParticleStyle.IMGE.ordinal()) {
            drawImage(canvas);
        }
        randomValue();
    }

    //圆形只有位移，没有旋转
    private void drawCircle(Canvas canvas) {
        if (centerX - circleRadius < 0 || centerX + circleRadius > vWidth) {
            isAddX = !isAddX;//需要转变
            if (centerX - circleRadius < 0) {
                centerX = circleRadius;
            } else {
                centerX = vWidth - circleRadius;
            }
        }
        if (centerY - circleRadius < 0 || centerY + circleRadius > vHeight) {
            isAddY = !isAddY;//需要转变
            if (centerY - circleRadius < 0) {
                centerY = circleRadius;
            } else {
                centerY = vHeight - circleRadius;
            }
        }
        canvas.drawCircle(centerX, centerY, circleRadius, mPaint);
    }

    //方形
    private void drawRect(Canvas canvas) {
        if (startX < 0 || startX + rectWidth > vWidth) {
            isAddX = !isAddX;
            if (startX < 0) {
                startX = 0;
                centerX = rectWidth / 2;
            } else {
                startX = vWidth - rectWidth;
                centerX = vWidth - rectWidth / 2;
            }
        }
        if (startY < 0 || startY + rectHeight > vHeight) {
            isAddY = !isAddY;
            if (startY < 0) {
                startY = 0;
                centerY = rectHeight / 2;
            } else {
                startY = vHeight - rectHeight;
                centerY = vHeight - rectHeight / 2;
            }
        }
        mMatrix.preRotate(mDegree, centerX, centerY);
        mMatrix.preTranslate(startX, startY);
        Bitmap bm = Bitmap.createBitmap(rectBitmap, 0, 0, rectWidth,
                rectHeight, mMatrix, true);
        canvas.drawBitmap(bm, startX, startY, null);
    }

    private void drawImage(Canvas canvas) {
        mMatrix.reset();
        if (startX < 0 || startX + particleWidth > vWidth) {
            isAddX = !isAddX;
            if (startX < 0) {
                startX = 0;
                centerX = particleWidth / 2;
            } else {
                startX = vWidth - particleWidth;
                centerX = vWidth - particleWidth / 2;
            }
        }
        if (startY < 0 || startY + particleHeight > vHeight) {
            isAddY = !isAddY;
            if (startY <= 0) {
                startY = 0;
                centerY = particleHeight / 2;
            } else {
                startY = vHeight - particleHeight;
                centerY = vHeight - particleHeight / 2;
            }
        }
        mMatrix.preRotate(mDegree, centerX, centerY);
        mMatrix.preTranslate(startX, startY);
        Bitmap bm = Bitmap.createBitmap(particleBitmap, 0, 0, particleWidth,
                particleHeight, mMatrix, true);
        canvas.drawBitmap(bm, startX, startY, null);
    }

    private void randomValue() {
        mDegree += random.nextInt(rotate) + 5;
        distX = random.nextInt(traslate) + 2;
        distY = random.nextInt(traslate) + 2;
    }

    private int getDistValue(boolean isAdd, int value) {
        return isAdd ? value : (0 - value);
    }

}
