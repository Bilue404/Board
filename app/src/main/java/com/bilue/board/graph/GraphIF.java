package com.bilue.board.graph;

import android.graphics.Canvas;

public interface GraphIF {
	//画笔类型
	public static final int penTool = 1;
	public static final int circlectTool = 2;
	public static final int lineTool = 3;
	public static final int rectuTool = 4;
	public static final int eraserTool = 5;

	public void draw(Canvas canvas);
	public void touchDown(float x, float y);
	public void touchMove(float x, float y);
	public void touchUp(float x, float y);
	public String getTAG();
	
	public int getDrawPenStyle();
}

