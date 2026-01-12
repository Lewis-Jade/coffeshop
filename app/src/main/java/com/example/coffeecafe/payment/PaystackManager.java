package com.example.coffeecafe.payment;

import android.content.Context;
import com.example.coffeecafe.utils.Constants;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

/**
 * Paystack Payment Manager
 * Handles M-Pesa payments through Paystack API
 * No SDK required - direct API integration
 */
public class PaystackManager {
    private static PaystackManager instance;
    private final OkHttpClient httpClient;
    private final String publicKey;

    private PaystackManager(Context context) {
        this.httpClient = new OkHttpClient();
        this.publicKey = Constants.getPaystackPublicKey();
        
        // Validate public key
        if (this.publicKey == null || this.publicKey.isEmpty() || 
            this.publicKey.equals("") || this.publicKey.equals("YOUR_PAYSTACK_PUBLIC_KEY")) {
            throw new IllegalStateException(
                "Paystack public key not configured!\n" +
                "Please add your Paystack key to local.properties:\n" +
                "paystack.key=pk_test_your_actual_key_here"
            );
        }
    }

    public static synchronized PaystackManager getInstance(Context context) {
        if (instance == null) {
            instance = new PaystackManager(context);
        }
        return instance;
    }

    public interface PaymentCallback {
        void onSuccess(String reference, String message);
        void onError(String error);
    }

    /**
     * Initialize payment transaction
     */
    public void initializePayment(String email, double amount, PaymentCallback callback) {
        try {
            // Convert amount to kobo (Paystack requires amount in smallest unit)
            int amountInKobo = (int) (amount * 100);

            JSONObject requestBody = new JSONObject();
            requestBody.put("email", email);
            requestBody.put("amount", amountInKobo);
            requestBody.put("currency", "KES");
            requestBody.put("channels", new org.json.JSONArray().put("mobile_money"));

            Request request = new Request.Builder()
                    .url("https://api.paystack.co/transaction/initialize")
                    .addHeader("Authorization", "Bearer " + publicKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError("Network error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        if (response.isSuccessful() && json.getBoolean("status")) {
                            JSONObject data = json.getJSONObject("data");
                            String reference = data.getString("reference");
                            String authorizationUrl = data.getString("authorization_url");
                            callback.onSuccess(reference, authorizationUrl);
                        } else {
                            String message = json.optString("message", "Payment initialization failed");
                            callback.onError(message);
                        }
                    } catch (JSONException e) {
                        callback.onError("Failed to parse response");
                    }
                }
            });
        } catch (JSONException e) {
            callback.onError(e.getMessage());
        }
    }

    /**
     * Verify payment transaction
     */
    public void verifyPayment(String reference, PaymentCallback callback) {
        Request request = new Request.Builder()
                .url("https://api.paystack.co/transaction/verify/" + reference)
                .addHeader("Authorization", "Bearer " + publicKey)
                .get()
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    JSONObject json = new JSONObject(responseBody);
                    if (response.isSuccessful() && json.getBoolean("status")) {
                        JSONObject data = json.getJSONObject("data");
                        String status = data.getString("status");
                        
                        if ("success".equals(status)) {
                            callback.onSuccess(reference, "Payment verified successfully");
                        } else {
                            callback.onError("Payment verification failed: " + status);
                        }
                    } else {
                        String message = json.optString("message", "Verification failed");
                        callback.onError(message);
                    }
                } catch (JSONException e) {
                    callback.onError("Failed to parse response");
                }
            }
        });
    }
}
