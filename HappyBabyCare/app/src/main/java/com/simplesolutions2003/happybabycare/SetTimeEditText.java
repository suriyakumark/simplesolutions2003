package com.simplesolutions2003.happybabycare;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.TimePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by SuriyaKumar on 8/29/2016.
 */
public class SetTimeEditText implements View.OnFocusChangeListener, TimePickerDialog.OnTimeSetListener {
    private EditText editText;
    private Calendar myCalendar;
    private Context ctx;

    public SetTimeEditText(EditText editText, Context ctx){
        this.editText = editText;
        this.ctx = ctx;
        this.editText.setOnFocusChangeListener(this);
        myCalendar = Calendar.getInstance();
    }

    @Override
    public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute)     {
        editText.setText(String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // TODO Auto-generated method stub
        if(hasFocus){
            new TimePickerDialog(ctx, this, myCalendar
                    .get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE),true).show();
        }
    }

}
