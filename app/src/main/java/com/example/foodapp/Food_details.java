package com.example.foodapp;

import  androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.foodapp.Model.CartNote;
import com.example.foodapp.Model.MenuNote;
import com.example.foodapp.user.Main2Activity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class Food_details extends AppCompatActivity {
    TextView food_price,food_name;
    ElegantNumberButton num_btn;
    Button cart,gocart;
    private String f_name,userid,rest_id;
    private Integer num,f_price,total_price;
    private ImageView menu_image;
    private String value="cart";
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseUser currentuser=mAuth.getCurrentUser();
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference colref;
    private DocumentReference mref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);
        num_btn = findViewById(R.id.num_btn);
        cart = findViewById(R.id.cart);
        food_price = findViewById(R.id.food_price);
        food_name = findViewById(R.id.food_name);
        gocart=findViewById(R.id.gocart);
        menu_image=findViewById(R.id.menu_image);
        try {
            Intent data = getIntent();
            final String id = data.getStringExtra("id");
            rest_id=data.getStringExtra("rest_id");
            String path = data.getStringExtra("path");
            final String rest_name = data.getStringExtra("rest_name");
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setLogo(R.drawable.ic_restaurant_white);
            setSupportActionBar(toolbar);
            getSupportActionBar().setLogo(R.drawable.ic_restaurant_white);
            getSupportActionBar().setTitle("  "+rest_name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            userid=currentuser.getUid();
            colref=db.collection("Users").document(userid).collection("cart");
            mref=db.document(path);
            //search food details...
            mref.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                MenuNote menuNote=documentSnapshot.toObject(MenuNote.class);
                                f_name=menuNote.getMenu_name();
                                f_price=menuNote.getPrice();
                                Picasso.get().load(menuNote.getImage()).into(menu_image);
                                food_name.setText(f_name);
                                food_price.setText(""+f_price);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_SHORT).show();
                        }
                    });
        //Adding selected food into cart
            cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        num = Integer.parseInt(num_btn.getNumber());//get number from eleganta btn
                        total_price=num*f_price;
                        CartNote note=new CartNote(f_name,f_price,num,total_price,rest_id,value,rest_name);
                        colref.add(note);
                        Toast.makeText(getApplicationContext(),"Successfully Added in Your Cart ",Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_SHORT).show();
                    }

                }
            });
            gocart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(getApplicationContext(), Main2Activity.class);
                    i.putExtra("value",1);
                    i.putExtra("userID",userid);
                    startActivity(i);

                }
            });

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_LONG).show();
        }

    }

}
