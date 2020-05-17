package com.example.StatRack.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyTopPlayersFragment extends PlayerListFragment {

    public MyTopPlayersFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // [START my_top_players_query]
        // My top players by number of stars
        String myUserId = getUid();
        Query myTopPlayersQuery = databaseReference.child(myUserId).child("squad")
                .orderByChild("goals");
        // [END my_top_players_query]

        return myTopPlayersQuery;
    }
}
