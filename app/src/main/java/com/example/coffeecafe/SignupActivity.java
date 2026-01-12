package com.example.coffeecafe;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
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

import com.example.coffeecafe.config.SupabaseClient;
import com.example.coffeecafe.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {
    private EditText etFullName,etEmail,etPhone,etPassword, etConfirmPassword;
    private String getFullName,getEmail,getPhone,getPassword,getConfirmedPassword,gender;
    private RadioGroup radioGroup;
    private RadioButton radioMale,radioFemale,selectedGender;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private ProgressDialog progressDialog;
    private SupabaseClient supabaseClient;
    private SessionManager sessionManager;

    private Button registerUser;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SystemHelper systemHelper = new SystemHelper(this);
        systemHelper.setSystemBars(R.color.gender,R.color.gender,false);

        // Initialize
        supabaseClient = SupabaseClient.getInstance();
        sessionManager = SessionManager.getInstance(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating account...");
        progressDialog.setCancelable(false);

        //password and confirm password icon toggle
        etFullName = findViewById(R.id.ev_full_name);
        etEmail = findViewById(R.id.ev_email);
        etPhone = findViewById(R.id.ev_phone);
        etPassword = findViewById(R.id.ev_password);
        etConfirmPassword = findViewById(R.id.ev_conf_password);
        registerUser = findViewById(R.id.bv_register);
        radioGroup = findViewById(R.id.tg_gender_select);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);
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

        //confirm password



        etConfirmPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etConfirmPassword.getRight() - etConfirmPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    isConfirmPasswordVisible = !isConfirmPasswordVisible;

                    if (isConfirmPasswordVisible) {
                        etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        etConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_open, 0);
                    } else {
                        etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        etConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_closed, 0);
                    }

                    etConfirmPassword.setSelection(etConfirmPassword.getText().length());
                    return true;
                }
            }
            return false;
        });



       registerUser.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View view) {
               performRegistration();
           }
       });





    }

    private void performRegistration() {
        //validate if the edittext is empty
        getFullName = etFullName.getText().toString().trim();
        getEmail = etEmail.getText().toString().trim();
        getPhone = etPhone.getText().toString().trim();
        getPassword = etPassword.getText().toString().trim();
        getConfirmedPassword = etConfirmPassword.getText().toString().trim();

        int selectedGenderId = radioGroup.getCheckedRadioButtonId();
        
        // Validation
        if(TextUtils.isEmpty(getFullName)) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return;
        }
        
        if(TextUtils.isEmpty(getEmail)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(getEmail).matches()) {
            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return;
        }
        
        if(TextUtils.isEmpty(getPhone)) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
            return;
        }
        
        if(TextUtils.isEmpty(getPassword)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }
        
        if(getPassword.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }
        
        if(TextUtils.isEmpty(getConfirmedPassword)) {
            etConfirmPassword.setError("Confirm password is required");
            etConfirmPassword.requestFocus();
            return;
        }
        
        if(!getPassword.equals(getConfirmedPassword)){
            etConfirmPassword.setError("Passwords do not match!");
            etConfirmPassword.requestFocus();
            return;
        }
        
        if(selectedGenderId == -1){
            Toast.makeText(getApplicationContext(),"Please select your gender",Toast.LENGTH_SHORT).show();
            return;
        }

        selectedGender = findViewById(selectedGenderId);
        gender = selectedGender.getText().toString().trim();
        
        // Show confirmation screen
        Intent confirmDetails = new Intent(getApplicationContext(), ConfirmDetails.class);
        confirmDetails.putExtra("full_name", getFullName);
        confirmDetails.putExtra("email", getEmail);
        confirmDetails.putExtra("phone", getPhone);
        confirmDetails.putExtra("gender", gender);
        confirmDetails.putExtra("password", getPassword);
        startActivity(confirmDetails);
    }
}