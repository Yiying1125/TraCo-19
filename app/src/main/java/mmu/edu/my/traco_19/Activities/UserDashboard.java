package mmu.edu.my.traco_19.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import maes.tech.intentanim.CustomIntent;
import mmu.edu.my.traco_19.Fragments.Dashboard;
import mmu.edu.my.traco_19.Fragments.LatestUpdates;
import mmu.edu.my.traco_19.Fragments.Profile;
import mmu.edu.my.traco_19.R;

import static android.widget.Toast.LENGTH_SHORT;
import static mmu.edu.my.traco_19.Activities.Register.SHARED_PREFS;

public class UserDashboard extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;
    Profile profile;
    UserDashboard userDashboard=this;
    Context context = this;
    public static BottomNavigationView bottomNav;


    public void LiveLocation(View view) {
    }

    public void LocationHistory(View view) {
    }

    public void ThemChanger(View view) {
    }

    public void logout(View view) {
        saveData("", "Id");
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(), Register.class));
        finish();
        CustomIntent.customType(this, "right-to-left");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_dashboard);


        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        profile = new Profile(loadData("Id"),context,userDashboard);

        if (savedInstanceState == null) {
            if (loadData("Selection").compareTo("0") == 0) {
                bottomNav.setSelectedItemId(R.id.profile);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        profile).commit();
            } else if (loadData("Selection").compareTo("1") == 0) {
                bottomNav.setSelectedItemId(R.id.home);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Dashboard()).commit();
            }else if (loadData("Selection").compareTo("2") == 0) {
                bottomNav.setSelectedItemId(R.id.LatestUpdates);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LatestUpdates()).commit();
            }  else {
                bottomNav.setSelectedItemId(R.id.profile);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        profile).commit();
            }
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
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
                                    new Dashboard()).commit();
                            break;
                        case R.id.LatestUpdates:
                            saveData("2", "Selection");
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new LatestUpdates()).commit();
                            break;
                    }
                    return true;
                }
            };
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
