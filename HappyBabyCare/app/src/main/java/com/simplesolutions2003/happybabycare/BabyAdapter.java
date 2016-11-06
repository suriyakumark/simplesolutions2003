package com.simplesolutions2003.happybabycare;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simplesolutions2003.happybabycare.data.AppContract;
import com.squareup.picasso.Picasso;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by SuriyaKumar on 8/30/2016.
 */
public class BabyAdapter extends CursorAdapter implements View.OnClickListener {

    private final String TAG = BabyAdapter.class.getSimpleName();
    private Context context;

    private static final String[] SUMMARY_COLUMNS = {
            AppContract.ActivitiesEntry.COLUMN_ACTIVITY_ID,
            AppContract.ActivitiesEntry.COLUMN_SUMMARY,
            AppContract.ActivitiesEntry.COLUMN_DETAIL
    };

    static final int COL_SUMMARY_ID = 0;
    static final int COL_SUMMARY_TYPE = 1;
    static final int COL_SUMMARY_DETAIL = 2;

    public static class ViewHolder {

        public final ImageView babyProfilePhoto;
        public final TextView babyName;
        public final LinearLayout babySummary;
        public final ImageButton babySelect;
        public final ImageButton babyEdit;

        public ViewHolder(View view) {
            babyProfilePhoto = (ImageView) view.findViewById(R.id.baby_prof_image);
            babyName = (TextView) view.findViewById(R.id.baby_name);
            babySummary = (LinearLayout) view.findViewById(R.id.baby_summary);
            babySelect = (ImageButton) view.findViewById(R.id.baby_select);
            babyEdit = (ImageButton) view.findViewById(R.id.baby_edit);
        }
    }


    public BabyAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.v(TAG, "newView");
        int layoutId = R.layout.baby_list_item;
        View view = (View) LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        Uri query_summary_uri = AppContract.ActivitiesEntry.buildActivitiesSummaryByUserIdBabyIdUri(MainActivity.LOGGED_IN_USER_ID,cursor.getLong(BabyFragment.COL_BABY_ID),new Utilities().getCurrentDateDB());
        Cursor summaryCursor = context.getContentResolver().query(query_summary_uri,SUMMARY_COLUMNS,null,null,null);
        if(summaryCursor != null){
            if(summaryCursor.getCount() > 0){
                Log.v(TAG, "summaryCursor " + summaryCursor.getCount());
                while(summaryCursor.moveToNext()) {
                    if(summaryCursor.getString(COL_SUMMARY_TYPE) != null & summaryCursor.getString(COL_SUMMARY_DETAIL) != null) {
                        Log.v(TAG, "summaryCursor " + summaryCursor.getString(COL_SUMMARY_TYPE) + " " + summaryCursor.getString(COL_SUMMARY_DETAIL));
                        TextView feedingInfo = new TextView(context);
                        feedingInfo.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        feedingInfo.setText(summaryCursor.getString(COL_SUMMARY_TYPE) + " " + summaryCursor.getString(COL_SUMMARY_DETAIL));
                        viewHolder.babySummary.addView(feedingInfo);
                    }
                }
            }
            summaryCursor.close();
        }

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.v(TAG, "bindView");
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if(cursor != null){
            Uri babyProfilePhotoUri = null;
            if(cursor.getString(BabyFragment.COL_BABY_PHOTO) != null) {
                babyProfilePhotoUri = Uri.parse(cursor.getString(BabyFragment.COL_BABY_PHOTO));
            }else{
                babyProfilePhotoUri = null;
            }
            Log.v(TAG, "babyProfilePhotoUri - " + babyProfilePhotoUri);

            if(babyProfilePhotoUri != null) {

                ParcelFileDescriptor parcelFD = null;
                try {
                    parcelFD = context.getContentResolver().openFileDescriptor(babyProfilePhotoUri, "r");
                    FileDescriptor imageSource = parcelFD.getFileDescriptor();
                    Bitmap origBitmap = BitmapFactory.decodeFileDescriptor(imageSource, null, null);
                    int nh = (int) ( origBitmap.getHeight() * (512.0 / origBitmap.getWidth()) );
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(origBitmap, 512, nh, true);
                    viewHolder.babyProfilePhoto.setImageBitmap(scaledBitmap);
                }catch (FileNotFoundException e){
                    Log.e(TAG,e.getMessage());
                }finally {
                    if (parcelFD != null)
                        try {
                            parcelFD.close();
                        } catch (IOException e) {
                            Log.e(TAG,e.getMessage());
                        }
                }
            }else{
                viewHolder.babyProfilePhoto.setImageDrawable(context.getDrawable(R.drawable.logo));
            }
            viewHolder.babyName.setText(cursor.getString(BabyFragment.COL_BABY_NAME));

            viewHolder.babySelect.setTag(new String[]{Long.toString(cursor.getLong(BabyFragment.COL_BABY_ID)),
                    cursor.getString(BabyFragment.COL_BABY_NAME)});
            viewHolder.babySelect.setOnClickListener(this);

            if(cursor.getCount() == 1 & MainActivity.ACTIVE_BABY_ID == -1){
                MainActivity.ACTIVE_BABY_ID = cursor.getLong(BabyFragment.COL_BABY_ID);
                MainActivity.ACTIVE_BABY_NAME = cursor.getString(BabyFragment.COL_BABY_NAME);
            }

            if(cursor.getLong(BabyFragment.COL_BABY_ID) == MainActivity.ACTIVE_BABY_ID){
                viewHolder.babySelect.setImageDrawable(context.getDrawable(R.drawable.select_icon_active));
                viewHolder.babySelect.setContentDescription(context.getString(R.string.cd_selected));
            }else{
                viewHolder.babySelect.setImageDrawable(context.getDrawable(R.drawable.select_icon_default));
                viewHolder.babySelect.setContentDescription(context.getString(R.string.cd_not_selected));
            }

            viewHolder.babyEdit.setTag(new String[]{Long.toString(cursor.getLong(BabyFragment.COL_BABY_ID)),
                    cursor.getString(BabyFragment.COL_BABY_NAME)});
            viewHolder.babyEdit.setOnClickListener(this);

            viewHolder.babyName.setContentDescription(viewHolder.babyName.getText().toString());
            viewHolder.babyEdit.setContentDescription(context.getString(R.string.cd_baby_icon_edit));
        }

    }

    public void onClick(View v) {

        Log.v(TAG, "onClick" + v.getTag());
        String[] params = (String[]) v.getTag();
        Long babyId = Long.parseLong(params[0]);
        String babyName = params[1];

        switch(v.getId()) {
            case R.id.baby_select:
                MainActivity.ACTIVE_BABY_ID = babyId;
                MainActivity.ACTIVE_BABY_NAME = babyName;
                ((MainActivity) context).handleFragments(new ActivitiesFragment(), ActivitiesFragment.TAG, ActivitiesFragment.KEEP_IN_STACK);
                break;
            case R.id.baby_edit:
                MainActivity.ACTIVE_BABY_ID = babyId;
                ((MainActivity) context).handleFragments(new BabyProfileFragment(),BabyProfileFragment.TAG,BabyProfileFragment.KEEP_IN_STACK);
                break;
        }
    }

}
