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
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private CartViewModel cartViewModel;
    private RecyclerView rvRecentPurchases;
    private HistoryAdapter historyAdapter;
    private TextView tvRecentPurchasesTitle, tvNoPurchases;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvRecentPurchases = view.findViewById(R.id.rvRecentPurchases);
        tvRecentPurchasesTitle = view.findViewById(R.id.tvRecentPurchasesTitle);
        tvNoPurchases = view.findViewById(R.id.tvNoPurchases);

        rvRecentPurchases.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAdapter = new HistoryAdapter(new ArrayList<>());
        rvRecentPurchases.setAdapter(historyAdapter);

        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        cartViewModel.getPurchasedItems().observe(getViewLifecycleOwner(), items -> {
            if (items != null && !items.isEmpty()) {
                historyAdapter.updateItems(items);
                tvRecentPurchasesTitle.setVisibility(View.VISIBLE);
                rvRecentPurchases.setVisibility(View.VISIBLE);
                tvNoPurchases.setVisibility(View.GONE);
            } else {
                tvRecentPurchasesTitle.setVisibility(View.GONE);
                rvRecentPurchases.setVisibility(View.GONE);
                tvNoPurchases.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }
}