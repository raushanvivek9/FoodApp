package com.example.foodapp.Fragments;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.Adapter.CartAdapter;
import com.example.foodapp.Model.CartNote;
import com.example.foodapp.Model.OrderNote;
import com.example.foodapp.Placeorder;
import com.example.foodapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.UUID;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class CartFragment extends Fragment {
    View cartview;
    RecyclerView cart_recyclerview;
    TextView t_price;
    Button place_order;
    private Integer total=0;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseUser currentuser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference myRef;
    private Query query;
    private CartAdapter adapter;

    private String order="",hotelId,rest_name,order_id,cust_id,userid;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        cartview=inflater.inflate(R.layout.fragment_cart,container,false);
        cart_recyclerview=cartview.findViewById(R.id.cart_recyclerview);
        t_price=cartview.findViewById(R.id.t_price);
        place_order=cartview.findViewById(R.id.place_order);
        Bundle bundle=this.getArguments();
        try {
            currentuser=mAuth.getCurrentUser();
            userid=currentuser.getUid();
            //generate random orderid and cust_id
            generateString();
            myRef=db.collection("Users").document(currentuser.getUid()).collection("cart");

            query = myRef;
            FirestoreRecyclerOptions<CartNote> options = new FirestoreRecyclerOptions.Builder<CartNote>()
                    .setQuery(query, CartNote.class)
                    .build();
            adapter = new CartAdapter(options);
            cart_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
            cart_recyclerview.setAdapter(adapter);
            //click on qtyChange for cancel the order
            adapter.setOnItemClickListener(new CartAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                }

                @Override
                public void qtyChange(DocumentSnapshot documentSnapshot, int position, final int qty) {
                    CartNote cartNote=documentSnapshot.toObject(CartNote.class);
                    String path=documentSnapshot.getReference().getPath();
                    db.document(path).update("quantity",qty);
                    db.document(path).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                CartNote cartNote=documentSnapshot.toObject(CartNote.class);
                                String path=documentSnapshot.getReference().getPath();
                                int food_price=cartNote.getFood_price();
                                int new_total=food_price*qty;
                                db.document(path).update("total_price",new_total);
                                totalprice();
                            }
                        }
                    });
                    //delete item if quantity is 0
                    if(qty==0){
                        db.document(path).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        WriteBatch batch=db.batch();

                                            batch.delete(documentSnapshot.getReference());
                                        batch.commit();
                                    }
                                });
                    }
                }

            });

            //delete items from cart
            ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    // Take action for the swiped item
                    adapter.deleteItem(viewHolder.getAdapterPosition());
                    totalprice();
                }

                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                    new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red))
                            .addActionIcon(R.drawable.ic_delete_black_24dp)
                            .create()
                            .decorate();

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(cart_recyclerview);

            //set total price and generate order list
            myRef.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                CartNote cartNote = documentSnapshot.toObject(CartNote.class);
                                Integer t = cartNote.getTotal_price();
                                String fname = cartNote.getFood_name();
                                Integer fquant = cartNote.getQuantity();
                                Integer fprice = cartNote.getFood_price();
                                hotelId = cartNote.getHotelId();
                                rest_name = cartNote.getRest_name();
                                total += t;
                                order += fname + " " + fquant + " x " + fprice + " = " + t + "\n";
                            }
                            t_price.setText("" + total);

                        }
                    });
        }catch (Exception e){
            Toast.makeText(getActivity(),"Network Error",Toast.LENGTH_LONG).show();
        }


        place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(total==0) {
                    Toast.makeText(getActivity(),"Cart Empty"+total,Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent=new Intent(getActivity(), Placeorder.class);
                    intent.putExtra("order",order);
                    intent.putExtra("total",total);
                    intent.putExtra("hotelId",hotelId);
                    intent.putExtra("userid",userid);
                    intent.putExtra("rest_name",rest_name);
                    intent.putExtra("order_id",order_id);
                    intent.putExtra("cust_id",cust_id);
                    startActivity(intent);
                }
            }
        });


        return cartview;
    }

    private void totalprice() {
        hotelId="";
        rest_name="";
        total=0;
        order="";
        myRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            CartNote cartNote = documentSnapshot.toObject(CartNote.class);
                            Integer t = cartNote.getTotal_price();
                            String fname = cartNote.getFood_name();
                            Integer fquant = cartNote.getQuantity();
                            Integer fprice = cartNote.getFood_price();
                            hotelId = cartNote.getHotelId();
                            rest_name = cartNote.getRest_name();
                            total += t;
                            order += fname + " " + fquant + " x " + fprice + " = " + t + "\n";
                        }
                        t_price.setText("" + total);

                    }
                });
    }

    private void generateString() {
        order_id= UUID.randomUUID().toString().replaceAll("-","").toUpperCase();
        cust_id= UUID.randomUUID().toString().replaceAll("-","").toUpperCase();

    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            adapter.startListening();
        }catch (Exception e)
        {
            Toast.makeText(getActivity(),"Network Error",Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            adapter.stopListening();
        }catch (Exception e)
        {
            Toast.makeText(getActivity(),"Network Error",Toast.LENGTH_SHORT).show();
        }
    }


}
