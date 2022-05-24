package com.example.collecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.collecto.databinding.ActivityMyCollectionsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class myCollections extends AppCompatActivity {

    private ActivityMyCollectionsBinding binding;
    private FirebaseAuth FireAuth;

    private ArrayList<ModelCollection> collArrayList;
    private AdapterCollection adapCollection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyCollectionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FireAuth = FirebaseAuth.getInstance();
        CheckUser();
        LoadCollections();

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapCollection.getFilter().filter(charSequence);
                } catch (Exception e) {
                    Toast.makeText(myCollections.this, ""+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.btnAddColl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(myCollections.this, AddCollectionActivity.class);
                startActivity(i);
            }
        });
    }

    private void LoadCollections() {
        collArrayList = new ArrayList<>();
        DatabaseReference refCollections = FirebaseDatabase.getInstance().getReference("collections");

        refCollections.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                collArrayList.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    ModelCollection mc = snap.getValue(ModelCollection.class);
                    collArrayList.add(mc);
                }

                adapCollection = new AdapterCollection(myCollections.this, collArrayList);
                binding.rvCollections.setAdapter(adapCollection);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CheckUser() {
        // get current user
        FirebaseUser firebaseUser = FireAuth.getCurrentUser();
        if (firebaseUser == null) {
            // not logged in,goto main screen
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {

        }
    }
}