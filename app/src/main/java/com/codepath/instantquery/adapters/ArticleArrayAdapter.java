package com.codepath.instantquery.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.instantquery.R;
import com.codepath.instantquery.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created on 10/21/2016.
 */
public class ArticleArrayAdapter extends ArrayAdapter <Article>{
    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the data item for position
        Article article = getItem(position);

        //check to see if the existing view is being re-used
        //if not inflate layout
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
        }
        // find the view items
        ImageView imageView = (ImageView) convertView.findViewById(R.id.ivImage);
        //clear out recycled image from convert view from last time
        imageView.setImageResource(0);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
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

        return convertView;
    }
}
