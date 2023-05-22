package com.example.foodapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.foodapp.Payment.Payment;

public class Placeorder extends AppCompatActivity {
    EditText tr_num,p_name,p_phone,p_pnr,p_berth;
    Button plcd_order;
    private String uname,berth,order,hotelId,userid,phone,pnr,train,rest_name,order_id,cust_id;
    private Integer total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeorder);
        tr_num=findViewById(R.id.tr_num);
        p_berth=findViewById(R.id.p_berth);
        p_name=findViewById(R.id.p_name);
        p_phone=findViewById(R.id.p_phone);
        p_pnr=findViewById(R.id.p_pnr);
        plcd_order=findViewById(R.id.pl_order);
        Intent i=getIntent();
        order=i.getStringExtra("order");
        hotelId=i.getStringExtra("hotelId");
        total=i.getIntExtra("total",0);
        userid=i.getStringExtra("userid");
        rest_name=i.getStringExtra("rest_name");
        order_id=i.getStringExtra("order_id");
        cust_id=i.getStringExtra("cust_id");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.ic_restaurant_white);
        getSupportActionBar().setTitle("  Fill Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        plcd_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(p_name.getText().toString().isEmpty() || p_phone.getText().toString().isEmpty() || p_pnr.getText().toString().isEmpty()
                        || tr_num.getText().toString().isEmpty() || p_berth.getText().toString().isEmpty())
                {
                    p_name.setError("Required");
                    p_name.requestFocus();
                    p_phone.requestFocus();
                    p_pnr.requestFocus();
                    tr_num.requestFocus();
                    p_berth.requestFocus();
                    return;
                }
                if(p_name.getText().toString().isEmpty()){
                    p_name.setError("name is required");
                    p_name.requestFocus();
                    return;
                }
                if(p_phone.getText().toString().isEmpty()){
                    p_phone.setError("phone no. is required");
                    p_phone.requestFocus();
                    return;
                }
                if(p_pnr.getText().toString().isEmpty()){
                    p_pnr.setError("PNR is required");
                    p_pnr.requestFocus();
                    return;
                }
                if(tr_num.getText().toString().isEmpty()){
                    tr_num.setError("Train no. is required");
                    tr_num.requestFocus();
                    return;
                }
                if(p_berth.getText().toString().isEmpty()){
                    p_berth.setError("Berth is required");
                    p_berth.requestFocus();
                    return;
                }
                phone=p_phone.getText().toString();
                pnr=p_pnr.getText().toString();
                train=tr_num.getText().toString();
                uname=p_name.getText().toString();
                berth=p_berth.getText().toString();
                if(phone.length()!=10)
                {
                    p_phone.setError("Invalid Phone no.");
                    p_phone.requestFocus();
                    return;
                }
                else if(pnr.length()!=10)
                {
                    p_pnr.setError("Invalid PNR no.");
                    p_pnr.requestFocus();
                    return;
                }
                else if(train.length()!=5)
                {
                    tr_num.setError("Invalid Train no.");
                    tr_num.requestFocus();
                    return;
                }
                else{
                    Intent intent=new Intent(getApplicationContext(), Payment.class);
                    intent.putExtra("order",order);
                    intent.putExtra("total",total);
                    intent.putExtra("hotelId",hotelId);
                    intent.putExtra("userid",userid);
                    intent.putExtra("rest_name",rest_name);
                    intent.putExtra("order_id",order_id);
                    intent.putExtra("cust_id",cust_id);
                    intent.putExtra("uname",uname);
                    intent.putExtra("pnr",pnr);
                    intent.putExtra("train_icon",train);
                    intent.putExtra("berth",berth);
                    intent.putExtra("phone",phone);
                    startActivity(intent);
                }

            }
        });
    }

}
