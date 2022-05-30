package com.example.collecto;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.collecto.databinding.ActivityEditItemBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class EditItem extends AppCompatActivity {

    String c_id, c_name, c_description, c_uid;

    //Layout Binding
    private ActivityEditItemBinding binding;

    //Firebase Authorisation
    private FirebaseAuth FireAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference pathReference;

    //Progress Dialog
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FireAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        storageRef = storage.getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.setMessage("Loading Item...");
        progressDialog.show();

        LoadCollData();
        LoadItemData();

        pathReference = storageRef.child("images/" + i_pic + ".jpg");
        StorageReference gsReference = storage.getReferenceFromUrl("gs://collecto-cda25.appspot.com/images/");

        LoadImage(i_pic);


        binding.imgEditBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EditItem.this, myItems.class);
                i.putExtra("Info", c_info);
                startActivity(i);
            }
        });

        binding.btnDelItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditItem.this);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete \nCollection: " + i_name + "?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int choice) {
                                progressDialog.show();
                                delItem();
                                progressDialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int choice) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        binding.btnEditItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveItem();
            }
        });

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Uri image = result.getData().getData();
                            binding.imgItemEdit.setImageURI(image);
                        }
                    }
                });

        binding.btnSelPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                someActivityResultLauncher.launch(gallery);
            }
        });
    }

    File localFile = null;

    private void LoadImage(String Image_ID) {
        StorageReference islandRef = storageRef.child(Image_ID + ".jpg");

        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Uri u = Uri.parse(localFile.toURI().toString());
                binding.imgItemEdit.setImageURI(u);
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
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
        binding.imgItemEdit.setDrawingCacheEnabled(true);
        binding.imgItemEdit.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) binding.imgItemEdit.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imgRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(EditItem.this, "Error in uploading item!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(EditItem.this, "Item Saved!", Toast.LENGTH_SHORT).show();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", c_id);
                hashMap.put("name", c_name);
                hashMap.put("description", c_description);
                hashMap.put("uid", c_uid);

                Intent i = new Intent(EditItem.this, myItems.class);
                i.putExtra("Info", hashMap);

                startActivity(i);

                progressDialog.dismiss();
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////
    }

    private void delItem() {
        DatabaseReference refCollections = FirebaseDatabase.getInstance()
                .getReference("items");

        Intent i = getIntent();
        HashMap<String, Object> i_info = (HashMap<String, Object>) i.getSerializableExtra("item");

        String i_id = (String) i_info.get("id");

        refCollections.child(i_id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", c_id);
                        hashMap.put("name", c_name);
                        hashMap.put("description", c_description);
                        hashMap.put("uid", c_uid);

                        Toast.makeText(EditItem.this, "Deleted item: \"" + i_name + "\"", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(EditItem.this, myItems.class);
                        i.putExtra("Info", hashMap);

                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditItem.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void SaveItem() {
        progressDialog.setMessage("Saving Item...");
        progressDialog.show();

        Intent i = getIntent();
        HashMap<String, Object> i_info = (HashMap<String, Object>) i.getSerializableExtra("item");

        String i_id = (String) i_info.get("id");

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String date = df.format(c);

        i_name = binding.edtEditItemName.getText().toString().trim();
        i_desc = binding.edtEditItemDesc.getText().toString().trim();
        i_pic = i_id;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", "" + i_id);
        hashMap.put("name", "" + i_name);
        hashMap.put("description", "" + i_desc);
        hashMap.put("date", date);
        hashMap.put("pic", i_pic);
        hashMap.put("collection", c_id);
        hashMap.put("uid", "" + FireAuth.getUid());

        DatabaseReference refCollections = FirebaseDatabase.getInstance().getReference("items");
        refCollections.child("" + i_id)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        UploadImage(storageRef, i_id);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(EditItem.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    String i_name = "";
    String i_desc = "";
    String i_pic = "";

    private void LoadItemData() {
        Intent i = getIntent();
        HashMap<String, Object> hashMap = (HashMap<String, Object>) i.getSerializableExtra("item");

        i_name = (String) hashMap.get("name");
        i_desc = (String) hashMap.get("description");
        i_pic = (String) hashMap.get("pic");

        binding.edtEditItemName.setText(i_name);
        binding.edtEditItemDesc.setText(i_desc);
        //TODO IMAGE LOAD
    }

    AdapterItem adapItem = new AdapterItem();
    HashMap<String, Object> c_info;

    private void LoadCollData() {
        c_info = adapItem.CollInfo();

        c_id = (String) c_info.get("id");
        c_name = (String) c_info.get("name");
        c_description = (String) c_info.get("description");
        c_uid = (String) c_info.get("uid");
    }
}