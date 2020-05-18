package com.example.StatRack.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyTopNotesFragment extends NoteListFragment {

    public MyTopNotesFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // [START my_top_notes_query]
        String myUserId = getUid();
        Query myTopNotesQuery = databaseReference.child(myUserId).child("notes")
                .orderByChild("");
        // [END my_top_players_query]

        return myTopNotesQuery;
    }
}
