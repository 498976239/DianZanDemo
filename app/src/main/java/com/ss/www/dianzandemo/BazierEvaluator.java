package com.ss.www.dianzandemo;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * Created by SS on 17-7-21.
 */
public class BazierEvaluator implements TypeEvaluator<PointF> {
    PointF pointF1;
    PointF pointF2;

    public BazierEvaluator(PointF pointF1, PointF pointF2) {
        this.pointF1 = pointF1;
        this.pointF2 = pointF2;
    }

    @Override
    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
        //fraction百分比，0~1
        PointF point = new PointF();
        point.x =  startValue.x*(1-fraction)*(1-fraction)*(1-fraction)
                +3*pointF1.x*fraction*(1-fraction)*(1-fraction)
                +3*pointF2.x*fraction*fraction*(1-fraction)
                +endValue.x*fraction*fraction;
        point.y =  startValue.y*(1-fraction)*(1-fraction)*(1-fraction)
                +3*pointF1.y*fraction*(1-fraction)*(1-fraction)
                +3*pointF2.y*fraction*fraction*(1-fraction)
                +endValue.y*fraction*fraction*fraction;
        return point;
    }
}
