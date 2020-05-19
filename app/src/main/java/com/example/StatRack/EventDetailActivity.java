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

import com.example.StatRack.databinding.ActivityEventDetailBinding;
import com.example.StatRack.models.Comment;
import com.example.StatRack.models.Event;
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

public class EventDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private static final String TAG = "EventDetailActivity";

    public static final String EXTRA_EVENT_KEY = "event_key";

    private DatabaseReference mEventReference;
    private DatabaseReference mCommentsReference;
    private DatabaseReference mEditLocationReference;
    private DatabaseReference mEditTitleReference;
    private DatabaseReference mEditDateReference;
    private DatabaseReference mEditTimeReference;
    private ValueEventListener mEventListener;
    private String mEventKey;
    private CommentAdapter mAdapter;
    private ActivityEventDetailBinding binding;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        binding = ActivityEventDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        // Get event key from intent
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Get event key from intent
        mEventKey = getIntent().getStringExtra(EXTRA_EVENT_KEY);
        if (mEventKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_EVENT_KEY");
        }

        // Initialize Database
        // Initialize Database
        mEventReference = FirebaseDatabase.getInstance().getReference()
                .child(getUid()).child("events").child(mEventKey);
        mEditLocationReference = FirebaseDatabase.getInstance().getReference()
                .child(getUid()).child("events").child(mEventKey).child("location");
        mEditTitleReference = FirebaseDatabase.getInstance().getReference()
                .child(getUid()).child("events").child(mEventKey).child("title");
        mEditDateReference = FirebaseDatabase.getInstance().getReference()
                .child(getUid()).child("events").child(mEventKey).child("date");
        mEditTitleReference = FirebaseDatabase.getInstance().getReference()
                .child(getUid()).child("events").child(mEventKey).child("time");
        mCommentsReference = FirebaseDatabase.getInstance().getReference()
                .child(getUid()).child("event-comments").child(mEventKey);

        binding.buttonEventComment.setOnClickListener(this);
        binding.recyclerEventComments.setLayoutManager(new LinearLayoutManager(this));
        binding.buttonEventEdit.setOnClickListener(this);
        binding.buttonEventDelete.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the event
        // [START event_value_event_listener]
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Event object and use the values to update the UI
                Event event = dataSnapshot.getValue(Event.class);
                // [START_EXCLUDE]
                binding.eventTextLayout.eventTitle.setText(event.title);
                binding.eventTextLayout.eventLocation.setText(event.location);
                binding.eventTextLayout.eventDate.setText(event.date);
                binding.eventTextLayout.eventTime.setText(event.time);
                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Event failed, log a message
                Log.w(TAG, "loadEvent:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(EventDetailActivity.this, "Failed to load event.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mEventReference.addValueEventListener(eventListener);
        // [END event_value_event_listener]

        // Keep copy of event listener so we can remove it when app stops
        mEventListener = eventListener;

        binding.backToEventList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToEventList();
            }
        });

        // Listen for comments
        mAdapter = new CommentAdapter(this, mCommentsReference);
        binding.recyclerEventComments.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove event value event listener
        if (mEventListener != null) {
            mEventReference.removeEventListener(mEventListener);
        }

        // Clean up comments listener
        mAdapter.cleanupListener();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonEventComment) {
            eventComment();
        }else if (i == R.id.buttonEventEdit){
            eventEdit();
        }else if (i == R.id.buttonEventDelete){
            eventDelete();
        }
    }

    private void eventDelete(){
        mEventReference.removeValue();
    }

    private void eventEdit() {
        final String title = binding.eventTextLayout.eventTitle.getText().toString();
        final String location = binding.eventTextLayout.eventLocation.getText().toString();
        final String date = binding.eventTextLayout.eventDate.getText().toString();
        final String time = binding.eventTextLayout.eventTime.getText().toString();

        // Disable button so there are no multi-events
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
                            Toast.makeText(EventDetailActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new event
                            writeEditEvent(userId, title, location, date, time);
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

    private void writeEditEvent(String userId, String title, String location, String date, String time) {
        Event event = new Event(userId, title, location, date, time);
        mEditTitleReference.setValue(event.title);
        mEditLocationReference.setValue(event.location);
        mEditDateReference.setValue(event.date);
        mEditTimeReference.setValue(event.time);
    }

    public void backToEventList(){
        Intent intent = new Intent(EventDetailActivity.this, EventList.class);
        startActivity(intent);
    }

    private void eventComment() {
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
                    Log.w(TAG, "eventComments:onCancelled", databaseError.toException());
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