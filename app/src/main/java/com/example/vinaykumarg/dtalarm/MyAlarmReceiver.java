package com.example.vinaykumarg.dtalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Vinay Kumar G on 01-06-2016.
 */
public class MyAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String str = intent.getExtras().getString("extra");
        Integer song_choice = intent.getExtras().getInt("songchoice");
        Log.e("Song to be played",song_choice+"");
        Intent service_intent = new Intent(context,RingtonePlayingService.class);
        service_intent.putExtra("extra",str);
        service_intent.putExtra("songchosen",song_choice);
        context.startService(service_intent);
    }
}

