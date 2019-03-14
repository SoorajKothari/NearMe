package com.example.danish.nearmee;

import android.os.AsyncTask;

import com.example.danish.nearmee.DataParser;
import com.example.danish.nearmee.DownloadUrl;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearByPlaces extends AsyncTask<Object,String,String> {

    private  String googleplacedata,url;
    private GoogleMap mMap;
    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googleplacedata = downloadUrl.readtheurl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleplacedata;
    }

    @Override
    protected void onPostExecute(String s) {

        List<HashMap<String,String>> nearbyplaceslist = null;
        DataParser dataParser = new DataParser();
        nearbyplaceslist = dataParser.parse(s);
        DisplayNearPlaces(nearbyplaceslist);

    }


    private void DisplayNearPlaces(List<HashMap<String,String>> nearbyplaceslist)
    {

        for(int i=0;i<nearbyplaceslist.size(); i++)
        {
            MarkerOptions markeroption = new MarkerOptions();
            HashMap<String,String> googlenearybyplace = nearbyplaceslist.get(i);
            String nameofplace = googlenearybyplace.get("place_name");
            String vicinity = googlenearybyplace.get("vicinity");
            double lat = Double.parseDouble(googlenearybyplace.get("lat"));
            double lng = Double.parseDouble(googlenearybyplace.get("lng"));

            LatLng latLng = new LatLng(lat,lng);

            markeroption.position(latLng);
            markeroption.title(nameofplace + " : " + vicinity);
            markeroption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mMap.addMarker(markeroption);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        }
    }
}
