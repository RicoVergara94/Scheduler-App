package edu.umb.cs.alarm_app_1;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class ToDo_Activity extends Activity {

    protected DBHelper mDBHelper;
    private List<ToDo_Item> list;
    private MyAdapter adapt;
    private EditText myTask;
    private String date_time_of_task;

    //private DatePickerDialog datepicker;
    //private Simple

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TASK 1: LAUNCH THE LAYOUT REPRESENTING THE MAIN ACTIVITY
        setContentView(R.layout.todo_screen);
        Intent intent = getIntent();
        String date_time_message = intent.getStringExtra(MainActivity.DATE_TIME_MESSAGE);


        // TASK 2: ESTABLISH REFERENCES TO THE UI
        //      ELEMENTS LOCATED ON THE LAYOUT
        myTask = (EditText) findViewById(R.id.editText1);
        date_time_of_task = date_time_message;


        //datepicker = findViewById(R.) maybe I should just do something on timeset

        // TASK 3: SET UP THE DATABASE
        mDBHelper = new DBHelper(this);
    }

    @Override
    protected void onResume(){
        super.onResume();

        list = mDBHelper.getAllTasks();
        adapt = new MyAdapter(this, R.layout.todo_item, list);

        ListView listTask = (ListView) findViewById(R.id.listView1);

        listTask.setAdapter(adapt);
    }



    //BUTTON CLICK EVENT FOR ADDING A TODO TASK
    public void addTaskNow(View view) {
        // String date_and_time = myTask.getText().toString() may need to find a way to get the dates differently
        String s = date_time_of_task + " " + myTask.getText().toString(); // changed variable name from s to task_description
        if (s.isEmpty()) { // changed task_description to s
            Toast.makeText(getApplicationContext(), "A TODO task must be entered.", Toast.LENGTH_SHORT).show();
        }
        /*if (!MainActivity.time_set()) {
            Toast.makeText(getApplicationContext(), "A time must be entered.", Toast.LENGTH_SHORT).show();
        } */
        else {

            //BUILD A NEW TASK ITEM AND ADD IT TO THE DATABASE
            ToDo_Item task = new ToDo_Item(s, 0); // changed task description back to s

            mDBHelper.addToDoItem(task);

            // CLEAR OUT THE TASK EDITVIEW
            myTask.setText("");

            // ADD THE TASK AND SET A NOTIFICATION OF CHANGES
            adapt.add(task);
            adapt.notifyDataSetChanged();

        }
    }

    //BUTTON CLICK EVENT FOR DELETING ALL TODO TASKS
    public void clearTasks(View view) {
        mDBHelper.clearAll(list);
        adapt.notifyDataSetChanged();
    }

    //BUTTON CLICK EVENT FOR DELETING THE FINISHED TODO TASKS
    public void deleteDone(View view) {
        mDBHelper.deleteSelected(list);
        adapt.notifyDataSetChanged();
    }

    //******************* ADAPTER ******************************
    private class MyAdapter extends ArrayAdapter<ToDo_Item> {
        Context context;
        List<ToDo_Item> taskList = new ArrayList<ToDo_Item>();

        public MyAdapter(Context c, int rId, List<ToDo_Item> objects) {
            super(c, rId, objects);
            taskList = objects;
            context = c;
        }

        //******************* TODO TASK ITEM VIEW ******************************
        /**
         * THIS METHOD DEFINES THE TODO ITEM THAT WILL BE PLACED
         * INSIDE THE LIST VIEW.
         *
         * THE CHECKBOX STATE IS THE IS_DONE STATUS OF THE TODO TASK
         * AND THE CHECKBOX TEXT IS THE TODO_ITEM TASK DESCRIPTION.
         */

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CheckBox isDoneChBx = null;
            if (convertView == null) {

                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.todo_item, parent, false);

                isDoneChBx = (CheckBox) convertView.findViewById(R.id.chkStatus);
                convertView.setTag(isDoneChBx);

                isDoneChBx.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CheckBox cb = (CheckBox) view;
                        ToDo_Item changeTask = (ToDo_Item) cb.getTag();
                        changeTask.setIs_done(cb.isChecked() == true ? 1 : 0);
                        mDBHelper.updateTask(changeTask);
                    }
                });

            } else {
                isDoneChBx = (CheckBox) convertView.getTag();//convertView.findViewById(R.id.chkStatus);
            }
            ToDo_Item current = taskList.get(position);
            isDoneChBx.setText(current.getDescription());
            isDoneChBx.setChecked(current.getIs_done() == 1 ? true : false);
            isDoneChBx.setTag(current);
            return convertView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}