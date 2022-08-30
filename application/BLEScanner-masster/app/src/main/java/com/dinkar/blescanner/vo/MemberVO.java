package com.dinkar.blescanner.vo;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class MemberVO implements Parcelable {
    @SerializedName("UserID")
    private String UserID;
    @SerializedName("password")
    private String password;
    @SerializedName("type")
    private String type;
    public MemberVO(String UserID, String password, String type){
        this.UserID = UserID;
        this.password = password;
        this.type = type;
    }
    public String getUserID() {
        return UserID;
    }
    public void setUserID(String userID) {
        this.UserID = userID;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    protected MemberVO(Parcel in) {
        UserID = in.readString();
        password = in.readString();
        type = in.readString();
    }
    public static final Creator<MemberVO> CREATOR = new Creator<MemberVO>() {
        @Override
        public MemberVO createFromParcel(Parcel in) {
            return new MemberVO(in);
        }
        @Override
        public MemberVO[] newArray(int size) {
            return new MemberVO[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(UserID);
        parcel.writeString(password);
        parcel.writeString(type);
    }
}