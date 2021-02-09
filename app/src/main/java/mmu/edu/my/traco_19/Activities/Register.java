package mmu.edu.my.traco_19.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
    LinearLayout linearLayout, forLogin, Container;
    TextView enterTitle, forgotText, RegistrationTitle;
    EditText nameE, emailE, passE, confirmPassE, focused;
    FrameLayout name, email, pass, confirmPass, forReg;
    ScrollView scrollView;
    ImageView backGround, logoImg;
    ProgressBar progressBar;

    //vars
    FirebaseAuth fAuth;
    DatabaseReference ref;
    FirebaseFirestore fStore;
    DocumentReference noteRef;
    Context context = this;

    public static final int STARTUP_DELAY = 300;
    public static final int ANIM_ITEM_DURATION = 1000;
    public static final int EDITTEXT_DELAY = 300;
    public static final int BUTTON_DELAY = 300;
    public static final int VIEW_DELAY = 400;

    int background = R.drawable.background;
    int logo = R.drawable.logo;

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


    public void setTHeme(int pos) {
        int style = R.style.Theme_TraCo19;

        if (pos == 1) {
            style = R.style.Theme_TraCo191;
            background = R.drawable.background1;
            logo = R.drawable.logo1;
        } else if (pos == 2) {
            style = R.style.Theme_TraCo192;
            background = R.drawable.background2;
            logo = R.drawable.logo2;
        } else if (pos == 3) {
            style = R.style.Theme_TraCo193;
            background = R.drawable.background3;
            logo = R.drawable.logo3;
        } else if (pos == 4) {
            style = R.style.Theme_TraCo194;
            background = R.drawable.background4;
            logo = R.drawable.logo4;
        } else if (pos == 5) {
            style = R.style.Theme_TraCo195;
            background = R.drawable.background5;
            logo = R.drawable.logo5;
        } else if (pos == 6) {
            style = R.style.Theme_TraCo196;
            background = R.drawable.background6;
            logo = R.drawable.logo6;
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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTHeme(getInt(loadData("Theme")));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        boolean permissions = checkPermissions();
        iniFunc();
        animationFunc();

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
        RegistrationTitle = findViewById(R.id.RegistrationTitle);
        logoImg = findViewById(R.id.logoImg);
        logoImg.setBackgroundResource(logo);
        Container = findViewById(R.id.Container);
        backGround = findViewById(R.id.backGround);
        backGround.setBackgroundResource(background);
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
    }

    public void animationFunc() {
        logoImg.post(() -> {
            Animation animation2 = new TranslateAnimation(0, 0, logoImg.getHeight(), 0);
            animation2.setStartTime(STARTUP_DELAY);
            animation2.setDuration(ANIM_ITEM_DURATION);
            animation2.setInterpolator(new DecelerateInterpolator(1.2f));
            animation2.setFillAfter(true);
            logoImg.startAnimation(animation2);
        });
        RegistrationTitle.post(() -> {
            Animation animation2 = new TranslateAnimation(0, 0, -RegistrationTitle.getHeight(), 0);
            animation2.setStartTime(STARTUP_DELAY);
            animation2.setDuration(ANIM_ITEM_DURATION);
            animation2.setInterpolator(new DecelerateInterpolator(1.2f));
            animation2.setFillAfter(true);
            RegistrationTitle.startAnimation(animation2);
        });

        for (int i = 0; i < Container.getChildCount(); i++) {
            View v = Container.getChildAt(i);
            ViewPropertyAnimatorCompat viewAnimator;

            if (v instanceof FrameLayout) {
                viewAnimator = ViewCompat.animate(v)
                        .scaleY(1).scaleX(1)
                        .setStartDelay((EDITTEXT_DELAY * i) + 500)
                        .setDuration(500);
            } else if (v instanceof RelativeLayout) {
                viewAnimator = ViewCompat.animate(v)
                        .scaleY(1).scaleX(1)
                        .setStartDelay((BUTTON_DELAY * i) + 500)
                        .setDuration(500);
            } else {
                viewAnimator = ViewCompat.animate(v)
                        .translationY(50).alpha(1)
                        .setStartDelay((VIEW_DELAY * i) + 500)
                        .setDuration(1000);
            }

            viewAnimator.setInterpolator(new DecelerateInterpolator()).start();
        }


    }

    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }

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

    public int dpToPx(int dip) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        return (int) px;
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