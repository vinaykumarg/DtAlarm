// Currently Not using this class


package com.example.vinaykumarg.dtalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class Main2Activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    AlarmManager alarm_manager;
    PendingIntent pending_intent;
    Intent intent;
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    int choose_song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentalarm);
        spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(this,
                R.array.Songs_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }

    public void startalarm_ringtone(View view) {
        Button stop = (Button) findViewById(R.id.alarm_stop);
        assert stop != null;
        stop.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm_manager.cancel(pending_intent);
                intent.putExtra("extra", "alarm off");
                intent.putExtra("songchoice",choose_song);
                sendBroadcast(intent);
            }
        }));
        alarm_manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        EditText e = (EditText) findViewById(R.id.Input_time);
        int time = Integer.parseInt(e.getText().toString());
        intent = new Intent(this,MyAlarmReceiver.class);
        intent.putExtra("extra","alarm on");
        intent.putExtra("songchoice",choose_song);
        Log.e("song", choose_song+"");
        pending_intent = PendingIntent.getBroadcast(this.getApplicationContext(), 123456789, intent, 0);
        alarm_manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (time * 1000), pending_intent);
    }

    public void startalarm_vibrate(View view) {
        EditText e1 =  (EditText) findViewById(R.id.Time);
        int time = Integer.parseInt(e1.getText().toString());
        Intent intent = new Intent(this,MyBroadcastReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(),123456789,intent,0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+(time*1000),pendingIntent);
        Toast.makeText(this, "Alarm set in " + time + " seconds", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        choose_song = (int) id;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
