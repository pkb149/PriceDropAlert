package com.pkb149.pricedropalert;

/**
 * Created by CoderGuru on 08-10-2017.
 */

import android.os.Parcel;
import android.os.Parcelable;


public class CardViewData implements Parcelable {
    private String product_tracking_id=null;
    private String productName=null;
    private String oldPrice=null;
    private String newPrice=null;
    private String url=null;
    private String urlToImage=null;


    CardViewData(Parcel in) {
        this.product_tracking_id = in.readString();
        this.productName = in.readString();
        this.oldPrice = in.readString();
        this.newPrice = in.readString();
        this.url = in.readString();
        this.urlToImage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(product_tracking_id);
        dest.writeString(productName);
        dest.writeString(oldPrice);
        dest.writeString(newPrice);
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

    public String getProduct_tracking_id() {
        return product_tracking_id;
    }

    public void setProduct_tracking_id(String product_tracking_id) {
        this.product_tracking_id = product_tracking_id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(String newPrice) {
        this.newPrice = newPrice;
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
