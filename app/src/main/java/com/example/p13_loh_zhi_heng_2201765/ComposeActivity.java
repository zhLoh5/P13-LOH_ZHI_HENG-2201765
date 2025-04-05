package com.example.p13_loh_zhi_heng_2201765;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComposeActivity extends AppCompatActivity {

    private TextView targetNumberText, currentSumText, questionProgressText;
    private GridLayout numberGrid;
    private Button submitButton, resetButton;
    private int targetNumber;
    private List<Integer> selectedNumbers;
    private int questionCount = 0;
    private final int TOTAL_QUESTIONS = 6;
    private int score = 0;
    private final int BUTTON_COUNT = 6;
    private List<Button> numberButtons;
    private MediaPlayer mediaPlayer;
    private boolean isMuted = false;
    private Button muteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        targetNumberText = findViewById(R.id.target_number);
        currentSumText = findViewById(R.id.current_sum);
        questionProgressText = findViewById(R.id.question_progress);
        numberGrid = findViewById(R.id.number_grid);
        submitButton = findViewById(R.id.submit_button);
        resetButton = findViewById(R.id.reset_button);
        muteButton = findViewById(R.id.mute_button);

        numberButtons = new ArrayList<>();
        selectedNumbers = new ArrayList<>();
        showGameRules();
        generateNumberButtons();
        submitButton.setOnClickListener(v -> checkAnswer());
        resetButton.setOnClickListener(v -> resetSelection());

        startNewQuestion();


        mediaPlayer = MediaPlayer.create(this, R.raw.bg3);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        muteButton.setOnClickListener(v -> toggleMusic());


        Button homeButton = findViewById(R.id.home_button);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ComposeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void toggleMusic() {
        if (isMuted) {
            mediaPlayer.start();
            muteButton.setText("🔊");
        } else {
            mediaPlayer.pause();
            muteButton.setText("🔇");
        }
        isMuted = !isMuted;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && !isMuted) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void showGameRules() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View customView = getLayoutInflater().inflate(R.layout.dialog_game_rules3, null);

        builder.setView(customView);

        ImageView imageView = customView.findViewById(R.id.rules_image);
        TextView textView = customView.findViewById(R.id.rules_text);
        Button startButton = customView.findViewById(R.id.start_button);

        AlertDialog dialog = builder.create();

        startButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void generateNumberButtons() {
        numberGrid.removeAllViews();
        for (int i = 0; i < BUTTON_COUNT; i++) {
            Button button = new Button(this);
            button.setLayoutParams(new GridLayout.LayoutParams());
            button.setTextSize(18);
            button.setOnClickListener(v -> selectNumber(button));
            numberGrid.addView(button);
            numberButtons.add(button);
        }
    }

    private void startNewQuestion() {
        if (questionCount >= TOTAL_QUESTIONS) {
            showFinalDialog();
            return;
        }

        questionCount++;
        selectedNumbers.clear();
        currentSumText.setText("Current Sum: 0");
        questionProgressText.setText("Question: " + questionCount + "/" + TOTAL_QUESTIONS);

        Random random = new Random();
        targetNumber = random.nextInt(50) + 10;
        targetNumberText.setText(String.valueOf(targetNumber));

        List<Integer> availableNumbers = new ArrayList<>();


        int num1 = random.nextInt(targetNumber / 2) + 1; // 选一个随机数
        int num2 = targetNumber - num1; // 计算另一个数
        availableNumbers.add(num1);
        availableNumbers.add(num2);


        while (availableNumbers.size() < BUTTON_COUNT) {
            int num = random.nextInt(20) + 1;
            if (!availableNumbers.contains(num)) {
                availableNumbers.add(num);
            }
        }


        java.util.Collections.shuffle(availableNumbers);

        for (int i = 0; i < BUTTON_COUNT; i++) {
            Button button = numberButtons.get(i);
            int num = availableNumbers.get(i);
            button.setText(String.valueOf(num));
            button.setEnabled(true);
        }
    }

    private void selectNumber(Button button) {
        int num = Integer.parseInt(button.getText().toString());
        selectedNumbers.add(num);
        button.setEnabled(false);
        updateCurrentSum();
    }

    private void updateCurrentSum() {
        int sum = 0;
        for (int num : selectedNumbers) {
            sum += num;
        }
        currentSumText.setText("Current Sum: " + sum);
    }

    private void checkAnswer() {
        int sum = 0;
        for (int num : selectedNumbers) {
            sum += num;
        }

        if (sum == targetNumber) {
            score++;
            showFeedbackDialog("Correct!", "Correct! Well done!", true);
        } else {
            showFeedbackDialog("Incorrect!", "Incorrect! Please Try again!", false);
        }
    }

    private void showFeedbackDialog(String title, String message, boolean correct) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_feedback2, null);
        builder.setView(dialogView);

        ImageView feedbackImage = dialogView.findViewById(R.id.feedback_image);
        TextView feedbackText = dialogView.findViewById(R.id.feedback_text);
        feedbackText.setText(message);

        if (correct) {
            feedbackImage.setImageResource(R.drawable.correct1);
        } else {
            feedbackImage.setImageResource(R.drawable.incorrect1);
        }

        builder.setPositiveButton("GO", (dialog, which) -> {
            if (correct) {
                startNewQuestion();
            } else {
                resetSelection();
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void resetSelection() {
        selectedNumbers.clear();
        currentSumText.setText("Current Sum: 0");
        for (Button button : numberButtons) {
            button.setEnabled(true);
        }
    }

    private void showFinalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_final_result3, null);
        builder.setView(dialogView);

        ImageView resultImage = dialogView.findViewById(R.id.final_image);
        TextView resultText = dialogView.findViewById(R.id.final_text);
        resultImage.setImageResource(R.drawable.finall);

        builder.setPositiveButton("Retry", (dialog, which) -> restartGame());
        builder.setNegativeButton("Home", (dialog, which) -> finish());
        builder.setCancelable(false);
        builder.show();
    }

    private void restartGame() {
        questionCount = 0;
        score = 0;
        startNewQuestion();
    }
}
