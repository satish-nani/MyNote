package com.programmingbear.mynote;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
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
    int isStarred=0;
    Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewnotepad);
        name=(EditText)findViewById(R.id.txtname);
        content=(EditText)findViewById(R.id.txtcontent);
        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mydb=new NDb(this);
        Bundle extras;
        extras=getIntent().getExtras();
        int Value=extras.getInt("id");
        if(Value>0){
            snackbar=Snackbar.make(coordinatorLayout, "Note Id : "+String.valueOf(Value), Snackbar.LENGTH_SHORT);
            snackbar.show();
            Cursor rs=mydb.getData(Value);
            id_To_Update=Value;
            rs.moveToFirst();
            String nam=rs.getString(rs.getColumnIndex(NDb.name));
            String contents=rs.getString(rs.getColumnIndex(NDb.remark));
            if(!rs.isClosed()){
                rs.close();
            }
            name.setText(nam);
            content.setText(contents);
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
                d.setTitle("Are you sure ");
                d.show();
                return true;
            case R.id.Save:
                Bundle extras = getIntent().getExtras();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = df.format(c.getTime());
                dateString = formattedDate;
                if (extras != null) {
                    int Value = extras.getInt("id");
                    if (Value > 0) {
                        if (content.getText().toString().trim().equals("") || name.getText().toString().trim().equals("")) {
                            snackbar = Snackbar.make(coordinatorLayout, "Please enter the note details", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            if (mydb.updateNotes(id_To_Update, name.getText().toString(), dateString, content.getText().toString())) {
                                snackbar = Snackbar.make(coordinatorLayout, "We are upto date with you :)", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            } else {
                                snackbar = Snackbar.make(coordinatorLayout, "Something happened, sorry :(", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                        }
                    } else {
                        if (content.getText().toString().trim().equals("") || name.getText().toString().trim().equals("")) {
                            snackbar = Snackbar.make(coordinatorLayout, "Please enter the note details", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            if (mydb.insertNotes(name.getText().toString(), dateString, content.getText().toString(),isStarred)) {
                                snackbar = Snackbar.make(coordinatorLayout, "Your note is with us. Don't worry!", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            } else {
                                snackbar = Snackbar.make(coordinatorLayout, "Something happened, sorry :(", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                        }

                    }
                }
                return true;
            case R.id.Starred:

                item.setVisible(false);
                isStarred=1;
                return true;
            case R.id.Unstarred:
                isStarred=1;
                item.setVisible(false);
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
}