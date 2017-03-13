package com.bilue.board.graph;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class LineImpl extends GraphIF {

	private String TAG = null;
	private float startx = 0;
	private float starty = 0;
	private float endx = 0;
	private float endy = 0;
	private int drawPenStyle = 3;
	private Path triangle = new Path(); 
	public LineImpl(float penSize, int penColor) {
		super();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(penColor);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(penSize);
		this.TAG = 3 + penSize + penColor + "";
		
		
		  
	        
	}

	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		if (null != canvas) {
			canvas.drawLine(startx, starty, endx, endy, mPaint);
			
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
