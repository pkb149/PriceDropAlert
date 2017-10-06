package com.pkb149.pricedropalert;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pkb149.pricedropalert.Utility.PrefManager;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    CallbackManager mCallbackManager;
    LoginButton loginButton;
    String TAG="MainActivity";
    PrefManager prefManager;
    private DatabaseReference mDatabase;
    ProfileTracker mProfileTracker;
    String url;
    private FirebaseAuth mAuth;
    EditText passwordET;
    EditText emailET;
    Button emailLoginButton;
    ProgressBar loader;
    Button signupButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefManager = new PrefManager(this);
        passwordET=(EditText) findViewById(R.id.password_et);
        emailET=(EditText) findViewById(R.id.email_et);
        emailLoginButton=(Button)findViewById(R.id.email_login_button);
        loader=(ProgressBar) findViewById(R.id.loader);
        signupButton=(Button)findViewById(R.id.signup_btn);


        //FIrebase auth (email/pass) instnace.
        mAuth = FirebaseAuth.getInstance();

        if (prefManager.isLoggedIn()) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
                finish();
        }

        Intent receivedIntent = getIntent();
        if(receivedIntent.getExtras()!=null){
            url=receivedIntent.getExtras().getString("url");
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mCallbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);

                if(Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            Log.v("facebook - profile", currentProfile.getFirstName());
                            mProfileTracker.stopTracking();
                            onLoginSuccess(loginResult,  currentProfile);
                        }
                    };
                    // no need to call startTracking() on mProfileTracker
                    // because it is called by its constructor, internally.
                }
                else {
                    Profile profile = Profile.getCurrentProfile();
                    Log.v("facebook - profile", profile.getFirstName());
                    onLoginSuccess(loginResult,  profile);
                }

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
        emailLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    loader.setVisibility(View.VISIBLE);
                    loader.bringToFront();
                    mAuth.signInWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String email=user.getEmail().replace(".", "");
                                        prefManager.setLoggedIn(email);
                                        loader.setVisibility(View.INVISIBLE);
                                        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                                        if(url!=null){
                                            intent.putExtra("url",url);
                                        }
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        loader.setVisibility(View.INVISIBLE);
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    // ...
                                }
                            });
                }

            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this,SignUp.class);
                if(url!=null){
                    intent.putExtra("url",url);
                }
                startActivity(intent);
            }
        });



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    void onLoginSuccess(final LoginResult loginResult, final Profile profile){
        //final Profile profile = Profile.getCurrentProfile();
        Log.e(TAG,profile.getId());

        prefManager.setLoggedIn(profile.getId());
        mDatabase.child("userss").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(profile.getId())) {
                    Log.e(TAG,"user doesn't exists");
                    mDatabase.child("users").child(profile.getId()).child("name").setValue(profile.getFirstName()+" "+profile.getLastName());
                    //https://graph.facebook.com/me?fields=id,email,first_name,gender,last_name,link,locale,name,timezone,updated_time,verified&access_token=<value of access_token>&debug=all
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {
                                    if (response.getError() != null) {
                                    } else {
                                        String email = object.optString("email");
                                        Log.e(TAG,email);
                                        mDatabase.child("users").child(profile.getId()).child("email").setValue(email);
                                        mDatabase.child("userss").child(profile.getId()).setValue(email);
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,email");
                    request.setParameters(parameters);
                    request.executeAsync();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        if(url!=null){
            intent.putExtra("url",url);
        }
        startActivity(intent);
        finish();
    }

    public boolean validate(){
            boolean valid = true;

            String email = emailET.getText().toString();
            String password = passwordET.getText().toString();

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailET.setError("Enter a valid Email Address");
                valid = false;
            } else {
                emailET.setError(null);
            }

            if (password.isEmpty() || password.length() < 6 ) {
                //_passwordText.setError("between 4 and 10 alphanumeric characters");

                passwordET.setError("Password format incorrect");
                valid = false;
            } else {
                passwordET.setError(null);
            }

            return valid;
    }
}
