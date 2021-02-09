package mmu.edu.my.traco_19.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import mmu.edu.my.traco_19.Classes.Theme;
import mmu.edu.my.traco_19.R;

public class CoverFlowAdapter extends BaseAdapter {

    private ArrayList<Theme> data;
    private Context activity;

    public CoverFlowAdapter(Context context, ArrayList<Theme> objects) {
        this.activity = context;
        this.data = objects;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Theme getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_flow, null, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.gameImage.setImageResource(data.get(position).getImageSource());
        viewHolder.gameName.setText(data.get(position).getName());

//        convertView.setOnClickListener(onClickListener(position));

        return convertView;
    }

//    private View.OnClickListener onClickListener(final int position) {
//        return v -> {
//            final Dialog dialog = new Dialog(activity);
//            dialog.setContentView(R.layout.dialog_game_info);
//            dialog.setCancelable(true); // dimiss when touching outside
//            dialog.setTitle("Game Details");
//
//            TextView text = (TextView) dialog.findViewById(R.id.name);
//            text.setText(getItem(position).getName());
//            ImageView image = (ImageView) dialog.findViewById(R.id.image);
//            image.setImageResource(getItem(position).getImageSource());
//
//            dialog.show();
//        };
//    }


    private static class ViewHolder {
        private TextView gameName;
        private ImageView gameImage;

        public ViewHolder(View v) {
            gameImage = v.findViewById(R.id.image);
            gameName = v.findViewById(R.id.name);
        }
    }
}