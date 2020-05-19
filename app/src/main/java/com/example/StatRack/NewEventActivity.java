package com.example.StatRack;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.StatRack.databinding.ActivityNewEventBinding;
import com.example.StatRack.models.Event;
import com.example.StatRack.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class NewEventActivity extends AppCompatActivity {

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private static final String TAG = "NewEventActivity";
    private static final String REQUIRED = "Required";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private ActivityNewEventBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //gets rid of the action bar at the top
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        binding = ActivityNewEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        binding.submitEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitEvent();
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    private void submitEvent() {
        final String title = binding.fieldTitle.getText().toString();
        final String location = binding.fieldLocation.getText().toString();
        final String date = binding.fieldDate.getText().toString();
        final String time = binding.fieldTime.getText().toString();

        // Title is required
        if (TextUtils.isEmpty(title)) {
            binding.fieldTitle.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(location)) {
            binding.fieldLocation.setError(REQUIRED);
            return;
        }

        if (TextUtils.isEmpty(date)) {
            binding.fieldDate.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(time)) {
            binding.fieldTime.setError(REQUIRED);
            return;
        }

        Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewEventActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new player
                            writeNewEvent(userId, title, location, date, time);
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

    private void writeNewEvent(String userId, String title, String location, String date, String time) {

        String key = mDatabase.child("events").push().getKey();
        Event event = new Event(userId, title, location, date, time);
        Map<String, Object> eventValues = event.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userId + "/events/" + "/" + key, eventValues);

        mDatabase.updateChildren(childUpdates);
    }

    private void back(){
        finish();
    }
}
