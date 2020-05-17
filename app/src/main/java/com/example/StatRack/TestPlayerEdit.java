package com.example.StatRack;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TestPlayerEdit extends AppCompatActivity {

    private static final String TAG = "TestPlayerEdit";

    private EditText playerInput, nameInput, positionInput, ageInput;
    private Button back, edit;

    FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    int num;
    private DatabaseReference PlayerRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_edit);

        //Views
        playerInput = (EditText) findViewById(R.id.playerInput);
        nameInput = (EditText) findViewById(R.id.nameInput);
        ageInput = (EditText) findViewById(R.id.ageInput);
        positionInput = (AutoCompleteTextView) findViewById(R.id.positionInput);
        back = (Button) findViewById(R.id.back);
        edit = (Button) findViewById(R.id.edit);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("users");
        FirebaseUser user = mAuth.getCurrentUser();
        String id = "";
        id = user.getUid();
        PlayerRef = myRef.child(id).child("squad");
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

            private void toastMessage(String s) {
            }
        };

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + value);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        PlayerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + value);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String player = playerInput.getText().toString().trim();
                String name = nameInput.getText().toString().trim();
                String age = ageInput.getText().toString().trim();
                String position = positionInput.getText().toString().trim();

                String id = "";
                String id1 = (player);
                FirebaseUser user = mAuth.getCurrentUser();
                id = user.getUid();

                myRef.child(id).child("squad").child(id1).child("name").setValue(name);
                myRef.child(id).child("squad").child(id1).child("age").setValue(age);
                myRef.child(id).child("squad").child(id1).child("position").setValue(position);
                myRef.child(id).child("squad").child(id1).child("appearances").setValue(0);
                myRef.child(id).child("squad").child(id1).child("goals").setValue(0);
                myRef.child(id).child("squad").child(id1).child("assists").setValue(0);
                myRef.child(id).child("squad").child(id1).child("yellow cards").setValue(0);
                myRef.child(id).child("squad").child(id1).child("red cards").setValue(0);
                myRef.child(id).child("squad").child(id1).child("attendance").setValue(0);

                toastMessage("Saving " + player + " update...");

                //resetting the data fields
                playerInput.setText("");
                nameInput.setText("");
                ageInput.setText("");
                positionInput.setText("");
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToMenu();
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
        Toast.makeText(TestPlayerEdit.this,message,Toast.LENGTH_SHORT).show();
    }

    public void backToMenu(){
        Intent intent = new Intent(TestPlayerEdit.this, MenuActivity.class);
        startActivity(intent);
    }

}