package com.bilue.board.graph;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;



import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by Administrator on 2015/8/12.
 */
public class TextImpl implements GraphIF{
    private String TAG = null;
    private Paint mPaint = new Paint();
    private float startx = 0;
    private float starty = 0;
    private float endx = 100;
    private float endy = 100;
    private String text;
    private int drawPenStyle = 7;



    public TextImpl(int penSize, int penColor,String text) {
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(penColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        //mPaint.setStrokeWidth(penSize);
        mPaint.setTextSize(penSize*2);
        this.text = text;
        this.TAG = 7 + penSize + penColor + "";

    }

    public void draw(Canvas canvas) {
        // TODO Auto-generated method stub
        if (null != canvas) {
            canvas.drawText(text,endx,endy,mPaint);
        }
    }

    public void touchDown(float x, float y) {
        // TODO Auto-generated method stub
        startx = x;
        starty = y;
        endx = x;
        endy = y;
    }

    public void touchMove(float x, float y) {
        // TODO Auto-generated method stub
        endx = x;
        endy = y;
    }

    public void touchUp(float x, float y) {
        endx = x;
        endy = y;
    }

    @Override
    public String getTAG() {
        // TODO Auto-generated method stub
        return TAG;
    }

    @Override
    public int getDrawPenStyle() {
        // TODO Auto-generated method stub
        return drawPenStyle;
    }



}
