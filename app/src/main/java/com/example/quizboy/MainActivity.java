package com.example.quizboy;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth fireAuth;
    private Button btnLogout;
    private TextView tvWelcome;
    private RecyclerView rvCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        fireAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fireAuth.getCurrentUser();

        // If no user is signed in, redirect to login
        if (currentUser == null) {
            redirectToLogin();
            return;
        }

        // Initialize UI elements
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnLogout);
        rvCategories = findViewById(R.id.rvCategories);

        // Set welcome message
        String email = currentUser.getEmail();
        String username = email != null ? email.split("@")[0] : "Student";
        tvWelcome.setText("Welcome, " + username + "!");

        // Setup logout button
        btnLogout.setOnClickListener(view -> {
            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        fireAuth.signOut();
                        Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                        redirectToLogin();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Setup quiz categories
        setupQuizCategories();

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupQuizCategories() {
        // Create list of quiz categories
        List<QuizCategory> categories = new ArrayList<>();
        categories.add(new QuizCategory("Addition", "Basic addition problems", R.drawable.ic_addition));
        categories.add(new QuizCategory("Subtraction", "Basic subtraction problems", R.drawable.ic_subtraction));
        categories.add(new QuizCategory("Multiplication", "Basic multiplication problems", R.drawable.ic_multiplication));
        categories.add(new QuizCategory("Division", "Basic division problems", R.drawable.ic_division));
        categories.add(new QuizCategory("Mixed", "Mixed operations", R.drawable.ic_mixed));
        categories.add(new QuizCategory("Word Problems", "Solve word problems", R.drawable.ic_word_problems));

        // Setup RecyclerView with grid layout (2 columns)
        rvCategories.setLayoutManager(new GridLayoutManager(this, 2));

        // Create and set adapter
        CategoryAdapter adapter = new CategoryAdapter(categories, position -> {
            QuizCategory category = categories.get(position);
            startQuiz(category);
        });

        rvCategories.setAdapter(adapter);
    }

    private void startQuiz(QuizCategory category) {
        Toast.makeText(this, "Starting " + category.getName() + " quiz", Toast.LENGTH_SHORT).show();

        // Here you would normally start the quiz activity
        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
        intent.putExtra("CATEGORY", category.getName());
        startActivity(intent);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, LogAct.class);
        startActivity(intent);
        finish();
    }

    // Inner class for quiz categories data model
    public static class QuizCategory {
        private String name;
        private String description;
        private int imageResourceId;

        public QuizCategory(String name, String description, int imageResourceId) {
            this.name = name;
            this.description = description;
            this.imageResourceId = imageResourceId;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public int getImageResourceId() {
            return imageResourceId;
        }
    }

    // Interface for item click handling
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // RecyclerView adapter for categories
    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
        private List<QuizCategory> categories;
        private OnItemClickListener listener;

        public CategoryAdapter(List<QuizCategory> categories, OnItemClickListener listener) {
            this.categories = categories;
            this.listener = listener;
        }

        @Override
        public CategoryViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_category, parent, false);
            return new CategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CategoryViewHolder holder, int position) {
            QuizCategory category = categories.get(position);
            holder.tvCategoryName.setText(category.getName());
            holder.tvCategoryDescription.setText(category.getDescription());
            holder.ivCategoryImage.setImageResource(category.getImageResourceId());
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        public class CategoryViewHolder extends RecyclerView.ViewHolder {
            TextView tvCategoryName;
            TextView tvCategoryDescription;
            android.widget.ImageView ivCategoryImage;

            public CategoryViewHolder(View itemView) {
                super(itemView);
                tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
                tvCategoryDescription = itemView.findViewById(R.id.tvCategoryDescription);
                ivCategoryImage = itemView.findViewById(R.id.ivCategoryImage);

                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                });
            }
        }
    }
}