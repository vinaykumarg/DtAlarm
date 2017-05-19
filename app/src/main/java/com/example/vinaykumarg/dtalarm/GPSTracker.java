package com.example.vinaykumarg.dtalarm;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class GPSTracker extends Activity implements LocationListener {

    static int choice;
    static int songchoice;
    private TextView latituteField;
    private TextView longitudeField;
    private TextView detinationField;
    private LocationManager locationManager;
    private String provider;
    private TextView myAddress;
    String destinatin = "";
    private TextView timefield;
    AlarmManager alarm_manager;
    PendingIntent pending_intent;
    Intent intent;
    Button stopalarm;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * Called when the activity is first created.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpstracker);
        latituteField = (TextView) findViewById(R.id.textView1);
        longitudeField = (TextView) findViewById(R.id.textView2);
        myAddress = (TextView) findViewById(R.id.address);
        detinationField = (TextView) findViewById(R.id.destination);
        stopalarm = (Button) findViewById(R.id.alarm_stop);
        stopalarm.setVisibility(View.GONE);
        timefield = (TextView) findViewById(R.id.time);
        Bundle bundle = getIntent().getExtras();
        destinatin = bundle.getString("destination");
        stopalarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm_manager.cancel(pending_intent);
                intent.putExtra("extra", "alarm off");
                intent.putExtra("songchoice", getsongchoice());
                sendBroadcast(intent);
            }
        });
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            Log.e("Location changed", "calling");
//            lastknownlocation = location;
            onLocationChanged(location);
        } else {
            latituteField.setText("Location not available");
            longitudeField.setText("Location not available");
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void setmodechoice(int modechoice) {
        Log.e("Mode is set", "hai");
        choice = modechoice;
        Log.e("GPS Tracker", choice + "");
    }

    public void setsongchoice(int song_choice) {
        Log.e("Song is set", "hello");
        songchoice = song_choice;
    }

    public int getmodechoice() {
        return choice;
    }

    public int getsongchoice() {
        return songchoice;
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        latituteField.setText(String.valueOf(lat));
        longitudeField.setText(String.valueOf(lng));
        String[] pas = new String[2];
        pas[0] = getMyLocationAddress(lat, lng);
        pas[1] = destinatin;
        myAddress.setText(pas[0]);
        detinationField.setText(destinatin);
        String js = getjson(pas);
        int time = gettime(js);
        String time1 = gettimestring(js);
        timefield.setText(time1);
        if (time <= 350) {
            startalarm();
            stopalarm.setVisibility(View.VISIBLE);
            Notify("sss", "You've received new message");
        }
    }

    private void Notify(String notificationTitle, String notificationMessage) {
    }

//    public void startalarm_vibrate() {
//        int time = 1;
//        Intent intent = new Intent(this, MyBroadcastReciever.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 123456789, intent, 0);
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (time * 1000), pendingIntent);
//        Toast.makeText(this, "Alarm set in " + time + " seconds", Toast.LENGTH_LONG).show();
//    }

    private void startalarm() {
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int time = 1;
        intent = new Intent(this, MyAlarmReceiver.class);
        intent.putExtra("extra", "alarm on");
        Log.e("Song choice:", songchoice+"");
        intent.putExtra("songchoice", songchoice);
        sendBroadcast(intent);
        pending_intent = PendingIntent.getBroadcast(this.getApplicationContext(), 123456789, intent, 0);
        alarm_manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (time * 1000), pending_intent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    public String getjson(String[] pas) {
        int mode;
        try {
            final String Maps_BASE_URL =
                    "https://maps.googleapis.com/maps/api/distancematrix/json?";
            final String source = "origins";
            final String destination = "destinations";
            final String APPID_PARAM = "key";
            mode = getmodechoice();
            if (mode == 1 || mode == 0) {                                          // For Driving
                Uri builtUri = Uri.parse(Maps_BASE_URL).buildUpon()
                        .appendQueryParameter(source, pas[0])
                        .appendQueryParameter(destination, pas[1])
                        .appendQueryParameter(APPID_PARAM, "AIzaSyDWbaBWaNTWRu3K710GiH3Eg07Qx4Q7dsw")
                        .build();
                URL url = new URL(builtUri.toString());
                Log.e("url", builtUri.toString());
                jsonclass sonclass = new jsonclass();
                String ff = sonclass.readUrl(url.toString());
                return ff;
            } else if (mode == 2) {                                                // For Bicycling
                Uri builtUri = Uri.parse(Maps_BASE_URL).buildUpon()
                        .appendQueryParameter(source, pas[0])
                        .appendQueryParameter(destination, pas[1])
                        .appendQueryParameter("mode", "bicycling")
                        .appendQueryParameter(APPID_PARAM, "AIzaSyDWbaBWaNTWRu3K710GiH3Eg07Qx4Q7dsw")
                        .build();
                URL url = new URL(builtUri.toString());
                Log.e("url", builtUri.toString());
                jsonclass sonclass = new jsonclass();
                String ff = sonclass.readUrl(url.toString());
                return ff;
            } else if (mode == 3) {                                                // For train
                Uri builtUri = Uri.parse(Maps_BASE_URL).buildUpon()
                        .appendQueryParameter(source, pas[0])
                        .appendQueryParameter(destination, pas[1])
                        .appendQueryParameter("mode", "transit")
                        .appendQueryParameter(APPID_PARAM, "AIzaSyDWbaBWaNTWRu3K710GiH3Eg07Qx4Q7dsw")
                        .build();
                URL url = new URL(builtUri.toString());
                Log.e("url", builtUri.toString());
                jsonclass sonclass = new jsonclass();
                String ff = sonclass.readUrl(url.toString());
                return ff;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMyLocationAddress(double lat, double lng) {

        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);

        try {

            //Place your latitude and longitude
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);

            if (addresses != null) {

                Address fetchedAddress = addresses.get(0);
                StringBuilder strAddress = new StringBuilder();

                for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append("\n");
                }

                return strAddress.toString();

            } else
                return "No location found!";

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not get address..!", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public int gettime(String jsstring) {
        try {
            JSONObject jsonobj = new JSONObject(jsstring);
            String togo = jsonobj.getString("destination_addresses").replaceAll("[\\[\\]\"]", "");
            String myloc = jsonobj.getString("origin_addresses").replaceAll("[\\[\\]\"]", "");
            JSONObject temp = new JSONObject(jsonobj.getString("rows").substring(1, jsonobj.getString("rows").length() - 1));
            JSONObject temp1 = new JSONObject(temp.getString("elements").substring(1, temp.getString("elements").length() - 1));
            JSONObject temp2 = new JSONObject(temp1.getString("distance"));
            JSONObject temp3 = new JSONObject(temp1.getString("duration"));
            String distance = temp2.getString("text");

            String distance1 = temp2.getString("value");
            String duration = temp3.getString("text");
            String duration1 = temp3.getString("value");
            int time = Integer.parseInt(duration1);
            return time;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String gettimestring(String jsstring) {
        try {
            JSONObject jsonobj = new JSONObject(jsstring);
            String togo = jsonobj.getString("destination_addresses").replaceAll("[\\[\\]\"]", "");
            String myloc = jsonobj.getString("origin_addresses").replaceAll("[\\[\\]\"]", "");
            JSONObject temp = new JSONObject(jsonobj.getString("rows").substring(1, jsonobj.getString("rows").length() - 1));
            JSONObject temp1 = new JSONObject(temp.getString("elements").substring(1, temp.getString("elements").length() - 1));
            JSONObject temp2 = new JSONObject(temp1.getString("distance"));
            JSONObject temp3 = new JSONObject(temp1.getString("duration"));
            String distance = temp2.getString("text");

            String distance1 = temp2.getString("value");
            String duration = temp3.getString("text");
            String duration1 = temp3.getString("value");
            int time = Integer.parseInt(duration1);
//            Log.e("dddd:",jsstring);
            return duration;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}