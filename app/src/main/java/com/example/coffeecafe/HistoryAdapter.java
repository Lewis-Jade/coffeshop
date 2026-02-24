package com.example.coffeecafe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<CartItem> purchasedItems;

    public HistoryAdapter(ArrayList<CartItem> purchasedItems) {
        this.purchasedItems = purchasedItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchased, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = purchasedItems.get(position);
        holder.tvName.setText(item.getName());
        holder.tvQuantity.setText("Qty: " + item.getQuantity());
        holder.tvPrice.setText("Ksh " + item.getTotalPrice());
    }

    @Override
    public int getItemCount() {
        return purchasedItems.size();
    }

    public void updateItems(ArrayList<CartItem> newItems) {
        this.purchasedItems.clear();
        this.purchasedItems.addAll(newItems);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPurchasedItemName);
            tvQuantity = itemView.findViewById(R.id.tvPurchasedItemQuantity);
            tvPrice = itemView.findViewById(R.id.tvPurchasedItemPrice);
        }
    }
}