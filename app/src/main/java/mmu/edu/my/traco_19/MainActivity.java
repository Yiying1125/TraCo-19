package mmu.edu.my.traco_19;



import android.content.Intent;
import android.os.Bundle;


import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private boolean doubleBackToExitPressedOnce = false;
    private Toast backPressToast;
    Version[] versions = {
            new Version("Location Service", "Track my location", R.drawable.tracklocationicon),
            new Version("Latest Update", "The latest news about Covid-19", R.drawable.updatenewsicon),
            new Version("Location History", "My location history", R.drawable.historyicon)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter myAdapter = new MyAdapter();
        myAdapter.addElements(versions);
        recyclerView.setAdapter(myAdapter);

    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        ArrayList<Version> elements = new ArrayList<Version>();

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View rowView = getLayoutInflater().inflate(R.layout.row, parent, false);
            return new MyViewHolder(rowView);
        }


        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
            holder.textView.setText(elements.get(i).getName());
            holder.textView2.setText(elements.get(i).getDescription());
            holder.imageView.setImageResource(elements.get(i).getIcon());
        }


        @Override
        public int getItemCount() {
            return elements.size();
        }

        public void addElements(Version[] versions) {
            elements.clear();
            elements.addAll(Arrays.asList(versions));
            notifyDataSetChanged();
        }

        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView textView;
            public TextView textView2;
            public ImageView imageView;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.track);
                textView2 = itemView.findViewById(R.id.trackldesc);
                imageView = itemView.findViewById(R.id.trackicon);
                itemView.setOnClickListener(this::onClick);
            }

            @Override
            public void onClick(View v) {
                String name = elements.get(getAdapterPosition()).getName();
                startActivity(new Intent(MainActivity.this, LatestUpdateActivity.class));
                finish();
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            backPressToast.cancel();
            super.onBackPressed();
            return;
        }
        doubleBackToExitPressedOnce = true;
        backPressToast = Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT);
        backPressToast.show();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}

