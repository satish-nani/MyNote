package com.programmingbear.mynote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.common.SignInButton;

/**
 * Created by satish on 21/2/2017.
 */

public class AllLogins extends Activity implements View.OnClickListener{

    NDb mydb;
    Bundle extras;
    SharedPreferences mPrefs;
    final String welcomeScreenShownPref = "welcomeScreenShown";
    CoordinatorLayout coordinatorLayout;
    int signInRequestCode=946;
    Button signUpButton,loginButton;
    SignInButton signInButton;
    Intent intent;
    ImageView closeWelcomeScreen,logo;
    Cursor mCursor;
    boolean isConnected;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_logins);
        logo=(ImageView)findViewById(R.id.logo);
        mydb=new NDb(this);
        mCursor=mydb.fetchSignInDetails();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // second argument is the default to use if the preference can't be found
        Boolean welcomeScreenShown = mPrefs.getBoolean(welcomeScreenShownPref, false);
      /*  if (!welcomeScreenShown) {
            // here you can launch another activity if it user is not entering the first time
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putBoolean(welcomeScreenShownPref, true);
            editor.commit(); // Very important to save the preference
        }else{
            intent=new Intent(WelcomeScreen.this,MyNotes.class);
            if ( mCursor.moveToFirst() ) {
                intent.putExtra("name",mCursor.getString(mCursor.getColumnIndex("name")));
                intent.putExtra("email",mCursor.getString(mCursor.getColumnIndex("emailId")));
                intent.putExtra("loginType",mCursor.getString(mCursor.getColumnIndex("loginType")));
            }else{
            intent.putExtra("name","");
            intent.putExtra("loginType","None");
            intent.putExtra("email","Please SignIn to display your details");}
            startActivity(intent);
        }*/

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        signUpButton=(Button)findViewById(R.id.signUp);
        loginButton=(Button)findViewById(R.id.login);
        closeWelcomeScreen=(ImageView)findViewById(R.id.close_screen);
        closeWelcomeScreen.setOnClickListener(this);
        signInButton=(SignInButton)findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==signInRequestCode){
            if(data!=null){
                Toast.makeText(this,getResources().getString(R.string.sign_in_success),Toast.LENGTH_SHORT).show();
                intent=new Intent(AllLogins.this,MyNotes.class);
                extras=data.getExtras();
                intent.putExtra("name",extras.getString("name"));
                intent.putExtra("email",extras.getString("emailId"));
                intent.putExtra("loginType",extras.getString("loginType"));
                startActivity(intent);
            }}else{
            Toast.makeText(this,getResources().getString(R.string.sign_in_fail),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()){

            case R.id.sign_in_button:
                checkInternetConnection();
                if(isConnected) {
                    startActivityForResult(new Intent(AllLogins.this, GoogleActivity.class), signInRequestCode);
                }
                else{
                    Toast.makeText(AllLogins.this,getResources().getString(R.string.no_internet_connection),Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.signUp:
                checkInternetConnection();
                if(isConnected) {
                    intent=new Intent(AllLogins.this,SignUpActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(AllLogins.this,getResources().getString(R.string.no_internet_connection),Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.login:
                checkInternetConnection();
                if(isConnected) {
                    intent= new Intent(AllLogins.this, LoginActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(AllLogins.this,getResources().getString(R.string.no_internet_connection),Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.close_screen:
                intent=new Intent(AllLogins.this,MyNotes.class);
                intent.putExtra("loginType","closeWelcomeScreen");
                intent.putExtra("email","Please SignIn to display your details");
                intent.putExtra("name","");
                startActivity(intent);
        }
    }
    public void checkInternetConnection(){
        //Used to determine whether the device has internet connection or not
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
