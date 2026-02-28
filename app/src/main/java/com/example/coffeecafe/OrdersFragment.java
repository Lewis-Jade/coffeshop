package com.example.coffeecafe;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

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
    private int grandTotal = 0;

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

        adapter = new CartAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            adapter.updateItems(items);
            adapter.notifyDataSetChanged();

            grandTotal = 0;
            if (items != null) {
                for (CartItem item : items) {
                    grandTotal += item.getTotalPrice();
                }
            }
            tvTotal.setText("Grand Total: Ksh " + grandTotal);
        });

        btnPay.setOnClickListener(v -> {
            if (grandTotal > 0) {
                showPaymentDialog();
            } else {
                Toast.makeText(getContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void showPaymentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_pay_mpesa, null);
        builder.setView(dialogView);

        TextInputEditText etPhone = dialogView.findViewById(R.id.etPhone);
        TextInputEditText etAmount = dialogView.findViewById(R.id.etAmount);
        MaterialButton btnConfirmPay = dialogView.findViewById(R.id.btnConfirmPay);
        ProgressBar progressBar = new ProgressBar(getContext()); // Simple logic to show loading

        etAmount.setText(String.valueOf(grandTotal));

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnConfirmPay.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            if (phone.isEmpty() || !phone.startsWith("254") || phone.length() != 12) {
                etPhone.setError("Enter a valid phone number (2547...)");
                return;
            }

            // Show loading state
            btnConfirmPay.setEnabled(false);
            btnConfirmPay.setText("Processing...");

            makePayment(phone, grandTotal, new PaymentCallback() {
                @Override
                public void onSuccess() {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Payment Successful!", Toast.LENGTH_LONG).show();
                            cartViewModel.moveToPurchased();
                            dialog.dismiss();
                        });
                    }
                }

                @Override
                public void onFailure(String message) {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            btnConfirmPay.setEnabled(true);
                            btnConfirmPay.setText("PAY NOW");
                            Toast.makeText(requireContext(), "Failed: " + message, Toast.LENGTH_LONG).show();
                            // Pop-up stays open here
                        });
                    }
                }
            });
        });

        dialog.show();
    }

    private void makePayment(String phone, int amount, PaymentCallback callback) {
        OkHttpClient client = new OkHttpClient();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone", phone);
            jsonObject.put("amount", amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json;charset=utf-8")
        );

        String url = "https://m-pesa-backend-xmc8.onrender.com/stkpush";

        Request request = new Request.Builder().url(url).post(requestBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure("Connection error. Check internet.");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String resp = response.body() != null ? response.body().string() : "";
                Log.d("MPESA_RESPONSE", resp);
                
                if (resp.contains("Success") || resp.contains("sent")) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(resp.isEmpty() ? "Unknown error" : resp);
                }
            }
        });
    }

    interface PaymentCallback {
        void onSuccess();
        void onFailure(String message);
    }
}