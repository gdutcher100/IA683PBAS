package com.IA683.pbas.Helpers;

import android.view.MotionEvent;
import android.view.View;

public abstract class TouchTimer implements View.OnTouchListener {

    private long touchStart = 0l;
    private long touchEnd = 0l;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.touchStart = System.currentTimeMillis();
                return true;

            case MotionEvent.ACTION_UP:
                this.touchEnd = System.currentTimeMillis();
                long touchTime = this.touchEnd - this.touchStart;
                onTouchEnded(touchTime);
                return true;

            case MotionEvent.ACTION_MOVE:
                return true;

            default:
                return false;
        }
    }

    protected abstract void onTouchEnded(long touchTimeInMillis);
}
