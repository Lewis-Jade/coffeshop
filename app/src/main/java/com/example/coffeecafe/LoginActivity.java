package com.example.coffeecafe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.atomic.AtomicBoolean;

public class LoginActivity extends AppCompatActivity {
    private EditText etPassword;
    Button buttonLogin;
    private boolean isPasswordVisible = false;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        SystemHelper systemHelper = new SystemHelper(this);
        systemHelper.setSystemBars(R.color.gender,R.color.gender,false);
        //password toggle icon


        etPassword = findViewById(R.id.ev_password);
        buttonLogin = findViewById(R.id.bv_login);
        final boolean[] isPasswordVisible = {false};

        etPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2; // index for drawableEnd
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etPassword.getRight() - etPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    isPasswordVisible[0] = !isPasswordVisible[0];

                    if (isPasswordVisible[0]) {
                        etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_open, 0);
                    } else {
                        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_closed, 0);
                    }

                    etPassword.setSelection(etPassword.getText().length());
                    return true;
                }
            }
            return false;
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DashBoard.class));
            }
        });


    }
}