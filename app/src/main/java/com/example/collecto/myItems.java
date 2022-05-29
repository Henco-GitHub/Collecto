package com.example.collecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.collecto.databinding.ActivityMyItemsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class myItems extends AppCompatActivity {

    String c_id, c_name, c_description, c_uid;

    private ActivityMyItemsBinding binding;
    private FirebaseAuth FireAuth;

    private ArrayList<ModelItem> itemArrayList;
    private AdapterItem adapItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FireAuth = FirebaseAuth.getInstance();
        CheckUser();
        LoadCollData();
        LoadItems();

        binding.btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("id", c_id);
                hashMap.put("name", c_name);
                hashMap.put("description", c_description);
                hashMap.put("uid", c_uid);

                Intent i = new Intent(myItems.this, AddItem.class);
                i.putExtra("Info", hashMap);

                startActivity(i);
            }
        });

        binding.imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(myItems.this, myCollections.class);
                startActivity(i);
            }
        });

        binding.edtSearchItems.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapItem.getFilter().filter(charSequence);
                } catch (Exception e) {
                    Toast.makeText(myItems.this, ""+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void LoadCollData() {
        Intent intent = getIntent();
        HashMap<String, Object> hashMap = (HashMap<String, Object>) intent.getSerializableExtra("Info");

        c_id = (String) hashMap.get("id");
        c_name = (String) hashMap.get("name");
        c_description = (String) hashMap.get("description");
        c_uid = (String) hashMap.get("uid");

        binding.lblItems.setText(c_name);
    }

    private void LoadItems() {
        itemArrayList = new ArrayList<>();
        DatabaseReference refItems = FirebaseDatabase.getInstance().getReference("items");

        refItems.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemArrayList.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    ModelItem mi = snap.getValue(ModelItem.class);

                    FirebaseUser firebaseUser = FireAuth.getCurrentUser();

                    if (mi.getUid().equals(firebaseUser.getUid())) {
                        if(mi.getCollection().equals(c_id)){
                            itemArrayList.add(mi);
                        }
                    }
                }

                adapItem = new AdapterItem(myItems.this, itemArrayList);
                binding.rvItems.setAdapter(adapItem);
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