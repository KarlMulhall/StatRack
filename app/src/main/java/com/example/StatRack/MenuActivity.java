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

    private Button btnSignOut, createPlayer, viewPlayers, editPlayer, deletePlayer, createEvent, viewEvents, editEvent, deleteEvents,
            createNote, editNote, deleteNote, createMatchLog, editMatchLog, deleteMatchLog;

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
        editPlayer = (Button) findViewById(R.id.editPlayerButton);
        deletePlayer = (Button) findViewById(R.id.deletePlayerButton);
        createEvent = (Button) findViewById(R.id.createEventButton);
        editEvent = (Button) findViewById(R.id.editEventButton);
        viewEvents = (Button) findViewById(R.id.viewEventsButton);
        deleteEvents = (Button) findViewById(R.id.deleteEventsButton);
        createNote = (Button) findViewById(R.id.createNoteButton);
        editNote = (Button) findViewById(R.id.editNoteButton);
        deleteNote = (Button) findViewById(R.id.deleteNoteButton);
        createMatchLog = (Button) findViewById(R.id.createMatchLogButton);
        editMatchLog = (Button) findViewById(R.id.editMatchLogButton);
        deleteMatchLog = (Button) findViewById(R.id.deleteMatchLogButton);

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

        editPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlayerEdit();

            }
        });

        deletePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlayerDelete();

            }
        });

        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEventCreation();

            }
        });

        editEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEventEdit();

            }
        });

        deleteEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEventDelete();

            }
        });

        viewEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openViewEvents();
            }
        });

        createNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNoteCreation();

            }
        });

        editNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNoteEdit();

            }
        });

        deleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNoteDelete();

            }
        });

        createMatchLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMatchLogCreation();

            }
        });

        editMatchLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMatchLogEdit();

            }
        });

        deleteMatchLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMatchLogDelete();

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

    public void openPlayerEdit(){
        Intent intent = new Intent(MenuActivity.this, PlayerEdit.class);
        startActivity(intent);
    }

    public void openPlayerDelete(){
        Intent intent = new Intent(MenuActivity.this, PlayerDelete.class);
        startActivity(intent);
    }

    public void openEventCreation(){
        Intent intent = new Intent(MenuActivity.this, TestEventCreation.class);
        startActivity(intent);
    }

    public void openEventEdit(){
        Intent intent = new Intent(MenuActivity.this, TestEventEdit.class);
        startActivity(intent);
    }

    public void openEventDelete(){
        Intent intent = new Intent(MenuActivity.this, TestEventDelete.class);
        startActivity(intent);
    }

    public void openViewEvents(){
        Intent intent = new Intent(MenuActivity.this, EventViewTest.class);
        startActivity(intent);
    }

    public void openNoteCreation(){
        Intent intent = new Intent(MenuActivity.this, NoteCreation.class);
        startActivity(intent);
    }

    public void openNoteEdit(){
        Intent intent = new Intent(MenuActivity.this, NoteEdit.class);
        startActivity(intent);
    }

    public void openNoteDelete(){
        Intent intent = new Intent(MenuActivity.this, NoteDelete.class);
        startActivity(intent);
    }

    public void openMatchLogCreation(){
        Intent intent = new Intent(MenuActivity.this, MatchLogCreate.class);
        startActivity(intent);
    }

    public void openMatchLogEdit(){
        Intent intent = new Intent(MenuActivity.this, MatchLogEdit.class);
        startActivity(intent);
    }

    public void openMatchLogDelete(){
        Intent intent = new Intent(MenuActivity.this, MatchLogDelete.class);
        startActivity(intent);
    }
}
