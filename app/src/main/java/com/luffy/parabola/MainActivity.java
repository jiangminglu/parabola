package com.luffy.parabola;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //分300步进行移动动画
    int count = 300;
    private float a = -1f / 75f;
    private float b = 0;
    private float c = 0;

    private int num = 0;
    private RelativeLayout mainContentLayout;
    private ImageView cartImg,itemImg;
    private Button addBtn;
    private TextView numTv;
    private Context mContext = this;
    ImageView animImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mainContentLayout = (RelativeLayout) findViewById(R.id.main_content_layout);
        itemImg = (ImageView) findViewById(R.id.item_img);
        cartImg = (ImageView) findViewById(R.id.cart_img);
        addBtn = (Button) findViewById(R.id.add_btn);
        numTv = (TextView) findViewById(R.id.num_tv);

        addAnimImg();

        addBtn.setOnClickListener(this);
    }

    private void addAnimImg() {
        animImg = new ImageView(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        Drawable drawable = itemImg.getBackground();
        animImg.setImageDrawable(drawable);
        animImg.setBackgroundColor(Color.BLACK);
        animImg.setVisibility(View.INVISIBLE);
        animImg.setLayoutParams(params);
        mainContentLayout.addView(animImg);
    }

    @Override
    public void onClick(View v1) {


        num++;
        //获取顶点坐标
        int[] locationCenter = new int[2];
        animImg.getLocationInWindow(locationCenter);

        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int actionbarH = 0;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && actionBar.isShowing()) {
            actionbarH = actionBar.getHeight();
        }
        locationCenter[1] = locationCenter[1] - statusBarHeight - actionbarH;
        Log.e("location_center", "(" + locationCenter[0] + "," + locationCenter[1] + ")");

        int[] locationEnd = new int[2];
        cartImg.getLocationInWindow(locationEnd);
        locationEnd[0]=locationEnd[0]+40;//这里的40是一个偏移量,为了保证落入购物车中
        locationEnd[1] = locationEnd[1] - cartImg.getLayoutParams().height-60;//这里的60是一个偏移量,为了保证落入购物车中
        Log.e("location_end", "(" + locationEnd[0] + "," + locationEnd[1] + ")");


        int[] locationStart = new int[2];
        locationStart[0] = locationCenter[0] - (locationEnd[0] - locationCenter[0]);
        locationStart[1] = locationEnd[1];
        Log.e("location_start", "(" + locationStart[0] + "," + locationStart[1] + ")");
        final int[][] points = {locationStart, locationCenter, locationEnd};

        count = locationEnd[0] - locationCenter[0];
        calculate(points);
        startAnimation(animImg, locationCenter[0]);
    }


    /**
     * 开始动画
     *
     * @param imageView
     * @param start     这里使用start 主要是为了计算y轴的位移.
     */
    private void startAnimation(final View imageView, int start) {

        Keyframe[] keyXframes = new Keyframe[count];
        Keyframe[] keyYframes = new Keyframe[count];
        final float keyStep = 1f / (float) count;
        float key = keyStep;
        for (int i = 0; i < count; ++i) {
            int x = i + 1;
            float y = getY(x + start);
            keyXframes[i] = Keyframe.ofFloat(key, x);
            keyYframes[i] = Keyframe.ofFloat(key, y);
            key += keyStep;
        }
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofKeyframe("translationX", keyXframes);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofKeyframe("translationY", keyYframes);

        ObjectAnimator yxBouncer = ObjectAnimator.ofPropertyValuesHolder(imageView, pvhY, pvhX).setDuration(16000);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(imageView, "rotation", 0F, 360F);
        rotation.setDuration(20).setRepeatMode(ValueAnimator.INFINITE);

        yxBouncer.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                imageView.setVisibility(View.VISIBLE);
                addBtn.setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (num > 0) {
                    numTv.setVisibility(View.VISIBLE);
                    numTv.setText(num + "");
                }
                mainContentLayout.removeView(animImg);
                animImg.clearAnimation();
                addAnimImg();
                addBtn.setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(yxBouncer,rotation);
        animatorSet.setDuration(16000);
        animatorSet.start();


    }


    /**
     * 这里是根据三个坐标点{（0,0），（300,0），（150,300）}计算出来的抛物线方程
     *
     * @param x
     * @return
     */
    private float getY(float x) {

        float y = a * x * x + b * x + c;
        Log.e("point", x + " : " + y);
        return y;
    }

    /**
     * 计算抛物线公式
     *
     * @param points
     */
    private void calculate(int[][] points) {
        float x1 = points[0][0];
        float y1 = points[0][1];
        float x2 = points[1][0];
        float y2 = points[1][1];
        float x3 = points[2][0];
        float y3 = points[2][1];

        a = (y1 * (x2 - x3) + y2 * (x3 - x1) + y3 * (x1 - x2))
                / (x1 * x1 * (x2 - x3) + x2 * x2 * (x3 - x1) + x3 * x3 * (x1 - x2));
        b = (y1 - y2) / (x1 - x2) - a * (x1 + x2);
        c = y1 - (x1 * x1) * a - x1 * b;

        Log.e("系数", "a:" + a + " b:" + b + " c:" + c);
    }
}
