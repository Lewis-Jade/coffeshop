package com.example.coffeecafe;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
private EditText edtResetEmail;
private Button resetBtn;
private FrameLayout loadingOverlay;
private FirebaseAuth firebaseAuth;
private String getEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        SystemHelper systemHelper = new SystemHelper(this);
        systemHelper.setSystemBars(R.color.gender,R.color.gender,false);

        edtResetEmail = (EditText)findViewById(R.id.ev_email);
        resetBtn = (Button)findViewById(R.id.bv_resetPassword);
        loadingOverlay = (FrameLayout)findViewById(R.id.loadingOverlay);
         firebaseAuth = FirebaseAuth.getInstance();

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNewPassword();
            }
        });

    }

    private void setNewPassword() {
      getEmail = edtResetEmail.getText().toString().trim();

      if(getEmail.isEmpty()){
          edtResetEmail.setError("This field must be filled");
          return;
      }
      showLoading();
      resetBtn.setEnabled(false);
      firebaseAuth.sendPasswordResetEmail(getEmail)
              .addOnCompleteListener(task ->{
                  hideLoading();
                  resetBtn.setEnabled(true);
                  if(task.isSuccessful()){
                      Toast.makeText(getApplicationContext(),"Reset Link send to your email",Toast.LENGTH_LONG).show();
                      finish();
                  }else{
                      Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                  }
        });


    }


    /////////Spinner loading animation on login
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