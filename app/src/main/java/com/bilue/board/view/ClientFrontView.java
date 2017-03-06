package com.bilue.board.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.widget.Toast;

import com.bilue.board.graph.ArrowImpl;
import com.bilue.board.graph.EraserImpl;
import com.bilue.board.graph.GraphIF;
import com.bilue.board.graph.LineImpl;
import com.bilue.board.graph.PenImpl;
import com.bilue.board.graph.RectuImpl;
import com.bilue.board.graph.TextImpl;
import com.bilue.board.util.DrawAction;
import com.bilue.board.util.Engine;
import com.bilue.board.graph.CirclectlImpl;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientFrontView extends View {

	private Bitmap baseBitmap;
	private Canvas canvas;
	private Paint paint;
	private Path myPath;

	private float startX, startY;

	private Thread sendActionThread = null;
	private GraphIF drawPenTool = null;
	private SendThread st = null;
	//判断是否抬起手了
	private boolean isUp = true;

	public ClientFrontView(Context context, AttributeSet attrs) {
		super(context, attrs);

		//Log.i("client_test", "此时的socket为" + Engine.clientSocket);
		init();

	}

	public void init() {
		drawPenTool = Engine.drawPenTool;
		st = new SendThread(Engine.clientSocket);
		sendActionThread = new Thread(st);
		sendActionThread.start();
		//Log.i("client_test", "UI线程启动了");
	}

	public void updatePaint() {
		switch (Engine.DRAW_PEN_STYLE) {
			case Engine.penTool: // 铅笔
				drawPenTool = new PenImpl(Engine.paintSize, Engine.paintColor);
				break;
				
			case Engine.circlectTool:
				drawPenTool = new CirclectlImpl(Engine.paintSize, Engine.paintColor);
				break;
			case Engine.lineTool:
				drawPenTool = new LineImpl(Engine.paintSize, Engine.paintColor);
				break;
			case Engine.rectuTool:
				drawPenTool = new RectuImpl(Engine.paintSize, Engine.paintColor);
				break;
			case Engine.eraserTool:
				drawPenTool = new EraserImpl(Engine.paintSize);
				break;
			case Engine.arrowTool:
				drawPenTool = new ArrowImpl(Engine.paintSize,Engine.paintColor);
				break;
			case Engine.textTool:
				drawPenTool = new TextImpl(Engine.paintSize,Engine.paintColor,Engine.paintText);
				break;
			default:
				break;
		}
	}

//	public void updatePaint(String text){
//		drawPenTool = new TextImpl(Engine.paintSize,Engine.paintColor,text);
//	}

	@Override
	public void draw(Canvas canvas) {
		// TODO 重写draw方法
		super.draw(canvas);
		drawPenTool.draw(canvas);
		this.setAlpha(1);
		if(isUp){
//			Paint mPaint = new Paint();
//			//mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//			canvas.drawColor(Color.argb(0, 0xff, 0, 0));
//			//mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

//			update Paint();
//			drawPenTool.draw(canvas);
			//canvas.drawColor(Color.argb(0, 0xff, 0, 0));
			//canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			//this.getBackground().setAlpha(100);
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			updatePaint();
			this.setAlpha(0);
			//canvas.drawColor(Color.argb(0, 0xff, 0, 0));

		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO 动作监听
		switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN:
				isUp = false;
				startX = event.getX();
				startY = event.getY();

			drawPenTool.touchDown(startX, startY);
			st.SendActon(drawPenTool.getTAG(), drawPenTool.getDrawPenStyle(), "ACTION_DOWN", startX, startY);
			invalidate();
				break;

			case MotionEvent.ACTION_MOVE:
				float endX;
				float endY;
				endX = event.getX();
			endY = event.getY();
			drawPenTool.touchMove(endX, endY);
			st.SendActon(drawPenTool.getTAG(), drawPenTool.getDrawPenStyle(), "ACTION_MOVE", endX, endY);
				invalidate();

				break;
			case MotionEvent.ACTION_UP:

			drawPenTool.touchUp(event.getX(), event.getY());
			st.SendActon(drawPenTool.getTAG(), drawPenTool.getDrawPenStyle(), "ACTION_UP", event.getX(),
					event.getY());

			isUp=true;
			invalidate();

			break;

		default:
			break;
		}

		return true;
	}

	class SendThread implements Runnable {

		Socket s = null;
		private boolean isSend = false;

		public SendThread(Socket s) {
			this.s = s;
			isSend = true;
		}

		@Override
		public void run() {

		}

		public void SendActon(String drawPenTAG, int drawPenStyle , String action, float x, float y) {
			// isSend = true;
			try {

				//Log.i("client_test", "客户端：写入动作开始");
				DrawAction da = new DrawAction(drawPenTAG, action, x, y,
						drawPenStyle, Engine.paintSize, Engine.paintColor,Engine.paintText);
				ObjectOutputStream obs = new ObjectOutputStream(
						s.getOutputStream());
				obs.writeObject(da);
				obs.flush();
				//Log.i("client_test", "客户端：写入动作结束");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}