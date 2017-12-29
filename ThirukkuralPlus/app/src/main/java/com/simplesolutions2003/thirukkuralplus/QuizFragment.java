package com.simplesolutions2003.thirukkuralplus;

import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.plattysoft.leonids.ParticleSystem;
import com.simplesolutions2003.thirukkuralplus.data.AppContract;

import java.util.Random;

/**
 * Created by SuriyaKumar on 8/20/2016.
 */
public class QuizFragment extends Fragment{
    public final static boolean KEEP_IN_STACK = true;
    public final static String TAG = QuizFragment.class.getSimpleName();

    public static long CHAPTER_ID = -1;

    static TextView word1;
    static TextView word2;
    static TextView word3;
    static TextView word4;
    static TextView word5;
    static TextView word6;
    static TextView word7;
    static LinearLayout heading;
    static ImageButton evaluate;
    static ImageButton refresh;
    static TextView appMenuHeading;
    static TextView appMenuSubHeading;

    int editableWord;
    String editableWordAnswer;

    public QuizFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG,"onCreateView");
        View rootView = inflater.inflate(R.layout.quiz, container, false);

        heading = (LinearLayout) rootView.findViewById (R.id.quiz_layout);
        appMenuHeading = (TextView) rootView.findViewById(R.id.app_menu_heading);
        appMenuSubHeading = (TextView) rootView.findViewById(R.id.app_menu_subheading);
        word1 = (TextView) rootView.findViewById(R.id.word1);
        word2 = (TextView) rootView.findViewById(R.id.word2);
        word3 = (TextView) rootView.findViewById(R.id.word3);
        word4 = (TextView) rootView.findViewById(R.id.word4);
        word5 = (TextView) rootView.findViewById(R.id.word5);
        word6 = (TextView) rootView.findViewById(R.id.word6);
        word7 = (TextView) rootView.findViewById(R.id.word7);
        evaluate = (ImageButton) rootView.findViewById(R.id.btn_evaluate);
        refresh = (ImageButton) rootView.findViewById(R.id.btn_refresh);

        if(new Utilities().isEnglishEnabled(this.getContext())) {
            appMenuHeading.setText(getString(R.string.nav_quiz_eng));
        }else{
            appMenuHeading.setText(getString(R.string.nav_quiz));
        }

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newQuiz();
            }
        });

        evaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editableWordAnswered = "";
                switch (editableWord){
                    case 0:
                        editableWordAnswered = word1.getText().toString();
                        break;
                    case 1:
                        editableWordAnswered = word2.getText().toString();
                        break;
                    case 2:
                        editableWordAnswered = word3.getText().toString();
                        break;
                    case 3:
                        editableWordAnswered = word4.getText().toString();
                        break;
                    case 4:
                        editableWordAnswered = word5.getText().toString();
                        break;
                    case 5:
                        editableWordAnswered = word6.getText().toString();
                        break;
                    case 6:
                        editableWordAnswered = word7.getText().toString();
                        break;
                }
                if(editableWordAnswered.isEmpty() || editableWordAnswered.equals("")) {
                    Toast toast1 = Toast.makeText(QuizFragment.this.getContext(),
                            QuizFragment.this.getContext().getString(R.string.msg_empty_answer),
                            Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.CENTER, 0, 0);
                    toast1.show();
                }else if(editableWordAnswer.equals(editableWordAnswered)){
                    evaluate.setImageResource(R.drawable.icon_right);
                    new Utilities().addScore(QuizFragment.this.getContext());
                    updateScore();
                    evaluate.setEnabled(false);
                    Toast toast2 = Toast.makeText(QuizFragment.this.getContext(),
                            QuizFragment.this.getContext().getString(R.string.msg_right_answer),
                            Toast.LENGTH_SHORT);
                    toast2.setGravity(Gravity.CENTER, 0, 0);
                    toast2.show();
                    if(new Utilities().getScore(QuizFragment.this.getContext()) % 100 == 0){
                        new ParticleSystem(QuizFragment.this.getActivity(), 100, R.drawable.firework, 3000)
                                .setSpeedRange(0.2f, 0.5f)
                                .oneShot(evaluate, 100);
                    }else if(new Utilities().getScore(QuizFragment.this.getContext()) % 50 == 0){
                        new ParticleSystem(QuizFragment.this.getActivity(), 100, R.drawable.star, 2000)
                                .setSpeedRange(0.2f, 0.5f)
                                .oneShot(evaluate, 100);
                    }
                }else {
                    evaluate.setImageResource(R.drawable.icon_wrong);
                    new Utilities().minusScore(QuizFragment.this.getContext());
                    updateScore();
                    Toast toast3 = Toast.makeText(QuizFragment.this.getContext(),
                            QuizFragment.this.getContext().getString(R.string.msg_wrong_answer),
                            Toast.LENGTH_SHORT);
                    toast3.setGravity(Gravity.CENTER, 0, 0);
                    toast3.show();
                }
            }
        });

        newQuiz();

        return rootView;
    }

    private void updateScore(){
        appMenuSubHeading.setText(getString(R.string.text_score) + new Utilities().getScore(this.getContext()));
    }

    private void newQuiz(){
        Log.v(TAG,"newQuiz");

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            Toast toast4;
            if(new Utilities().isEnglishEnabled(this.getContext())) {
                toast4 = Toast.makeText(QuizFragment.this.getContext(),
                        QuizFragment.this.getContext().getString(R.string.msg_rotate_eng),
                        Toast.LENGTH_SHORT);
            }else{
                toast4 = Toast.makeText(QuizFragment.this.getContext(),
                        QuizFragment.this.getContext().getString(R.string.msg_rotate),
                        Toast.LENGTH_SHORT);
            }

            toast4.setGravity(Gravity.CENTER, 0, 0);
            toast4.show();
        }


        updateScore();

        int number;
        if(CHAPTER_ID == -1) {
            number = (new Random().nextInt(300));
        }else{
            number = (new Random().nextInt(10));
            number = number + (((int) CHAPTER_ID - 1) * 10);
        }
        number++;

        long title_num = -1;
        String kural_name = "";

        Cursor widgetRow = this.getContext().getContentResolver().query(
                AppContract.KuralsEntry.buildKuralsUri(number),KuralsFragment.KURALS_COLUMNS,
                null,
                null,
                null);

        if(widgetRow != null){
            if(widgetRow.getCount() > 0) {
                widgetRow.moveToFirst();
                title_num = widgetRow.getLong(KuralsFragment.COL_KURALS_ID);
                kural_name = widgetRow.getString(KuralsFragment.COL_KURALS_NAME);
            }
            widgetRow.close();
        }

        kural_name = kural_name.replaceAll("\\r|\\n", " ");
        kural_name = kural_name.replaceAll("  ", " ");
        String[] kural_name_array = kural_name.split(" ");
        word1.setText(kural_name_array[0]);
        word2.setText(kural_name_array[1]);
        word3.setText(kural_name_array[2]);
        word4.setText(kural_name_array[3]);
        word5.setText(kural_name_array[4]);
        word6.setText(kural_name_array[5]);
        word7.setText(kural_name_array[6]);

        word1.setEnabled(false);
        word2.setEnabled(false);
        word3.setEnabled(false);
        word4.setEnabled(false);
        word5.setEnabled(false);
        word6.setEnabled(false);
        word7.setEnabled(false);

        word1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        word2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        word3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        word4.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        word5.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        word6.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        word7.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        editableWord = (new Random().nextInt(6));
        switch (editableWord){
            case 0:
                editableWordAnswer = word1.getText().toString();
                word1.setText("");
                word1.setLayoutParams(new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT));
                word1.setEnabled(true);
                break;
            case 1:
                editableWordAnswer = word2.getText().toString();
                word2.setText("");
                word2.setLayoutParams(new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT));
                word2.setEnabled(true);
                break;
            case 2:
                editableWordAnswer = word3.getText().toString();
                word3.setText("");
                word3.setLayoutParams(new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT));
                word3.setEnabled(true);
                break;
            case 3:
                editableWordAnswer = word4.getText().toString();
                word4.setText("");
                word4.setLayoutParams(new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT));
                word4.setEnabled(true);
                break;
            case 4:
                editableWordAnswer = word5.getText().toString();
                word5.setText("");
                word5.setLayoutParams(new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT));
                word5.setEnabled(true);
                break;
            case 5:
                editableWordAnswer = word6.getText().toString();
                word6.setText("");
                word6.setLayoutParams(new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT));
                word6.setEnabled(true);
                break;
            case 6:
                editableWordAnswer = word7.getText().toString();
                word7.setText("");
                word7.setLayoutParams(new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT));
                word7.setEnabled(true);
                break;
        }

        evaluate.setImageResource(R.drawable.icon_quiz);
        evaluate.setEnabled(true);

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
        MainActivity.CURRENT_FRAGMENT = TAG;
        hideKeyboard();
    }

    public void onPause(){
        super.onPause();
    }

    public void hideKeyboard(){
        Utilities.hideKeyboardFrom(this.getContext(),this.getView().getRootView());
    }



}
