package com.simplesolutions2003.happybabycare;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.simplesolutions2003.happybabycare.data.AppContract;

/**
 * Created by SuriyaKumar on 9/9/2016.
 */
public class ShareListAdapter  extends CursorAdapter implements View.OnClickListener {

    private final String TAG = ShareListAdapter.class.getSimpleName();
    private Context context;

    public static class ViewHolder {

        public final TextView memberEmail;
        public final ImageButton memberDelete;

        public ViewHolder(View view) {
            memberEmail = (TextView) view.findViewById(R.id.user_id);
            memberDelete = (ImageButton) view.findViewById(R.id.member_delete);
        }
    }


    public ShareListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.v(TAG, "newView");
        int layoutId = R.layout.manage_subscribe_list_item;
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
            Log.v(TAG,cursor.getString(BabyProfileFragment.COL_SHARE_USER_ID));
            viewHolder.memberEmail.setText(cursor.getString(BabyProfileFragment.COL_SHARE_USER_ID));

            viewHolder.memberDelete.setTag(new String[]{Long.toString(cursor.getLong(BabyProfileFragment.COL_SHARE_ID))});
            viewHolder.memberDelete.setOnClickListener(this);

            viewHolder.memberEmail.setContentDescription(viewHolder.memberEmail.getText().toString());
            viewHolder.memberDelete.setContentDescription(context.getString(R.string.btn_share_member_remove) + " " + viewHolder.memberEmail.getText().toString());
        }

    }

    public void onClick(View v) {

        Log.v(TAG, "onClick" + v.getTag());
        String[] params = (String[]) v.getTag();
        Long _id = Long.parseLong(params[0]);

        switch(v.getId()) {
            case R.id.member_delete:
                Uri buildGroup = AppContract.ShareEntry.CONTENT_URI;
                String sSelection = AppContract.ShareEntry._ID + " = ? ";
                String[] sSelectionArgs = new String[]{Long.toString(_id)};
                context.getContentResolver().delete(buildGroup,sSelection,sSelectionArgs);
                break;

        }
    }

}
