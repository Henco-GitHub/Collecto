package com.example.collecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class myCollections extends AppCompatActivity {

    ArrayList<User> alUsers = new ArrayList<User>();
    User user;

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
        setContentView(R.layout.activity_my_collections);

        alUsers = UserList();
        String username = "None";
        if(getIntent().getExtras() != null) {
            username = (String) getIntent().getSerializableExtra("username");
        }

        if (username.equals("None")) {
            Toast.makeText(this, "Username not set!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }

        user = GetUser(username);
        //if (user == null) {
        //    Toast.makeText(this, user.getUsername(), Toast.LENGTH_SHORT).show();
        //    Intent i = new Intent(this, MainActivity.class);
        //    startActivity(i);
        //}
    }

    private User GetUser(String user_id) {
        for (User u : alUsers) {
            if (u.getId().equals(user_id)) {
                Toast.makeText(this, user.getUsername().toString(), Toast.LENGTH_SHORT).show();
                return u;
            }
        }

        return null;
    }

    //Get List of Registered Users
    private ArrayList<User> UserList() {
        ArrayList<User> toReturn = new ArrayList<User>();
        DatabaseReference refUsers = refRoot.child("users");

        refUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User u = new User();

                for (DataSnapshot UsersFromDB : snapshot.getChildren()) {
                    u = UsersFromDB.getValue(User.class);
                    toReturn.add(u);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return toReturn;
    }
}