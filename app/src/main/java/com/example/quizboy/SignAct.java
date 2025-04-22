package com.example.quizboy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.activity.EdgeToEdge;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignAct extends AppCompatActivity {
    private FirebaseAuth fireAuth;
    private EditText entEmailAddress;
    private EditText enPassword;
    private EditText rePassword;
    private Button btnSignUp;
    private TextView btsLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign);

        // Initialize UI elements
        entEmailAddress = findViewById(R.id.entEmailAddress);
        enPassword = findViewById(R.id.enPassword);
        rePassword = findViewById(R.id.rePassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btsLogin = findViewById(R.id.btsLogin);

        fireAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUser();
            }
        });

        btsLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignAct.this, LogAct.class);
                startActivity(intent);
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void signUser() {
        String email = entEmailAddress.getText().toString();
        String password = enPassword.getText().toString();
        String confPassword = rePassword.getText().toString();

        if (email.trim().isEmpty() || password.trim().isEmpty() || confPassword.trim().isEmpty()) {
            Toast.makeText(this, "Error: Email & Password must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confPassword)) {
            Toast.makeText(this, "Error: Password and Re-Password do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        fireAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignAct.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Error creating user", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}