package com.example.vinaykumarg.dtalarm;

/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Getting Location Updates.
 * <p/>
 * Demonstrates how to use the Fused Location Provider API to get updates about a device's
 * location. The Fused Location Provider is part of the Google Play services location APIs.
 * <p/>
 * For a simpler example that shows the use of Google Play services to fetch the last known location
 * of a device, see
 * https://github.com/googlesamples/android-play-location/tree/master/BasicLocation.
 * <p/>
 * This sample uses Google Play services, but it does not require authentication. For a sample that
 * uses Google Play services for authentication, see
 * https://github.com/googlesamples/android-google-accounts/tree/master/QuickStart.
 */
public class mylocationtracker extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
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
    protected static final String TAG = "location-updates-sample";

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;

    // UI Widgets.
    protected Button mStartUpdatesButton;
    protected Button mStopUpdatesButton;
    protected TextView mLastUpdateTimeTextView;
    protected TextView mLatitudeTextView;
    protected TextView mLongitudeTextView;

    // Labels.
    protected String mLatitudeLabel;
    protected String mLongitudeLabel;
    protected String mLastUpdateTimeLabel;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Locate the UI widgets.
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
        mStartUpdatesButton = (Button) findViewById(R.id.start_updates_button);
        mStopUpdatesButton = (Button) findViewById(R.id.stop_updates_button);
        mLatitudeTextView = (TextView) findViewById(R.id.latitude_text);
        mLongitudeTextView = (TextView) findViewById(R.id.longitude_text);
        mLastUpdateTimeTextView = (TextView) findViewById(R.id.last_update_time_text);

        // Set labels.
        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mLastUpdateTimeLabel = getResources().getString(R.string.last_update_time_label);

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
//        setButtonsEnabledState();
//        startLocationUpdates();
        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
                setButtonsEnabledState();
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
            updateUI();
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
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

    private void startalarm() {
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int time = 1;
        intent = new Intent(this, MyAlarmReceiver.class);
        intent.putExtra("extra", "alarm on");
        Log.e("Song choice:", songchoice + "");
        intent.putExtra("songchoice", songchoice);
        sendBroadcast(intent);
        pending_intent = PendingIntent.getBroadcast(this.getApplicationContext(), 123456789, intent, 0);
        alarm_manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (time * 1000), pending_intent);
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

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Handles the Start Updates button and requests start of location updates. Does nothing if
     * updates have already been requested.
     */
    public void startUpdatesButtonHandler(View view) {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            setButtonsEnabledState();
            startLocationUpdates();
        }
    }

    /**
     * Handles the Stop Updates button, and requests removal of location updates. Does nothing if
     * updates were not previously requested.
     */
    public void stopUpdatesButtonHandler(View view) {
        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;
            setButtonsEnabledState();
            stopLocationUpdates();
        }
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Ensures that only one button is enabled at any time. The Start Updates button is enabled
     * if the user is not requesting location updates. The Stop Updates button is enabled if the
     * user is requesting location updates.
     */
    private void setButtonsEnabledState() {
        if (mRequestingLocationUpdates) {
            mStartUpdatesButton.setEnabled(false);
            mStopUpdatesButton.setEnabled(true);
        } else {
            mStartUpdatesButton.setEnabled(true);
            mStopUpdatesButton.setEnabled(false);
        }
    }

    /**
     * Updates the latitude, the longitude, and the last location time in the UI.
     */
    private void updateUI() {
        mLatitudeTextView.setText(String.format("%s: %f", mLatitudeLabel,
                mCurrentLocation.getLatitude()));
        mLongitudeTextView.setText(String.format("%s: %f", mLongitudeLabel,
                mCurrentLocation.getLongitude()));
        mLastUpdateTimeTextView.setText(String.format("%s: %s", mLastUpdateTimeLabel,
                mLastUpdateTime));
        String[] pas = new String[2];
        double lat = mCurrentLocation.getLatitude();

        double lng = mCurrentLocation.getLongitude();
        pas[0] = getMyLocationAddress(lat, lng);
        pas[1] = destinatin;
        myAddress.setText(pas[0]);
        detinationField.setText(destinatin);
        String js = getjson(pas);
        int time = gettime(js);
        String time1 = gettimestring(js);
        if (time1 != null) {
            timefield.setText(time1);
            if (time <= 350) {
                startalarm();
                stopalarm.setVisibility(View.VISIBLE);
            }
        } else {
            timefield.setText("no data available");
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        mGoogleApiClient.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "mylocationtracker Page", // TODO: Define a title for the content shown.
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
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "mylocationtracker Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.vinaykumarg.dtalarm/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {

            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
        Toast.makeText(this, getResources().getString(R.string.location_updated_message),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }
}

