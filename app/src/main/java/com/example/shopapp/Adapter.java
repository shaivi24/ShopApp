package com.example.shopapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class Adapter extends FirebaseRecyclerAdapter<Shop, Adapter.viewholder> {


    public Adapter(@NonNull FirebaseRecyclerOptions<Shop> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull viewholder holder, int position, @NonNull Shop model) {
        holder.itemCompanyName.setText(model.getCompanyName());

        if(MainActivity.count <= model.getId())  MainActivity.count=model.getId()+1;

        holder.itemShopID.setText("ID: " + model.getId());
        holder.itemContactName.setText("Contact Name: " + model.getContactName());
        holder.itemMobileNumber.setText("Mobile Number: " + model.getMobileNumber());
        Glide.with(holder.image.getContext())
                .load(model.getImage())
                .into(holder.image);

        ((View)holder.image.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),Update.class);
                intent.putExtra("model", model);
                view.getContext().startActivity(intent);
            }
        });


    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new viewholder(view);
    }

    class viewholder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView itemCompanyName, itemShopID, itemContactName, itemMobileNumber;


        public viewholder(@NonNull View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.pic);
            itemCompanyName = (TextView) itemView.findViewById(R.id.itemCompanyName);
            itemShopID = (TextView) itemView.findViewById(R.id.itemShopID);
            itemContactName = (TextView) itemView.findViewById(R.id.itemContactName);
            itemMobileNumber = (TextView) itemView.findViewById(R.id.itemMobile);
        }
    }
}
