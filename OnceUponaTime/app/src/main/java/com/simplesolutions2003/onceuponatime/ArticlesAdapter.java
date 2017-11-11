package com.simplesolutions2003.onceuponatime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplesolutions2003.onceuponatime.data.AppContract;

/**
 * Created by SuriyaKumar on 9/5/2016.
 */
public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {
    private final static String LOG_TAG = ArticlesAdapter.class.getSimpleName();

    private Cursor mCursor;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;
        public TextView newView;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = (ImageView) view.findViewById(R.id.article_thumbnail);
            titleView = (TextView) view.findViewById(R.id.article_title);
            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
            newView = (TextView) view.findViewById(R.id.article_new);
        }
    }

    public ArticlesAdapter(Context context,Cursor cursor, int flags) {
        mCursor = cursor;
        this.context = context;
    }

    @Override
    public long getItemId(int position) {
        if(mCursor != null && !mCursor.isClosed()){
            mCursor.moveToPosition(position);
            return mCursor.getLong(ArticlesFragment.COL_ARTICLE_ID);
        }
        return -1;
    }

    public int getBookmarkId(int position) {
        if(mCursor != null && !mCursor.isClosed()){
            mCursor.moveToPosition(position);
            return mCursor.getInt(ArticlesFragment.COL_ARTICLE_BOOKMARK);
        }
        return -1;
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
                ArticleDetailFragment.ARTICLE_BOOKMARK = getBookmarkId(viewHolder.getAdapterPosition());

                ContentValues articleValues = new ContentValues();
                articleValues.put(AppContract.ArticleEntry.COLUMN_NEW, "0");

                ((MainActivity) context).getContentResolver().update(AppContract.ArticleEntry.CONTENT_URI, articleValues,
                        AppContract.ArticleEntry._ID + " = ?",new String[]{Long.toString(getItemId(viewHolder.getAdapterPosition()))});

                ArticlesFragment.dPosition = viewHolder.getAdapterPosition();

                Log.v(LOG_TAG,"replacing fragment");
                MainActivity.displayAd();
                FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations( R.anim.scale_in, 0, 0, R.anim.scale_out);
                fragmentTransaction.replace(R.id.frame_container, new ArticleDetailFragment(), ArticleDetailFragment.TAG);
                fragmentTransaction.addToBackStack(ArticlesFragment.TAG);
                fragmentTransaction.commit();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.titleView.setText(mCursor.getString(ArticlesFragment.COL_ARTICLE_TITLE));
        holder.subtitleView.setText(mCursor.getString(ArticlesFragment.COL_ARTICLE_AUTHOR));

        Log.v(LOG_TAG, "New - " + mCursor.getString(ArticlesFragment.COL_ARTICLE_NEW));
        if(mCursor.getString(ArticlesFragment.COL_ARTICLE_NEW).equals("1")) {
            holder.newView.setText("*New");
            holder.newView.setVisibility(View.VISIBLE);
        }else if(ArticlesFragment.ARTICLE_TYPE.equals("favorites")){
            holder.newView.setText(mCursor.getString(ArticlesFragment.COL_ARTICLE_TYPE).toUpperCase());
            holder.newView.setVisibility(View.VISIBLE);
        }else{
            holder.newView.setText("");
            holder.newView.setVisibility(View.GONE);
            int padding =(int) context.getResources().getDimensionPixelOffset(R.dimen.card_title_padding);
            holder.titleView.setPadding(padding,padding,padding,padding);
        }
        /*Picasso.with(context)
                .load(mCursor.getString(ArticlesFragment.COL_ARTICLE_COVER_PIC))
                .noFade()
                .into(holder.thumbnailView);*/
        new Utilities(context).loadImageView(holder.thumbnailView,
                mCursor.getString(ArticlesFragment.COL_ARTICLE_COVER_PIC));

        if(isThumbnailEnabled(ArticlesFragment.ARTICLE_TYPE)){
            holder.thumbnailView.setVisibility(View.VISIBLE);
        }else{
            holder.thumbnailView.setVisibility(View.GONE);
        }


        //add logic for accessibility
        holder.titleView.setContentDescription(holder.titleView.getText());
        holder.subtitleView.setContentDescription(holder.subtitleView.getText());
        holder.thumbnailView.setContentDescription(holder.titleView.getText());
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public boolean isThumbnailEnabled(String articleType){
        switch (articleType){
            case MainActivity.ARTICLE_TYPE_SHORT_STORIES:
                return true;
            case MainActivity.ARTICLE_TYPE_STORIES:
                return true;
            case MainActivity.ARTICLE_TYPE_RHYMES:
                return true;
            default:
                return false;
        }

    }
}
