package com.pkb149.pricedropalert;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

public class NotificationsActivity extends AppCompatActivity implements NotificationsRecyclerViewAdapter.NotificationsListItemClickListener{
    PrefManager prefManager;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private AdView mAdView;
    List<NotificationsCardViewData> data;
    NotificationsRecyclerViewAdapter adapter;
    RecyclerView _recyclerView;
    NotificationsCardViewData cardViewData;
    SwipeRefreshLayout swipeRefreshLayout;
    String TAG="NotificationsActivity";
    DatabaseReference notifRef;

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

        adapter = new NotificationsRecyclerViewAdapter(data, this,this);
        _recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getApplicationContext());
        _recyclerView.setLayoutManager(linearLayoutManager);

        notifRef =mDatabase.child("users").child(prefManager.getUserssId()).child("notifications");
        notifRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,"onDataChange Called");
                adapter.clear();
                    for(DataSnapshot notificationSnapshot : dataSnapshot.getChildren()){

                        cardViewData= new NotificationsCardViewData(Parcel.obtain());
                        if(notificationSnapshot.child("imgUrl").getValue()!=null
                                &&notificationSnapshot.child("prodName").getValue()!=null
                                &&notificationSnapshot.child("url").getValue()!=null
                                &&notificationSnapshot.child("msg").getValue()!=null){

                            cardViewData.setUrlToImage(notificationSnapshot.child("imgUrl").getValue().toString());
                            cardViewData.setProductName(notificationSnapshot.child("prodName").getValue().toString());
                            cardViewData.setUrl(notificationSnapshot.child("url").getValue().toString());
                            cardViewData.setNotification_id(notificationSnapshot.getKey());
                            cardViewData.setNotificationText(notificationSnapshot.child("msg").getValue().toString());
                            data.add(0,cardViewData);
                            adapter.notifyDataSetChanged();
                        }
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //long id=(long)viewHolder.itemView.getTag();
                //mAdapter.swapCursor(getAllGuests());
                //adapter.remove(viewHolder.getAdapterPosition());
                notifRef.child(data.get(viewHolder.getAdapterPosition()).getNotification_id()).removeValue();
                data.remove(viewHolder.getAdapterPosition());
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(_recyclerView);
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

            final AlertDialog.Builder builder = new AlertDialog.Builder(NotificationsActivity.this);

            builder.setTitle("Are you sure you want to clear all notification?\n You can clear indvidual notifications by swiping them right.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDatabase.child("users")
                            .child(prefManager.getUserssId())
                            .child("notifications")
                            .removeValue();
                    adapter.clear();
                    //.setValue("NULL");
                }
            });
            builder.setNegativeButton("Cancel", null);
            final AlertDialog ad=builder.create();
            //ad.getWindow().setBackgroundDrawableResource(R.color.primary_dark);
            builder.show();
            //Toast.makeText(getApplicationContext(),"clicked",Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onListItemClick(int clickedItemIndex) {
        Uri uri = Uri.parse(data.get(clickedItemIndex).getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        //Toast.makeText(getApplicationContext(),String.valueOf(clickedItemIndex),Toast.LENGTH_LONG).show();
    }
    @Override
    public void onNewIntent(Intent intent){
        Bundle extras = intent.getExtras();
        Toast.makeText(getApplicationContext(),"notification clicked",Toast.LENGTH_LONG).show();
    }
}