package com.example.coffeecafe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final ArrayList<CartItem> cartItems;

    public CartAdapter(ArrayList<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_orders_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        DrinksModel drink = item.getDrink();

        holder.tvName.setText(drink.getName());
        holder.tvQty.setText("x" + item.getQuantity());
        holder.tvSubtotal.setText("Ksh " + item.getTotalPrice());
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvSubtotal;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
        }
    }

    public void updateItems(ArrayList<CartItem> newItems) {
        cartItems.clear();
        cartItems.addAll(newItems);
    }
}