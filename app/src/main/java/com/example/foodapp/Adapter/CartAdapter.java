package com.example.foodapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.foodapp.Model.CartNote;
import com.example.foodapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class CartAdapter extends FirestoreRecyclerAdapter<CartNote,CartAdapter.CartViewHolder> {
    private CartAdapter.OnItemClickListener listener;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CartAdapter(@NonNull FirestoreRecyclerOptions<CartNote> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull CartNote model) {
        holder.cart_item_name.setText(model.getFood_name());
        holder.cart_item_quantity.setText(""+model.getQuantity());
        holder.cart_item_price.setText(""+model.getFood_price());
        holder.cart_item_totalprice.setText(""+model.getTotal_price());
        holder.qt_btn.setNumber(""+model.getQuantity());

    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,parent,false);
        return new CartViewHolder(view);
    }
    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        TextView cart_item_name,cart_item_price,cart_item_quantity,cart_item_totalprice;
        ImageView cart_item_count;
        ElegantNumberButton qt_btn;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cart_item_name=itemView.findViewById(R.id.cart_item_name);
            cart_item_price=itemView.findViewById(R.id.cart_item_price);
            cart_item_quantity=itemView.findViewById(R.id.cart_item_quantity);
            cart_item_totalprice=itemView.findViewById(R.id.cart_item_totalprice);
            cart_item_count=itemView.findViewById(R.id.cart_item_count);
            qt_btn=itemView.findViewById(R.id.qt_btn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION && listener!=null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

           qt_btn.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
               @Override
               public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                   int position=getAdapterPosition();
                   if(position!=RecyclerView.NO_POSITION && listener!=null) {
                       listener.qtyChange(getSnapshots().getSnapshot(position), position,newValue);
                   }

               }
           });
        }
    }
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
        void qtyChange(DocumentSnapshot documentSnapshot,int position,int qty);

    }
    public  void setOnItemClickListener(CartAdapter.OnItemClickListener listener){
        this.listener=listener;
    }
}
