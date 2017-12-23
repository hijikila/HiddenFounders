package com.example.youssefgoumehri.hddenfounders;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;

public class PicturesViewActivity extends AppCompatActivity {


    /**
     * VARIABLES
     */
    private RetreivedAlbum currAlbum;                   //the current album
    private GridView photoList;                         //List of photos inside the current album
    private PictureAdapter pictureAdapter;              //the adapter of the UI
    private AlertDialog dialog;                         //an AlertDialog to show while long operations
    private AlertDialog.Builder builder;                //AlertDialog builder
    private boolean activityResumed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);

        //Setting the actionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Pictures");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        activityResumed = false;

        //get the gridview
        photoList = findViewById(R.id.photoView);

        //get data from the src activity
        Intent srcIntent = getIntent();
        currAlbum =  srcIntent.getExtras().getParcelable("album");


        //Retrieving the photos in the current album
        reterievePhotos();

    }





    /**
     * Add some actionBar entries
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.upload_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }





    /**
     * Managing clicks on action bar buttons
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.UploadToFireBase:
                if(pictureAdapter != null && isInternetAvailable())
                    pictureAdapter.uploadAll();
                pictureAdapter.notifyDataSetChanged();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }








    /**
     * Sends a request to get photos of the current album
     */
    private void reterievePhotos(){


        if(!isInternetAvailable()){
            Toast.makeText(getApplicationContext(),"Check your internet connection please",Toast.LENGTH_LONG).show();
            onBackPressed();
        }

        Bundle params = new Bundle();
        params.putString("fields", "id, source");


        //Setting up and launching the alert dialog
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Fetching for photos...").setCancelable(true);
        dialog = builder.create();
        try {
            dialog.show();
        }catch(Exception e){}


        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                currAlbum.getAlbumId()+"/photos",
                params,
                HttpMethod.GET, new GraphRequest.Callback(){

            @Override
            public void onCompleted(GraphResponse response) {
                Log.e("JSON OBJECT", response.toString()+" completed");

                try {
                    RegisterPhotos(response);
                } catch (JSONException e) {
                }


            }

        });

        request.executeAsync();
    }





    /**
     * Manages the response of the photo request and
     * sets the custom adater to the activity's GridView
     * @param response
     * @throws JSONException
     */
    private void RegisterPhotos(GraphResponse response) throws JSONException {


        if(response.getJSONObject() == null) return;


        JSONArray photodata = response.getJSONObject().getJSONArray("data");
        JSONObject o;


        ArrayList<String> picturesId = new ArrayList<>();
        ArrayList<Photos> picturesUrl = new ArrayList<>();


        for(int iterator = 0; iterator < photodata.length(); iterator++){
            o = (JSONObject) photodata.get(iterator);

            picturesId.add(o.get("id").toString());
            picturesUrl.add(new Photos(currAlbum.getPhoto(o.get("source").toString().replace("\\", ""))));


        }


        currAlbum.setPicturesUrl(picturesUrl);
        pictureAdapter = new PictureAdapter(this.getApplicationContext(), currAlbum, PicturesViewActivity.this);
        photoList.setAdapter(pictureAdapter);

        dialog.dismiss();

    }


    /**
     * Checks if the device is connected to internet
     * @return boolean
     */

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobiData = cm.getNetworkInfo(cm.TYPE_MOBILE);
        NetworkInfo Wifi = cm.getNetworkInfo(cm.TYPE_WIFI);
        if (mobiData != null && mobiData.isConnectedOrConnecting()) {
            return true;
        }
        if (Wifi != null && Wifi.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


}