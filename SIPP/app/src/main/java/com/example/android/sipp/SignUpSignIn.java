package com.example.android.sipp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpSignIn extends AppCompatActivity {

    TextView txt_signupSwitchBlock,txt_signinSwitchBlock,txt_head_signinSignup,txt_forgetpass;
    CircleImageView img_circleBoy;
    Button btn_signinSignup;
    EditText editText_password;
    EditText editText_mail;
    CheckBox mcheckBox;
    EditText editText_name;
    private String currentUserId;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_signin);

        mFirebaseAuth = FirebaseAuth.getInstance();

        //DataBase Reference
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        //Storage reference
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("sipp_image");

        txt_signinSwitchBlock = findViewById(R.id.text_signin_switch_block);
        txt_head_signinSignup = findViewById(R.id.text_signin_signup);
        txt_signupSwitchBlock = findViewById(R.id.text_signup_switch_block);
        img_circleBoy = findViewById(R.id.img_circle_boy);
        txt_forgetpass = findViewById(R.id.text_forget_pass);
        btn_signinSignup = findViewById(R.id.btn_sigin_signup);
        editText_password = findViewById(R.id.edit_password);
        mcheckBox = findViewById(R.id.checkbox);
        editText_mail = findViewById(R.id.edit_mail);
        editText_name = findViewById(R.id.edit_name);

        txt_signinSwitchBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_signinSwitchBlock.setTextColor(Color.parseColor("#FFFFFF"));
                txt_signinSwitchBlock.setBackgroundColor(Color.parseColor("#039BE5"));
                img_circleBoy.setImageResource(R.drawable.signin_boy_img_compressor);
                txt_forgetpass.setVisibility(View.VISIBLE);
                txt_signupSwitchBlock.setTextColor(Color.parseColor("#039BE5"));
                txt_signupSwitchBlock.setBackgroundResource(R.drawable.border_color);
                btn_signinSignup.setText("Sign in");
                txt_head_signinSignup.setText("Sign In");
                editText_password.setText("");
                editText_name.setVisibility(View.GONE);
            }
        });

        txt_signupSwitchBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_signupSwitchBlock.setTextColor(Color.parseColor("#FFFFFF"));
                txt_signupSwitchBlock.setBackgroundColor(Color.parseColor("#039BE5"));
                img_circleBoy.setImageResource(R.drawable.sigup_boy_img_compressor);
                txt_forgetpass.setVisibility(View.INVISIBLE);
                btn_signinSignup.setText("Sign Up");
                txt_signinSwitchBlock.setTextColor(Color.parseColor("#039BE5"));
                txt_signinSwitchBlock.setBackgroundResource(R.drawable.border_color);
                txt_head_signinSignup.setText("Sign Up");
                editText_password.setText("");
                editText_name.setVisibility(View.VISIBLE);
            }
        });

        editText_password.setOnTouchListener(new View.OnTouchListener() {
            //silencing the warning here using @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                img_circleBoy.setImageResource(R.drawable.connect_boy_img_close_eye);
                return false;
            }
        });


        mcheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked) {
                    // show password
                    editText_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    img_circleBoy.setImageResource(R.drawable.connect_boy_img_close_eye);

                } else {
                    // hide password
                    editText_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    img_circleBoy.setImageResource(R.drawable.connect_boy_img_open_eye);
                }
            }
        });

        btn_signinSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn_txt = btn_signinSignup.getText().toString();
//                Log.i("Tag","Press: "+btn_txt);
                switch (btn_txt) {
                    case "Sign Up":
//                        Log.i("Tag","In Sign Up");
                        registration();
                        break;
                    case "Sign in":
//                        Log.i("Tag","In Sign In");
                        login();
                        break;

                        default:
                        break;
                }
//                Log.i("Tag","Outside Switch");
            }
        });

        /*checking the status of the user, whether login or log out using FireB auth listener*/
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                if(mUser!=null){//existing user, then take to MainActivity
                    Intent intent = new Intent(SignUpSignIn.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                //else, we will do nothing & let him do register
            }
        };
    }

    private void registration() {
        final String name = editText_name.getText().toString();
        final String email = editText_mail.getText().toString();
        final String password = editText_password.getText().toString();
        if(name.length()>0 && email.length()>5 && password.length()>0) {
            mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpSignIn.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //if the creation was successful or not
                    if (!task.isSuccessful()) {
                        Toast.makeText(SignUpSignIn.this, "Wrong Info", Toast.LENGTH_LONG).show();
                    } else {//successful
                        String userID = mFirebaseAuth.getCurrentUser().getUid();
                        mDatabaseReference = mFirebaseDatabase.getReference().child("Users").child(userID);
                        Map userInfo = new HashMap<>();
                        userInfo.put("name", name);
                        userInfo.put("pollImages", "default");
                        userInfo.put("votes", 0);
                        mDatabaseReference.updateChildren(userInfo);
                    }
                }
            });
        }
    }

    private void login() {
        final String email = editText_mail.getText().toString();
        final String password = editText_password.getText().toString();
        if(email.length()>5  && password.length()>0) {
            mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(SignUpSignIn.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //if the creation was successful or not
                    if (!task.isSuccessful()) {
                        Toast.makeText(SignUpSignIn.this, "Error while Log in", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}
