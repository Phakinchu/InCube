package com.cwm.incube;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Programinput extends AppCompatActivity  {

    String tree1,tree2,tree3 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        setContentView(R.layout.activity_programinput);

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
        final Spinner dropdown2 = findViewById(R.id.secondtreespinner);
        String[] items2 = new String[]{"--เลือก--", "กล้วย"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.spinner, items2);
        dropdown2.setAdapter(adapter2);
        dropdown2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                tree2 = parentView.getItemAtPosition(position).toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });


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
                Intent x =new Intent(getApplicationContext(),Resultprogram.class) ;
                x.putExtra("main_tree",tree1.toString());
                x.putExtra("second_tree",tree2.toString());
                x.putExtra("third_tree",tree3.toString());
                setResult(RESULT_OK, x);
                finish();
            }
        });
    }
}


