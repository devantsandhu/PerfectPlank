package com.example.plank;

import android.annotation.SuppressLint;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Timer extends AppCompatActivity {

    private Button button;
    private TextView timeDisplay;
    private boolean isPaused;
    private boolean started;
    private CountDownTimer mainTimer;
    private CountDownTimer countDownTimer;
    private Boolean countingDown;

    private int milliseconds;
    private long prevTime;

    private NumberPicker numberPickerMin;
    private NumberPicker numberPickerSec;

    public void setEffects(SoundPool effects) {
        this.effects = effects;
    }

    public void setStartUp(int startUp) {
        this.startUp = startUp;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

    public SoundPool getEffects() {
        return effects;
    }

    private SoundPool effects;
    private int startUp, complete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    Timer () {
        started = false;
        countingDown = false;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setTimeDisplay(TextView timeDisplay) {
        this.timeDisplay = timeDisplay;
    }

    public void updateTime(String time) {
        timeDisplay.setText(time);
    }


    // ******************************************
    public void start() {
        int min = numberPickerMin.getValue();
        int sec = numberPickerSec.getValue();
        milliseconds = min * 60 * 1000 + sec * 1000;
        effects.play(startUp,1,1,0,0,1);
        countingDown = true;
        isPaused = false;
        setStatus("CANCEL");
        prevTime = milliseconds;

        countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long l) {
                if (l <= 1000)
                    updateTime("GO!");
                else if (l <= 2000)
                    updateTime("SET");
                else if (l <= 3000)
                    updateTime("READY");
            }

            @Override
            public void onFinish() {
                countingDown = false;
                started = true;
                //setStatus("END");
                mainTimer = new CountDownTimer(milliseconds, 1000) {
                    @SuppressLint("DefaultLocale")
                    public void onTick(long millisUntilFinished) {
                        if (!isPaused) {
                            prevTime = millisUntilFinished;
                            int seconds = (int) (millisUntilFinished / 1000);
                            int minutes = seconds / 60;
                            seconds     = seconds % 60;
                            //setStatus("seconds remaining: " + millisUntilFinished / 1000);
                            updateTime(String.format("%d:%02d", minutes, seconds));
                        }
                    }
                    public void onFinish() {
                        //setStatus("FINISHED");
                        stop();
                        effects.play(complete,1,1,0,0,1);
                    }
                }.start();
                //resume();
            }
        }.start();
    }

    public void resume() {
        mainTimer = new CountDownTimer(prevTime, 1000) {
            @SuppressLint("DefaultLocale")
            public void onTick(long millisUntilFinished) {
                if (!isPaused) {
                    prevTime = millisUntilFinished;
                    int seconds = (int) (millisUntilFinished / 1000);
                    int minutes = seconds / 60;
                    seconds     = seconds % 60;
                    //setStatus("seconds remaining: " + millisUntilFinished / 1000);
                    updateTime(String.format("%d:%02d", minutes, seconds));
                }
            }
            public void onFinish() {
                stop();
                effects.play(complete,1,1,0,0,1);
            }
        }.start();
        isPaused = false;
        //setStatus("END");
    }

    public void pause() {
        mainTimer.cancel();
        //setStatus("STOP");
        isPaused = true;
    }

    public void stop() {
        effects.stop(effects.play(startUp,1,1,0,0,1));
        started = false;
        countingDown = false;
        if (countDownTimer != null)
            countDownTimer.cancel();
        if (mainTimer != null)
            mainTimer.cancel();
        setStatus("START");
        prevTime = 0;
        updateTime("0:00");
    }

    public void setNumberPickerMin(NumberPicker numberPickerMin) {
        this.numberPickerMin = numberPickerMin;
    }

    public void setNumberPickerSec(NumberPicker numberPickerSec) {
        this.numberPickerSec = numberPickerSec;
    }

    @SuppressLint("SetTextI18n")
    public void setButton(Button button) {
        this.button = button;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countingDown) {
                    effects.stop(startUp);
                    stop();
                }
                else if (!started) {
                    start();
                }
                else {
                    stop();
                }
            }
        });
    }

    public void setStatus(String status) {
        button.setText(status);
    }
}
