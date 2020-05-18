package com.example.StatRack.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyNotesFragment extends NoteListFragment {

    public MyNotesFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my notes
        return databaseReference.child(getUid())
                .child("notes");
    }
}
