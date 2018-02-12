package com.cwm.incube;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.cwm.incube.R.id.maintree;
import static com.cwm.incube.R.id.map;

public class Result extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    List<LatLng> listLatLng = new ArrayList<>();
    List<Circle> listCircle = new ArrayList<>();
    List<Circle> listCircleRadius = new ArrayList<>();
    Polygon polygon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkPermission();
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        setContentView(R.layout.activity_result);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        showCurrentLocation();
        Bundle data = getIntent().getExtras();
        double[] lat = data.getDoubleArray("lat");
        double[] lng = data.getDoubleArray("lng");
        for(int i =0 ; i<lat.length;i++){
            listLatLng.add(new LatLng(lat[i],lng[i]));
        }
        addPolygon();
        computeGridRadius();

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if (location != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
            }
        }
    }


    private void addPolygon(){
        mMap.clear();
        PolygonOptions polygonOptions = new PolygonOptions();
        polygon = mMap.addPolygon(polygonOptions.addAll(listLatLng)
                .strokeWidth((float)3.5)
                .strokeColor(Color.argb(255,0,175,0))
                .fillColor(Color.argb(30,0,255,0)));
    }

    private void addTree(double treeSize,double radius ,double lat,double lng,int r,int g,int b){
        Circle treePoint = mMap.addCircle(new CircleOptions()
                .center(new LatLng(lat, lng))
                .radius(radius)
                .strokeColor(Color.argb(0,0,0,0))
                .fillColor(Color.argb(40,r,g,b)));
        Circle treeRadius = mMap.addCircle(new CircleOptions()
                .center(new LatLng(lat, lng))
                .radius(treeSize)
                .strokeColor(Color.argb(0,0,0,0))
                .fillColor(Color.argb(255,r,g,b)));
        listCircle.add(treePoint);
        listCircleRadius.add(treeRadius);

    }


    private void addGridTree(double mainRadius,double subRadius ){
        LatLng endPointNE = endPointNE();
        LatLng endPointSW = endPointSW();
        for (double i = endPointNE.latitude - meterToRad(mainRadius); i > endPointSW.latitude; i -= meterToRad(mainRadius *2)) {
            for(double j = endPointSW.longitude + meterToRad(mainRadius); j < endPointNE.longitude;j += meterToRad(mainRadius *2)) {
                if (PolyUtil.containsLocation(new LatLng(i,j), listLatLng, true)) {
                    addTree(0.5,mainRadius ,i,j, 255, 0, 0);
                }
            }
        }

        for (int direct = 0 ; direct <= 1 ; direct++) {
            for (double i = endPointNE.latitude - meterToRad(((direct==0)? mainRadius:subRadius)*2); i > endPointSW.latitude; i -= meterToRad(((direct==0)? mainRadius:subRadius)*2)) {
                for (double j = endPointSW.longitude + meterToRad(((direct==0)? subRadius:mainRadius)*2); j < endPointNE.longitude; j += meterToRad(((direct==0)? subRadius:mainRadius)*2)) {
                    if (PolyUtil.containsLocation(new LatLng(i, j), listLatLng, true)) {
                        addTree(0.3,subRadius, i, j, 255, 255, 0);
                    }
                }
            }
        }
    }

    private double meterToRad(double m){
        return m*0.00000899;
    }

    private LatLng endPointNE(){
        LatLng endPointN = listLatLng.get(0);
        LatLng endPointE = listLatLng.get(0);
        for (int i = 1; i < listLatLng.size(); i++) {
            if(listLatLng.get(i).latitude>endPointN.latitude){
                endPointN=listLatLng.get(i);
            }
            if(listLatLng.get(i).longitude>endPointE.longitude){
                endPointE=listLatLng.get(i);
            }
        }return new LatLng(endPointN.latitude,endPointE.longitude);
    }

    private LatLng endPointSW(){
        LatLng endPointS = listLatLng.get(0);
        LatLng endPointW = listLatLng.get(0);
        for (int i = 1; i < listLatLng.size(); i++) {
            if(listLatLng.get(i).latitude<endPointS.latitude){
                endPointS=listLatLng.get(i);
            }
            if(listLatLng.get(i).longitude<endPointW.longitude){
                endPointW=listLatLng.get(i);
            }
        }return new LatLng(endPointS.latitude,endPointW.longitude);
    }

    public void computeGridRadius() {
        if (polygon!=null) {
            clearCircle();
            String mainTree = getIntent().getStringExtra("main_tree");
            String subTree = getIntent().getStringExtra("third_tree");
            double mainRadius=0;
            double subRadius=0;
            if(mainTree.equals("มะม่วง")){
                mainRadius = 1.5;
            }else if(mainTree.equals("ลำไย")){
                mainRadius = 4;
            }
            if(subTree.equals("พริกไทย")){
                subRadius = 0.75;
            }else if(subTree.equals("ผักกูด")){
                subRadius = 0.75;
            }else if(subTree.equals("ตะไคร้")){
                subRadius = 0.75;
            }else if(subTree.equals("คะน้า")){
                subRadius = 0.75;
            }else if(subTree.equals("ผักบุ้งจีน")){
                subRadius = 0.75;
            }
            addGridTree(mainRadius,subRadius);
        }
    }

    private void clearCircle(){
        for (int i = 0; i < listCircle.size(); i++) {
            listCircle.get(i).remove();
        }
        for (int i = 0; i < listCircleRadius.size(); i++) {
            listCircleRadius.get(i).remove();
        }
    }

}

