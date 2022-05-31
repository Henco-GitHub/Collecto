package com.example.collecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.collecto.databinding.ActivityEditItemBinding;
import com.example.collecto.databinding.ActivityViewItemBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ViewItem extends AppCompatActivity {

    //Class Variables
    //For Collection Details
    private String c_id, c_name, c_description, c_uid;
    //For Item Details
    private String i_name, i_desc, i_pic, i_date;
    //File for Image
    private File localFile = null;
    //Variables for storing Collection Data
    private AdapterItem adapterItem = new AdapterItem();
    private HashMap<String, Object> c_info;

    //View binding
    private ActivityViewItemBinding binding;

    //Firebase Instances
    private FirebaseAuth FireAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    //Progress Dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Set Firebase Instances
        FireAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        storageRef = storage.getReference();

        //Instantiate Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        //Stall until Item is loaded properly
        progressDialog.setMessage("Loading Item...");
        progressDialog.show();

        //Load Collection and Item data
        LoadCollData();
        LoadItemData();

        //Load Picture based on Item data
        LoadImage(i_pic);

        //Back Button Code
        binding.imgViewBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewItem.this, myItems.class);
                //Add current collection data as extra
                i.putExtra("Info", c_info);
                startActivity(i);
            }
        });
    }

    private void LoadImage(String Image_ID) {
        //Set reference of image to load
        StorageReference islandRef = storageRef.child(Image_ID + ".jpg");

        //Create temp file for image download
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //Get the file from Firebase and store it in Temp Local File
        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                //Parse downloaded file and set image
                Uri u = Uri.parse(localFile.toURI().toString());
                binding.imgItemView.setImageURI(u);

                //Dismiss Dialog as all data should have loaded
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    //Load Item data from intent
    private void LoadItemData() {
        //get intent from Collections page
        Intent i = getIntent();

        //Get extra from intent
        HashMap<String, Object> hashMap = (HashMap<String, Object>) i.getSerializableExtra("item");

        //Set item data variables
        i_name = (String) hashMap.get("name");
        i_desc = (String) hashMap.get("description");
        i_pic = (String) hashMap.get("pic");
        i_date = (String) hashMap.get("date");

        //Load some data into fields
        binding.lblViewItemSub.setText(i_name);
        binding.edtViewItemDesc.setText(i_desc);
        binding.lblViewDate.setText("Added: " + i_date);
    }

    //Load collection data from adapter item
    private void LoadCollData() {
        //Set c_info to hold data to work with
        c_info = adapterItem.CollInfo();

        //Retrieve all Collection info into variables
        c_id = (String) c_info.get("id");
        c_name = (String) c_info.get("name");
        c_description = (String) c_info.get("description");
        c_uid = (String) c_info.get("uid");
    }
}