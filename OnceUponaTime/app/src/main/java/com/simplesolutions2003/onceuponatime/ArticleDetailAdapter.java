package com.simplesolutions2003.onceuponatime;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
            }else{
                viewHolder.textView.setVisibility(View.GONE);
            }
        }

    }
}
