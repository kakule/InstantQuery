package com.codepath.instantquery.models;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created on 10/24/2016.
 */
@Parcel
public class ArticleCollection {

    ArrayList <Article> articleList;

    public ArticleCollection () {

    }

    public ArticleCollection(ArrayList articles) {
        this.articleList = articles;
    }

    public ArrayList<Article> getArticleList() {
        return articleList;
    }
}
