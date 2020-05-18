package com.example.StatRack;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.StatRack.databinding.ActivityNewPlayerBinding;
import com.example.StatRack.models.Player;
import com.example.StatRack.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class NewPlayerActivity extends AppCompatActivity {

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private static final String TAG = "NewPlayerActivity";
    private static final String REQUIRED = "Required";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private ActivityNewPlayerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        binding.submitPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPlayer();
            }
        });
    }

    private void submitPlayer() {
        final String name = binding.fieldName.getText().toString();
        final String position = binding.fieldPosition.getText().toString();

        // Title is required
        if (TextUtils.isEmpty(name)) {
            binding.fieldName.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(position)) {
            binding.fieldPosition.setError(REQUIRED);
            return;
        }

        Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]
        final String userId = getUid();
        mDatabase.child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewPlayerActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new player
                            writeNewPlayer(userId, name, position);
                        }

                        // Finish this Activity, back to the stream
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());

                    }
                });
    }

    private void writeNewPlayer(String userId, String name, String position) {

        String key = mDatabase.child("squad").push().getKey();
        Player player = new Player(userId, name, position);
        Map<String, Object> playerValues = player.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userId + "/squad/" + "/" + key, playerValues);

        mDatabase.updateChildren(childUpdates);
    }
}
