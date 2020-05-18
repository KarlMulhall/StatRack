package com.example.StatRack.models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START comment_class]
@IgnoreExtraProperties
public class ChangedPlayer {

    public String uid;
    public String author;
    public String text;

    public ChangedPlayer() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public ChangedPlayer(String uid, String author, String text) {
        this.uid = uid;
        this.author = author;
        this.text = text;
    }

    public ChangedPlayer(String uid, String authorName, String nameText, String positionText) {
    }
}
// [END comment_class]
