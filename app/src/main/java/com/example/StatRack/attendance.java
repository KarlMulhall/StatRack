package com.example.StatRack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class attendance extends AppCompatActivity {
    private static final String TAG = "ATTENDANCE_TAG";
    TextView title;
    ListView players;
    Button back;
    RadioButton button1, button2, button3, button4, button5, button6, button7;
    private String userID;
    int num;
    int counter = 0;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef, PlayerRef, appearRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        title = (TextView) findViewById(R.id.title);
        players = (ListView) findViewById(R.id.players);
        back = (Button) findViewById(R.id.back);
        button1 = (RadioButton) findViewById(R.id.radio1);
        button2 = (RadioButton) findViewById(R.id.radio2);
        button3 = (RadioButton) findViewById(R.id.radio3);
        button4 = (RadioButton) findViewById(R.id.radio4);
        button5 = (RadioButton) findViewById(R.id.radio5);
        button6 = (RadioButton) findViewById(R.id.radio6);
        button7 = (RadioButton) findViewById(R.id.radio7);



        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("users");
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();



        //Listener to get current user
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //user is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully Signed In with: " + user.getEmail());
                } else {
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

        //applying functionality to back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuActivity();
            }
        });
    }

    private void openMenuActivity() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }




    private void showData(DataSnapshot dataSnapshot) {

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Player player1 = new Player();
            Player player02 = new Player();
            Player player03 = new Player();
            Player player04 = new Player();
            Player player05 = new Player();


            String id0 = ("player" + 0);
            String id1 = ("player" + num);
            String id2 = ("player" + 2);
            String id3 = ("player" + 3);
            String id4 = ("player" + 4);



            player1.setName(ds.child(userID).child("squad").child(id0).getValue(Player.class).getName());
            displayRadioOne();
            //player1.setAge(ds.child(userID).child("squad").child(id1).getValue(Player.class).getAge());
            //player1.setPosition(ds.child(userID).child("squad").child(id1).getValue(Player.class).getPosition());

            player02.setName(ds.child(userID).child("squad").child(id1).getValue(Player.class).getName());
            displayRadioTwo();

            player03.setName(ds.child(userID).child("squad").child(id2).getValue(Player.class).getName());
            displayRadioThree();

            player04.setName(ds.child(userID).child("squad").child(id3).getValue(Player.class).getName());
            displayRadioFour();

            player05.setName(ds.child(userID).child("squad").child(id4).getValue(Player.class).getName());
            displayRadioFive();



            Log.d(TAG, "showData: name: " + player1.getName());
           // Log.d(TAG, "showData: age: " + player1.getAge());
           // Log.d(TAG, "showData: position: " + player1.getPosition());

            ArrayList<String> array = new ArrayList<>();
            array.add(player1.getName());
            array.add(player02.getName());
            array.add(player03.getName());
            array.add(player04.getName());
            array.add(player05.getName());



            //array.add(player1.getPosition());

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array);
            players.setAdapter(adapter);
        }
    }



    private void displayRadioFive() {

        button5.setVisibility(View.VISIBLE);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add one to appearance
                int counter =0;
                String id = "";
                FirebaseUser user = mAuth.getCurrentUser();
                id = user.getUid();
                //add one to counter when clicked
                myRef.child(id).child("squad").child("player4").child("appearances").setValue(counter + 1);
            }
        });
    }

    private void displayRadioFour() {
        button4.setVisibility(View.VISIBLE);
    }

    private void displayRadioThree() {
        button3.setVisibility(View.VISIBLE);
    }

    private void displayRadioTwo() {
        button2.setVisibility(View.VISIBLE);
    }

    private void displayRadioOne() {
        button1.setVisibility(View.VISIBLE);
    }

    private void toastMessage (String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void onStart() {

        super.onStart();
    }
}
