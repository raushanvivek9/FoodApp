package com.example.foodapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.R;
import com.example.foodapp.Model.RestProfile;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class RestAdapter extends FirestoreRecyclerAdapter<RestProfile,RestAdapter.RestHolder> {

    private OnItemClickListener listener;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public RestAdapter(@NonNull FirestoreRecyclerOptions<RestProfile> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RestHolder holder, int position, @NonNull RestProfile model) {
        holder.rest_name.setText(model.getRest_name());
        holder.rest_add.setText(model.getAddress());
        holder.rest_city.setText(model.getCity());
        Picasso.get().load(model.getImage()).into(holder.rest_image);

    }

    @NonNull
    @Override
    public RestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.rest_list,parent,false);
        return new RestHolder(view);
    }

    public class RestHolder extends RecyclerView.ViewHolder {
        TextView rest_add,rest_name,rest_city;
        ImageView rest_image;
        public RestHolder(@NonNull View itemView) {
            super(itemView);
            rest_add=itemView.findViewById(R.id.rest_add);
            rest_name=itemView.findViewById(R.id.rest_name);
            rest_city=itemView.findViewById(R.id.rest_city);
            rest_image=itemView.findViewById(R.id.rest_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION && listener!=null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }
    //
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot,int position);

    }
    public  void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }
}
