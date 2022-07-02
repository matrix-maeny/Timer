package com.matrix_maeny.timer.fragments;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.matrix_maeny.timer.MainActivity;
import com.matrix_maeny.timer.NotificationSender;
import com.matrix_maeny.timer.R;

import java.util.Calendar;

public class TimerFragment extends Fragment {

    NumberPicker hourPicker, minutePicker, secondPicker;
    ImageView playPauseBtn, stopBtn;
    TextView timerTextView;
    AppCompatButton stopTimerBtn;
    SeekBar seekBar;
    Handler handler = null;
    MediaPlayer mediaPlayer = null;
    int totalTime = 0,progressTime = 0;
    NotificationManager manager = null;
    NotificationCompat.Builder notification = null;
    double progress = 100;

    Calendar calendar = null;

    int hour = 0, minute = 0, second = 0;
    boolean running = false;
    boolean clickedPlayBtn = false, started = false;

    int tempSec1 = 0, tempSec2 = 0;
    String timeText;

    public TimerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_timer, container, false);

        hourPicker = root.findViewById(R.id.hourPicker);
        minutePicker = root.findViewById(R.id.minutePicker);
        secondPicker = root.findViewById(R.id.secondPicker);
        timerTextView = root.findViewById(R.id.timerText);
        playPauseBtn = root.findViewById(R.id.playPauseButton);
        stopBtn = root.findViewById(R.id.stopButton);
        stopTimerBtn = root.findViewById(R.id.stopTimerBtn);
        seekBar = root.findViewById(R.id.seekBar);
        seekBar.setEnabled(false);

        mediaPlayer = MediaPlayer.create(getContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        mediaPlayer.setLooping(true);
        manager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        handler = new Handler();

        createNotification();


        timerTextView.setVisibility(View.GONE);
        stopTimerBtn.setVisibility(View.GONE);
        seekBar.setVisibility(View.GONE);

        hourPicker.setMaxValue(23);
        minutePicker.setMaxValue(59);
        secondPicker.setMaxValue(59);

        playPauseBtn.setOnClickListener(playPauseListener);
        stopBtn.setOnClickListener(stopBtnListener);
        stopTimerBtn.setOnClickListener(stopTimerListener);

        return root;
    }

    View.OnClickListener playPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!started) {
                hour = hourPicker.getValue();
                minute = minutePicker.getValue();
                second = secondPicker.getValue();
            }

            totalTime = getTotalTime(hour, minute, second);

            if (totalTime != 0) {
                if (!clickedPlayBtn) {
                    playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                    clickedPlayBtn = true;
                    if (!started) {
                        startTimer();
                        progressTime = totalTime;
                        started = true;
                    } else {
                        resumeTimer();
                    }
                } else {
                    playPauseBtn.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                    pauseTimer();
                    clickedPlayBtn = false;
                }
            } else {
                playPauseBtn.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                clickedPlayBtn = false;
                Toast.makeText(getContext(), "Can't run timer for selected time", Toast.LENGTH_LONG).show();
            }

        }
    };

    View.OnClickListener stopBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            stopMusic();
            resetTimer();
            started = false;
            playPauseBtn.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
            clickedPlayBtn = false;
            stopTimerBtn.setVisibility(View.GONE);

        }
    };

    View.OnClickListener stopTimerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // stop music
            stopMusic();
            resetTimer();
            started = false;
            playPauseBtn.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
            clickedPlayBtn = false;
            stopTimerBtn.setVisibility(View.GONE);
            //manager.cancel(0);
        }
    };

    private void startTimer() {

        hourPicker.setVisibility(View.GONE);
        minutePicker.setVisibility(View.GONE);
        secondPicker.setVisibility(View.GONE);
        timerTextView.setVisibility(View.VISIBLE);
        seekBar.setVisibility(View.VISIBLE);

        setTheText();
        seekBar.setProgress(100);

        handler.postDelayed(countdownRunnable, 1000);
        sendNotification();
        running = true;


    }

    private void resumeTimer() { // to resume timer from where it stopped
        handler.postDelayed(countdownRunnable, 1000);
        sendNotification();
    }

    private void pauseTimer() { // to pause timer

        handler.removeCallbacks(countdownRunnable);
        running = false;
        manager.cancel(0);

    }

    private void resetTimer() {
        pauseTimer();

        hourPicker.setVisibility(View.VISIBLE);
        minutePicker.setVisibility(View.VISIBLE);
        secondPicker.setVisibility(View.VISIBLE);
        timerTextView.setVisibility(View.GONE);
        seekBar.setVisibility(View.GONE);
        seekBar.setProgress(100);

        // hour = minute = second = 0;
        //you have to set seek bar
        hour = second = minute = tempSec1 = tempSec2 = 0;
        handler.removeCallbacks(messageRunnable);
//        manager.cancel(0);
        WakeLock.releaseLock();

    }

    private void startMusic() {


        if (mediaPlayer != null) {
            try {

                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
                handler.removeCallbacks(countdownRunnable);


            } catch (Exception e) {
                Toast.makeText(getContext(), "some error occurred while running alarm", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer = MediaPlayer.create(getContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            mediaPlayer.setLooping(true);
        }
    }


    private int getTotalTime(int h, int m, int s) {
        return h * 60 * 60 + m * 60 + s;
    }

    private void setTheText() {

        String hourText;// = "";
        String minuteText;// = "";
        String secText;// = "";


        if (hour <= 9) { // if < 9 show as 01 or 02 means add 0 as padding
            hourText = "0" + hour;
        } else {
            hourText = "" + hour;

        }

        if (second <= 9) { // padding
            secText = "0" + second;
        } else {
            secText = "" + second;
        }


        if (minute < 10) { // padding
            minuteText = "0" + minute;
        } else {
            minuteText = "" + minute;
        }

        timeText = "" + hourText + ":" + minuteText + ":" + secText;
        timerTextView.setText(timeText);
    }

    private void sendNotification() {

        notification.setContentTitle("Timer is running");
        manager.notify(0, notification.build());

    }

    private void createNotification() {

        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notification = new NotificationCompat.Builder(requireContext(), NotificationSender.CHANNEL_TIMER_ID)
                .setSmallIcon(R.drawable.timer2)
                .setContentTitle("Timer is running")
                .setContentText("Tap to stop stop the timer")
                .setAutoCancel(false)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setOngoing(true);
    }

    Runnable countdownRunnable = new Runnable() {
        @Override
        public void run() {


            boolean checkSetText = true;
            int totalTime2 = getTotalTime(hour, minute, second);
            progress = (double) totalTime2 / progressTime;
            progress *= 100;
            seekBar.setProgress((int) progress);


            calendar = Calendar.getInstance();
            tempSec1 = calendar.get(Calendar.SECOND);

            if (totalTime2 != 0) { // total time should not be zero

                if (second == 0 && minute != 0) {
                    setTheText();
                    checkSetText = false;
                    second = 59;
                    minute--;
                }


                if (second == 0 && hour != 0) {
                    setTheText();
                    checkSetText = false;
                    minute = 59;
                    second = 59;
                    hour--;
                }

//                if(hour == 23) hour = 0;



                if (tempSec1 != tempSec2 && checkSetText)
                    second--;


                setTheText();

                tempSec2 = calendar.get(Calendar.SECOND);

                handler.postDelayed(this, 0);

            } else {
                WakeLock.acquireLock(getContext());
                setTheText();
                seekBar.setProgress(0);
                clickedPlayBtn = false;
                playPauseBtn.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                running = false;
                stopTimerBtn.setVisibility(View.VISIBLE);
                handler.postDelayed(messageRunnable, 0);
                startMusic();


            }

        }
    };

    Runnable messageRunnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(getContext(), "Stop the timer", Toast.LENGTH_LONG).show();
            handler.postDelayed(this, 5000);
        }
    };


    @Override
    public void onPause() {
        if (clickedPlayBtn) {
            handler.postDelayed(countdownRunnable, 0);
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (clickedPlayBtn) {
            handler.postDelayed(countdownRunnable, 0);
        }
        super.onStop();
    }

    // for waking up the screen
    static class WakeLock {
        private static PowerManager.WakeLock wakeLock;

        public static void acquireLock(Context ctx) {
            if (wakeLock != null) wakeLock.release();

            PowerManager manager = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);

            wakeLock = manager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "myapp:WakeLock");
            wakeLock.acquire(2 * 60 * 1000L/*2 minutes*/);
        }

        public static void releaseLock() {
            if (wakeLock != null) wakeLock.release();
            wakeLock = null;
        }
    }

}