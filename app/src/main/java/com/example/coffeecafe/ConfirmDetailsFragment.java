package com.example.coffeecafe;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ConfirmDetailsFragment extends Fragment {
TextView tvFullName,tvEmail,tvPhone,tvGender;
String getFullName,getEmail,getPhone,getGender;
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvFullName = view.findViewById(R.id.tv_fullname);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.ev_phone);
        tvGender = view.findViewById(R.id.tv_gender);
        // receive data from SignupActivity
        Bundle bundle = getArguments();

        if(bundle != null){
            getFullName = bundle.getString("full_name");
            getEmail = bundle.getString("email");
            getPhone = bundle.getString("phone");
            getGender = bundle.getString("gender");
            tvFullName.setText("Name:  "+getFullName);
            tvEmail.setText("Email: "+getEmail);
            tvPhone.setText("Phone: "+getPhone);
            tvGender.setText("gender: "+getGender);
        }


    }
}