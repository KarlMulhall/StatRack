package com.example.StatRack;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.StatRack.databinding.ActivityPlayerDetailEditBinding;
import com.example.StatRack.models.ChangedPlayer;
import com.example.StatRack.models.Comment;
import com.example.StatRack.models.Player;
import com.example.StatRack.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerDetailEdit extends AppCompatActivity implements View.OnClickListener {

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private static final String TAG = "PlayerDetailEdit";

    public static final String EXTRA_PLAYER_KEY = "player_key";

    private DatabaseReference mPlayerReference;
    private DatabaseReference mCommentsReference;
    private DatabaseReference mEditPositionReference;
    private DatabaseReference mEditNameReference;
    private ValueEventListener mPlayerListener;
    private String mPlayerKey;
    private CommentAdapter mAdapter;
    private ActivityPlayerDetailEditBinding binding;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerDetailEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Get player key from intent
        mPlayerKey = getIntent().getStringExtra(EXTRA_PLAYER_KEY);
        if (mPlayerKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_PLAYER_KEY");
        }

        // Initialize Database
        mPlayerReference = FirebaseDatabase.getInstance().getReference()
                .child(getUid()).child("squad").child(mPlayerKey);
        mEditPositionReference = FirebaseDatabase.getInstance().getReference()
                .child(getUid()).child("squad").child(mPlayerKey).child("position");
        mEditNameReference = FirebaseDatabase.getInstance().getReference()
                .child(getUid()).child("squad").child(mPlayerKey).child("name");
        mCommentsReference = FirebaseDatabase.getInstance().getReference()
                .child(getUid()).child("player-comments").child(mPlayerKey);

        binding.buttonPlayerComment.setOnClickListener(this);
        binding.recyclerPlayerComments.setLayoutManager(new LinearLayoutManager(this));
        binding.buttonPlayerEdit.setOnClickListener(this);
    }


    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the player
        // [START player_value_event_listener]
        ValueEventListener playerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Player object and use the values to update the UI
                Player player = dataSnapshot.getValue(Player.class);
                // [START_EXCLUDE]
                binding.playerAuthorLayout.playerAuthor.setText(player.author);
                binding.playerTextLayout.playerName.setText(player.name);
                binding.playerTextLayout.playerPosition.setText(player.position);
                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Player failed, log a message
                Log.w(TAG, "loadPlayer:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(PlayerDetailEdit.this, "Failed to load player.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mPlayerReference.addValueEventListener(playerListener);
        // [END player_value_event_listener]

        // Keep copy of player listener so we can remove it when app stops
        mPlayerListener = playerListener;

        // Listen for comments
        mAdapter = new CommentAdapter(this, mCommentsReference);
        binding.recyclerPlayerComments.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove player value event listener
        if (mPlayerListener != null) {
            mPlayerReference.removeEventListener(mPlayerListener);
        }

        // Clean up comments listener
        mAdapter.cleanupListener();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonPlayerComment) {
            playerComment();
        } else if (i == R.id.buttonPlayerEdit){
            playerEdit();
        }
    }

    private void playerEdit() {
        final String name = binding.playerTextLayout.playerName.getText().toString();
        final String position = binding.playerTextLayout.playerPosition.getText().toString();

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
                            Toast.makeText(PlayerDetailEdit.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new player
                            writeEditPlayer(userId, user.username, name, position);
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

    private void writeEditPlayer(String userId, String username, String name, String position) {
        // Create new player at /user-players/$userid/$playerid and at
        // /players/$playerid simultaneously
        //String key = FirebaseDatabase.getInstance().getReference()
//                .child(getUid()).child("squad").push().getKey();
//        Player player = new Player(userId, username, name, position);
//        Map<String, Object> playerValues = player.toMap();
//
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put(mPlayerKey , playerValues);
//
//        mPlayerReference.updateChildren(childUpdates);
        Player player = new Player(userId, username, name, position);
        mEditNameReference.setValue(player.name);
        mEditPositionReference.setValue(player.position);
    }

    private void playerComment() {
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