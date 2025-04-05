package com.example.p13_loh_zhi_heng_2201765;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer bgMusic;
    private boolean isMuted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bgMusic = MediaPlayer.create(this, R.raw.bg);
        bgMusic.setLooping(true);
        bgMusic.start();

        // 绑定静音按钮
        Button muteButton = findViewById(R.id.mute_button);
        muteButton.setOnClickListener(v -> toggleMusic(muteButton));
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

    public void CompareActivity(View view) {
        Intent intent = new Intent(this, CompareActivity.class);
        startActivity(intent);
    }

    public void OrderActivity(View view) {
        Intent intent = new Intent(this, OrderActivity.class);
        startActivity(intent);
    }

    public void ComposeActivity(View view) {
        Intent intent = new Intent(this, ComposeActivity.class);
        startActivity(intent);
    }
}
