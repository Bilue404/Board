package com.bilue.board.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.bilue.board.R;
import com.bilue.board.util.Engine;
import com.bilue.board.view.ClientFrontView;

/**
 * @description CustomSeekBar
 * @author Justin
 * @date Dec 17, 2013
 * @version v1.0
 */
public class CustomSeekBar extends SeekBar{

    private Context mContext;
    private View mView;
    private PopupWindow mPopupWindow;
    private float mDensity;
    private int mOffsetX;
    private int mRadius;
    private ValueAnimator valueAnimator;

    private Bitmap mBitmap;
    private int mBitmapHeight;
    private int mBitmapWidth;
    private int mThumbWidth;
    private int mSlideLength;

    private int paintColor = Engine.DEFAULT_COLOR;
    private ClientFrontView clientFrontView=null;

    private static final int MSG_DISMISS = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DISMISS:
                    startAnimation(1, 0, 400);
                    break;
            }
        }
    };

    public CustomSeekBar(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        setOnSeekBarChangeListener(null);

        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.popover);
        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        mDensity = getResources().getDisplayMetrics().density;
        mPopupWindow = new PopupWindow();
        WindowManager manager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm=new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        final int width = (int) (dm.widthPixels*0.8);
        final int height=(int) (dm.heightPixels *0.4) ;

        mPopupWindow.setHeight( height);
        mPopupWindow.setWidth(width);
        //mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.alpha(0)));
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(80,226,226,226)));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(false);
        mPopupWindow.setTouchInterceptor(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    return true;
                }
                return false;
            }
        });
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(paintColor);

        final Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(60);
        
        mView = new View(mContext) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                paint.setStrokeWidth(mRadius);
                paint.setColor(Engine.paintColor);
                //canvas.drawBitmap(mBitmap, mOffsetX, 0, paint);
                canvas.drawLine(40, height / 2, width-40, height / 2, paint);
                canvas.drawText(mRadius+"", width/2-40, height/2+100, textPaint);
              
            }
        };
        mPopupWindow.setContentView(mView);
    }

    @SuppressLint("NewApi")
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Drawable drawable = getThumb();
        mThumbWidth = drawable.getIntrinsicWidth();
        mSlideLength = getWidth() - mThumbWidth;
        //mPopupWindow.setWidth(mSlideLength + mBitmapWidth);
    }

    private void startAnimation(float start, float end, int duration) {
        valueAnimator = ValueAnimator.ofFloat(start, end);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (Float) animation.getAnimatedValue();
                mView.setAlpha(alpha);
                mView.invalidate();
            }
        });
        
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.start();
    }

    
    public void sendClient(ClientFrontView cfv){
    	clientFrontView = cfv;
    }
    
    
    
    @Override
    public void setOnSeekBarChangeListener(final OnSeekBarChangeListener l) {
        super.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRadius = (int) (progress * mDensity / 5);
                mOffsetX = (int) (mSlideLength * progress * 1.0f / seekBar.getMax());
                
                mView.invalidate();
                if (l != null) {
                    l.onProgressChanged(seekBar, progress, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                mView.setAlpha(1);
                if (mPopupWindow.isShowing()) {
                    mHandler.removeMessages(MSG_DISMISS);
                } else {
                    show(seekBar, (mThumbWidth - mBitmapWidth) / 2, -mBitmapHeight);
                }
                if (l != null) {
                    l.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mPopupWindow.isShowing()) {
                    mHandler.sendEmptyMessageDelayed(MSG_DISMISS, 500);
                    Engine.paintSize = mRadius;
                    //TODO 笔画大小
                    if (listener != null) {
                        listener.onSizeChangeListener(mRadius);
                    }
//                    clientFrontView.updatePaint();
                }
                if (l != null) {
                    l.onStopTrackingTouch(seekBar);
                    
                }
            }

            public void show(View anchor, int offsetX, int offsetY) {
                int[] loc = new int[2];
                anchor.getLocationOnScreen(loc);
                int x = loc[0];
                int y = loc[1];
                x += offsetX;
                y += offsetY;
                //mPopupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, x, y);
                mPopupWindow.showAtLocation(anchor, Gravity.CENTER_VERTICAL, 0, 0);
            }
        });
    }

    public void setSeekBarColor(int color){
        paintColor = color;
    }

    private OnSizeChangeListener listener;
    public void setOnSizeChangeListener(OnSizeChangeListener listener){
        this.listener = listener;
    }

    public interface OnSizeChangeListener{
        void onSizeChangeListener(int size);
    }
}
