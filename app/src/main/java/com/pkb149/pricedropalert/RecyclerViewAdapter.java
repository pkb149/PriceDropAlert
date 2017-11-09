package com.pkb149.pricedropalert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pkb149.pricedropalert.Utility.PrefManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.NewsViewHolder> {


    Context context;
    List<CardViewData> cardViewDatas= new ArrayList<>();
    NewsListItemClickListener listener;
    Integer callingFragment;


    public interface NewsListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public RecyclerViewAdapter(List<CardViewData> cardViewDatas, Context context, NewsListItemClickListener listener) {
        this.cardViewDatas = cardViewDatas;
        this.context = context;
        this.listener=listener;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        NewsViewHolder holder = new NewsViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final NewsViewHolder holder, final int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        //holder.more.setImageResource(R.drawable.ic_more_vert_black_24dp);
        holder.imageView.setScaleType(ImageView.ScaleType.CENTER);
        Picasso.with(context).load(cardViewDatas.get(position).getUrlToImage())
                .placeholder(R.drawable.progress_animation)

                .into(holder.imageView,new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            holder.imageView.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.border));
                        }

                    }

                    @Override
                    public void onError() {
                        // TODO Auto-generated method stub

                    }
                });

        holder.productName.setText(cardViewDatas.get(position).getProductName());
        holder.oldPrice.setText(cardViewDatas.get(position).getOldPrice());
        holder.newPrice.setText(cardViewDatas.get(position).getNewPrice());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Are you sure?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PrefManager prefManager=new PrefManager(context);
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("users")
                                .child(prefManager.getUserssId())
                                .child("products")
                                .child(cardViewDatas.get(position).getProduct_tracking_id())
                                .removeValue();
                                //.setValue("NULL");
                    }
                });
                builder.setNegativeButton("Cancel", null);
                final AlertDialog ad=builder.create();
                //ad.getWindow().setBackgroundDrawableResource(R.color.primary_dark);
                builder.show();
            }
        });

        holder.buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),cardViewDatas.get(position).getUrl(), Toast.LENGTH_LONG).show();
                Uri uri = Uri.parse(cardViewDatas.get(position).getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return cardViewDatas.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, CardViewData data) {
        cardViewDatas.add(position, data);
        notifyItemInserted(position);
    }
    public void add(CardViewData data) {
        cardViewDatas.add(data);
        notifyDataSetChanged();
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(CardViewData data) {
        //int position = list.indexOf(data);
        //list.remove(position);
        // notifyItemRemoved(position);
    }
    public void clear() {
        cardViewDatas.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<CardViewData>  data) {
        cardViewDatas.clear();
        cardViewDatas.addAll(data);
        notifyDataSetChanged();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CardView cv;
        ImageView more;
        Button buy;
        Button delete;
        ImageView imageView;
        TextView productName;
        TextView oldPrice;
        TextView newPrice;

        public NewsViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardView);
            buy = (Button) itemView.findViewById(R.id.buy_now_button);
            delete = (Button) itemView.findViewById(R.id.delete_product);
            imageView=(ImageView) itemView.findViewById(R.id.viewGif);
            productName=(TextView)itemView.findViewById(R.id.product_name);
            oldPrice=(TextView)itemView.findViewById(R.id.price);
            newPrice=(TextView)itemView.findViewById(R.id.new_price);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            listener.onListItemClick(clickedPosition);
            //Uri uri = Uri.parse(cardViewDatas.get(clickedPosition).getUrl());
            //Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //context.startActivity(intent);
        }
    }

}