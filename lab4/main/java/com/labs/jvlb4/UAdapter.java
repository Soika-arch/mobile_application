package com.labs.jvlb4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Map;

class UAdapter extends ArrayAdapter<Map<String,String>> {
    private final boolean _isgrid;

    UAdapter(Context context, ArrayList<Map<String, String>> User, boolean grid) {
        super(context, 0, User);

        _isgrid = grid;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Map<String,String> user = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(_isgrid? R.layout.grid_item:R.layout.list_item,parent,false);
        }
        TextView Name = (TextView) convertView.findViewById(R.id.name);
        TextView City = (TextView) convertView.findViewById(R.id.city);
        Name.setText(user.get("name"));
        City.setText(user.get("city"));

        return convertView;
    }
}
