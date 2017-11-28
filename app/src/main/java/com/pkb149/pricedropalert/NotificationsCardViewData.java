package com.pkb149.pricedropalert;

/**
 * Created by CoderGuru on 12-11-2017.
 */

import android.os.Parcel;
import android.os.Parcelable;

public class NotificationsCardViewData implements Parcelable {
    private String notification_id=null;
    private String productName=null;
    private String notificationText=null;
    private String url=null;
    private String urlToImage=null;


    NotificationsCardViewData(Parcel in) {
        this.notification_id = in.readString();
        this.productName = in.readString();
        this.notificationText = in.readString();
        this.url = in.readString();
        this.urlToImage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(notification_id);
        dest.writeString(productName);
        dest.writeString(notificationText);
        dest.writeString(url);
        dest.writeString(urlToImage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<CardViewData> CREATOR = new Parcelable.Creator<CardViewData>() {

        @Override
        public CardViewData createFromParcel(Parcel source) {
            return new CardViewData(source);
        }

        @Override
        public CardViewData[] newArray(int size) {
            return new CardViewData[size];
        }
    };

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }
}

