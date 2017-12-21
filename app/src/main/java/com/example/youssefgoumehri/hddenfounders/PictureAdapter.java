package com.example.youssefgoumehri.hddenfounders;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Youssef Goumehri on 17/12/2017.
 * Objective : Serves as custom adapter of the grid view of pictures
 */

public class PictureAdapter extends BaseAdapter {


    /**
     * Variables
     */
    private Context context;                //the object of the current context
    private RetreivedAlbum currAlbum;       //contains the current album
    private ArrayList<Photos> photoList;    //The photos inside the current album
    private LayoutInflater infalter;        //Layout inflater
    private FirebaseUploadManager fbum;     //used to upload photos to fireBase server
    private ArrayList<String> indexToUpload;//the index of the current album



    /**
     * constructor
     * @param context
     * @param currAlbum
     * @param _this
     */
    public PictureAdapter(Context context, RetreivedAlbum currAlbum, PicturesViewActivity _this) {
        System.out.println("OK we're in");
        this.context = context;
        this.currAlbum = currAlbum;
        this.photoList = this.currAlbum.getPicturesUrl();
        this.infalter = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.fbum = new FirebaseUploadManager(_this);
        this.indexToUpload = new ArrayList<>();
    }



    @Override
    public int getCount() {
        return photoList == null ? 0:photoList.size();
    }

    @Override
    public Object getItem(int i) {
        return photoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }






    /**
     * Serves as holder to a single photo view elements
     */
    private class Holder{
        ImageView pitureView;
        ImageView checkImage;
    }



    /**
     * Creates the single photo view
     * @param i
     * @param view
     * @param viewGroup
     * @return View
     */
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {


        View piclist = infalter.inflate(R.layout.picturelist, null);

        Holder holder = new PictureAdapter.Holder();

        holder.pitureView =  piclist.findViewById(R.id.picture);

        holder.checkImage = piclist.findViewById(R.id.checkedImg);


        if(photoList.get(i).isChecked()) holder.checkImage.setVisibility(View.VISIBLE);

        else holder.checkImage.setVisibility(View.INVISIBLE);

        final Bitmap img = photoList.get(i).getMbitMap();
        if(img != null) holder.pitureView.setImageBitmap(img);
        piclist.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View view) {

                toggleCheck(i);
                notifyDataSetChanged();
                return false;
            }


        });

        return piclist;
    }




    /**
     * (un)Checks an image
     * @param i
     */
    private void toggleCheck(int i){
        if(photoList.get(i).isChecked())indexToUpload.remove(i+"");
        else indexToUpload.add(i+"");
        photoList.get(i).toggleCkecked();

    }



    /**
     * Uploads checked photos to FireBase Server
     */
    public void uploadAll(){
        if(this.indexToUpload.size() == 0) {
            Toast.makeText(this.context, "Nothing to upload!", Toast.LENGTH_LONG).show();
            return;
        }

        for (String index :
                indexToUpload) {
            fbum.uploadToServer(photoList.get(Integer.parseInt(index)).getMbitMap());
            photoList.get(Integer.parseInt(index)).toggleCkecked();
        }

    }



}
