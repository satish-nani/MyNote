package com.programmingbear.mynote;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by satish on 23/10/2016.
 */
public class DisplayNote extends AppCompatActivity{

    EditText name;
    EditText content;
    private CoordinatorLayout coordinatorLayout;
    private NDb mydb;
    Snackbar snackbar;
    int id_To_Update=0;
    String dateString;
    ActionBar actionBar;
    int isStarred;
    Bundle extras;
    ScheduleClient scheduleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewnotepad);
        scheduleClient = new ScheduleClient(this);
        scheduleClient.doBindService();
        name=(EditText)findViewById(R.id.txtname);
        content=(EditText)findViewById(R.id.txtcontent);
        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        actionBar=getSupportActionBar();
        try {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }catch(Exception e){

        }
        mydb=new NDb(this);
        extras=getIntent().getExtras();
        int Value=extras.getInt("id");
           /* if(Value>0){
            snackbar=Snackbar.make(coordinatorLayout,"Note priority things first :P", Snackbar.LENGTH_SHORT);
            snackbar.show();
            Cursor rs=mydb.getData(Value);
            id_To_Update=Value;
            rs.moveToFirst();
            String nam=rs.getString(rs.getColumnIndex(NDb.name));
            String contents=rs.getString(rs.getColumnIndex(NDb.remark));
            isStarred=rs.getInt(rs.getColumnIndex(NDb.isStarred));
            if(!rs.isClosed()){
                rs.close();
            }
            name.setText(nam);
            content.setText(contents);
        }else */if(Value==10){
            Cursor rs=mydb.getId(extras.getString("text"));
            rs.moveToFirst();
            id_To_Update=rs.getInt(rs.getColumnIndex(NDb._id));
            isStarred=rs.getInt(rs.getColumnIndex(NDb.isStarred));
                name.setText(extras.getString("text"));
                content.setText(extras.getString("remark"));
            }else if(Value==0){
            name.setText(extras.getString(""));
            content.setText(extras.getString(""));
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(isStarred==1){
            menu.findItem(R.id.Starred).setVisible(false);
            menu.findItem(R.id.Unstarred).setVisible(true);
        }else{
            menu.findItem(R.id.Starred).setVisible(true);
            menu.findItem(R.id.Unstarred).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

       /* Bundle extras= getIntent().getExtras();
        if(extras!=null){*/
            //int Value=extras.getInt("id");

        getMenuInflater().inflate(R.menu.display_menu, menu);

        //}
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, MyNotes.class);
                startActivity(homeIntent);
                return true;
            case R.id.Delete:
                deleteNote();
                return true;
            case R.id.Save:
                saveNote();
                return true;
            case R.id.Starred:
                item.setVisible(false);
                isStarred=1;
                snackbar=Snackbar.make(coordinatorLayout,"Save the note to reflect the changes",Snackbar.LENGTH_SHORT);
                snackbar.show();
                return true;
            case R.id.Unstarred:
                isStarred=0;
                item.setVisible(false);
                return true;
            case R.id.PickTime:
                int currentHour,currentMinute;
                Calendar ca = Calendar.getInstance();
                currentHour = ca.get(Calendar.HOUR_OF_DAY);
                currentMinute = ca.get(Calendar.MINUTE);
                // Launch Time Picker Dialog
                TimePickerDialog tpd = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                sendPickedTime(hourOfDay,minute);
                                Toast.makeText(getApplicationContext(),hourOfDay + " : " + minute,Toast.LENGTH_SHORT).show();
                            }
                        }, currentHour, currentMinute, true);
                tpd.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent=new Intent(getApplicationContext(),MyNotes.class);
        startActivity(intent);
        finish();
    }

    public void sendPickedTime(final int mHour, final int mMinute){

        int mYear,mMonth,mDay;
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        Toast.makeText(getApplicationContext(),dayOfMonth + "/" + (monthOfYear+1) + "/" + year+"/n"+mHour+":"+mMinute,Toast.LENGTH_SHORT).show();
                        Calendar notificationDT=Calendar.getInstance();
                       notificationDT.set(year, monthOfYear , dayOfMonth);
                        notificationDT.set(Calendar.HOUR_OF_DAY, mHour);
                        notificationDT.set(Calendar.MINUTE, mMinute);
                        notificationDT.set(Calendar.SECOND, 0);
                        createNotification(notificationDT,name.getText().toString(),content.getText().toString());
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void createNotification(Calendar notification,String name,String content){

        scheduleClient.setAlarmForNotification(notification,name,content);
        mydb.createAlarm(name,content);

    }

    @Override
    protected void onStop() {
        // When our activity is stopped ensure we also stop the connection to the service
        // this stops us leaking our activity into the system *bad*
        if(scheduleClient != null)
            scheduleClient.doUnbindService();
        super.onStop();
    }

    public void saveNote(){

        Bundle extras = getIntent().getExtras();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = df.format(c.getTime());
        if (extras != null) {
            String type = extras.getString("Type");
            if (!(type.equals("new"))) {
                if (content.getText().toString().trim().equals("") || name.getText().toString().trim().equals("")) {
                    snackbar = Snackbar.make(coordinatorLayout, "Please enter the note details", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    if (mydb.updateNotes(id_To_Update, name.getText().toString(), dateString, content.getText().toString(),isStarred)) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        snackbar = Snackbar.make(coordinatorLayout, "We are upto date with you :)", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000);

                    } else {
                        snackbar = Snackbar.make(coordinatorLayout, "Something happened, sorry :(", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }
            } else if(type.equals("new")){
                if (content.getText().toString().trim().equals("") || name.getText().toString().trim().equals("")) {
                    snackbar = Snackbar.make(coordinatorLayout, "Please enter the note details", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    if (mydb.insertNotes(name.getText().toString(), dateString, content.getText().toString(),isStarred)) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        snackbar = Snackbar.make(coordinatorLayout, "Your note is with us. Don't worry!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent=new Intent();
                                setResult(RESULT_OK,intent);
                                finish();
                            }
                        }, 1000);
                    } else {
                        snackbar = Snackbar.make(coordinatorLayout, "Something happened, sorry :(", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }

            }
        }
    }

    public void deleteNote(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.DeleteNote).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                mydb.deleteNotes(id_To_Update);
                Toast.makeText(DisplayNote.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MyNotes.class);
                startActivity(intent);
                finish();
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog d = builder.create();
        d.setTitle("Deleted Note cannot be restored");
        d.show();
    }
}
