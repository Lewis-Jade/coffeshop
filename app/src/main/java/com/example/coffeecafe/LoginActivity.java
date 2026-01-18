package com.example.coffeecafe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.atomic.AtomicBoolean;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail,etPassword;
    private Button buttonLogin;
    private FrameLayout loadingOverlay;
    private FirebaseAuth firebaseAuth;
    private  String userEmail,userPassword;
    private CheckBox rememberMe;

    private boolean isPasswordVisible
            = false;
    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);



        SystemHelper systemHelper = new SystemHelper(this);
        systemHelper.setSystemBars(R.color.gender,R.color.gender,false);
        //password toggle icon

        etEmail = findViewById(R.id.ev_email);
        etPassword = findViewById(R.id.ev_password);
        buttonLogin = findViewById(R.id.bv_login);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        rememberMe = findViewById(R.id.check_box);

        firebaseAuth = FirebaseAuth.getInstance();

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
 ///////       //////////////////// remember me check
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        boolean remember = prefs.getBoolean("remember", false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (remember && user != null && user.isEmailVerified()) {
            startActivity(new Intent(this, DashBoard.class));
            finish();
        }
////// Login button logic

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userEmail = etEmail.getText().toString().trim();
                userPassword = etPassword.getText().toString().trim();

                if(userEmail.isEmpty() || userPassword.isEmpty()){
                    Toast.makeText(getApplicationContext(),"All Fields mus be filled!",Toast.LENGTH_SHORT).show();
                    return;
                }
                showLoading();
                buttonLogin.setEnabled(false);
                firebaseAuth.signInWithEmailAndPassword(userEmail,userPassword)
                        .addOnCompleteListener(task -> {
                            hideLoading();
                            buttonLogin.setEnabled(true);
                            if(task.isSuccessful()){
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                if(firebaseUser != null && firebaseUser.isEmailVerified()){
                                    saveRememberMe(rememberMe.isChecked());

                                    startActivity(new Intent(getApplicationContext(),DashBoard.class));
                                    finish();
                                }else{
                                    firebaseAuth.signOut();
                                    showEmailNotVerifiedDialog(firebaseUser);

                                }
                            }else{
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });



            }
        });


    }

    private void saveRememberMe(boolean remember) {
        SharedPreferences sharedPreferences = getSharedPreferences("firebaseAuth",MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("remember",remember).apply();

    }

    private  void showEmailNotVerifiedDialog(FirebaseUser firebaseUser){

        new AlertDialog.Builder(this)
                .setTitle("Email not Verified")
                .setMessage("Verify your email before logging in !")
                .setPositiveButton("resend email",(d,w)->{
                    if(firebaseUser != null) firebaseUser.sendEmailVerification();
                    Toast.makeText(getApplicationContext(),"verification email send",Toast.LENGTH_SHORT).show();

                }).setNegativeButton("Ok",null).show();
    }

    private void showLoading() {
        loadingOverlay.setVisibility(View.VISIBLE);
        loadingOverlay.post(() -> getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE));
    }

    private void hideLoading() {
        loadingOverlay.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }




}