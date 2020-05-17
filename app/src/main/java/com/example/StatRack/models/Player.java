package com.example.StatRack.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Player
{

    public String uid;
    public String author;
    public String name;
    public String position;
    public int appearances = 0;
    public int goals = 0;
    public int assists = 0;
    public int yCards = 0;
    public int rCards = 0;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public Player()
    {
        //empty constructor
    }

    public Player(String uid, String author, String name, String position) {

        this.uid = uid;
        this.author = author;
        this.name = name;
        this.position = position;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("name", name);
        result.put("position", position);
        result.put("appearances", appearances);
        result.put("goals", goals);
        result.put("assists", assists);
        result.put("yellow cards", yCards);
        result.put("red cards", rCards);
        result.put("starCount", starCount);
        result.put("stars", stars);

        return result;
    }
}