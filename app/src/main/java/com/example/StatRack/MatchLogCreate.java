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

public class MatchLogCreate extends AppCompatActivity {

    private static final String TAG = "MatchLogCreate";

    private EditText oppositionInput, locationInput, starttimeInput, endtimeInput, dateInput,
            goalsscoredInput, oppositionscoreInput, injuriesInput, absencesInput, yellowcardsInput, redcardsInput;
    private Button back, addToDatabase;

    FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    int num;
    private DatabaseReference MatchLogRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_log_create);

        //Views
        oppositionInput = (EditText) findViewById(R.id.oppositionInput);
        locationInput = (EditText) findViewById(R.id.locationInput);
        starttimeInput = (EditText) findViewById(R.id.starttimeInput);
        endtimeInput = (EditText) findViewById(R.id.endtimeInput);
        dateInput = (EditText) findViewById(R.id.dateInput);
        goalsscoredInput = (EditText) findViewById(R.id.goalsscoredInput);
        oppositionscoreInput = (EditText) findViewById(R.id.oppositionscoreInput);
        injuriesInput = (EditText) findViewById(R.id.injuriesInput);
        absencesInput = (EditText) findViewById(R.id.absencesInput);
        yellowcardsInput = (EditText) findViewById(R.id.yellowcardsInput);
        redcardsInput = (EditText) findViewById(R.id.redcardsInput);
        back = (Button) findViewById(R.id.backButton);
        addToDatabase = (Button) findViewById(R.id.addToDatabaseButton);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("users");
        FirebaseUser user = mAuth.getCurrentUser();
        String id = "";
        id = user.getUid();
        MatchLogRef = myRef.child(id).child("matchlogs");
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

        MatchLogRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + value);

                if(dataSnapshot.exists()){
                    num = (int) dataSnapshot.getChildrenCount();
                }else{
                    num = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        addToDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String opposition = oppositionInput.getText().toString().trim();
                String location = locationInput.getText().toString().trim();
                String starttime = starttimeInput.getText().toString().trim();
                String endtime = endtimeInput.getText().toString().trim();
                String date = dateInput.getText().toString().trim();
                String goalsscored = goalsscoredInput.getText().toString().trim();
                String oppositionscore = oppositionscoreInput.getText().toString().trim();
                String injuries = injuriesInput.getText().toString().trim();
                String absences = absencesInput.getText().toString().trim();
                String yellowcards = yellowcardsInput.getText().toString().trim();
                String redcards = redcardsInput.getText().toString().trim();

                if(!opposition.equals("")){
                    String id = "";
                    String id1 = ("matchlog" + num);
                    FirebaseUser user = mAuth.getCurrentUser();
                    id = user.getUid();
                    myRef.child(id).child("matchlogs").child(id1).child("opposition").setValue(opposition);
                    myRef.child(id).child("matchlogs").child(id1).child("location").setValue(location);
                    myRef.child(id).child("matchlogs").child(id1).child("starttime").setValue(starttime);
                    myRef.child(id).child("matchlogs").child(id1).child("endtime").setValue(endtime);
                    myRef.child(id).child("matchlogs").child(id1).child("date").setValue(date);
                    myRef.child(id).child("matchlogs").child(id1).child("goalsscored").setValue(goalsscored);
                    myRef.child(id).child("matchlogs").child(id1).child("oppositionscore").setValue(oppositionscore);
                    myRef.child(id).child("matchlogs").child(id1).child("injuries").setValue(injuries);
                    myRef.child(id).child("matchlogs").child(id1).child("absences").setValue(absences);
                    myRef.child(id).child("matchlogs").child(id1).child("yellowcards").setValue(yellowcards);
                    myRef.child(id).child("matchlogs").child(id1).child("redcards").setValue(redcards);

                    toastMessage("Saving " + opposition + " match log to your events list...");

                    //resetting the data fields
                    oppositionInput.setText("");
                    locationInput.setText("");
                    starttimeInput.setText("");
                    endtimeInput.setText("");
                    dateInput.setText("");
                    goalsscoredInput.setText("");
                    oppositionscoreInput.setText("");
                    injuriesInput.setText("");
                    absencesInput.setText("");
                    yellowcardsInput.setText("");
                    redcardsInput.setText("");
                }
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
        Toast.makeText(MatchLogCreate.this,message,Toast.LENGTH_SHORT).show();
    }

    public void backToMenu(){
        Intent intent = new Intent(MatchLogCreate.this, MenuActivity.class);
        startActivity(intent);
    }

}
