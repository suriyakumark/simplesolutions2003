package com.simplesolutions2003.happybabycare;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.simplesolutions2003.happybabycare.data.AppContract;

/**
 * Created by SuriyaKumar on 8/20/2016.
 */
public class GroupFragment extends Fragment {
    public final static boolean KEEP_IN_STACK = false;
    public final static String TAG = GroupFragment.class.getSimpleName();

    EditText joinGroupId;
    Button joinGroupButton;
    Button createGroupButton;
    Button skipGroupButton;

    public GroupFragment(){}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.group, container, false);
        joinGroupId = (EditText) rootView.findViewById(R.id.group_id);
        joinGroupButton = (Button) rootView.findViewById(R.id.group_join);
        createGroupButton = (Button) rootView.findViewById(R.id.group_create);
        skipGroupButton = (Button) rootView.findViewById(R.id.group_skip);

        joinGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //////////////////////////////////////////////////////
                ///check the server to see if this group id exists///
                /////////////////////////////////////////////////////
                Uri uri = AppContract.UserEntry.CONTENT_URI;
                String sSelection = AppContract.UserEntry._ID + " = ?";
                String[] sSelectionArgs = new String[]{Long.toString(MainActivity.LOGGED_IN_USER_ID_ID)};
                ContentValues updateValues = new ContentValues();
                updateValues.put(AppContract.UserEntry.COLUMN_GROUP_ID,joinGroupId.getText().toString());
                getActivity().getContentResolver().update(uri,updateValues,sSelection,sSelectionArgs);
                MainActivity.GROUP_ID = joinGroupId.getText().toString();
                ((MainActivity) getActivity()).handleFragments(new BabyFragment(),BabyFragment.TAG,BabyFragment.KEEP_IN_STACK);
            }
        });

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = AppContract.UserEntry.CONTENT_URI;
                String sSelection = AppContract.UserEntry._ID + " = ?";
                String[] sSelectionArgs = new String[]{Long.toString(MainActivity.LOGGED_IN_USER_ID_ID)};
                ContentValues updateValues = new ContentValues();
                updateValues.put(AppContract.UserEntry.COLUMN_GROUP_ID,MainActivity.LOGGED_IN_USER_ID);
                getActivity().getContentResolver().update(uri,updateValues,sSelection,sSelectionArgs);
                MainActivity.GROUP_ID = MainActivity.LOGGED_IN_USER_ID;
                ((MainActivity) getActivity()).handleFragments(new GroupManageFragment(),GroupManageFragment.TAG,GroupManageFragment.KEEP_IN_STACK);
            }
        });

        skipGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).handleFragments(new BabyFragment(),BabyFragment.TAG,BabyFragment.KEEP_IN_STACK);
            }
        });

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu,inflater);
        ((MainActivity) getActivity()).disableActionEditMenus();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.v(TAG, "onPrepareOptionsMenu");
        super.onPrepareOptionsMenu(menu);
    }
}
