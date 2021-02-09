package mmu.edu.my.traco_19.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import mmu.edu.my.traco_19.BroadCasts.NotificationBroadcastReceiver;
import mmu.edu.my.traco_19.R;

import com.google.android.gms.location.LocationListener;


public class LocationService extends Service {

    Context context = this;
    String NewLoc = "";
    String Status = "1";
    Loc loc = new Loc();
    Location mCurrentLocation;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    boolean pushed = true;
    long INTERVAL = 500000;
    long FASTEST_INTERVAL = 500000;
    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createLocationRequest();
        FireBaseListener();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel serviceChannel = new NotificationChannel(
                    "serviceNotificationChannelId",
                    "Location tracker service",
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (notifManager != null) {
                notifManager.createNotificationChannel(serviceChannel);
            }

            Intent hidingIntent = new Intent(this, NotificationBroadcastReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    1,
                    hidingIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            Notification notification = new Notification.Builder(this, "serviceNotificationChannelId")
                    .setContentTitle("Location tracker service")
                    .setContentText("Service is running")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .getNotification();
            startForeground(1, notification);
        } else {
            Notification.Builder builder = new Notification.Builder(this);
            this.startForeground(-1, builder.getNotification());
            stopSelf();
        }
        return START_NOT_STICKY;
    }


    public void push() {
        if (!pushed && mCurrentLocation != null) {
            final String Id = loadData("Id");
            NewLoc = mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude() + "," + Status;
            if (!Id.isEmpty() && !Status.isEmpty()) {
                long milliseconds = System.currentTimeMillis();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd");
                Date resultDate = new Date(milliseconds);
                if (IsNetworkEnabled()) {
                    FirebaseDatabase.getInstance().getReference().child("Member/" + Id + "/Location History/" + sdf.format(resultDate) + "/" + (milliseconds / 1000)).setValue(NewLoc);
                    String currLoc = mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude() + "," + Status + "," + milliseconds / 1000L;
                    if(!loadData("showLocation").isEmpty()) {
                        FirebaseDatabase.getInstance().getReference().child("Member/" + Id + "/CurrLocation").setValue(currLoc);
                    }
                }
                pushed = true;
            }
        }
    }

    public void FireBaseListener() {
        FirebaseDatabase.getInstance().getReference().child("Member").child(loadData("Id")).child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    status(Objects.requireNonNull(dataSnapshot.getValue()).toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        FirebaseDatabase.getInstance().getReference().child("LocationTrigger").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loc.create();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public void status(String status) {
        try {
            if (Integer.parseInt(status) == 1) {
                Status = "1";
            }
            if (Integer.parseInt(status) == 2) {
                Status = "2";
            }
            if (Integer.parseInt(status) == 3) {
                Status = "3";
            }
        } catch (Exception ignored) {
        }
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(loc)
                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class Loc implements LocationListener, GoogleApiClient.ConnectionCallbacks {
        public void create() {

            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
            pushed = false;
        }

        @Override
        public void onConnected(Bundle bundle) {
            startLocationUpdates();
        }

        @SuppressLint("MissingPermission")
        public void startLocationUpdates() {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this, Looper.getMainLooper());
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
        }

        @Override
        public void onLocationChanged(Location location) {
            mCurrentLocation = location;
            mGoogleApiClient.disconnect();
            push();
        }
    }

    public boolean IsNetworkEnabled() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //we are connected to a network
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).

                getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).

                        getState() == NetworkInfo.State.CONNECTED;
    }

    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }

}