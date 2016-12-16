package com.example.yiwenren.virginiajobsearch.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yiwenren.virginiajobsearch.R;
import com.example.yiwenren.virginiajobsearch.models.SearchResult;

import java.util.List;

/**
 * Created by yiwenren on 11/30/16.
 */

public class SearchResultAdapter extends BaseAdapter {
    private Context context;
    private List<SearchResult> searchData;

    public SearchResultAdapter(@NonNull Context context, List<SearchResult> searchData){
        this.context = context;
        this.searchData = searchData;
    }

    @Override
    public int getCount() {
        return searchData.size();
    }

    @Override
    public Object getItem(int position) {
        return searchData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false);
        }
        SearchResult searchResult = searchData.get(position);

        ((TextView) convertView.findViewById(R.id.search_title)).setText(searchResult.getTitle());
        ((TextView) convertView.findViewById(R.id.search_company_loc)).setText(searchResult.getOrganizationName());
        ((TextView) convertView.findViewById(R.id.search_degree)).setText(searchResult.getLocation());


        return convertView;

    }
}
