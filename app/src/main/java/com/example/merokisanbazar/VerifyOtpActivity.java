package com.example.merokisanbazar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyOtpActivity extends AppCompatActivity {

    private TextView number, resend;
    private EditText code1, code2, code3, code4, code5, code6;
    private Button verify;


    String verificationId, myNumber;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //to remove the title feature from window
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //to hide the action bar
        Objects.requireNonNull(getSupportActionBar()).hide();
        //to make window fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_verify_otp);

        number = findViewById(R.id.number);
        code1 = findViewById(R.id.code1);
        code3 = findViewById(R.id.code3);
        code2 = findViewById(R.id.code2);
        code4 = findViewById(R.id.code4);
        code5 = findViewById(R.id.code5);
        code6 = findViewById(R.id.code6);
        verify = findViewById(R.id.btnVerify);
        resend = findViewById(R.id.resend);
        progressBar = findViewById(R.id.progressBar);

        code1.requestFocus();
        setUpOtpInputs();

        verificationId = getIntent().getExtras().getString("verificationId");
        myNumber = getIntent().getExtras().getString("number");
        number.setText(myNumber);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        myNumber,
                        60,
                        TimeUnit.SECONDS,   //once the code is send user cannot send otp for 60s
                        VerifyOtpActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull @NotNull PhoneAuthCredential phoneAuthCredential) {
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull @NotNull FirebaseException e) {
                                progressBar.setVisibility(View.VISIBLE);
                                Toast.makeText(VerifyOtpActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull @NotNull String newVerificationId, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(newVerificationId, forceResendingToken);
                                progressBar.setVisibility(View.VISIBLE);
                                verificationId = newVerificationId;
                                Toast.makeText(VerifyOtpActivity.this, "OTP code send", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });


        verify.setOnClickListener(v -> {

            if (code1.getText().toString().trim().isEmpty()) {
                code1.setError("Please enter the first digit of the code..");
                code1.requestFocus();
                return;
            }
            if (code2.getText().toString().trim().isEmpty()) {
                code2.setError("Please enter the second digit of the code..");
                code2.requestFocus();
                return;
            }
            if (code3.getText().toString().trim().isEmpty()) {
                code3.setError("Please enter the third digit of the code..");
                code3.requestFocus();
                return;
            }
            if (code4.getText().toString().trim().isEmpty()) {
                code4.setError("Please enter the fourth digit of the code..");
                code4.requestFocus();
                return;
            }
            if (code5.getText().toString().trim().isEmpty()) {
                code5.setError("Please enter the fifth digit of the code..");
                code5.requestFocus();
                return;
            }
            if (code6.getText().toString().trim().isEmpty()) {
                code6.setError("Please enter the sixth digit of the code..");
                code6.requestFocus();
                return;
            }

            verifyCode();
        });

    }

    private void verifyCode() {
        String code = code1.getText().toString() +
                code2.getText().toString() +
                code3.getText().toString() +
                code4.getText().toString() +
                code5.getText().toString() +
                code6.getText().toString();

        if (verificationId != null) {
            progressBar.setVisibility(View.VISIBLE);
            verify.setVisibility(View.INVISIBLE);
            PhoneAuthCredential phoneAuthProvider = PhoneAuthProvider.getCredential(
                    verificationId,
                    code
            );
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthProvider)
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                    })
                    .addOnCompleteListener(task -> {
                        verify.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
        }
    }

    private void setUpOtpInputs() {
        code1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    code2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    code3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    code4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    code5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    code6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}