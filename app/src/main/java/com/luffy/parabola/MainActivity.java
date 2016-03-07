package com.luffy.parabola;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private RelativeLayout mainContentLayout;
    private ImageView cartImg;
    private Button addBtn;
    private TextView numTv;
    private Context mContext = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init(){
        mainContentLayout = (RelativeLayout) findViewById(R.id.main_content_layout);
        cartImg = (ImageView) findViewById(R.id.cart_img);
        addBtn = (Button) findViewById(R.id.add_btn);
        numTv = (TextView) findViewById(R.id.num_tv);

        addBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        ImageView imageView = new ImageView(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        imageView.setLayoutParams(params);

        imageView.setVisibility(View.GONE);
        mainContentLayout.addView(imageView);


        int[] locationCenter = new int[2];
        v.getLocationInWindow(locationCenter);
        Log.e("location_center", "(" + locationCenter[0] + "," + locationCenter[1]+")");

        int[] locationEnd = new int[2];
        cartImg.getLocationInWindow(locationEnd);
        Log.e("location_end","("+locationEnd[0]+","+locationEnd[1]+")");


        int[] locationStart = new int[2];
        locationStart[0] = locationCenter[0]-(locationEnd[0]-locationCenter[0]);
        locationStart[1] = locationEnd[1];
        Log.e("location_start","("+locationStart[0]+","+locationStart[1]+")");
        final int[][] points = { locationStart, locationCenter,locationEnd };
        calculate(points);
    }

    /**
     * 计算抛物线公式
     * @param points
     */
    private static void calculate(int[][] points) {
        float x1 = points[0][0];
        float y1 = points[0][1];
        float x2 = points[1][0];
        float y2 = points[1][1];
        float x3 = points[2][0];
        float y3 = points[2][1];

        final float a = (y1 * (x2 - x3) + y2 * (x3 - x1) + y3 * (x1 - x2))
                / (x1 * x1 * (x2 - x3) + x2 * x2 * (x3 - x1) + x3 * x3 * (x1 - x2));
        final float b = (y1 - y2) / (x1 - x2) - a * (x1 + x2);
        final float c = y1 - (x1 * x1) * a - x1 * b;

        Log.e("系数","a:" + a + " b:" + b + " c" + c);
    }
}
