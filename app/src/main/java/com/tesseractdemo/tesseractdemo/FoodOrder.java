package com.tesseractdemo.tesseractdemo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class FoodOrder implements Parcelable {
    //three fields
    String name;
    int quantity;
    Double price;

    //constructor
    public FoodOrder(String fname, int fquantity, double fprice){
        name = fname;
        quantity = fquantity;
        price = fprice;
    }

    //Functions
    public void setName(String newName){
        this.name = newName;
    }

    public void setQuantity(int newQuantity){
        this.quantity = newQuantity;
    }

    public void setPrice(Double newPrice){
        this.price = newPrice;
    }

    public String getName(){
        return this.name;
    }

    public int getQuantity(){
        return this.quantity;
    }

    public Double getPrice(){
        return this.price;
    }

    public String printOrder() {
        return "Meal Name: " + this.getName() +
                ", Quantity: " + this.getQuantity() +
                ", Price: " + this.getPrice();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.quantity);
        dest.writeValue(this.price);
    }

    protected FoodOrder(Parcel in) {
        ArrayList orderList = new ArrayList<FoodOrder>();

        this.name = in.readString();
        this.quantity = in.readInt();
        this.price = (Double) in.readValue(Double.class.getClassLoader());
    }


    public static final Parcelable.Creator<FoodOrder> CREATOR = new Parcelable.Creator<FoodOrder>() {
        @Override
        public FoodOrder createFromParcel(Parcel source) {
            return new FoodOrder(source);
        }

        @Override
        public FoodOrder[] newArray(int size) {
            return new FoodOrder[size];
        }
    };

}
