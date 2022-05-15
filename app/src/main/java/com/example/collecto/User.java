package com.example.collecto;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User {
    String username;
    String password;
    boolean uNameResult;

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


}
