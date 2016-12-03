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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    EditText emailField;
    EditText nameField;
    EditText passwordField;
    EditText UserPasswordConfirm;
    Button registerButton;
    TextView loginRedirection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        emailField = (EditText) findViewById(R.id.UserEmail);
        nameField = (EditText) findViewById(R.id.UserName);
        passwordField = (EditText) findViewById(R.id.UserPassword);
        registerButton = (Button) findViewById(R.id.RegisterButton);
        loginRedirection = (TextView) findViewById(R.id.LoginHere);
        UserPasswordConfirm = (EditText) findViewById(R.id.UserPasswordConfirm);

        registerButton.setOnClickListener(this);
        loginRedirection.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.RegisterButton:

                String name = nameField.getText().toString();
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                String passwordConfirm = UserPasswordConfirm.getText().toString();


                //CHECK THE CREDENTIALS

                //CHECK FOR EMPTY EMAIL
                Boolean emailCheckEmpty = true;
                boolean emailCheck = true;
                if(email.equals("")){
                    emailCheckEmpty = false;
                    Toast toast =  Toast.makeText(RegisterActivity.this, "Please Provide Your Email! ", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else{

                    //CHECK FOR VALID EMAIL FORMAT
                    emailCheck = isValidEmail(email);
                    if(!emailCheck ){
                        emailCheck = false;

                        Toast toast =  Toast.makeText(RegisterActivity.this, "This Is Not A Valid Email Format, Please Provide Valid Email! ", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                }

                //CHECK FOR EMPTY NAME
                boolean nameCheckEmpty = true;
                if(name.equals("") && emailCheckEmpty && emailCheck){
                    nameCheckEmpty = false;
                    Toast toast =  Toast.makeText(RegisterActivity.this, "Please Provide Your Name! ", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }


                //CHECK FOR EMPTY PASSWORD
                boolean passwordCheckEmpty= true;
                if(password.equals("") && nameCheckEmpty && emailCheck && emailCheckEmpty){
                    passwordCheckEmpty = false;
                    Toast toast = Toast.makeText(RegisterActivity.this, "Please Provide A Strong Password! ", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }

                //CHECK FOR EMPTY CONFIRM PASSWORD
                boolean confirmPasswordCheckEmpty= true;
                if(passwordConfirm.equals("") && passwordCheckEmpty && nameCheckEmpty && emailCheck && emailCheckEmpty){
                    confirmPasswordCheckEmpty = false;
                    Toast toast = Toast.makeText(RegisterActivity.this, "Please Type Your Password Again! ", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }

                //CHECK FOR PASSWORD AND CONFIRM PASSWORD MATCH
                boolean passwordCheck= true;
                if(!password.equals(passwordConfirm) && confirmPasswordCheckEmpty && passwordCheckEmpty && nameCheckEmpty && emailCheck && emailCheckEmpty) {
                    passwordCheck   = false;
                    Toast toast = Toast.makeText(RegisterActivity.this, "Password and Confirm Password Are Not Same, Please Check Again! ", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();

                }

                //CHECK FOR ACTIVE INTERNET CONNECTION
                boolean isInternetActive = true;
                if (!isNetworkAvailable() && passwordCheck && passwordCheckEmpty && emailCheck && emailCheckEmpty && nameCheckEmpty && confirmPasswordCheckEmpty) {
                    Toast toast = Toast.makeText(RegisterActivity.this, "You Don't Have An Active Internet Connection, Please Check Your Internet Connection And Then Try Again!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    isInternetActive = false;
                }

                if(nameCheckEmpty && emailCheckEmpty && isInternetActive && passwordCheckEmpty && passwordCheck && emailCheck && confirmPasswordCheckEmpty ) {
                    BackendlessUser backendlessUser = new BackendlessUser();

                    final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
                    progressDialog.setTitle("Please Wait!");
                    progressDialog.setMessage("Logging In...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    backendlessUser.setEmail(email);
                    backendlessUser.setProperty("name", name);
                    backendlessUser.setPassword(password);

                    final String nameOfUser = name;
                    Backendless.UserService.register(backendlessUser, new AsyncCallback<BackendlessUser>() {

                        @Override
                        public void handleResponse(BackendlessUser response) {
                            Toast.makeText(RegisterActivity.this,"Hey "+ nameOfUser + "You Have Been Successfully Registered", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            progressDialog.dismiss();
                            finish();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(RegisterActivity.this,"Hey Registration Failed, Better Luck Again", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }

                break;

            case R.id.LoginHere:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                this.finish();

                break;
        }

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
}
