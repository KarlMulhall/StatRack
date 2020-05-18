package com.example.StatRack;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.StatRack.databinding.ActivityNewNoteBinding;
import com.example.StatRack.models.Note;
import com.example.StatRack.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class NewNoteActivity extends AppCompatActivity {

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private static final String TAG = "NewNoteActivity";
    private static final String REQUIRED = "Required";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private ActivityNewNoteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        binding.submitNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitNote();
            }
        });
    }

    private void submitNote() {
        final String title = binding.fieldTitle.getText().toString();
        final String description = binding.fieldDescription.getText().toString();

        // Title is required
        if (TextUtils.isEmpty(title)) {
            binding.fieldTitle.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(description)) {
            binding.fieldDescription.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-notes
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
                            Toast.makeText(NewNoteActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new note
                            writeNewNote(userId, user.username, title, description);
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

    private void writeNewNote(String userId, String username, String title, String description) {
        String key = mDatabase.child("notes").push().getKey();
        Note note = new Note(userId, username, title, description);
        Map<String, Object> noteValues = note.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userId + "/notes/" + "/" + key, noteValues);

        mDatabase.updateChildren(childUpdates);
    }
}
