package com.example.piechart3d;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Scroller;

public class CustomScroller extends View {
	private Context mContext;
	public FlingRunnable mFlingRunnable;
	interface ScrollListener{
		public void onScrolledDiff(int diff);
	}
	ScrollListener l;
	public CustomScroller(Context context,ScrollListener l) {
		super(context);
		this.l=l;
		this.mContext = context;
		mFlingRunnable = new FlingRunnable();
	}

	public class FlingRunnable implements Runnable {
		private Scroller mScroller;
		private int mCurrScrollX = 1000, lastx;
		private int mCurrScrollY = 1000;

		// Constructor
		public FlingRunnable() {
			LinearInterpolator linearInterpolator = new LinearInterpolator();
			/*
			 * The accelerate interpolator starts slowly but arrives at the
			 * final position at full speed. The opposite effect can be achieved
			 * by using decelerate_interpolator. This will start at full speed
			 * but then slowly come to rest at the end position.
			 */
			AccelerateInterpolator ai = new AccelerateInterpolator(1.0f);
			DecelerateInterpolator di = new DecelerateInterpolator(1.0f);
			/*
			 * The interpolator that combines an acceleration phase and a
			 * deceleration phase is, you guessed it, the
			 * accelerate_decelerate_interpolator. The code for the XML file
			 * looks like follows.
			 */
			AccelerateDecelerateInterpolator adi = new AccelerateDecelerateInterpolator();

			/*
			 * The anticipate_interpolator starts by moving the view backwards
			 * before it accelerates forwards. This creates the effect of a
			 * slingshot. As with the accelerate interpolator, the final
			 * position is reached at full speed.
			 */
			AnticipateInterpolator ani = new AnticipateInterpolator(1.0f);
			/*
			 * The opposite of the anticipate interpolator is the
			 * overshoot_interpolator. Here the view starts with full speed but
			 * overshoots the target before it returns to the final position.
			 */
			OvershootInterpolator osi = new OvershootInterpolator(1.0f);
			/*
			 * Combining the effects of both the anticipate and the overshoot
			 * interpolator is the anticipate_overshoot_interpolator. The
			 * movement starts away from the target before it accelerates
			 * towards the target. It then overshoots and returns to the final
			 * position.
			 */
			AnticipateOvershootInterpolator anosi = new AnticipateOvershootInterpolator(1.0f);
			/* http://cogitolearning.co.uk/?p=952 */
			// Create scroller and assign a interpolator to it.
			mScroller = new Scroller(mContext, di);
		}
		
		void startScroll(int startX, int startY, int maxX, int maxY, int duration) {
			mScroller.startScroll(startX, startY, maxX, maxY, duration);
			post(this);
		}

		public boolean onContentFling(MotionEvent e1, MotionEvent e2,
				float velocityX, float velocityY) {
			Log.d("app", "Started scrolling" + mCurrScrollX);
			mScroller.fling(mCurrScrollX, mCurrScrollY, (int) velocityX,
					(int) velocityY, 0, Integer.MAX_VALUE, 0, 0);
			post(this);
			return true;
		}


		@Override
		public void run() {
			if (mScroller.isFinished()) {
				Log.d("app", "scroller is finished, done with fling");
				return;
			}
			// If we are still scrolling get the new x,y values.
			runOnFliing();
		}

		void runOnFliing() {
			if (mScroller.computeScrollOffset()) {
				mCurrScrollX = mScroller.getCurrX();
				mCurrScrollY = mScroller.getCurrY();
				int diff = lastx - mCurrScrollX;
				if (diff != 0) {
					if(l!=null)
						l.onScrolledDiff(diff);
					// scrollingView.scrollBy(diff, 0);
					lastx = mCurrScrollX;
					Log.d("app", "scrolling" + lastx);
				}
				postDelayed(this, 0);
			}
		}

		void runOnIniti() {
			boolean more = mScroller.computeScrollOffset();
			mCurrScrollX = mScroller.getCurrX();
			int diff = lastx - mCurrScrollX;
			if (diff != 0) {
				// scrollingView.scrollBy(diff, 0);
				lastx = mCurrScrollX;
			}
		}


		public void stopScroller() {
			mScroller.abortAnimation();
		}

		boolean isFlinging() {
			return !mScroller.isFinished();
		}

		void forceFinished() {
			if (!mScroller.isFinished()) {
				mScroller.forceFinished(true);
			}
		}
	}

	public void setListener(ScrollListener l){
		this.l=l;
	}
}