package com.example.raqib.instadate;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText emailField;
    EditText passwordField;
    TextView LoginButton;
    TextView RegisterHere;
    ActionBar actionBar;
    int uiOptions = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //TO HIDE STATUS BAR AND ACTION BAR
        View decorView = getWindow().getDecorView();
        actionBar = getSupportActionBar();
//        myRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        emailField = (EditText) findViewById(R.id.UserEmailLogin);
        passwordField = (EditText) findViewById(R.id.UserLoginPassword);
        LoginButton = (TextView) findViewById(R.id.LoginButton);
        RegisterHere = (TextView) findViewById(R.id.newUser);

        LoginButton.setOnClickListener(this);
        RegisterHere.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.LoginButton:
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();

                //CHECK FOR EMPTY EMAIL
                boolean emailCheckEmpty = true;
                boolean emailCheck = true;

                if(email.equals("")){
                    emailCheckEmpty = false;
                    Toast toast =  Toast.makeText(LoginActivity.this, "Please Provide Your Email! ", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else{

                    //CHECK FOR VALID EMAIL FORMAT
                    emailCheck = isValidEmail(email);
                    if(!emailCheck ){
                        emailCheck = false;

                        Toast toast =  Toast.makeText(LoginActivity.this, "This Is Not A Valid Email Format, Please Provide Valid Email! ", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                }

                //CHECK FOR EMPTY PASSWORD
                boolean passwordCheckEmpty= true;
                if(password.equals("")){
                    passwordCheckEmpty = false;
                    Toast toast = Toast.makeText(LoginActivity.this, "Please Provide The Particular Password!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }

                if(emailCheckEmpty && passwordCheckEmpty && emailCheck) {

                    Backendless.UserService.login(email,password, new AsyncCallback<BackendlessUser>() {

                                @Override
                                public void handleResponse(BackendlessUser response) {
                                    Toast.makeText(LoginActivity.this,"Hey You Have Been Successfully Logged In", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Toast.makeText(LoginActivity.this,"Hey Log In Failed, Check Credentials And Try Again ", Toast.LENGTH_LONG).show();
                                }
                            },
                            true);
                }

                break;

            case R.id.newUser:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                this.finish();

                break;
        }
    }
    public static boolean isValidEmail(CharSequence  target ) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
