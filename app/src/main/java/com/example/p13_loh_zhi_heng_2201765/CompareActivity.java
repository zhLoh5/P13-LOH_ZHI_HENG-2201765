package com.example.p13_loh_zhi_heng_2201765;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.LayoutInflater;
import android.content.Intent;

import java.util.Random;

public class CompareActivity extends AppCompatActivity {

    private TextView number_1;
    private TextView number_2;
    private TextView questionCountText;
    private Button muteButton;
    private int number1;
    private int number2;
    private int questionCount = 0;
    private int correctCount = 0;
    private final int TOTAL_QUESTIONS = 15;
    private MediaPlayer mediaPlayer;
    private boolean isMuted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        number_1 = findViewById(R.id.number_1);
        number_2 = findViewById(R.id.number_2);
        questionCountText = findViewById(R.id.question_count);
        Button greater_button = findViewById(R.id.greater_button);
        Button less_button = findViewById(R.id.less_button);
        Button equal_button = findViewById(R.id.equal_button);
        muteButton = findViewById(R.id.mute_button);

        showGameRules();
        generateNumbers();

        greater_button.setOnClickListener(v -> checkComparison("greater"));
        less_button.setOnClickListener(v -> checkComparison("less"));
        equal_button.setOnClickListener(v -> checkComparison("equal"));

        mediaPlayer = MediaPlayer.create(this, R.raw.bg1);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        muteButton.setOnClickListener(v -> toggleMusic());

        Button homeButton = findViewById(R.id.home_button);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(CompareActivity.this, MainActivity.class);
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

        View customView = getLayoutInflater().inflate(R.layout.dialog_game_rules, null);

        builder.setView(customView);

        ImageView imageView = customView.findViewById(R.id.rules_image);
        TextView textView = customView.findViewById(R.id.rules_text);
        Button startButton = customView.findViewById(R.id.start_button);

        AlertDialog dialog = builder.create();

        startButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    private void generateNumbers() {
        if (questionCount >= TOTAL_QUESTIONS) {
            showFinalResult();
            return;
        }
        questionCount++;

        Random random = new Random();
        number1 = random.nextInt(100);
        number2 = random.nextInt(100);

        number_1.setText(String.valueOf(number1));
        number_2.setText(String.valueOf(number2));
        questionCountText.setText("Progress: " + questionCount + "/" + TOTAL_QUESTIONS);
    }

    private void checkComparison(String comparison) {
        boolean isCorrect;
        int imageResId;
        String message;

        if (comparison.equals("greater")) {
            isCorrect = number1 > number2;
        } else if (comparison.equals("less")) {
            isCorrect = number1 < number2;
        } else {
            isCorrect = number1 == number2;
        }

        if (isCorrect) {
            correctCount++;
            message = "✅ Nice, Keep Going！";
            imageResId = R.drawable.meteors3;
        } else {
            message = "❌ Ouch, that's not right!";
            imageResId = R.drawable.meteors1;
        }

        showFeedbackDialog(message, imageResId);
    }

    private void showFeedbackDialog(String message, int imageResId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_feedback, null);

        TextView textView = customView.findViewById(R.id.feedback_text);
        ImageView imageView = customView.findViewById(R.id.feedback_image);

        textView.setText(message);
        imageView.setImageResource(imageResId);

        AlertDialog dialog = builder.setView(customView).setCancelable(false).create();

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (d, which) -> {
            if (questionCount < TOTAL_QUESTIONS) {
                generateNumbers();
            } else {
                showFinalResult();
            }
            d.dismiss();
        });

        dialog.show();
    }

    private void showFinalResult() {
        String title, message;
        int imageResId;

        if (correctCount == TOTAL_QUESTIONS) {
            title = "🎉 Perfection landing！";
            message = "You dodged them all, great！";
            imageResId = R.drawable.success;
        } else {
            title = "Successful landing!";
            message = "You dodged " + correctCount + " / " + TOTAL_QUESTIONS + " Keep going it！";
            imageResId = R.drawable.success2;
        }

        showFinalResultDialog(title, message, imageResId);
    }

    private void showFinalResultDialog(String title, String message, int imageResId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_final_result, null);

        TextView titleView = customView.findViewById(R.id.result_title);
        TextView messageView = customView.findViewById(R.id.result_message);
        ImageView imageView = customView.findViewById(R.id.result_image);
        Button restartButton = customView.findViewById(R.id.restart_button);
        Button homeButton = customView.findViewById(R.id.home_button);

        titleView.setText(title);
        messageView.setText(message);
        imageView.setImageResource(imageResId);

        AlertDialog dialog = builder.setView(customView).setCancelable(false).create();

        restartButton.setOnClickListener(v -> {
            dialog.dismiss();
            restartGame();
        });

        homeButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }

    private void restartGame() {
        questionCount = 0;
        correctCount = 0;
        generateNumbers();
    }
}
