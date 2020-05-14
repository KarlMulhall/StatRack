package com.example.StatRack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mEmail,mPassword;
    private Button btnSignIn, regButton;

    //validation
    private Boolean validEmptyEmailPass()
    {
        String email = mEmail.getText().toString();
        String pass = mPassword.getText().toString();

        //EMPTY
        if (email.isEmpty())
        {
            mEmail.setError("You need to enter a email!");
            return false;
        }
        else if (pass.isEmpty())
        {
            mPassword.setError("You to enter a password!");
            return false;
        }


        else
        {
            mEmail.setError(null);
            mPassword.setError(null);
            return true;
        }
    }

    private Boolean validpassLength()
    {
        String pass = mPassword.getText().toString();

        if (pass.length() <= 5)
        {
            mPassword.setError("We need a longer password!");
            return false;
        }
        else
        {
            mPassword.setError(null);
            return true;
        }
    }

    private Boolean validEmail()
    {
        String email = mEmail.getText().toString();
        String emailPrefix = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (!email.matches(emailPrefix))
        {
            mEmail.setError("Invalid Email!");
            return false;
        }

        else
        {
            mEmail.setError(null);
            return false;
        }
    }

    private Boolean seriousPassValid()
    {
        String pass = mPassword.getText().toString();
        String passwordValid = "^" +
                //"(?=.*[0-9])" +               //at least 1 digit
                //"(?=.*[a-z])" +               //at least 1 lower case char
                //"(?=.*[A-Z])" +               //at least 1 upper case char
                "(?=.*[a-zA-Z])" +              //any letter
                "(?=.*[@#$%^&+=])" +            //at least 1 special char
                "(?=\\S+$)" +                   //no spaces
                ".{4,}" +                       //at least 4 char
                "$";

        if (!pass.matches(passwordValid))
        {
            mPassword.setError("Password is weak!");
            return false;
        }

        else
        {
            mPassword.setError(null);
            return  true;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        //gets rid of the action bar at the top
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        //declare buttons and edit texts on create
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        btnSignIn = (Button) findViewById(R.id.signInButton);
        regButton = (Button) findViewById(R.id.buttonRegister);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    //user is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully Signed In with: " + user.getEmail());
                }else{
                    //user is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully Signed Out");
                }
            }
        };

        //If logged in send to main
        if (mAuth.getCurrentUser() != null)
        {
            openMenuActivity();
        }

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterActivity();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String pass = mPassword.getText().toString();

                //All validation checks
                validEmptyEmailPass();
                validpassLength();
                validEmail();
                seriousPassValid();




                if (!email.equals("") && !pass.equals("")){
                    mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                openMenuActivity();
                            }
                            else{
                                toastMessage("Login Failed");
                            }
                        }
                    });
                }else{
                    toastMessage("Required Fields not filled");
                }

            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop(){
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //add a toast to show when successfully signed in
    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT).show();
    }

    public void openMenuActivity(){
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(intent);
    }

    public void openRegisterActivity(){
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

}
