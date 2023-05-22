package com.example.foodapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.foodapp.Model.OrderNote;
import com.example.foodapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class OrderAdapter extends FirestoreRecyclerAdapter<OrderNote,OrderAdapter.OrderViewHolder> {
    private OrderAdapter.OnItemClickListener listener;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public OrderAdapter(@NonNull FirestoreRecyclerOptions<OrderNote> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull OrderNote model) {
        holder.hot_name.setText(model.getRest_name());
        holder.bill_name.setText("Bill Name: "+model.getCust_name());
        holder.orderid.setText("Order Id: "+model.getOrder_id());
        holder.pay_mtd.setText("Payment Method: "+model.getPay_method());
        holder.order_del.setText("Order: "+model.getOrder());
        holder.total_price.setText("Total Price: "+model.getTotal_price());
        holder.phone_no.setText("Phone no: "+model.getPhone_no());
        holder.train_no.setText("Train no: "+model.getTrain_no());
        holder.pnr_no.setText("PNR No :"+model.getPnr_no());
        holder.status.setText("Status:" +model.getStatus());
        holder.time.setText("Time "+model.getTimestamp());
        if(model.getStatus().equals("Cancelled") | model.getStatus().equals("Delivered")){
            holder.cancel_ord.setVisibility(View.GONE);
        }


    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.orderview,parent,false);
        return new OrderViewHolder(view);
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView hot_name,bill_name,order_del,total_price,phone_no,train_no,pnr_no,orderid,pay_mtd,status,cancel_ord,time;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            hot_name=itemView.findViewById(R.id.hot_name);
            bill_name=itemView.findViewById(R.id.bill_name);
            order_del=itemView.findViewById(R.id.order_del);
            total_price=itemView.findViewById(R.id.total_price);
            phone_no=itemView.findViewById(R.id.phone_no);
            train_no=itemView.findViewById(R.id.train_no);
            pnr_no=itemView.findViewById(R.id.pnr_no);
            orderid=itemView.findViewById(R.id.orderid);
            pay_mtd=itemView.findViewById(R.id.pay_mtd);
            status=itemView.findViewById(R.id.status);
            cancel_ord=itemView.findViewById(R.id.cancel_order);
            time=itemView.findViewById(R.id.time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION && listener!=null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
            cancel_ord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position =getAdapterPosition();
                    if(listener!=null){
                        listener.onCancel_ordClick(getSnapshots().getSnapshot(position),position);
                    }

                }
            });


        }
    }
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
        void onCancel_ordClick(DocumentSnapshot documentSnapshot,int position);

    }
    public  void setOnItemClickListener(OrderAdapter.OnItemClickListener listener){
        this.listener=listener;
    }
}

