package com.cwm.incube;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Programinput extends AppCompatActivity  {

    String tree1,tree2,tree3 ;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        setContentView(R.layout.activity_programinput);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Button _tomain =(Button)findViewById(R.id.button3) ;
        _tomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x =new Intent(getApplicationContext(),MainActivity.class) ;
                startActivity(x);
            }
        });

        //drop down maintree and intent value
        Spinner dropdown1 = findViewById(R.id.maintreespinner);
        String[] items1 = new String[]{"--เลือก--","มะม่วง", "ลำไย"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this,R.layout.spinner, items1);
        dropdown1.setAdapter(adapter1);
        dropdown1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                tree1 = parentView.getItemAtPosition(position).toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        //drop down 2nd tree
//        final Spinner dropdown2 = findViewById(R.id.secondtreespinner);
//        String[] items2 = new String[]{"--เลือก--", "กล้วย"};
//        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.spinner, items2);
//        dropdown2.setAdapter(adapter2);
//        dropdown2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                tree2 = parentView.getItemAtPosition(position).toString();
//
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // your code here
//            }
//        });


        //drop down 3rd tree
        final Spinner dropdown3 = findViewById(R.id.thirdtreespinner);
        String[] items3 = new String[]{"--เลือก--","พริกไทย", "ผักกูด","ตะไคร้","คะน้า","ผักบุ้งจีน"};
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, R.layout.spinner, items3);
        dropdown3.setAdapter(adapter3);
        dropdown3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
               tree3 = parentView.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
        final String Text = dropdown3.getSelectedItem().toString();

        Button _tonewsblog =(Button)findViewById(R.id.button2) ;
        _tonewsblog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x = new Intent(getApplicationContext(),All_result.class) ;
                Bundle data = getIntent().getExtras();
                EditText PH1 = (EditText) findViewById(R.id.editText9);
                String PH = PH1.getText().toString();
                EditText CD1 = (EditText) findViewById(R.id.editText10);
                String CD = CD1.getText().toString();
                EditText N1 = (EditText) findViewById(R.id.editText4);
                String N = N1.getText().toString();
                EditText P1 = (EditText) findViewById(R.id.editText5);
                String P = P1.getText().toString();
                EditText K1 = (EditText) findViewById(R.id.editText6);
                String K = K1.getText().toString();
                EditText Cost1 = (EditText) findViewById(R.id.editText);
                String Cost =Cost1.getText().toString();
                RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
                RadioButton radioButton;
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selectedId);
                String radioselect = radioButton.getText().toString();
                Log.d("PH",PH);
                Log.d("CD",CD);
                Log.d("N",N);
                Log.d("P",P);
                Log.d("K",K);
                Log.d("Cost",Cost);
                Log.d("RadioButton",radioselect);

                x.putExtra("main_tree",tree1.toString());
//                x.putExtra("second_tree",tree2.toString());
                x.putExtra("third_tree",tree3.toString());
                x.putExtra("PH",PH.toString());
                x.putExtra("CD",CD.toString());
                x.putExtra("N",N.toString());
                x.putExtra("P",P.toString());
                x.putExtra("K",K.toString());
                x.putExtra("Cost",Cost.toString());
                x.putExtra("RadioButton",radioselect.toString());
                x.putExtra("main_tree",tree1.toString());
//                x.putExtra("second_tree",tree2.toString());
                x.putExtra("third_tree",tree3.toString());
                x.putExtras(data);
                startActivity(x);
            }
        });

//        Button _testtimeline =(Button)findViewById(R.id.button3) ;
//        _testtimeline.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent x =new Intent(getApplicationContext(),Result_main.class) ;
//                startActivity(x);
//            }
//        });
    }
}


