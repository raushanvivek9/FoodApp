package com.example.foodapp.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.foodapp.Adapter.OrderAdapter;
import com.example.foodapp.Email.JavaMailAPI;
import com.example.foodapp.Model.OrderHotelNote;
import com.example.foodapp.Model.OrderNote;
import com.example.foodapp.Model.UserDetailNote;
import com.example.foodapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class SeeOrderDetails extends AppCompatActivity {
    RecyclerView order_recyclerview;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Query query;
    private CollectionReference colref;
    private OrderAdapter adapter;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseUser currentuser=mAuth.getCurrentUser();
    private String userid=currentuser.getUid(),email,hotel_uid;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_order_details);
        order_recyclerview=findViewById(R.id.order_recyclerview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Order Details");
        //setup back arrow button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        Toast.makeText(getApplicationContext(),"user id"+userid,Toast.LENGTH_LONG).show();
        try {
            colref=db.collection("Users").document(userid).collection("orderdetails");

            query=colref.orderBy("timestamp",Query.Direction.DESCENDING);
            FirestoreRecyclerOptions<OrderNote> options=new FirestoreRecyclerOptions.Builder<OrderNote>()
                .setQuery(query,OrderNote.class)
                .build();
            adapter=new OrderAdapter(options);
            order_recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            order_recyclerview.setAdapter(adapter);
            //click on cancel for cancel the order
            adapter.setOnItemClickListener(new OrderAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                }

                @Override
                public void onCancel_ordClick(DocumentSnapshot documentSnapshot, int position) {
                    OrderNote orderNote=documentSnapshot.toObject(OrderNote.class);
                    String path=documentSnapshot.getReference().getPath();
                    String hotelId=orderNote.getHotelId();
                    final String order_id=orderNote.getOrder_id();
                    String sts=orderNote.getStatus();
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

                    if(sts.equals("Pending") | sts.equals("Accepted"))
                    {
                        db.document(path).update("status","Cancelled");
                        db.collection("hotels").document(hotelId).collection("orderdetails")
                                .whereEqualTo("order_id",order_id)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for(QueryDocumentSnapshot documentSnapshot1:queryDocumentSnapshots){
                                            OrderHotelNote orderHotelNote=documentSnapshot1.toObject(OrderHotelNote.class);
                                            String path2=documentSnapshot1.getReference().getPath();
                                            email=orderHotelNote.getU_email();
                                            db.document(path2).update("status","Cancelled");
                                            Toast.makeText(getApplicationContext(),"Order Cancelled Successful",Toast.LENGTH_SHORT).show();

                                            String message = "Your order "+order_id+" is Cancelled Successful";
                                            String subject = "Order Details";
                                            //Send Mail
                                            JavaMailAPI javaMailAPI = new JavaMailAPI(SeeOrderDetails.this,email,subject,message);
                                            javaMailAPI.execute();
                                            //create notification
                                            databaseReference= FirebaseDatabase.getInstance().getReference();
                                            HashMap<String,String> chatnotification=new HashMap<>();
                                            chatnotification.put("from",currentuser.getUid());
                                            chatnotification.put("type","User Cancelled Order");
                                            databaseReference.child("Notification").child(hotel_uid).push().setValue(chatnotification);
                                        }

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });


                    }
                    else if(sts.equals("Delivered")){
                        Toast.makeText(getApplicationContext(),"Order already delivered",Toast.LENGTH_LONG).show();
                    }
                    else if(sts.equals("Cancelled")){
                        Toast.makeText(getApplicationContext(),"Order already Cancelled",Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e){
            Toast.makeText(getApplicationContext(),"Network Error ",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            adapter.startListening();
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            adapter.stopListening();
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_SHORT).show();
        }
    }
}
