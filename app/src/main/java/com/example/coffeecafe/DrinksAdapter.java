package com.example.coffeecafe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DrinksAdapter extends RecyclerView.Adapter<DrinksAdapter.ViewHolder> {
     ArrayList<DrinksModel> itemList;
     Context context;
    public DrinksAdapter(Context context,ArrayList<DrinksModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public DrinksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DrinksAdapter.ViewHolder holder, int position) {
     DrinksModel item = itemList.get(position);
     holder.name.setText(item.getName());
     holder.price.setText( "Ksh."+item.getPrice());
     holder.description.setText(item.getDescription());
     holder.image.setImageResource(item.getImage());

     holder.itemView.setOnClickListener(view ->{
         Bundle bundle = new Bundle();
         bundle.putSerializable("coffee", item);
         CoffeeDetailFragment coffeeDetailFragment = new CoffeeDetailFragment();
         coffeeDetailFragment.setArguments(bundle);

         ((FragmentActivity)context).getSupportFragmentManager()
                 .beginTransaction()
                 .replace(R.id.frameContainer,coffeeDetailFragment)
                 .addToBackStack(null)
                 .commit();

     });



    }

    @Override
    public int getItemCount() {
        return  itemList.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
      TextView name,price,description;
      ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            description = itemView.findViewById(R.id.description);
            image = itemView.findViewById(R.id.coffeeImage);
        }
    }
}
