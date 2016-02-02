package com.bignerdranch.android.draganddraw;

import android.graphics.PointF;

/**
 * Created by saurabh on 2/2/16.
 */
public class Box {

    private PointF origin;
    private PointF current;

    public Box(PointF origin) {
        this.origin = origin;
        this.current = origin;
    }

    public PointF getOrigin() {
        return origin;
    }

    public PointF getCurrent() {
        return current;
    }

    public void setCurrent(PointF current) {
        this.current = current;
    }
}
