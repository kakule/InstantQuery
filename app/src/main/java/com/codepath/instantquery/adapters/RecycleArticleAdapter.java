package com.codepath.instantquery.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.instantquery.R;
import com.codepath.instantquery.activities.ArticleActivity;
import com.codepath.instantquery.models.Article;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

/**
 * Created on 10/21/2016.
 */
public class RecycleArticleAdapter extends
        RecyclerView.Adapter<RecycleArticleAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener{

        ImageView imageView;
        TextView tvTitle;
        private Context context;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(Context context, View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            // find the view items
            this.imageView = (ImageView) itemView.findViewById(R.id.ivImage);
            this.tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            // Store the context
            this.context = context;
            // Attach a click listener to the entire row view
            itemView.setOnClickListener(this);
        }

        // Handles the row being being clicked
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                // We can access the data within the views
                //Toast.makeText(context, tvTitle.getText(), Toast.LENGTH_SHORT).show();
                //create an intent to display item
                Intent i = new Intent(context.getApplicationContext(), ArticleActivity.class);
                //get article to display
                Article article = mArticles.get(position);
                //pass into intent
                i.putExtra(ArticleActivity.articleIntentKey, Parcels.wrap(article));
                //launch activity


                context.startActivity(i);
            }
        }
    }


    // Store a member variable for the contacts
    protected List<Article> mArticles;
    // Store the context for easy access
    protected Context mContext;

    public RecycleArticleAdapter(Context context, List<Article> articles) {
        //super(context, android.R.layout.simple_list_item_1, articles);

        mArticles = articles;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public RecycleArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View articleView = inflater.inflate(R.layout.item_article_result, parent, false);
        // Return a new holder instance
        return new ViewHolder(context, articleView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RecycleArticleAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Article article = mArticles.get(position);

        // Set item views based on your views and data model
        ImageView imageView = viewHolder.imageView;
        TextView tvTitle = viewHolder.tvTitle;

        tvTitle.setText(article.getHeadLine());

        //populate the thumbnail image
        //remote download the image in the background
        String thumbnail = article.getThumbnail();
        if (!TextUtils.isEmpty(thumbnail)) {
            Picasso.with(getContext()).load(thumbnail)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.mipmap.ic_no_image);
        }

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mArticles.size();
    }

}
