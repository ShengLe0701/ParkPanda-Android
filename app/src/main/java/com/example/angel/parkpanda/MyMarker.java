package com.example.angel.parkpanda;

import java.io.Serializable;

public class MyMarker implements Serializable {
    private String mAddress;
    private String mDesc;
    private String  mFreeSpace;
    private String mLat;
    private String mLon;
    private String mName;
    private String mPrice;
    private String mTime;
    private String mTotalSpace;
    private String mParkImage;

    public MyMarker(String strAdd,String strDesc,String strFree,String strLat,String strLon,String strName,String strPrice,String strTime,String strTotal,String strParkIm)
    {
        this.mAddress = strAdd;
        this.mDesc = strDesc;
        this.mFreeSpace = strFree;
        this.mLat=strLat;
        this.mLon=strLon;
        this.mName=strName;
        this.mPrice=strPrice;
        this.mTime=strTime;
        this.mTotalSpace=strTotal;
        this.mParkImage=strParkIm;
    }

    public String getmParkImage()
    {
        return mParkImage;
    }
    public void setmParkImage(String mLabel)
    {
        this.mParkImage=mLabel;
    }

    public String getmAddress()
    {
        return mAddress;
    }
    public void setmAddress(String mLabel)
    {
        this.mAddress=mLabel;
    }

    public String getmDesc()
    {
        return mDesc;
    }
    public void setmDesc(String mLabel)
    {
        this.mDesc=mLabel;
    }

    public String getmFreeSpace()
    {
        return mFreeSpace;
    }
    public void setmFreeSpace(String mLabel)
    {
        this.mFreeSpace=mLabel;
    }

    public String getmLat()
    {
        return mLat;
    }
    public void setmLat(String mLabel)
    {
        this.mLat=mLabel;
    }

    public String getmLon()
    {
        return mLon;
    }
    public void setmLon(String mLabel)
    {
        this.mLon=mLabel;
    }

    public String getmName()
    {
        return mName;
    }
    public void setmName(String mLabel)
    {
        this.mName=mLabel;
    }

    public String getmPrice()
    {
        return mPrice;
    }
    public void setmPrice(String mLabel) { this.mPrice=mLabel;  }

    public String getmTime()
    {
        return mTime;
    }
    public void setmTime(String mLabel)
    {
        this.mTime=mLabel;
    }

    public String getmTotalSpace()
    {
        return mTotalSpace;
    }
    public void setmTotalSpace(String mLabel)
    {
        this.mTime=mLabel;
    }

}