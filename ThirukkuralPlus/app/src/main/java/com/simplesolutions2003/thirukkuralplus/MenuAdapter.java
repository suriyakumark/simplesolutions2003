package com.simplesolutions2003.thirukkuralplus;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.simplesolutions2003.thirukkuralplus.data.AppContract;

import java.security.acl.Group;

/**
 * Created by SuriyaKumar on 9/5/2016.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private final static String LOG_TAG = MenuAdapter.class.getSimpleName();

    private Cursor mCursor;
    private Context context;
    private String adapterType;

    public final static String ADAPTER_TYPE_SECTIONS = "SECTIONS";
    public final static String ADAPTER_TYPE_GROUPS = "GROUPS";
    public final static String ADAPTER_TYPE_CHAPTERS = "CHAPTERS";
    public final static String ADAPTER_TYPE_KURALS = "KURALS";

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title_num;
        public TextView titleView;
        public TextView subtitleView;
        public TextView titleViewS;
        public TextView subtitleViewS;
        public TextView extraInfo1;
        public TextView extraSubInfo1;
        public TextView extraInfo2;
        public TextView extraSubInfo2;
        public TextView extraInfo3;
        public TextView extraSubInfo3;


        public ViewHolder(View view) {
            super(view);
            title_num = (TextView) view.findViewById(R.id.title_num);
            titleView = (TextView) view.findViewById(R.id.app_menu_title);
            subtitleView = (TextView) view.findViewById(R.id.app_menu_subtitle);
            titleViewS = (TextView) view.findViewById(R.id.app_menu_title_small);
            subtitleViewS = (TextView) view.findViewById(R.id.app_menu_subtitle_small);
            extraInfo1 = (TextView) view.findViewById(R.id.extra_info1);
            extraSubInfo1 = (TextView) view.findViewById(R.id.extra_subinfo1);
            extraInfo2 = (TextView) view.findViewById(R.id.extra_info2);
            extraSubInfo2 = (TextView) view.findViewById(R.id.extra_subinfo2);
            extraInfo3 = (TextView) view.findViewById(R.id.extra_info3);
            extraSubInfo3 = (TextView) view.findViewById(R.id.extra_subinfo3);

        }
    }

    public MenuAdapter(Context context, Cursor cursor, int flags) {
        mCursor = cursor;
        this.context = context;
    }

    public void setAdapterType(String adapterType){
        this.adapterType = adapterType;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        switch(adapterType) {
            case ADAPTER_TYPE_SECTIONS:
                return mCursor.getLong(SectionsFragment.COL_SECTIONS_ID);
            case ADAPTER_TYPE_GROUPS:
                return mCursor.getLong(GroupsFragment.COL_GROUPS_ID);
            case ADAPTER_TYPE_CHAPTERS:
                return mCursor.getLong(ChaptersFragment.COL_CHAPTERS_ID);
            case ADAPTER_TYPE_KURALS:
                return mCursor.getLong(KuralsFragment.COL_KURALS_ID);
            default:
                return -1;
        }
    }

    public Cursor getCursor(int position) {
        mCursor.moveToPosition(position);
        return mCursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.app_menu_list_item;
        View view = (View) LayoutInflater.from(context).inflate(layoutId, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction;

                switch(adapterType){
                    case ADAPTER_TYPE_SECTIONS:

                        SectionsFragment.dPosition = viewHolder.getAdapterPosition();

                        ChaptersFragment.SECTION_ID = getItemId(viewHolder.getAdapterPosition());
                        ChaptersFragment.MODE = ChaptersFragment.MODE_READ;
                        ChaptersFragment.GROUP_ID = -1;

                        // do not add this fragment to back stack, as this is the starting transaction
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_container, new ChaptersFragment(), ChaptersFragment.TAG);
                        fragmentTransaction.addToBackStack(SectionsFragment.TAG);
                        fragmentTransaction.commit();

                        break;
                    case ADAPTER_TYPE_GROUPS:
                        GroupsFragment.dPosition = viewHolder.getAdapterPosition();

                        ChaptersFragment.SECTION_ID = -1;
                        ChaptersFragment.MODE = ChaptersFragment.MODE_READ;
                        ChaptersFragment.GROUP_ID = getItemId(viewHolder.getAdapterPosition());

                        // do not add this fragment to back stack, as this is the starting transaction
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_container, new ChaptersFragment(), ChaptersFragment.TAG);
                        fragmentTransaction.addToBackStack(GroupsFragment.TAG);
                        fragmentTransaction.commit();

                        break;
                    case ADAPTER_TYPE_CHAPTERS:
                        ChaptersFragment.dPosition = viewHolder.getAdapterPosition();

                        if(ChaptersFragment.MODE == ChaptersFragment.MODE_QUIZ){
                            QuizFragment.CHAPTER_ID = getItemId(viewHolder.getAdapterPosition());
                            // do not add this fragment to back stack, as this is the starting transaction
                            fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frame_container, new QuizFragment(), QuizFragment.TAG);
                            fragmentTransaction.addToBackStack(ChaptersFragment.TAG);
                            fragmentTransaction.commit();

                        }else {
                            KuralsFragment.dPosition = 0;
                            KuralsFragment.CHAPTER_ID = getItemId(viewHolder.getAdapterPosition());
                            KuralsFragment.SEARCH_TEXT = "";

                            // do not add this fragment to back stack, as this is the starting transaction
                            fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frame_container, new KuralsFragment(), KuralsFragment.TAG);
                            fragmentTransaction.addToBackStack(ChaptersFragment.TAG);
                            fragmentTransaction.commit();
                        }
                        break;
                    case ADAPTER_TYPE_KURALS:
                        KuralsFragment.dPosition = viewHolder.getAdapterPosition();
                        KuralsDetailFragment.KURALS_ID = getItemId(viewHolder.getAdapterPosition());
                        Cursor cursor = getCursor(viewHolder.getAdapterPosition());
                        KuralsDetailFragment.KURALS_NAME = cursor.getString(KuralsFragment.COL_KURALS_NAME);

                        KuralsDetailFragment.KURALS_CHAPTER_ID = cursor.getLong(KuralsFragment.COL_KURALS_CHAPTER_ID);
                        KuralsDetailFragment.SECTION_ID = cursor.getLong(KuralsFragment.COL_SECTION_ID);
                        KuralsDetailFragment.GROUP_ID = cursor.getLong(KuralsFragment.COL_GROUP_ID);
                        KuralsDetailFragment.KURALS_FAV = cursor.getLong(KuralsFragment.COL_KURALS_FAV);
                        KuralsDetailFragment.KURALS_READ = cursor.getLong(KuralsFragment.COL_KURALS_READ);

                        KuralsDetailFragment.KURALS_NAME = cursor.getString(KuralsFragment.COL_KURALS_NAME);
                        KuralsDetailFragment.KURALS_NAME_ENG = cursor.getString(KuralsFragment.COL_KURALS_NAME_ENG);
                        KuralsDetailFragment.KURALS_EXP_MUVA = cursor.getString(KuralsFragment.COL_KURALS_EXP_MUVA);
                        KuralsDetailFragment.KURALS_EXP_SOLO = cursor.getString(KuralsFragment.COL_KURALS_EXP_SOLO);
                        KuralsDetailFragment.KURALS_EXP_MUKA = cursor.getString(KuralsFragment.COL_KURALS_EXP_MUKA);
                        KuralsDetailFragment.KURALS_COUPLET = cursor.getString(KuralsFragment.COL_KURALS_COUPLET);
                        KuralsDetailFragment.KURALS_TRANS = cursor.getString(KuralsFragment.COL_KURALS_TRANS);
                        KuralsDetailFragment.CHAPTER_NAME = cursor.getString(KuralsFragment.COL_CHAPTER_NAME);
                        KuralsDetailFragment.CHAPTER_NAME_ENG = cursor.getString(KuralsFragment.COL_CHAPTER_NAME_ENG);
                        KuralsDetailFragment.SECTION_NAME = cursor.getString(KuralsFragment.COL_SECTION_NAME);
                        KuralsDetailFragment.SECTION_NAME_ENG = cursor.getString(KuralsFragment.COL_SECTION_NAME_ENG);
                        KuralsDetailFragment.GROUP_NAME = cursor.getString(KuralsFragment.COL_GROUP_NAME);
                        KuralsDetailFragment.GROUP_NAME_ENG = cursor.getString(KuralsFragment.COL_GROUP_NAME_ENG);

                        // do not add this fragment to back stack, as this is the starting transaction
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_container, new KuralsDetailFragment(), KuralsDetailFragment.TAG);
                        fragmentTransaction.addToBackStack(KuralsFragment.TAG);
                        fragmentTransaction.commit();
                        break;
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        switch(adapterType) {
            case ADAPTER_TYPE_SECTIONS:
                holder.title_num.setText(mCursor.getString(SectionsFragment.COL_SECTIONS_ID));
                holder.titleView.setText(mCursor.getString(SectionsFragment.COL_SECTIONS_NAME));
                holder.subtitleView.setText(mCursor.getString(SectionsFragment.COL_SECTIONS_NAME_ENG));
                break;
            case ADAPTER_TYPE_GROUPS:
                holder.title_num.setText(mCursor.getString(GroupsFragment.COL_GROUPS_ID));
                holder.titleView.setText(mCursor.getString(GroupsFragment.COL_GROUPS_NAME));
                holder.subtitleView.setText(mCursor.getString(GroupsFragment.COL_GROUPS_NAME_ENG));
                break;
            case ADAPTER_TYPE_CHAPTERS:
                holder.title_num.setText(mCursor.getString(ChaptersFragment.COL_CHAPTERS_ID));
                holder.titleView.setText(mCursor.getString(ChaptersFragment.COL_CHAPTERS_NAME));
                holder.subtitleView.setText(mCursor.getString(ChaptersFragment.COL_CHAPTERS_NAME_ENG));
                if(ChaptersFragment.SECTION_ID > -1 || ChaptersFragment.GROUP_ID > -1) {
                    holder.extraInfo1.setText(mCursor.getString(ChaptersFragment.COL_SECTION_NAME)+" ["+mCursor.getString(ChaptersFragment.COL_CHAPTERS_SECTION_ID)+"]");
                    holder.extraSubInfo1.setText(mCursor.getString(ChaptersFragment.COL_SECTION_NAME_ENG));
                    holder.extraInfo2.setText(mCursor.getString(ChaptersFragment.COL_GROUP_NAME)+" ["+mCursor.getString(ChaptersFragment.COL_CHAPTERS_GROUP_ID)+"]");
                    holder.extraSubInfo2.setText(mCursor.getString(ChaptersFragment.COL_GROUP_NAME_ENG));
                }
                break;
            case ADAPTER_TYPE_KURALS:
                holder.title_num.setText(mCursor.getString(KuralsFragment.COL_KURALS_ID));
                holder.titleViewS.setText(mCursor.getString(KuralsFragment.COL_KURALS_NAME));
                holder.subtitleViewS.setText(mCursor.getString(KuralsFragment.COL_KURALS_NAME_ENG));
                holder.extraInfo1.setText(mCursor.getString(KuralsFragment.COL_SECTION_NAME)+" ["+mCursor.getString(KuralsFragment.COL_SECTION_ID)+"]");
                holder.extraSubInfo1.setText(mCursor.getString(KuralsFragment.COL_SECTION_NAME_ENG));
                holder.extraInfo2.setText(mCursor.getString(KuralsFragment.COL_GROUP_NAME)+" ["+mCursor.getString(KuralsFragment.COL_GROUP_ID)+"]");
                holder.extraSubInfo2.setText(mCursor.getString(KuralsFragment.COL_GROUP_NAME_ENG));
                holder.extraInfo3.setText(mCursor.getString(KuralsFragment.COL_CHAPTER_NAME)+" ["+mCursor.getString(KuralsFragment.COL_KURALS_CHAPTER_ID)+"]");
                holder.extraSubInfo3.setText(mCursor.getString(KuralsFragment.COL_CHAPTER_NAME_ENG));
                break;

            default:
                break;
        }

        if(holder.titleView.getText().toString().isEmpty()){
            holder.titleView.setVisibility(View.GONE);
        }
        if(holder.titleViewS.getText().toString().isEmpty()){
            holder.titleViewS.setVisibility(View.GONE);
        }
        if(holder.subtitleView.getText().toString().isEmpty() || !new Utilities().isEnglishEnabled(context)){
            holder.subtitleView.setVisibility(View.GONE);
        }
        if(holder.subtitleViewS.getText().toString().isEmpty() || !new Utilities().isEnglishEnabled(context)){
            holder.subtitleViewS.setVisibility(View.GONE);
        }
        if(holder.extraInfo1.getText().toString().isEmpty()){
            holder.extraInfo1.setVisibility(View.GONE);
        }
        if(holder.extraInfo2.getText().toString().isEmpty()){
            holder.extraInfo2.setVisibility(View.GONE);
        }
        if(holder.extraInfo3.getText().toString().isEmpty()){
            holder.extraInfo3.setVisibility(View.GONE);
        }
        if(holder.extraSubInfo1.getText().toString().isEmpty() || !new Utilities().isEnglishEnabled(context)){
            holder.extraSubInfo1.setVisibility(View.GONE);
        }
        if(holder.extraSubInfo2.getText().toString().isEmpty() || !new Utilities().isEnglishEnabled(context)){
            holder.extraSubInfo2.setVisibility(View.GONE);
        }
        if(holder.extraSubInfo3.getText().toString().isEmpty() || !new Utilities().isEnglishEnabled(context)){
            holder.extraSubInfo3.setVisibility(View.GONE);
        }

        //add logic for accessibility
        holder.titleView.setContentDescription(holder.titleView.getText());
        holder.subtitleView.setContentDescription(holder.subtitleView.getText());
        holder.extraInfo1.setContentDescription(holder.extraInfo1.getText());
        holder.extraSubInfo1.setContentDescription(holder.extraSubInfo1.getText());
        holder.extraInfo2.setContentDescription(holder.extraInfo2.getText());
        holder.extraSubInfo2.setContentDescription(holder.extraSubInfo2.getText());
        holder.extraInfo3.setContentDescription(holder.extraInfo3.getText());
        holder.extraSubInfo3.setContentDescription(holder.extraSubInfo3.getText());

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }


}
