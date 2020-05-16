package com.example.StatRack;

public class MatchLog
{
    private String opposition;
    private String location;
    private String starttime;
    private String endtime;
    private String date;
    private String goalsscored;
    private String oppositionscore;
    private String injuries;
    private String absences;
    private String yellowcards;
    private String redcards;

    public MatchLog()
    {
        //empty constructor
    }

    public MatchLog(String opposition, String location, String starttime, String endtime, String date, String goalsscored,
                    String oppositionscore, String injuries, String absences, String yellowcards, String redcards) {
        this.opposition = opposition;
        this.location = location;
        this.starttime = starttime;
        this.endtime = endtime;
        this.date = date;
        this.goalsscored = goalsscored;
        this.oppositionscore = oppositionscore;
        this.injuries = injuries;
        this.absences = absences;
        this.yellowcards = yellowcards;
        this.redcards = redcards;
    }

    public String getOpposition() {
        return opposition;
    }

    public void setOpposition(String opposition) {
        this.opposition = opposition;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGoalsscored() {
        return goalsscored;
    }

    public void setGoalsscored(String goalsscored) {
        this.goalsscored = goalsscored;
    }

    public String getOppositionscore() {
        return oppositionscore;
    }

    public void setOppositionscore(String oppositionscore) {
        this.oppositionscore = oppositionscore;
    }

    public String getInjuries() {
        return injuries;
    }

    public void setInjuries(String injuries) {
        this.injuries = injuries;
    }

    public String getAbsences() {
        return absences;
    }

    public void setAbsences(String absences) {
        this.absences = absences;
    }

    public String getYellowcards() {
        return yellowcards;
    }

    public void setYellowcards(String yellowcards) {
        this.yellowcards = yellowcards;
    }

    public String getRedcards() {
        return redcards;
    }

    public void setRedcards(String redcards) {
        this.redcards = redcards;
    }
}

