package com.pkb149.pricedropalert;

import android.content.Intent;
import android.os.Parcel;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pkb149.pricedropalert.Utility.PrefManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements RecyclerViewAdapter.NewsListItemClickListener{
    PrefManager prefManager;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private AdView mAdView;
    ProgressBar loader;
    List<CardViewData> data;
    RecyclerViewAdapter adapter;
    RecyclerView _recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    CardViewData cardViewData;
    String TAG="DashboardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Submitted Products");
        prefManager=new PrefManager(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FacebookSdk.sdkInitialize(getApplicationContext());
        loader=(ProgressBar) findViewById(R.id.loader);
        //loader.setVisibility(View.VISIBLE);
        _recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        data= new ArrayList<>();
        swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        adapter = new RecyclerViewAdapter(data, getApplicationContext(),this);
        _recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getApplicationContext());
        _recyclerView.setLayoutManager(linearLayoutManager);

        //banner ad
        mAdView = (AdView) findViewById(R.id.adView);
        MobileAds.initialize(getApplicationContext(),
                "ca-app-pub-7059442386503248~2825075442");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        final DatabaseReference productsRef =mDatabase.child("users").child(prefManager.getUserssId()).child("products");

        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    cardViewData= new CardViewData(Parcel.obtain());
                    if(messageSnapshot.child("imgUrl").exists()){
                        cardViewData.setUrlToImage(messageSnapshot.child("imgUrl").getValue().toString());
                        cardViewData.setProductName(messageSnapshot.child("prodName").getValue().toString());
                        cardViewData.setUrl(messageSnapshot.child("url").getValue().toString());
                        cardViewData.setProduct_tracking_id(messageSnapshot.getKey());
                        cardViewData.setOldPrice(messageSnapshot.child("oldPrice").getValue().toString());
                        cardViewData.setNewPrice(messageSnapshot.child("newPrice").getValue().toString());
                        adapter.add(cardViewData);
                        //cardViewData.setPrice(messageSnapshot.child("price").getChildren());
                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        /*cardViewData.setPrice("10999");
        cardViewData.setProductName("Redmi Note 4 (Black, 64 GB)");
        cardViewData.setUrl("http://dl.flipkart.com/dl/apple-iphone-7-black-32-gb/p/itmen6daftcqwzeg?pid=MOBEMK62PN2HU7EE&cmpid=product.share.pp");
        cardViewData.setProduct_tracking_id("1507453095205");
        cardViewData.setUrlToImage("https://rukminim1.flixcart.com/image/832/832/mobile/7/e/e/apple-iphone-7-na-original-imaemzee435f9gpu.jpeg");

        data.add(cardViewData);
        data.add(cardViewData);
        data.add(cardViewData);
        data.add(cardViewData);
        data.add(cardViewData);
        data.add(cardViewData);
        data.add(cardViewData);
        data.add(cardViewData);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_dashboard);
        item.setVisible(false);
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
        else if(id==R.id.action_notifications){
            Intent intent = new Intent(getApplicationContext(), NotificationsActivity.class);
            startActivity(intent);
            return true;
        }
        else if(id==android.R.id.home){
            onBackPressed();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onListItemClick(int clickedItemIndex) {
       // Toast.makeText(getApplicationContext(),data.get(clickedItemIndex).getProduct_tracking_id(),Toast.LENGTH_LONG).show();
    }
}
