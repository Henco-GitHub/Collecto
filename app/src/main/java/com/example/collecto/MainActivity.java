package com.example.collecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText txtUsername;
    EditText txtPassword;
    Button btnLogReg;
    ImageView imgLogo;

    public String username;
    public String password;
    boolean uNameResult;
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
        setContentView(R.layout.activity_main);

        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogReg = findViewById(R.id.btnLogReg);
        imgLogo = findViewById(R.id.imgLogo);

        alUsers = GetUsers();
    }

    //Button Click Method to Login/Register
    public void clickLoginRegister(View view) {
        //Get child "users" reference
        DatabaseReference refUsers = refRoot.child("users");

        //Get Details from Login/Register form.
        username = txtUsername.getText().toString();
        password = txtPassword.getText().toString();

        user = new User(username, password);

        //TO DO: Check Validation

        String msgToast = "";

        Intent i = new Intent(MainActivity.this, myCollections.class);

        if (UserExists(username) == true) {
            switch (Login(user)) {
                case -1:
                    msgToast = "Successful Login!";
                    i.putExtra("username", user.getId());
                    break;
                case 0:
                case 1:
                    msgToast = "Incorrect Login Details!";
            }

            //Output Message
            Toast.makeText(this, msgToast, Toast.LENGTH_SHORT).show();
        } else {
            //Register new User
            Collection books = new Collection();
            books.setName("Books");
            user.AddCollection(books);

            refUsers.child(user.getId()+"").setValue(user);
            
            //Tell User Success
            Toast.makeText(this, "Successful Register and Login!", Toast.LENGTH_SHORT).show();
        }

        startActivity(i);
    }

    //Method to try and Login with input details
    public int Login(User attempt) {
        int result = -1; //Login Attempt Successful

        //Find Username in ArrayList
        for (int i = 0; i < alUsers.size(); i++) {
            //Get User Object of current index(i)
            User u = alUsers.get(i);

            //Check if current index matches login attempt username
            if (!attempt.getUsername().equals(u.getUsername())) {
                result = 0; //Username does not match/exist
                continue;
            }

            //Check if current index matches login attempt password
            if (!attempt.getPassword().equals(u.getPassword())) {
                result = 1; //Passwords do not match | Cannot login
                continue;
            }

            result = -1; //Successful
            attempt.LoadCollections();
            break;
        }

        return result;
    }

    //Get List of Registered Users
    public ArrayList<User> GetUsers() {
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

    //Check if User Exists
    public boolean UserExists(String uName) {
        for (User u : alUsers) {
            if (u.getUsername().equals(uName)) {
                return true;
            }
        }

        return false;
    }
}