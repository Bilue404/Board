package com.bilue.board.graph;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class PenImpl extends GraphIF{

	private String TAG = null;
	private Path mpath = new Path();
	private float startX =0;
	private float startY =0;
	
	private int drawPenStyle = 1;
	
	
	public PenImpl(int penSize, int penColor) {
		super();
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(penSize);
		mPaint.setColor(penColor);
		this.TAG = 1+penSize+penColor+"";
	}
	
	@Override
	public void draw(Canvas canvas) {
		if(canvas != null){
			canvas.drawPath(mpath, mPaint);
		}
	}

	@Override
	public void touchDown(float x, float y) {
		mpath.moveTo(x, y);
		startX = x;
		startY = y;
	}

	@Override
	public void touchMove(float x, float y) {
		mpath.quadTo(startX, startY, x, y);
		startX = x;
		startY = y;
	}

	@Override
	public void touchUp(float x, float y) {
		mpath.lineTo(x, y);
	}

	@Override
	public String getTAG() {
		// TODO Auto-generated method stub
		return this.TAG;
	}

	@Override
	public int getDrawPenStyle() {
		// TODO Auto-generated method stub
		return drawPenStyle;
	}

	
}
