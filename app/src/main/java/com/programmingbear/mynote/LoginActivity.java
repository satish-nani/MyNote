package com.programmingbear.mynote;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by satish on 27/2/2017.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    NDb database=new NDb(this);
    FirebaseUser firebaseUser;
    String email,password,name;
    String TAG="AuthState";
    FirebaseAuth mAuth;
    AppCompatTextView mTextView;
    Intent intent;
    int forgotPasswordRequest=108;
    FirebaseAuth.AuthStateListener mAuthListener;
    private EditText inputEmail, inputPassword;
    private TextInputLayout inputLayoutEmail, inputLayoutPassword;
    Button btnLogin,sendVerification;
    ProgressDialog mLoginProgressDialog;
    Intent enterApp;
    boolean isConnected,isVerificationEmailSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        mLoginProgressDialog = new ProgressDialog(this);
        mLoginProgressDialog.setMessage("Signing in...");
        mAuth = FirebaseAuth.getInstance();
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        sendVerification=(Button)findViewById(R.id.send_verification);
        sendVerification.setOnClickListener(this);
        mTextView=(AppCompatTextView)findViewById(R.id.fp_textview);
        mTextView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                intent =new Intent(LoginActivity.this,ForgotPassword.class);
                startActivityForResult(intent,forgotPasswordRequest);
            }

        });
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                //Used to determine whether the device has internet connection or not
                ConnectivityManager cm =
                        (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if(isConnected) {
                    submitForm();
                }else{
                    Toast.makeText(LoginActivity.this,R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
                }
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    name=user.getDisplayName();
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
       inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
       // inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Validating form
     */
    private void submitForm() {

        if (validateEmail()&&validatePassword()) {
            email=inputEmail.getText().toString();
            password=inputPassword.getText().toString();
            showProgressDialog();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                //To know the type of exception occured use the following the line.
                                FirebaseAuthException e = (FirebaseAuthException)task.getException();
                                hideProgressDialog();
                                showError(e);
                                Toast.makeText(LoginActivity.this,e.getErrorCode(),
                                        Toast.LENGTH_SHORT).show();
                                /*Toast.makeText(LoginActivity.this, getResources().getString(R.string.username_or_password_is_wrong),
                                        Toast.LENGTH_SHORT).show();*/
                            }else{
                               hideProgressDialog();
                                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                enterApp=new Intent(LoginActivity.this,MyNotes.class);
                                enterApp.putExtra("loginType","EmailAndPassword");
                                database.insertSignInDetails(name,email, "","EmailAndPassword");
                                enterApp.putExtra("name",name);
                                enterApp.putExtra("email",email);
                                enterApp.putExtra("password",password);
                                if(firebaseUser.isEmailVerified()){
                                    Toast.makeText(LoginActivity.this,getResources().getString(R.string.sign_in_success),Toast.LENGTH_SHORT).show();
                                    startActivity(enterApp);
                                }
                                else{
                                    sendVerification.setVisibility(View.VISIBLE);
                                    inputLayoutPassword.setVisibility(View.GONE);
                                    mTextView.setVisibility(View.GONE);
                                    btnLogin.setVisibility(View.GONE);
                                    Toast.makeText(LoginActivity.this,getResources().getString(R.string.verify_email),Toast.LENGTH_SHORT).show();
                                }
                            }

                            // ...
                        }
                    });
        }

    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password_login));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==forgotPasswordRequest){
            if(data!=null){
            inputEmail.setText(data.getExtras().getString("email"));
                Toast.makeText(LoginActivity.this, "Password reset email is sent", Toast.LENGTH_LONG).show();
        }}
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void showProgressDialog(){
        mLoginProgressDialog.show();
    }
    public void hideProgressDialog(){
        mLoginProgressDialog.dismiss();
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.send_verification:
                hideKeyboard();
                if(validateEmail()){
                    String verificationEmail=inputEmail.getText().toString().trim();
                    //Used to determine whether the device has internet connection or not
                    ConnectivityManager cm =
                            (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    isConnected = activeNetwork != null &&
                            activeNetwork.isConnectedOrConnecting();
                    if(isConnected) {
                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if(verificationEmail.equals(firebaseUser.getEmail())) {
                            sendEmailVerification(firebaseUser);
                        }else{
                            Toast.makeText(LoginActivity.this,getResources().getString(R.string.email_mismatch),Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Toast.makeText(LoginActivity.this,R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
                    }

                }
                break;
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }

    public void showError(FirebaseAuthException e){

        switch ( e.getErrorCode() ){

        }
    }

    public void sendEmailVerification(final FirebaseUser user){

        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if ( task.isSuccessful() ) {
                    Toast.makeText(LoginActivity.this,
                            "Verification email sent to "+user.getEmail()+". Please verify email before you login.",
                            Toast.LENGTH_LONG
                    ).show();
                    isVerificationEmailSent = true;
                } else {
                    Log.e(TAG, "sendEmailVerification", task.getException());
                    Toast.makeText(LoginActivity.this,
                            "Failed to send verification email.",
                            Toast.LENGTH_LONG).show();
                    isVerificationEmailSent = false;
                }
            }
        });
    }
    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}
