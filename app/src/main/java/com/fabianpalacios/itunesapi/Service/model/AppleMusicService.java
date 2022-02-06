package com.fabianpalacios.itunesapi.Service.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cafsoft.foundation.HTTPURLResponse;
import cafsoft.foundation.URLComponents;
import cafsoft.foundation.URLQueryItem;
import cafsoft.foundation.URLSession;

public class AppleMusicService {
    private URLComponents components = null;

    public AppleMusicService(){
        components = new URLComponents();
        components.setScheme("https");
        components.setHost("itunes.apple.com");
        components.setPath("/search");
    }

    //Se pasa la URL, se obtiene las canciones y se las almacena en el objeto.
    public void searchSongsByTerm(String searchTerm, OnDataResponse delegate){
        components.setQueryItems(new URLQueryItem[]{
                new URLQueryItem("media","music"),
                new URLQueryItem("entity", "song"),
                new URLQueryItem("term", searchTerm)
        });
        URLSession.getShared().dataTask(components.getURL(),(data, response, error)->{
            HTTPURLResponse resp = (HTTPURLResponse) response;
            Root root = null;
            int statusCode = -1;

            if(error == null && resp.getStatusCode() == 200){
                String text = data.toText();
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                root = gson.fromJson(text, Root.class);
                root.toString();
                Log.d("Demostracion", "ok");
                statusCode = resp.getStatusCode();



            }else {
                Log.d("Demostracion", "ERROR NO ES NULL");
            }
            if(delegate !=  null){
                delegate.onChange(error != null, statusCode,root);
            }
        }).resume();
    }

    public interface OnDataResponse{
        public abstract void onChange(boolean isNetworkError, int statusCode, Root root);
    }
}
