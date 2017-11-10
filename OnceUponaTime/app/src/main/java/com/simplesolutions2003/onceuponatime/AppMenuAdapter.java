package com.simplesolutions2003.onceuponatime;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by SuriyaKumar on 9/5/2016.
 */
public class AppMenuAdapter extends RecyclerView.Adapter<AppMenuAdapter.ViewHolder> {
    private final static String LOG_TAG = AppMenuAdapter.class.getSimpleName();

    private Cursor mCursor;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView titleView;
        public TextView subtitleView;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.app_menu_image);
            titleView = (TextView) view.findViewById(R.id.app_menu_title);
            subtitleView = (TextView) view.findViewById(R.id.app_menu_subtitle);

        }
    }

    public AppMenuAdapter(Context context, Cursor cursor, int flags) {
        mCursor = cursor;
        this.context = context;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(AppMenuFragment.COL_MENU_ID);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.app_menu_list_item;
        View view = (View) LayoutInflater.from(context).inflate(layoutId, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TextView app_menu_title_tv = (TextView) view.findViewById(R.id.app_menu_title);

                ArticlesFragment.ARTICLE_TYPE = app_menu_title_tv.getText().toString().toLowerCase();
                AppMenuFragment.dPosition = viewHolder.getAdapterPosition();

                ArticlesFragment.dPosition = 0;
                FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations( R.anim.slide_in_left, 0, 0, R.anim.slide_out_left);
                fragmentTransaction.replace(R.id.frame_container, new ArticlesFragment(), ArticlesFragment.TAG);
                fragmentTransaction.addToBackStack(AppMenuFragment.TAG);
                fragmentTransaction.commit();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        holder.imageView.setImageResource(getMenuIcon(mCursor.getString(AppMenuFragment.COL_MENU_COVER_PIC)));
        holder.titleView.setText(mCursor.getString(AppMenuFragment.COL_MENU_MENU));
        holder.subtitleView.setText(mCursor.getString(AppMenuFragment.COL_MENU_DESC));

        //add logic for accessibility
        holder.titleView.setContentDescription(holder.titleView.getText());
        holder.subtitleView.setContentDescription(holder.subtitleView.getText());
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    private int getMenuIcon(String menu_pic){
        switch (menu_pic){
            case "menu_01":
                return R.drawable.menu_01;
            case "menu_02":
                return R.drawable.menu_02;
            case "menu_03":
                return R.drawable.menu_03;
            case "menu_04":
                return R.drawable.menu_04;
            case "menu_05":
                return R.drawable.menu_05;
        }
        return -1;
    }

}
