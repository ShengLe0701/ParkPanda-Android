package com.example.angel.parkpanda;

/**
 * Created by Angel on 7/28/2016.
 */
public class FIREPARKINFO {

    private String address;
    private String lon;
    private String price;
    private String time;
    private String desc;
    private String name;
    private String lat;
    private String totalSpace;
    private String  freeSpace;
    private String  image;



    public String getImage()
    {
        return image;
    }
    public void setImage(String mLabel)
    {
        this.image=mLabel;
    }

    public String getaddress()
    {
        return address;
    }
    public void setaddress(String mLabel)
    {
        this.address=mLabel;
    }

    public String getdesc()
    {
        return desc;
    }
    public void setdesc(String mLabel)
    {
        this.desc=mLabel;
    }

    public String getfreeSpace()
    {
        return freeSpace;
    }
    public void setmfreeSpace(String mLabel)
    {
        this.freeSpace=mLabel;
    }

    public String getlat()
    {
        return lat;
    }
    public void setlat(String mLabel)
    {
        this.lat=mLabel;
    }

    public String getlon()
    {
        return lon;
    }
    public void setlon(String mLabel)
    {
        this.lon=mLabel;
    }

    public String getname()
    {
        return name;
    }
    public void setname(String mLabel)
    {
        this.name=mLabel;
    }

    public String getprice()
    {
        return price;
    }
    public void setprice(String mLabel) { this.price=mLabel;  }

    public String gettime()
    {
        return time;
    }
    public void settime(String mLabel)
    {
        this.time=mLabel;
    }

    public String gettotalSpace()
    {
        return totalSpace;
    }
    public void settotalSpace(String mLabel)
    {
        this.totalSpace=mLabel;
    }
}

