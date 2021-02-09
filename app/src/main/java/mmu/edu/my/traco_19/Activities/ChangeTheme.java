package mmu.edu.my.traco_19.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.moondroid.coverflow.components.ui.containers.FeatureCoverFlow;
import mmu.edu.my.traco_19.Adapters.CoverFlowAdapter;
import mmu.edu.my.traco_19.Classes.Theme;
import mmu.edu.my.traco_19.R;

import static mmu.edu.my.traco_19.Activities.Register.SHARED_PREFS;

public class ChangeTheme extends AppCompatActivity {

    TextView pageNumber;
    Button back, next;

    private ArrayList<Theme> themes;
    private FeatureCoverFlow coverFlow;
    int currentPage = 0;

    public void back(View view) {
        onBackPressed();
    }

    public void save(View view) {
        saveData(Integer.toString(currentPage), "Theme");
        startActivity(new Intent(getApplicationContext(), UserDashboard.class));
        finish();
    }

    public void setTHeme(int pos) {
        int style = R.style.Theme_TraCo19;

        if (pos == 1) {
            style = R.style.Theme_TraCo191;
        } else if (pos == 2) {
            style = R.style.Theme_TraCo192;
        } else if (pos == 3) {
            style = R.style.Theme_TraCo193;
        } else if (pos == 4) {
            style = R.style.Theme_TraCo194;
        } else if (pos == 5) {
            style = R.style.Theme_TraCo195;
        } else if (pos == 6) {
            style = R.style.Theme_TraCo196;
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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTHeme(getInt(loadData("Theme")));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_theme);

        coverFlow = findViewById(R.id.coverflow);
        pageNumber = findViewById(R.id.pageNumber);
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);

        settingDummyData();
        CoverFlowAdapter adapter = new CoverFlowAdapter(this, themes);
        coverFlow.setAdapter(adapter);
        coverFlow.setOnScrollPositionListener(onScrollListener());
        coverFlow.scrollToPosition(getInt(loadData("Theme")));
        pageNumber.setText((currentPage + 1) + "/" + themes.size());
        back.setOnClickListener(v -> {
            currentPage--;
            if (currentPage < 0) {
                currentPage = themes.size() - 1;
            }
            pageNumber.setText((currentPage + 1) + "/" + themes.size());
            coverFlow.scrollToPosition(currentPage);
        });
        next.setOnClickListener(v -> {
            currentPage++;
            if (currentPage >= themes.size()) {
                currentPage = 0;
            }
            pageNumber.setText((currentPage + 1) + "/" + themes.size());
            coverFlow.scrollToPosition(currentPage);
        });
    }

    @SuppressLint("SetTextI18n")
    private FeatureCoverFlow.OnScrollPositionListener onScrollListener() {
        return new FeatureCoverFlow.OnScrollPositionListener() {
            @Override
            public void onScrolledToPosition(int position) {
                currentPage = position;
//                System.out.println("Scrolled to: " + currentPage);
                changeThemeTo(position);
                pageNumber.setText((currentPage + 1) + "/" + themes.size());
            }

            @Override
            public void onScrolling() {
                pageNumber.setText("-/" + themes.size());
            }
        };
    }

    public void changeThemeTo(int pos) {
        FrameLayout toolbar= findViewById(R.id.toolbar);
        ImageView backGround = findViewById(R.id.backGround);
        Button save = findViewById(R.id.save);
        int background = R.drawable.background;
        String colourPrimary = "#539AA5";

        if (pos == 1) {
            background = R.drawable.background1;
            colourPrimary = "#0A00FF";
        } else if (pos == 2) {
            background = R.drawable.background2;
            colourPrimary = "#FF00C8";
        } else if (pos == 3) {
            background = R.drawable.background3;
            colourPrimary = "#FF0000";
        } else if (pos == 4) {
            background = R.drawable.background4;
            colourPrimary = "#0BAC00";
        } else if (pos == 5) {
            background = R.drawable.background5;
            colourPrimary = "#D55500";
        } else if (pos == 6) {
            background = R.drawable.background6;
            colourPrimary = "#6400BC";
        }

        back.setTextColor(Color.parseColor(colourPrimary));
        next.setTextColor(Color.parseColor(colourPrimary));

        save.setBackgroundColor(Color.parseColor(colourPrimary));
        toolbar.setBackgroundColor(Color.parseColor(colourPrimary));
        backGround.setBackgroundResource(background);


    }

    private void settingDummyData() {
        themes = new ArrayList<>();
        themes.add(new Theme(R.drawable.them, "Cyan"));
        themes.add(new Theme(R.drawable.theme1, "Blue"));
        themes.add(new Theme(R.drawable.theme2, "Pink"));
        themes.add(new Theme(R.drawable.theme3, "Red"));
        themes.add(new Theme(R.drawable.theme4, "Green"));
        themes.add(new Theme(R.drawable.theme5, "Orange"));
        themes.add(new Theme(R.drawable.theme6, "Purple"));
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
        startActivity(new Intent(getApplicationContext(), UserDashboard.class));
        finish();
    }
}