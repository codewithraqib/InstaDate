package com.example.raqib.instadate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class LoginActivity extends AppCompatActivity implements  View.OnClickListener{
    static EditText emailField;
    static EditText passwordField;
    static Button LoginButton;
    static TextView RegisterHere, ForgetPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = (EditText) findViewById(R.id.UserEmailLogin);
        passwordField = (EditText) findViewById(R.id.UserLoginPassword);
        LoginButton = (Button) findViewById(R.id.LoginButton);
        RegisterHere = (TextView) findViewById(R.id.newUser);
        ForgetPassword = (TextView) findViewById(R.id.forgetPassword);

//        GoForLogin goForLogin = new GoForLogin();
//        Log.e("Background call", "Working...");
//        goForLogin.execute();
//        Log.e("Background call", "Done!");

        LoginButton.setOnClickListener(this);
        RegisterHere.setOnClickListener(this);
        ForgetPassword.setOnClickListener(this);

    }

    //HELPER METHOD TO DETERMINE WHETHER NETWORK IS AVAILABLE OR NOT
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isValidEmail(CharSequence  target ) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    //PRIVATE CLASS FOR LOGGING IN A NEW USER
//    private class GoForLogin extends AsyncTask<Void, Void, Void> implements View.OnClickListener {
//        ProgressDialog pdl = new ProgressDialog(getApplicationContext());
//
//        @Override
//        protected void onPreExecute() {
//
//            Log.e("onPreExecute", "Working...");
//
////            if(isNetworkAvailable()){
////
////                pdl.setTitle("Logging In...");
////                pdl.setMessage("Please Wait!");
////                pdl.show();
////            }
//
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
////            pdl.dismiss();
//            super.onPostExecute(aVoid);
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            Log.e("doInBackground", "Working...");
//
////            LoginButton.setOnClickListener(this);
////            Log.e("LoginButton", "Working...");
////            RegisterHere.setOnClickListener(this);
////            ForgetPassword.setOnClickListener(this);
//
//            return null;
//        }
//    }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.LoginButton:
                    String email = emailField.getText().toString();
                    String password = passwordField.getText().toString();

                    //CHECK FOR EMPTY EMAIL
                    boolean emailCheckEmpty = true;
                    boolean emailCheck = true;

                    if (email.equals("")) {
                        emailCheckEmpty = false;
                        Toast toast = Toast.makeText(LoginActivity.this, "Please Provide Your Email! ", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {

                        //CHECK FOR VALID EMAIL FORMAT
                        emailCheck = isValidEmail(email);
                        if (!emailCheck) {
                            emailCheck = false;

                            Toast toast = Toast.makeText(LoginActivity.this, "This Is Not A Valid Email Format, Please Provide Valid Email! ", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }

                    //CHECK FOR EMPTY PASSWORD
                    boolean passwordCheckEmpty = true;
                    if (password.equals("") && emailCheck && emailCheckEmpty) {
                        passwordCheckEmpty = false;
                        Toast toast = Toast.makeText(LoginActivity.this, "Please Provide The Particular Password!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }

                    //CHECK FOR ACTIVE INTERNET CONNECTION
                    boolean isInternetActive = true;
                    if (!isNetworkAvailable() && passwordCheckEmpty && emailCheck && emailCheckEmpty) {
                        Toast toast = Toast.makeText(LoginActivity.this, "You Don't Have An Active Internet Connection, Please Check Your Internet Connection And Then Try Again!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        isInternetActive = false;
                    }

                    if (emailCheckEmpty && passwordCheckEmpty && emailCheck && isInternetActive) {

                        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.setTitle("Please Wait!");
                        progressDialog.setMessage("Logging In...");
//                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {

                                    @Override
                                    public void handleResponse(BackendlessUser response) {
                                        Toast.makeText(LoginActivity.this, "Hey You Have Been Successfully Logged In", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Toast.makeText(LoginActivity.this, "Hey Log In Failed, Check Credentials And Try Again ", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                },
                                true); //TRUE IS HERE TO REMEMBER THE CURRENTLY LOGGED IN USER
                    }

                    break;

                case R.id.newUser:
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));


                    break;

                case R.id.forgetPassword:
//                AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
//                builder.setTitle("Reset Your Password");
//                final EditText enterMail = new EditText(getApplicationContext());
//                builder.setView(enterMail);
//                builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String email = enterMail.getText().toString();
//                        Backendless.UserService.restorePassword( email, new AsyncCallback<Void>() {
//                                    public void handleResponse( Void response )
//                                    {
//                                        Toast.makeText(getApplicationContext(), "Please Check Your Mail For The Reset Email!", Toast.LENGTH_LONG).show();
//                                    }
//                                    public void handleFault( BackendlessFault fault )
//                                    {
//                                        int errorCode = Integer.parseInt(fault.getCode());
//                                        if(errorCode == 3020){
//                                            Toast.makeText(getApplicationContext(), "This Email Doesn't Belong To Any User, Rather You can Register with This Email! ", Toast.LENGTH_LONG).show();
//                                        }else if(errorCode == 3025){
//                                            Toast.makeText(getApplicationContext(), "You Have Probably Misspelled The Email, Please Try Again!", Toast.LENGTH_LONG).show();
//                                        }else if(errorCode == 2002){
//                                            Toast.makeText(getApplicationContext(), "You Must Update Your App, You Are Using An Older Version! ", Toast.LENGTH_LONG).show();
//                                        }else{
//                                            Toast.makeText(getApplicationContext(), "An Unknown Error Has Occurred, Please Try Again! ", Toast.LENGTH_LONG).show();
//                                        }
//                                    }
//                                });
//                    }
//                });
//                builder.show();

                    String emailToReset = emailField.getText().toString();

                    boolean emailNotEmpty = true;
                    boolean checkEmailSyntax = true;
                    boolean internetActive = true;

                    //CHECK FOR EMPTY AND VALID EMAIL FORMAT
                    if (emailToReset.equals("")) {
                        emailNotEmpty = false;
                        Toast toast = Toast.makeText(LoginActivity.this, "Please Provide Your Email! ", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {

                        //CHECK FOR VALID EMAIL FORMAT
                        checkEmailSyntax = isValidEmail(emailToReset);
                        if (!checkEmailSyntax) {
                            checkEmailSyntax = false;

                            Toast toast = Toast.makeText(LoginActivity.this, "This Is Not A Valid Email Format, Please Provide Valid Email! ", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }

                    //CHECK FOR ACTIVE INTERNET CONNECTION
                    if (!isNetworkAvailable() && emailNotEmpty && checkEmailSyntax) {
                        Toast toast = Toast.makeText(LoginActivity.this, "You Don't Have An Active Internet Connection, Please Check Your Internet Connection And Then Try Again!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        internetActive = false;
                    }

                    if (emailNotEmpty && checkEmailSyntax && internetActive) {

                        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.setTitle("Please Wait!");
                        progressDialog.setMessage("Logging In...");
//                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        Backendless.UserService.restorePassword(emailToReset, new AsyncCallback<Void>() {

                            public void handleResponse(Void response) {
                                progressDialog.cancel();
                                Toast.makeText(getApplicationContext(), "Please Check Your Mail For The Reset Email!", Toast.LENGTH_LONG).show();
                            }

                            public void handleFault(BackendlessFault fault) {
                                if (fault.equals(3020)) {
                                    progressDialog.cancel();
                                    Toast.makeText(getApplicationContext(), "This Email Doesn't Belong To Any User, Rather You can Register with This Email! ", Toast.LENGTH_LONG).show();
                                } else if (fault.equals(3025)) {
                                    progressDialog.cancel();
                                    Toast.makeText(getApplicationContext(), "You Have Probably Misspelled The Email, Please Try Again!", Toast.LENGTH_LONG).show();
                                } else if (fault.equals(2002)) {
                                    progressDialog.cancel();
                                    Toast.makeText(getApplicationContext(), "You Must Update Your App, You Are Using An Older Version! ", Toast.LENGTH_LONG).show();
                                } else {
                                    progressDialog.cancel();
                                    Toast.makeText(getApplicationContext(), "An Unknown Error Has Occurred, Please Try Again! ", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }

            }
        }

}
