package com.example.collecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.collecto.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    //Firebase Authentication
    private FirebaseAuth FireAuth;
    //Variable to use Progress Dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Initialise Firebase Authentication
        FireAuth = FirebaseAuth.getInstance();

        //Progress Dialog to wait for Firebase
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateData();
            }
        });

        binding.imgEditBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

    String name = "";
    String email = "";
    String passwd = "";
    String confirmPasswd = "";
    private void ValidateData() {
        name = binding.edtName.getText().toString().trim();
        email = binding.edtEmail.getText().toString().trim();
        passwd = binding.edtPasswd.getText().toString().trim();
        confirmPasswd = binding.edtConfirmPasswd.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter a valid name!", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(passwd)) {
            Toast.makeText(this, "Please enter a valid password!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmPasswd)) {
            Toast.makeText(this, "Please enter a valid confirmation!", Toast.LENGTH_SHORT).show();
        }
        else if(!passwd.equals(confirmPasswd)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
        }
        else {
            CreateUserAccount();
        }
    }

    private void CreateUserAccount() {
        //Stall user
        progressDialog.setMessage("Creating User Account...");
        progressDialog.show();

        //Create user on Firebase
        FireAuth.createUserWithEmailAndPassword(email, passwd)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        UpdateUserInfo();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void UpdateUserInfo() {
        progressDialog.setMessage("Saving User Info...");

        String u_id = FireAuth.getUid();

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("uid", u_id);
        hashMap.put("email", email);
        hashMap.put("name", name);

        DatabaseReference refUsers = FirebaseDatabase.getInstance().getReference("users");

        refUsers.child(u_id)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "User Account Added...", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(RegisterActivity.this, myCollections.class);
                        startActivity(i);

                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}