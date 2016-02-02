package com.bignerdranch.android.photogallery.model;

/**
 * Created by saurabh on 1/2/16.
 */
public class Photos {

    private String total;
    private String page;
    private String pages;
    private Photo[] photo;
    private String perpage;

    public String getTotal() {
        return total;
    }

    public String getPage() {
        return page;
    }

    public String getPages() {
        return pages;
    }

    public Photo[] getPhoto() {
        return photo;
    }

    public String getPerpage() {
        return perpage;
    }

}
