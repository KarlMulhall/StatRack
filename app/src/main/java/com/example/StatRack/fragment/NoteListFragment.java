package com.example.StatRack.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.StatRack.NoteDetailActivity;
import com.example.StatRack.R;
import com.example.StatRack.models.Note;
import com.example.StatRack.viewholder.NoteViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

public abstract class NoteListFragment extends Fragment {

    private static final String TAG = "NoteListFragment";

    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Note, NoteViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public NoteListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_notes, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRecycler = rootView.findViewById(R.id.noteList);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query notesQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Note>()
                .setQuery(notesQuery, Note.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Note, NoteViewHolder>(options) {

            @Override
            public NoteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new NoteViewHolder(inflater.inflate(R.layout.item_note, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(NoteViewHolder viewHolder, int position, final Note model) {
                final DatabaseReference noteRef = getRef(position);

                // Set click listener for the whole player view
                final String noteKey = noteRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PlayerDetailActivity
                        Intent intent = new Intent(getActivity(), NoteDetailActivity.class);
                        intent.putExtra(NoteDetailActivity.EXTRA_NOTE_KEY, noteKey);
                        startActivity(intent);
                    }
                });

                // Determine if the current user has liked this player and set UI accordingly
                if (model.stars.containsKey(getUid())) {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
                } else {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
                }

                // Bind Player to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToNote(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        // Need to write to both places the player is stored
                        DatabaseReference AllNotesRef = mDatabase.child(model.uid).child("notelist").child(noteRef.getKey());

                        // Run two transactions
                        onStarClicked(AllNotesRef);
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    // [START player_stars_transaction]
    private void onStarClicked(DatabaseReference noteRef) {
        noteRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Note n = mutableData.getValue(Note.class);
                if (n == null) {
                    return Transaction.success(mutableData);
                }

                if (n.stars.containsKey(getUid())) {
                    // Unstar the player and remove self from stars
                    n.starCount = n.starCount - 1;
                    n.stars.remove(getUid());
                } else {
                    // Star the player and add self to stars
                    n.starCount = n.starCount + 1;
                    n.stars.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(n);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
                Log.d(TAG, "playerTransaction:onComplete:" + databaseError);
            }
        });
    }
    // [END player_stars_transaction]


    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}
