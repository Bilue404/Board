package com.bilue.board.util;

import java.io.Serializable;

public class DrawAction implements Serializable {
	private String drawPenTAG;
	private String action;
	private float x;
	private float y; 
	private int drawPenStyle;
	private int paintSize;
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
			int drawPenStyle, int paintSize, int paintColor,String paintText) {
		
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
	public int getPaintSize() {
		return paintSize;
	}
	public int getPaintColor() {
		return paintColor;
	}

	public String getPaintText(){
		return paintText;
	}
	
}
