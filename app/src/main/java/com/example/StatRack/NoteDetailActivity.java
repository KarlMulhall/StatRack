package com.example.StatRack;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.StatRack.databinding.ActivityNoteDetailBinding;
import com.example.StatRack.models.Comment;
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
    private DatabaseReference mCommentsReference;
    private DatabaseReference mEditDescriptionReference;
    private DatabaseReference mEditTitleReference;
    private ValueEventListener mNoteListener;
    private String mNoteKey;
    private CommentAdapter mAdapter;
    private ActivityNoteDetailBinding binding;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        binding = ActivityNoteDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        // Get player key from intent
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Get player key from intent
        mNoteKey = getIntent().getStringExtra(EXTRA_NOTE_KEY);
        if (mNoteKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_NOTE_KEY");
        }

        // Initialize Database
        // Initialize Database
        mNoteReference = FirebaseDatabase.getInstance().getReference()
                .child(getUid()).child("notelist").child(mNoteKey);
        mEditDescriptionReference = FirebaseDatabase.getInstance().getReference()
                .child(getUid()).child("notelist").child(mNoteKey).child("description");
        mEditTitleReference = FirebaseDatabase.getInstance().getReference()
                .child(getUid()).child("notelist").child(mNoteKey).child("title");
        mCommentsReference = FirebaseDatabase.getInstance().getReference()
                .child(getUid()).child("note-comments").child(mNoteKey);

        binding.buttonNoteComment.setOnClickListener(this);
        binding.recyclerNoteComments.setLayoutManager(new LinearLayoutManager(this));
        binding.buttonNoteEdit.setOnClickListener(this);
        binding.buttonNoteDelete.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the player
        // [START player_value_event_listener]
        ValueEventListener noteListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Player object and use the values to update the UI
                Note note = dataSnapshot.getValue(Note.class);
                // [START_EXCLUDE]
                binding.noteTextLayout.noteTitle.setText(note.title);
                binding.noteTextLayout.noteDescription.setText(note.description);
                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Player failed, log a message
                Log.w(TAG, "loadNote:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(NoteDetailActivity.this, "Failed to load note.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mNoteReference.addValueEventListener(noteListener);
        // [END player_value_event_listener]

        // Keep copy of player listener so we can remove it when app stops
        mNoteListener = noteListener;

        binding.backToNoteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToNoteList();
            }
        });

        // Listen for comments
        mAdapter = new CommentAdapter(this, mCommentsReference);
        binding.recyclerNoteComments.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove player value event listener
        if (mNoteListener != null) {
            mNoteReference.removeEventListener(mNoteListener);
        }

        // Clean up comments listener
        mAdapter.cleanupListener();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonNoteComment) {
           noteComment();
        }else if (i == R.id.buttonNoteEdit){
            noteEdit();
        }else if (i == R.id.buttonNoteDelete){
            noteDelete();
        }
    }

    private void noteDelete(){
        mNoteReference.removeValue();
    }

    private void noteEdit() {
        final String title = binding.noteTextLayout.noteTitle.getText().toString();
        final String description = binding.noteTextLayout.noteDescription.getText().toString();

        // Disable button so there are no multi-players
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
                            Toast.makeText(NoteDetailActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new player
                            writeEditNote(userId, title, description);
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

    private void writeEditNote(String userId, String title, String description) {
        Note note = new Note(userId, title, description);
        mEditTitleReference.setValue(note.title);
        mEditDescriptionReference.setValue(note.description);
    }

    public void backToNoteList(){
        Intent intent = new Intent(NoteDetailActivity.this, NoteList.class);
        startActivity(intent);
    }

    private void noteComment() {
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user information
                        User user = dataSnapshot.getValue(User.class);
                        String authorName = user.username;

                        // Create new comment object
                        String commentText = binding.fieldCommentText.getText().toString();
                        Comment comment = new Comment(uid, authorName, commentText);

                        // Push the comment, it will appear in the list
                        mCommentsReference.push().setValue(comment);

                        // Clear the field
                        binding.fieldCommentText.setText(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private static class CommentViewHolder extends RecyclerView.ViewHolder {

        public TextView authorView;
        public TextView bodyView;

        public CommentViewHolder(View itemView) {
            super(itemView);

            authorView = itemView.findViewById(R.id.commentAuthor);
            bodyView = itemView.findViewById(R.id.commentBody);
        }
    }

    private static class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;

        private List<String> mCommentIds = new ArrayList<>();
        private List<Comment> mComments = new ArrayList<>();

        public CommentAdapter(final Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = ref;

            // Create child event listener
            // [START child_event_listener_recycler]
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new comment has been added, add it to the displayed list
                    Comment comment = dataSnapshot.getValue(Comment.class);

                    // [START_EXCLUDE]
                    // Update RecyclerView
                    mCommentIds.add(dataSnapshot.getKey());
                    mComments.add(comment);
                    notifyItemInserted(mComments.size() - 1);
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    Comment newComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Replace with the new data
                        mComments.set(commentIndex, newComment);

                        // Update the RecyclerView
                        notifyItemChanged(commentIndex);
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String commentKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Remove data from the list
                        mCommentIds.remove(commentIndex);
                        mComments.remove(commentIndex);

                        // Update the RecyclerView
                        notifyItemRemoved(commentIndex);
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    Comment movedComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "playerComments:onCancelled", databaseError.toException());
                    Toast.makeText(mContext, "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);
            // [END child_event_listener_recycler]

            // Store reference to listener so it can be removed on app stop
            mChildEventListener = childEventListener;
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            Comment comment = mComments.get(position);
            holder.authorView.setText(comment.author);
            holder.bodyView.setText(comment.text);
        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

    }
}
