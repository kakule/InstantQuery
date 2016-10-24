package com.codepath.instantquery;

import android.util.Log;
import android.widget.AbsListView;

/**
 * Created on 10/22/2016.
 */
public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {
    // The minimum number of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 8;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;
    //previous call time
    Long prev = 0L;
    //current call time
    Long curr = 0L;

    public EndlessScrollListener() {
        Log.d("DEBUG", "new endless scroll");
        Log.d("DEBUG", "currentPage" + this.currentPage);
        Log.d("DEBUG", "prev total" + this.previousTotalItemCount);
        Log.d("DEBUG", "loading" + this.loading);
        Log.d("DEBUG", "page start index" + this.startingPageIndex);
    }

    public EndlessScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    public EndlessScrollListener(int visibleThreshold, int startPage, boolean loading) {
        this.visibleThreshold = visibleThreshold;
        this.startingPageIndex = startPage;
        this.currentPage = startPage;
        this.loading = loading;
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        //check the current time
        curr = System.currentTimeMillis();
        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) { this.loading = true; }
        }
        // If it's still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && (totalItemCount > previousTotalItemCount)
                && (curr - prev >= 1000L)) {
            prev = curr;
            loading = false;
            previousTotalItemCount = totalItemCount;
            currentPage++;
        } else if (loading && (totalItemCount <= previousTotalItemCount)
                && (curr - prev >= 3000L)) {
            //Assume loading failed so set to not loading but don't increment page
            loading = false;

        }

        // If it isn't currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        if (!loading && (firstVisibleItem + visibleItemCount + visibleThreshold) >= totalItemCount ) {
            loading = onLoadMore(currentPage + 1, totalItemCount);
            //loading = onLoadMore(currentPage, totalItemCount);
        }
    }

    // Defines the process for actually loading more data based on page
    // Returns true if more data is being loaded; returns false if there is no more data to load.
    public abstract boolean onLoadMore(int page, int totalItemsCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Don't take any action on changed
    }
}
