
/*
 *Created by Beijing ASU Tech Co.Ltd
 *Copyright (c) 2017. All right reserved.
 */

package com.yishu.asumap;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * Handle activity gesture slide
 */
public class AsuSlidingActivity extends Activity {
    private AsuSlidingLayout mRootView;// gesture container

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView = new AsuSlidingLayout(this);
        mRootView.bindActivity(this);
    }

    /**
     * To solve the conflict between up and down sliding
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // activity slide(app exit)
        mRootView.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }
}
