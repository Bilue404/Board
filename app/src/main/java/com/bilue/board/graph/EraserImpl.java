package com.bilue.board.graph;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;


public class EraserImpl implements GraphIF {
	
	private String TAG = null;
	private Path mpath = new Path();
	private Paint mpaint = new Paint();
	private float startX =0;
	private float startY =0;
	
	private int drawPenStyle = 5;
	
	
	public EraserImpl(int penSize) {
		mpaint.setStyle(Paint.Style.STROKE);
		mpaint.setStrokeWidth(penSize);
		mpaint.setColor(Color.WHITE);
		this.TAG = 5+penSize+Color.WHITE+"";
	}
	
	@Override
	public void draw(Canvas canvas) {
		if(canvas != null){
			canvas.drawPath(mpath, mpaint);
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
