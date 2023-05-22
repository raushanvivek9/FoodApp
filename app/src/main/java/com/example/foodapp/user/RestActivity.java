package com.example.foodapp.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


import com.example.foodapp.Adapter.RestAdapter;
import com.example.foodapp.Menu;
import com.example.foodapp.R;

import com.example.foodapp.Model.RestProfile;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RestActivity extends AppCompatActivity {
    private String city;
    RecyclerView rest_recyclerview;
    private Query query;
    private RestAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference hotelsref = db.collection("hotels");
    private static final String TAG = "RestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.ic_restaurant_white);
        getSupportActionBar().setTitle("  Select Restaurant");
        //setup back arrow button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent data = getIntent();
        try {
            city = data.getStringExtra("city");
            rest_recyclerview=findViewById(R.id.rest_recyclerview);
            query=hotelsref.whereEqualTo("city",city);

            FirestoreRecyclerOptions<RestProfile> options=new FirestoreRecyclerOptions.Builder<RestProfile>()
                    .setQuery(query,RestProfile.class)
                    .build();
            adapter=new RestAdapter(options);
            rest_recyclerview.setLayoutManager(new LinearLayoutManager(this));
            rest_recyclerview.setAdapter(adapter);
            //call interface from ResetAdapter
            adapter.setOnItemClickListener(new RestAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                    RestProfile restProfile=documentSnapshot.toObject(RestProfile.class);
                    String id=documentSnapshot.getId();
                    String path=documentSnapshot.getReference().getPath();
                    String name= (String) documentSnapshot.get("rest_name");
                    Intent i=new Intent(getApplicationContext(), Menu.class);
                    i.putExtra("id",id);
                    i.putExtra("path",path);
                    i.putExtra("rest_name",name);
//                Toast.makeText(getApplicationContext(),"name"+name,Toast.LENGTH_LONG).show();
                    startActivity(i);

                }
            });

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_LONG).show();
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
