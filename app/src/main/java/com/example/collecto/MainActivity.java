package com.example.collecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    //Firebase
    private FirebaseDatabase rootNode;
    DatabaseReference Userreference;

    String TAG = "Firebase";

    final ArrayList<String> listUsername = new ArrayList<>();

    public String username;
    public String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogReg = findViewById(R.id.btnLogReg);
        imgLogo = findViewById(R.id.imgLogo);


        //Firebase
        rootNode = FirebaseDatabase.getInstance();
        Userreference=rootNode.getReference("users");



    }

    public void loginRegister(View v)
    {


        if (txtUsername.getText().toString().equals("") )
        {
            Toast.makeText(getApplicationContext(),"Please make sure to fill in both fields",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            //read username

            Userreference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot snapshot1 : snapshot.getChildren())
                    {
                        login l2 = snapshot1.getValue(login.class);
                        int id = l2.getId();

                        username = snapshot1.child(id + "/username").getValue().toString();
                        password = snapshot1.child(id + "/password").getValue().toString();

                        listUsername.add(username);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            if (listUsername.contains(txtUsername.getText().toString()) && txtPassword.getText().toString() == password)
            {
                //log user in
                //send user to next page
                Intent intent = new Intent(this,myCollections.class);
                startActivity(intent);
            }
            else {
                //register user
                //login class
                login l = new login(txtUsername.getText().toString(), txtPassword.getText().toString(), 3);
                Userreference.child(l.getId()+"").setValue(l);

                //send user to next page
                Intent intent = new Intent(this,myCollections.class);
                startActivity(intent);
            }




        }




    }



}