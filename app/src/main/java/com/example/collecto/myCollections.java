package com.example.collecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.collecto.databinding.ActivityMyCollectionsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class myCollections extends AppCompatActivity {

    private ActivityMyCollectionsBinding binding;

    //Firebase Variables
    //Get Firebase References.
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //Get reference of "message" key.
    DatabaseReference refMessage = database.getReference("message");
    //Get Main parent of Database.
    DatabaseReference refRoot = refMessage.getParent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyCollectionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnAddColl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(myCollections.this, AddCollectionActivity.class);
                startActivity(i);
            }
        });
    }
}