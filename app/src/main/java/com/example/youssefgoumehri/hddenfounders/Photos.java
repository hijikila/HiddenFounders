package com.example.youssefgoumehri.hddenfounders;

import android.graphics.Bitmap;

/**
 * Created by Youssef Goumehri on 18/12/2017.
 *
 */

public class Photos {

    /**
     * Variables
     */
    private Bitmap mbitMap; // bitmap of the actual photo
    private boolean checked;// indicates whether the photo is selected for upload


    /**
     * constructor
     * @param mbitMap
     */
    public Photos(Bitmap mbitMap) {
        this.mbitMap = mbitMap;
        this.checked = false;
    }



    /**
     * Getters and Setters
     */
    public void toggleCkecked(){
        this.checked = !this.checked;
    }

    public boolean isChecked(){
        return checked;
    }

    public Bitmap getMbitMap() {
        return mbitMap;
    }

    public void setMbitMap(Bitmap mbitMap) {
        this.mbitMap = mbitMap;
    }
}
