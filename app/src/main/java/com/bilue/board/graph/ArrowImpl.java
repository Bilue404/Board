package com.bilue.board.graph;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by Administrator on 2015/8/12.
 */
public class ArrowImpl extends GraphIF{
    private String TAG = null;
    private float startx = 0;
    private float starty = 0;
    private float endx = 0;
    private float endy = 0;
    private int drawPenStyle = 6;

    private double H = 20; // 箭头高度
    private double L = 8; // 底边的一半


    private int x3 = 50;
    private int y3 = 50;
    private int x4 = 50;
    private int y4 = 50;
    //private Path triangle = new Path();

    public ArrowImpl(int penSize, int penColor) {
        super();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(penColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(8);
        this.TAG = 6 + penSize + penColor + "";




    }

    public void draw(Canvas canvas) {
        if (null != canvas) {
            //canvas.drawLine(startx, starty, endx, endy, mPaint);


            double awrad = Math.atan(L / H); // 箭头角度
            double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度
            double[] arrXY_1 = rotateVec(endx - startx, endy - starty, awrad, true, arraow_len);
            double[] arrXY_2 = rotateVec(endx - startx, endy - starty, -awrad, true, arraow_len);
            double x_3 = endx - arrXY_1[0]; // (x3,y3)是第一端点
            double y_3 = endy - arrXY_1[1];
            double x_4 = endx - arrXY_2[0]; // (x4,y4)是第二端点
            double y_4 = endy - arrXY_2[1];
            Double X3 = new Double(x_3);
            x3 = X3.intValue();
            Double Y3 = new Double(y_3);
            y3 = Y3.intValue();
            Double X4 = new Double(x_4);
            x4 = X4.intValue();
            Double Y4 = new Double(y_4);
            y4 = Y4.intValue();
            // 画线
            canvas.drawLine(startx, starty, endx, endy, mPaint);
            Path triangle = new Path();
            triangle.moveTo(endx, endy);
            triangle.lineTo(x3, y3);
            triangle.lineTo(x4, y4);
            triangle.close();
            canvas.drawPath(triangle,mPaint);

        }
    }

    public void touchDown(float x, float y) {
        startx = x;
        starty = y;
        endx = x+10;
        endy = y+10;
    }

    public void touchMove(float x, float y) {
        endx = x;
        endy = y;
    }

    public void touchUp(float x, float y) {
        endx = x;
        endy = y;
    }

    @Override
    public String getTAG() {
        return TAG;
    }

    @Override
    public int getDrawPenStyle() {
        return drawPenStyle;
    }


    // 计算
    public double[] rotateVec(double px, double py, double ang, boolean isChLen, double newLen)
    {
        double mathstr[] = new double[2];
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }
}
