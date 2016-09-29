package com.simplesolutions2003.happybabycare;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.transition.Explode;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplesolutions2003.happybabycare.data.AppContract;
import com.squareup.picasso.Picasso;

/**
 * Created by SuriyaKumar on 9/5/2016.
 */
public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    private Cursor mCursor;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = (ImageView) view.findViewById(R.id.article_thumbnail);
            titleView = (TextView) view.findViewById(R.id.article_title);
            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
        }
    }

    public ArticlesAdapter(Context context,Cursor cursor, int flags) {
        mCursor = cursor;
        this.context = context;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(ArticlesFragment.COL_ARTICLE_ID);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.articles_list_item;
        View view = (View) LayoutInflater.from(context).inflate(layoutId, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArticleDetailFragment.ARTICLE_ID = getItemId(viewHolder.getAdapterPosition());
                ((MainActivity) context).handleFragments(new ArticleDetailFragment(), ArticleDetailFragment.TAG, ArticleDetailFragment.KEEP_IN_STACK);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.titleView.setText(mCursor.getString(ArticlesFragment.COL_ARTICLE_TITLE));
        holder.subtitleView.setText(mCursor.getString(ArticlesFragment.COL_ARTICLE_CATEGORY));

        Picasso.with(context)
                .load(mCursor.getString(ArticlesFragment.COL_ARTICLE_COVER_PIC))
                .noFade()
                .into(holder.thumbnailView);

        //add logic for accessibility
        holder.titleView.setContentDescription(holder.titleView.getText());
        holder.subtitleView.setContentDescription(holder.subtitleView.getText());
        holder.thumbnailView.setContentDescription(holder.titleView.getText());
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

}
