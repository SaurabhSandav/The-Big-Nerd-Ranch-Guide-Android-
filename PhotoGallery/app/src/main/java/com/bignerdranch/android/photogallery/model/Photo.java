package com.bignerdranch.android.photogallery.model;

import android.net.Uri;

/**
 * Created by saurabh on 1/2/16.
 */
public class Photo {

    private String isfamily;
    private String farm;
    private String id;
    private String title;
    private String ispublic;
    private String url_s;
    private String owner;
    private String secret;
    private String height_s;
    private String server;
    private String width_s;
    private String isfriend;

    public String getIsfamily() {
        return isfamily;
    }

    public String getFarm() {
        return farm;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getIspublic() {
        return ispublic;
    }

    public String getUrl_s() {
        return url_s;
    }

    public String getOwner() {
        return owner;
    }

    public String getSecret() {
        return secret;
    }

    public String getHeight_s() {
        return height_s;
    }

    public String getServer() {
        return server;
    }

    public String getWidth_s() {
        return width_s;
    }

    public String getIsfriend() {
        return isfriend;
    }

    public Uri getPhotoPageUri() {
        return Uri.parse("http://www.flickr.com/photos/")
                .buildUpon()
                .appendPath(owner)
                .appendPath(id)
                .build();
    }
}
