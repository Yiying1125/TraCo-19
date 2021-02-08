package mmu.edu.my.traco_19.Activities;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import mmu.edu.my.traco_19.R;

import static mmu.edu.my.traco_19.Activities.Register.SHARED_PREFS;

public class ForgotPassword extends AppCompatActivity {

    Context context = this;
    EditText emailAddress;
    ProgressBar progressBar;
    int background = R.drawable.background;

    public void onback(View view) {
        onBackPressed();
    }

    public void SendEmail(View view) {
        final String emailString = emailAddress.getText().toString().trim();

        if (TextUtils.isEmpty(emailString)) {
            emailAddress.setError("Email address is Required.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().sendPasswordResetEmail(emailString).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "A resent link has been sent to your email successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
            }
            progressBar.setVisibility(View.GONE);
        });
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
        setContentView(R.layout.user_activity_forgot_password);

        ImageView Background = findViewById(R.id.scrollView2);
        Background.setBackgroundResource(background);
        emailAddress = findViewById(R.id.emailAddress);
        emailAddress.setText(getIntent().getStringExtra("EmailAddress"));
        progressBar = findViewById(R.id.progressBar);
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
        super.onBackPressed();
    }
}