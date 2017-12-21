package com.example.youssefgoumehri.hddenfounders;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class CnxActivity extends AppCompatActivity implements View.OnClickListener{

    /**
     * VARIABLES
     */
    private LoginButton LoginBtn;                       //Button that serves to login using facebook
    private CallbackManager fbCallBack;                 //Call Back manager for the login operation
    private ArrayList<RetreivedAlbum>albumsList;        //ArrayList of connected profile albums
    private BitmapFromURL bitmapFromURL;                //gets a Bitmap of an URL String
    private Profile currProfile;                        //The current profile
    private Button goToAlbums;                          //the button to go show your albums
    private Intent intent;                              //Intent for activity changing
    private ImageView ppv;                              //The profile picture ImageView
    private TextView userNameTv;                        //TextView showing the name of the current user
    private boolean activityResumed;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cnx);


        //Retrieving some views
        goToAlbums = findViewById(R.id.ToAlbums);
        ppv = findViewById(R.id.profile_picture);
        userNameTv = findViewById(R.id.userName);

        //Hiding the profile picture and button goToAlbums
        goToAlbums.setVisibility(View.GONE);
        ppv.setVisibility(View.GONE);
        userNameTv.setVisibility(View.GONE);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //Instantiating
        albumsList = new ArrayList<>();

        activityResumed = false;

        //Setting the login button
        LoginBtn = findViewById(R.id.login_button);
        LoginBtn.setReadPermissions("email");
        LoginBtn.setReadPermissions("user_photos");

        //Create an instance of facebookCallBackManager
        fbCallBack = CallbackManager.Factory.create();

        //Setting an OnClick listener to the button
        goToAlbums.setOnClickListener(this);





        //AccessToken state change listener
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null){
                    ppv.setVisibility(View.GONE);
                    goToAlbums.setVisibility(View.GONE);
                    userNameTv.setVisibility(View.GONE);
                }else{
                    ppv.setVisibility(View.VISIBLE);
                    goToAlbums.setVisibility(View.VISIBLE);
                    userNameTv.setVisibility(View.VISIBLE);
                }
            }
        };


        accessTokenTracker.startTracking();
        //register the callback listener of the logging
         registerCallBack();

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fbCallBack.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * On Activity resume
     */
    @Override
    protected void onResume() {
        super.onResume();

        //if a session exists
        if(AccessToken.getCurrentAccessToken() != null) {
            //if(activityResumed) {
            if(isInternetAvailable()) {
                //sending request to for albums
                setProfile();
                setProfilePicture(currProfile.getProfilePictureUri(350, 440));
            }
            //}else activityResumed = true;
        }

    }




    /**
     *  Registers a listener for the facebook login button
     *  - onSuccess : Sets Current profile
     */
    private void  registerCallBack(){

        LoginBtn.registerCallback(fbCallBack, new FacebookCallback<LoginResult>(){
            @Override
            public void onSuccess(LoginResult loginResult) {
               setProfile();

            }

            //in case the request is canceled
            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),"Canceled !",Toast.LENGTH_LONG).show();
            }

            //in case an error occurred while request
            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getApplicationContext(),"An error occurred",Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Gets the current connected from the AccessToken
     * and Sets the Profile Object
     */
    private void setProfile(){


        //getting the current connected profile information
        Profile.fetchProfileForCurrentAccessToken();
        currProfile = Profile.getCurrentProfile();

        updateUI();

    }




    /**
     * Populate the UI with retrieved data
     */
    private void updateUI() {

        setProfilePicture(currProfile.getProfilePictureUri(350, 440));

        userNameTv.setText(currProfile.getName());

        goToAlbums.setVisibility(View.VISIBLE);
        ppv.setVisibility(View.VISIBLE);
        userNameTv.setVisibility(View.VISIBLE);


    }





    /**
     *Retrieves bitmap object from its Uri
     * @param pictureURI Uri
     */
    private void setProfilePicture(Uri pictureURI){
        try {
            String link = pictureURI.toString();
            bitmapFromURL = new BitmapFromURL();

            Bitmap b = bitmapFromURL.execute(new URL(link)).get();
            ppv.setImageBitmap(b);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }



    /**
     * Manages the click to go to the album's activity
     * passing the response containing albums as intent Extras
     * @param view
     */
    @Override
    public void onClick(View view) {

         if(AccessToken.getCurrentAccessToken() != null && isInternetAvailable()) {
             System.out.println("in22222");
            intent = new Intent(getApplicationContext(), AlbumsActivity.class);

            getApplicationContext().startActivity(intent);
        }

    }




    /**
     * Checks if the device is connected to internet
     * @return boolean
     */
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("8.8.8.8");
            return !ipAddr.equals("");

        } catch (Exception e) {
            System.out.println("exception test internet");
            return false;
        }

    }
}
