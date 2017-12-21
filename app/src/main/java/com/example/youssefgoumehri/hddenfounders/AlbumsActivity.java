package com.example.youssefgoumehri.hddenfounders;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AlbumsActivity extends AppCompatActivity {

    private GridView listAlbum;                         //View that lists the albums
    private ArrayList<RetreivedAlbum> albumsList;       //ArrayList of connected profile albums
    private BitmapFromURL bitmapFromURL;                //gets a Bitmap of an URL String
    private android.app.AlertDialog dialog;             //an AlertDialog to show while long operations
    private android.app.AlertDialog.Builder builder;    //AlertDialog builder
    private boolean activityResumed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        setTitle("Albums");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listAlbum = findViewById(R.id.listView);
        albumsList = new ArrayList<>();



        activityResumed = false;


        //preparing the request to retrieve albums
            prepareRequest();


    }

    @Override
    protected void onPause() {
        super.onPause();
        activityResumed = true;
    }


    /**
     * On Activity resume
     */
    @Override
    protected void onResume() {
        super.onResume();

        //if session exists
        if(AccessToken.getCurrentAccessToken() != null) {

            listAlbum.setAdapter(null);
            listAlbum.deferNotifyDataSetChanged();
            //sending request to for albums
            if(activityResumed && isInternetAvailable()) {
                prepareRequest();
                activityResumed = false;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * - Fetches the JSONObject result for albums cover photo
     * @param album
     * @param pos
     * @throws JSONException
     * @throws MalformedURLException
     * @throws ExecutionException
     * @throws InterruptedException
     */

    private void RetrieveCover(JSONObject album, final int pos) throws JSONException, MalformedURLException, ExecutionException, InterruptedException {

        if(album.has("cover_photo")) {
            String coverID = (album.getJSONObject("cover_photo").getJSONArray("images")).getJSONObject(0).get("source").toString();

            String link = coverID.replace("\\","");
            bitmapFromURL = new BitmapFromURL();
            this.setPicture(bitmapFromURL.execute(new URL(link)).get(), pos);
        }

    }



    /**
     * Setter of the cover photo variable
     * @param img
     * @param posAlbum
     */
    private void setPicture(Bitmap img, int posAlbum){
        albumsList.get(posAlbum).setCoverPicture(img);
    }


    /**
     * Retrieves data from the Graph API response
     * and pass it to local variables
     * @param response
     * @throws JSONException
     */
    private void RegisterAlbums(GraphResponse response) throws JSONException {


        JSONObject jo = response.getJSONObject();
        JSONArray ja  = jo.getJSONArray("data");

        //Adding albums to the arraylist of albums
        for(int iterator = 0; iterator < ja.length(); iterator++){

            try {
                albumsList.add(new RetreivedAlbum((JSONObject) ja.get(iterator)));
                RetrieveCover((JSONObject) ja.get(iterator), albumsList.size()-1);


            } catch (JSONException | MalformedURLException | InterruptedException | ExecutionException e) {
                Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_LONG).show();
            }
        }

        listAlbum.setAdapter(new CustomAdapter(this.getApplicationContext(), albumsList));
        listAlbum.deferNotifyDataSetChanged();

        dialog.dismiss();
    }

    /**
     * Sends a Request to Servers for the albums of
     * the current connected profile
     */
    private void prepareRequest(){


        builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Fetching for albums...").setCancelable(true);

        dialog = builder.create();
        try {
            dialog.show();
        }catch(Exception e){}
        //Retrieving the current connection access Token
        AccessToken at = AccessToken.getCurrentAccessToken();

        if(at == null) {return;}

        //Select the information to get from server
        Bundle params = new Bundle();
        params.putString("fields", "id , name , count, cover_photo.fields(id, images.fields(source))");
        params.putString("type","large");

        //Sending request to the server
        GraphRequest request = new GraphRequest(at,
                at.getUserId()+"/albums?type=large",
                params,
                HttpMethod.GET, new GraphRequest.Callback(){

            @Override
            public void onCompleted(GraphResponse response) {
                try {
                    //managing response
                    RegisterAlbums(response);

                } catch (JSONException  e) {
                    Log.v("info","ERRRRRRROR");
                }
            }

        });


        request.executeAsync();

    }



    /**
     * Checks if the device is connected to internet
     * @return boolean
     */
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }

    }
}
