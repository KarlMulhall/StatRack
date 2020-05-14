package com.example.StatRack;

public class Player
{
    private String name;
    private String age;
    private String position;
    private int appearances;
    private int goals;
    private int assists;
    private int yCards;
    private int rCards;
    private int attendance;


    public Player()
    {
        //empty constructor
    }

    public Player(String name, String age, String position,int appearances, int goals, int assists, int yCards, int rCards, int attendance) {
        this.name = name;
        this.age = age;
        this.position = position;
        this.appearances = appearances;
        this.goals = goals;
        this.assists = assists;
        this.yCards = yCards;
        this.rCards = rCards;
        this.attendance = attendance;
    }

    public int getAttendance() {
        return attendance;
    }

    public void setAttendance(int attendance) {
        this.attendance = attendance;
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

    public int getAppearances() {
        return appearances;
    }

    public void setAppearances(int goals) {
        this.appearances = appearances;
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
