package com.example.coffeecafe;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrdersFragment extends Fragment {

    private CartViewModel cartViewModel;
    private RecyclerView recyclerView;
    private TextView tvTotal;
    private CartAdapter adapter;
    private Button btnPay;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        tvTotal = view.findViewById(R.id.tvTotal);
        recyclerView = view.findViewById(R.id.cartRecyclerView);
        btnPay = view.findViewById(R.id.btnPay);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        // Initialize adapter with empty list first
        adapter = new CartAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Observe cart items and update RecyclerView & total dynamically
        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            adapter.updateItems(items);
            adapter.notifyDataSetChanged();

            // Update grand total
            int total = 0;
            for (CartItem item : items) {
                total += item.getTotalPrice();
            }
            tvTotal.setText("Grand Total: Ksh " + total);
        });
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePayment("254708374149", 10);
            }
        });
        return view;
    }

    private void makePayment(String phone,int amount){
        OkHttpClient client = new OkHttpClient();
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("phone",phone);
            jsonObject.put("amount",amount);
        }catch (JSONException e){
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json;charset=utf-8")
        );

        String url = " https://4092b5d2137d.ngrok-free.app/stkpush";

        Request request = new Request.Builder().url(url).post(requestBody).build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(),
                            "Payment failed. Check connection",
                            Toast.LENGTH_SHORT
                    ).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!isAdded()) return;

                String resp = response.body() != null ? response.body().string() : "";

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(),
                            "STK push sent. Enter your M-Pesa PIN",
                            Toast.LENGTH_LONG
                    ).show();
                    Log.d("MPESA_RESPONSE", resp);
                });
            }
        });


    }



}
