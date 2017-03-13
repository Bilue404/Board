package com.bilue.board.socket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import com.bilue.board.activity.Board;
import com.bilue.board.controller.ZoomController;
import com.bilue.board.graph.ArrowImpl;
import com.bilue.board.graph.CirclectlImpl;
import com.bilue.board.graph.EraserImpl;
import com.bilue.board.graph.GraphIF;
import com.bilue.board.graph.LineImpl;
import com.bilue.board.graph.PenImpl;
import com.bilue.board.graph.RectuImpl;
import com.bilue.board.graph.TextImpl;
import com.bilue.board.util.BitmapUtil;
import com.bilue.board.util.ControlStack;
import com.bilue.board.util.DrawAction;
import com.bilue.board.util.Engine;
import com.bilue.board.util.GraphStack;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerSock {

	private Canvas canvas = null;
	private View view;
	private Bitmap baseBitmap = null;
	private Paint paint;
	private Thread openServerThread = null;
	private Thread serverSendThread = null;
	private ArrayList<Socket> userList;
	private ControlStack myStack;
	private ServerSocket serverSocker;
	private GraphStack graphStack;

	private float hScale = ZoomController.getHeightScale();
	private float wScale = ZoomController.getWidthScale();
	// private GraphIF drawPenTool = new PenImpl(5,Color.BLACK);
	public ServerSock() {
	}

	public ServerSock(View view) {
		this.view = view;
		userList = new ArrayList<Socket>();
		initPaint();

		myStack = new ControlStack();
		graphStack = new GraphStack();
		initBitMap();
		openSever();

	}

	public ServerSock(View view,String path) {


		String myDir = path + "/";

		File mfile = new File(myDir);

		if(!mfile.exists()){
			return;
		}


		File[] files = mfile.listFiles();

		ArrayList<Bitmap> items = new ArrayList<>();

		if(files.length>0){
			for (int i = 0; i < files.length; i++) {
				Bitmap bitmap = BitmapFactory.decodeFile(files[i].getAbsolutePath()).copy(Bitmap.Config.ARGB_8888,true);
				items.add(bitmap);
			}
		}

		graphStack = new GraphStack(items);

		this.view = view;
		userList = new ArrayList<Socket>();
		initPaint();

		myStack = new ControlStack();

		initBitMap(files[files.length-1].getAbsolutePath());
		openSever();

	}

	// 开启服务
	public void openSever() {

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());


		try {


			serverSocker = new ServerSocket(Engine.Port);

			//Log.i("test", "服务器开启成功");
			ServerOpenThread t = new ServerOpenThread(serverSocker);
			openServerThread = new Thread(t);
			openServerThread.start();

			//Log.i("test", "--服务器:发送所有人进程开启");
			ServerSend ss = new ServerSend();
			serverSendThread = new Thread(ss);
			serverSendThread.start();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i("test", "服务器开启失败");
			e.printStackTrace();
		}

	}


	//同步配置
	public void syncConfig(){

	}


	// 初始化画布
	public void initBitMap() {
//		this.baseBitmap = Bitmap.createBitmap(this.view.getWidth(),
//				this.view.getHeight(), Bitmap.Config.ARGB_8888);
		this.baseBitmap = Bitmap.createBitmap((int)ZoomController.NORM_WIDGET, (int)ZoomController.NORM_HEIGHT, Bitmap.Config.ARGB_8888);
		this.canvas = new Canvas(this.baseBitmap);
		this.canvas.drawColor(Color.WHITE);
		myStack.push(baseBitmap);
		graphStack.push(baseBitmap,0);

	}


	// 初始化历史画布
	public void initBitMap(String path) {
		this.baseBitmap = BitmapFactory.decodeFile(path).copy(Bitmap.Config.ARGB_8888,true);
		this.canvas = new Canvas(this.baseBitmap);
//		this.canvas.drawColor(Color.WHITE);
		myStack.push(baseBitmap);
	}

	// 初始化钢笔
	public void initPaint() {
		this.paint = new Paint();
		this.paint.setColor(Color.RED);
		this.paint.setStrokeWidth(5);
	}

	// 用户首次进来时 就发送当前画布

	//清空画布
	public void cleanBitmap(){
		canvas.drawColor(Color.WHITE);
	}

	public void undo(){
		Bitmap bitmap;
		bitmap = myStack.undo();
		if(bitmap!=null){
			canvas.drawBitmap(bitmap,0,0,paint);
			refresh(Board.position);
		}
	}

	public void redo(){
		Bitmap bitmap;
		bitmap = myStack.redo();
		if(bitmap!=null){
			canvas.drawBitmap(bitmap,0,0,paint);
			refresh(Board.position);
		}
	}

	public void add(int position){

		canvas.drawColor(Color.WHITE);
		graphStack.push(baseBitmap,position);
	}

	public void del(int position){
		Bitmap bitmap;
		bitmap = graphStack.delect(position-1);
		if(bitmap!=null){
			canvas.drawBitmap(bitmap,0,0,paint);
		}
	}

	public void last(int position){

		Bitmap bitmap;
		bitmap = graphStack.last(position);
		if(bitmap!=null){
			canvas.drawBitmap(bitmap,0,0,paint);
		}
	}

	public void next(int position){

		Bitmap bitmap;
		bitmap = graphStack.next(position);
		if(bitmap!=null){
			canvas.drawBitmap(bitmap,0,0,paint);
		}
	}

	public ArrayList<Bitmap> getBitmaps(){
		return graphStack.getBitmaps();
	}


	public void refresh(int position){
		graphStack.refresh(baseBitmap,position);
	}


	public int getCount(){
		return graphStack.getCount();
	}


	// 服务器 用户接入监听
	class ServerOpenThread implements Runnable {

		private ServerSocket ss = null;
		private boolean opening = false;

		public ServerOpenThread(ServerSocket ss) {
			this.ss = ss;
			opening = true;
		}

		@Override
		public void run() {
			while (opening) {
				try {
					//Log.i("test", "服务器监听中");
					Socket s = this.ss.accept();

					Thread clientTd = new Thread(new ClientThread(s));
					clientTd.start();

					// sendInitview(s);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	// 用于监听每个用户的操作 在服务器监听那里 监听到一个用户就新建一个线程
	// 监听来着客户端的操作 并修改服务器画布 发送给客户端
	class ClientThread implements Runnable {

		Socket s;
		private boolean isconnect = false;
		private GraphIF clientDrawPen = null;

		public ClientThread(Socket s) {
			this.s = s;
			isconnect = true;
		}

		@Override
		public void run() {
			// 用户首次进来 发送初始化的bitmap
			sendInitview(this.s);
			// 先发送初始化bitmap 再添加用户防止 sendall 和 sendinitview 写入同时进行 冲突
			userList.add(this.s);
			//Log.i("test", "新用户登进");
			//Log.i("test", "当前人数用户" + userList.size());
			// 在这里给每个人一个画笔

			while (isconnect) {
				try {
					ObjectInputStream ois = new ObjectInputStream(
							s.getInputStream());
					DrawAction da = (DrawAction) ois.readObject();
					// Log.i("server_test",
					// "--服务端：原始的TAG为"+drawPenTool.getTAG());
					// Log.i("server_test", "--服务端：得到的TAG为"+da.getdrawPenTAG());
					initDrawPen(da);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		private void initDrawPen(DrawAction da) {
			// 可增加判断 if 画笔类型为空 就
			
			
			
			if (clientDrawPen == null) {
				clientDrawPen = new PenImpl(5, Color.BLACK);

			}
			if (!da.getdrawPenTAG().equals(clientDrawPen.getTAG())) {
				switch (da.getDrawPenStyle()) {
					case 1:
						clientDrawPen = new PenImpl(da.getPaintSize()/wScale, da.getPaintColor());
						break;
					case Engine.circlectTool:
						clientDrawPen = new CirclectlImpl(da.getPaintSize()/wScale, da.getPaintColor());
						break;
					case Engine.lineTool:
						clientDrawPen = new LineImpl(da.getPaintSize()/wScale, da.getPaintColor());
						break;
					case Engine.rectuTool:
						clientDrawPen = new RectuImpl(da.getPaintSize()/wScale, da.getPaintColor());
						break;
					case Engine.eraserTool:

						clientDrawPen = new EraserImpl(da.getPaintSize()/wScale);
						break;
					case Engine.arrowTool:
						clientDrawPen = new ArrowImpl(da.getPaintSize()/wScale,da.getPaintColor());
						break;
					case Engine.textTool:
						clientDrawPen = new TextImpl(da.getPaintSize()/wScale,da.getPaintColor(),da.getPaintText());
						break;
					default:
						break;
				}
			}

			drawBitMap(da);
		}


		private void drawBitMap(DrawAction da) {
			//如果是笔画就全画
			if(da.getDrawPenStyle()==1||da.getDrawPenStyle()==5){
				if (da.getAction().equals("ACTION_DOWN")) {

					clientDrawPen.touchDown(da.getX()/wScale, da.getY()/hScale);
					// Log.i("server_test", "--服务端：手指按下");
				} else if (da.getAction().equals("ACTION_MOVE")) {
					clientDrawPen.touchMove(da.getX()/wScale, da.getY()/hScale);
					 //Log.i("server_test", "--服务端：手指滑动");
				} else {
					clientDrawPen.touchUp(da.getX()/wScale, da.getY()/hScale);
					// Log.i("server_test", "--服务端：手指抬起");
					clientDrawPen.draw(canvas);

//					clientDrawPen = new PenImpl(5,5);
					//每画一笔 就要重置画笔 这样就会有层次感
					clientDrawPen = null;
					myStack.push(baseBitmap);
					graphStack.refresh(baseBitmap, Board.position);
					return;
				}
				
				clientDrawPen.draw(canvas);
			}
			//如果是矢量图就只画结尾
			else{
				if (da.getAction().equals("ACTION_DOWN")) {
					clientDrawPen.touchDown(da.getX()/wScale, da.getY()/hScale);
				} else if (da.getAction().equals("ACTION_MOVE")) {
					clientDrawPen.touchMove(da.getX()/wScale, da.getY()/hScale);
				} else {
					clientDrawPen.touchUp(da.getX()/wScale, da.getY()/hScale);
					clientDrawPen.draw(canvas);
					myStack.push(baseBitmap);
					graphStack.refresh(baseBitmap, Board.position);
					//每画一笔 就要重置画笔 这样就会有层次感
					clientDrawPen = null;
					//myStack.push(baseBitmap);
				}
			}
		}

		private void sendInitview(Socket s) {
			Log.e("sendInitView","the view is send");
			OutputStream ops = null;
			DataOutputStream dos = null;
			byte[] bitByt = null;
			try {

				bitByt = BitmapUtil.Bit2Byte(baseBitmap);

				int byteSize = bitByt.length;
				ops = s.getOutputStream();
				dos = new DataOutputStream(ops);

				dos.writeInt(byteSize);
				dos.write(bitByt);
				dos.flush();
				// Log.i("test", "写入成功");

			} catch (IOException e1) {

				e1.printStackTrace();
			}
		}

	}

	// 发送图片线程
	class ServerSend implements Runnable {
		public ServerSend() {
		}

		@Override
		public void run() {
			while (true) {
				if (userList.size() != 0) {
					// Log.i("server_test", "--服务端：开始发送所有数据");
					for (int i = 0; i < userList.size(); i++) {
						Socket st = (Socket) userList.get(i);
						sendAll(st);
					}
				}
			}

		}

		private void sendAll(Socket s) {
			OutputStream ops = null;
			DataOutputStream dos = null;
			byte[] bitByt = null;
			try {

				bitByt = BitmapUtil.Bit2Byte(baseBitmap);

				int byteSize = bitByt.length;
				ops = s.getOutputStream();
				dos = new DataOutputStream(ops);

				dos.writeInt(byteSize);
				dos.write(bitByt);
				dos.flush();
				// Log.i("test", "--服务端:写入给所有人成功");

			} catch (IOException e1) {
				Log.i("test", "--服务端:写入给所有人失败");
				e1.printStackTrace();
			}
		}

	}




}
