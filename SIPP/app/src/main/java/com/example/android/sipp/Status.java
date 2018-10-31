package com.example.android.sipp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class Status extends AppCompatActivity {
    private String keyId;
    private Integer votes;

    private TextView mMyName;
    private TextView mHighScoreName;
    private TextView mLowestScoreName;
    private TextView mMyScoreStatus;
    private TextView mHighestScoreStatus;
    private TextView mLowestScoreStatus;
    private String currentUserId;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private Button mBackButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mMyScoreStatus = findViewById(R.id.txt_my_status_score);
        mHighestScoreStatus = findViewById(R.id.txt_highest_status_score);
        mLowestScoreStatus = findViewById(R.id.txt_lowest_status_score);
        mBackButton = findViewById(R.id.btn_back);

        mMyName = findViewById(R.id.txt_my_status_name);
        mHighScoreName = findViewById(R.id.txt_highest_status_name);
        mLowestScoreName = findViewById(R.id.txt_lowest_status_name);
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUserId = mFirebaseAuth.getCurrentUser().getUid();

        //DataBase Reference
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Users");

        myStatus();
        highestStatus();
        lowestStatus();

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Status.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void lowestStatus() {
        Query myMostViewedPostsQuery = mDatabaseReference.orderByChild("votes").limitToFirst(1);
        myMostViewedPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<?,?> map = (HashMap<?,?>) dataSnapshot.getValue();
                for(Map.Entry m:map.entrySet()){
                    HashMap<?,?> map1 = (HashMap<?,?>) m.getValue();
                    String s = map1.get("votes").toString();
                    String name = map1.get("name").toString();
                    mLowestScoreName.setText(name);
                    mLowestScoreStatus.setText(s);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void highestStatus() {
        // Most viewed posts
        Query myMostViewedPostsQuery = mDatabaseReference.orderByChild("votes").limitToLast(1);
        myMostViewedPostsQuery.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<?,?> map = (HashMap<?,?>) dataSnapshot.getValue();
                for(Map.Entry m:map.entrySet()){
                    HashMap<?,?> map1 = (HashMap<?,?>) m.getValue();
                    String s = map1.get("votes").toString();
                    String name = map1.get("name").toString();
                    mHighScoreName.setText(name);
                    mHighestScoreStatus.setText(s);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void myStatus() {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
//                EachPerson post = dataSnapshot.getValue(EachPerson.class);
                mMyName.setText(dataSnapshot.child(currentUserId).child("name").getValue().toString());
                mMyScoreStatus.setText(dataSnapshot.child(currentUserId).child("votes").getValue().toString());
                // ...
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Status", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabaseReference.addValueEventListener(postListener);
    }
}
