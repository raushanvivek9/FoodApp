package com.example.foodapp.Payment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.foodapp.Email.JavaMailAPI;
import com.example.foodapp.Model.OrderHotelNote;
import com.example.foodapp.Model.OrderNote;
import com.example.foodapp.Model.UserDetailNote;
import com.example.foodapp.R;
import com.example.foodapp.user.Main2Activity;
import com.example.foodapp.user.User_login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;


import java.util.HashMap;
import java.util.List;

public class Payment extends AppCompatActivity {
    RadioButton cod,razorpay;
    RadioGroup radioGroup;
    Button conf_payment;
    private  int i;
    private String selectedPayment;
    private String uname,berth,order,hotelId,userid,phone,pnr,train,rest_name,order_id,cust_id,hotel_uid;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseUser currentuser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatabaseReference databaseReference;
    private Integer total;
    private String status="Pending",u_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        cod=findViewById(R.id.cod);
        razorpay=findViewById(R.id.razorpay);

        Intent intent=getIntent();
        order=intent.getStringExtra("order");
        total=intent.getIntExtra("total",0);
        hotelId=intent.getStringExtra("hotelId");
        userid=intent.getStringExtra("userid");
        rest_name=intent.getStringExtra("rest_name");
        order_id=intent.getStringExtra("order_id");
        cust_id=intent.getStringExtra("cust_id");
        uname=intent.getStringExtra("uname");
        pnr=intent.getStringExtra("pnr");
        train=intent.getStringExtra("train_icon");
        berth=intent.getStringExtra("berth");
        phone=intent.getStringExtra("phone");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.ic_restaurant_white);
        getSupportActionBar().setTitle("  Select Payment Method");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        radioGroup=findViewById(R.id.radioGroup);
        conf_payment=findViewById(R.id.conf_payment);

        i=radioGroup.getCheckedRadioButtonId();
        RadioButton rb=radioGroup.findViewById(i);
        currentuser=mAuth.getCurrentUser();
        //fetching hotel uid
        db.collection("hotels").document(hotelId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserDetailNote userNote = documentSnapshot.toObject(UserDetailNote.class);
                        hotel_uid=userNote.getUid();
//                        Toast.makeText(getApplicationContext(),"uid"+hotel_uid,Toast.LENGTH_LONG).show();
                    }
                });
        //fetch user email_id
        db.collection("Users").document(currentuser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserDetailNote userDetailNote=documentSnapshot.toObject(UserDetailNote.class);
                        u_email=userDetailNote.getUsername();
//                        Toast.makeText(getApplicationContext(),"user"+u_email,Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Error: "+e,Toast.LENGTH_SHORT).show();
                    }
                });

        //Confirm payment method
        conf_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i=radioGroup.getCheckedRadioButtonId();
                RadioButton rb=radioGroup.findViewById(i);
                if(i==-1)
                {
                    Toast.makeText(getApplicationContext(),"Please select payment method",Toast.LENGTH_SHORT).show();
                }
                else{
                    selectedPayment=rb.getText().toString();
                    //CoD payment
                    if(selectedPayment.equals("COD(Cash On Delivery)"))
                    {
                        //add order details in user collection
                        OrderNote note=new OrderNote(order,total,hotelId,uname,pnr,phone,train,berth,rest_name,order_id,cust_id,selectedPayment,status);
                        db.collection("Users").document(userid).collection("orderdetails")
                                .add(note);
                        //add order details in hotel collection
                        OrderHotelNote hotelNote= new OrderHotelNote(order,total,userid,uname,pnr,phone,train,berth,order_id,cust_id,selectedPayment,status,u_email);
                        db.collection("hotels").document(hotelId).collection("orderdetails")
                                .add(hotelNote);

                        //delete items form cart
                        db.collection("Users").document(userid).collection("cart").whereEqualTo("value","cart")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        WriteBatch batch=db.batch();
                                        List<DocumentSnapshot> snapshotList=queryDocumentSnapshots.getDocuments();
                                        for(DocumentSnapshot snapshot:snapshotList){
                                            batch.delete(snapshot.getReference());
                                        }
                                        batch.commit();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                        Toast.makeText(getApplicationContext(),"Order Placed Successfully",Toast.LENGTH_LONG).show();
                        //send msg on email
                        sendMail();
                        //create notification
                        databaseReference= FirebaseDatabase.getInstance().getReference();
                        HashMap<String,String> chatnotification=new HashMap<>();
                        chatnotification.put("from",currentuser.getUid());
                        chatnotification.put("type","New Order");
                        databaseReference.child("Notification").child(hotel_uid).push().setValue(chatnotification);

                        //sucess message
                        View view= LayoutInflater.from(Payment.this).inflate(R.layout.activity_success__status,null);
                        AlertDialog.Builder builder=new AlertDialog.Builder(Payment.this);
                        builder.setCancelable(false)
                                .setView(view)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i=new Intent(getApplicationContext(), Main2Activity.class);
                                                i.addFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(i);
                                                finish();
                                            }
                                        });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                    }
                    //Online Payment
                    else if(selectedPayment.equals("Online Payment")){
//                        Toast.makeText(getApplicationContext(),"paytm",Toast.LENGTH_SHORT).show();
//
                        Intent i=new Intent(getApplicationContext(),RazorPay.class);
                        i.putExtra("order",order);
                        i.putExtra("total",total);
                        i.putExtra("hotelId",hotelId);
                        i.putExtra("userid",userid);
                        i.putExtra("rest_name",rest_name);
                        i.putExtra("order_id",order_id);
                        i.putExtra("cust_id",cust_id);
                        i.putExtra("uname",uname);
                        i.putExtra("pnr",pnr);
                        i.putExtra("train_icon",train);
                        i.putExtra("berth",berth);
                        i.putExtra("phone",phone);
                        i.putExtra("u_email",u_email);
                        i.putExtra("hotel_uid",hotel_uid);
                        startActivity(i);

                    }
                }

            }
        });
    }
    private void sendMail() {

        String mail = u_email;
        String message = " Thank You for order "+uname+", Your order placed successfully \n Your orderId is "+order_id+"\nYour order is:\n"+order;
        String subject = "Order Details";

        //Send Mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(this,mail,subject,message);
        javaMailAPI.execute();
    }
}
