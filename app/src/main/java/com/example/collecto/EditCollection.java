package com.example.collecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.collecto.databinding.ActivityEditCollectionBinding;
import com.example.collecto.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.Inet4Address;
import java.util.HashMap;

public class EditCollection extends AppCompatActivity {

    String id, name, description, uid;

    private ActivityEditCollectionBinding binding;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditCollectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LoadCollData();

        binding.imgEditBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EditCollection.this, myCollections.class);
                startActivity(i);
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Deleting: \"" + name + "\"");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.btnDelColl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditCollection.this);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete \nCollection: " + name + "?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int choice) {
                                progressDialog.show();
                                delCollection();
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

        binding.btnEditColl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveCollection();
            }
        });

    }

    private void SaveCollection() {
        long c_id = System.currentTimeMillis();

        name = binding.edtEditCollName.getText().toString();
        description = binding.edtEditCollDesc.getText().toString();

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("name", name);
        hashMap.put("description", description);
        hashMap.put("uid", "" + uid);


        DatabaseReference refCollections = FirebaseDatabase.getInstance()
                .getReference("collections");

        refCollections.child(id)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EditCollection.this, "Collection Saved!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(EditCollection.this, myCollections.class);
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditCollection.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void delCollection() {
        DatabaseReference refCollections = FirebaseDatabase.getInstance()
                .getReference("collections");

        refCollections.child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EditCollection.this, "Deleted collection: \"" + name + "\"", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(EditCollection.this, myCollections.class);
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditCollection.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void LoadCollData() {
        Intent intent = getIntent();
        HashMap<String, Object> hashMap = (HashMap<String, Object>) intent.getSerializableExtra("Info");

        id = (String) hashMap.get("id");
        name = (String) hashMap.get("name");
        description = (String) hashMap.get("description");
        uid = (String) hashMap.get("uid");

        binding.edtEditCollName.setText(name);
        binding.edtEditCollDesc.setText(description);
    }
}