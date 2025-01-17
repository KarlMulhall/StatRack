package com.example.StatRack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TestPlayerCreation extends AppCompatActivity {

    private static final String TAG = "TestPlayerCreation";

    private EditText nameInput, positionInput, ageInput;
    private Button back, addToDatabase;
    private LottieAnimationView tickAnimation;

    FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    int num;
    private DatabaseReference PlayerRef;

    String[] positions = {"Goalkeeper", "Defender", "Midfielder", "Forward"};

    private Boolean validatePosition() {
        String position = positionInput.getText().toString();

        if (!position.matches(String.valueOf(positions)))
        {
            positionInput.setError("Please pick one from the suggested list");
            return false;
        }

        else
        {
            positionInput.setError(null);
            return true;
        }
    }

    private Boolean validateAge() {
        int age = Integer.parseInt(ageInput.getText().toString());
        if (age > 80)
        {
            ageInput.setError("You're to old...");
            return false;
        }
         else {
            ageInput.setError(null);
            return true;
        }
    }

    private Boolean validateName() {
        String val = nameInput.getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if (val.isEmpty()) {
            nameInput.setError("Field cannot be empty");
            return false;
        } else if (val.length() >= 15) {
            nameInput.setError("Name is too long");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            nameInput.setError("Spaces are not allowed");
            return false;
        } else {
            nameInput.setError(null);
            return true;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_player_creation);

        //Views
        nameInput = (EditText) findViewById(R.id.nameInput);
        ageInput = (EditText) findViewById(R.id.ageInput);
        positionInput = (AutoCompleteTextView) findViewById(R.id.positionInput);
        back = (Button) findViewById(R.id.backButton);
        addToDatabase = (Button) findViewById(R.id.addToDatabaseButton);
        //animation
        tickAnimation = findViewById(R.id.tickAnmimation);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, positions);
        AutoCompleteTextView autoTextfield = (AutoCompleteTextView) findViewById(R.id.positionInput);
        autoTextfield.setThreshold(1);
        autoTextfield.setAdapter(adapter);

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
                tickAnimation.isAnimating();
                tickAnimation.setVisibility(View.VISIBLE);

                String name = nameInput.getText().toString().trim();
                String age = ageInput.getText().toString().trim();
                String position = positionInput.getText().toString().trim();

                if(!name.equals("")){
                    String userID = "";
                    String playerID = ("player" + num);
                    FirebaseUser user = mAuth.getCurrentUser();
                    userID = user.getUid();
                    myRef.child(userID).child("squad").child(playerID).child("name").setValue(name);
                    myRef.child(userID).child("squad").child(playerID).child("age").setValue(age);
                    myRef.child(userID).child("squad").child(playerID).child("position").setValue(position);
                    myRef.child(userID).child("squad").child(playerID).child("appearances").setValue(0);
                    myRef.child(userID).child("squad").child(playerID).child("goals").setValue(0);
                    myRef.child(userID).child("squad").child(playerID).child("assists").setValue(0);
                    myRef.child(userID).child("squad").child(playerID).child("yellow cards").setValue(0);
                    myRef.child(userID).child("squad").child(playerID).child("red cards").setValue(0);
                    myRef.child(userID).child("squad").child(playerID).child("attendance").setValue(0);

                    toastMessage("Saving " + name + " to your squad...");

                    //resetting the data fields
                    nameInput.setText("");
                    ageInput.setText("");
                    positionInput.setText("");
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
        Toast.makeText(TestPlayerCreation.this,message,Toast.LENGTH_SHORT).show();
    }

    public void backToMenu(){
        Intent intent = new Intent(TestPlayerCreation.this, MenuActivity.class);
        startActivity(intent);
    }

}
