package com.pkb149.pricedropalert;

/**
 * Created by CoderGuru on 12-11-2017.
 */

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;


public class NotificationsRecyclerViewAdapter extends RecyclerView.Adapter<NotificationsRecyclerViewAdapter.NotificationsViewHolder> {


    Context context;
    List<NotificationsCardViewData> notificationsCardViewDatas= new ArrayList<>();
    NotificationsListItemClickListener listener;
    Integer callingFragment;


    public interface NotificationsListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public NotificationsRecyclerViewAdapter(List<NotificationsCardViewData> cardViewDatas, Context context, NotificationsListItemClickListener listener) {
        this.notificationsCardViewDatas = cardViewDatas;
        this.context = context;
        this.listener=listener;
    }

    @Override
    public NotificationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifications_card_view, parent, false);
        NotificationsViewHolder holder = new NotificationsViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final NotificationsViewHolder holder, final int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        //holder.more.setImageResource(R.drawable.ic_more_vert_black_24dp);
        holder.imageView.setScaleType(ImageView.ScaleType.CENTER);
        Picasso.with(context).load(notificationsCardViewDatas.get(position).getUrlToImage())
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

        holder.productName.setText(notificationsCardViewDatas.get(position).getProductName());
        holder.notifiaction.setText(notificationsCardViewDatas.get(position).getNotificationText());
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return notificationsCardViewDatas.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, NotificationsCardViewData data) {
        notificationsCardViewDatas.add(position, data);
        notifyItemInserted(position);
    }
    public void add(NotificationsCardViewData data) {
        notificationsCardViewDatas.add(data);
        notifyDataSetChanged();
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(NotificationsCardViewData data) {
        //int position = list.indexOf(data);
        //list.remove(position);
        // notifyItemRemoved(position);
    }
    public void clear() {
        notificationsCardViewDatas.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<NotificationsCardViewData>  data) {
        notificationsCardViewDatas.clear();
        notificationsCardViewDatas.addAll(data);
        notifyDataSetChanged();
    }

    class NotificationsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CardView cv;
        ImageView imageView;
        TextView productName;
        TextView notifiaction;

        public NotificationsViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.notifications_card_view);
            imageView=(ImageView) itemView.findViewById(R.id.view_gif_not);
            productName=(TextView)itemView.findViewById(R.id.product_name_not);
            notifiaction=(TextView)itemView.findViewById(R.id.notification);
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