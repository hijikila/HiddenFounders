package com.example.youssefgoumehri.hddenfounders;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Youssef Goumehri on 12/12/2017.
 */

public class RetreivedAlbum implements Parcelable {

    /**
     * VARIABLES
     */
    private int pictureCount;                       //Nb of photos inside this album
    private String albumName;                       //The current album name
    private String albumId;                         //The current album ID
    private Bitmap coverPicture;                    //The current album's cover picture
    private ArrayList<Photos> picturesUrl;          //ArrayList of the album's photos


    public ArrayList<Photos> getPicturesUrl() {
        return picturesUrl;
    }

    public void setPicturesUrl(ArrayList<Photos> picturesUrl) {
        this.picturesUrl = picturesUrl;
    }


    public String getAlbumName() {
        return albumName;
    }

    public String getAlbumId() {
        return albumId;
    }

    public Bitmap getCoverPicture() {
        return coverPicture;
    }

    public void setCoverPicture(Bitmap coverPicture) {
        this.coverPicture = coverPicture;
    }



    public RetreivedAlbum(JSONObject album) throws JSONException, MalformedURLException {

        this.albumId = album.get("id").toString();
        this.albumName = album.get("name").toString();
        this.pictureCount = Integer.parseInt(album.get("count").toString());
        this.picturesUrl = new ArrayList<>();
    }

    public RetreivedAlbum(Parcel in){
        this.albumId = in.readString();
        this.albumName = in.readString();
        this.pictureCount = in.readInt();

        this.picturesUrl = new ArrayList<>();

        in.readArrayList(getPicturesUrl().getClass().getClassLoader());
    }

    public String toString() {
        return "albumId : " + this.albumId + " , albumName : " + this.albumName + " , nb pictures : " + this.pictureCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {


        parcel.writeString(albumId);
        parcel.writeString(albumName);
        parcel.writeInt(pictureCount);
        parcel.writeArray(picturesUrl.toArray());

    }

    public static final Parcelable.Creator<RetreivedAlbum> CREATOR = new Parcelable.Creator<RetreivedAlbum>() {
        public RetreivedAlbum createFromParcel(Parcel in) {
            return new RetreivedAlbum(in);
        }

        @Override
        public RetreivedAlbum[] newArray(int i) {
            return new RetreivedAlbum[0];
        }
    };

    public Bitmap getPhoto(String link){
        BitmapFromURL bitmapFromURL = new BitmapFromURL();
        try {
            return (bitmapFromURL.execute(new URL(link)).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
