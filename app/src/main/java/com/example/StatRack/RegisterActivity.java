package com.example.StatRack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    //Message Tag
    private static final String TAG = "RegisterActivity";

    //buttons, textfields, passwords
    Button regButton, regLogin;
    EditText regEmail, regPass;

    //Database
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //gets rid of the action bar at the top
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        //Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //If logged in send to main
        if (mAuth.getCurrentUser() != null)
        {
            openMenuActivity();
        }

        //setting views
        regEmail = findViewById(R.id.regEmail);
        regPass = findViewById(R.id.regPass);
        regButton = findViewById(R.id.regButton);
        regLogin = findViewById(R.id.loginButton);


        //On click for register button
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = regEmail.getText().toString().trim();
                String pass = regPass.getText().toString().trim();

                if (email.isEmpty())
                {
                    toastMessage("Please place your email!");
                }

                if (pass.isEmpty())
                {
                    toastMessage("Please type a password!");
                }

                //Sending off to the database
                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            toastMessage("Registration Completed!");
                            openMainActivity();
                        }

                        else
                        {
                            toastMessage("Hmmm something went wrong..");
                            Log.d(TAG, "BIG ERROR : " + task);
                        }
                    }
                });
            }
        });



        //Sending new Activity to Login
        regLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openMainActivity();
            }
        });
    }

    private void toastMessage(String message){
        Toast.makeText(RegisterActivity.this,message,Toast.LENGTH_SHORT).show();
    }

    public void openMenuActivity(){
        Intent intent = new Intent(RegisterActivity.this, MenuActivity.class);
        startActivity(intent);
    }

    public void openMainActivity(){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
