package com.example.android.newsapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user on 7/28/2017.
 */
public class ArticleAdapter extends ArrayAdapter<Article> {
    //@BindView(R.id.title) TextView title;
    // @BindView(R.id.section_name) TextView section_name;
    // @BindView(R.id.publishedDate) TextView publishDate;

    public ArticleAdapter(Activity context, ArrayList<Article> articles) {
        super(context, 0, articles);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view

        View listItemView = convertView;
        //ButterKnife.bind(this,listItemView);
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_feed_item, parent, false);
        }
        Article currentArticle = getItem(position);

        // Find the TextView in the news_feed_item layout with the ID version_name
        TextView title = (TextView) listItemView.findViewById(R.id.title);


        TextView section_name = (TextView) listItemView.findViewById(R.id.section_name);


        TextView publishDate = (TextView) listItemView.findViewById(R.id.publishedDate);
        // set this text on the name TextView
        title.setText(currentArticle.getTitle());


        // set this text on the name TextView
        section_name.setText(currentArticle.getSectionName());

        // set this text on the name TextView
        publishDate.setText(String.valueOf(currentArticle.getPublishDate()));

        return listItemView;
    }


}
