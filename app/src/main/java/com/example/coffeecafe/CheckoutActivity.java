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
import android.content.Intent;
import android.net.Uri;
import com.example.coffeecafe.models.CartItem;
import com.example.coffeecafe.payment.PaystackManager;
import com.example.coffeecafe.repositories.OrderRepository;
import com.example.coffeecafe.utils.CartManager;
import com.example.coffeecafe.utils.SessionManager;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView tvTotal, tvEmail;
    private Button btnPayWithMpesa;
    private CartManager cartManager;
    private SessionManager sessionManager;
    private OrderRepository orderRepository;
    private PaystackManager paystackManager;
    private List<CartItem> cartItems;
    private double totalAmount;
    private ProgressDialog progressDialog;
    private String currentPaymentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        SystemHelper systemHelper = new SystemHelper(this);
        systemHelper.setSystemBars(R.color.gender, R.color.gender, false);

        cartManager = CartManager.getInstance(this);
        sessionManager = SessionManager.getInstance(this);
        orderRepository = new OrderRepository(this);
        
        // Initialize PaystackManager with error handling
        try {
            paystackManager = PaystackManager.getInstance(this);
        } catch (IllegalStateException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing payment...");
        progressDialog.setCancelable(false);

        recyclerView = findViewById(R.id.checkout_recycler_view);
        tvTotal = findViewById(R.id.checkout_total);
        tvEmail = findViewById(R.id.checkout_email);
        btnPayWithMpesa = findViewById(R.id.btn_pay_with_mpesa);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadCheckoutData();

        btnPayWithMpesa.setOnClickListener(v -> initiateMpesaPayment());
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

    private void initiateMpesaPayment() {
        String userEmail = sessionManager.getUserEmail();
        if (userEmail == null) {
            Toast.makeText(this, "Please login to continue", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        paystackManager.initializePayment(userEmail, totalAmount, new PaystackManager.PaymentCallback() {
            @Override
            public void onSuccess(String reference, String authorizationUrl) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    currentPaymentReference = reference;
                    
                    // Open M-PESA payment page in browser
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(authorizationUrl));
                    startActivity(browserIntent);
                    
                    Toast.makeText(CheckoutActivity.this, 
                        "Complete payment in browser. Reference: " + reference, 
                        Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(CheckoutActivity.this, 
                        "Payment initialization failed: " + error, 
                        Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // When user returns from browser, verify payment
        if (currentPaymentReference != null) {
            verifyPaymentStatus();
        }
    }

    private void verifyPaymentStatus() {
        progressDialog.setMessage("Verifying payment...");
        progressDialog.show();

        paystackManager.verifyPayment(currentPaymentReference, new PaystackManager.PaymentCallback() {
            @Override
            public void onSuccess(String reference, String message) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    handlePaymentSuccess(reference);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(CheckoutActivity.this, 
                        "Payment verification: " + error, 
                        Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void handlePaymentSuccess(String reference) {
        
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
