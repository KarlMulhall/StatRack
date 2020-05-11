package com.example.StatRack;

public class Player
{
    private String name;
    private String age;
    private String position;
    private int goals;
    private int assists;
    private int yCards;
    private int rCards;


    public Player()
    {
        //empty constructor
    }

    public Player(String name, String age, String position, int goals, int assists, int yCards, int rCards) {
        this.name = name;
        this.age = age;
        this.position = position;
        this.goals = goals;
        this.assists = assists;
        this.yCards = yCards;
        this.rCards = rCards;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public int getyCards() {
        return yCards;
    }

    public void setyCards(int yCards) {
        this.yCards = yCards;
    }

    public int getrCards() {
        return rCards;
    }

    public void setrCards(int rCards) {
        this.rCards = rCards;
    }
}
