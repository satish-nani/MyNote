package com.programmingbear.mynote;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by satish on 22/2/2017.
 */

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    NDb database=new NDb(this);
    FirebaseUser firebaseUser;
    String name,email,password;
    String TAG="AuthState";
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    EditText inputName, inputEmail, inputPassword;
    TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword;
    Button btnSignUp,resendVerification;
    ProgressDialog mSignUpProgressDialog;
    Intent enterApp;
    boolean isVerificationEmailSent,isConnected;
    CoordinatorLayout mCoordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_screen);
        mSignUpProgressDialog = new ProgressDialog(this);
        mCoordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        mSignUpProgressDialog.setMessage("Signing in...");
        mAuth = FirebaseAuth.getInstance();
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputName = (EditText) findViewById(R.id.input_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        resendVerification=(Button)findViewById(R.id.resend_verification);
        resendVerification.setOnClickListener(this);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name).build();
                    user.updateProfile(profileUpdates);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        btnSignUp.setOnClickListener(this);
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

        if (validateName()&&validateEmail()&&validatePassword()) {
            email=inputEmail.getText().toString();
            password=inputPassword.getText().toString();
            name=inputName.getText().toString();
            showProgressDialog();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.

                            if (!task.isSuccessful()) {
                                //To know the type of exception use the following line.
                                //FirebaseAuthException e = (FirebaseAuthException)task.getException();

                                FirebaseAuthException e = (FirebaseAuthException)task.getException();
                                hideProgressDialog();
                                if(e.getErrorCode()!=null) {
                                    Toast.makeText(SignUpActivity.this, e.getErrorCode(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                sendEmailVerification(firebaseUser);
                                if(isVerificationEmailSent){
                                    hideProgressDialog();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                        }
                                    }, 1000);
                                    startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                                }else{
                                    hideProgressDialog();
                                }

                                //Used to signIn without verification
                               /*
                                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                enterApp=new Intent(SignUpActivity.this,MyNotes.class);
                                database.insertSignInDetails(name,email, "","SignUp");
                                enterApp.putExtra("loginType","SignUp");
                                enterApp.putExtra("email",email);
                                enterApp.putExtra("name",name);
                                enterApp.putExtra("password",password);
                                startActivity(enterApp);*/
                            }

                            // ...
                        }
                    });
        }
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
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
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
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

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()){

            case R.id.resend_verification:
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
                            Toast.makeText(SignUpActivity.this,getResources().getString(R.string.email_mismatch),Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Toast.makeText(SignUpActivity.this,R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case R.id.btn_signup:
               hideKeyboard();
                //Used to determine whether the device has internet connection or not
                ConnectivityManager cm =
                        (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if(isConnected) {
                    submitForm();
                }else{
                    Toast.makeText(SignUpActivity.this,R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
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
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }


    public void showProgressDialog(){
        mSignUpProgressDialog.show();
    }
    public void hideProgressDialog(){
        mSignUpProgressDialog.dismiss();
    }

    public void sendEmailVerification(final FirebaseUser user){

        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if ( task.isSuccessful() ) {
                   Toast.makeText(SignUpActivity.this,
                            "Verification email sent to "+user.getEmail()+". Please verify email before you login.",
                            Toast.LENGTH_LONG
                           ).show();
                    isVerificationEmailSent = true;
                    resendVerification.setVisibility(View.VISIBLE);
                    btnSignUp.setVisibility(View.GONE);
                    inputLayoutPassword.setVisibility(View.GONE);
                    inputLayoutName.setVisibility(View.INVISIBLE);
                } else {
                    Log.e(TAG, "sendEmailVerification", task.getException());
                    Toast.makeText(SignUpActivity.this,
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

