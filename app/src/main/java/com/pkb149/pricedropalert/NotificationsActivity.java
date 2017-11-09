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

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity implements RecyclerViewAdapter.NewsListItemClickListener{
    PrefManager prefManager;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private AdView mAdView;
    List<CardViewData> data;
    RecyclerViewAdapter adapter;
    RecyclerView _recyclerView;
    CardViewData cardViewData;
    SwipeRefreshLayout swipeRefreshLayout;
    String TAG="NotificationsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefManager=new PrefManager(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FacebookSdk.sdkInitialize(getApplicationContext());
        _recyclerView=(RecyclerView)findViewById(R.id.recycler_view_not);
        data= new ArrayList<>();
        setTitle("Notifications");

        //banner ad
        mAdView = (AdView) findViewById(R.id.adView);
        MobileAds.initialize(getApplicationContext(),
                "ca-app-pub-7059442386503248~2825075442");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swipeContainer_not);
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

        final DatabaseReference notifRef =mDatabase.child("users").child(prefManager.getUserssId()).child("notifications");
        notifRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,"onDataChange Called");
                    for(DataSnapshot notificationSnapshot : dataSnapshot.getChildren()){
                        final DatabaseReference productsRef
                                =mDatabase.child("users")
                                .child(prefManager.getUserssId())
                                .child("products")
                                .child(notificationSnapshot.getValue().toString());
                        Log.d(TAG,"Notifcation Id: "+notificationSnapshot.getValue().toString());
                                productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Log.d(TAG,"Fetching Product Details:");
                                        cardViewData= new CardViewData(Parcel.obtain());
                                        if(dataSnapshot.child("imgUrl").getValue()!=null) {
                                            cardViewData.setUrlToImage(dataSnapshot.child("imgUrl").getValue().toString());
                                            cardViewData.setProductName(dataSnapshot.child("prodName").getValue().toString());
                                            cardViewData.setUrl(dataSnapshot.child("url").getValue().toString());
                                            cardViewData.setProduct_tracking_id(dataSnapshot.getKey());
                                            cardViewData.setOldPrice(dataSnapshot.child("oldPrice").getValue().toString());
                                            cardViewData.setNewPrice(dataSnapshot.child("newPrice").getValue().toString());
                                            adapter.add(cardViewData);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private static final int MENU_CLEAR_NOT = Menu.FIRST + 4;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(0,MENU_CLEAR_NOT, Menu.NONE, "Clear Notification");
        MenuItem item = menu.findItem(R.id.action_notifications);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
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
        else if(id==android.R.id.home){
            onBackPressed();
            finish();
            return true;
        }
        else if(id==MENU_CLEAR_NOT){
            Toast.makeText(getApplicationContext(),"clicked",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onListItemClick(int clickedItemIndex) {

    }
}
