package com.example.coffeecafe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ConfirmDetails extends AppCompatActivity {
 TextView tvFullName,tvEmail,tvPhone,tvGender;
 String getFullName,getEmail,getPhone,getGender;
 Button btnCancel;
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_details);
        SystemHelper systemHelper = new SystemHelper(this);
        systemHelper.setSystemBars(R.color.gender,R.color.gender,false);

        tvFullName = findViewById(R.id.tv_fullname);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        tvGender = findViewById(R.id.tv_gender);
       btnCancel = findViewById(R.id.bv_cancel);
        Intent getUserDetails = getIntent();
        getFullName = getUserDetails.getStringExtra("full_name");
        getEmail = getUserDetails.getStringExtra("email");
        getPhone = getUserDetails.getStringExtra("phone");
        getGender = getUserDetails.getStringExtra("gender");

        tvFullName.setText(String.format("%s",getFullName));
        tvEmail.setText(String.format("%s",getEmail));
        tvPhone.setText(String.format("%s",getPhone));
        tvGender.setText(String.format("%s",getGender));
        setBtnCancel();

    }

    public  void setBtnCancel(){
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}