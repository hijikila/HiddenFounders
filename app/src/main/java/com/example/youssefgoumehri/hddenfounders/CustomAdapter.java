package com.example.youssefgoumehri.hddenfounders;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Youssef Goumehri on 12/12/2017.
 * Objective : Serves as custom adapter of the grid view of albums
 */

public class CustomAdapter extends BaseAdapter {



    /**
     * Variables
     */
    private ArrayList<RetreivedAlbum> albums;   //The list of current profile albums
    private Context context;                    //the object of the current context
    private LayoutInflater infalter;            //Layout inflater



    /**
     * Constructor
     * @param context
     * @param albums
     */
    public CustomAdapter(Context context, ArrayList<RetreivedAlbum> albums){
        this.context = context;
        this.albums = albums;
        this.infalter = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public int getCount() {
        return albums == null ? 0:this.albums.size();
    }

    @Override
    public Object getItem(int i) {
        return this.albums.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }





    /**
     * Holder of single album's view elements
     */
    private class Holder{
        TextView albumName;
        ImageView image;
    }





    /**
     *
     * @param i
     * @param view
     * @param viewGroup
     * @return
     */
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View albumlist = infalter.inflate(R.layout.albumlist,null);
        Holder holder = new Holder();
        holder.albumName =  albumlist.findViewById(R.id.albumname);
        holder.image =  albumlist.findViewById(R.id.cover_image);

        holder.albumName.setText(this.albums.get(i).getAlbumName());
        holder.image.setImageBitmap(this.albums.get(i).getCoverPicture());

        albumlist.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PicturesViewActivity.class);
                intent.putExtra("album",  albums.get(i));
                context.startActivity(intent);

            }
        });
        return albumlist;
    }
}
