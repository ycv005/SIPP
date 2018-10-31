package com.example.android.sipp;
import android.support.v7.app.AppCompatActivity;

public class EachPerson extends AppCompatActivity {

    private String userID;
    private int votes;
    private String name;
    private String pollImages;

    public  EachPerson(){

    }
    public EachPerson(String userID,String name,String pollImages,int votes){
        this.name = name;
        this.userID = userID;
        this.pollImages = pollImages;
        this.votes = votes;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getName() {
        return name;
    }

    public String getUserID() {
        return userID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return pollImages;
    }

    public void setpollImages(String pollImages) {
        this.pollImages = pollImages;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}

