package com.prosysopc.ua.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by haripriyasaranya on 08/09/15.
 */
public class ListAdapter extends BaseAdapter {

    private ArrayList<SearchInformation> searchInformationArrayList;
    private LayoutInflater inflater;

    public ListAdapter(Context context, ArrayList<SearchInformation> searchInformationArrayList) {
        this.searchInformationArrayList = searchInformationArrayList;
        inflater = LayoutInflater.from(context);
    }

    public ListAdapter() {
        super();
    }

    @Override
    public int getCount() {
        return this.searchInformationArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.searchInformationArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view == null) {
            view = inflater.inflate(R.layout.listview_item_row, null);
            viewHolder = new ViewHolder();
            viewHolder.headLineInformation = (TextView) view.findViewById(R.id.headline_name);
            viewHolder.subLineInformation = (TextView) view.findViewById(R.id.subtext_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.headLineInformation.setText(this.searchInformationArrayList.get(i).getHeadLineInformation());
        viewHolder.subLineInformation.setText(this.searchInformationArrayList.get(i).getSubLineInformation());
        return view;
    }

    private static class ViewHolder {
        TextView headLineInformation, subLineInformation;
    }
}
