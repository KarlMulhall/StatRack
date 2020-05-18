package com.example.StatRack;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.StatRack.databinding.ActivityNoteDetailBinding;
import com.example.StatRack.models.Note;
import com.example.StatRack.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NoteDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private static final String TAG = "NoteDetailActivity";

    public static final String EXTRA_NOTE_KEY = "note_key";

    private DatabaseReference mNoteReference;
    private ValueEventListener mNoteListener;
    private String mNoteKey;
    private ActivityNoteDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get note key from intent
        mNoteKey = getIntent().getStringExtra(EXTRA_NOTE_KEY);
        if (mNoteKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_NOTE_KEY");
        }

        // Initialize Database
        mNoteReference = FirebaseDatabase.getInstance().getReference()
                .child(getUid()).child("notes").child(mNoteKey);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the note
        // [START note_value_event_listener]
        ValueEventListener noteListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Note object and use the values to update the UI
                Note note = dataSnapshot.getValue(Note.class);
                // [START_EXCLUDE]
                binding.noteAuthorLayout.noteAuthor.setText(note.author);
                binding.noteTextLayout.noteTitle.setText(note.title);
                binding.noteTextLayout.noteDescription.setText(note.description);
                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Note failed, log a message
                Log.w(TAG, "loadNote:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(NoteDetailActivity.this, "Failed to load note.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mNoteReference.addValueEventListener(noteListener);
        // [END note_value_event_listener]

        // Keep copy of note listener so we can remove it when app stops
        mNoteListener = noteListener;
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove note value event listener
        if (mNoteListener != null) {
            mNoteReference.removeEventListener(mNoteListener);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
    }
}

