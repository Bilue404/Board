package com.bilue.board.graph;

import android.graphics.Canvas;
import android.graphics.Paint;

public class CirclectlImpl extends GraphIF {
	private String TAG = null;
    private boolean m_hasDrawn = false;
    private float startx = 0;  
    private float starty = 0;  
    private float endx = 0;  
    private float endy = 0; 
    private float radius=0;   
    private int drawPenStyle = 2;
    public CirclectlImpl(int penSize, int penColor)
    {
		super();
    	mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(penColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(penSize);
        this.TAG = 2+penSize+penColor+"";
    }	
	@Override
	public void draw(Canvas canvas) {
		if (null != canvas)
        {
			canvas.drawCircle((startx+endx)/2, (starty+endy)/2, radius, mPaint);
        }
		
	}

	@Override
	public void touchDown(float x, float y) {
		startx=x;
		starty=y;
		endx=x;
		endy=y;
		radius=0;		
	}

	@Override
	public void touchMove(float x, float y) {
		endx=x;
		endy=y;
		radius=(float) ((Math.sqrt((x-startx)*(x-startx)+(y-starty)*(y-starty)))/2);
		m_hasDrawn=true;
		
	}

	@Override
	public void touchUp(float x, float y) {
		endx=x;
		endy=y;		
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
