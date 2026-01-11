package com.example.coffeecafe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.coffeecafe.models.CartItem;
import com.example.coffeecafe.repositories.OrderRepository;
import com.example.coffeecafe.utils.CartManager;
import com.example.coffeecafe.utils.Constants;
import com.example.coffeecafe.utils.SessionManager;
import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView tvTotal, tvEmail;
    private Button btnPayWithPaystack;
    private CartManager cartManager;
    private SessionManager sessionManager;
    private OrderRepository orderRepository;
    private List<CartItem> cartItems;
    private double totalAmount;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        SystemHelper systemHelper = new SystemHelper(this);
        systemHelper.setSystemBars(R.color.gender, R.color.gender, false);

        // Initialize Paystack with public key from BuildConfig
        PaystackSdk.initialize(getApplicationContext());
        PaystackSdk.setPublicKey(Constants.getPaystackPublicKey());

        cartManager = CartManager.getInstance(this);
        sessionManager = SessionManager.getInstance(this);
        orderRepository = new OrderRepository(this);
        
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing payment...");
        progressDialog.setCancelable(false);

        recyclerView = findViewById(R.id.checkout_recycler_view);
        tvTotal = findViewById(R.id.checkout_total);
        tvEmail = findViewById(R.id.checkout_email);
        btnPayWithPaystack = findViewById(R.id.btn_pay_with_paystack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadCheckoutData();

        btnPayWithPaystack.setOnClickListener(v -> initiatePaystackPayment());
    }

    private void loadCheckoutData() {
        cartItems = cartManager.getCartItems();
        totalAmount = cartManager.getCartTotal();

        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Display items
        CheckoutItemsAdapter adapter = new CheckoutItemsAdapter(this, cartItems);
        recyclerView.setAdapter(adapter);

        // Display total
        tvTotal.setText(String.format("Ksh %.0f", totalAmount));
        
        // Display user email
        String email = sessionManager.getUserEmail();
        if (email != null) {
            tvEmail.setText(email);
        }
    }

    private void initiatePaystackPayment() {
        String userEmail = sessionManager.getUserEmail();
        if (userEmail == null) {
            Toast.makeText(this, "Please login to continue", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert amount to kobo (Paystack requires amount in smallest currency unit)
        // For KES, multiply by 100
        int amountInCents = (int) (totalAmount * 100);

        Charge charge = new Charge();
        charge.setEmail(userEmail);
        charge.setAmount(amountInCents);
        charge.setCurrency("KES");

        progressDialog.show();

        PaystackSdk.chargeCard(this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    handlePaymentSuccess(transaction);
                });
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                // Called before validation
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(CheckoutActivity.this, 
                        "Payment failed: " + error.getMessage(), 
                        Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void handlePaymentSuccess(Transaction transaction) {
        String reference = transaction.getReference();
        
        progressDialog.setMessage("Creating order...");
        progressDialog.show();
        
        // Save order to database
        orderRepository.createOrder(cartItems, totalAmount, reference, new OrderRepository.CreateOrderCallback() {
            @Override
            public void onSuccess(String orderId) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    cartManager.clearCart();
                    
                    Intent intent = new Intent(CheckoutActivity.this, PaymentSuccessActivity.class);
                    intent.putExtra("reference", reference);
                    intent.putExtra("amount", totalAmount);
                    intent.putExtra("order_id", orderId);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    // Even if order saving fails, payment was successful
                    // Clear cart and show success but log the error
                    Toast.makeText(CheckoutActivity.this, 
                        "Payment successful but order recording failed. Contact support with reference: " + reference, 
                        Toast.LENGTH_LONG).show();
                    
                    cartManager.clearCart();
                    
                    Intent intent = new Intent(CheckoutActivity.this, PaymentSuccessActivity.class);
                    intent.putExtra("reference", reference);
                    intent.putExtra("amount", totalAmount);
                    startActivity(intent);
                    finish();
                });
            }
        });
    }
}
