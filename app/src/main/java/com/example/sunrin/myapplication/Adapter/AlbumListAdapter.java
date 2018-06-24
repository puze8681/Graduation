package com.example.sunrin.myapplication.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sunrin.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AlbumListAdapter extends BaseAdapter{

    private ArrayList<String> bitmaps;
    private ArrayList<String> names;

    public AlbumListAdapter(ArrayList<String> bitmapsdata, ArrayList<String> namesdata) {
        this.bitmaps = bitmapsdata;
        this.names = namesdata;
    }

    @Override
    public int getCount() {
        return bitmaps.size();
    }

    @Override
    public Object getItem(int position) {
        return bitmaps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
            convertView = inflater.inflate(R.layout.album_list_custom_grid, parent, false);
        }

        Log.e("data", bitmaps.toString());

        TextView textView = convertView.findViewById(R.id.text_album_name);
        ImageView imageView = convertView.findViewById(R.id.full_image);

//        Glide.with(convertView).load(bitmaps.get(position)).into(imageView);
        Picasso
                .with(context)
                .load(bitmaps.get(position))
                .fit() // will explain later
                .into((ImageView) imageView);
        textView.setText(names.get(position).toString());

        return convertView;
    }
}
