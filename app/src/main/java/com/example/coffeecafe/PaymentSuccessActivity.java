package com.example.coffeecafe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentSuccessActivity extends AppCompatActivity {
    private TextView tvReference, tvAmount;
    private Button btnViewOrders, btnBackToDrinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        SystemHelper systemHelper = new SystemHelper(this);
        systemHelper.setSystemBars(R.color.gender, R.color.gender, false);

        tvReference = findViewById(R.id.tv_payment_reference);
        tvAmount = findViewById(R.id.tv_payment_amount);
        btnViewOrders = findViewById(R.id.btn_view_orders);
        btnBackToDrinks = findViewById(R.id.btn_back_to_drinks);

        // Get payment details from intent
        String reference = getIntent().getStringExtra("reference");
        double amount = getIntent().getDoubleExtra("amount", 0);

        tvReference.setText("Reference: " + reference);
        tvAmount.setText(String.format("Amount Paid: Ksh %.0f", amount));

        btnViewOrders.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashBoard.class);
            intent.putExtra("navigate_to", "orders");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        btnBackToDrinks.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashBoard.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // Prevent going back to checkout
        Intent intent = new Intent(this, DashBoard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
