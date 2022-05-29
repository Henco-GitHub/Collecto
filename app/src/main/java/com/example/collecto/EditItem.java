package com.example.collecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.collecto.databinding.ActivityEditItemBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    //Progress Dialog
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FireAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        LoadCollData();
        LoadItemData();

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
        i_pic = "";

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
                        progressDialog.dismiss();
                        Toast.makeText(EditItem.this, "Item Added!", Toast.LENGTH_SHORT).show();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", c_id);
                        hashMap.put("name", c_name);
                        hashMap.put("description", c_description);
                        hashMap.put("uid", c_uid);

                        Intent i = new Intent(EditItem.this, myItems.class);
                        i.putExtra("Info", hashMap);

                        startActivity(i);
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