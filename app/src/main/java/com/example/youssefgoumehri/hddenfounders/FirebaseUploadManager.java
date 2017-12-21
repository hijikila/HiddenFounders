package com.example.youssefgoumehri.hddenfounders;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * Created by Youssef Goumehri on 18/12/2017.
 * Helps to Upload photos to FireBase Servers
 */

public class FirebaseUploadManager {

    /**
     * VARIABLES
     */
    private PicturesViewActivity _this;         //The request source
    private FirebaseStorage storage;            // Variables
    private StorageReference storageReference; // for uloading



    
    /**
     * Constructor
     * @param _this PicturesViewActivity
     */
    public FirebaseUploadManager(PicturesViewActivity _this) {

        this._this = _this;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }





    /**
     * Uploads given Bitmap to FireBase server
     * @param imgFile Bitmap
     */
    public void uploadToServer(Bitmap imgFile){
        final ProgressDialog progressDialog = new ProgressDialog(_this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imgFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        ref.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(_this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(_this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = 100.0*(taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploading "+progress+"%");
                    }
                });
    }
}
