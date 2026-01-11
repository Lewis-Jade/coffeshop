package com.example.coffeecafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
Button registerUser,loginUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        SystemHelper systemHelper = new SystemHelper(this);
        systemHelper.setSystemBars(R.color.gender,R.color.gender,false);

        // Check if user is already logged in
        com.example.coffeecafe.utils.SessionManager sessionManager = 
            com.example.coffeecafe.utils.SessionManager.getInstance(this);
        
        // Verify BuildConfig credentials are loaded
        if (com.example.coffeecafe.BuildConfig.SUPABASE_URL.isEmpty()) {
            android.widget.Toast.makeText(this, 
                "Configuration error: Please set up local.properties file", 
                android.widget.Toast.LENGTH_LONG).show();
            // Continue to allow viewing the app structure
        }
        
        if (sessionManager.isLoggedIn()) {
            sessionManager.restoreSession();
            startActivity(new Intent(MainActivity.this, DashBoard.class));
            finish();
            return;
        }

        registerUser = findViewById(R.id.bv_register);
        loginUser = findViewById(R.id.bv_login);

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(MainActivity.this,SignupActivity.class);
                startActivity(registerIntent);
            }
        });

        loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginUser = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(loginUser);
            }
        });

    }
}