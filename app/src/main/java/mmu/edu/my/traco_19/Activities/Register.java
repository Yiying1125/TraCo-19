package mmu.edu.my.traco_19.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import maes.tech.intentanim.CustomIntent;
import mmu.edu.my.traco_19.R;


public class Register extends AppCompatActivity {

    //views
    LinearLayout linearLayout, forLogin;
    TextView enterTitle, forgotText;
    EditText nameE, emailE, passE, confirmPassE, focused;
    FrameLayout name, email, pass, confirmPass, forReg;
    ScrollView scrollView;
    ImageView backGround;
    ProgressBar progressBar;

    //vars
    FirebaseAuth fAuth;
    DatabaseReference ref;
    FirebaseFirestore fStore;
    DocumentReference noteRef;
    Context context = this;
    String userID = "";
    //    String AdminName = "", AdminPassword = "";
    long maxId = 1;
    //    int PERMISSION_ID = 44;
    boolean register = false, fail = false;
    public static final String TAG = "TAG";
    public static final String SHARED_PREFS = "sharedPrefs";

    public void Back(View view) {
        onBackPressed();
    }

    public void Forgot(View view) {
        Intent intent = new Intent(getBaseContext(), ForgotPassword.class);
        intent.putExtra("EmailAddress", emailE.getText().toString());
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void register(View view) {
        if (register) {
            final String name = nameE.getText().toString().trim();
            final String email = emailE.getText().toString().trim();
            String password = passE.getText().toString().trim();
            String ConfirmPassword = confirmPassE.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                nameE.setError("Name is Required.");
                return;
            }

            if (TextUtils.isEmpty(email)) {
                emailE.setError("Email address is Required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                passE.setError("Password is Required.");
                return;
            }

            if (password.length() < 6) {
                passE.setError("Password Must be >= 6 Characters");
                return;
            }

            if (password.compareTo(ConfirmPassword) != 0) {
                confirmPassE.setError("Password is not similar.");
                return;
            }
//
//            if (!checkPermissions()) {
//                requestPermissions();
//                Toast.makeText(context, "Please accept permissions first!", Toast.LENGTH_LONG).show();
//                return;
//            }

            progressBar.setVisibility(View.VISIBLE);

            // register the user in firebase

            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "User Created.", Toast.LENGTH_SHORT).show();
                    userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    Map<String, Object> user = new HashMap<>();
                    ref.child(String.valueOf(maxId + 1)).child("name").setValue(name);
                    ref.child(String.valueOf(maxId + 1)).child("email").setValue(email);
                    ref.child(String.valueOf(maxId + 1)).child("status").setValue("1");


                    user.put("Id", Long.toString(maxId + 1));
                    saveData(Long.toString(maxId + 1), "Id");

                    documentReference.set(user).addOnSuccessListener(aVoid -> {
                        fail = false;
                        Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                    }).addOnFailureListener(e -> {
                        Log.d(TAG, "onFailure: " + e.toString());
                        progressBar.setVisibility(View.GONE);
                        fail = true;
                    });
                    if (!fail) {
                        startActivity(new Intent(getApplicationContext(), UserDashboard.class));
                        finish();
                    }
                } else {
                    Toast.makeText(context, "Error ! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {

            final String email = emailE.getText().toString().trim();
            String password = passE.getText().toString().trim();


            if (TextUtils.isEmpty(email)) {
                emailE.setError("Email is Required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                passE.setError("Password is Required.");
                return;
            }

//            if (password.compareTo(AdminPassword) == 0 && email.compareTo(AdminName) == 0) {
//                startActivity(new Intent(getApplicationContext(), AdminDashboard.class));
//                finish();
//                return;
//            }

            if (password.length() < 6) {
                passE.setError("Password Must be >= 6 Characters");
                return;
            }

//            if (!checkPermissions()) {
//                requestPermissions();
//                Toast.makeText(context, "Please accept permissions first!", Toast.LENGTH_LONG).show();
//                return;
//            }

            progressBar.setVisibility(View.VISIBLE);

            // authenticate the user

            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    saveId();
                } else {
                    Toast.makeText(context, "Error ! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    public void Login(View view) {
        clearFocus();
        if (register) {
            name.setVisibility(View.GONE);
            confirmPass.setVisibility(View.GONE);
            forgotText.setVisibility(View.VISIBLE);
            emailE.setText("");
            passE.setText("");
            emailE.setError(null);
            passE.setError(null);
            emailE.clearFocus();
            passE.clearFocus();
            enterTitle.setText("LOGIN");
            linearLayout.setVisibility(View.VISIBLE);
            forReg.setVisibility(View.GONE);
            forLogin.setVisibility(View.VISIBLE);
            register = false;

        } else {
            name.setVisibility(View.VISIBLE);
            confirmPass.setVisibility(View.VISIBLE);
            forgotText.setVisibility(View.GONE);

            nameE.setText("");
            nameE.clearFocus();
            nameE.setError(null);

            emailE.setText("");
            emailE.clearFocus();
            emailE.setError(null);

            confirmPassE.setText("");
            confirmPassE.clearFocus();
            confirmPassE.setError(null);

            passE.setText("");
            passE.clearFocus();
            passE.setError(null);

            linearLayout.setVisibility(View.GONE);
            forReg.setVisibility(View.VISIBLE);
            forLogin.setVisibility(View.GONE);
            enterTitle.setText("CREATE");
            register = true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        boolean permissions = checkPermissions();

        iniFunc();

//        if (permissions) {
//        if (!loadData("log").isEmpty()) {
//            startActivity(new Intent(getApplicationContext(), AdminDashboard.class));
//            finish();
//        }
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), UserDashboard.class));
            finish();
        }
//        }
    }

//    public String loadData(String name) {
//        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//        if (sharedPreferences == null) {
//            return "";
//        }
//        return sharedPreferences.getString(name, "");
//    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void clearFocus() {
        if (focused != null) {
            focused.clearFocus();
            hideKeyboard(focused);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void iniFunc() {
        backGround = findViewById(R.id.backGround);
        scrollView = findViewById(R.id.scrole);
        forLogin = findViewById(R.id.forLoginTitle);
        forReg = findViewById(R.id.forRegisTiltle);
        enterTitle = findViewById(R.id.enterTitle);
        linearLayout = findViewById(R.id.forLogin);
        name = findViewById(R.id.Name);
        email = findViewById(R.id.email);
        forgotText = findViewById(R.id.forgotText);
        pass = findViewById(R.id.pass);
        confirmPass = findViewById(R.id.conPass);
        nameE = findViewById(R.id.NameE);
        passE = findViewById(R.id.password);
        emailE = findViewById(R.id.emailadress);
        confirmPassE = findViewById(R.id.confirmpassword);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);
        ref = FirebaseDatabase.getInstance().getReference().child("Member");

        scrollView.setOnTouchListener((v, event) -> {
            clearFocus();
            return false;
        });
        backGround.setOnTouchListener((v, event) -> {
            clearFocus();
            return false;
        });
        nameE.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                focused = nameE;
            }
        });
        emailE.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                focused = emailE;
            }
        });
        passE.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                focused = passE;
            }
        });
        confirmPassE.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                focused = confirmPassE;
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Member").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                maxId = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        FirebaseDatabase.getInstance().getReference().child("adminInfo").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.child("adminName").exists() && dataSnapshot.child("adminPass").exists()) {
//                    AdminName = Objects.requireNonNull(dataSnapshot.child("adminName").getValue()).toString();
//                    AdminPassword = Objects.requireNonNull(dataSnapshot.child("adminPass").getValue()).toString();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
    }

