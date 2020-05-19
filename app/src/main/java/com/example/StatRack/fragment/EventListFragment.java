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

import com.example.StatRack.EventDetailActivity;
import com.example.StatRack.R;
import com.example.StatRack.models.Event;
import com.example.StatRack.viewholder.EventViewHolder;
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

public abstract class EventListFragment extends Fragment {

    private static final String TAG = "EventListFragment";

    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Event, EventViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public EventListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_events, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRecycler = rootView.findViewById(R.id.eventList);
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
        Query eventsQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(eventsQuery, Event.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(options) {

            @Override
            public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new EventViewHolder(inflater.inflate(R.layout.item_event, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(EventViewHolder viewHolder, int position, final Event model) {
                final DatabaseReference eventRef = getRef(position);

                // Set click listener for the whole player view
                final String eventKey = eventRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PlayerDetailActivity
                        Intent intent = new Intent(getActivity(), EventDetailActivity.class);
                        intent.putExtra(EventDetailActivity.EXTRA_EVENT_KEY, eventKey);
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
                viewHolder.bindToEvent(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        // Need to write to both places the player is stored
                        DatabaseReference AllEventsRef = mDatabase.child(model.uid).child("eventlist").child(eventRef.getKey());

                        // Run two transactions
                        onStarClicked(AllEventsRef);
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    // [START player_stars_transaction]
    private void onStarClicked(DatabaseReference eventRef) {
        eventRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event e = mutableData.getValue(Event.class);
                if (e == null) {
                    return Transaction.success(mutableData);
                }

                if (e.stars.containsKey(getUid())) {
                    // Unstar the player and remove self from stars
                    e.starCount = e.starCount - 1;
                    e.stars.remove(getUid());
                } else {
                    // Star the player and add self to stars
                    e.starCount = e.starCount + 1;
                    e.stars.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(e);
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
