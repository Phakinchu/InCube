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

import static com.cwm.incube.R.id.map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double area=0.0;
    List<LatLng> listLatLng = new ArrayList<>();
    List<Marker> listMarker = new ArrayList<>();
    Polyline polyline;
    Polygon polygon;
    TextView areatext ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkPermission();
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);
        Button _toinputprogram =(Button)findViewById(R.id.button4) ;
        _toinputprogram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x =new Intent(getApplicationContext(),Programinput.class) ;
                startActivity(x);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        showCurrentLocation();
        addMarkerOnMapLongClick();
        MarkerOnClick();
        onMarkerDrag();
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

    private void addMarkerOnMapLongClick() {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            public void onMapLongClick(LatLng latLng) {
                if(polygon==null){
                    addMarker(latLng);
                }
            }
        });
    }

    private void clearPolyline(){
        if(polyline != null){
            polyline.remove();
        }
    }

    private void addMarker(LatLng latLng){
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latLng.latitude, latLng.longitude))
                .draggable(true));
        listLatLng.add(latLng);
        listMarker.add(marker);
        clearPolyline();
        polyline = mMap.addPolyline(new PolylineOptions().addAll(listLatLng)
                .color(Color.argb(255,0,175,0)));
    }

    private void MarkerOnClick(){
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            public boolean onMarkerClick(Marker marker) {
                if(listLatLng.get(0).equals(marker.getPosition())){
                    addPolygon();
                    computeArea();
                    DecimalFormat df = new DecimalFormat("#.##");
                    area = Double.valueOf(df.format(area));
                    areatext  = (TextView)findViewById(R.id.textView5);
                    areatext.setText(String.valueOf(area));
                    return false;
                }else{
                    deleteMarker(marker);
                    return true;
                }
            }
        });
    }

    private void addPolygon(){
        mMap.clear();
        polyline.remove();
        PolygonOptions polygonOptions = new PolygonOptions();
        polygon = mMap.addPolygon(polygonOptions.addAll(listLatLng)
                .strokeWidth((float)3.5)
                .strokeColor(Color.argb(255,0,175,0))
                .fillColor(Color.argb(30,0,255,0)));
        //addRandomTree(25,50);
        addGridTree(1.5);
    }

    private void addTree(double radius ,double lat,double lng,int r,int g,int b){
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(lat, lng))
                .radius(radius)
                .strokeColor(Color.argb(0,0,0,0))
                .fillColor(Color.argb(40,r,g,b)));
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(lat, lng))
                .radius(0.5)
                .strokeColor(Color.argb(0,0,0,0))
                .fillColor(Color.argb(255,r,g,b)));
    }

    private void addRandomTree(double radius ,int min,int max){
        Random rand = new Random();
        int treeCount = rand.nextInt(max)+min;
        LatLng endPointNE = endPointNE();
        LatLng endPointSW = endPointSW();
        for (int i = 0; i < treeCount; i++) {
            Log.d("DebugTag", "treeCount: " + i);
            while (true){
                Random position = new Random();
                double treeLat = (position.nextDouble()*(endPointNE.latitude-endPointSW.latitude))+endPointSW.latitude;
                double treeLng = (position.nextDouble()*(endPointNE.longitude-endPointSW.longitude))+endPointSW.longitude;
                Log.d("DebugTag", "LatLng: " + treeLat+","+treeLng);
                LatLng treeLatLng = new LatLng(treeLat,treeLng);
                if(PolyUtil.containsLocation(treeLatLng, listLatLng, true)){
                    Log.d("DebugTag", "treeLatLng: " + treeLatLng);
                    addTree(radius ,treeLat,treeLng,255,0,0);
                    break;
                }
            }

        }
    }

    private void addGridTree(double radius ){
        LatLng endPointNE = endPointNE();
        LatLng endPointSW = endPointSW();
        for (double i = endPointNE.latitude - meterToRad(radius); i > endPointSW.latitude; i -= meterToRad(radius *2)) {
            for(double j = endPointSW.longitude + meterToRad(radius); j < endPointNE.longitude;j += meterToRad(radius *2)) {
                if (PolyUtil.containsLocation(new LatLng(i,j), listLatLng, true)) {
                    addTree(radius ,i,j, 255, 0, 0);
                }
            }
        }

//        while (true){
//            //Random position = new Random();
//            //double treeLat = (position.nextDouble()*(endPointNE.latitude-endPointSW.latitude))+endPointSW.latitude;
//            //double treeLng = (position.nextDouble()*(endPointNE.longitude-endPointSW.longitude))+endPointSW.longitude;
//
//            if(PolyUtil.containsLocation(referenceLatLng, listLatLng, true)){
//                break;
//            }
//        }
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

    private void computeArea(){
        area = SphericalUtil.computeArea(listLatLng);
        Log.d("DebugTag", "Area: " + Double.toString(area));

    }

    private void deleteMarker(Marker marker){
        marker.remove();
        listLatLng.remove(marker.getPosition());
        listMarker.remove(marker);
        clearPolyline();
        polyline = mMap.addPolyline(new PolylineOptions().addAll(listLatLng)
                .color(Color.argb(255,0,175,0)));
    }

    private void onMarkerDrag(){
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            int index;

            public void onMarkerDragStart(Marker marker) {
                index = listMarker.indexOf(marker);
                drag(marker);
            }

            public void onMarkerDrag(Marker marker) {
                drag(marker);
            }

            public void onMarkerDragEnd(Marker marker) {
                drag(marker);
            }

            private void drag(Marker marker){
                listLatLng.set(index, marker.getPosition());
                clearPolyline();
                polyline = mMap.addPolyline(new PolylineOptions().addAll(listLatLng)
                        .color(Color.argb(255,0,175,0)));
            }
        });
    }

}

