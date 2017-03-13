package com.bilue.board.graph;

import android.graphics.Canvas;
import android.graphics.Paint;

public class RectuImpl extends GraphIF {
	private String TAG = null;

	private float startx = 0;
	private float starty = 0;
	private float endx = 0;
	private float endy = 0;

	private int drawPenStyle = 4;

	public RectuImpl(float penSize, int penColor) {
		super();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(penColor);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(penSize);//
		this.TAG = 4 + penSize + penColor + "";
	}

	public void draw(Canvas canvas) {
		if (null != canvas) {
			canvas.drawRect(startx, starty, endx, endy, mPaint);
		}
	}

	public void touchDown(float x, float y) {
		startx = x;
		starty = y;
		endx = x;
		endy = y;
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

}
