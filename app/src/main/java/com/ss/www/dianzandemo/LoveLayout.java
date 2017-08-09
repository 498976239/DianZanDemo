package com.ss.www.dianzandemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * Created by SS on 17-7-21.
 */
public class LoveLayout extends RelativeLayout {
    private Drawable red;
    private Drawable purple;
    private Drawable green;
    private Drawable xi;
    private Drawable[] drawables;
    private int dWidth;
    private int dHeight;
    private int mWidth;
    private int mHeight;
    private LayoutParams params;
    private Random mRandom = new Random();//创建一个随机类
//不能单单使用没有AttributeSet参数的构造方法
    public LoveLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        drawables = new Drawable[3];
        red = getResources().getDrawable(R.drawable.ic_favorite_red_a700_48dp);
        purple = getResources().getDrawable(R.drawable.ic_favorite_purple_700_48dp);
        green = getResources().getDrawable(R.drawable.ic_favorite_green_600_48dp);
        xi = getResources().getDrawable(R.drawable.aaa);
        drawables[0] = red ;
        drawables[1] = purple ;
        drawables[2] = green ;
        //得到图片的实际宽高,但是这个时候图片还没有显示出来，所以拿不到这两个属性，所以在onMeasure()里拿
        dWidth = red.getIntrinsicWidth();
        dHeight = red.getIntrinsicHeight();
        //初始化params
        params = new LayoutParams(dWidth,dHeight);
        params.addRule(CENTER_HORIZONTAL,TRUE);//让图片水平居中,动态布局
        params.addRule(ALIGN_PARENT_BOTTOM,TRUE);//位于父控件的底部
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测绘--得到本layout的宽高
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    /**
     * 
     */
    public void addLove(){
        final ImageView iv = new ImageView(getContext());
        iv.setLayoutParams(params);
        iv.setImageDrawable(drawables[mRandom.nextInt(3)]);
        addView(iv);
        //使用属性动画控制坐标
        AnimatorSet set = getAnimator(iv);
        //设置一个监听
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //结束以后，将imageview移除
                removeView(iv);
            }
        });
        set.start();//开启动画集合
    }

    /**构造3个 动画属性
     * @param iv
     * @return
     */
    private AnimatorSet getAnimator(ImageView iv) {
        //Alpha动画
        ObjectAnimator alpha = ObjectAnimator.ofFloat(iv,"alpha",0.3f,1f);
        //缩放动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(iv,"scaleX",0.2f,1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(iv,"scaleY",0.2f,1f);
        AnimatorSet enter = new AnimatorSet();
        //设置时间
        enter.setDuration(500);
        //三个动画一起作用
        enter.playTogether(alpha,scaleX,scaleY);
        //作用在iv上
        enter.setTarget(iv);
        //贝塞尔曲线动画（核心就是不断的iv的坐标--point(x,y)）
        ValueAnimator bezierValueAnimator = getBezierValueAnimator(iv);//使用ObjectAnimator也可以
        AnimatorSet bezierSet = new AnimatorSet();
        bezierSet.playSequentially(enter,bezierValueAnimator);//先执行center在执行bezierValueAnimator动画，序列执行
       // bezierSet.setDuration(3000);//不用设置时间，因为时间是由两个动画一起加起来决定的
       // bezierSet.setInterpolator(new AccelerateInterpolator());//加速器
        bezierSet.setTarget(iv);
        return bezierSet;
    }

    private ValueAnimator getBezierValueAnimator(final ImageView iv) {
        //构造一个贝塞尔曲线动画
        PointF pointF2 = getPointF(2);
        PointF pointF1 = getPointF(1);
        PointF pointF0 = new PointF((mWidth-dWidth)/2 ,mHeight-dHeight);
        PointF pointF3 = new PointF(mRandom.nextInt(mWidth),0);
        //估值器
        BazierEvaluator mBazierEvaluator = new BazierEvaluator(pointF1,pointF2);
        //属性动画不仅可以改变view的属性，还可以改变自定义的属性
        ValueAnimator mValueAnimator = ValueAnimator.ofObject(mBazierEvaluator,pointF0,pointF3);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //估值器通过计算，然后通过动画属性的监听，就可以得到坐标
                PointF pointF = (PointF) animation.getAnimatedValue();
                iv.setX(pointF.x);
                iv.setY(pointF.y);
                //得到百分比；来控制透明度
                iv.setAlpha(1-animation.getAnimatedFraction());
            }
        });
        mValueAnimator.setTarget(iv);
        mValueAnimator.setDuration(3000);
        return mValueAnimator;
    }

    private PointF getPointF(int i) {
        PointF pointF = new PointF();
        pointF.x = mRandom.nextInt(mWidth);
        pointF.y = mRandom.nextInt(mHeight);
        //为了好看,要保证point1 > point2
        if(i == 2){
            pointF.y = mRandom.nextInt(mHeight/2);
        }else{
            pointF.y = mRandom.nextInt(mHeight/2)+mHeight/2;
        }
        return pointF;
    }
}
