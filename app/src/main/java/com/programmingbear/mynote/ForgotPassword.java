package com.programmingbear.mynote;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by satish on 1/3/2017.
 */

public class ForgotPassword extends AppCompatActivity {

    FirebaseUser firebaseUser;
    String email;
    int forgotPasswordRequest = 108;
    String TAG = "AuthState";
    FirebaseAuth mAuth;
    Intent intent;
    FirebaseAuth.AuthStateListener mAuthListener;
    EditText inputEmail;
    TextInputLayout inputLayoutEmail;
    Button btnResetPassword;
    ProgressDialog mLoginProgressDialog;
    boolean isConnected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        mLoginProgressDialog = new ProgressDialog(this);
        mLoginProgressDialog.setMessage("Sending password reset mail...");
        mAuth = FirebaseAuth.getInstance();
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputEmail = (EditText) findViewById(R.id.input_email);
        btnResetPassword = (Button) findViewById(R.id.reset_password);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if ( user != null ) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                checkInternetConnection();
                if(isConnected) {
                    resetPassword();
                }else{
                    Toast.makeText(ForgotPassword.this,getResources().getString(R.string.no_internet_connection),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if ( mAuthListener != null ) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void resetPassword() {

        //  if (validateEmail()&&validatePassword()) {
        email = inputEmail.getText().toString();
        showProgressDialog();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    //  To know the type of exception occured use the following line
                    FirebaseAuthException e = (FirebaseAuthException)task.getException();
                    hideProgressDialog();
                    Toast.makeText(ForgotPassword.this, getResources().getString(R.string.reset_password_fail),
                            Toast.LENGTH_SHORT).show();
                }else{
                    hideProgressDialog();
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    intent=new Intent();
                    intent.putExtra("email",email);
                    setResult(forgotPasswordRequest,intent);
                    finish();
                }

                // ...
            }
        });


    }

    public void showProgressDialog(){
        mLoginProgressDialog.show();
    }
    public void hideProgressDialog(){
        mLoginProgressDialog.dismiss();
    }

    public void checkInternetConnection(){
        //Used to determine whether the device has internet connection or not
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}
