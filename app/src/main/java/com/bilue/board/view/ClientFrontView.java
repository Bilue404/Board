package com.bilue.board.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bilue.board.controller.ConfigController;
import com.bilue.board.controller.ZoomController;
import com.bilue.board.graph.ArrowImpl;
import com.bilue.board.graph.CirclectlImpl;
import com.bilue.board.graph.EraserImpl;
import com.bilue.board.graph.GraphIF;
import com.bilue.board.graph.LineImpl;
import com.bilue.board.graph.PenImpl;
import com.bilue.board.graph.RectuImpl;
import com.bilue.board.graph.TextImpl;
import com.bilue.board.util.DrawAction;
import com.bilue.board.util.Engine;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientFrontView extends View {


	private float startX, startY;

	private Thread sendActionThread = null;
	private GraphIF drawPenTool = null;
//	private SendThread st = null;
	//判断是否抬起手了
	private boolean isUp = true;
	private float scale = ZoomController.getScale();

	private int paintStyle = Engine.penTool;
	private float paintSize = Engine.DEFAULT_SIZE/scale;
	private int paintColor = Engine.DEFAULT_COLOR;
	private OnActionChangeListener listener;
	public ClientFrontView(Context context, AttributeSet attrs) {
		super(context, attrs);

		//Log.i("client_test", "此时的socket为" + Engine.clientSocket);
		init();

	}

	public void init() {
		drawPenTool = Engine.drawPenTool;

//		st = new SendThread(Engine.clientSocket);
//		sendActionThread = new Thread(st);
//		sendActionThread.start();

		//Log.i("client_test", "UI线程启动了");
	}


	public void updatePaint() {
		switch (paintStyle) {
			case Engine.penTool: // 铅笔
				drawPenTool = new PenImpl(paintSize, paintColor);
				break;

			case Engine.circlectTool:
				drawPenTool = new CirclectlImpl(paintSize, paintColor);
				break;
			case Engine.lineTool:
				drawPenTool = new LineImpl(paintSize,paintColor);
				break;
			case Engine.rectuTool:
				drawPenTool = new RectuImpl(paintSize,paintColor);
				break;
			case Engine.eraserTool:
				drawPenTool = new EraserImpl(paintSize);
				break;
			case Engine.arrowTool:
				drawPenTool = new ArrowImpl(paintSize,paintColor);
				break;
			case Engine.textTool:
				drawPenTool = new TextImpl(paintSize,paintColor,Engine.paintText);
				break;
			default:
				break;
		}
	}

	public void setPaintStyle(int paintStyle){
		this.paintStyle = paintStyle;
		updatePaint();
	}

	public void setPaintSize(float paintSize) {
		this.paintSize = paintSize/scale;
		updatePaint();
	}

	public void setPaintColor(int paintColor) {
		this.paintColor = paintColor;
		updatePaint();
	}

	public void setOnActionChangeListener(OnActionChangeListener listener){
		this.listener = listener;
	}



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

//				st.SendActon(drawPenTool.getTAG(), drawPenTool.getDrawPenStyle(), "ACTION_DOWN", startX, startY);

				if (listener != null) {
					listener.onActionChange(drawPenTool.getTAG(), drawPenTool.getDrawPenStyle(), "ACTION_DOWN", startX, startY,paintSize,paintColor,Engine.paintText);
				}
				invalidate();
				break;

			case MotionEvent.ACTION_MOVE:
				float endX;
				float endY;
				endX = event.getX();
				endY = event.getY();
				drawPenTool.touchMove(endX, endY);
//				st.SendActon(drawPenTool.getTAG(), drawPenTool.getDrawPenStyle(), "ACTION_MOVE", endX, endY);

				if (listener != null) {
					listener.onActionChange(drawPenTool.getTAG(), drawPenTool.getDrawPenStyle(), "ACTION_MOVE", endX, endY,paintSize,paintColor,Engine.paintText);
				}

				invalidate();

				break;
			case MotionEvent.ACTION_UP:

				drawPenTool.touchUp(event.getX(), event.getY());
//				st.SendActon(drawPenTool.getTAG(), drawPenTool.getDrawPenStyle(), "ACTION_UP", event.getX(),
//						event.getY());

				isUp=true;
				if (listener != null) {
					listener.onActionChange(drawPenTool.getTAG(), drawPenTool.getDrawPenStyle(), "ACTION_UP", event.getX(), event.getY(),paintSize,paintColor,Engine.paintText);
				}
				invalidate();

			break;

		default:
			break;
		}

		return true;
	}


//	JSONArray jsonArray = new JSONArray();
//	private void putJson(DrawAction drawAction){
//		Gson gson = new Gson();
//		String drawStr = gson.toJson(drawAction);
//		try {
//			JSONObject jsonObject = new JSONObject(drawStr);
//			Log.e("putJson","the jsonObject is "+jsonObject);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}



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
						drawPenStyle, paintSize, paintColor,Engine.paintText);
				ConfigController configController = ConfigController.getConfigController();
				configController.putAction(da);

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


	public interface OnActionChangeListener{
		void onActionChange(String drawPenTAG, int drawPenStyle , String action, float x, float y, float paintSize, int paintColor,String paintText);
	}

}