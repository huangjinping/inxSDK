package cdax.naindex;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;


public class LocationInformation {
    private Activity context;
    private final static String LOCAL_GOOGLE = "local_location";

    public LocationInformation(Activity context) {
        this.context = context;
        init();
    }

    public static GoogleLocation getLocalLocation(Context context) {
        GoogleLocation googleLocation = new GoogleLocation();
        SharedPreferences local_location = context.getSharedPreferences(LOCAL_GOOGLE, MODE_PRIVATE);
        googleLocation.setGoogleLatitude(local_location.getString("googleLatitude", ""));
        googleLocation.setGoogleLongitude(local_location.getString("googleLongitude", ""));
        return googleLocation;
    }

    public static void clear(Context context) {
        SharedPreferences setting_info = context.getSharedPreferences(LOCAL_GOOGLE, MODE_PRIVATE);
        SharedPreferences.Editor edit = setting_info.edit();
        edit.putString("googleLatitude", "");
        edit.putString("googleLongitude", "");
        edit.commit();
    }


    private LocationCallBack locationCallBack;

    public void setLocationCallBack(LocationCallBack locationCallBack) {
        this.locationCallBack = locationCallBack;
    }

    public static void saveLocalLocation(Context context, GoogleLocation googleLocation) {
        SharedPreferences setting_info = context.getSharedPreferences(LOCAL_GOOGLE, MODE_PRIVATE);
        SharedPreferences.Editor edit = setting_info.edit();
        edit.putString("googleLatitude", googleLocation.getGoogleLatitude());
        edit.putString("googleLongitude", googleLocation.getGoogleLongitude());
        edit.commit();
    }


    private void init() {
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mSettingsClient = LocationServices.getSettingsClient(context);
        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

    }


//    private ObservableEmitter mEmitter;
//
//    public Observable<GoogleLocation> start() {
//        startLoction();
//        return Observable.create(new ObservableOnSubscribe<GoogleLocation>() {
//            @Override
//            public void subscribe(@NonNull ObservableEmitter<GoogleLocation> emitter) throws Throwable {
//                mEmitter = emitter;
//            }
//        });
//    }
//
//    public Observable<GoogleLocation> start(int delay) {
//        Log.d(TAG, "=======start=======" + delay);
//
//        startLoction();
//        return Observable.create(new ObservableOnSubscribe<GoogleLocation>() {
//            @Override
//            public void subscribe(@NonNull ObservableEmitter<GoogleLocation> emitter) throws Throwable {
//                mEmitter = emitter;
//                Disposable subscribe = Observable.timer(delay, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Throwable {
//                        emitter.onComplete();
//                    }
//                });
//            }
//        });
//    }

    private static final String TAG = "LocationHelper";

    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    /**
     * Constant used in the location settings dialog.
     */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient mSettingsClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Callback for Location events.
     */
    private LocationCallback mLocationCallback;

    /**
     * Represents a geographical location.
     */
    private Location mCurrentLocation;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    private String mLastUpdateTime;


    public void startLoction() {
        startLocationUpdates();
    }


    /**
     * Creates a callback for receiving location events.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                stopLocationUpdates();
                GoogleLocation googleLocation = new GoogleLocation();
                googleLocation.setGoogleLatitude(mCurrentLocation.getLatitude() + "");
                googleLocation.setGoogleLongitude(mCurrentLocation.getLongitude() + "");
                Log.d(TAG, mCurrentLocation.getLatitude() + "==============" + mCurrentLocation.getLongitude());
                saveLocalLocation(context, googleLocation);

                if (locationCallBack != null) {
                    locationCallBack.onLocationResult(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    locationCallBack.onCompleted();

                }
            }
        };
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
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }


    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private void startLocationUpdates() {
        //


        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
        } else {
            return;
        }
        Log.i(TAG, "Begin by checking if the device has the necessary location settings.");

        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(context, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");
                        Log.d(LOCAL_GOOGLE, "=======PERMISSION_GRANTED=====================");

                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions

                            return;
                        }
                        Log.d(TAG, "=======PERMISSION_GRANTED=====================");

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                    }
                })
                .addOnFailureListener(context, new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        if (locationCallBack != null) {
                            locationCallBack.onGoogleapisFaile("Location faile");
                        }

                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(context, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }

                    }
                });
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    public void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(context, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;

                    }
                });
    }


    public abstract static class LocationCallBack {
        public void onLocationResult(double latitude, double longitude) {

        }

        public void onGoogleapisFaile(String error) {

        }

        public void onCompleted() {

        }
    }


}
