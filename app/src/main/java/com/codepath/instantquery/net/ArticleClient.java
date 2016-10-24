package com.codepath.instantquery.net;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created on 10/24/2016.
 */
public class ArticleClient {
    private static final String BASE_URL = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
    private AsyncHttpClient client;

    public ArticleClient() {
        this.client = new AsyncHttpClient();
    }

    // Method for accessing the search API
    public void getArticles(RequestParams params,
                            JsonHttpResponseHandler handler) {
        client.get(BASE_URL, params, handler);
    }
}
