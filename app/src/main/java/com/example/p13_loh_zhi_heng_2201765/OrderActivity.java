package com.example.p13_loh_zhi_heng_2201765;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import android.media.MediaPlayer;


public class OrderActivity extends AppCompatActivity {

    private LinearLayout numberContainer, dropContainer;
    private TextView questionText, progressText;
    private Button checkButton, resetButton;
    private List<Integer> numbers = new ArrayList<>();
    private List<TextView> droppedViews = new ArrayList<>();
    private int questionCount = 0;
    private final int TOTAL_QUESTIONS = 6;
    private boolean isAscending;
    private MediaPlayer bgMusic;
    private boolean isMuted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        numberContainer = findViewById(R.id.number_container);
        dropContainer = findViewById(R.id.drop_container);
        questionText = findViewById(R.id.question_text);
        progressText = findViewById(R.id.question_count);
        checkButton = findViewById(R.id.check_button);
        resetButton = findViewById(R.id.reset_button);

        checkButton.setOnClickListener(v -> checkAnswer());
        resetButton.setOnClickListener(v -> resetGame());

        startNewQuestion();
        showGameRules();

        bgMusic = MediaPlayer.create(this, R.raw.bg2);
        bgMusic.setLooping(true);
        bgMusic.start();


        Button muteButton = findViewById(R.id.mute_button);
        muteButton.setOnClickListener(v -> toggleMusic(muteButton));

        Button homeButton = findViewById(R.id.home_button);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrderActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void showGameRules() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);

        View customView = getLayoutInflater().inflate(R.layout.dialog_game_rules2, null);

        builder.setView(customView);

        ImageView imageView = customView.findViewById(R.id.rules_image);
        TextView textView = customView.findViewById(R.id.rules_text);
        Button startButton = customView.findViewById(R.id.start_button);

        androidx.appcompat.app.AlertDialog dialog = builder.create();

        startButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void toggleMusic(Button muteButton) {
        if (isMuted) {
            bgMusic.start();
            muteButton.setText("🔊");
        } else {
            bgMusic.pause();
            muteButton.setText("🔇");
        }
        isMuted = !isMuted;
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (bgMusic != null && bgMusic.isPlaying()) {
            bgMusic.pause();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (bgMusic != null && !isMuted) {
            bgMusic.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bgMusic != null) {
            bgMusic.release();
            bgMusic = null;
        }
    }

    private void startNewQuestion() {
        if (questionCount >= TOTAL_QUESTIONS) {
            showFinalResult();
            return;
        }

        questionCount++;
        progressText.setText("Quest: " + questionCount + "/" + TOTAL_QUESTIONS);
        numberContainer.removeAllViews();
        dropContainer.removeAllViews();
        droppedViews.clear();
        numbers.clear();

        Random random = new Random();
        int numCount = random.nextInt(2) + 4;
        isAscending = random.nextBoolean();
        questionText.setText("Arrange in " + (isAscending ? "Ascending" : "Descending") + " order");

        for (int i = 0; i < numCount; i++) {
            numbers.add(random.nextInt(100));
        }
        Collections.shuffle(numbers);

        for (int num : numbers) {
            TextView textView = createDraggableTextView(num);
            numberContainer.addView(textView);
        }

        for (int i = 0; i < numCount; i++) {
            TextView placeholder = createDropZone();
            dropContainer.addView(placeholder);
        }
    }

    private TextView createDraggableTextView(int num) {
        TextView textView = new TextView(this);
        textView.setText(String.valueOf(num));
        textView.setTextSize(24);
        textView.setPadding(16, 16, 16, 16);
        textView.setBackgroundResource(R.color.white);
        textView.setOnLongClickListener(v -> {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDragAndDrop(data, shadowBuilder, v, 0);
            return true;
        });
        return textView;
    }

    private TextView createDropZone() {
        TextView dropZone = new TextView(this);
        dropZone.setText("?");
        dropZone.setTextSize(24);
        dropZone.setPadding(16, 16, 16, 16);
        dropZone.setTextColor(Color.WHITE);
        dropZone.setOnDragListener(new DragTouchListener());
        return dropZone;
    }

    private void checkAnswer() {
        if (droppedViews.size() != numbers.size()) return;

        List<Integer> userOrder = new ArrayList<>();
        for (TextView view : droppedViews) {
            userOrder.add(Integer.parseInt(view.getText().toString()));
        }

        List<Integer> correctOrder = new ArrayList<>(numbers);
        if (isAscending) {
            Collections.sort(correctOrder);
        } else {
            correctOrder.sort(Collections.reverseOrder());
        }

        if (userOrder.equals(correctOrder)) {
            showCorrectDialog();
        } else {
            showIncorrectDialog();
        }
    }

    private void resetGame() {
        numberContainer.removeAllViews();
        dropContainer.removeAllViews();
        droppedViews.clear();

        for (int num : numbers) {
            TextView textView = createDraggableTextView(num);
            numberContainer.addView(textView);
        }

        for (int i = 0; i < numbers.size(); i++) {
            TextView placeholder = createDropZone();
            dropContainer.addView(placeholder);
        }
    }

    private void showFinalResult() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View customView = getLayoutInflater().inflate(R.layout.dialog_final_result2, null);
        TextView messageText = customView.findViewById(R.id.dialog_message);
        ImageView imageView = customView.findViewById(R.id.dialog_image);

        messageText.setText("Well Done! Now will be easily to pack them!");
        imageView.setImageResource(R.drawable.ordersuccess);

        builder.setView(customView);
        builder.setPositiveButton("Restart", (dialog, which) -> {
            questionCount = 0;
            startNewQuestion();
        });
        builder.setNegativeButton("Home", (dialog, which) -> {
            Intent intent = new Intent(OrderActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        builder.show();
    }



    private void showCorrectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View customView = getLayoutInflater().inflate(R.layout.dialog_final_result2, null);
        TextView messageText = customView.findViewById(R.id.dialog_message);
        ImageView imageView = customView.findViewById(R.id.dialog_image);
        messageText.setText("Great job! Moving to the next quest.");
        imageView.setImageResource(R.drawable.correct);

        builder.setView(customView);
        builder.setPositiveButton("GO", (dialog, which) -> startNewQuestion());
        builder.show();
    }

    private void showIncorrectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View customView = getLayoutInflater().inflate(R.layout.dialog_final_result2, null);
        TextView messageText = customView.findViewById(R.id.dialog_message);
        ImageView imageView = customView.findViewById(R.id.dialog_image);

        messageText.setText("Incorrect. Please try again.");
        imageView.setImageResource(R.drawable.incorrect);

        builder.setView(customView);
        builder.setPositiveButton("GO", null);
        builder.show();
    }

    private class DragTouchListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            if (event.getAction() == DragEvent.ACTION_DROP) {
                TextView target = (TextView) v;
                View draggedView = (View) event.getLocalState();
                TextView draggedTextView = (TextView) draggedView;

                target.setText(draggedTextView.getText());
                target.setOnDragListener(null);
                droppedViews.add(target);
                draggedView.setVisibility(View.INVISIBLE);
            }
            return true;
        }
    }
}
