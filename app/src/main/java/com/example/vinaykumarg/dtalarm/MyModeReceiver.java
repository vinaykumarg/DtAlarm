package com.example.vinaykumarg.dtalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Abhilash on 6/2/2016.
 */
public class MyModeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Integer mode_choice = intent.getExtras().getInt("Mode_choice");
        Integer song_choice = intent.getExtras().getInt("Song_choice");
        Log.e("Mode choice:",mode_choice+"");
        Log.e("Song choice:",song_choice+"");
        mylocationtracker g = new mylocationtracker();
        g.setmodechoice(mode_choice);
        g.setsongchoice(song_choice);
    }
}
