package com.programmingbear.mynote;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by satish on 23/10/2016.
 */
public class MyNotes extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,ClickListener{

    SharedPreferences viewTypeChoice;
    SharedPreferences.Editor mEditor;
    String userChoice="isList";
    int signInRequestCode=946;
    boolean isList,showETForHome,showETForStarred,showETForRemainders;
    static int home,starred,remainders;
    CoordinatorLayout coordinatorLayout;
    FloatingActionButton btnadd;
    String userEmail="";
    String username="";
    String loginType="";
    NDb mydb;
    Menu menu;
    Context context=this;
    RecyclerView recycler_view;
    DrawerLayout mDrawerLayout;
    Toolbar mToolbar;
    TextView mName,mEmailId,mEmptyNote;
    ImageView mHeaderImage,mProfileImage;
    Menu drawerMenu;
    MenuItem logoutItem,loginItem;
    static int NEW_NOTE_REQUEST=100;
    Recycler_View_Adapter adapter;
    List<note> noteList=new ArrayList<note>();
    CardView mCardView;
    Intent intent;
    Bundle extras;
    GoogleActivity mGoogleActivity;
    LinearLayoutManager mLinearLayoutManager;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);
        home=1;
        mLinearLayoutManager=new LinearLayoutManager(this);
        mStaggeredGridLayoutManager=new StaggeredGridLayoutManager(2,1);
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
        mEmptyNote=(TextView)findViewById(R.id.emptyNote);
        mHeaderImage=(ImageView)headerLayout.findViewById(R.id.img_header_bg);
        mProfileImage=(ImageView)headerLayout.findViewById(R.id.img_profile);
        drawerMenu=navigationView.getMenu();
        logoutItem=drawerMenu.findItem(R.id.google_plus_logout);
        loginItem=drawerMenu.findItem(R.id.google_plus_login);
        mCardView=(CardView)findViewById(R.id.cardview);
        intent =getIntent();
        extras=intent.getExtras();
        if(extras!=null&&extras.getString("loginType").equals("SignUp")){
            loginType=extras.getString("loginType");
            userEmail=extras.getString("email");
            username=extras.getString("name");
            mEmailId.setText(userEmail);
            mName.setText(username);
        }else if(extras!=null&&extras.getString("loginType").equals("Gplus")){
            loginType=extras.getString("loginType");
            userEmail=extras.getString("email");
            username=extras.getString("name");
            mEmailId.setText(userEmail);
            mName.setText(username);
        }else if(extras!=null&&extras.getString("loginType").equals("EmailAndPassword")){
            loginType=extras.getString("loginType");
            userEmail=extras.getString("email");
            Log.d("userEmail",username);
            username=extras.getString("name");
            mEmailId.setText(userEmail);
            mName.setText(username);
        }else if(extras!=null&&extras.getString("loginType").equals("None")){
            loginType=extras.getString("loginType");
            userEmail=extras.getString("email");
            username=extras.getString("name");
            mEmailId.setText(userEmail);
            mName.setText(username);
        }else if(extras!=null&&extras.getString("loginType").equals("closeWelcomeScreen")){
            loginType=extras.getString("loginType");
            userEmail=extras.getString("email");
            Log.d("userEmail",userEmail);
            username=extras.getString("name");
            mEmailId.setText(userEmail);
            mName.setText(username);
        }
        //Getting User choice of viewing(List or Grid) the notes from the shared preferences
        viewTypeChoice= PreferenceManager.getDefaultSharedPreferences(this);
        mEditor=viewTypeChoice.edit();
        isList=viewTypeChoice.getBoolean(userChoice,true);

        //Getting user signIn details if he already logged in.
        mydb=new NDb(this);
       if(username.equals("")&&userEmail.equals("")) {
            Cursor signInDetails = mydb.fetchSignInDetails();
            if ( signInDetails.moveToFirst() ) {
                logoutItem.setVisible(true);
                loginItem.setVisible(false);
                mName.setText(signInDetails.getString(signInDetails.getColumnIndex("name")));
                mEmailId.setText(signInDetails.getString(signInDetails.getColumnIndex("emailId")));
               /* if(signInDetails.getString(signInDetails.getColumnIndex("photoUrl"))==""){

                }else{
                    photoURL = signInDetails.getString(signInDetails.getColumnIndex("photoUrl"));
                }
                loginType = signInDetails.getString(signInDetails.getColumnIndex("loginType"));
                Glide.with(getApplicationContext()).load(photoURL)
                        .crossFade()
                        .thumbnail(0.5f)
                        .bitmapTransform(new CircleTransform(this))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mProfileImage);*/

            } else {
                logoutItem.setVisible(false);
                loginItem.setVisible(true);
            }
        }else if(username.equals("")&&userEmail.equals("Please SignIn to display your details")){
            logoutItem.setVisible(false);
            loginItem.setVisible(true);
        }else if(username!=""&&userEmail!=""){
            logoutItem.setVisible(true);
            loginItem.setVisible(false);
        }


        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,R.string.open_drawer, R.string.close_drawer);
        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
        // loading header background image***
        Glide.with(MyNotes.this).load(R.drawable.header)
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
        //start
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
        adapter=new Recycler_View_Adapter(noteList,getApplication());
        recycler_view.setAdapter(adapter);
        isList = viewTypeChoice.getBoolean(userChoice, true);
        if(isList){
            recycler_view.setLayoutManager(new LinearLayoutManager(this));
        }else{
        recycler_view.setLayoutManager(new StaggeredGridLayoutManager(2,1));
        }
        recycler_view.setHasFixedSize(true);
        adapter.setClickListener(this);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        isList=viewTypeChoice.getBoolean(userChoice,true);
        if(isList){
            menu.findItem(R.id.listView).setVisible(false);
            menu.findItem(R.id.gridView).setVisible(true);
        }else{
            menu.findItem(R.id.listView).setVisible(true);
            menu.findItem(R.id.gridView).setVisible(false);
        }

        return true;
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
                dataBundle.putString("Type", "new");
                Intent intent=new Intent(getApplicationContext(),DisplayNote.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
                return true;
            case R.id.deleteAll:
                deleteAllNotes();
                return true;
            case R.id.listView:
                mEditor.putBoolean(userChoice,true);
                mEditor.commit();
                //adapterCode();
                userChoiceNotes(home,starred,remainders);
                Log.d("homeValue",String.valueOf(home));
                Log.i("StarredValue",String.valueOf(starred));
                Log.i("remainderValue",String.valueOf(remainders));
                //Log.i("homeValue",String.valueOf(home));
                item.setVisible(false);
                menu.findItem(R.id.gridView).setVisible(true);
                return true;
            case R.id.gridView:
                mEditor.putBoolean(userChoice,false);
                mEditor.commit();
                //adapterCode();
                userChoiceNotes(home,starred,remainders);
                item.setVisible(false);
                menu.findItem(R.id.listView).setVisible(true);
                return true;
            /*case R.id.deleteSignInDetails:
                mydb.deleteSignInDetails();*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapterCode();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==signInRequestCode){
            if(data!=null){
                mName.setText(data.getExtras().getString("name"));
                mEmailId.setText(data.getExtras().getString("emailId"));
                //If the user pro pic needs to be displayed use the following line.
                //String photoUrl=data.getExtras().getString("photoUrl");
                loginType=data.getExtras().getString("loginType");
               /* Glide.with(getApplicationContext()).load(photoUrl)
                        .crossFade()
                        .thumbnail(0.5f)
                        .bitmapTransform(new CircleTransform(this))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mProfileImage);*/

            }

        }else if(requestCode==NEW_NOTE_REQUEST&&resultCode==RESULT_OK){


        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_home:
                home=1;
                starred=0;
                remainders=0;
                userChoiceNotes(home,starred,remainders);
                //showAllNotes();
                hideDrawer();
                break;
            case R.id.google_plus_login:
                /*startActivityForResult(new Intent(MyNotes.this,GoogleActivity.class),signInRequestCode);*/
                startActivity(new Intent(MyNotes.this,AllLogins.class));
                hideDrawer();
                loginItem.setVisible(false);
                logoutItem.setVisible(true);
                return true;
            case R.id.google_plus_logout:
                mName.setText("");
                mEmailId.setText("");
                mGoogleActivity=new GoogleActivity();
                mGoogleActivity.signOut();
                mydb.deleteSignInDetails();
                loginItem.setVisible(true);
                logoutItem.setVisible(false);
                hideDrawer();
                mEmailId.setText("Please SignIn to display your details");
                return true;
            case R.id.nav_starred:
                home=0;
                starred=1;
                remainders=0;
               //showStarredNotes();
                userChoiceNotes(home,starred,remainders);
                hideDrawer();
                return true;
            case R.id.nav_about_us:
                startActivity(new Intent(MyNotes.this,AboutUs.class));
                return true;
            case R.id.nav_remainders:
                home=0;
                starred=0;
                remainders=1;
                //showRemainders();
                userChoiceNotes(home,starred,remainders);
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
    public void itemClicked(String text,String remark) {

        Intent viewIntent=new Intent(this,DisplayNote.class);
        // viewIntent.putExtra("id", position+1);
        viewIntent.putExtra("text",text);
        // 10 is just a value given to identify this intent
        viewIntent.putExtra("id",10);
        viewIntent.putExtra("remark",remark);
        viewIntent.putExtra("Type", "old");
        startActivity(viewIntent);
    }

    @Override
    public void imageClicked(String text,String tag) {

        if(tag.equals("R.drawable.ic_filled_star")){
            mydb.setImpBool(text,0);
            adapter.notifyDataSetChanged();
            adapterCode();
        }else{
            mydb.setImpBool(text,1);
            adapter.notifyDataSetChanged();
            adapterCode();

        }}

    public void adapterCode(){

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
        }if(noteList.isEmpty()){
            mEmptyNote.setVisibility(View.VISIBLE);
            adapter=new Recycler_View_Adapter(noteList,getApplication());
            recycler_view.setAdapter(adapter);
        }else{
            mEmptyNote.setVisibility(View.GONE);
        adapter=new Recycler_View_Adapter(noteList,getApplication());
        recycler_view.setAdapter(adapter);
        isList=viewTypeChoice.getBoolean(userChoice,true);
        if(isList){
            recycler_view.setLayoutManager(new LinearLayoutManager(this));
        }else{
        recycler_view.setLayoutManager(new StaggeredGridLayoutManager(2,1));
        recycler_view.setHasFixedSize(true);
        }
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
    }

    public void showRemainders() {

        List<note> noteList = mydb.getRemainderNotes();
        // List<note> emptyList=new ArrayList<note>();
        if ( noteList.isEmpty() ) {
            mEmptyNote.setVisibility(View.VISIBLE);
            adapter = new Recycler_View_Adapter(noteList, getApplication());
            recycler_view.setAdapter(adapter);
        } else {
            mEmptyNote.setVisibility(View.GONE);
            adapter = new Recycler_View_Adapter(noteList, getApplication());
            recycler_view.setAdapter(adapter);
            isList = viewTypeChoice.getBoolean(userChoice, true);
            if ( isList ) {
                recycler_view.setLayoutManager(new LinearLayoutManager(this));
            } else {
                recycler_view.setLayoutManager(new StaggeredGridLayoutManager(2, 1));
                recycler_view.setHasFixedSize(true);
            }
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
    }

    public void showAllNotes(){
        adapterCode();
    }

    public void userChoiceNotes(int homeNotes,int starredNotes,int remainderNotes){

        if((homeNotes==1)&&(starredNotes==0)&&(remainderNotes==0)){
            adapterCode();
        }else if((homeNotes==0)&&(starredNotes==1)&&(remainderNotes==0)){
            showStarredNotes();
        }else if((homeNotes==0)&&(starredNotes==0)&&(remainderNotes==1)){
            showRemainders();
        }
    }

    public void showStarredNotes(){
        List<note> starredNotes=new ArrayList<note>();
        starredNotes=mydb.getStarredNotes();
        if(starredNotes.isEmpty()){
            mEmptyNote.setVisibility(View.VISIBLE);
            adapter = new Recycler_View_Adapter(starredNotes, getApplication());
            recycler_view.setAdapter(adapter);
        }else {
            mEmptyNote.setVisibility(View.GONE);
            adapter = new Recycler_View_Adapter(starredNotes, getApplication());
            recycler_view.setAdapter(adapter);
            isList = viewTypeChoice.getBoolean(userChoice, true);
            if ( isList ) {
                recycler_view.setLayoutManager(mLinearLayoutManager);
            } else {
                recycler_view.setLayoutManager(new StaggeredGridLayoutManager(2, 1));
            }
            recycler_view.setHasFixedSize(true);
            adapter.setClickListener(this);
        }
    }
    public void deleteAllNotes(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.DeleteNote).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                mydb.deleteAllNotes();
                adapterCode();
                Toast.makeText(MyNotes.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
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
        d.setTitle("Deleted Notes cannot be restored");
        d.show();
    }
}
