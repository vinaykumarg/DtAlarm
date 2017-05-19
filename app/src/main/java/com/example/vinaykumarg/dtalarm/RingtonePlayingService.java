package com.example.vinaykumarg.dtalarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Random;

/**
 * Created by Abhilash on 5/20/2016.
 */
public class RingtonePlayingService extends Service {

    MediaPlayer media_song;
    boolean isRunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        String state = intent.getExtras().getString("extra");

        Integer song = intent.getExtras().getInt("songchosen");

        Log.e("Song that is played:",song+"");

        Intent mainintent = new Intent(this.getApplicationContext(),MainActivity.class);
        NotificationManager notify_manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        PendingIntent pending_intent = PendingIntent.getActivity(this, 0, mainintent, 0);

        Notification notification_popup = new Notification.Builder(this)
                .setContentTitle("An Alarm is going off!")
                .setContentText("Click me")
                .setContentIntent(pending_intent)
                .setStyle(new Notification.BigTextStyle().bigText("This is a notification"))
                .setAutoCancel(true)
                .build();

        assert state!= null;

        switch (state) {
            case "alarm on":
                startId = 1;
                break;
            case "alarm off":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }


        if(!this.isRunning && startId == 1){
            this.isRunning = true;
            startId = 0;
            notify_manager.notify(0, notification_popup);

            if(song == 0){
                int min = 1;
                int max = 6;
                Random rand = new Random();
                int number = rand.nextInt(max+min);

                if(number==1){
                    media_song = MediaPlayer.create(this,R.raw.birdsound);
                    media_song.start();
                }
                else if(number == 2){
                    media_song = MediaPlayer.create(this,R.raw.firetrucksiren);
                    media_song.start();
                }
                else if(number == 3){
                    media_song = MediaPlayer.create(this,R.raw.hiphopbeattone);
                    media_song.start();
                }
                else if(number == 4){
                    media_song = MediaPlayer.create(this,R.raw.manasuke);
                    media_song.start();
                }
                else if(number == 5){
                    media_song = MediaPlayer.create(this,R.raw.raindrops);
                    media_song.start();
                }
                else if(number == 6){
                    media_song = MediaPlayer.create(this,R.raw.wakeupsound);
                    media_song.start();
                }
                else{
                    media_song = MediaPlayer.create(this,R.raw.birdsound);
                    media_song.start();
                }
            }
            else if(song == 1){
                media_song = MediaPlayer.create(this,R.raw.birdsound);
                media_song.start();
            }
            else if(song == 2){
                media_song = MediaPlayer.create(this,R.raw.firetrucksiren);
                media_song.start();
            }
            else if(song == 3){
                media_song = MediaPlayer.create(this,R.raw.hiphopbeattone);
                media_song.start();
            }
            else if(song == 4){
                media_song = MediaPlayer.create(this,R.raw.manasuke);
                media_song.start();
            }
            else if(song == 5){
                media_song = MediaPlayer.create(this,R.raw.raindrops);
                media_song.start();
            }
            else if(song == 6){
                media_song = MediaPlayer.create(this,R.raw.wakeupsound);
                media_song.start();
            }
            else{
                media_song = MediaPlayer.create(this,R.raw.birdsound);
                media_song.start();
            }
        }

        else if(this.isRunning && startId == 0){
            media_song.stop();
            media_song.reset();
            this.isRunning = false;
            startId = 0;
        }

        else if(!this.isRunning && startId == 0){
            this.isRunning = false;
            startId = 0;
        }

        else if(this.isRunning && startId == 1){
            this.isRunning = true;
            startId = 1;
        }

        else{

        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.isRunning = false;
    }
}