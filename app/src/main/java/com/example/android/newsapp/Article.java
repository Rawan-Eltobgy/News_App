package com.example.android.newsapp;

/**
 * Created by user on 7/28/2017.
 */

public class Article {
    String webUrl;
    private String sectionName;
    private String title;
    private String publishDate;

    public Article(String sectionName, String title, String publishDate, String webUrl) {
        this.sectionName = sectionName;
        this.title = title;
        this.webUrl = webUrl;
        this.publishDate = publishDate;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getTitle() {
        return title;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getPublishDate() {
        return String.valueOf(publishDate);
    }

}

