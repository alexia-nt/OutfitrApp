package com.example.outfitrapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class MyAdapter extends BaseAdapter {

    private ArrayList<DataClass> daraList;
    private Context context;
    LayoutInflater layoutInflater;
    public MyAdapter(ArrayList<DataClass> daraList, Context context) {
        this.daraList = daraList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return daraList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(layoutInflater==null){
            layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }
        if(view==null){
            view=layoutInflater.inflate(R.layout.grid_item,null);
        }
        /* ImageView gridDelete=view.findViewById(R.id.deleteView);*/
        ImageView gridImage=view.findViewById(R.id.gridImage);
        TextView gridCaption=view.findViewById(R.id.gridCaption);

        Glide.with(context).load(daraList.get(position).getImageURL()).into(gridImage);
        gridCaption.setText(daraList.get(position).getCaption());

        return view;
    }
}

//this is a test
