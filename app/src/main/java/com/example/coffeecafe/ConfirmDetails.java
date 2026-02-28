package com.example.coffeecafe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

        tvFullName.setText(getFullName);
        tvEmail.setText(getEmail);
        tvPhone.setText(getPhone);
        tvGender.setText(getGender);
        
        setBtnCancel();

        btnConfirm.setOnClickListener(view -> confirmUser());
    }

    private void setBtnCancel() {
        btnCancel.setOnClickListener(view -> {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("full_name", getFullName);
            resultIntent.putExtra("email", getEmail);
            resultIntent.putExtra("phone", getPhone);
            resultIntent.putExtra("gender", getGender);

            if (firebaseUser != null) {
                firebaseUser.delete().addOnCompleteListener(task -> {
                    FirebaseAuth.getInstance().signOut();
                    setResult(RESULT_CANCELED, resultIntent);
                    finish();
                });
            } else {
                setResult(RESULT_CANCELED, resultIntent);
                finish();
            }
        });
    }

    private void confirmUser() {
        btnConfirm.setEnabled(false);
        btnConfirm.setText("Saving...");
        btnConfirm.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.ic_plus));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Session expired. Please try again.", Toast.LENGTH_SHORT).show();
            btnConfirm.setEnabled(true);
            btnConfirm.setText("Confirm");
            return;
        }

        String uid = user.getUid();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        // Use a single listener to check for phone existence AND save
        reference.orderByChild("phone").equalTo(getPhone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Check if the existing phone belongs to the CURRENT user (re-registration)
                            boolean isMine = false;
                            for (DataSnapshot child : snapshot.getChildren()) {
                                if (child.getKey().equals(uid)) {
                                    isMine = true;
                                    break;
                                }
                            }
                            
                            if (!isMine) {
                                Toast.makeText(ConfirmDetails.this, "Phone number already registered!", Toast.LENGTH_SHORT).show();
                                btnConfirm.setEnabled(true);
                                btnConfirm.setText("Confirm");
                                return;
                            }
                        }
                        
                        // Proceed to save
                        saveUserData(user, uid);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        btnConfirm.setEnabled(true);
                        btnConfirm.setText("Confirm");
                        Toast.makeText(ConfirmDetails.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(FirebaseUser user, String uid) {
        Map<String, Object> userdata = new HashMap<>();
        userdata.put("fullName", getFullName);
        userdata.put("email", getEmail);
        userdata.put("phone", getPhone);
        userdata.put("gender", getGender);

        reference.child(uid).setValue(userdata)
                .addOnSuccessListener(aVoid -> sendEmailVerification(user))
                .addOnFailureListener(e -> {
                    btnConfirm.setEnabled(true);
                    btnConfirm.setText("Confirm");
                    Toast.makeText(ConfirmDetails.this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showSuccessDialog();
                    } else {
                        btnConfirm.setEnabled(true);
                        btnConfirm.setText("Confirm");
                        Toast.makeText(ConfirmDetails.this, "Email error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(ConfirmDetails.this)
                .setTitle("🎉 Account Created")
                .setMessage("Your account was successfully created! A verification email has been sent to " + getEmail + ". Please check your inbox and SPAM folder.")
                .setCancelable(false)
                .setPositiveButton("Go to Login", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(ConfirmDetails.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .show();
    }
}