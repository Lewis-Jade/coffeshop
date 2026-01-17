package com.example.coffeecafe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ConfirmDetails extends AppCompatActivity {
    TextView tvFullName, tvEmail, tvPhone, tvGender;
    String getFullName, getEmail, getPhone, getGender;
    private Button btnCancel, btnConfirm;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_details);
        SystemHelper systemHelper = new SystemHelper(this);
        systemHelper.setSystemBars(R.color.gender, R.color.gender, false);

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

        tvFullName.setText(String.format("%s", getFullName));
        tvEmail.setText(String.format("%s", getEmail));
        tvPhone.setText(String.format("%s", getPhone));
        tvGender.setText(String.format("%s", getGender));
        setBtnCancel();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmUser();
            }
        });

    }
/////// cancel btn
private void setBtnCancel() {
    btnCancel.setOnClickListener(view -> {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent backToSignup = new Intent(getApplicationContext(), SignupActivity.class);

        // Pass back all current field values
        backToSignup.putExtra("full_name", getIntent().getStringExtra("full_name"));
        backToSignup.putExtra("email", getIntent().getStringExtra("email"));
        backToSignup.putExtra("phone", getIntent().getStringExtra("phone"));
        backToSignup.putExtra("gender", getIntent().getStringExtra("gender"));

        if (firebaseUser != null) {
            firebaseUser.delete().addOnCompleteListener(task -> {
                FirebaseAuth.getInstance().signOut();
                backToSignup.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(backToSignup);
                finish();
            });
        } else {
            backToSignup.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(backToSignup);
            finish();
        }

    });
}


    //creating a user account
    private void confirmUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return; // safety check

        String uid = user.getUid();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        String phoneNumber = getPhone;
        String fullName = getFullName;
        String email = getEmail;

        // Check if phone number already exists
        reference.orderByChild("phone").equalTo(phoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(ConfirmDetails.this,
                                    "Phone number already registered!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Phone is unique → save user data
                            Map<String, Object> userdata = new HashMap<>();
                            userdata.put("fullName", fullName);
                            userdata.put("email", email);
                            userdata.put("phone", phoneNumber);

                            reference.child(uid).setValue(userdata)
                                    .addOnSuccessListener(aVoid -> {
                                        // Send email verification
                                        sendEmailVerification(user);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(ConfirmDetails.this,
                                                "Failed to save your details!",
                                                Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ConfirmDetails.this,
                                "Database error!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to send verification email and show success popup
    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Show success popup
                        showSuccessDialog();
                    } else {
                        Toast.makeText(ConfirmDetails.this,
                                "Failed to send verification email.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Beautiful popup with button to go to LoginActivity
    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmDetails.this);
        builder.setTitle("🎉 Account Created")
                .setMessage("Your account was successfully created! A verification email has been sent. Please check your inbox.")
                .setCancelable(false)
                .setPositiveButton("Go to Login", (dialog, which) -> {
                    startActivity(new Intent(ConfirmDetails.this, LoginActivity.class));
                    finish();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}