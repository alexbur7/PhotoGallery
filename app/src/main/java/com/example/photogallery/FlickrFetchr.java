package com.example.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrFetchr {
    private static final String TAG="FlickrFetchr";
    private static final String API_KEY ="cc016265e7b08c89434c802cc7e4b00b";

    public byte[] getUrlBytes(String urlSpeck) throws IOException {
        URL url = new URL(urlSpeck);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage()+": with "+ urlSpeck);
            }
            int byteRead=0;
            byte[] buffer = new byte[1024];
            while((byteRead=in.read(buffer))>0){
                out.write(buffer,0,byteRead);
            }
            out.close();
            return out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpeck) throws IOException {
        return new String(getUrlBytes(urlSpeck));
    }

    public List<GalleryItem> fetchItems(){
        List<GalleryItem> items = new ArrayList<>();
        try{
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method","flickr.photos.getRecent")
                    .appendQueryParameter("api_key",API_KEY)
                    .appendQueryParameter("format","json")
                    .appendQueryParameter("nojsoncallback","1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG,"Received JSON: "+jsonString);
            JSONObject jsonObject = new JSONObject(jsonString);
            parseItems(items,jsonObject);
        } catch (IOException e) {
            Log.e(TAG,"Failed to fetch URL: ",e);
        } catch (JSONException e) {
            Log.e(TAG,"Failed to parse JSON",e);
        }
        return items;
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws JSONException {
        JSONObject photosSJSONObject = jsonBody.getJSONObject("photos");
        JSONArray photoJSONArray = photosSJSONObject.getJSONArray("photo");

        for (int i = 0; i < photoJSONArray.length(); i++) {
            JSONObject photoJSONObject = photoJSONArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setmId(photoJSONObject.getString("id"));
            item.setmCaption(photoJSONObject.getString("title"));

            if (!photoJSONObject.has("url_s")){
                continue;
            }

            item.setmUrl(photoJSONObject.getString("url_s"));
            items.add(item);
        }
    }
}
