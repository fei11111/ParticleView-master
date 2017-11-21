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

    public Particle(Matrix mMatrix, Paint mPaint, int startX, int startY, Bitmap particleBitmap, int vWidth, int vHeight) {
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
    }

    public void drawItem(Canvas canvas) {
        mMatrix.reset();
        startX = startX + getDistValue(isAddX, distX);
        startY = startY + getDistValue(isAddY, distY);
        centerX = (startX + particleWidth) / 2;
        centerY = (startY + particleHeight) / 2;
        if (startX <= 0 || startX + particleWidth >= vWidth) {
            isAddX = !isAddX;
            if (startX <= 0) {
                startX = 0;
                centerX = particleWidth / 2;
            } else {
                startX = vWidth - particleWidth;
                centerX = vWidth - particleWidth / 2;
            }
        }
        if (startY <= 0 || startY + particleHeight >= vHeight) {
            isAddY = !isAddY;
            if(startY<=0) {
                startY = 0;
                centerY = particleHeight / 2;
            }else {
                startY = vHeight - particleHeight;
                centerY = vHeight - particleHeight / 2;
            }
        }
        mMatrix.preRotate(mDegree, centerX, centerY);
        mMatrix.preTranslate(startX, startY);
        Bitmap bm = Bitmap.createBitmap(particleBitmap, 0, 0, particleWidth,
                particleHeight, mMatrix, true);
        canvas.drawBitmap(bm,startX,startY,null);
        randomValue();
    }

    private void randomValue() {
        mDegree += random.nextInt(5) + 3;
        distX = random.nextInt(2) + 2;
        distY = random.nextInt(2) + 2;
    }

    private int getDistValue(boolean isAdd, int value) {
        return isAdd ? value : (0 - value);
    }

}
