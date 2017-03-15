package com.bilue.board.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.bilue.board.util.BitmapUtil;

public class ClientBgView extends View {

	private Bitmap baseBitmap = null;
	private Paint paint = null;


	public ClientBgView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if (baseBitmap != null) {
			canvas.drawBitmap(baseBitmap, 0, 0, paint);
		}
	}

	public void setBitmap(Bitmap bitmap){
		if (bitmap!=null){
			baseBitmap = bitmap;
			invalidate();
		}
	}


	public boolean save(String fileName) {
		if (baseBitmap != null) {
			//System.currentTimeMillis()+""
			return BitmapUtil.saveBitmap2file(baseBitmap, fileName);
		}
		return false;

	}


}
