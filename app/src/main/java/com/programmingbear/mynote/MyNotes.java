package com.programmingbear.mynote;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by satish on 23/10/2016.
 */
public class MyNotes extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,ClickListener{

    int signInRequestCode=946;
    CoordinatorLayout coordinatorLayout;
    FloatingActionButton btnadd;
    NDb mydb;
    Menu menu;
    Context context=this;
   // SimpleCursorAdapter adapter;
    Snackbar snackbar;
    RecyclerView recycler_view;
    DrawerLayout mDrawerLayout;
    Toolbar mToolbar;
    TextView mName,mEmailId;
    ImageView mHeaderImage,mProfileImage;
    Menu drawerMenu;
    MenuItem logoutItem,loginItem;
    static int NEW_NOTE_REQUEST=100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);

        recycler_view=(RecyclerView)findViewById(R.id.recyclerview);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        context = this;
        mToolbar=(Toolbar)findViewById(R.id.toolbar);
              setSupportActionBar(mToolbar);
              mToolbar.setTitle(R.string.app_name);
        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        btnadd = (FloatingActionButton) findViewById(R.id.btnadd);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout=navigationView.getHeaderView(0);
        mName=(TextView)headerLayout.findViewById(R.id.name);
        mEmailId=(TextView)headerLayout.findViewById(R.id.email_id);
        mHeaderImage=(ImageView)headerLayout.findViewById(R.id.img_header_bg);
        mProfileImage=(ImageView)headerLayout.findViewById(R.id.img_profile);
        drawerMenu=navigationView.getMenu();
        logoutItem=drawerMenu.findItem(R.id.google_plus_logout);
        loginItem=drawerMenu.findItem(R.id.google_plus_login);

        mydb=new NDb(this);
        Cursor signInDetails=mydb.fetchSignInDetails();
        if(signInDetails.moveToFirst()) {
            logoutItem.setVisible(true);
            loginItem.setVisible(false);

                mName.setText(signInDetails.getString(signInDetails.getColumnIndex("name")));
                System.out.println(signInDetails.getString(signInDetails.getColumnIndex("name")));
                mEmailId.setText(signInDetails.getString(signInDetails.getColumnIndex("emailId")));
                String photoURL=signInDetails.getString(signInDetails.getColumnIndex("photoUrl"));
                Glide.with(getApplicationContext()).load(photoURL)
                        .crossFade()
                        .thumbnail(0.5f)
                        .bitmapTransform(new CircleTransform(this))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mProfileImage);

        }else{
            logoutItem.setVisible(false);
            loginItem.setVisible(true);
        }

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,R.string.open_drawer, R.string.close_drawer);
        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
        // loading header background image
       Glide.with(MyNotes.this).load(R.drawable.nav_header_bg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mHeaderImage);
        // Loading profile image
       /*Glide.with(this).load(R.drawable.my_pro_pic)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mProfileImage);
        */

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle dataBundle = new Bundle();
                dataBundle.putString("Type", "new");
                Intent intent = new Intent(getApplicationContext(), DisplayNote.class);
                intent.putExtras(dataBundle);
                startActivityForResult(intent,NEW_NOTE_REQUEST);
            }
        });
        Cursor c = mydb.fetchAll();


       // String[] fieldNames = new String[]{NDb.name, NDb._id, NDb.dates, NDb.remark};
        //int[] display = new int[]{R.id.txtnamerow, R.id.txtidrow, R.id.txtdate, R.id.txtremark};

        //start
        List<note> noteList=new ArrayList<note>();
        if(c.moveToFirst()) {
            while(!c.isAfterLast()) {
                note noteObj = new note();
                noteObj.setName(c.getString(c.getColumnIndex("name")));
                noteObj.setRemark(c.getString(c.getColumnIndex("remark")));
                noteObj.setDates(c.getString(c.getColumnIndex("dates")));
                noteObj.setIsStarred(c.getInt(c.getColumnIndex("isStarred")));
                noteList.add(noteObj);
                c.moveToNext();
            }
        }
        Recycler_View_Adapter adapter=new Recycler_View_Adapter(noteList,getApplication());
        recycler_view.setAdapter(adapter);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setHasFixedSize(true);
        adapter.setClickListener(this);
        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    btnadd.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0 || dy < 0 && btnadd.isShown())
                    btnadd.hide();
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        //end

      /*  if(c==null){
            adapter = new SimpleCursorAdapter(this, R.layout.listtemplate, null, fieldNames, display, 0);
        }else{
            adapter = new SimpleCursorAdapter(this, R.layout.listtemplate, c, fieldNames, display, 0);
        }*/


      /*  mylist = (ListView) findViewById(R.id.listView1);
        mylist.setAdapter(adapter);
        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                LinearLayout linearLayoutParent = (LinearLayout) arg1;
                LinearLayout linearLayoutChild = (LinearLayout) linearLayoutParent.getChildAt(0);
                TextView m = (TextView) linearLayoutChild.getChildAt(1);
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", Integer.parseInt(m.getText().toString()));
                Intent intent = new Intent(getApplicationContext(), DisplayNote.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
                finish();
            }
        });*/
    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case R.id.add:
                Bundle dataBundle=new Bundle();
                dataBundle.putInt("id", 0);
                Intent intent=new Intent(getApplicationContext(),DisplayNote.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
                return true;
            case R.id.deleteAll:
                mydb.deleteAllNotes();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Cursor c = mydb.fetchAll();
        List<note> noteList=new ArrayList<note>();
        if(c.moveToFirst()) {
            while(!c.isAfterLast()) {
                note noteObj = new note();
                noteObj.setName(c.getString(c.getColumnIndex("name")));
                noteObj.setRemark(c.getString(c.getColumnIndex("remark")));
                noteObj.setDates(c.getString(c.getColumnIndex("dates")));
                noteObj.setIsStarred(c.getInt(c.getColumnIndex("isStarred")));
                noteList.add(noteObj);
                c.moveToNext();
            }
        }
        Recycler_View_Adapter adapter=new Recycler_View_Adapter(noteList,getApplication());
        recycler_view.setAdapter(adapter);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setHasFixedSize(true);
        adapter.setClickListener(this);
        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if ( newState == RecyclerView.SCROLL_STATE_IDLE ) {
                    btnadd.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if ( dy > 0 || dy < 0 && btnadd.isShown() )
                    btnadd.hide();
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==signInRequestCode){
           if(data!=null){
               mName.setText(data.getExtras().getString("name"));
               mEmailId.setText(data.getExtras().getString("emailId"));
               String photoUrl=data.getExtras().getString("photoUrl");
               Glide.with(getApplicationContext()).load(photoUrl)
                       .crossFade()
                       .thumbnail(0.5f)
                       .bitmapTransform(new CircleTransform(this))
                       .diskCacheStrategy(DiskCacheStrategy.ALL)
                       .into(mProfileImage);

           }

        }else if(requestCode==NEW_NOTE_REQUEST&&resultCode==RESULT_OK){


        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

      switch (item.getItemId()){
          case R.id.google_plus_login:
              startActivityForResult(new Intent(MyNotes.this,GoogleActivity.class),signInRequestCode);
              hideDrawer();
              loginItem.setVisible(false);
              logoutItem.setVisible(true);
              return true;
          case R.id.google_plus_logout:
              mName.setText("");
              mEmailId.setText("");

              mydb.deleteSignInDetails();
              loginItem.setVisible(true);
              logoutItem.setVisible(false);
              hideDrawer();
              return true;
      }

        hideDrawer();

        return true;
    }

    //close the drawer
    private void hideDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    //open the drawer
    private void showDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    //when the user presses backbutton the following method gets executed
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            hideDrawer();
        else
            super.onBackPressed();
    }

    @Override
    public void itemClicked(View v, int position) {

        Intent viewIntent=new Intent(this,DisplayNote.class);
        viewIntent.putExtra("id", position+1);
        viewIntent.putExtra("Type", "old");
        startActivity(viewIntent);
    }

   /* @Override
    public void imageClicked(View v, int position) {
        int isImpOrNot=0;
        ImageView img=  (ImageView)v.findViewById(R.id.is_starred);

        Cursor imp = mydb.getImpBool(position+1);
        imp.moveToFirst();
        while ( !imp.isAfterLast() ) {
            isImpOrNot = imp.getInt(imp.getColumnIndex(NDb.isStarred));
        }
        if ( isImpOrNot == 1 ) {
            img.setImageResource(R.drawable.ic_empty_star);
            mydb.updateStar(position + 1, 0);
        } else {
            img.setImageResource(R.drawable.ic_filled_star);
            mydb.updateStar(position + 1, 1);
        }}*/
        /*public int sendImpBool(int position){
            int result=0;
            mydb.getData(1);
            if(imp.moveToFirst()){
            while ( !imp.isAfterLast() ) {
                result = imp.getInt(imp.getColumnIndex(NDb.isStarred));
            }}
            return result;
        }*/
}
