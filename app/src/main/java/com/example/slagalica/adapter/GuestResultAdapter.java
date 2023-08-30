package com.example.slagalica.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.slagalica.R;
import com.example.slagalica.model.GuestResult;

import java.util.List;

public class GuestResultAdapter extends BaseAdapter {

    private Context context;
    private List<GuestResult> resultList;
    private LayoutInflater inflater;

    public GuestResultAdapter(Context context, List<GuestResult> resultList) {
        this.context = context;
        this.resultList = resultList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public Object getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_view_layout, parent, false);
        }

        GuestResult resultGuest = resultList.get(position);

        TextView nicknameTextView = convertView.findViewById(R.id.nicknameTextView);
        TextView bodoviTextView = convertView.findViewById(R.id.bodoviTextView);

        nicknameTextView.setText(resultGuest.getNickname());
        bodoviTextView.setText(String.valueOf(resultGuest.getBodovi()));

        return convertView;
    }
}