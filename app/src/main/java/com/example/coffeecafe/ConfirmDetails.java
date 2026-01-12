package com.example.coffeecafe;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coffeecafe.config.SupabaseClient;
import com.example.coffeecafe.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfirmDetails extends AppCompatActivity {
 TextView tvFullName,tvEmail,tvPhone,tvGender;
 String getFullName,getEmail,getPhone,getGender,getPassword;
 Button btnCancel, btnConfirm;
 private ProgressDialog progressDialog;
 private SupabaseClient supabaseClient;
 private SessionManager sessionManager;
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_details);
        SystemHelper systemHelper = new SystemHelper(this);
        systemHelper.setSystemBars(R.color.gender,R.color.gender,false);

        // Initialize
        supabaseClient = SupabaseClient.getInstance();
        sessionManager = SessionManager.getInstance(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating account...");
        progressDialog.setCancelable(false);

        tvFullName = findViewById(R.id.tv_fullname);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        tvGender = findViewById(R.id.tv_gender);
        btnCancel = findViewById(R.id.bv_cancel);
        btnConfirm = findViewById(R.id.bv_confirm);
        
        Intent getUserDetails = getIntent();
        getFullName = getUserDetails.getStringExtra("full_name");
        getEmail = getUserDetails.getStringExtra("email");
        getPhone = getUserDetails.getStringExtra("phone");
        getGender = getUserDetails.getStringExtra("gender");
        getPassword = getUserDetails.getStringExtra("password");

        tvFullName.setText(String.format("Full Name: %s",getFullName));
        tvEmail.setText(String.format("Email: %s",getEmail));
        tvPhone.setText(String.format("Phone: %s",getPhone));
        tvGender.setText(String.format("Gender: %s",getGender));
        
        setBtnCancel();
        setBtnConfirm();

    }

    public void setBtnCancel(){
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void setBtnConfirm(){
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        progressDialog.show();

        supabaseClient.signUp(getEmail, getPassword, getFullName, getPhone, new SupabaseClient.AuthCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    try {
                        // Check if we got a session (auto-confirm enabled) or need email confirmation
                        if (response.has("access_token")) {
                            String accessToken = response.getString("access_token");
                            JSONObject user = response.getJSONObject("user");
                            String userId = user.getString("id");
                            String userEmail = user.getString("email");

                            // Save session
                            sessionManager.saveUserSession(userId, userEmail, accessToken, false);

                            Toast.makeText(ConfirmDetails.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            navigateToDashboard();
                        } else {
                            // Email confirmation required
                            Toast.makeText(ConfirmDetails.this, 
                                "Registration successful! Please check your email to confirm your account.", 
                                Toast.LENGTH_LONG).show();
                            navigateToLogin();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(ConfirmDetails.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        navigateToLogin();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(ConfirmDetails.this, "Registration failed: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(ConfirmDetails.this, DashBoard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ConfirmDetails.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}