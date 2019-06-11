package com.example.shen.ecard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class MyAdapter extends ArrayAdapter<ListItem> {
    public MyAdapter(Context context, ArrayList<ListItem> listitems) {
        super(context,0,listitems);
    }
    private LayoutInflater layoutInflater;
    ImageView imageView;
    TextView tv1;
    TextView tv2;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItem listitem=getItem(position);
        if (convertView == null) {
            convertView = layoutInflater.from(getContext()).inflate(R.layout.list, parent,false);
        }
        imageView=(ImageView)convertView.findViewById(R.id.logo);
        tv1 = (TextView) convertView.findViewById(R.id.tv1);
        tv2 = (TextView) convertView.findViewById(R.id.tv2);
        switch (listitem.getCompany()){
            case "Woolworths":
                imageView.setImageResource(R.drawable.woolworths);
                break;
            case "Coles":
                imageView.setImageResource(R.drawable.flybuys);
                break;
        }
        tv1.setText(listitem.getNumber());
        tv2.setText(listitem.getCompany());
        return convertView;
    }
}
