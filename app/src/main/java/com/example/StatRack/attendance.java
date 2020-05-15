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
    private String userID;
    int num;
    ArrayList<String> playerList;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef, PlayerRef, secondPlayerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        //array adapter
        final ArrayAdapter<String> playerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, playerList);

        title = (TextView) findViewById(R.id.title);
        players = (ListView) findViewById(R.id.players);
        back = (Button) findViewById(R.id.back);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        PlayerRef = myRef.child(userID).child("squad");
        secondPlayerRef = myRef.child(userID).child("squad");

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



        PlayerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + value);

                if(dataSnapshot.exists()){
                    num = (int) dataSnapshot.getChildrenCount();
                }else{
                    num = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //snapshot of the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
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
            String id1 = ("player" + num);
            player1.setName(ds.child(userID).child("squad").child(id1).getValue(Player.class).getName());
            player1.setAge(ds.child(userID).child("squad").child(id1).getValue(Player.class).getAge());
            player1.setPosition(ds.child(userID).child("squad").child(id1).getValue(Player.class).getPosition());

            Log.d(TAG, "showData: name: " + player1.getName());
            Log.d(TAG, "showData: age: " + player1.getAge());
            Log.d(TAG, "showData: position: " + player1.getPosition());

            ArrayList<String> array = new ArrayList<>();
            array.add(player1.getName());
            array.add(player1.getAge());
            array.add(player1.getPosition());

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array);
            players.setAdapter(adapter);
        }
    }

    private void toastMessage (String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
