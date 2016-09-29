package com.simplesolutions2003.happybabycare;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.simplesolutions2003.happybabycare.data.AppContract;
import com.squareup.picasso.Picasso;

/**
 * Created by SuriyaKumar on 8/23/2016.
 */
public class BabyProfileFragment extends Fragment {
    public final static boolean KEEP_IN_STACK = false;
    public final static String TAG = BabyProfileFragment.class.getSimpleName();

    ImageButton babyProfilePhoto;
    Uri babyProfilePhotoUri = null;
    EditText babyName;
    EditText babyBirthDate;
    EditText babyDueDate;
    RadioGroup babyGenderGroup;
    RadioButton babyGenderMale;
    RadioButton babyGenderFeMale;

    Bitmap image = null;
    Bitmap rotateImage = null;
    private static final int GALLERY = 1;

    private static final String[] BABY_COLUMNS = {
            AppContract.BabyEntry.TABLE_NAME + "." + AppContract.BabyEntry._ID,
            AppContract.BabyEntry.TABLE_NAME + "." + AppContract.BabyEntry.COLUMN_USER_ID,
            AppContract.BabyEntry.TABLE_NAME + "." + AppContract.BabyEntry.COLUMN_NAME,
            AppContract.BabyEntry.TABLE_NAME + "." + AppContract.BabyEntry.COLUMN_BIRTH_DATE,
            AppContract.BabyEntry.TABLE_NAME + "." + AppContract.BabyEntry.COLUMN_DUE_DATE,
            AppContract.BabyEntry.TABLE_NAME + "." + AppContract.BabyEntry.COLUMN_GENDER,
            AppContract.BabyEntry.TABLE_NAME + "." + AppContract.BabyEntry.COLUMN_PHOTO,
            AppContract.BabyEntry.TABLE_NAME + "." + AppContract.BabyEntry.COLUMN_ACTIVE
    };


    static final int COL_BABY_ID = 0;
    static final int COL_BABY_USER_ID = 1;
    static final int COL_BABY_NAME = 2;
    static final int COL_BABY_BIRTH_DATE = 3;
    static final int COL_BABY_DUE_DATE = 4;
    static final int COL_BABY_GENDER = 5;
    static final int COL_BABY_PHOTO = 6;
    static final int COL_BABY_ACTIVE = 7;

