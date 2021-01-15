package mmu.edu.my.traco_19.Activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import mmu.edu.my.traco_19.R;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;
import static mmu.edu.my.traco_19.Activities.Register.SHARED_PREFS;

public class mapLiveLocation extends AppCompatActivity implements OnMapReadyCallback {

    //views
    GoogleMap googleMap;
    ImageView selectedImage;
    ProgressBar progressBar;
    RelativeLayout UserDetails;
    FrameLayout backgroundScreen;
    TextView UserName, UserID, lastSeen, mode;
    ImageButton Button;

    //vars
    Timer timer;
    String profileId;
    ArrayList<Marker> allUsersMarkers = new ArrayList<>();
    GoogleApiClient mGoogleApiClient;
    ArrayList<Integer> statusForAll = new ArrayList<>();
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    static int refreshRate = 1000;
    int idChosen, max = 0;
    boolean cameraMoved = false, slide = false, following = false;

    public void back(View view) {
        onBackPressed();
    }

    public void gps(View view) {
        if (!following) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(allUsersMarkers.get(idChosen).getPosition(), googleMap.getCameraPosition().zoom));
            Button.setImageResource(R.drawable.ic_gps_fixed);
            following = true;
        } else {
            Button.setImageResource(R.drawable.ic_gps_not_fixed_black_24dp);
            following = false;
        }
    }

    public void empty(View view) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_live_location);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                profileId = null;
            } else {
                profileId = extras.getString("UserId");
            }
        } else {
            profileId = (String) savedInstanceState.getSerializable("UserId");
        }
        initVar();
        initMap();
    }

    public void startLiveListening() {
        timer = new Timer();
        getUsersLocations();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                getUsersLocations();
            }
        };
        timer.schedule(timerTask, 5000, refreshRate);
    }

    public void getUsersLocations() {
        FirebaseDatabase.getInstance().getReference().child("Member").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (profileId != null) {
                    cameraMoved = true;
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if ((int) dataSnapshot.getChildrenCount() > max) {
                        if (googleMap != null) {
                            googleMap.clear();
                        }
                        allUsersMarkers.clear();
                        statusForAll.clear();
                        for (int i = 0; i <= (int) dataSnapshot.getChildrenCount(); i++) {
                            allUsersMarkers.add(null);
                            statusForAll.add(null);
                        }
                    }
                    max = (int) dataSnapshot.getChildrenCount();
                    if (ds.getKey() != null) {
                        Object object = dataSnapshot.child(ds.getKey()).child("CurrLocation").getValue();
                        if (object != null && dataSnapshot.child(ds.getKey()).child("status").exists()) {
                            LatLng loc = new LatLng(getWithIndex(object, 0, 0), getWithIndex(object, 1, 0));
                            addMarker(getInt(Objects.requireNonNull(dataSnapshot.child(ds.getKey()).child("status").getValue()).toString()), loc, getInt(ds.getKey()));
                            if (!cameraMoved) {
                                builder.include(loc);
                            }
                        }
                    }
                }

                if (!cameraMoved) {
                    try {
                        LatLngBounds bounds = builder.build();
                        int width = getResources().getDisplayMetrics().widthPixels;
                        int height = getResources().getDisplayMetrics().heightPixels;
                        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                        googleMap.animateCamera(cu);
                        cameraMoved = true;
                    }catch (Exception ignored){
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(2.944490, 101.602753))
                                .zoom(10).build()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public double getWithIndex(Object s, int index, double defualt) {
        try {
            String[] arr = s.toString().split(",", -1);
            return Double.parseDouble(arr[index]);
        } catch (Exception ignored) {
            return defualt;
        }
    }

    public int getInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception ignored) {
            return -1;
        }
    }

    public void addMarker(int status, LatLng loc, int id) {
        if (max >= id && id >= 0) {
            String DrawableFile = "green_user";

            if (status == 2) {
                DrawableFile = "yellow_user";
            } else if (status == 3) {
                DrawableFile = "red_user";
            }

            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(resizeBitmap(DrawableFile, 130, 130));

            if (allUsersMarkers.get(id) == null || statusForAll.get(id) == null) {
                statusForAll.set(id, status);
                if (getInt(loadData("Id")) == id) {
                    allUsersMarkers.set(id, googleMap.addMarker(new MarkerOptions()
                            .icon(icon)
                            .position(loc)
                            .title("You")));
                } else {
                    allUsersMarkers.set(id, googleMap.addMarker(new MarkerOptions()
                            .icon(icon)
                            .position(loc)
                            .title(Integer.toString(id))));
                }
            } else {
                if (statusForAll.get(id) != status) {
                    allUsersMarkers.get(id).remove();

                    if (getInt(loadData("Id")) == id) {
                        allUsersMarkers.set(id, googleMap.addMarker(new MarkerOptions()
                                .icon(icon)
                                .position(loc)
                                .title("You")));
                    } else {
                        allUsersMarkers.set(id, googleMap.addMarker(new MarkerOptions()
                                .icon(icon)
                                .position(loc)
                                .title(Integer.toString(id))));
                    }
                    statusForAll.set(id, status);
                }
                if (allUsersMarkers.get(id).getPosition() != loc) {
                    MarkerAnimation.animateMarkerToGB(allUsersMarkers.get(id), loc, new LatLngInterpolator.Spherical());
                }
            }
            if (id == idChosen) {
                if (slide) {
                    if (following) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(allUsersMarkers.get(id).getPosition(), googleMap.getCameraPosition().zoom));
                    }
                    updateDetails();
                }
            } else if (profileId != null && id == Integer.parseInt(profileId)) {
                idChosen = Integer.parseInt(profileId);
                updateDetails();
                animationInUP(UserDetails);
                slide = true;
                cameraMoved = true;
                profileId = null;
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(allUsersMarkers.get(id).getPosition(), 16)
                        , new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {
                                following = true;
                                Button.setImageResource(R.drawable.ic_gps_fixed);
                            }

                            @Override
                            public void onCancel() {

                            }
                        }
                );

            }
        }
    }


    public Bitmap resizeBitmap(String drawableName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(drawableName, "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }

    public String TimeConverter(long unixSeconds) {
        Date date = new java.util.Date(unixSeconds * 1000L);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
        return sdf.format(date);
    }


//    @Override
//    public void finish() {
//        super.finish();
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//        timer.cancel();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initVar() {
        progressBar = findViewById(R.id.progressBar);
        UserDetails = findViewById(R.id.UserDetails);
        UserName = findViewById(R.id.UserName);
        UserID = findViewById(R.id.UserID);
        lastSeen = findViewById(R.id.Time);
        mode = findViewById(R.id.status);
        selectedImage = findViewById(R.id.profilePicture);
        Button = findViewById(R.id.Button);
        backgroundScreen = findViewById(R.id.backgroundScreen);
        backgroundScreen.setOnTouchListener((v, event) -> {
            Button.setImageResource(R.drawable.ic_gps_not_fixed_black_24dp);
            following = false;
            return false;
        });
        animationOutDOWN0(UserDetails);
    }

    public void initMap() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapTracker);
        supportMapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onMapReady(GoogleMap Map) {
        googleMap = Map;
        googleMap.setOnMarkerClickListener(marker -> {
            try {
                idChosen = Integer.parseInt(marker.getTitle());
            }catch (Exception ignored){
                idChosen = getInt(loadData("Id"));
            }
            updateDetails();
            animationInUP(UserDetails);
            slide = true;
            return false;
        });
        googleMap.setOnMapClickListener(latLng -> {
            if (slide) {
                animationOutDOWN(UserDetails);
                slide = false;
                Button.setImageResource(R.drawable.ic_gps_not_fixed_black_24dp);
                following = false;
            }
        });
        startLiveListening();
    }

    public void updateDetails() {
        getPic(Integer.toString(idChosen));

        FirebaseDatabase.getInstance().getReference().child("Member").child(Integer.toString(idChosen)).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isOnline = false;
                if (dataSnapshot.child("name").exists()) {
                    UserName.setText(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                }
                if (dataSnapshot.child("CurrLocation").exists()) {
                    if ((System.currentTimeMillis() - (getWithIndex(dataSnapshot.child("CurrLocation").getValue(), 3, 0) * 1000L)) < 180000) {
                        isOnline = true;
                    }
                }
                if (isOnline) {
                    mode.setText("online");
                    mode.setTextColor(Color.GREEN);
                } else {
                    mode.setText("offline");
                    mode.setTextColor(Color.RED);
                }
                UserID.setText(Integer.toString(idChosen));

                if (dataSnapshot.child("CurrLocation").exists()) {
                    lastSeen.setText(TimeConverter((long) getWithIndex(dataSnapshot.child("CurrLocation").getValue(), 3, 0)));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getPic(String id) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("uploads").child(id);
        try {
            final File localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> selectedImage.setImageURI(Uri.fromFile(localFile))).addOnFailureListener(exception -> selectedImage.setImageResource(R.drawable.avatar));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static class MarkerAnimation {
        public static void animateMarkerToGB(final Marker marker, final LatLng finalPosition, final LatLngInterpolator latLngInterpolator) {
            final LatLng startPosition = marker.getPosition();
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final Interpolator interpolator = new AccelerateDecelerateInterpolator();
            final float durationInMs = refreshRate - 100;
            handler.post(new Runnable() {
                long elapsed;
                float t;
                float v;

                @Override
                public void run() {
                    // Calculate progress using interpolator
                    elapsed = SystemClock.uptimeMillis() - start;
                    t = elapsed / durationInMs;
                    v = interpolator.getInterpolation(t);
                    marker.setPosition(latLngInterpolator.interpolate(v, startPosition, finalPosition));
                    // Repeat till progress is complete.
                    if (t < 1) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    }
                }
            });
        }
    }

    public interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class Spherical implements LatLngInterpolator {
            /* From github.com/googlemaps/android-maps-utils */
            @Override
            public LatLng interpolate(float fraction, LatLng from, LatLng to) {
                // http://en.wikipedia.org/wiki/Slerp
                double fromLat = toRadians(from.latitude);
                double fromLng = toRadians(from.longitude);
                double toLat = toRadians(to.latitude);
                double toLng = toRadians(to.longitude);
                double cosFromLat = cos(fromLat);
                double cosToLat = cos(toLat);
                // Computes Spherical interpolation coefficients.
                double angle = computeAngleBetween(fromLat, fromLng, toLat, toLng);
                double sinAngle = sin(angle);
                if (sinAngle < 1E-6) {
                    return from;
                }
                double a = sin((1 - fraction) * angle) / sinAngle;
                double b = sin(fraction * angle) / sinAngle;
                // Converts from polar to vector and interpolate.
                double x = a * cosFromLat * cos(fromLng) + b * cosToLat * cos(toLng);
                double y = a * cosFromLat * sin(fromLng) + b * cosToLat * sin(toLng);
                double z = a * sin(fromLat) + b * sin(toLat);
                // Converts interpolated vector back to polar.
                double lat = atan2(z, sqrt(x * x + y * y));
                double lng = atan2(y, x);
                return new LatLng(toDegrees(lat), toDegrees(lng));
            }

            private double computeAngleBetween(double fromLat, double fromLng, double toLat, double toLng) {
                // Haversine's formula
                double dLat = fromLat - toLat;
                double dLng = fromLng - toLng;
                return 2 * asin(sqrt(pow(sin(dLat / 2), 2) +
                        cos(fromLat) * cos(toLat) * pow(sin(dLng / 2), 2)));
            }
        }

    }

    public void animationInUP(View view) {
        Animation inFromBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromBottom.setDuration(500);
        inFromBottom.setInterpolator(new AccelerateInterpolator());
        inFromBottom.setFillAfter(true);
        view.startAnimation(inFromBottom);
    }

    private void animationOutDOWN(View view) {
        Animation outtoBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f);
        outtoBottom.setDuration(500);
        outtoBottom.setInterpolator(new AccelerateInterpolator());
        outtoBottom.setFillAfter(true);
        view.startAnimation(outtoBottom);
    }

    private void animationOutDOWN0(View view) {
        Animation outtoBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f);
        outtoBottom.setDuration(0);
        outtoBottom.setInterpolator(new AccelerateInterpolator());
        outtoBottom.setFillAfter(true);
        view.startAnimation(outtoBottom);
    }

    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }

    @Override
    public void onBackPressed() {
        if (slide) {
            animationOutDOWN(UserDetails);
            slide = false;
        } else {
            super.onBackPressed();
        }
    }
}
