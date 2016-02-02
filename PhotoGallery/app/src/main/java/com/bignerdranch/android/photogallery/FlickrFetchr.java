package com.bignerdranch.android.photogallery;

import android.net.Uri;

import com.bignerdranch.android.photogallery.model.Flickr;
import com.bignerdranch.android.photogallery.model.Photo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by saurabh on 31/1/16.
 */
public class FlickrFetchr {

    private static final String TAG = "FlickrFetchr";
    public static final String API_KEY = "";
    public static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    public static final String SEARCH_METHOD = "flickr.photos.search";
    public static final Uri ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            return out.toByteArray();

        } finally {
            connection.disconnect();
        }

    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<Photo> getRecentPhotos(String page) {
        String url = buildUrl(FETCH_RECENTS_METHOD, page, null);
        return downloadGalleryItems(url);
    }

    public List<Photo> searchPhotos(String page, String query) {
        String url = buildUrl(SEARCH_METHOD, page, query);
        return downloadGalleryItems(url);
    }

    private List<Photo> downloadGalleryItems(String url) {
        //Log.d(TAG, "downloadGalleryItems() called with: " + "url = [" + url + "]");
        List<Photo> items = new ArrayList<>();

        try {
            String jsonString = getUrlString(url);
            //Log.i(TAG, "Received JSON: " + jsonString);

            Gson gson = new GsonBuilder().create();
            Flickr flickr = gson.fromJson(jsonString, Flickr.class);
            items = new ArrayList<>(Arrays.asList(flickr.getPhotos().getPhoto()));

        } catch (IOException e) {
            //Log.e(TAG, "Failed to fetch items", e);
        }

        return items;
    }

    private String buildUrl(String method, String page, String query) {
        Uri.Builder uriBuilder = ENDPOINT.buildUpon()
                .appendQueryParameter("method", method);

        uriBuilder.appendQueryParameter("page", page);

        if (method.equals(SEARCH_METHOD))
            uriBuilder.appendQueryParameter("text", query);

        return uriBuilder.build().toString();
    }
}