    public BabyProfileFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.baby_profile, container, false);
        babyProfilePhoto = (ImageButton) rootView.findViewById(R.id.baby_prof_image);
        babyName = (EditText) rootView.findViewById(R.id.baby_name);
        babyBirthDate = (EditText) rootView.findViewById(R.id.baby_birthdate);
        babyDueDate = (EditText) rootView.findViewById(R.id.baby_due_date);
        babyGenderGroup = (RadioGroup) rootView.findViewById(R.id.baby_gender);
        babyGenderMale = (RadioButton) rootView.findViewById(R.id.baby_gender_male);
        babyGenderFeMale = (RadioButton) rootView.findViewById(R.id.baby_gender_female);
        babyBirthDate.setInputType(InputType.TYPE_NULL);
        babyDueDate.setInputType(InputType.TYPE_NULL);

        babyName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateMenuVisibility();
            }
        });

        babyBirthDate.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                new Utilities(getActivity()).resetFocus(babyBirthDate);
                updateMenuVisibility();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        babyDueDate.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                new Utilities(getActivity()).resetFocus(babyDueDate);
                updateMenuVisibility();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        babyProfilePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "babyProfilePhoto");
                //babyProfilePhoto.setImageBitmap(null);
                if (image != null)
                    image.recycle();
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY);
            }
        });

        SetDateEditText setBirthDate = new SetDateEditText(babyBirthDate, getActivity());
        SetDateEditText setDueDate = new SetDateEditText(babyDueDate, getActivity());

        if(MainActivity.ACTIVE_BABY_ID != -1) {
            Uri baby_uri = AppContract.BabyEntry.buildBabyByUserIdBabyIdUri(MainActivity.LOGGED_IN_USER_ID,MainActivity.ACTIVE_BABY_ID);
            Cursor babyprofile = getActivity().getContentResolver().query(baby_uri,BABY_COLUMNS,null,null,null);
            if(babyprofile != null){
                if(babyprofile.getCount() > 0){
                    Log.v(TAG,"got baby profile");
                    babyprofile.moveToFirst();
                    babyName.setText(babyprofile.getString(COL_BABY_NAME));
                    babyBirthDate.setText(babyprofile.getString(COL_BABY_BIRTH_DATE));
                    babyDueDate.setText(babyprofile.getString(COL_BABY_DUE_DATE));
                    if(babyprofile.getString(COL_BABY_GENDER).equals(babyGenderMale.getText().toString())) {
                        babyGenderMale.setChecked(true);
                        babyGenderMale.setContentDescription(getString(R.string.cd_selected));
                        babyGenderFeMale.setContentDescription(getString(R.string.cd_not_selected));
                    }else{
                        babyGenderFeMale.setChecked(true);
                        babyGenderMale.setContentDescription(getString(R.string.cd_not_selected));
                        babyGenderFeMale.setContentDescription(getString(R.string.cd_selected));
                    }
                    if(babyprofile.getString(COL_BABY_PHOTO) != null){
                        babyProfilePhotoUri = Uri.parse(babyprofile.getString(COL_BABY_PHOTO));
                        setBabyProfilePhoto();
                    }

                    babyName.setContentDescription(babyName.getText().toString());
                    babyBirthDate.setContentDescription(babyBirthDate.getText().toString());
                    babyDueDate.setContentDescription(babyDueDate.getText().toString());

                }
            }else{
                MainActivity.ACTIVE_BABY_ID = -1;
            }
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        validateInputs();
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_save:
                actionSave();
                return true;
            case R.id.action_delete:
                if(MainActivity.ACTIVE_BABY_ID != -1) {
                    actionDelete();
                }
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateMenuVisibility(){
        validateInputs();
        ActivityCompat.invalidateOptionsMenu(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GALLERY && resultCode != 0) {
            babyProfilePhotoUri = data.getData();
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            int takeFlags = intent.getFlags();
            takeFlags &=  (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            getActivity().getContentResolver().takePersistableUriPermission(babyProfilePhotoUri, takeFlags);
            setBabyProfilePhoto();
        }
    }

    public void validateInputs() {
        if(babyName.getText().toString().isEmpty() |
            babyBirthDate.getText().toString().isEmpty()){
            MainActivity.saveMenuEnabled = false;
        }else{
            MainActivity.saveMenuEnabled = true;
        }

        if (MainActivity.ACTIVE_BABY_ID == -1) {
            MainActivity.deleteMenuEnabled = false;
        } else {
            MainActivity.deleteMenuEnabled = true;
        }
    }

    public void setBabyProfilePhoto(){
        Log.v(TAG,"setBabyProfilePhoto - " + babyProfilePhotoUri.toString());

        if(babyProfilePhotoUri != null) {
            ParcelFileDescriptor parcelFD = null;
            try {
                parcelFD = getActivity().getContentResolver().openFileDescriptor(babyProfilePhotoUri, "r");
                FileDescriptor imageSource = parcelFD.getFileDescriptor();
                Bitmap origBitmap = BitmapFactory.decodeFileDescriptor(imageSource, null, null);
                int nh = (int) ( origBitmap.getHeight() * (512.0 / origBitmap.getWidth()) );
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(origBitmap, 512, nh, true);

                babyProfilePhoto.setImageBitmap(scaledBitmap);
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
            babyProfilePhoto.setImageDrawable(getActivity().getDrawable(R.drawable.logo));
        }
    }

    public void actionSave(){
        Log.v(TAG, "actionSave");
        Uri baby_uri = AppContract.BabyEntry.CONTENT_URI;

        ContentValues newValues = new ContentValues();
        newValues.put(AppContract.BabyEntry.COLUMN_USER_ID, MainActivity.LOGGED_IN_USER_ID);
        newValues.put(AppContract.BabyEntry.COLUMN_NAME , babyName.getText().toString());
        newValues.put(AppContract.BabyEntry.COLUMN_BIRTH_DATE, babyBirthDate.getText().toString());
        newValues.put(AppContract.BabyEntry.COLUMN_DUE_DATE, babyDueDate.getText().toString());

        if(babyGenderGroup.getCheckedRadioButtonId() == babyGenderMale.getId()) {
            newValues.put(AppContract.BabyEntry.COLUMN_GENDER, babyGenderMale.getText().toString());
        }else{
            newValues.put(AppContract.BabyEntry.COLUMN_GENDER, babyGenderFeMale.getText().toString());
        }
        if(babyProfilePhotoUri != null) {
            newValues.put(AppContract.BabyEntry.COLUMN_PHOTO, babyProfilePhotoUri.toString());
        }

        MainActivity.ACTIVE_BABY_NAME = babyName.getText().toString();
        if(MainActivity.ACTIVE_BABY_ID == -1) {
            MainActivity.ACTIVE_BABY_ID = AppContract.BabyEntry.getIdFromUri(getActivity().getContentResolver().insert(baby_uri, newValues));
            if(MainActivity.ACTIVE_BABY_ID == -1){
                Toast.makeText(getActivity(), getString(R.string.text_baby_cannot_save), Toast.LENGTH_LONG).show();
                MainActivity.ACTIVE_BABY_NAME = null;
            }else{
                Toast.makeText(getActivity(), getString(R.string.text_baby_added), Toast.LENGTH_LONG).show();
            }
        }else{
            String babyWhere = AppContract.BabyEntry._ID + " = ? AND " + AppContract.BabyEntry.COLUMN_USER_ID + " = ? ";
            String[] babyWhereArgs = new String[]{Long.toString(MainActivity.ACTIVE_BABY_ID),MainActivity.LOGGED_IN_USER_ID};
            getActivity().getContentResolver().update(baby_uri, newValues, babyWhere,babyWhereArgs);
            Toast.makeText(getActivity(), getString(R.string.text_baby_saved), Toast.LENGTH_LONG).show();
        }
        goBack();
    }

    public void actionDelete(){
        Log.v(TAG, "actionDelete");

        Uri baby_uri = AppContract.BabyEntry.CONTENT_URI;
        String babyWhere = AppContract.BabyEntry._ID + " = ? AND " + AppContract.BabyEntry.COLUMN_USER_ID + " = ? ";
        String[] babyWhereArgs = new String[]{Long.toString(MainActivity.ACTIVE_BABY_ID),MainActivity.LOGGED_IN_USER_ID};
        getActivity().getContentResolver().delete(baby_uri, babyWhere,babyWhereArgs);
        Toast.makeText(getActivity(), getString(R.string.text_baby_deleted), Toast.LENGTH_LONG).show();
        MainActivity.ACTIVE_BABY_ID = -1;
        MainActivity.ACTIVE_BABY_NAME = null;
        goBack();
    }

    public void goBack(){
        Log.v(TAG, "goBack");
        ((MainActivity) getActivity()).handleFragments(new BabyFragment(),BabyFragment.TAG,BabyFragment.KEEP_IN_STACK);
        //((MainActivity) getActivity()).handleFragments(true);
    }
}
