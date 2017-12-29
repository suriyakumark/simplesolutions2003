package com.simplesolutions2003.onceuponatime;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.simplesolutions2003.onceuponatime.data.AppContract;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.BatchUpdateException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.provider.Settings.Secure;
import org.json.JSONObject;
/**
 * Created by SuriyaKumar on 8/20/2016.
 */
public class SubmitArticle extends Fragment{
    public final static boolean KEEP_IN_STACK = true;
    public final static String TAG = SubmitArticle.class.getSimpleName();
    public final static String ARTICLE_SUBMIT_URL = "http://www.suriyakumar.com/api/articles/t1/submitArticles.php";
    public final static int STORY_SUBMIT_LIMIT = 3;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    static TextView tvEmail;
    static TextView tvTitle;
    static TextView tvContent;
    static Button btnSubmit;
    HttpAsyncTask httpAsyncTask;

    Context context;

    public SubmitArticle(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        context = SubmitArticle.this.getContext();
        //dPosition = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.submit_article, container, false);
        tvEmail = (TextView) rootView.findViewById(R.id.email_id);
        tvTitle = (TextView) rootView.findViewById(R.id.title);
        tvContent = (TextView) rootView.findViewById(R.id.content);
        btnSubmit = (Button) rootView.findViewById(R.id.submit);

        tvEmail.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putString(getString(R.string.pref_key_email), s.toString());
                editor.commit();
            }
        });

        tvTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putString(getString(R.string.pref_key_title), s.toString());
                editor.commit();
            }
        });

        tvContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putString(getString(R.string.pref_key_content), s.toString());
                editor.commit();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(tvEmail.getText().toString()) &&
                        Patterns.EMAIL_ADDRESS.matcher(tvEmail.getText().toString()).matches() &&
                        !TextUtils.isEmpty(tvTitle.getText().toString()) &&
                        !TextUtils.isEmpty(tvContent.getText().toString())) {
                    httpAsyncTask = new HttpAsyncTask();
                    httpAsyncTask.execute();
                }else{
                    if(!Patterns.EMAIL_ADDRESS.matcher(tvEmail.getText().toString()).matches()){
                        Toast.makeText(context, context.getString(R.string.msg_email_error), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, context.getString(R.string.msg_fields_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        tvEmail.setText(prefs.getString(SubmitArticle.this.getContext().getString(R.string.pref_key_email), ""));
        tvTitle.setText(prefs.getString(SubmitArticle.this.getContext().getString(R.string.pref_key_title), ""));
        tvContent.setText(prefs.getString(SubmitArticle.this.getContext().getString(R.string.pref_key_content), ""));

        if(getSubmitCount() >= STORY_SUBMIT_LIMIT ){
            btnSubmit.setEnabled(false);
            Toast.makeText(context, context.getString(R.string.msg_article_limit), Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu,inflater);
        MainActivity.bSearchEnabled = false;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.v(TAG, "onPrepareOptionsMenu");
        super.onPrepareOptionsMenu(menu);
    }

    public void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
        hideKeyboard();
    }

    public void hideKeyboard(){
        Utilities.hideKeyboardFrom(this.getContext(),this.getView().getRootView());
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return submitArticleToServer();
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            if(result.equals("Success")){
                Toast.makeText(context, context.getString(R.string.msg_article_submitted), Toast.LENGTH_LONG).show();
                incrementSubmitCount();
                if(getSubmitCount() >= STORY_SUBMIT_LIMIT ){
                    btnSubmit.setEnabled(false);
                    Toast.makeText(context, context.getString(R.string.msg_article_limit), Toast.LENGTH_SHORT).show();
                }
                hideKeyboard();
                tvTitle.setText("");
                tvContent.setText("");
            }else{
                Toast.makeText(context, context.getString(R.string.msg_article_problem), Toast.LENGTH_LONG).show();
            }

        }
    }

    public String submitArticleToServer(){
        String url = ARTICLE_SUBMIT_URL;
        String result = "";
        try {

            String json = "";

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("email_id", tvEmail.getText().toString());
            jsonObject.accumulate("device", Secure.getString(context.getContentResolver(),
                    Secure.ANDROID_ID));
            jsonObject.accumulate("title", tvTitle.getText().toString());
            jsonObject.accumulate("content", tvContent.getText().toString());

            json = jsonObject.toString();
            Log.v(TAG,"json - "+json);

            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            result = response.body().string();

        } catch (IOException e) {
            Log.v(TAG, e.getLocalizedMessage());
        } catch (JSONException e){
            Log.v(TAG, e.getLocalizedMessage());
        }

        return result;
    }

    public String getDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String sDate = dateFormat.format(calendar.getTime());
        return sDate;
    }

    public void incrementSubmitCount(){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String story_date = prefs.getString(context.getString(R.string.pref_story_submit_date), "");
        int story_count = prefs.getInt(context.getString(R.string.pref_story_submit_count), 0);

        if(story_date.equals(getDate())){
            story_count = story_count + 1;
        }else{
            story_count = 1;
        }

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(getString(R.string.pref_story_submit_date), getDate());
        editor.putInt(getString(R.string.pref_story_submit_count), story_count);
        editor.commit();

    }

    public int getSubmitCount(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String story_date = prefs.getString(context.getString(R.string.pref_story_submit_date), "");
        int story_count = prefs.getInt(context.getString(R.string.pref_story_submit_count), 0);
        if(story_date.equals(getDate())){
            return story_count;
        }else{
            return 0;
        }
    }
}
