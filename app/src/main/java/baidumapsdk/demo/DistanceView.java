package baidumapsdk.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DistanceView extends LinearLayout {

    private TextView mDistanceView;

    public DistanceView(Context context) {
        super(context);
        initView(null);
    }

    public DistanceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        inflate(getContext(), R.layout.distance_view, this);
        mDistanceView = findViewById(R.id.tv_distance);
    }

    public void setDistance(double distance) {
        int distanceInt = (int) distance;
        mDistanceView.setText(String.valueOf(distanceInt) + "m");
    }
}
