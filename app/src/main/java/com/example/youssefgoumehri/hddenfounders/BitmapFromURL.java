package com.example.youssefgoumehri.hddenfounders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Youssef Goumehri on 15/12/2017.
 * Objective : Retrieves the actual image from the URL passed as parameter
 * Returns the Bitmap object of the image
 */

public class BitmapFromURL extends AsyncTask< URL , Void , Bitmap >{

    @Override
    protected Bitmap doInBackground(URL... url) {
        Bitmap coverPicture = null;
        try {
            coverPicture = BitmapFactory.decodeStream(url[0].openConnection().getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return coverPicture;
    }
}
