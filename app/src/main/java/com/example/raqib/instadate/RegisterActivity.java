package com.example.raqib.instadate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.BackendlessUser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    EditText emailField;
    EditText nameField;
    EditText passwordField;
    EditText UserPasswordConfirm;
    Button registerButton;
    TextView loginRedirection;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "Register Activity";
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private static boolean signedInSuccessfully = false;
//    final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth = FirebaseAuth.getInstance();
        SignInButton googleSignInButton = (SignInButton) findViewById(R.id.googleSignInButton);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast toast =  Toast.makeText(RegisterActivity.this, "There is an error Signing In, Try again!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        assert googleSignInButton != null;
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null && signedInSuccessfully) {
                    // User is signed in
                    Toast.makeText(RegisterActivity.this,"Hey You Have Been Successfully Registered", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };


        emailField = (EditText) findViewById(R.id.UserEmail);
        nameField = (EditText) findViewById(R.id.UserName);
        passwordField = (EditText) findViewById(R.id.UserPassword);
        registerButton = (Button) findViewById(R.id.RegisterButton);
        loginRedirection = (TextView) findViewById(R.id.LoginHere);
        UserPasswordConfirm = (EditText) findViewById(R.id.UserPasswordConfirm);

        registerButton.setOnClickListener(this);
        loginRedirection.setOnClickListener(this);
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
//        progressDialog.setTitle("Please Wait!");
//        progressDialog.setMessage("Signing up with Google...");
//        progressDialog.setCancelable(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                signedInSuccessfully= true;
//                progressDialog.dismiss();

            } else {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(RegisterActivity.this,"Signing up with Google failed, Try again!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //ATTACH THE LISTENER TO YOUR FIREBASE AUTH INSTANCE IN THE ONSTART() METHOD AND REMOVE IT ON ONSTOP()
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
                    progressDialog.setMessage("Signing Up...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

//                    backendlessUser.setEmail(email);
//                    backendlessUser.setProperty("name", name);
//                    backendlessUser.setPassword(password);

                    final String nameOfUser = name;



                    //BACKENDLESS IMPLEMENTATION
//                    Backendless.UserService.register(backendlessUser, new AsyncCallback<BackendlessUser>() {
//
//                        @Override
//                        public void handleResponse(BackendlessUser response) {
//                            Toast.makeText(RegisterActivity.this,"Hey "+ nameOfUser + "You Have Been Successfully Registered", Toast.LENGTH_LONG).show();
//                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
//                            progressDialog.dismiss();
//                            finish();
//                        }
//
//                        @Override
//                        public void handleFault(BackendlessFault fault) {
//                            Toast.makeText(RegisterActivity.this,"Hey Registration Failed, Better Luck Again", Toast.LENGTH_LONG).show();
//                            progressDialog.dismiss();
//                        }
//                    });



                    //FIREBASE IMPLEMENTATION
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                                    Toast.makeText(RegisterActivity.this,"Hey "+ nameOfUser + "You Have Been Successfully Registered", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    progressDialog.dismiss();
                                    finish();

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this,"Hey Registration Failed, Better Luck Again", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                    }

                                    // ...
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
