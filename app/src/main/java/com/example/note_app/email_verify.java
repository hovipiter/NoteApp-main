package com.example.note_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

public class email_verify extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    Button btn_reset;
    TextInputEditText tv_email;
    String user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);

        tv_email = findViewById(R.id.input_email);
        btn_reset = findViewById(R.id.btn_verify_to_reset);


        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_email = tv_email.getText().toString().trim();
                if (TextUtils.isEmpty(user_email)) {
                    Toast.makeText(getApplicationContext(), "Enter your email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                handling_email_verify(user_email);
            }
        });

    }

    private void handling_email_verify(String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(email_verify.this, "Reset link had sent to your email", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(email_verify.this, log_in.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(email_verify.this, "Fail to send reset link! Try again", Toast.LENGTH_LONG).show();
                    }
                });
    }
}