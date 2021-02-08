package mmu.edu.my.traco_19.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import maes.tech.intentanim.CustomIntent;
import mmu.edu.my.traco_19.Fragments.Dashboard;
import mmu.edu.my.traco_19.Fragments.Profile;
import mmu.edu.my.traco_19.LatestUpdateActivity;
import mmu.edu.my.traco_19.R;
import mmu.edu.my.traco_19.Services.LocationService;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static mmu.edu.my.traco_19.Activities.Register.SHARED_PREFS;

public class UserDashboard extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;
    Profile profile;
    UserDashboard userDashboard = this;
    Context context = this;
    public static BottomNavigationView bottomNav;
    int PERMISSION_ID = 44;
    int background = R.drawable.background;
    Switch switchService;

    public void LatestUpdates(View view){
        startActivity(new Intent(getApplicationContext(), LatestUpdateActivity.class));
    }

    public void addPic(View view) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(UserDashboard.this);
    }

    public void currentLocation(View view) {
        Intent i = new Intent(getApplicationContext(), mapLiveLocation.class);
        String UserId = loadData("Id");
        i.putExtra("UserId", UserId);
        startActivity(i);
    }

    public void LiveLocation(View view) {
        startActivity(new Intent(getApplicationContext(), mapLiveLocation.class));
    }

    public void LocationHistory(View view) {
        startActivity(new Intent(getApplicationContext(), LocationHistory.class));
    }

    public void ThemChanger(View view) {
        startActivity(new Intent(getApplicationContext(), ChangeTheme.class));
        finish();
    }

    public void logout(View view) {
        saveData("", "Id");
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(), Register.class));
        finish();
        CustomIntent.customType(this, "right-to-left");
    }

    public void setTHeme(int pos) {
        int style = R.style.Theme_TraCo19;

        if (pos == 1) {
            style = R.style.Theme_TraCo191;
            background = R.drawable.background1;
        } else if (pos == 2) {
            style = R.style.Theme_TraCo192;
            background = R.drawable.background2;
        } else if (pos == 3) {
            style = R.style.Theme_TraCo193;
            background = R.drawable.background3;
        } else if (pos == 4) {
            style = R.style.Theme_TraCo194;
            background = R.drawable.background4;
        } else if (pos == 5) {
            style = R.style.Theme_TraCo195;
            background = R.drawable.background5;
        } else if (pos == 6) {
            style = R.style.Theme_TraCo196;
            background = R.drawable.background6;
        }
        setTheme(style);
    }

    public int getInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception ignored) {
            return 0;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTHeme(getInt(loadData("Theme")));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_dashboard);

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        profile = new Profile(loadData("Id"), context, userDashboard, background);

        if (savedInstanceState == null) {
            if (loadData("Selection").compareTo("0") == 0) {
                bottomNav.setSelectedItemId(R.id.profile);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        profile).commit();
            } else if (loadData("Selection").compareTo("1") == 0) {
                bottomNav.setSelectedItemId(R.id.home);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Dashboard(background)).commit();
            } else {
                bottomNav.setSelectedItemId(R.id.profile);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        profile).commit();
            }
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.profile:
                            saveData("0", "Selection");
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    profile).commit();
                            break;
                        case R.id.home:
                            saveData("1", "Selection");
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new Dashboard(background)).commit();
                            break;
//                        case R.id.LatestUpdates:
//                            saveData("2", "Selection");
//                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                                    new LatestUpdates()).commit();
//                            break;
                    }
                    return true;
                }
            };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                profile.setURI(result.getUri());
                uploadProfilePic(result.getUri());
            }
        }
    }

    private void uploadProfilePic(Uri mImageUri) {
        if (mImageUri != null) {
            StorageReference fileReference = FirebaseStorage.getInstance().getReference("uploads").child(loadData("Id"));
            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(taskSnapshot -> Toast.makeText(context, "Profile picture has been uploaded successfully", LENGTH_LONG).show()).addOnFailureListener(exception -> Toast.makeText(context, "Error while uploading profile picture", LENGTH_SHORT).show());
        }
    }

    public void DownloadProfilePic(ImageView pic) {
        try {
            FirebaseStorage.getInstance().getReference("uploads").child(loadData("Id")).getDownloadUrl().addOnSuccessListener(downloadUrl -> Picasso.get()
                    .load(downloadUrl)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .fit()
                    .into(pic));
        } catch (Exception ignored) {

        }
    }

    public void showLocation(Boolean showLocation) {
        if (showLocation) {
            saveData("1", "showLocation");
        } else {
            saveData("", "showLocation");
            FirebaseDatabase.getInstance().getReference().child("Member/" + loadData("Id") + "/CurrLocation").removeValue();
        }
    }

    public boolean getShowLocation() {
        return loadData("showLocation").compareTo("1") == 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void turnOnService(Boolean runService, Switch switchService) {
        this.switchService = switchService;
        if (runService && !isMyServiceRunning()) {
            if (checkPermissions()) {
                ContextCompat.startForegroundService(this, new Intent(this, LocationService.class));
            } else {
                requestPermissions();
            }
        } else if (!runService && isMyServiceRunning()) {
            stopService(new Intent(this, LocationService.class));
        }
        switchService.setChecked(isMyServiceRunning());
    }

    public boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @SuppressLint("BatteryLife")
    public void checkBackgroundPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (pm != null && !pm.isIgnoringBatteryOptimizations(packageName)) {
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + packageName));
                context.startActivity(intent);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= 29) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    PERMISSION_ID
            );
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ID
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Permission accepted", Toast.LENGTH_SHORT).show();
                checkBackgroundPermission();
                ContextCompat.startForegroundService(this, new Intent(this, LocationService.class));
                if (switchService != null) {
                    switchService.setChecked(isMyServiceRunning());
                }
            }
        }
    }

    public void saveData(String data, String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, data);
        editor.apply();
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
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
}
