package com.simplesolutions2003.happybabycare;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
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
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if(cursor != null){
            if(cursor.getPosition() == 0){
                ArticleDetailFragment.articleDetailTitle.setText(cursor.getString(ArticleDetailFragment.COL_ARTICLE_TITLE));
                ArticleDetailFragment.articleDetailTitle.setContentDescription(ArticleDetailFragment.articleDetailTitle.getText().toString());
            }
            if(cursor.getString(ArticleDetailFragment.COL_ARTICLE_DETAIL_TYPE).equals(ARTICLE_DETAIL_TYPE_IMAGE)) {
                Picasso.with(context)
                        .load(cursor.getString(ArticleDetailFragment.COL_ARTICLE_DETAIL_CONTENT))
                        .noFade()
                        .into(viewHolder.imageView);
                viewHolder.imageView.setVisibility(View.VISIBLE);
            }else{
                viewHolder.imageView.setVisibility(View.GONE);
            }
            if(cursor.getString(ArticleDetailFragment.COL_ARTICLE_DETAIL_TYPE).equals(ARTICLE_DETAIL_TYPE_TEXT)) {
                viewHolder.textView.setText(cursor.getString(ArticleDetailFragment.COL_ARTICLE_DETAIL_CONTENT));
                viewHolder.textView.setContentDescription(viewHolder.textView.getText().toString());
                viewHolder.textView.setVisibility(View.VISIBLE);
            }else{
                viewHolder.textView.setVisibility(View.GONE);
            }
        }

    }
}
