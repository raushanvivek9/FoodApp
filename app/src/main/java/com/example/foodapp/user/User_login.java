package com.example.foodapp.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class User_login extends AppCompatActivity {
    EditText email, password;
    TextView forgot,signup;
    ProgressBar progressBar;
    Button login;
    CheckBox password_show;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private DatabaseReference myRef,databaseReference;
    private String pswd;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        password_show=(CheckBox)findViewById(R.id.password_show);
        login = (Button) findViewById(R.id.login);
        forgot = findViewById(R.id.forgot);
        signup =  findViewById(R.id.signup);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        password_show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else
                {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        db = FirebaseDatabase.getInstance();
        myRef = db.getReference("Users");


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check Internet Connection
                ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo infos=connectivityManager.getActiveNetworkInfo();
                if(null!=infos){
                    userlogin();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Network Error: Please Check your Connection!!",Toast.LENGTH_LONG).show();
                }

            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check Internet Connection
                ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo infos=connectivityManager.getActiveNetworkInfo();
                if(null!=infos){
                    View v= LayoutInflater.from(User_login.this).inflate(R.layout.activity_forgot,null);
                    final EditText user=(EditText)v.findViewById(R.id.f_email);
                    AlertDialog.Builder builder=new AlertDialog.Builder(User_login.this);
                    builder.setTitle("Forgot Password.")
                            .setCancelable(false)
                            .setView(v)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String u=user.getText().toString();
                                    if (u.equals("")) {
                                        Toast.makeText(User_login.this, "Please enter your registered EmailID", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        mAuth.sendPasswordResetEmail(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(User_login.this, "Reset Link send to your Email ID", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(User_login.this, "Error to sending reset link", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }

                                }
                            })
                            .setNegativeButton("Cancel",null);
                    AlertDialog alert= builder.create();
                    alert.show();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Network Error: Please Check your Connection!!",Toast.LENGTH_LONG).show();
                }

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(User_login.this, Sign.class);
                startActivity(i);
            }
        });

    }

    private void userlogin() {
        username = email.getText().toString();
        pswd = password.getText().toString();
        if (username.isEmpty()) {
            email.setError("email is required");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            email.setError("please enter the valid email");
            email.requestFocus();
            return;
        }
        if (pswd.isEmpty()) {
            password.setError("password is required");
            password.requestFocus();
            return;
        }
        if (pswd.length() < 6) {
            password.setError("Minimum length of password should be 6");
            password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        Query query1 = myRef.orderByChild("username").equalTo(username);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //fetch user name
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                        String usern = map.get("username").toString();
                        //check username
                        if (usern.equals(username)) {
                            mAuth.signInWithEmailAndPassword(username,pswd)
                                    .addOnCompleteListener(User_login.this,new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                //generate device token
                                                String deviceToken= FirebaseInstanceId.getInstance().getToken();
                                                databaseReference= FirebaseDatabase.getInstance().getReference();
                                                String currentid=mAuth.getCurrentUser().getUid();
                                                databaseReference.child("Users").child(currentid).child("device_Token").setValue(deviceToken);

                                                Intent i = new Intent(User_login.this, Main2Activity.class);
                                                //clear all open activity
                                                i.addFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
                                                progressBar.setVisibility(View.GONE);
                                                startActivity(i);
                                                finish();
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        }
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Invalid user id", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Error: "+databaseError,Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);

            }
        });
    }
}
