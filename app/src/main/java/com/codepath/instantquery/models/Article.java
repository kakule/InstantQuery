package com.codepath.instantquery.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created on 10/21/2016.
 */

@Parcel
public class Article{
    String webUrl;
    String headLine;
    String thumbnail;
    String snippet;
    // empty constructor needed by the Parceler library
    public Article () {

    }

    public Article(JSONObject jsonObject) {
        try {
            this.webUrl = jsonObject.getString("web_url");
            this.headLine = jsonObject.getJSONObject("headline").getString("main");
            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            if (multimedia.length() > 0) {
                JSONObject multimediaJson = multimedia.getJSONObject(new Random()
                        .nextInt(multimedia.length()));
                this.thumbnail = "http://www.nytimes.com/" + multimediaJson.getString("url");
            } else {
                this.thumbnail = "";
                this.snippet = jsonObject.getString("snippet");
            }
        } catch (JSONException e) {
            Log.d("DEBUG", "constructor");
            e.printStackTrace();
        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray array) {
        ArrayList<Article> results = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                results.add(new Article(array.getJSONObject(i)));
            } catch (JSONException e) {
                Log.d("DEBUG", "from JSON");
                e.printStackTrace();
            }
        }

        return results;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadLine() {
        return headLine;
    }

    public String getSnippet() {
        return snippet;
    }
}
