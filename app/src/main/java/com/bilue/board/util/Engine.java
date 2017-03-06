package com.bilue.board.util;

import android.graphics.Color;

import java.net.Socket;

import com.bilue.board.graph.GraphIF;
import com.bilue.board.graph.PenImpl;

public class Engine {

	//socket相关
	public static boolean isClient = false;
	public static String ServerIp ="192.168.43.1" ;
	public static final int Port = 9188;
	public static final String initPasswd = "12345678";	
	public static Socket clientSocket = null; 
	public static String missName = "";

	//画笔初始化相关
	public static GraphIF drawPenTool = new PenImpl(5, Color.BLACK); //画笔类型
	public static int DRAW_PEN_STYLE = 1;
	public static int paintColor = Color.BLACK; //画笔颜色
	public static int paintSize = 5; //画笔大小
	public static String paintText = "init";
	//画笔类型
	public static final int penTool = 1;
	public static final int circlectTool = 2;
	public static final int lineTool = 3;
	public static final int rectuTool = 4;
	public static final int eraserTool = 5;
	public static final int arrowTool = 6;
	public static final int textTool = 7;
	
}
