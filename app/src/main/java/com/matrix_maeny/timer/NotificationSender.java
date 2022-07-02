package com.matrix_maeny.timer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationSender extends Application {

    public static String CHANNEL_TIMER_ID = "TIMER_CHANNEL";
    public static String CHANNEL_STOPWATCH_ID = "STOPWATCH_CHANNEL";

    @Override
    public void onCreate() {
        super.onCreate();

        createChannels();
    }

    private void createChannels(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel timerChannel = new NotificationChannel(CHANNEL_TIMER_ID,"Timer channel", NotificationManager.IMPORTANCE_HIGH);
            timerChannel.setDescription("This channel is used to send notification of timer");

            NotificationChannel stopwatchChannel = new NotificationChannel(CHANNEL_STOPWATCH_ID,"Stopwatch channel", NotificationManager.IMPORTANCE_HIGH);
            stopwatchChannel.setDescription("This channel is used to send notification of Stopwatch");

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(timerChannel);
            manager.createNotificationChannel(stopwatchChannel);

        }
    }
}
