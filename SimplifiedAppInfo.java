package com.axel.datatracking;

import android.net.TrafficStats;
import android.os.Parcel;
import android.os.Parcelable;

public class SimplifiedAppInfo implements Parcelable {

    private String AppName;
    private String AppPackageName;
    private int uid;
    private long upbyts = 0;
    private long downbytes = 0;

    SimplifiedAppInfo (String name, String title, int uid) {
        this.AppName =  name;
        this.AppPackageName = title;
        this.uid = uid;
    }

    private SimplifiedAppInfo (Parcel source) {
        AppName = source.readString();
        AppPackageName = source.readString();
        uid = source.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(AppName);
        dest.writeString(AppPackageName);
        dest.writeInt(uid);
    }

    void setUpbyts() {
        this.upbyts = TrafficStats.getUidTxBytes(uid);
    }

    void setDownbytes() {
        this.downbytes = TrafficStats.getUidRxBytes(uid);
    }

    long getDownbytes() {
        return downbytes;
    }

    long getUpbyts() {
        return upbyts;
    }

    int getUid() {
        return uid;
    }

    String getAppName() {
        return AppName;
    }

    public static final Creator<SimplifiedAppInfo> CREATOR = new Creator<SimplifiedAppInfo>() {
        @Override
        public SimplifiedAppInfo createFromParcel(Parcel parcel) {
            return new SimplifiedAppInfo(parcel);
        }

        @Override
        public SimplifiedAppInfo[] newArray(int size) {
            return new SimplifiedAppInfo[size];
        }
    };
}

