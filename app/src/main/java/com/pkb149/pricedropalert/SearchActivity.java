package com.pkb149.pricedropalert;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pkb149.pricedropalert.Utility.PrefManager;

public class SearchActivity extends AppCompatActivity {
    PrefManager prefManager;
    String receivedText;
    String TAG="SearchActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefManager=new PrefManager(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        FacebookSdk.sdkInitialize(getApplicationContext());
        final EditText tv=(EditText) findViewById(R.id.link);
        Button submit=(Button) findViewById(R.id.submit);
        Intent receivedIntent = getIntent();
        String receivedAction = receivedIntent.getAction();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(Intent.ACTION_SEND.equals(receivedAction)){
                receivedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
        }
        else if( receivedIntent.getExtras()!=null){
            receivedText=receivedIntent.getExtras().getString("url");
            //app has been launched directly, not from share list
        }
        if (receivedText != null) {
            //set the text
            Toast.makeText(getApplicationContext(),receivedText,Toast.LENGTH_LONG).show();
            Log.e(TAG,receivedText);
            tv.setText(receivedText);
        }
        if(prefManager.isLoggedIn()){
            mDatabase
                    .child("users")
                    .child(new PrefManager(this).getUserssId())
                    .child("firebase_instance_id")
                    .setValue(FirebaseInstanceId.getInstance().getToken());
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(prefManager.isLoggedIn()){
                    Toast.makeText(getApplicationContext(),"Product Details for tracking has been submitted",Toast.LENGTH_LONG).show();
                    String timeinmillies=Long.toString(System.currentTimeMillis());
                    mDatabase.child("users")
                            .child(prefManager.getUserssId())
                            .child("products")
                            .child(timeinmillies)
                            .child("url")
                            .setValue(tv.getText().toString());
                    mDatabase.child("users")
                            .child(prefManager.getUserssId())
                            .child("products")
                            .child(timeinmillies)
                            .child("firstRun")
                            .setValue("true");
                }else{
                    Toast.makeText(getApplicationContext(),"Log in to start tracking.",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("url",receivedText);
                    startActivity(intent);
                    finish();
                }
            }
        });
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            //TODO Log user Out
            prefManager.clearLoggedIn();
            if(LoginManager.getInstance()!=null){
                LoginManager.getInstance().logOut();
            }
            else {
                mAuth.signOut();
            }

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        /*else if(id==android.R.id.home){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }




}
