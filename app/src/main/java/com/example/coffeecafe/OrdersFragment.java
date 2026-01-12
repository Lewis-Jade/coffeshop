package com.example.coffeecafe;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.coffeecafe.models.Order;
import com.example.coffeecafe.repositories.OrderRepository;
import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {
    private RecyclerView recyclerView;
    private OrdersAdapter ordersAdapter;
    private ProgressBar progressBar;
    private TextView tvEmptyOrders;
    private OrderRepository orderRepository;
    private List<Order> orders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        
        orderRepository = new OrderRepository(requireContext());
        
        recyclerView = view.findViewById(R.id.orders_recycler_view);
        progressBar = view.findViewById(R.id.orders_progress_bar);
        tvEmptyOrders = view.findViewById(R.id.tv_empty_orders);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orders = new ArrayList<>();
        
        loadOrders();
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvEmptyOrders.setVisibility(View.GONE);
        
        orderRepository.fetchUserOrders(new OrderRepository.FetchOrdersCallback() {
            @Override
            public void onSuccess(List<Order> fetchedOrders) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    orders = fetchedOrders;
                    
                    if (orders.isEmpty()) {
                        tvEmptyOrders.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        ordersAdapter = new OrdersAdapter(requireContext(), orders);
                        recyclerView.setAdapter(ordersAdapter);
                    }
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvEmptyOrders.setVisibility(View.VISIBLE);
                    tvEmptyOrders.setText("Failed to load orders: " + error);
                    Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}