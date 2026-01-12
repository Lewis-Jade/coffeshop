package com.example.coffeecafe;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.coffeecafe.utils.SessionManager;

public class HomeFragment extends Fragment {
    private TextView tvWelcome, tvEmail;
    private Button btnLogout;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        sessionManager = SessionManager.getInstance(requireContext());
        
        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvEmail = view.findViewById(R.id.tv_user_email);
        btnLogout = view.findViewById(R.id.btn_logout);
        
        // Display user info
        String email = sessionManager.getUserEmail();
        if (email != null) {
            tvWelcome.setText("Welcome!");
            tvEmail.setText(email);
        }
        
        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
        
        return view;
    }
}