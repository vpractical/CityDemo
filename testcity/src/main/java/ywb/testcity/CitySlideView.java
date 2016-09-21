package ywb.testcity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by Johnny on 2016/8/16.
 */
public class CitySlideView extends View {
	private int mTouchSlop,downY,selectedPos = -1;
	private int mWidth,mHeight,mTotalHeight;
	private int letterSize = 40;
	private Paint paint;
	private Rect mTextRect;
	private boolean isVerticalSlide;



	public interface OnSelectedLettleListener{
		void onSelected(String lettle);
		void onUp();
	}

	private OnSelectedLettleListener selectedLettleListener;
	public void setOnSelectedLettleListener(OnSelectedLettleListener l){
		this.selectedLettleListener = l;
	}
	public CitySlideView(Context context) {
		this(context,null);
	}

	public CitySlideView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public CitySlideView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context){
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mTextRect = new Rect();
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpec = MeasureSpec.getMode(widthMeasureSpec);
		int heightSpec = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width,height;
		if(widthSpec == MeasureSpec.EXACTLY){
			width = widthSize;
		}else{
			width = widthSize / 2;
		}
		if (heightSpec == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			height = heightSize * 1 / 2;
		}

		mWidth = width;
		mHeight = height;
		mTotalHeight = (mHeight / MainActivity.letterList.size() - letterSize) / 2 + letterSize - 5;

		setMeasuredDimension(width,height);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent e) {
		switch(e.getAction()){
			case MotionEvent.ACTION_DOWN:
				downY = (int) e.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				isVerticalSlide = Math.abs(downY - e.getY()) > mTouchSlop;
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:

			break;
			default:
				break;
		}

		return super.dispatchTouchEvent(e);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {

		switch(e.getAction()){
			case MotionEvent.ACTION_DOWN:
				selectedPos = (int) (e.getY() / (mHeight / MainActivity.letterList.size() + 1));
				mTotalHeight = (mHeight / MainActivity.letterList.size() - letterSize) / 2 + letterSize - 5;
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				if(isVerticalSlide){
					selectedPos = (int) (e.getY() / (mHeight / MainActivity.letterList.size() + 1));
					mTotalHeight = (mHeight / MainActivity.letterList.size() - letterSize) / 2 + letterSize - 5;
					invalidate();
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
					selectedPos = -1;
					mTotalHeight = (mHeight / MainActivity.letterList.size() - letterSize) / 2 + letterSize - 5;
					invalidate();
				selectedLettleListener.onUp();
				break;
			default:
				break;
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		paint.setColor(Color.TRANSPARENT);
		canvas.drawRect(0,0,mWidth,mHeight,paint);
		for (int i = 0; i < MainActivity.letterList.size(); i++) {
			String text = MainActivity.letterList.get(i);
			if(i == selectedPos){
				paint.setColor(Color.RED);
				selectedLettleListener.onSelected(text);
			}else{
				paint.setColor(Color.GRAY);
			}
			paint.setTextSize(letterSize);
			paint.getTextBounds(text,0,text.length(),mTextRect);
			canvas.drawText(text,(mWidth - mTextRect.width()) / 2,mTotalHeight,paint);
			mTotalHeight += mHeight / MainActivity.letterList.size();
		}
	}
}
