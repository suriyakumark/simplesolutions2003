package com.simplesolutions2003.thirukkuralplus;

import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.simplesolutions2003.thirukkuralplus.data.AppContract;
import com.simplesolutions2003.thirukkuralplus.data.AppContract.ChaptersEntry;
import com.simplesolutions2003.thirukkuralplus.data.AppContract.GroupsEntry;
import com.simplesolutions2003.thirukkuralplus.data.AppContract.KuralsEntry;
import com.simplesolutions2003.thirukkuralplus.data.AppContract.SectionsEntry;

/**
 * Created by SuriyaKumar on 8/20/2016.
 */
public class KuralsDetailFragment extends Fragment{
    public final static boolean KEEP_IN_STACK = true;
    public final static String TAG = KuralsDetailFragment.class.getSimpleName();

    public static long KURALS_ID;
    public static long KURALS_CHAPTER_ID;
    public static String KURALS_NAME;
    public static String KURALS_NAME_ENG;
    public static String KURALS_EXP_MUVA;
    public static String KURALS_EXP_SOLO;
    public static String KURALS_EXP_MUKA;
    public static String KURALS_COUPLET;
    public static String KURALS_TRANS;
    public static long KURALS_FAV;
    public static long KURALS_READ;
    public static String CHAPTER_NAME;
    public static String CHAPTER_NAME_ENG;
    public static long SECTION_ID;
    public static String SECTION_NAME;
    public static String SECTION_NAME_ENG;
    public static long GROUP_ID;
    public static String GROUP_NAME;
    public static String GROUP_NAME_ENG;

