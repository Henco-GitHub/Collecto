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
import com.example.collecto.databinding.ActivityMyCollectionsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddCollectionActivity extends AppCompatActivity {

    //Layout Binding
    private ActivityAddCollectionBinding binding;

    //Firebase Authorisation
    private FirebaseAuth FireAuth;

    //Progress Dialog
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FireAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.btnAddColl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateData();
            }
        });

        binding.imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AddCollectionActivity.this, myCollections.class);
                startActivity(i);
            }
        });
    }

    String c_name = "";
    String c_desc = "";
    private void ValidateData() {
        c_name = binding.edtCollName.getText().toString().trim();
        c_desc = binding.edtCollDesc.getText().toString().trim();

        if (TextUtils.isEmpty(c_name)) {
            Toast.makeText(this, "Invalid name entered!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(c_desc)) {
            Toast.makeText(this, "Invalid description entered!", Toast.LENGTH_SHORT).show();
        }
        else {
            AddCollection();
        }
    }

    private void AddCollection() {
        progressDialog.setMessage("Adding Collection...");
        progressDialog.show();

        long c_id = System.currentTimeMillis();

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("id","" + c_id);
        hashMap.put("name","" + c_name);
        hashMap.put("description","" + c_desc);
        hashMap.put("uid", "" + FireAuth.getUid());

        DatabaseReference refCollections = FirebaseDatabase.getInstance().getReference("collections");
        refCollections.child("" + c_id)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(AddCollectionActivity.this, "Collection Added!", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(AddCollectionActivity.this, myCollections.class);
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddCollectionActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}