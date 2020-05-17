package com.example.StatRack;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
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

public class TestEventEdit extends AppCompatActivity {

    private static final String TAG = "TestEventEdit";

    private EditText eventInput, nameInput, timeInput, dateInput, locationInput;
    private Button back, edit;

    FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    int num;
    private DatabaseReference EventRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_event_edit);

        //Views
        eventInput = (EditText) findViewById(R.id.eventInput);
        nameInput = (EditText) findViewById(R.id.nameInput);
        timeInput = (EditText) findViewById(R.id.timeInput);
        dateInput = (EditText) findViewById(R.id.dateInput);
        locationInput = (EditText) findViewById(R.id.locationInput);
        back = (Button) findViewById(R.id.backButton);
        edit = (Button) findViewById(R.id.edit);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("users");
        FirebaseUser user = mAuth.getCurrentUser();
        String id = "";
        id = user.getUid();
        EventRef = myRef.child(id).child("events");
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

        EventRef.addValueEventListener(new ValueEventListener() {
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

                String event = eventInput.getText().toString().trim();
                String name = nameInput.getText().toString().trim();
                String time = timeInput.getText().toString().trim();
                String date = dateInput.getText().toString().trim();
                String location = locationInput.getText().toString().trim();

                String id = "";
                String id1 = (event);
                FirebaseUser user = mAuth.getCurrentUser();
                id = user.getUid();

                    myRef.child(id).child("events").child(id1).child("name").setValue(name);
                    myRef.child(id).child("events").child(id1).child("time").setValue(time);
                    myRef.child(id).child("events").child(id1).child("date").setValue(date);
                    myRef.child(id).child("events").child(id1).child("location").setValue(location);

                    toastMessage("Saving " + name + " to your events list...");

                    //resetting the data fields
                    eventInput.setText("");
                    nameInput.setText("");
                    timeInput.setText("");
                    dateInput.setText("");
                    locationInput.setText("");
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
        Toast.makeText(TestEventEdit.this,message,Toast.LENGTH_SHORT).show();
    }

    public void backToMenu(){
        Intent intent = new Intent(TestEventEdit.this, MenuActivity.class);
        startActivity(intent);
    }

}
