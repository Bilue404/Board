package com.bilue.board.graph;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class GraphIF {
	protected Paint mPaint;
	//画笔类型
	public static final int penTool = 1;
	public static final int circlectTool = 2;
	public static final int lineTool = 3;
	public static final int rectuTool = 4;
	public static final int eraserTool = 5;

	public GraphIF(){
		mPaint = new Paint();
	}

	public abstract void draw(Canvas canvas);
	public abstract void touchDown(float x, float y);
	public abstract void touchMove(float x, float y);
	public abstract void touchUp(float x, float y);
	public abstract String getTAG();
	
	public abstract int getDrawPenStyle();


}

