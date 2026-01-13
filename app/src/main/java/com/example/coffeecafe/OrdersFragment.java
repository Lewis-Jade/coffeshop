package com.example.coffeecafe;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class OrdersFragment extends Fragment {

    private CartViewModel cartViewModel;
    private RecyclerView recyclerView;
    private TextView tvTotal;
    private CartAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        tvTotal = view.findViewById(R.id.tvTotal);
        recyclerView = view.findViewById(R.id.cartRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        // Initialize adapter with empty list first
        adapter = new CartAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Observe cart items and update RecyclerView & total dynamically
        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            adapter.updateItems(items); // you'll need to add updateItems() in your adapter
            adapter.notifyDataSetChanged();

            // Update grand total
            int total = 0;
            for (CartItem item : items) {
                total += item.getTotalPrice();
            }
            tvTotal.setText("Grand Total: Ksh " + total);
        });

        return view;
    }
}
