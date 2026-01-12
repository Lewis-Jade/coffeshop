package com.example.coffeecafe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.coffeecafe.models.CartItem;
import com.example.coffeecafe.utils.CartManager;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartItem> cartItems;
    private Context context;
    private CartManager cartManager;
    private OnCartChangedListener listener;

    public interface OnCartChangedListener {
        void onCartChanged();
    }

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartChangedListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
        this.cartManager = CartManager.getInstance(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        
        holder.name.setText(item.getProductName());
        holder.price.setText("Ksh " + (int)item.getProductPrice());
        holder.quantity.setText(String.valueOf(item.getQuantity()));
        holder.subtotal.setText("Ksh " + (int)item.getSubtotal());
        
        // Set image
        if (item.getLocalImageResource() != 0) {
            holder.image.setImageResource(item.getLocalImageResource());
        }

        holder.btnPlus.setOnClickListener(v -> {
            cartManager.updateCartItem(item.getProductId(), item.getQuantity() + 1);
            listener.onCartChanged();
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                cartManager.updateCartItem(item.getProductId(), item.getQuantity() - 1);
            } else {
                cartManager.removeFromCart(item.getProductId());
            }
            listener.onCartChanged();
        });

        holder.btnRemove.setOnClickListener(v -> {
            cartManager.removeFromCart(item.getProductId());
            listener.onCartChanged();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, quantity, subtotal;
        ImageView image;
        ImageButton btnPlus, btnMinus, btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cart_item_name);
            price = itemView.findViewById(R.id.cart_item_price);
            quantity = itemView.findViewById(R.id.cart_item_quantity);
            subtotal = itemView.findViewById(R.id.cart_item_subtotal);
            image = itemView.findViewById(R.id.cart_item_image);
            btnPlus = itemView.findViewById(R.id.cart_btn_plus);
            btnMinus = itemView.findViewById(R.id.cart_btn_minus);
            btnRemove = itemView.findViewById(R.id.cart_btn_remove);
        }
    }
}
