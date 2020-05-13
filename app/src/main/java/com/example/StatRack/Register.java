package com.example.StatRack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    //Message Tag
    private static final String TAG = "REGISTER_TAG";

    //buttons, textfields, passwords
    Button regButton, regLogin;
    EditText regEmail, regPass;

    //Database
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //If logged in send to main
        if (mAuth.getCurrentUser() != null)
        {
            startActivity( new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        //setting views
        regEmail = findViewById(R.id.regEmail);
        regPass = findViewById(R.id.regPass);
        regButton = findViewById(R.id.regButton);
        regLogin = findViewById(R.id.loginButton);

        //toStrings
        String globalEmail = regEmail.getText().toString().trim();
        String globalPass = regPass.getText().toString().trim();

        //On click for register button
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = regEmail.getText().toString().trim();
                String pass = regPass.getText().toString().trim();

                validation();
            }
        });

        //Sending off to the database
        mAuth.createUserWithEmailAndPassword(globalEmail, globalPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(Register.this, "Registration Completed!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register.this, MainActivity.class));
                }

                else
                {
                    Toast.makeText(Register.this, "Hmmm something went wrong..", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "BIG ERROR : " + task);
                }
            }
        });

        //Sending new Activity to Login
        regLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, MainActivity.class));
            }
        });
    }

    private void validation()
    {
        if (regEmail == null)
        {
            Toast.makeText(Register.this, "Please place your email!", Toast.LENGTH_SHORT).show();
        }

        if (regPass == null)
        {
            Toast.makeText(Register.this, "Please type a password!", Toast.LENGTH_SHORT).show();
        }
    }



}
