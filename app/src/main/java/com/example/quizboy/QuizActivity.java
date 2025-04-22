package com.example.quizboy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {
    private TextView tvQuestionNumber;
    private TextView tvQuestion;
    private RadioGroup rgOptions;
    private RadioButton rbOption1;
    private RadioButton rbOption2;
    private RadioButton rbOption3;
    private RadioButton rbOption4;
    private Button btnNext;

    private List<QuizQuestion> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);

        // Get category from intent
        category = getIntent().getStringExtra("CATEGORY");
        if (category == null) {
            category = "Mixed";
        }

        // Initialize UI elements
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestion = findViewById(R.id.tvQuestion);
        rgOptions = findViewById(R.id.rgOptions);
        rbOption1 = findViewById(R.id.rbOption1);
        rbOption2 = findViewById(R.id.rbOption2);
        rbOption3 = findViewById(R.id.rbOption3);
        rbOption4 = findViewById(R.id.rbOption4);
        btnNext = findViewById(R.id.btnNext);

        // Generate questions based on category
        generateQuestions();

        // Display first question
        displayQuestion(currentQuestionIndex);

        // Set button click listener
        btnNext.setOnClickListener(view -> {
            // Check if an option is selected
            if (rgOptions.getCheckedRadioButtonId() == -1) {
                Toast.makeText(QuizActivity.this, "Please select an answer", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if answer is correct
            RadioButton selectedOption = findViewById(rgOptions.getCheckedRadioButtonId());
            String selectedAnswer = selectedOption.getText().toString();
            QuizQuestion currentQuestion = questions.get(currentQuestionIndex);

            if (selectedAnswer.equals(String.valueOf(currentQuestion.getCorrectAnswer()))) {
                score++;
            }

            // Move to next question or end quiz
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                displayQuestion(currentQuestionIndex);
            } else {
                showResults();
            }
        });

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void generateQuestions() {
        questions = new ArrayList<>();
        Random random = new Random();

        // Generate 10 questions based on the category
        for (int i = 0; i < 10; i++) {
            int num1, num2, answer;
            String operator;
            List<Integer> options = new ArrayList<>();

            switch (category) {
                case "Addition":
                    num1 = random.nextInt(20) + 1;
                    num2 = random.nextInt(20) + 1;
                    answer = num1 + num2;
                    operator = "+";
                    break;

                case "Subtraction":
                    num1 = random.nextInt(20) + 10;
                    num2 = random.nextInt(num1);
                    answer = num1 - num2;
                    operator = "-";
                    break;

                case "Multiplication":
                    num1 = random.nextInt(10) + 1;
                    num2 = random.nextInt(10) + 1;
                    answer = num1 * num2;
                    operator = "×";
                    break;

                case "Division":
                    num2 = random.nextInt(10) + 1;
                    answer = random.nextInt(10) + 1;
                    num1 = num2 * answer;
                    operator = "÷";
                    break;

                case "Word Problems":
                    questions = generateWordProblems();
                    return;

                default: // Mixed
                    int op = random.nextInt(4);
                    if (op == 0) {
                        num1 = random.nextInt(20) + 1;
                        num2 = random.nextInt(20) + 1;
                        answer = num1 + num2;
                        operator = "+";
                    } else if (op == 1) {
                        num1 = random.nextInt(20) + 10;
                        num2 = random.nextInt(num1);
                        answer = num1 - num2;
                        operator = "-";
                    } else if (op == 2) {
                        num1 = random.nextInt(10) + 1;
                        num2 = random.nextInt(10) + 1;
                        answer = num1 * num2;
                        operator = "×";
                    } else {
                        num2 = random.nextInt(10) + 1;
                        answer = random.nextInt(10) + 1;
                        num1 = num2 * answer;
                        operator = "÷";
                    }
                    break;
            }

            // Create options (including the correct answer)
            options.add(answer);

            // Add 3 more unique wrong options
            while (options.size() < 4) {
                int wrongAnswer;
                if (category.equals("Division") || operator.equals("÷")) {
                    wrongAnswer = random.nextInt(15) + 1;
                } else {
                    wrongAnswer = Math.max(1, answer + (random.nextInt(11) - 5));
                }

                if (!options.contains(wrongAnswer)) {
                    options.add(wrongAnswer);
                }
            }

            // Shuffle options
            Collections.shuffle(options);

            // Create question text
            String questionText = num1 + " " + operator + " " + num2 + " = ?";

            // Add question to list
            questions.add(new QuizQuestion(questionText, answer, options));
        }
    }

    private List<QuizQuestion> generateWordProblems() {
        List<QuizQuestion> wordProblems = new ArrayList<>();
        Random random = new Random();

        String[] additionTemplates = {
                "John has %d apples. Sarah gives him %d more apples. How many apples does John have now?",
                "There are %d red balls and %d blue balls in a box. How many balls are there in total?",
                "You have %d stickers. Your friend gives you %d more. How many stickers do you have now?"
        };

        String[] subtractionTemplates = {
                "Mary has %d candies. She gives %d candies to her friend. How many candies does she have left?",
                "There are %d students in a class. %d students are absent today. How many students are present?",
                "You have %d dollars. You spend %d dollars on a toy. How much money do you have left?"
        };

        String[] multiplicationTemplates = {
                "Each box has %d pencils. If you have %d boxes, how many pencils do you have in total?",
                "A teacher gives %d stickers to each of her %d students. How many stickers does she give out in total?",
                "If each row has %d chairs and there are %d rows, how many chairs are there in total?"
        };

        String[] divisionTemplates = {
                "You have %d candies and want to share them equally among %d friends. How many candies does each friend get?",
                "There are %d students in a class. The teacher wants to divide them into %d equal groups. How many students will be in each group?",
                "You have %d dollars and each toy costs %d dollars. How many toys can you buy?"
        };

        for (int i = 0; i < 10; i++) {
            int type = random.nextInt(4);
            int num1, num2, answer;
            String question;
            List<Integer> options = new ArrayList<>();

            switch (type) {
                case 0: // Addition
                    num1 = random.nextInt(20) + 1;
                    num2 = random.nextInt(20) + 1;
                    answer = num1 + num2;
                    question = String.format(
                            additionTemplates[random.nextInt(additionTemplates.length)],
                            num1, num2
                    );
                    break;

                case 1: // Subtraction
                    num1 = random.nextInt(20) + 10;
                    num2 = random.nextInt(num1);
                    answer = num1 - num2;
                    question = String.format(
                            subtractionTemplates[random.nextInt(subtractionTemplates.length)],
                            num1, num2
                    );
                    break;

                case 2: // Multiplication
                    num1 = random.nextInt(10) + 1;
                    num2 = random.nextInt(10) + 1;
                    answer = num1 * num2;
                    question = String.format(
                            multiplicationTemplates[random.nextInt(multiplicationTemplates.length)],
                            num1, num2
                    );
                    break;

                default: // Division (ensure clean division)
                    num2 = random.nextInt(10) + 1;
                    answer = random.nextInt(10) + 1;
                    num1 = num2 * answer;
                    question = String.format(
                            divisionTemplates[random.nextInt(divisionTemplates.length)],
                            num1, num2
                    );
                    break;
            }

            // Create options (including the correct answer)
            options.add(answer);

            // Add 3 more unique wrong options
            while (options.size() < 4) {
                int wrongAnswer = Math.max(1, answer + (random.nextInt(11) - 5));
                if (!options.contains(wrongAnswer)) {
                    options.add(wrongAnswer);
                }
            }

            // Shuffle options
            Collections.shuffle(options);

            // Add question to list
            wordProblems.add(new QuizQuestion(question, answer, options));
        }

        return wordProblems;
    }

    private void displayQuestion(int index) {
        QuizQuestion question = questions.get(index);

        // Update question number
        tvQuestionNumber.setText("Question " + (index + 1) + " of " + questions.size());

        // Set question text
        tvQuestion.setText(question.getQuestionText());

        // Clear previous selection
        rgOptions.clearCheck();

        // Set options
        List<Integer> options = question.getOptions();
        rbOption1.setText(String.valueOf(options.get(0)));
        rbOption2.setText(String.valueOf(options.get(1)));
        rbOption3.setText(String.valueOf(options.get(2)));
        rbOption4.setText(String.valueOf(options.get(3)));

        // Update button text for the last question
        if (index == questions.size() - 1) {
            btnNext.setText("Finish");
        }
    }

    private void showResults() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quiz Results");

        // Calculate percentage
        int percentage = (score * 100) / questions.size();
        String message = "You scored " + score + " out of " + questions.size() + "\n";
        message += "Percentage: " + percentage + "%\n\n";

        // Add feedback based on score
        if (percentage >= 90) {
            message += "Excellent! You're a math genius!";
        } else if (percentage >= 70) {
            message += "Great job! Keep practicing!";
        } else if (percentage >= 50) {
            message += "Good effort! Try again to improve your score.";
        } else {
            message += "Keep practicing and you'll get better!";
        }

        builder.setMessage(message);
        builder.setPositiveButton("Back to Categories", (dialog, which) -> {
            finish();
        });
        builder.setNegativeButton("Try Again", (dialog, which) -> {
            // Reset quiz and start over
            currentQuestionIndex = 0;
            score = 0;
            generateQuestions();
            displayQuestion(currentQuestionIndex);
        });
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Quit Quiz")
                .setMessage("Are you sure you want to quit this quiz? Your progress will be lost.")
                .setPositiveButton("Quit", (dialog, which) -> {
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Inner class for quiz questions
    public static class QuizQuestion {
        private String questionText;
        private int correctAnswer;
        private List<Integer> options;

        public QuizQuestion(String questionText, int correctAnswer, List<Integer> options) {
            this.questionText = questionText;
            this.correctAnswer = correctAnswer;
            this.options = options;
        }

        public String getQuestionText() {
            return questionText;
        }

        public int getCorrectAnswer() {
            return correctAnswer;
        }

        public List<Integer> getOptions() {
            return options;
        }
    }
}