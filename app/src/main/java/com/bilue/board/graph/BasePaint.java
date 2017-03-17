package com.bilue.board.graph;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class BasePaint {
	protected Paint mPaint;

	public BasePaint(){
		mPaint = new Paint();
	}

	public abstract void draw(Canvas canvas);
	public abstract void touchDown(float x, float y);
	public abstract void touchMove(float x, float y);
	public abstract void touchUp(float x, float y);
	public abstract String getTAG();
	
	public abstract int getDrawPenStyle();


}

