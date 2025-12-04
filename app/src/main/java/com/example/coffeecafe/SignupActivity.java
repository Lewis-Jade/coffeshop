package com.example.coffeecafe;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignupActivity extends AppCompatActivity {
    private EditText etFullName,etEmail,etPhone,etPassword, etConfirmPassword;
    private String getFullName,getEmail,getPhone,getPassword,getConfirmedPassword,gender;
    private RadioGroup radioGroup;
    private RadioButton radioMale,radioFemale,selectedGender;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

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


        //validate if the edittext is empty
        getFullName = etFullName.getText().toString().trim();
        getEmail = etEmail.getText().toString().trim();
        getPhone = etPhone.getText().toString().trim();
        getPassword = etPassword.getText().toString().trim();
        getConfirmedPassword = etConfirmPassword.getText().toString().trim();

        int selectedGenderId = radioGroup.getCheckedRadioButtonId();



       registerUser.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               if(getFullName.isEmpty() || getEmail.isEmpty() || getPhone.isEmpty() || getPassword.isEmpty() || getConfirmedPassword.isEmpty() || selectedGenderId == -1){
                   Toast.makeText(getApplicationContext(),"All fields must be filled!",Toast.LENGTH_SHORT).show();
                   return;
               }
           if(!getPassword.equals(getConfirmedPassword)){
               etConfirmPassword.setError("Passwords do not match!");
               return;
           }

               selectedGender = findViewById(selectedGenderId);
               gender = selectedGender.toString().trim();
           //Pass data to fragment

               ConfirmDetailsFragment confirmDetailsFragment = new ConfirmDetailsFragment();
               Bundle bundle = new Bundle();
               bundle.putString("full_name", getFullName);
               bundle.putString("email",getEmail);
               bundle.putString("phone",getPhone);
               bundle.putString("gender",gender);
               confirmDetailsFragment.setArguments(bundle);


               getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,confirmDetailsFragment).addToBackStack(null).commit();

           }
       });





    }
}