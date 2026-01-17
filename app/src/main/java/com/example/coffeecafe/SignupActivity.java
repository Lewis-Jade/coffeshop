package com.example.coffeecafe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import org.jspecify.annotations.NonNull;

public class SignupActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etPassword, etConfirmPassword;
    private RadioGroup radioGroup;
    private RadioButton radioMale, radioFemale, selectedGender;
    private Button registerUser;
    private FrameLayout loadingOverlay;

    private String getFullName, getEmail, getPhone, getPassword, getConfirmedPassword, gender;
    private boolean isPasswordVisible = false, isConfirmPasswordVisible = false;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.signup);

        SystemHelper systemHelper = new SystemHelper(this);
        systemHelper.setSystemBars(R.color.gender,R.color.gender,false);

        // Initialize views
        etFullName = findViewById(R.id.ev_full_name);
        etEmail = findViewById(R.id.ev_email);
        etPhone = findViewById(R.id.ev_phone);
        etPassword = findViewById(R.id.ev_password);
        etConfirmPassword = findViewById(R.id.ev_conf_password);
        registerUser = findViewById(R.id.bv_register);
        radioGroup = findViewById(R.id.tg_gender_select);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        restoreFieldsOnCancel();

        auth = FirebaseAuth.getInstance();

        // Password toggle
        setupPasswordToggle();

        // Restore saved state
        if (savedInstanceState != null) restoreFields(savedInstanceState);

        // Register click
        registerUser.setOnClickListener(view -> handleRegister());
    }

    private void handleRegister() {
        // Get input values
        getFullName = etFullName.getText().toString().trim();
        getEmail = etEmail.getText().toString().trim();
        getPhone = etPhone.getText().toString().trim();
        getPassword = etPassword.getText().toString().trim();
        getConfirmedPassword = etConfirmPassword.getText().toString().trim();

        int selectedGenderId = radioGroup.getCheckedRadioButtonId();

        // Validate
        if (getFullName.isEmpty() || getEmail.isEmpty() || getPhone.isEmpty() ||
                getPassword.isEmpty() || getConfirmedPassword.isEmpty() || selectedGenderId == -1) {
            Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!getPassword.equals(getConfirmedPassword)) {
            etConfirmPassword.setError("Passwords do not match!");
            return;
        }

        // Show loader and lock button
        showLoading();
        registerUser.setEnabled(false);

        selectedGender = findViewById(selectedGenderId);
        gender = selectedGender.getText().toString().trim();

        // Firebase signup with minimum spinner display
        long startTime = System.currentTimeMillis();
        loadingOverlay.post(() -> auth.createUserWithEmailAndPassword(getEmail, getPassword)
                .addOnCompleteListener(task -> {
                    long elapsed = System.currentTimeMillis() - startTime;
                    long delay = Math.max(300 - elapsed, 0); // ensure spinner shows at least 300ms

                    loadingOverlay.postDelayed(() -> {
                        hideLoading();
                        registerUser.setEnabled(true);

                        if (task.isSuccessful()) {
                            // Go to ConfirmDetails
                            Intent confirmDetails = new Intent(this, ConfirmDetails.class);
                            confirmDetails.putExtra("full_name", getFullName);
                            confirmDetails.putExtra("email", getEmail);
                            confirmDetails.putExtra("phone", getPhone);
                            confirmDetails.putExtra("gender", gender);
                            startActivityForResult(confirmDetails, 1001);


                        } else {
                            Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }, delay);
                }));
    }

    private void setupPasswordToggle() {
        etPassword.setOnTouchListener((v, event) -> togglePasswordVisibility(etPassword, event, true));
        etConfirmPassword.setOnTouchListener((v, event) -> togglePasswordVisibility(etConfirmPassword, event, false));
    }

    private boolean togglePasswordVisibility(EditText et, MotionEvent event, boolean isPassword) {
        final int DRAWABLE_RIGHT = 2;
        if (event.getAction() == MotionEvent.ACTION_UP &&
                event.getRawX() >= (et.getRight() - et.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

            if (isPassword) isPasswordVisible = !isPasswordVisible;
            else isConfirmPasswordVisible = !isConfirmPasswordVisible;

            boolean visible = isPassword ? isPasswordVisible : isConfirmPasswordVisible;
            et.setTransformationMethod(visible ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
            et.setCompoundDrawablesWithIntrinsicBounds(0, 0, visible ? R.drawable.eye_open : R.drawable.eye_closed, 0);
            et.setSelection(et.getText().length());
            return true;
        }
        return false;
    }

    // Loading spinner methods
    private void showLoading() {
        loadingOverlay.setVisibility(View.VISIBLE);
        loadingOverlay.post(() -> getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE));
    }

    private void hideLoading() {
        loadingOverlay.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerUser.setEnabled(true);
        hideLoading();
    }

    // Save and restore field state (rotations or process death)
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("fullName", etFullName.getText().toString());
        outState.putString("email", etEmail.getText().toString());
        outState.putString("phone", etPhone.getText().toString());
        outState.putString("password", etPassword.getText().toString());
        outState.putString("confirmPassword", etConfirmPassword.getText().toString());
    }

    private void restoreFields(Bundle savedInstanceState) {
        etFullName.setText(savedInstanceState.getString("fullName"));
        etEmail.setText(savedInstanceState.getString("email"));
        etPhone.setText(savedInstanceState.getString("phone"));
        etPassword.setText(savedInstanceState.getString("password"));
        etConfirmPassword.setText(savedInstanceState.getString("confirmPassword"));
    }

    /////Restore  the input field from ConfirmDetails on cancel

    private void restoreFieldsOnCancel(){
        // Restore previous values if returning from ConfirmDetails
        Intent intent = getIntent();
        if (intent != null) {
            String fullName = intent.getStringExtra("full_name");
            String email = intent.getStringExtra("email");
            String phone = intent.getStringExtra("phone");
            String genderStr = intent.getStringExtra("gender");

            if (fullName != null) etFullName.setText(fullName);
            if (email != null) etEmail.setText(email);
            if (phone != null) etPhone.setText(phone);

            // Restore gender radio
            if (genderStr != null) {
                if (genderStr.equalsIgnoreCase("Male")) {
                    radioMale.setChecked(true);
                } else if (genderStr.equalsIgnoreCase("Female")) {
                    radioFemale.setChecked(true);
                }
            }
        }

    }
}
