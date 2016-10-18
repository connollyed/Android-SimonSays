package com.connollyed.simonsays;

import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class SimonSaysActivity extends AppCompatActivity {

    private ArrayList <Integer> sequence;
    private final int GREEN=0, RED=1, BLUE=2, YELLOW=3;
    private SoundPool soundPool;
    boolean sounds_loaded = false;
    private int click_index = 0;

    private CountDownTimer timer;
    private int timerval = 10000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simon_says);


        // Load the sound
//        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
//            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
//        } else {
//            AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                    .setUsage(AudioAttributes.USAGE_GAME)
//                    .build();
//
//            SoundPool.Builder builder = new SoundPool.Builder();
//            builder.setMaxStreams(2)
//                    .setAudioAttributes(audioAttributes)
//                    .build();
//
//            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
//                @Override
//                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
//                    sounds_loaded = true;
//                }
//            });
//        }
        // int soundID = soundPool.load(this, R.raw.beep, 1);


        sequence = new ArrayList<>();
        addToSequence();
        addToSequence();

        Log.i("SEQ", sequence.toString());
        displaySequence();

        //Timer
        runTimer();
    }

    public void runTimer(){
        final TextView timer_text = (TextView) findViewById(R.id.timer);

        if(timer != null)
            timer.cancel();

        timer = new CountDownTimer(timerval + 300,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer_text.setText(String.valueOf(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                timer_text.setText("0");
                Toast.makeText(getApplicationContext(),"GAME OVER", Toast.LENGTH_SHORT).show();
                finish();
                //End Game
            }
        }.start();
        timerval += 5000;
    }
    public void ButtonClick(final View view) {
        ImageButton clicked = (ImageButton) view;
        boolean seq_matches = true;

        Log.i("SEQ", "TAG = " + clicked.getTag().toString());

        //Highlight clicked button
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                highlightColour(view);
                Looper.loop();
            }
        });
        thread.start();

        // Code entered was wrong
        if(Integer.valueOf(clicked.getTag().toString())!=(sequence.get(click_index))) {
            seq_matches = false;
        } else {
            click_index++;
        }

        //User Sequence and Generated Sequence dont match
        if(seq_matches == false){
            //GAME OVER
            Toast.makeText(this,"GAME OVER",Toast.LENGTH_SHORT).show();
            finish();
        } else if (seq_matches == true && click_index == sequence.size()){
            // Sequences match generate new sequence and display
            addToSequence();
            Log.i("SEQ", sequence.toString());
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            displaySequence();
            click_index = 0; //reset click index to start again

            //reset timer
            runTimer();
        }
    }

    /**
     * Display Sequence reads the Current Sequence and highlights segment
     */
    private void displaySequence() {
        final ImageButton red = (ImageButton) findViewById(R.id.redButton);
        final ImageButton green = (ImageButton) findViewById(R.id.greenButton);
        final ImageButton blue = (ImageButton) findViewById(R.id.blueButton);
        final ImageButton yellow = (ImageButton) findViewById(R.id.yellowButton);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

//                //      Disable Image Buttons click
//                red.setEnabled(false);
//                green.setEnabled(false);
//                blue.setEnabled(false);
//                yellow.setEnabled(false);

                Looper.prepare();
                for (int i = 0; i < sequence.size(); i++) {
                    int element = sequence.get(i);
                    if (element == RED) {
                        highlightColour(red);
                    } else if (element == GREEN) {
                        highlightColour(green);
                    } else if (element == BLUE) {
                        highlightColour(blue);
                    } else {
                        highlightColour(yellow);    // else element is YELLOW
                    }
                }
                Looper.loop();

//                //        Make entire window touchable again, call
//                red.setEnabled(true);
//                green.setEnabled(true);
//                blue.setEnabled(true);
//                yellow.setEnabled(true);

            }
        });
        thread.start();



    }

    /**
     * Fades in and out specified colour
     * @param view The button to be faded in and out
     */
    private void highlightColour(View view) {
        final ImageButton v = (ImageButton) view;
        final int SLEEP_TIME = 400;
        final int FADE_DURATION = 50;

        //Sleep and fade In
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        v.animate().alpha(0.0f).setDuration(FADE_DURATION);

        //Sleep and fade out
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        v.animate().alpha(1.0f).setDuration(FADE_DURATION);

        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Adds a new element/colour to the sequence
     */
    private void addToSequence(){
        sequence.add(getRandomNumber());
    }

    /**
     * Gets a number in the range 0 - 3 representing a color
     * @return Random Number
     */
    private int getRandomNumber(){
        Random num = new Random();
        return num.nextInt(4);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        //Cancel Countdown timer if one exists
        if(timer!=null)
            timer.cancel();
    }
}