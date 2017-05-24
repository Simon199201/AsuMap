
/*
 *Created by Beijing ASU Tech Co.Ltd
 *Copyright (c) 2017. All right reserved.
 */

package com.yishu.asumap;

import android.app.Activity;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Handle activity sliding
 */
public class AsuSlidingLayout extends FrameLayout {
    private static final String TAG = "AsuSlidingLayout";
    private final static int ASU_MOTION_START_MARGIN_MAX = 35;// The maximum margin trigger slide
    private final static int ASU_MOTION_DISTANCE = 50;// Minimum distance of trigger slip
    private final static int MOTION_DURATION = 500;// animatioin duratioin time

    private Activity mActivity;
    private Scroller mScroller;// Scroller
    private int mScreenWidth = -1;// screen width
    private int mLastMoveX;// end x position
    private int mMotionStart;// start x position

    private static final int CLICK_AREA_MAX = 5;// click area max
    private int mLastXIntercept = 0;// last position x
    private int mLastYIntercept = 0;// last position y
    private int mFirstXIntercept = 0;// first position x

    public AsuSlidingLayout(Activity activity) {
        this(activity, null);
    }

    public AsuSlidingLayout(Activity activity, AttributeSet attrs) {
        this(activity, attrs, 0);
    }

    public AsuSlidingLayout(Activity activity, AttributeSet attrs,
                            int defStyleAttr) {
        super(activity, attrs, defStyleAttr);
        initView(activity);
    }

    /**
     * init view
     *
     * @param activity
     */
    private void initView(Activity activity) {
        mActivity = activity;
        mScroller = new Scroller(mActivity);
    }

    /**
     * bind Activity
     */
    public void bindActivity(Activity activity) {
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        View child = decorView.getChildAt(0);
        decorView.removeView(child);
        addView(child);
        decorView.addView(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mMotionStart = (int) event.getX();
                mScreenWidth = getWidth();
                Log.e(TAG, "onTouchEvent: ACTION_DOWN" );
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMotionStart > ASU_MOTION_START_MARGIN_MAX) {
                    return true;
                }
                // Move with gestures
                int rightMovedX = mLastMoveX - (int) event.getX();
                // Slide out screen
                if (getScrollX() + rightMovedX >= 0) {
                    scrollTo(0, 0);
                }
                // Trigger slip
                else if ((int) event.getX() > ASU_MOTION_START_MARGIN_MAX) {
                    scrollBy(rightMovedX, 0);
                }
                mLastMoveX = (int) event.getX();
                Log.e(TAG, "onTouchEvent: ACTION_MOVE" );

                break;
            case MotionEvent.ACTION_UP:
                if (mMotionStart > ASU_MOTION_START_MARGIN_MAX) {
                    return true;
                }
                Log.e("---)00000", event.getX() + "-" + mMotionStart + "===000 =" + (event.getX() - mMotionStart));
                if (event.getX() - mMotionStart > ASU_MOTION_DISTANCE) { //
                    scrollCloseGestures();
                } else {
                    scrollRestore();
                }
                Log.e(TAG, "onTouchEvent: ACTION_UP" );

                break;
            default:
        }
        return true;
    }

    /**
     * scroll to close
     */
    public void scrollClose() {
        mScreenWidth = getWidth();
        mScroller.startScroll(0, 0, -mScreenWidth, 0,
                MOTION_DURATION);
        invalidate();
    }

    /**
     * back, from current to left
     */
    private void scrollRestore() {
        int startX = getScrollX();
        int dx = -startX;
        mScroller.startScroll(startX, 0, dx, 0, MOTION_DURATION);
        invalidate();
    }

    /**
     * close, from current to right
     */
    private void scrollCloseGestures() {
        int startX = getScrollX();
        int dx = -startX - mScreenWidth;
        mScroller.startScroll(startX, 0, dx, 0, MOTION_DURATION);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();
        } else if (-getScrollX() == mScreenWidth) {
            Log.e("computerScrill--", "---走这个方法了");
            mActivity.finish();
        }
        super.computeScroll();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    /**
     * To solve the conflict between up and down sliding by delta (non-Javadoc)
     *
     * @see ViewGroup#onInterceptTouchEvent(MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercepted = false;
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // Don't intercept, handle by child view
                intercepted = false;
                mFirstXIntercept = (int) event.getX();
                Log.e(TAG, "onInterceptTouchEvent: ACTION_DOWN" + intercepted);

                break;
            }
            case MotionEvent.ACTION_MOVE: {
                // x delta value
                int deltaX = x - mLastXIntercept;
                // y delta value
                int deltaY = y - mLastYIntercept;
                // Solve the collision problem of click and slide
                if (Math.abs(deltaX) > Math.abs(deltaY)
                        && Math.abs(mFirstXIntercept - x) >= CLICK_AREA_MAX) {
                    // to intercept
                    intercepted = false;
                    Log.e(TAG, "onInterceptTouchEvent: ACTION_MOVE" + intercepted);
                } else {
                    intercepted = false;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                // Don't intercept, handle by child view
                intercepted = false;
                Log.e(TAG, "onInterceptTouchEvent: ACTION_UP" + intercepted);
                break;
            }
            default:
                break;
        }
        mLastXIntercept = x;
        mLastYIntercept = y;
        return intercepted;
    }
}
