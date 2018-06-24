package com.example.sunrin.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.sunrin.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PhotoListAdapter extends BaseAdapter{

    private ArrayList<String> bitmaps;

    public PhotoListAdapter(ArrayList<String> bitmapsdata) {
        this.bitmaps = bitmapsdata;
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
            convertView = inflater.inflate(R.layout.photo_list_custom_grid, parent, false);
        }

        Log.e("data", bitmaps.toString());

        ImageView imageView = convertView.findViewById(R.id.photo_full_image);

//        Glide.with(convertView).load(bitmaps.get(position)).into(imageView);
        Picasso
                .with(context)
                .load(bitmaps.get(position))
                .fit() // will explain later
                .into((ImageView) imageView);

        return convertView;
    }
}
