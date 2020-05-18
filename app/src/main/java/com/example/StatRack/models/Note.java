package com.example.StatRack.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Note
{

    public String uid;
    public String author;
    public String title;
    public String description;

    public Note()
    {
        //empty constructor
    }

    public Note(String uid, String author, String title, String description) {

        this.uid = uid;
        this.author = author;
        this.title = title;
        this.description = description;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("description", description);

        return result;
    }
}
