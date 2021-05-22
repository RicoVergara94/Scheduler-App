package edu.umb.cs.alarm_app_1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.DatePickerDialog;
import android.content.Context;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.util.Calendar;


import java.util.Calendar;
public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private TextView mTextView;
    private TextView dTextView; // TextView for the date
    private StringBuffer buffer_date_time; // Must be in format of 2016-01-01 10:20:05.123
    private String time_of_task;// What we want next to task in task-activity
    private String date_of_task;
    private String date_time_task;
    private Boolean bool_date_chosen; // In order to make sure before stored in database a date is chosen
    private static int _id; // the unique id for the alarms
    private static Boolean bool_time_set; // If no date was chosen then the alarm is set for current date
    public static String TIME_MESSAGE;
    public static String DATE_MESSAGE;
    public static String DATE_TIME_MESSAGE;
    private int num_alarms;
    private int year;
    private int month;
    private int day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.textView);
        dTextView = findViewById(R.id.date_Text_View);
        Button buttonTimePicker = findViewById(R.id.button_timepicker);
        buttonTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        Button buttonCancelAlarm = findViewById(R.id.button_cancel);
        buttonCancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });

    }
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    @Override

    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)

    {
        // Create a Calender instance
        Calendar mCalender = Calendar.getInstance();
        this.year = year;
        this.month = month;
        day = dayOfMonth;
        // Set static variables of Calender instance
        mCalender.set(Calendar.YEAR,year);
        mCalender.set(Calendar.MONTH,month);
        mCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        // Get the date in form of string
        String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(mCalender.getTime());
        date_of_task = selectedDate;
        // Set the textview to the selectedDate String
        dTextView.setText(selectedDate);
        FieldPosition pos = new FieldPosition(DateFormat.YEAR_FIELD);
        FieldPosition pos2 = new FieldPosition(DateFormat.MONTH_FIELD);
        FieldPosition pos3 = new FieldPosition(DateFormat.DAY_OF_WEEK_IN_MONTH_FIELD);


    }

    public void Switch_To_Do(View view) {
        Intent intent = new Intent(MainActivity.this, ToDo_Activity.class);

        /*EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message); */
        date_time_task = date_of_task + " " + time_of_task;

        intent.putExtra(DATE_TIME_MESSAGE, date_time_task);


        startActivity(intent);
    }
    public static boolean time_set() { return bool_time_set; }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        updateTimeText(c); // will need to change this
        startAlarm(c);

    }
    private void updateTimeText(Calendar c) {
        int an_alarm = 1;
        num_alarms += an_alarm;
        String timeText = Integer.toString(num_alarms) + " number of alarms are set.";
        //timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        time_of_task = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        bool_time_set = true;

        mTextView.setText(timeText);
    }
    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        final int id = (int) System.currentTimeMillis();
        //set_unique_id(id);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_ONE_SHOT);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0); original line, line above was added
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

    }


 /*   public void set_unique_id(int unique_id) {
        _id = unique_id;
    }
    public static int get_unique_id() {
        return _id;
    }
    public static void reset_id() {
        _id = -1;
    }
  */


    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.cancel(pendingIntent);
        mTextView.setText("Alarm canceled");
    }
}
