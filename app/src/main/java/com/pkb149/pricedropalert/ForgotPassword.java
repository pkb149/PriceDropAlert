package com.pkb149.pricedropalert;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
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

public class ForgotPassword extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button submit;
    String TAG="SignUp.java";
    EditText emailET;
    String url;
    PrefManager prefManager;
    ProgressBar loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mAuth = FirebaseAuth.getInstance();
        submit=(Button)findViewById(R.id.submit_email_for_password_reset_button);
        emailET=(EditText)findViewById(R.id.email_et_fp);
        loader=(ProgressBar)findViewById(R.id.loader_forgot_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefManager=new PrefManager(this);


        Intent receivedIntent = getIntent();
        if(receivedIntent.getExtras()!=null){
            url=receivedIntent.getExtras().getString("url");
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    loader.setVisibility(View.VISIBLE);
                    mAuth.sendPasswordResetEmail(emailET.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    loader.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(),"Mail has been sent",Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loader.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(),"Error occured, Please try again.",Toast.LENGTH_LONG).show();
                                }
                            });

                }

            }
        });


    }

    public  boolean validate(){
        boolean valid = true;

        String email = emailET.getText().toString();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.setError("Enter a valid Email Address");
            valid = false;
        } else {
            emailET.setError(null);
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

