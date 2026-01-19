package com.example.coffeecafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AutoLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auto_login);

        SystemHelper systemHelper = new SystemHelper(this);
        systemHelper.setSystemBars(R.color.gender,R.color.gender,false);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        SharedPreferences prefs = getSharedPreferences("firebaseAuth", MODE_PRIVATE);

        boolean remember = prefs.getBoolean("remember", false);
        FirebaseUser user = firebaseAuth.getCurrentUser();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if (remember && user != null && user.isEmailVerified()) {
                startActivity(new Intent(this, DashBoard.class));
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }

            finish();

        }, 800);
    }
    }
