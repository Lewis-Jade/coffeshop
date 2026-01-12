package com.example.coffeecafe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.coffeecafe.models.Order;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {
    private List<Order> orders;
    private Context context;

    public OrdersAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        
        holder.orderId.setText("Order #" + order.getId().substring(0, 8));
        holder.totalAmount.setText("Ksh " + (int)order.getTotalAmount());
        holder.status.setText(capitalizeFirst(order.getStatus()));
        
        // Format date
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(order.getCreatedAt());
            if (date != null) {
                holder.date.setText(outputFormat.format(date));
            }
        } catch (ParseException e) {
            holder.date.setText(order.getCreatedAt());
        }
        
        // Set status color
        switch (order.getStatus().toLowerCase()) {
            case "pending":
                holder.status.setTextColor(context.getResources().getColor(R.color.ic_minus));
                break;
            case "paid":
                holder.status.setTextColor(context.getResources().getColor(R.color.ic_plus));
                break;
            case "completed":
                holder.status.setTextColor(context.getResources().getColor(R.color.gender));
                break;
            case "cancelled":
                holder.status.setTextColor(context.getResources().getColor(R.color.ic_minus));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, totalAmount, status, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
            totalAmount = itemView.findViewById(R.id.order_total);
            status = itemView.findViewById(R.id.order_status);
            date = itemView.findViewById(R.id.order_date);
        }
    }
}
