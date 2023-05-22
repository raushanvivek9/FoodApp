package com.example.foodapp.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodapp.Email.JavaMailAPI;
import com.example.foodapp.Model.UserDetailNote;
import com.example.foodapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Random;

public class OptVerification extends AppCompatActivity {
    private int otp_no;
    private int otp;
    Button otpsubmit;
    EditText otpnumber;
    TextView resendotp;
    private String name,email,password,phone;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opt_verification);
        otpnumber=findViewById(R.id.otpNumber);
        otpsubmit=findViewById(R.id.otpsubmit);
        resendotp=findViewById(R.id.resendotp);
        Intent intent=getIntent();
        name=intent.getStringExtra("name");
        email=intent.getStringExtra("email");
        password=intent.getStringExtra("password");
        phone=intent.getStringExtra("phone");
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        myRef= FirebaseDatabase.getInstance().getReference();
        randomNumber();

        //submit otp for login
        otpsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp=Integer.parseInt(otpnumber.getText().toString());
                final ProgressDialog progressDialog=new ProgressDialog(OptVerification.this);
                progressDialog.setTitle("Please Wait...");
                progressDialog.setMessage("Loading...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();
                if(otp==otp_no)
                {
                mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String currentuser=mAuth.getCurrentUser().getUid();
//                                  //generate device token
                                    String deviceToken= FirebaseInstanceId.getInstance().getToken();
                                    HashMap<String,String> profleMap=new HashMap<>();
                                    profleMap.put("uid",currentuser);
                                    profleMap.put("name",name);
                                    profleMap.put("username",email);
                                    profleMap.put("phone_no",phone);
                                    profleMap.put("device_Token",deviceToken);
                                    myRef.child("Users").child(currentuser).setValue(profleMap);
                                    //add user details in firestore
                                    UserDetailNote note=new UserDetailNote(email,currentuser,phone);
                                    db.collection("Users").document(currentuser).set(note);
                                    progressDialog.dismiss();
                                    //open main  activity
                                    Intent i=new Intent(OptVerification.this,Main2Activity.class);
                                    //clear all open activity
                                    i.addFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();

                                    Toast.makeText(getApplicationContext(),"user registered successful",Toast.LENGTH_LONG).show();
                                }
                                else if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    progressDialog.dismiss();

                                    finish();
                                }
                                else
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_SHORT).show();
                            }
                        });

                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Invalid OTP",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Resend OTP if click on resend
        resendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomNumber();
            }
        });
    }
    private void randomNumber() {
        Random random=new Random();
        otp_no=random.nextInt(999999);
        //send otp on email
        sendMail();
        Toast.makeText(getApplicationContext(),"OTP sent successfully on your Email Id",Toast.LENGTH_LONG).show();
    }
    //send mail
    private void sendMail() {
        String message = "Your OTP(One Time Password) Verification Code is "+otp_no;
        String subject = "Order Details";

        //Send Mail
        try {
            JavaMailAPI javaMailAPI = new JavaMailAPI(this,email,subject,message);

            javaMailAPI.execute();
        }
        catch (Exception e){
        }

    }
}
