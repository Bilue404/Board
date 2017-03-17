package com.bilue.board.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bilue.board.constant.Engine;
import com.bilue.board.controller.ZoomController;
import com.bilue.board.graph.BasePaint;
import com.bilue.board.graph.PaintFactory;
import com.bilue.board.graph.PenPaint;

public class ClientFrontView extends View {


	private float startX, startY;

	private BasePaint drawPenTool = null;
	//判断是否抬起手了
	private boolean isUp = true;
	private float scale = ZoomController.getScale();

	private int paintStyle = Engine.PEN_TOOL;
	private float paintSize = Engine.DEFAULT_SIZE/scale;
	private int paintColor = Engine.DEFAULT_COLOR;
	private String paintText = "";
	private OnActionChangeListener listener;
	private PaintFactory paintFactory;
	public ClientFrontView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void init() {
		drawPenTool = new PenPaint(Engine.DEFAULT_SIZE, Engine.DEFAULT_COLOR);
	}


	public void updatePaint() {
		if (paintFactory == null) {
			paintFactory = new PaintFactory();
		}
		drawPenTool = paintFactory.creatPaint(paintStyle,paintSize,paintColor,paintText);
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

	public void setPaintText(String text){
		 this.paintText = text;
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
					listener.onActionChange(drawPenTool.getTAG(), drawPenTool.getDrawPenStyle(), "ACTION_DOWN", startX, startY,paintSize,paintColor,paintText);
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
					listener.onActionChange(drawPenTool.getTAG(), drawPenTool.getDrawPenStyle(), "ACTION_MOVE", endX, endY,paintSize,paintColor,paintText);
				}

				invalidate();

				break;
			case MotionEvent.ACTION_UP:

				drawPenTool.touchUp(event.getX(), event.getY());

				isUp=true;
				if (listener != null) {
					listener.onActionChange(drawPenTool.getTAG(), drawPenTool.getDrawPenStyle(), "ACTION_UP", event.getX(), event.getY(),paintSize,paintColor,paintText);
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