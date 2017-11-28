package com.pkb149.pricedropalert;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
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

import org.json.JSONObject;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button signUp;
    String TAG="SignUp.java";
    EditText nameET;
    EditText emailET;
    private DatabaseReference mDatabase;
    EditText passwordET;
    ProgressBar loader;
    String url;
    PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        signUp=(Button)findViewById(R.id.email_signup_button);
        nameET=(EditText)findViewById(R.id.name_et);
        emailET=(EditText)findViewById(R.id.email_et_su);
        passwordET=(EditText)findViewById(R.id.password_et_su);
        loader=(ProgressBar) findViewById(R.id.loader);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefManager=new PrefManager(this);


        Intent receivedIntent = getIntent();
        if(receivedIntent.getExtras()!=null){
            url=receivedIntent.getExtras().getString("url");
        }

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    loader.setVisibility(View.VISIBLE);
                    loader.bringToFront();
                    mAuth.createUserWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString())
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String email=user.getEmail();
                                        email=email.replace(".", "");
                                        prefManager.setLoggedIn(email);
                                        mDatabase = FirebaseDatabase.getInstance().getReference();
                                        mDatabase.child("userss").child(email).child("email").setValue(user.getEmail());
                                        mDatabase.child("users").child(email).child("name").setValue(nameET.getText().toString());
                                        mDatabase.child("users").child(email).child("email").setValue(user.getEmail());
                                        loader.setVisibility(View.INVISIBLE);

                                        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                                        if(url!=null){
                                            intent.putExtra("url",url);
                                        }
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        loader.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                }

            }
        });


    }

    public  boolean validate(){
        boolean valid = true;

        String email = emailET.getText().toString();
        String name = nameET.getText().toString();
        String password = passwordET.getText().toString();

        if (name.isEmpty() || name.length()<3) {
            emailET.setError("Minimum length is 3 Characters");
            valid = false;
        } else {
            emailET.setError(null);
        }

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            onBackPressed();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