    private ImageButton btn_favorite;
    public KuralsDetailFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.kural_detail, container, false);

        btn_favorite = (ImageButton) rootView.findViewById(R.id.btn_favorite);
        favoriteUpdate();

        TextView kuralSection = (TextView) rootView.findViewById(R.id.extra_info1);
        TextView kuralSectionEng = (TextView) rootView.findViewById(R.id.extra_subinfo1);
        TextView kuralGroup = (TextView) rootView.findViewById(R.id.extra_info2);
        TextView kuralGroupEng = (TextView) rootView.findViewById(R.id.extra_subinfo2);
        TextView kuralChapter = (TextView) rootView.findViewById(R.id.extra_info3);
        TextView kuralChapterEng = (TextView) rootView.findViewById(R.id.extra_subinfo3);

        TextView kuralNum = (TextView) rootView.findViewById(R.id.title_num);
        TextView kuralName = (TextView) rootView.findViewById(R.id.title_small);
        TextView kuralNameEng = (TextView) rootView.findViewById(R.id.subtitle_small);
        
        TextView kuralInfoTitle1 = (TextView) rootView.findViewById(R.id.detail_infotitle1);
        TextView kuralInfo1 = (TextView) rootView.findViewById(R.id.detail_info1);

        TextView kuralInfoTitle2 = (TextView) rootView.findViewById(R.id.detail_infotitle2);
        TextView kuralInfo2 = (TextView) rootView.findViewById(R.id.detail_info2);

        TextView kuralInfoTitle3 = (TextView) rootView.findViewById(R.id.detail_infotitle3);
        TextView kuralInfo3 = (TextView) rootView.findViewById(R.id.detail_info3);

        TextView kuralInfoTitle4 = (TextView) rootView.findViewById(R.id.detail_infotitle4);
        TextView kuralInfo4 = (TextView) rootView.findViewById(R.id.detail_info4);

        TextView kuralInfoTitle5 = (TextView) rootView.findViewById(R.id.detail_infotitle5);
        TextView kuralInfo5 = (TextView) rootView.findViewById(R.id.detail_info5);

        TextView kuralRead = (TextView) rootView.findViewById(R.id.detail_read);

        kuralSection.setText(SECTION_NAME + " [" + SECTION_ID + "]");
        kuralSectionEng.setText(SECTION_NAME_ENG);
        kuralGroup.setText(GROUP_NAME + " [" + GROUP_ID + "]");
        kuralGroupEng.setText(GROUP_NAME_ENG);
        kuralChapter.setText(CHAPTER_NAME + " [" + KURALS_CHAPTER_ID + "]");
        kuralChapterEng.setText(CHAPTER_NAME_ENG);

        kuralNum.setText("[" + KURALS_ID + "]");
        kuralName.setText(KURALS_NAME);
        kuralNameEng.setText(KURALS_NAME_ENG);

        kuralInfoTitle1.setText(getString(R.string.title_muva));
        kuralInfo1.setText(KURALS_EXP_MUVA);
        kuralInfoTitle2.setText(getString(R.string.title_solo));
        kuralInfo2.setText(KURALS_EXP_SOLO);
        kuralInfoTitle3.setText(getString(R.string.title_muka));
        kuralInfo3.setText(KURALS_EXP_MUKA);
        kuralInfoTitle4.setText(getString(R.string.title_couplet));
        kuralInfo4.setText(KURALS_COUPLET);
        kuralInfoTitle5.setText(getString(R.string.title_trans));
        kuralInfo5.setText(KURALS_TRANS);

        kuralRead.setText(getString(R.string.text_read) + KURALS_READ);

        ContentValues readValues = new ContentValues();
        readValues.put(KuralsEntry.COLUMN_READ, KURALS_READ + 1);
        KuralsDetailFragment.this.getContext().getContentResolver().update(
                KuralsEntry.CONTENT_URI,readValues,
                KuralsEntry.TABLE_NAME +
                        "." + KuralsEntry._ID + " = ? ",
                new String[]{Long.toString(KURALS_ID)}
        );

        btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues favoriteValues = new ContentValues();
                if(KURALS_FAV == 1){
                    KURALS_FAV = 0;
                }else{
                    KURALS_FAV = 1;
                }
                favoriteValues.put(KuralsEntry.COLUMN_FAVORITE, KURALS_FAV);
                KuralsDetailFragment.this.getContext().getContentResolver().update(
                        KuralsEntry.CONTENT_URI,favoriteValues,
                        KuralsEntry.TABLE_NAME +
                                "." + KuralsEntry._ID + " = ? ",
                        new String[]{Long.toString(KURALS_ID)}
                );
                favoriteUpdate();
            }
        });

        if(!new Utilities().isEnglishEnabled(this.getContext())) {
            kuralSectionEng.setVisibility(View.GONE);
            kuralGroupEng.setVisibility(View.GONE);
            kuralChapterEng.setVisibility(View.GONE);
            kuralNameEng.setVisibility(View.GONE);
            kuralInfoTitle4.setVisibility(View.GONE);
            kuralInfo4.setVisibility(View.GONE);
            kuralInfoTitle5.setVisibility(View.GONE);
            kuralInfo5.setVisibility(View.GONE);
        }

        return rootView;
    }

    public void favoriteUpdate(){
        if(KURALS_FAV == 1){
            btn_favorite.setImageResource(R.drawable.icon_favorite);
        }else{
            btn_favorite.setImageResource(R.drawable.icon_unfavorite);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.v(TAG, "onPrepareOptionsMenu");
        super.onPrepareOptionsMenu(menu);
    }

    public void onResume()
    {
        super.onResume();
        shareKural();
        MainActivity.CURRENT_FRAGMENT = TAG;
        hideKeyboard();
    }

    @Override
    public void onPause() {
        MainActivity.share_kural = "";
        super.onPause();
    }

    public void hideKeyboard(){
        Utilities.hideKeyboardFrom(this.getContext(),this.getView().getRootView());
    }

    public void shareKural(){
        if(new Utilities().isEnglishEnabled(this.getContext())) {
            MainActivity.share_kural = KURALS_ID + "\n" +
                    KURALS_NAME + "\n\n" +
                    KURALS_NAME_ENG + "\n\n" +
                    SECTION_NAME + "/" + GROUP_NAME + "/" + CHAPTER_NAME + "\n\n";
        }else{
            MainActivity.share_kural = KURALS_ID + "\n" +
                    KURALS_NAME + "\n\n" +
                    SECTION_NAME + "/" + GROUP_NAME + "/" + CHAPTER_NAME + "\n\n";
        }
    }
}
