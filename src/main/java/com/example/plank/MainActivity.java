package com.example.plank;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity{

    private Accelerometer accelerometer;
    private TextView plankPos;
    private Timer timer;
    private SoundPool inst;
    private int right,left,lower,raise,perfect;
    private int currStreamID;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        plankPos = findViewById(R.id.simpleTextView);

        NumberPicker Min = (NumberPicker) findViewById(R.id.numpicker_minutes);
        NumberPicker Sec = (NumberPicker) findViewById(R.id.numpicker_seconds);

        Min.setMaxValue(59);
        Min.setMinValue(0);

        Sec.setMaxValue(59);
        Sec.setMinValue(0);

        inst = new SoundPool.Builder()
                .setMaxStreams(1)
                .build();

        right = inst.load(this, R.raw.right, 1);
        left = inst.load(this, R.raw.left, 1);
        lower = inst.load(this, R.raw.lower, 1);
        raise = inst.load(this, R.raw.raise, 1);
        perfect = inst.load(this, R.raw.perfect, 1);

        timer = new Timer();
        Button startButton = (Button) findViewById(R.id.button);
        startButton.setText("START");
        timer.setButton(startButton);
        timer.setTimeDisplay((TextView) findViewById(R.id.time));
        timer.setNumberPickerMin(Min);
        timer.setNumberPickerSec(Sec);

        SoundPool effects = new SoundPool.Builder()
                .setMaxStreams(1)
                .build();

        int startup  = effects.load(this, R.raw.mk64_countdown, 1);
        int complete = effects.load(this, R.raw.tada, 1);

        timer.setEffects(effects);
        timer.setStartUp(startup);
        timer.setComplete(complete);

        accelerometer = new Accelerometer(this);
        accelerometer.setListener(new Accelerometer.Listener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onMovement(float x, float y, float z) {
                if (timer.isStarted()) {
                    //inst.autoResume();
                    if (y > 1.1f) {
                        if (!timer.isPaused()) {
                            timer.pause();
                            inst.play(raise, 1, 1, 0, -1, 1);
                            plankPos.setText("Raise Hips");
                            plankPos.setTextColor(Color.parseColor("red"));
                        }

                    } else if (y < -1.1f) {
                        if (!timer.isPaused()) {
                            timer.pause();
                            inst.play(lower, 1, 1, 0, -1, 1);
                            plankPos.setText("Lower Hips");
                            plankPos.setTextColor(Color.parseColor("red"));
                        }

                    } else if (x > 1.5f) {
                        if (!timer.isPaused()) {
                            timer.pause();
                            inst.play(left, 1, 1, 0, -1, 1);
                            plankPos.setText("Raise Left Torso");
                            plankPos.setTextColor(Color.parseColor("red"));
                        }
                    } else if (x < -1.5f) {
                        if (!timer.isPaused()) {
                            timer.pause();
                            inst.play(right, 1, 1, 0, -1, 1);
                            plankPos.setText("Raise Right Torso");
                            plankPos.setTextColor(Color.parseColor("red"));
                        }
                    } else {
                        if (timer.isPaused()) {
                            timer.resume();
                            inst.play(perfect, 1, 1, 0, 0, 1);
                        }
                        plankPos.setText("Perfect!");
                        plankPos.setTextColor(Color.parseColor("green"));
                    }
                }
                else {
                    inst.autoPause();
                    //inst.stop(currStreamID);
                    plankPos.setText("READY");
                    plankPos.setTextColor(Color.parseColor("green"));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        accelerometer.register();
    }

    @Override
    protected void onPause() {
        accelerometer.unregister();
        inst.autoPause();
        super.onPause();
    }
}