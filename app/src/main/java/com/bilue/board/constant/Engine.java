package com.bilue.board.constant;

import android.graphics.Color;

public class Engine {

	//socket相关
//	public static boolean isClient = false;
	public static final String SERVER_IP ="192.168.43.1" ;
	public static final int Port = 9188;
	public static final String DEFAULT_PASSWD = "12345678";

	public static final int DEFAULT_COLOR = Color.BLACK;
	public static final float DEFAULT_SIZE = 5f;
	//画笔初始化相关
//	public static int DRAW_PEN_STYLE = 1;
//	public static String paintText = "init";
	//画笔类型
	public static final int PEN_TOOL = 1;
	public static final int CIRCLECT_TOOL = 2;
	public static final int LINE_TOOL = 3;
	public static final int RECTU_TOOL = 4;
	public static final int ERASER_TOOL = 5;
	public static final int ARROW_TOOL = 6;
	public static final int TEXT_TOOL = 7;
	
}
