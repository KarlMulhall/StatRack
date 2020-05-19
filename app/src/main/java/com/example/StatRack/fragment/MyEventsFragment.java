package com.example.StatRack.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyEventsFragment extends EventListFragment {

    public MyEventsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my events
        return databaseReference.child(getUid())
                .child("eventlist");
    }
}