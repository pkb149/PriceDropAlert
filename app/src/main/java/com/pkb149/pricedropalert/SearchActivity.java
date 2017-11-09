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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pkb149.pricedropalert.Utility.PrefManager;

import java.net.URL;

public class SearchActivity extends AppCompatActivity {
    PrefManager prefManager;
    String receivedText;
    String TAG="SearchActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    static EditText tv;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefManager=new PrefManager(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        FacebookSdk.sdkInitialize(getApplicationContext());
        tv=(EditText) findViewById(R.id.link);
        Button submit=(Button) findViewById(R.id.submit);
        Intent receivedIntent = getIntent();
        String receivedAction = receivedIntent.getAction();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //banner ad
        mAdView = (AdView) findViewById(R.id.adView);
        MobileAds.initialize(getApplicationContext(),
                "ca-app-pub-7059442386503248~2825075442");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //Interstitial Ad
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        //my AdUnitId: ca-app-pub-7059442386503248/6196811410
        //replace before production
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        if(Intent.ACTION_SEND.equals(receivedAction)){
                receivedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
        }
        else if( receivedIntent.getExtras()!=null){
            receivedText=receivedIntent.getExtras().getString("url");
            //app has been launched directly, not from share list
        }
        if (receivedText != null) {
            //set the text
            //Toast.makeText(getApplicationContext(),receivedText,Toast.LENGTH_LONG).show();
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

                    String enteredString=tv.getText().toString();
                    String[] urlString=enteredString.split("\\s");
                    URL url=null;

                        try {
                            url = new URL(urlString[urlString.length-1]);

                        } catch (Exception exception) {

                        }
                        finally{
                            if(url==null){
                                Toast.makeText(getApplicationContext(),"Invalid URL",Toast.LENGTH_LONG).show();
                            }else{
                                    Toast.makeText(getApplicationContext(),"Submitting url for tracking...",Toast.LENGTH_LONG).show();
                                    submitForAlert("Flipkart",url);
                            }
                        }
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
        if(prefManager.isLoggedIn()) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
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
            //cleared firebase_instance_id to block notifications
            mDatabase.child("users")
                    .child(prefManager.getUserssId())
                    .child("firebase_instance_id")
                    .setValue("NULL");
            prefManager.clearLoggedIn();
            if(LoginManager.getInstance()!=null){
                LoginManager.getInstance().logOut();
                //facebook logout

            }
            else {
                // email logout
                mAuth.signOut();
            }

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        else if(id==R.id.action_dashboard){
            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(intent);
            return true;
        }
        else if(id==R.id.action_notifications){
            Intent intent = new Intent(getApplicationContext(), NotificationsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

public void submitForAlert(String website, URL url) {
    String timeinmillies = Long.toString(System.currentTimeMillis());
    mDatabase.child("users")
            .child(prefManager.getUserssId())
            .child("products")
            .child(timeinmillies)
            .child("url")
            .setValue(url.toString());
    mDatabase.child("users")
            .child(prefManager.getUserssId())
            .child("products")
            .child(timeinmillies)
            .child("runId")
            .setValue("0");
    mDatabase.child("users")
            .child(prefManager.getUserssId())
            .child("products")
            .child(timeinmillies)
            .child("firstRun")
            .setValue("true").addOnSuccessListener(SearchActivity.this, new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            mInterstitialAd.show();
            tv.setText("");
            Toast.makeText(getApplicationContext(),"Product has been submitted for tracking.",Toast.LENGTH_LONG).show();
        }
    });
}
}


