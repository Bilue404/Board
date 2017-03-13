package com.bilue.board.util;

import java.io.Serializable;

public class DrawAction implements Serializable {
	public static final String ACTION_DOWM = "ACTION_DOWN";
	public static final String ACTION_MOVE = "ACTION_MOVE";
	public static final String ACTION_UP = "ACTION_UP";
	private String drawPenTAG;
	private String action;
	private float x;
	private float y; 
	private int drawPenStyle;
	private float paintSize;
	private int paintColor;
	private String paintText;
//	
//	public DrawAction(String drawPenTAG,String action,float x,float y) {
//		this.drawPenTAG = drawPenTAG;
//		this.action = action;
//		this.x = x;
//		this.y = y;
//	}
	
	
	
	
	public DrawAction(String drawPenTAG, String action, float x, float y,
			int drawPenStyle, float paintSize, int paintColor,String paintText) {
		
		this.drawPenTAG = drawPenTAG;
		this.action = action;
		this.x = x;
		this.y = y;
		this.drawPenStyle = drawPenStyle;
		this.paintSize = paintSize;
		this.paintColor = paintColor;
		this.paintText = paintText;
	}




	public String getdrawPenTAG() {
		return drawPenTAG;
	}
	public String getAction() {
		return action;
	}
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}

	
	public int getDrawPenStyle() {
		return drawPenStyle;
	}
	public float getPaintSize() {
		return paintSize;
	}
	public int getPaintColor() {
		return paintColor;
	}

	public String getPaintText(){
		return paintText;
	}
	
}
