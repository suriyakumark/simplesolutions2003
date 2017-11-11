package com.simplesolutions2003.onceuponatime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.simplesolutions2003.onceuponatime.data.AppContract;
import com.simplesolutions2003.onceuponatime.data.AppContract.ArticleEntry;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by SuriyaKumar on 8/21/2016.
 */
public class ArticleDetailAdapter extends CursorAdapter {
    private final String TAG = ArticleDetailAdapter.class.getSimpleName();
    private Context context;

    public static final String ARTICLE_DETAIL_TYPE_TEXT = "text";
    public static final String ARTICLE_DETAIL_TYPE_IMAGE = "image";

    public static class ViewHolder {

        public final ImageView imageView;
        public final TextView textView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.article_list_image);
            textView = (TextView) view.findViewById(R.id.article_list_text);
        }

    }

    public ArticleDetailAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        Log.v(TAG, "newView");
        int layoutId = R.layout.article_detail_list_item;
        View view = (View) LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Log.v(TAG, "bindView");
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        if(cursor != null){
            if(cursor.getString(ArticleDetailFragment.COL_ARTICLE_DETAIL_TYPE).equals(ARTICLE_DETAIL_TYPE_IMAGE)) {
                new Utilities(context).loadImageView(viewHolder.imageView,
                        cursor.getString(ArticleDetailFragment.COL_ARTICLE_DETAIL_CONTENT));
                viewHolder.imageView.setVisibility(View.VISIBLE);
            }else{
                viewHolder.imageView.setVisibility(View.GONE);
            }
            if(cursor.getString(ArticleDetailFragment.COL_ARTICLE_DETAIL_TYPE).equals(ARTICLE_DETAIL_TYPE_TEXT)) {
                SpannableString spanString = new SpannableString(cursor.getString(ArticleDetailFragment.COL_ARTICLE_DETAIL_CONTENT));
                viewHolder.textView.setContentDescription(viewHolder.textView.getText().toString());
                if(spanString.toString().contains("MORAL:")){
                    spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                }
                viewHolder.textView.setText(spanString);
                viewHolder.textView.setVisibility(View.VISIBLE);

                if(ArticleDetailFragment.ARTICLE_BOOKMARK == cursor.getPosition() &&
                        ArticleDetailFragment.ARTICLE_BOOKMARK > 0){
                    handleBookmark(viewHolder.textView,true);
                }else{
                    handleBookmark(viewHolder.textView,false);
                }

                viewHolder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View parentRow = (View) view.getParent();
                        ListView listView = (ListView) parentRow.getParent();
                        final int bookmarkSequence = listView.getPositionForView(parentRow);
                        if(ArticleDetailFragment.ARTICLE_BOOKMARK > 0 &&
                                ArticleDetailFragment.ARTICLE_BOOKMARK != bookmarkSequence){
                            View childRow = (View) listView.getChildAt(ArticleDetailFragment.ARTICLE_BOOKMARK
                                    - listView.getFirstVisiblePosition());
                            if(childRow != null) {
                                View childText = childRow.findViewById(R.id.article_list_text);
                                if(childText != null) {
                                    handleBookmark(childText, false);
                                }
                            }
                        }
                        handleBookmark(view, updateBookmark(bookmarkSequence));

                    }
                });
            }else{
                viewHolder.textView.setVisibility(View.GONE);
            }
        }
    }

    public boolean updateBookmark(int bookmarkSequence){
        if(ArticleDetailFragment.ARTICLE_BOOKMARK == bookmarkSequence) {
            bookmarkSequence = 0;
        }

        ContentValues bookmarkValues = new ContentValues();
        bookmarkValues.put(ArticleEntry.COLUMN_BOOKMARK, Long.toString(bookmarkSequence));

        ((MainActivity) context).getContentResolver().update(AppContract.ArticleEntry.CONTENT_URI, bookmarkValues,
                ArticleEntry.TABLE_NAME + "." + ArticleEntry._ID + " = ?",
                new String[]{Long.toString(ArticleDetailFragment.ARTICLE_ID)});

        ArticleDetailFragment.ARTICLE_BOOKMARK = bookmarkSequence;
        Log.v(TAG,"bookmarkSequence - "+ bookmarkSequence);

        if(bookmarkSequence == 0) {
            return false;
        }else{
            return true;
        }

    }

    public void handleBookmark(View view, boolean setBookmark){
        if(setBookmark){
            GradientDrawable gd = new GradientDrawable();
            gd.setCornerRadius(5);
            gd.setStroke(1, Color.RED);
            if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackgroundDrawable(gd);
            } else {
                view.setBackground(gd);
            }
        }else{
            view.setBackgroundResource(0);
        }

    }
}
