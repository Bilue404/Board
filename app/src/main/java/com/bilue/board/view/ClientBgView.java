package com.bilue.board.view;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.net.Socket;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.bilue.board.activity.Board;
import com.bilue.board.util.BitmapUtil;
import com.bilue.board.util.Engine;

public class ClientBgView extends View {

	public final int UPDATE_VIEW = 1;
	public final int CONNECT_SUCCSEE = 4;
	private Bitmap baseBitmap = null;
	private Paint paint = null;

	private MyHandler myHandler;
	// private Socket s ;
	private Thread conThread = null;
	private Thread recThread = null;

	public ClientBgView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		myHandler = new MyHandler();

	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.draw(canvas);

		// 判断bitmap 是否为空 不为空就开始画
		if (baseBitmap != null) {
			canvas.drawBitmap(baseBitmap, 0, 0, paint);
			// Log.i("client_test", "修改画布成功");
		}
	}

	// 连接服务器
	public void connectServer() {
		connectThread ct = new connectThread();
		this.conThread = new Thread(ct);
		this.conThread.start();

	}

	public void startClient() {

		RecThread rec = new RecThread(Engine.clientSocket);
		recThread = new Thread(rec);
		recThread.start();

	}

	public boolean save(String fileName) {
		if (baseBitmap != null) {
			//System.currentTimeMillis()+""
			return BitmapUtil.saveBitmap2file(baseBitmap, fileName);
		}
		return false;

	}

	class connectThread implements Runnable {

		Socket s;

		public connectThread() {

		}

		@Override
		public void run() {
			// Log.i("client_test", "尝试连接");

			try {
				Thread.sleep(3000);// 暂停5S 再开始连接 防止服务器还在开启 导致连入失败

				this.s = new Socket(Engine.ServerIp, Engine.Port);
				Engine.clientSocket = this.s;
				// Log.i("client_test", "客户端建立成功");

				Message msg = new Message();
				msg.what = CONNECT_SUCCSEE;
				msg.obj = this.s;
				myHandler.sendMessage(msg);

				Message mm = new Message();
				mm.what = 6565;
				Board.serverBoardHandler.sendMessage(mm);

				// Log.i("client_test", "消息发送成功");

			} catch (Exception e) {

				// Log.i("client_test", "消息发送失败");
				e.printStackTrace();
			}
		}

	}

	// 连接服务 用于监听服务器发下来的图片
	class RecThread implements Runnable {

		private boolean scaning = false;
		private Socket s = null;
		private InputStream is = null;
		private DataInputStream dis = null;

		public RecThread(Socket s) {
			this.s = s;
			scaning = true;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (s != null) {

				// getBaseBitmap(s);
				// Message msg = new Message();
				// msg.what = UPDATE_VIEW;

				while (scaning) {
					// 做监听处理getBaseBitmap(s);
					// 连进来先接受

					getBaseBitmap(s);
					Message msg = new Message();
					msg.what = UPDATE_VIEW;
					myHandler.sendMessage(msg);

				}
			}

		}

		// 获取首次bitmap
		private void getBaseBitmap(Socket s) {

			try {
				is = s.getInputStream();
				dis = new DataInputStream(is);
				int i = dis.readInt();
				// Log.i("client_test", "读过来的文件大小为"+i);

				byte[] bitByte = new byte[i];
				// Log.i("client_test", "开始读数据");
				dis.readFully(bitByte);
				// Log.i("client_test", "读数据结束");
				// Log.i("client_test", "开始解析数据");
				baseBitmap = BitmapUtil.Bytes2Bitmap(bitByte);
				// Log.i("client_test", "解析结束dis.read");

				// 问题测试结果 bitbyte 拿得到

			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.i("client_test", "读取图片文件出错");
				e.printStackTrace();
			}
		}

	}


	//处理画布更新
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			switch (msg.what) {

			case UPDATE_VIEW:
				// Log.i("client_test", " 提示更新界面");
				invalidate();
				break;
			case CONNECT_SUCCSEE:
				// Log.i("client_test", " 开启客户端线程");
				Engine.clientSocket = (Socket) msg.obj;
				startClient();
				break;
			default:
				break;
			}
		}
	}

}
