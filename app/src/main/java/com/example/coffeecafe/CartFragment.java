package com.example.coffeecafe;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.coffeecafe.models.CartItem;
import com.example.coffeecafe.utils.CartManager;
import java.util.List;

public class CartFragment extends Fragment {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private TextView tvEmptyCart, tvSubtotal, tvTotal;
    private Button btnCheckout;
    private CartManager cartManager;
    private List<CartItem> cartItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartManager = CartManager.getInstance(requireContext());

        recyclerView = view.findViewById(R.id.cart_recycler_view);
        tvEmptyCart = view.findViewById(R.id.tv_empty_cart);
        tvSubtotal = view.findViewById(R.id.tv_subtotal);
        tvTotal = view.findViewById(R.id.tv_total);
        btnCheckout = view.findViewById(R.id.btn_checkout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadCartItems();

        btnCheckout.setOnClickListener(v -> {
            if (cartItems != null && !cartItems.isEmpty()) {
                Intent intent = new Intent(getActivity(), CheckoutActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartItems();
    }

    private void loadCartItems() {
        cartItems = cartManager.getCartItems();
        
        if (cartItems.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyCart.setVisibility(View.VISIBLE);
            btnCheckout.setEnabled(false);
            btnCheckout.setAlpha(0.5f);
            tvSubtotal.setText("Ksh 0");
            tvTotal.setText("Ksh 0");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyCart.setVisibility(View.GONE);
            btnCheckout.setEnabled(true);
            btnCheckout.setAlpha(1.0f);
            
            cartAdapter = new CartAdapter(requireContext(), cartItems, this::onCartItemChanged);
            recyclerView.setAdapter(cartAdapter);
            
            updateTotals();
        }
    }

    private void onCartItemChanged() {
        loadCartItems();
    }

    private void updateTotals() {
        double total = cartManager.getCartTotal();
        tvSubtotal.setText(String.format("Ksh %.0f", total));
        tvTotal.setText(String.format("Ksh %.0f", total));
    }
}
