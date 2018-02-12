package com.cwm.incube;

import android.widget.Switch;

/**
 * Created by warak on 02/12/18.
 */

public class Price {
    private double mango = 45 ;
    private double longan = 80;

    public double getPrice(String name) {
        if(name.equals("mango")){return mango;}
        else if(name.equals("mango")){return longan;}
        return 0;
    }


}
