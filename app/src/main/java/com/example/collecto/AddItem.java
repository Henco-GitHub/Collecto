package com.example.collecto;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.collecto.databinding.ActivityAddCollectionBinding;
import com.example.collecto.databinding.ActivityAddItemBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddItem extends AppCompatActivity {

    private static final int PICTURE_FROM_CAMERA = 1;
    String c_id, c_name, c_description, c_uid;
    String i_name = "";
    String i_desc = "";
    String i_pic = "";


    //Layout Binding
    private ActivityAddItemBinding binding;

    //Firebase Authorisation
    private FirebaseAuth FireAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    //Progress Dialog
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        FireAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        storageRef = storage.getReference();

        LoadCollData();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateData();
            }
        });

        binding.imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", c_id);
                hashMap.put("name", c_name);
                hashMap.put("description", c_description);
                hashMap.put("uid", c_uid);

                Intent i = new Intent(AddItem.this, myItems.class);
                i.putExtra("Info", hashMap);

                startActivity(i);
            }
        });

        ActivityResultLauncher<Intent> selectImage = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Uri image = result.getData().getData();
                            Bitmap bitmap = null;
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver() , image);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            binding.imgItem.setImageBitmap(bitmap);
                        }
                    }
                });

        binding.btnSelPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    selectImage.launch(gallery);
                } catch (Exception e) {
                    Toast.makeText(AddItem.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

    }

    private void UploadImage(StorageReference ref, String Image_ID) {
        ////////////////////////////////////////////////////////////////////////////////////////////
        // Create a reference to "mountains.jpg"
        StorageReference imgRef = ref.child(Image_ID + ".jpg");

        // Create a reference to 'images/mountains.jpg'
        StorageReference imgImagesRef = ref.child("images/" + Image_ID + ".jpg");

        // While the file names are the same, the references point to different files
        imgRef.getName().equals(imgImagesRef.getName());    // true
        imgRef.getPath().equals(imgImagesRef.getPath());    // false
        ////////////////////////////////////////////////////////////////////////////////////////////
        binding.imgItem.setDrawingCacheEnabled(true);
        binding.imgItem.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) binding.imgItem.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imgRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(AddItem.this, "Error in uploading item!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddItem.this, "Item Added!", Toast.LENGTH_SHORT).show();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", c_id);
                hashMap.put("name", c_name);
                hashMap.put("description", c_description);
                hashMap.put("uid", c_uid);

                Intent i = new Intent(AddItem.this, myItems.class);
                i.putExtra("Info", hashMap);

                progressDialog.dismiss();

                startActivity(i);
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////
    }

    private void ValidateData() {
        i_name = binding.edtItemName.getText().toString().trim();
        i_desc = binding.edtItemDesc.getText().toString().trim();
        i_pic = ""; //TO SET LATER

        if (TextUtils.isEmpty(i_name)) {
            Toast.makeText(this, "Invalid name entered!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(i_desc)) {
            Toast.makeText(this, "Invalid description entered!", Toast.LENGTH_SHORT).show();
        } else if (binding.imgItem.getDrawable() == null) {
            Toast.makeText(this, "Please select another image!", Toast.LENGTH_SHORT).show();
        } else {
            AddItem();
        }
    }

    private void LoadCollData() {
        Intent intent = getIntent();
        HashMap<String, Object> hashMap = (HashMap<String, Object>) intent.getSerializableExtra("Info");

        c_id = (String) hashMap.get("id");
        c_name = (String) hashMap.get("name");
        c_description = (String) hashMap.get("description");
        c_uid = (String) hashMap.get("uid");
    }

    private void AddItem() {
        progressDialog.setMessage("Adding Item...");
        progressDialog.show();

        String i_id = "" + System.currentTimeMillis();

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String date = df.format(c);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", "" + i_id);
        hashMap.put("name", "" + i_name);
        hashMap.put("description", "" + i_desc);
        hashMap.put("date", date);
        hashMap.put("pic", i_id);
        hashMap.put("collection", c_id);
        hashMap.put("uid", "" + FireAuth.getUid());


        DatabaseReference refCollections = FirebaseDatabase.getInstance().getReference("items");
        refCollections.child("" + i_id)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        UploadImage(storageRef, "" + i_id);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddItem.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Taking and setting a Picture

    File photoFile = null;

    private void takePicture(){
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            photoFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri uri = FileProvider.getUriForFile(this, "com.example.collecto.fileprovider", photoFile);
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(pictureIntent, PICTURE_FROM_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICTURE_FROM_CAMERA) {
            if (resultCode == RESULT_OK) {
                Uri uri = FileProvider.getUriForFile(this, "com.example.collecto.fileprovider", photoFile);
                binding.imgItem.setImageURI(uri);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}