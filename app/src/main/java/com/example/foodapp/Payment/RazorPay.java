package com.example.foodapp.Payment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.foodapp.Email.JavaMailAPI;
import com.example.foodapp.Model.OrderHotelNote;
import com.example.foodapp.Model.OrderNote;
import com.example.foodapp.R;
import com.example.foodapp.user.Main2Activity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.paytm.pgsdk.Log;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class RazorPay extends Activity implements PaymentResultListener {
    private String uname,berth,order,hotelId,userid,phone,pnr,train,rest_name,order_id,cust_id,u_email,TAG="Payment Error",hotel_uid;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String status="Pending",selectedPayment="RazorPay";
    private Integer total;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseUser currentuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_razor_pay);
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
        u_email=intent.getStringExtra("u_email");
        hotel_uid=intent.getStringExtra("hotel_uid");
        currentuser=mAuth.getCurrentUser();

        /**
         * Preload payment resources
         */
        Checkout.preload(getApplicationContext());

        startPayment();
    }
    //start online payment
    public void startPayment() {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        /**
         * Set your logo here
         */
//        checkout.setImage(R.drawable.logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: ACME Corp || HasGeek etc.
             */
            options.put("name", "Food App");

            /**
             * Description can be anything
             * eg: Reference No. #123123 - This order number is passed by you for your internal reference. This is not the `razorpay_order_id`.
             *     Invoice Payment
             *     etc.
             */
//            options.put("description", "Reference No. #123456");


            options.put("description", "Payment for food");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
//            options.put("order_id", order_id);

            options.put("currency", "INR");

            /**
             * Amount is always passed in currency subunits
             * Eg: "500" = INR 5.00
             */
            Integer price=total*100;
            String amount=price.toString();
            options.put("amount", amount);

            JSONObject preFill=new JSONObject();
            preFill.put("email",u_email);
            preFill.put("contact",phone);
            options.put("prefill",preFill);

            checkout.open(activity, options);
        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        //add order in orderdetails in Users
        OrderNote note=new OrderNote(order,total,hotelId,uname,pnr,phone,train,berth,rest_name,order_id,cust_id,selectedPayment,status);
        db.collection("Users").document(userid).collection("orderdetails")
                .add(note);
        //add order in order details in hotel
        OrderHotelNote hotelNote= new OrderHotelNote(order,total,userid,uname,pnr,phone,train,berth,order_id,cust_id,selectedPayment,status,u_email);
        db.collection("hotels").document(hotelId).collection("orderdetails")
                .add(hotelNote);
        //delete item from cart
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
        //send mail
        sendMail();

        //create notification
        databaseReference= FirebaseDatabase.getInstance().getReference();
        HashMap<String,String> chatnotification=new HashMap<>();
        chatnotification.put("from",currentuser.getUid());
        chatnotification.put("type","New Order");
        databaseReference.child("Notification").child(hotel_uid).push().setValue(chatnotification);
        //sucess message
        View view= LayoutInflater.from(RazorPay.this).inflate(R.layout.activity_success__status,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(RazorPay.this);
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

        Toast.makeText(getApplicationContext(),"Payment Successful",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPaymentError(int i, String s) {
        finish();
        Toast.makeText(getApplicationContext(),"Error: "+s,Toast.LENGTH_LONG).show();

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
