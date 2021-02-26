package com.example.loginapplication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.Arrays;

public class Login extends AppCompatActivity {

    private EditText loginEmailtext;              // Email Text Field
    private EditText loginPasswordtext;           // Password Text Field
    private Button loginbtn;                       // Login button
    private Button loginregisterbtn;                // Registration button
    private LoginButton loginButton;                 // Facebook login button
    private FirebaseAuth mAuth;                   // Firebase AUTH VARIABLE
    private ProgressBar progressBar;            // Progressbar
    private CheckBox checkBox;                 // Checkbox
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private static final String TAG = "FBAUTH";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());


        progressBar = findViewById(R.id.login_progressBar);                   // Match the variable it related ids
        checkBox = findViewById(R.id.login_checkbox);
        loginEmailtext = findViewById(R.id.login_email);
        loginPasswordtext = findViewById(R.id.login_password);
        loginbtn = findViewById(R.id.login_ui_button);
        loginregisterbtn = findViewById(R.id.login_register_button);
        callbackManager = CallbackManager.Factory.create();

        loginButton =findViewById(R.id.login_button);





        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setPermissions(Arrays.asList(EMAIL));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {        // Facebook login button actions
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "onError: "+exception.getMessage());
                // App code
            }
        });




        loginbtn.setOnClickListener(new View.OnClickListener() {               // Login button on click listener
            @Override
            public void onClick(View view) {
                String loginEmail = loginEmailtext.getText().toString();
                String loginPass = loginPasswordtext.getText().toString();

                if(!TextUtils.isEmpty(loginEmail)|| !TextUtils.isEmpty(loginPass)){
                    progressBar.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(loginEmail,loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                sendtoMain();
                            }else {
                                String error = task.getException().getMessage();
                                Toast.makeText(getApplicationContext(),"Error :"+error,Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    loginPasswordtext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());  // If user tick on the check box
                }else {
                    loginPasswordtext.setTransformationMethod(PasswordTransformationMethod.getInstance());    // If use tick on the check box
                }
            }
        });

        loginregisterbtn.setOnClickListener(new View.OnClickListener() {                  //If user click on the register button then move to the Register page
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){

            Intent intent = new Intent(Login.this,MainActivity.class);  // If current user is not expire then move to the Profile page
            startActivity(intent);
            finish();
        }
    }

    private void sendtoMain(){
        Intent intent = new Intent(Login.this,MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {                             // Handele the acccess toke in facebook
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            openProfile();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

//    private void openProfile() {
//        startActivity(new Intent(this,MainActivity.class));
//        finish();
//    }
}