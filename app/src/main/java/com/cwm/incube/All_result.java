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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
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
import com.cwm.incube.Price;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class All_result extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    private ScrollView mScrollView;
    List<LatLng> listLatLng = new ArrayList<>();
    List<Circle> listMainCircle = new ArrayList<>();
    List<Circle> listSubCircle = new ArrayList<>();
    List<Circle> listCircleRadius = new ArrayList<>();
    Polygon polygon;
    Price price = new Price();
    String mainTree = "";
    double mainRadius=0 , subRadius=0 ,costsub = 0;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkPermission();
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        setContentView(R.layout.activity_all_result);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);
        CustomScrollView myScrollView = (CustomScrollView) findViewById(R.id.scroll);
        Intent intent = getIntent();
        String x = intent.getStringExtra("main_tree");
        String y = intent.getStringExtra("third_tree");
        TextView main = (TextView) findViewById(R.id.maintree) ;
        TextView sub = (TextView) findViewById(R.id.subtree) ;
        main.setText(x);
        sub.setText(y);

        Button _tomain =(Button)findViewById(R.id.tomain) ;
        _tomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x =new Intent(getApplicationContext(),MainActivity.class) ;
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
        Bundle data = getIntent().getExtras();
        double[] lat = data.getDoubleArray("lat");
        double[] lng = data.getDoubleArray("lng");
        for(int i =0 ; i<lat.length;i++){
            listLatLng.add(new LatLng(lat[i],lng[i]));
        }
        if (listLatLng.size() != 0) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(listLatLng.get(0), 19));
        }
        addPolygon();
        computeGridRadius();

        double fund = Double.parseDouble(getIntent().getStringExtra("Cost"));
        double cost = listMainCircle.size()*price.getPrice(mainTree);

        if(fund>=(cost+costsub)){
            TextView main = (TextView) findViewById(R.id.cost) ;
            main.setText("พอต่อการใช้เพาะปลูกในพื้นที่นี้");
            TextView urcost = (TextView) findViewById(R.id.yourcost) ;
            urcost.setText("     ต้นทุนที่ต้องใช้ "+(cost+costsub)+" บาท ต้นทุนของท่าน "+fund+" บาท ไม่เกินงบประมาณ");
            Log.d("DebugTag", "is fund enough : YES.\nCost : " + (cost+costsub));
        }else {
            TextView main = (TextView) findViewById(R.id.cost) ;
            main.setText("ไม่พอต่อการใช้เพาะปลูกในพื้นที่นี้");
            TextView urcost = (TextView) findViewById(R.id.yourcost) ;
            urcost.setText("     ต้นทุนที่ต้องใช้ "+(cost+costsub)+" บาท ต้นทุนของท่านคือ "+fund+" บาท ซึ่งไม่เพียงพอ");
            Log.d("DebugTag", "is fund enough : NO.\nCost : " + (cost+costsub));
        }
        TextView mainnum = (TextView) findViewById(R.id.numbermaintree) ;
        mainnum.setText(listMainCircle.size()+" ต้น");
        TextView subnum = (TextView) findViewById(R.id.numbersubtree) ;
        subnum.setText(listSubCircle.size()+" จุด");
        TextView mainradian = (TextView) findViewById(R.id.radian) ;
        mainradian.setText(subRadius + " เมตร");

        String soil = getIntent().getStringExtra("RadioButton");
        if(soil.equals("ดินร่วน")){
            TextView ursoil = (TextView) findViewById(R.id.yoursoil) ;
            ursoil.setText(soil +" ซึ่งเหมาะสมกับการเพาะปลูก");
        }
        else{
            TextView ursoil = (TextView) findViewById(R.id.yoursoil) ;
            ursoil.setText(soil +" ซึ่งไม่เหมาะสมกับการเพาะปลูก ควรปรับผิวดินให้เป็น ดินร่วน");
        }

        if(mainRadius == 1.5){
            TextView cal = (TextView) findViewById(R.id.calculate) ;
            cal.setText("     พืชหลักของท่านคือ มะม่วง ซึ่งใช้เวลาในการเติบโตตั้งแต่ต้นถึงออกผลประมาณ 5 ปี ในช่วงระหว่างนี้สามารถเก็บเกี่ยวพืชปลูกเเซม");
            String subTree = getIntent().getStringExtra("third_tree");
            if(subTree.equals("พริกไทย")){
                cal.append("(พริกไทย) ใช้เวลาปลูก 1ปีครึ่ง หลังจากนั้นจึงจะสามารถเก็บเกี่ยวได้ ซึ่งสามารถเก็บเกี่ยวได้ประมาณ 9-10 ครั้ง หลังจากนั้นท่านต้องงดการปลูกพริกไทยเพื่อให้ต้นมะม่วงมีสารอาหารที่เพียงพอสำหรับการออกผล");
            }else if(subTree.equals("ผักกูด")){
                cal.append("(ผักกูด) หากปลูกพร้อมมะม่วงจะใช้เวลาเจริญเติบโตอีก 6 เดือน หลังจากนั้นจะเก็บเกี่ยวได้ทุก 3-4 วัน ซึ่งสามารถเก็บเกี่ยวได้ 500 กว่าครั้ง (หากสภาพอากาศคงที่) ");
            }else if(subTree.equals("ตะไคร้")){
                cal.append("(ตะไคร้) หากปลูกพร้อมมะม่วงจะใช้เวลาเจริญเติบโตอีกประมาณ 7 เดือน จากนั้นสามารถเก็บเกี่ยวได้ทันที หากทำอย่างต่อเนื่องสามารถเก็บเกี่ยวได้ประมาณ 8-9 ครั้ง  ");
            }else if(subTree.equals("คะน้า")){
                cal.append("(คะน้า) หากปลูกพร้อมมะม่วงจะใช้เวลาเจริญเติบโตอีกประมาณ 2 เดือน จากนั้นสามารถเก็บเกี่ยวได้ทันที หากทำอย่างต่อเนื่องสามารถเก็บเกี่ยวได้ประมาณ 20-25 ครั้ง  ");
            }else if(subTree.equals("ผักบุ้งจีน")){
                cal.append("(ผักบุ้งจีน) หากปลูกพร้อมมะม่วงจะใช้เวลาเจริญเติบโตอีกประมาณ 1 เดือน จากนั้นสามารถเก็บเกี่ยวได้ทันที หากทำอย่างต่อเนื่องสามารถเก็บเกี่ยวได้ประมาณ 50-55 ครั้ง  ");
            }
            TextView water = (TextView) findViewById(R.id.wateruse) ;
            water.setText("     ควรใช้น้ำต่อไร่มะม่วงในประมาณ "+(listMainCircle.size()*22.5)+" ลิตรต่อวันในพื้นที่นี้ แต่หากมะม่วงติดผลแล้วควรใช้น้ำประมาณ "+(listMainCircle.size()*60)+" ลิตรต่อวันต่อพื้นที่นี้");
        }
        else if(mainRadius == 4){
            TextView cal = (TextView) findViewById(R.id.calculate) ;
            cal.setText("     พืชหลักของท่านคือ ลำไย ซึ่งใช้เวลาในการเติบโตตั้งแต่ต้นถึงออกผลประมาณ 7 ปี ในช่วงระหว่างนี้สามารถเก็บเกี่ยวพืชปลูกเเซม");
            String subTree = getIntent().getStringExtra("third_tree");
            if(subTree.equals("พริกไทย")){
                cal.append("(พริกไทย) ใช้เวลาปลูก 1ปีครึ่ง หลังจากนั้นจึงจะสามารถเก็บเกี่ยวได้ ซึ่งสามารถเก็บเกี่ยวได้ประมาณ 18-20 ครั้ง หลังจากนั้นท่านต้องงดการปลูกพริกไทยเพื่อให้ต้นมะม่วงมีสารอาหารที่เพียงพอสำหรับการออกผล");
            }else if(subTree.equals("ผักกูด")){
                cal.append("(ผักกูด) หากปลูกพร้อมมะม่วงจะใช้เวลาเจริญเติบโตอีก 6 เดือน หลังจากนั้นจะเก็บเกี่ยวได้ทุก 3-4 วัน ซึ่งสามารถเก็บเกี่ยวได้เกือบ 700 กว่าครั้ง (หากสภาพอากาศคงที่) ");
            }else if(subTree.equals("ตะไคร้")){
                cal.append("(ตะไคร้) หากปลูกพร้อมมะม่วงจะใช้เวลาเจริญเติบโตอีกประมาณ 7 เดือน จากนั้นสามารถเก็บเกี่ยวได้ทันที หากทำอย่างต่อเนื่องสามารถเก็บเกี่ยวได้ประมาณ 12 ครั้ง  ");
            }else if(subTree.equals("คะน้า")){
                cal.append("(คะน้า) หากปลูกพร้อมมะม่วงจะใช้เวลาเจริญเติบโตอีกประมาณ 2 เดือน จากนั้นสามารถเก็บเกี่ยวได้ทันที หากทำอย่างต่อเนื่องสามารถเก็บเกี่ยวได้ประมาณ 38-40 ครั้ง  ");
            }else if(subTree.equals("ผักบุ้งจีน")){
                cal.append("(ผักบุ้งจีน) หากปลูกพร้อมมะม่วงจะใช้เวลาเจริญเติบโตอีกประมาณ 1 เดือน จากนั้นสามารถเก็บเกี่ยวได้ทันที หากทำอย่างต่อเนื่องสามารถเก็บเกี่ยวได้ประมาณ 80 ครั้ง  ");
            }
            TextView water = (TextView) findViewById(R.id.wateruse) ;
            water.setText("  ควรใช้น้ำต่อไร่ลำไยในประมาณ "+(listMainCircle.size()*20)+" ลิตรต่อวันในพื้นที่นี้ แต่หากมะม่วงติดผลแล้วควรใช้น้ำประมาณ "+(listMainCircle.size()*50)+" ต่อวันต่อพื้นที่นี้");
        }

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
        if(treeSize<0.5){
            listSubCircle.add(treePoint);
        }else{
            listMainCircle.add(treePoint);
        }
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
            if(mainTree.equals("มะม่วง")){
                this.mainTree = "mango";
                mainRadius = 1.5;
            }else if(mainTree.equals("ลำไย")){
                this.mainTree = "longan";
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
            if(subTree.equals("พริกไทย")){
                TextView dotpertree = (TextView) findViewById(R.id.subtreeperdot) ;
                dotpertree.setText("2ต้น/1จุด");
                TextView numsubtree = (TextView) findViewById(R.id.numbersubtree) ;
                numsubtree.setText("ประมาณ "+ 2*listSubCircle.size()+" ต้น");
                costsub = (2*listSubCircle.size())*2 ;
            }else if(subTree.equals("ผักกูด")){
                TextView dotpertree = (TextView) findViewById(R.id.subtreeperdot) ;
                dotpertree.setText("5ต้น/1จุด");
                TextView numsubtree = (TextView) findViewById(R.id.numbersubtree) ;
                numsubtree.setText("ประมาณ "+ 5*listSubCircle.size()+" ต้น");
                costsub = (5*listSubCircle.size())*4 ;
            }else if(subTree.equals("ตะไคร้")){
                TextView dotpertree = (TextView) findViewById(R.id.subtreeperdot) ;
                dotpertree.setText("20ต้น/1จุด");
                TextView numsubtree = (TextView) findViewById(R.id.numbersubtree) ;
                numsubtree.setText("ประมาณ "+ 20*listSubCircle.size()+" ต้น");
                costsub = 0 ;
            }else if(subTree.equals("คะน้า")){
                TextView dotpertree = (TextView) findViewById(R.id.subtreeperdot) ;
                dotpertree.setText("40ต้น/1จุด");
                TextView numsubtree = (TextView) findViewById(R.id.numbersubtree) ;
                numsubtree.setText("ประมาณ "+ 40*listSubCircle.size()+" ต้น");
                costsub = (40*listSubCircle.size())*0.01 ;
            }else if(subTree.equals("ผักบุ้งจีน")){
                TextView dotpertree = (TextView) findViewById(R.id.subtreeperdot) ;
                dotpertree.setText("80ต้น/1จุด");
                TextView numsubtree = (TextView) findViewById(R.id.numbersubtree) ;
                numsubtree.setText("ประมาณ "+ 80*listSubCircle.size()+" ต้น");
                costsub = (40*listSubCircle.size())*0.03 ;
            }
        }
    }

    private void clearCircle(){
        for (int i = 0; i < listMainCircle.size(); i++) {
            listMainCircle.get(i).remove();
        }
        for (int i = 0; i < listSubCircle.size(); i++) {
            listSubCircle.get(i).remove();
        }
        for (int i = 0; i < listCircleRadius.size(); i++) {
            listCircleRadius.get(i).remove();
        }
    }

}

