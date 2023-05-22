package com.example.foodapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.foodapp.Adapter.MenuAdapter;
import com.example.foodapp.Model.MenuNote;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class Menu extends AppCompatActivity {
    private String rest_id,restPath,rest_name;
    RecyclerView menu_recyclerview;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference restref;
    private Query query;
    MenuAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
//        textView=findViewById(R.id.textView);
        final Intent data=getIntent();
        rest_id=data.getStringExtra("id");
        restPath=data.getStringExtra("path");
        rest_name=data.getStringExtra("rest_name");
        menu_recyclerview=findViewById(R.id.menu_recyclerview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.ic_restaurant_white);
        getSupportActionBar().setTitle("  "+rest_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            restref=db.collection("hotels").document(rest_id).collection("menu");
            query=restref;
            FirestoreRecyclerOptions<MenuNote> options=new FirestoreRecyclerOptions.Builder<MenuNote>()
                    .setQuery(query,MenuNote.class)
                    .build();
            adapter=new MenuAdapter(options);
            menu_recyclerview.setLayoutManager(new LinearLayoutManager(this));
            menu_recyclerview.setAdapter(adapter);
            adapter.setOnItemClickListener(new MenuAdapter.OnItemClickListener(){
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                    MenuNote menuNote=documentSnapshot.toObject(MenuNote.class);
                    String id=documentSnapshot.getId();
                    String path=documentSnapshot.getReference().getPath();
//                Toast.makeText(getApplicationContext(),"id"+id,Toast.LENGTH_LONG).show();
                    Intent i=new Intent(getApplicationContext(), Food_details.class);
                    i.putExtra("id",id);
                    i.putExtra("rest_id",rest_id);
                    i.putExtra("path",path);
                    i.putExtra("rest_name",rest_name);
                    startActivity(i);
                }

            });

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_SHORT).show();
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
