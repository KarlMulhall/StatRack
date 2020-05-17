package com.example.StatRack.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyPlayersFragment extends PlayerListFragment {

    public MyPlayersFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my players
        return databaseReference.child(getUid())
                .child("squad");
    }
}
