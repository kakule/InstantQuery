package com.codepath.instantquery.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.instantquery.R;
import com.codepath.instantquery.adapters.RecycleArticleAdapter;
import com.codepath.instantquery.adapters.SpacesItemDecoration;
import com.codepath.instantquery.interfaces.EndlessRecyclerViewScrollListener;
import com.codepath.instantquery.models.Article;
import com.codepath.instantquery.models.ArticleCollection;
import com.codepath.instantquery.net.ArticleClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class SearchActivity extends AppCompatActivity implements
        FilterSearchDialogFragment.FilterFragListener{

    String currentListKey = "currentlist";
    Calendar beginDate;
    String sortOrder;
    boolean [] newsDesk;

    RecyclerView rvResult;
    StaggeredGridLayoutManager gridLayoutManager;
    SpacesItemDecoration decoration;
    String lastSearch;
    ArticleClient client;
    int lastPage = 0;

    ArrayList<Article> articles;
    RecycleArticleAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //retrieve past state if present
        if (savedInstanceState == null ||
                !savedInstanceState.containsKey(currentListKey)) {
            articles = new ArrayList<>();
        } else {
            ArticleCollection ac = Parcels.unwrap(savedInstanceState.getParcelable(currentListKey));
            articles = ac.getArticleList();
        }

        setupViews();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(currentListKey, Parcels.wrap(new ArticleCollection(articles)));
        super.onSaveInstanceState(outState);
    }

    public void setupViews() {
        rvResult = (RecyclerView) findViewById(R.id.rvResults);
        client = new ArticleClient();
        adapter = new RecycleArticleAdapter(this, articles);
        rvResult.setAdapter(adapter);
        gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        // Set layout manager to position the items
        rvResult.setLayoutManager(gridLayoutManager);
        decoration = new SpacesItemDecoration(16);
        rvResult.addItemDecoration(decoration);
        rvResult.setItemAnimator(new SlideInUpAnimator());

    }

    private void setNewScrollListener () {
        rvResult.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                customLoadMoreDataFromApi(page);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                if (!TextUtils.isEmpty(query)) {
                    setNewScrollListener();
                    ArticleSearch(query, 0);
                    searchView.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            // This is the up button
            //case android.R.id.home:
            //    NavUtils.navigateUpFromSameTask(this);
                // overridePendingTransition(R.animator.anim_left, R.animator.anim_right);
                //return true;
            case R.id.action_filter:
                onSearchFilter();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void ArticleSearch(String query, int page) {
        if (isNetworkAvailable() && isOnline()) {
            lastSearch = query;
            //Toast.makeText(this, "Searching for " + query, Toast.LENGTH_LONG).show();
            RequestParams params = getParams(page);
            if (page == 0) {
                client = new ArticleClient();
            }

            client.getArticles(params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    JSONArray articleResults;
                    int offset;
                    try {
                        articleResults = response.getJSONObject("response").getJSONArray("docs");
                        offset = Integer.parseInt(response.getJSONObject("response").
                                getJSONObject("meta").getString("offset"));
                        if (offset == 0)
                            articles.clear();
                        articles.addAll(Article.fromJSONArray(articleResults));
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                      JSONObject errorResponse) {
                    Log.d("DEBUG", "Failed: " + statusCode);
                    Log.d("DEBUG", "Error: " + throwable);
                }

            });
        }
    }

    //get the parameters for query
    private RequestParams getParams (int page) {

        RequestParams params = new RequestParams();
        params.put("api-key", R.string.search_api);
        params.put("page", page);
        params.put("q", lastSearch);
        if (beginDate != null)
            params.put("begin_date",
                    new SimpleDateFormat("yyyyMMdd").format(beginDate.getTime()));
        if (sortOrder != null)
            params.put("sort", sortOrder.toLowerCase());
        if (newsDesk != null) {
            StringBuilder ndrequest = new StringBuilder("news_desk:(");
            if (newsDesk[0])
                ndrequest.append("\"Arts\"");
            if (newsDesk[1])
                ndrequest.append(" \"Fashion & Style\"");
            if (newsDesk[2])
                ndrequest.append(" \"Sports\"");
            ndrequest.append(")");
            params.put("fq", ndrequest.toString());
        }
        //params.put("fq", "news_desk:(\"sports\")");
        Log.d("DEBUG", lastSearch + " " + Integer.toString(page));
        Log.d("DEBUG", "params " + params.toString());

        return params;
    }

    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
        ArticleSearch(lastSearch, offset);
    }

    public void onSearchFilter () {
        FilterSearchDialogFragment newSearchFilter = FilterSearchDialogFragment
                .newInstance(beginDate, sortOrder, newsDesk);
        newSearchFilter.show(getSupportFragmentManager(), FilterSearchDialogFragment.fragManFilterKey);
    }

    @Override
    public void onSaveFilter (boolean [] news_desk, Calendar begDate, String sort) {
        beginDate = begDate;
        sortOrder = sort;
        newsDesk = news_desk;
        for (int i = 0; i < news_desk.length; i++) {
            if (news_desk[i])
                Log.d("DEBUG", "news_desk " + i + " true");
        }
        //Log.d("DEBUG", "begindate " + new SimpleDateFormat("MM/dd/yy").format(beginDate.getTime()));
        //Log.d("DEBUG", "sortorder " + sort);
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }
}
