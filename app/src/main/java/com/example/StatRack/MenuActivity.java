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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "MenuActivity";

    private Button btnSignOut, viewPlayers, viewEvents, viewNote, calendar;

    public String email;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //gets rid of the action bar at the top
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_main_menu);

        getSupportActionBar().hide();

        btnSignOut = (Button) findViewById(R.id.signOutButton);
        viewPlayers = (Button) findViewById(R.id.viewPlayersButton);
        viewEvents = (Button) findViewById(R.id.viewEventsButton);
        viewNote = (Button) findViewById(R.id.viewNoteButton);
        calendar = (Button) findViewById(R.id.calendarButton);

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

        viewPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openViewPlayers();
            }
        });

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCalendar();
            }
        });

        viewEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openViewEvents();
            }
        });

        viewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openViewNote();
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

    public void openViewPlayers(){
        Intent intent = new Intent(MenuActivity.this, SquadHubActivity.class);
        startActivity(intent);
    }

    public void openCalendar(){
        Intent intent = new Intent(MenuActivity.this, Calendar.class);
        startActivity(intent);
    }

    public void openViewEvents(){
        Intent intent = new Intent(MenuActivity.this, EventList.class);
        startActivity(intent);
    }

    public void openViewNote(){
        Intent intent = new Intent(MenuActivity.this, NoteList.class);
        startActivity(intent);
    }
}
