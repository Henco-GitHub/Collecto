package com.example.collecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddItem extends AppCompatActivity {

    String c_id, c_name, c_description, c_uid;

    //Layout Binding
    private ActivityAddItemBinding binding;

    //Firebase Authorisation
    private FirebaseAuth FireAuth;

    //Progress Dialog
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FireAuth = FirebaseAuth.getInstance();

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
                Intent i = new Intent(AddItem.this, myItems.class);
                startActivity(i);
            }
        });
    }

    String i_name = "";
    String i_desc = "";
    String i_pic = "";
    private void ValidateData() {
        i_name = binding.edtItemName.getText().toString().trim();
        i_desc = binding.edtItemDesc.getText().toString().trim();
        i_pic = ""; //TO SET LATER

        if (TextUtils.isEmpty(i_name)) {
            Toast.makeText(this, "Invalid name entered!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(i_desc)) {
            Toast.makeText(this, "Invalid description entered!", Toast.LENGTH_SHORT).show();
        }
        else {
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

        long i_id = System.currentTimeMillis();

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String date = df.format(c);

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id","" + i_id);
        hashMap.put("name","" + i_name);
        hashMap.put("description","" + i_desc);
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
                        Toast.makeText(AddItem.this, "Item Added!", Toast.LENGTH_SHORT).show();

                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("id", c_id);
                        hashMap.put("name", c_name);
                        hashMap.put("description", c_description);
                        hashMap.put("uid", c_uid);

                        Intent i = new Intent(AddItem.this, myItems.class);
                        i.putExtra("Info", hashMap);

                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddItem.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}