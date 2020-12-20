package mmu.edu.my.traco_19;



import android.os.Bundle;


import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
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

        class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView textView;
            public TextView textView2;
            public ImageView imageView;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.track);
                textView2 = itemView.findViewById(R.id.trackldesc);
                imageView = itemView.findViewById(R.id.trackicon);
            }
        }
    }
}

