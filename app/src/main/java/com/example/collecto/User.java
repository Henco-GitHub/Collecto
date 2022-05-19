package com.example.collecto;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class User extends Collections {
    private String username;
    private String password;
    private String id;
    private User tUser = this;

    public static User loggedin;

    private boolean uNameResult;

    //Firebase Variables
    //Get Firebase References.
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //Get reference of "message" key.
    DatabaseReference refMessage = database.getReference("message");
    //Get Main parent of Database.
    DatabaseReference refRoot = refMessage.getParent();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        setId();
        return id;
    }

    private void setId() {
        this.id = username;
    }

    //Load Collections into extended(inherited) ArrayList
    public void LoadCollections() {
        tUser.ClearList();

        //Users Reference
        DatabaseReference refUsers = refRoot.child("users");
        //Specific User Reference
        DatabaseReference refLoggedIn = refUsers.child(this.getId());
        //User Collections reference
        DatabaseReference refCollections = refLoggedIn.child("collections");

        refCollections.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Collection c = new Collection();

                for (DataSnapshot dbCollection : snapshot.getChildren()) {
                    String c_name = dbCollection.child("name").getValue(String.class);
                    String c_desc = dbCollection.child("description").getValue(String.class);

                    c = new Collection(c_name, c_desc);

                    tUser.AddCollection(c);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
