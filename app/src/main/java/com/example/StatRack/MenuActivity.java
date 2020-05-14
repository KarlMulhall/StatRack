package com.example.StatRack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "MenuActivity";

    private Button btnSignOut, createPlayer, viewPlayers, createEvent, viewEvents;

    public String email;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        btnSignOut = (Button) findViewById(R.id.signOutButton);
        createPlayer = (Button) findViewById(R.id.createPlayerButton);
        viewPlayers = (Button) findViewById(R.id.viewPlayersButton);
        createEvent = (Button) findViewById(R.id.createEventButton);
        viewEvents = (Button) findViewById(R.id.viewEventsButton);

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

        email = mAuth.getCurrentUser().getEmail();

        createPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlayerCreation();

            }
        });

        viewPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openViewPlayers();
            }
        });

        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEventCreation();

            }
        });

        viewEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openViewEvents();
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                signOut();
                toastMessage("Signing Out...");
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

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public void signOut(){
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void openPlayerCreation(){
        Intent intent = new Intent(MenuActivity.this, TestPlayerCreation.class);
        startActivity(intent);
    }

    public void openViewPlayers(){
        Intent intent = new Intent(MenuActivity.this, PlayerViewTest.class);
        startActivity(intent);
    }

    public void openEventCreation(){
        Intent intent = new Intent(MenuActivity.this, TestEventCreation.class);
        startActivity(intent);
    }

    public void openViewEvents(){
        Intent intent = new Intent(MenuActivity.this, EventViewTest.class);
        startActivity(intent);
    }
}