//    @SuppressLint("BatteryLife")
//    public void checkBackgroundPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            String packageName = context.getPackageName();
//            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//            if (pm != null && !pm.isIgnoringBatteryOptimizations(packageName)) {
//                Intent intent = new Intent();
//                intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
//                intent.setData(Uri.parse("package:" + packageName));
//                context.startActivity(intent);
//            }
//        }
//    }

//    @RequiresApi(api = Build.VERSION_CODES.Q)
//    private boolean checkPermissions() {
//        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
//    }

//    private void requestPermissions() {
//        if (Build.VERSION.SDK_INT >= 29) {
//
//            ActivityCompat.requestPermissions(
//                    this,
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
//                    PERMISSION_ID
//            );
//        } else {
//            ActivityCompat.requestPermissions(
//                    this,
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSION_ID
//            );
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_ID) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(context, "Permission accepted", Toast.LENGTH_SHORT).show();
//                checkBackgroundPermission();
//                if (fAuth.getCurrentUser() != null) {
//                    startActivity(new Intent(getApplicationContext(), UserDashboard.class));
//                    finish();
//                }
//            } else {
//                finish();
//            }
//        } else {
//            finish();
//        }
//    }


    public void saveData(String data, String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, data);
        editor.apply();
    }

    public void saveId() {
        String userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        noteRef = fStore.collection("users").document(userID);
        noteRef.addSnapshotListener(this, (documentSnapshot, e) -> {
            if (e != null) {
                Toast.makeText(context, "Error while loading!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());
                FirebaseAuth.getInstance().signOut();//logout
                progressBar.setVisibility(View.GONE);
                return;
            }

            assert documentSnapshot != null;
            if (documentSnapshot.exists()) {
                String Id = documentSnapshot.getString("Id");
                saveData(Id, "Id");
                Toast.makeText(context, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), UserDashboard.class));
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "left-to-right");
    }

    @Override
    public void onBackPressed() {
        if (register) {
            Login(null);
        }
    }

}