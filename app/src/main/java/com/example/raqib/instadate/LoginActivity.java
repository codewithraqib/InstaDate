package com.example.raqib.instadate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

public class LoginActivity extends AppCompatActivity implements  View.OnClickListener{
    EditText emailField;
    EditText passwordField;
    Button LoginButton;
    TextView RegisterHere, ForgetPassword;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "Login Activity";
    private CoordinatorLayout loginCoordinatorLayout;
    CallbackManager mCallbackManager = CallbackManager.Factory.create();
   // static TextView setNameInDrawer, setEmailInDrawer;


    @Override
    protected void onStart(){
        super.onStart();
        Log.e("onStart: logged out","successfully" );
        mAuth.signOut();
    }
    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        loginCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.loginCoordinatorLayout);


        // Initialize Facebook Login button

        com.facebook.login.widget.LoginButton loginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "facebook:onError", error);
            }
        });

        emailField = (EditText) findViewById(R.id.UserEmailLogin);
        passwordField = (EditText) findViewById(R.id.UserLoginPassword);
        LoginButton = (Button) findViewById(R.id.LoginButton);
        RegisterHere = (TextView) findViewById(R.id.newUser);
        ForgetPassword = (TextView) findViewById(R.id.forgetPassword);

        //TO SET THE NAME IN THE DRAWER BUT FAILS DUE TO NPE
//        setNameInDrawer = (TextView) findViewById(R.id.drawerLogIn);
//        setEmailInDrawer = (TextView) findViewById(R.id.drawerUserEmail);
//        Log.e("LOGIN ISSUE*****: ",String.valueOf(setNameInDrawer));
//        Log.e("LOGIN ISSUE*****:: ",String.valueOf(setEmailInDrawer));


//        GoForLogin goForLogin = new GoForLogin();
//        Log.e("Background call", "Working...");
//        goForLogin.execute();
//        Log.e("Background call", "Done!");

        mAuth = FirebaseAuth.getInstance();

        SignInButton googleSignInButton = (SignInButton) findViewById(R.id.googleSignUpButton);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.EMAIL))
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast toast =  Toast.makeText(LoginActivity.this, "There is an error Signing In, Try again!", Toast.LENGTH_SHORT);
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

        LoginButton.setOnClickListener(this);
        RegisterHere.setOnClickListener(this);
        ForgetPassword.setOnClickListener(this);

    }

    //FACEBOOK LOGIN
    private void handleFacebookAccessToken(AccessToken token) {
        Log.e(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        Toast.makeText(LoginActivity.this,"You Have Signed Up With Facebook!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        Log.e("SignIn ",String.valueOf(signInIntent));
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    for (UserInfo profile : user.getProviderData()) {

                        String email = profile.getEmail();
                        Toast.makeText(LoginActivity.this,"Check your email" + email , Toast.LENGTH_LONG).show();

                    };
                }
                Toast.makeText(LoginActivity.this,"You Have Signed Up With Google!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));

            } else {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(LoginActivity.this,"Signing up with Google failed, Try again!", Toast.LENGTH_LONG).show();
            }
        }else
        {
            super.onActivityResult(requestCode, resultCode, data);

            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
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
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                        progressDialog.show();


                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        mAuth = FirebaseAuth.getInstance();

                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                if (user != null) {
                                                    // User is signed in
                                                    Toast toast = Toast.makeText(LoginActivity.this, "Hey You Have Been Successfully Logged In", Toast.LENGTH_SHORT);
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();

                                                    progressDialog.dismiss();

                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                }

                                        //ON SIGN IN FAILED
                                        if (!task.isSuccessful()) {
                                            Snackbar snackbar = Snackbar.make( loginCoordinatorLayout,"Hey Log In Failed, make sure you have entered correct email!", Snackbar.LENGTH_INDEFINITE)
                                                    .setAction("OKAY", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                        }
                                                    });
                                            snackbar.setActionTextColor(Color.CYAN);
                                            View sbView = snackbar.getView();
                                            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                            textView.setTextColor(Color.WHITE);

                                            snackbar.show();
                                            Toast toast2 = Toast.makeText(LoginActivity.this, "Hey Log In Failed, make sure you have entered correct email!", Toast.LENGTH_SHORT);
                                            toast2.setGravity(Gravity.CENTER, 0, 0);
                                            toast2.show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                    }

                    break;

                case R.id.newUser:
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));


                    break;

                case R.id.forgetPassword:

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
                        progressDialog.show();

                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.sendPasswordResetEmail(emailToReset)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast toast = Toast.makeText(LoginActivity.this, "Check your mail address for password reset email!", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            Log.d(TAG, "Email sent.");
                                            progressDialog.dismiss();
                                        }else{
                                            Toast toast = Toast.makeText(LoginActivity.this, "There is an error, Make sure this is a valid email", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                    }

            }
        }

}
