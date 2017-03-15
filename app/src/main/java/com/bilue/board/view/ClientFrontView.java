package com.bilue.board.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bilue.board.controller.ZoomController;
import com.bilue.board.graph.ArrowImpl;
import com.bilue.board.graph.CirclectlImpl;
import com.bilue.board.graph.EraserImpl;
import com.bilue.board.graph.GraphIF;
import com.bilue.board.graph.LineImpl;
import com.bilue.board.graph.PenImpl;
import com.bilue.board.graph.RectuImpl;
import com.bilue.board.graph.TextImpl;
import com.bilue.board.util.Engine;

public class ClientFrontView extends View {


	private float startX, startY;

	private GraphIF drawPenTool = null;
	//判断是否抬起手了
	private boolean isUp = true;
	private float scale = ZoomController.getScale();

	private int paintStyle = Engine.PEN_TOOL;
	private float paintSize = Engine.DEFAULT_SIZE/scale;
	private int paintColor = Engine.DEFAULT_COLOR;
	private OnActionChangeListener listener;
	public ClientFrontView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void init() {
		drawPenTool = Engine.drawPenTool;
	}


	public void updatePaint() {
		switch (paintStyle) {
			case Engine.PEN_TOOL: // 铅笔
				drawPenTool = new PenImpl(paintSize, paintColor);
				break;

			case Engine.CIRCLECT_TOOL:
				drawPenTool = new CirclectlImpl(paintSize, paintColor);
				break;
			case Engine.LINE_TOOL:
				drawPenTool = new LineImpl(paintSize,paintColor);
				break;
			case Engine.RECTU_TOOL:
				drawPenTool = new RectuImpl(paintSize,paintColor);
				break;
			case Engine.ERASER_TOOL:
				drawPenTool = new EraserImpl(paintSize);
				break;
			case Engine.ARROW_TOOL:
				drawPenTool = new ArrowImpl(paintSize,paintColor);
				break;
			case Engine.TEXT_TOOL:
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
		switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN:
				isUp = false;
				startX = event.getX();
				startY = event.getY();

				drawPenTool.touchDown(startX, startY);


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

				if (listener != null) {
					listener.onActionChange(drawPenTool.getTAG(), drawPenTool.getDrawPenStyle(), "ACTION_MOVE", endX, endY,paintSize,paintColor,Engine.paintText);
				}

				invalidate();

				break;
			case MotionEvent.ACTION_UP:

				drawPenTool.touchUp(event.getX(), event.getY());

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


	public interface OnActionChangeListener{
		void onActionChange(String drawPenTAG, int drawPenStyle , String action, float x, float y, float paintSize, int paintColor,String paintText);
	}

}