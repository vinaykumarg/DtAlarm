package com.example.vinaykumarg.dtalarm;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Intent intent1, intent2;
    PendingIntent pending_intent;
    Spinner spinner, spinner1;
    ArrayAdapter<CharSequence> adapter;
    ArrayAdapter<CharSequence> adapter1;
    int modechoice;
    int songchoice;
    int REQUEST_PLACE_PICKER = 1;
    ImageButton place;
    private static final String TAG = "Error";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private GoogleApiClient client;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for internet
        boolean internet = haveNetworkConnection();
        if (!internet) {
            Toast.makeText(MainActivity.this, "Please enable internet ", Toast.LENGTH_LONG).show();
        }
        setContentView(R.layout.activity_main);
        TextView desi = (TextView) findViewById(R.id.destalert);
        place = (ImageButton) findViewById(R.id.places);
        tv = (TextView) findViewById(R.id.txtPlaceDetails);

        // Checking for GPS
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        boolean check = locationManager.isProviderEnabled(provider);
        if (!check) {
            Toast.makeText(MainActivity.this, "please enable GPS", Toast.LENGTH_LONG).show();
        }
        assert tv != null;
        tv.setVisibility(View.GONE);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());

                String placeDetailsStr =
//                        place.getName() + "\n"
//                        + place.getId() + "\n"
//                        + place.getLatLng().toString() + "\n"
                        place.getAddress() + "\n";
//                        + place.getAttributions();
//                assert tv != null;
                tv.setText(placeDetailsStr);
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        place.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onPickButtonClick(v);
            }
        });


        spinner = (Spinner) findViewById(R.id.mode_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(this,
                R.array.Mode_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        spinner1 = (Spinner) findViewById(R.id.song_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter1 = ArrayAdapter.createFromResource(this,
                R.array.Songs_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(this);

//        final EditText des = (EditText) findViewById(R.id.destination);
//        assert des != null;
//        des.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Button getloc = (Button) findViewById(R.id.getloc);
//        Button alarm = (Button) findViewById(R.id.alarm);
        assert getloc != null;
        getloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent1 = new Intent(MainActivity.this, MyModeReceiver.class);
                intent1.putExtra("Mode_choice", modechoice);
                intent1.putExtra("Song_choice", songchoice);
                sendBroadcast(intent1);
                if (tv.getText()!="") {
                Intent intent = new Intent(MainActivity.this, mylocationtracker.class);
                 intent.putExtra("destination", tv.getText().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "destination not set", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        alarm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
//                startActivity(intent);
//            }
//        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner spin = (Spinner) parent;

        if (spin.getId() == R.id.mode_spinner) {
            modechoice = (int) id;
            Log.e("position", modechoice + "");
//            Toast.makeText(this, "Your choose :" + position, Toast.LENGTH_SHORT).show();
        } else if (spin.getId() == R.id.song_spinner) {
            songchoice = (int) id;
            Log.e("position1", songchoice + "");
//            Toast.makeText(this, "Your choose :" + position, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.vinaykumarg.dtalarm/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.vinaykumarg.dtalarm/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    // Place Picker Code

    public void onPickButtonClick (View v){
        // Construct an intent for the place picker
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult ( int requestCode,
                                      int resultCode, Intent data){

        if (requestCode == REQUEST_PLACE_PICKER
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null) {
                attributions = "";
            }

            tv = (TextView) findViewById(R.id.txtPlaceDetails);
            assert tv != null;
            tv.setText(address);
            String info = (String) address;
            Toast.makeText(MainActivity.this, info, Toast.LENGTH_LONG).show();

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Checking for internet connectivity

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}