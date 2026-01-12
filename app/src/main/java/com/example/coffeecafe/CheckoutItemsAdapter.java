package com.example.coffeecafe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.coffeecafe.models.CartItem;
import java.util.List;

public class CheckoutItemsAdapter extends RecyclerView.Adapter<CheckoutItemsAdapter.ViewHolder> {
    private List<CartItem> items;
    private Context context;

    public CheckoutItemsAdapter(Context context, List<CartItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = items.get(position);
        
        holder.name.setText(item.getProductName());
        holder.quantity.setText("x" + item.getQuantity());
        holder.price.setText("Ksh " + (int)item.getSubtotal());
        
        if (item.getLocalImageResource() != 0) {
            holder.image.setImageResource(item.getLocalImageResource());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, quantity, price;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.checkout_item_name);
            quantity = itemView.findViewById(R.id.checkout_item_quantity);
            price = itemView.findViewById(R.id.checkout_item_price);
            image = itemView.findViewById(R.id.checkout_item_image);
        }
    }
}
