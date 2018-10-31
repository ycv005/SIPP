package com.example.android.sipp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String currentUserId;
    private ArrayList<EachPerson> mEachPersonList;
    private ArrayAdapter<EachPerson> arrayAdapter;
    private FloatingActionButton fab_addImage;
    public static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER =  2;
    static final int RC_IMAGE_CAPTURE = 3;
    private ImageView cardImage;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private SwipeFlingAdapterView flingContainer;

    private String mUsername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUserId = mFirebaseAuth.getCurrentUser().getUid();

        //DataBase Reference
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Users");

        //Storage reference
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("poll_images");

        cardImage = findViewById(R.id.img_person_cardview);

        onSignedInInitialize();

        fab_addImage = findViewById(R.id.fab_add);

        mEachPersonList = new ArrayList<EachPerson>();
        arrayAdapter = new CustomArrayAdapter(this, R.layout.item, mEachPersonList );

        flingContainer = (SwipeFlingAdapterView)findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                mEachPersonList.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                EachPerson cardPerson = (EachPerson) dataObject;
                final String cardPersonuserId = cardPerson.getUserID();
                if(cardPersonuserId != currentUserId){
                    DatabaseReference upvotesRef = mDatabaseReference.child(cardPersonuserId).child("votes");
                    upvotesRef.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            Integer currentValue = mutableData.getValue(Integer.class);
                            if (currentValue == null) {
                                mutableData.setValue(1);
                            } else {
                                mutableData.setValue(currentValue - 1);
                            }
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(
                                DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                            System.out.println("Transaction completed");
                        }
                    });
                }
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                EachPerson cardPerson = (EachPerson) dataObject;
                final String cardPersonuserId = cardPerson.getUserID();
                if(cardPersonuserId != currentUserId){
                    DatabaseReference downvoteRef = mDatabaseReference.child(cardPersonuserId).child("votes");
                    downvoteRef.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            Integer currentValue = mutableData.getValue(Integer.class);
                            if (currentValue == null) {
                                mutableData.setValue(1);
                            } else {
                                mutableData.setValue(currentValue + 1);
                            }
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(
                                DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                            System.out.println("Transaction completed");
                        }
                    });
                }
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        fab_addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Complete Action Using"),RC_PHOTO_PICKER);
                attachDatabaseReadListener();
            }
        });
    }

    private void onSignedInInitialize() {
        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.i("tag","DataSnapshot"+dataSnapshot.getKey());
                    String pollImageUrl = "default";
                    if (!dataSnapshot.child("pollImages").getValue().equals("default")) {
                        pollImageUrl =dataSnapshot.child("pollImages").getValue().toString();
                    }
                    String votes = dataSnapshot.child("votes").getValue().toString();
                    EachPerson eachPerson = new EachPerson(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), pollImageUrl,Integer.parseInt(votes));
                    mEachPersonList.add(eachPerson);
                    arrayAdapter.notifyDataSetChanged();
                }
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.i("tag","inside onChild Change");
                    String pollImageUrl = "default";
                    if (!dataSnapshot.child("pollImages").getValue().equals("default")) {
                        pollImageUrl =dataSnapshot.child("pollImages").getValue().toString();
                    }
                }
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {}
            };
            mDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        else if(requestCode==RC_PHOTO_PICKER && resultCode==RESULT_OK){
            Uri imageUri = data.getData(); //image will be return back as the uri
            final StorageReference photoRef = mStorageReference.child(imageUri.getLastPathSegment());
            UploadTask uploadTask = photoRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
               public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                   }
                   // Continue with the task to get the download URL
                   return photoRef.getDownloadUrl();
               }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
               @Override
               public void onComplete(@NonNull Task<Uri> task) {
                   if (task.isSuccessful()) {
                       Uri downloadUri = task.getResult();
                        DatabaseReference tempDb = mFirebaseDatabase.getReference().child("Users").child(currentUserId).child("pollImages");
                       tempDb.setValue(downloadUri.toString());
                       arrayAdapter.notifyDataSetChanged();
                       attachDatabaseReadListener();
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.menu_appbar_signout:
                    signOut();
                    return true;
            case R.id.menu_appbar_status:
                    goToStatus();
                    return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToStatus() {
        Intent intent = new Intent(MainActivity.this, Status.class);
        startActivity(intent);
        finish();
    }


    private void signOut() {
        mFirebaseAuth.signOut();
        Intent intent = new Intent(MainActivity.this, SignUpSignIn.class);
        onSignedOutCleanup();
        startActivity(intent);
        finish();
    }
    private void onSignedOutCleanup() {
        arrayAdapter.clear();
        detachDatabaseReadListener();
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }
}
