package com.example.foodapp.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import com.example.foodapp.Model.HomeNote;
import com.example.foodapp.R;
import com.example.foodapp.user.RestActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.widget.AdapterView.*;

public class HomeFragment extends Fragment {
    Button submit,submit2,cancel;
    EditText train_num;
    TextView home,select;
    Spinner spinner;
    private String St_name;
    private Integer Number;
    private View home_view;
    private FirebaseFirestore db;
    private CollectionReference stationcol;
    Bundle bundle;
    private String st;
    String email_id;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        home_view= inflater.inflate(R.layout.fragment_home,container,false);
        submit=home_view.findViewById(R.id.submit);
        submit2=home_view.findViewById(R.id.submit2);
        cancel=home_view.findViewById(R.id.cancel_b);
        train_num=home_view.findViewById(R.id.train_num);
        home=home_view.findViewById(R.id.home);
        select=home_view.findViewById(R.id.select);
        db=FirebaseFirestore.getInstance();
        stationcol=db.collection("trains");
        spinner=home_view.findViewById(R.id.spinner);
        bundle=new Bundle();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(train_num.getText().toString().isEmpty())
                {
                    train_num.setError("Enter Train number");
                    train_num.requestFocus();
                    return;
                }
                else if(train_num.getText().toString().length()!=5)
                {
                    train_num.setError("Enter valid Train number");
                    train_num.requestFocus();
                    return;

                }
                else {
                    Number = Integer.parseInt(train_num.getText().toString());
                    submit.setVisibility(View.GONE);
                    final ProgressDialog loadingbar=new ProgressDialog(getActivity());
                    loadingbar.setTitle("Loading");
                    loadingbar.setMessage("Fetching Station");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                    final ArrayList<String> items = new ArrayList<String>();
                    items.add("----------------------select station-----------------------");
                    stationcol.whereEqualTo("train_no", Number)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                if(task.getResult().size()>0){
                                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                            HomeNote note = documentSnapshot.toObject(HomeNote.class);
                                            note.setDocumentId(documentSnapshot.getId());
                                            String documentId = note.getDocumentId();
                                            //get station name from database
                                            for (String stat:note.getStation_name()){
                                                items.add(stat);
                                              //  Toast.makeText(getActivity(),"successful"+items,Toast.LENGTH_LONG).show();
                                            }
                                    }

                                }
                                else {
                                    Toast.makeText(getActivity(),"Train Not Found",Toast.LENGTH_LONG).show();
                                    loadingbar.dismiss();
                                    train_num.setError("Train Not Found,Enter valid Train number");
                                    train_num.requestFocus();
                                    return;

                                }
                            }

                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinner.setAdapter(arrayAdapter);
                                            select.setVisibility(View.VISIBLE);
                                            spinner.setVisibility(View.VISIBLE);
                                            cancel.setVisibility(VISIBLE);
                                            loadingbar.dismiss();
                                }
                            });
                    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                St_name = spinner.getSelectedItem().toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    submit2.setVisibility(VISIBLE);
                }
            }
        });

        submit2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (St_name.equals("----------------------select station-----------------------")){
                    Toast.makeText(getActivity(), "Please select station", Toast.LENGTH_SHORT).show();

                } else {
                    Intent i=new Intent(getActivity(), RestActivity.class);
                    i.putExtra("city",St_name);
                    i.putExtra("u_email_id",email_id);
                    startActivity(i);
                }
            }

        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(GONE);
                cancel.setVisibility(GONE);
                submit2.setVisibility(GONE);
                select.setVisibility(GONE);
                submit.setVisibility(VISIBLE);
            }
        });

        return home_view;
    }

}
