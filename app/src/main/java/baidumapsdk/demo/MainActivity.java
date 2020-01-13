package baidumapsdk.demo;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements BaiduMap.OnMapLoadedCallback, BaiduMap.OnMapStatusChangeListener, BaiduMap.OnMarkerClickListener, BaiduMap.OnMapClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private List<MarkerOptions> pointList = new LinkedList<>();
    private List<PolylineOptions> lineList = new LinkedList<>();
    private double sum;
    private TextView mRulerTextView;
    private boolean ruleMode;
    private RelativeLayout tool;
    private ImageView close;
    private ImageView back;
    private ImageView clean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();

        mRulerTextView = findViewById(R.id.ruler);
        tool = findViewById(R.id.ruler_tool);
        close = findViewById(R.id.ruler_close);
        clean = findViewById(R.id.ruler_clean);
        back = findViewById(R.id.ruler_back);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mMapView.showZoomControls(false);
        mBaiduMap.getUiSettings().setRotateGesturesEnabled(false);
        //地图加载完的监听
        mBaiduMap.setOnMapLoadedCallback(this);
        mRulerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ruleMode = true;
                mRulerTextView.setVisibility(View.GONE);
                tool.setVisibility(View.VISIBLE);
                clean();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiduMap.clear();
                ruleMode = false;
                mRulerTextView.setVisibility(View.VISIBLE);
                tool.setVisibility(View.GONE);
                clean();
            }
        });
        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiduMap.clear();
                clean();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

    }

    private void back() {
        if (lineList.size() > 0) {
            MarkerOptions lastPoint = pointList.get(pointList.size() - 1);
            PolylineOptions lastLine = lineList.get(lineList.size() - 1);

            lastPoint.visible(false);
            lastLine.visible(false);
            pointList.remove(lastPoint);
            lineList.remove(lastLine);
        }
    }


    @Override
    public void onMapLoaded() {
        //地图状态变化监听
        mBaiduMap.setOnMapStatusChangeListener(this);
        //Marker 被点击监听
        mBaiduMap.setOnMarkerClickListener(this);
        //点击地图监听
        mBaiduMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        Log.e(TAG, "onMapStatusChangeFinish zoom: " + mapStatus.zoom);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.e(TAG, "onMapClick LatLng: " + latLng.toString());

        if (!ruleMode) {
            return;
        }

        pointList.add(drawPoint(latLng));

        if (pointList.size() > 1) {
            MarkerOptions lastPoint = pointList.get(pointList.size() - 1);
            lineList.add(drawLine(pointList.get(pointList.size() - 2).getPosition(), lastPoint.getPosition()));


            PolylineOptions polylineOptions = lineList.get(lineList.size() - 1);
            Bundle bundle = polylineOptions.getExtraInfo();
            Double d = bundle.getDouble("m");

            sum += d;

            DistanceView distanceView = new DistanceView(this);
            distanceView.setDistance(sum);

            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(distanceView);
            MarkerOptions markerOptions = new MarkerOptions().position(lastPoint.getPosition()).icon(bitmapDescriptor);
            mBaiduMap.addOverlay(markerOptions);
        }

    }

    private void clean() {
        mBaiduMap.clear();
        lineList.clear();
        pointList.clear();
    }

    private MarkerOptions drawPoint(LatLng point) {
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.ic_point);
        MarkerOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        mBaiduMap.addOverlay(option);
        return option;
    }

    private PolylineOptions drawLine(LatLng start, LatLng end) {
        List<LatLng> line = new ArrayList<>();
        line.add(start);
        line.add(end);

        PolylineOptions polylineOptions = new PolylineOptions()
                .width(3)
                .color(0xAAFF0000)
                .points(line);
        mBaiduMap.addOverlay(polylineOptions);
        Bundle bundle = new Bundle();
        bundle.putDouble("m", DistanceUtil.getDistance(start, end));
        polylineOptions.extraInfo(bundle);
        return polylineOptions;
    }

    @Override
    public void onMapPoiClick(MapPoi mapPoi) {

    }

    private void requestPermission() {
        String[] perms = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET
        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "运行时权限请求",
                    1, perms);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
