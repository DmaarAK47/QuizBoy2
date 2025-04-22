package com.example.quizboy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LogIntro extends AppCompatActivity {
    private Button btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_intro);

        btnGetStarted = findViewById(R.id.btnGetStarted);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Toast.makeText(this, "User is already Logged in", Toast.LENGTH_SHORT).show();
            // Add a slight delay before redirecting to prevent immediate closing
            findViewById(R.id.main).postDelayed(() -> {
                redirect("MAIN");
            }, 1500); // 1.5 seconds delay
        } else {
            // Only set up the click listener if user is not logged in
            btnGetStarted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    redirect("LOGIN");
                }
            });
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (@NonNull View v, @NonNull WindowInsetsCompat insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void redirect(String name) {
        Intent intent;

        if (name.equals("LOGIN")) {
            intent = new Intent(this, LogAct.class);
        } else if (name.equals("MAIN")) {
            intent = new Intent(this, MainActivity.class);
        } else {
            throw new RuntimeException("No Path Exists");
        }

        startActivity(intent);
        finish();
    }
}