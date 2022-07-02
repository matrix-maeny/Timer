package com.matrix_maeny.timer.fragments;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.matrix_maeny.timer.MainActivity;
import com.matrix_maeny.timer.NotificationSender;
import com.matrix_maeny.timer.R;

import java.util.Calendar;

public class StopWatchFragment extends Fragment {


    ImageView playPauseBtn, stopBtn;
    TextView stopWatchText;
    SeekBar seekBar;
    private boolean running = false;
    boolean clickedPlayBtn = false;
    Handler handler;
    private int hour = 0, minute = 0, sec = 0;
    private String timeText;
    int tempSec1 = 0, tempSec2 = 0;
    Calendar calendar;//= Calendar.getInstance();

    NotificationManager manager = null;
    NotificationCompat.Builder notification = null;

    public StopWatchFragment() {
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_stop_watch, container, false);

        playPauseBtn = root.findViewById(R.id.playPauseButton);
        stopBtn = root.findViewById(R.id.stopButton);
        stopWatchText = root.findViewById(R.id.stopWatchText);
        seekBar = root.findViewById(R.id.seekBar);
        seekBar.setEnabled(false);

        createNotification();

        handler = new Handler();

        manager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);


        //calendar.set(Calendar.SECOND,0);

        playPauseBtn.setOnClickListener(playPauseListener);

        stopBtn.setOnClickListener(stopBtnListener);


        return root;
    }

    private void startStopWatch() { // for starting the time
        if (!running) {

            handler.postDelayed(runnable, 1000);
            sendNotification();

            running = true;
            sendNotification();
        }
    }

    private void pauseStopWatch() { // for pausing the time
        if (running) {
            handler.removeCallbacks(runnable);
            running = false;
            manager.cancel(1);
        }
    }

    private void resetStopWatch() { // for resetting the time

        pauseStopWatch();
        timeText = "00:00:00";
        hour = minute = sec = tempSec1 = tempSec2 = 0;
        seekBar.setProgress(0);
        stopWatchText.setText(timeText);
//        manager.cancel(1);
    }

    private void createNotification() {

        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notification = new NotificationCompat.Builder(requireContext(), NotificationSender.CHANNEL_STOPWATCH_ID)
                .setSmallIcon(R.drawable.timer2)
                .setContentTitle("Stopwatch is running")
                .setContentText("Tap to stop stop the Stopwatch")
                .setAutoCancel(false)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setOngoing(true);
    }

    private void sendNotification() {

        notification.setContentTitle("Stopwatch is running");
        manager.notify(1, notification.build());

    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String hourText;// = "";
            String minuteText;// = "";
            String secText;// = "";

            calendar = Calendar.getInstance();
            tempSec1 = calendar.get(Calendar.SECOND);

            if (hour != 24) { //hour should not be 24

                if (tempSec1 != tempSec2) {
                    sec++;
                }
                double progress = sec / 60.0;
                progress *= 100;
                seekBar.setProgress((int) progress);
                if (minute == 60) { // increment hour at hour = 60
                    minute = 0;
                    hour++;
                }

                if (sec == 60) { // at 60 make sec = 0;
                    sec = 0;
                    minute++;
                }

                if (hour <= 9) { // if < 9 show as 01 or 02 means add 0 as padding
                    hourText = "0" + hour;
                } else {
                    hourText = "" + hour;

                }

                if (sec <= 9) { // padding
                    secText = "0" + sec;
                } else {
                    secText = "" + sec;
                }


                if (minute < 10) { // padding
                    minuteText = "0" + minute;
                } else {
                    minuteText = "" + minute;
                }

                timeText = "" + hourText + ":" + minuteText + ":" + secText;
                stopWatchText.setText(timeText);
                tempSec2 = calendar.get(Calendar.SECOND);

                handler.postDelayed(this, 0);

            } else {
                pauseStopWatch();
            }

        }
    };

    View.OnClickListener playPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!clickedPlayBtn) {
                playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                clickedPlayBtn = true;
                startStopWatch();
            } else {
                playPauseBtn.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                pauseStopWatch();
                clickedPlayBtn = false;
            }

        }
    };

    View.OnClickListener stopBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            resetStopWatch();
            playPauseBtn.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
            clickedPlayBtn = false;
        }
    };


    @Override
    public void onPause() {
        if (clickedPlayBtn) {
            handler.postDelayed(runnable, 0);
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (clickedPlayBtn) {
            handler.postDelayed(runnable, 0);
        }
        super.onStop();
    }
}