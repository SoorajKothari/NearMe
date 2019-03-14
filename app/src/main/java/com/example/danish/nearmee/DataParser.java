package com.example.danish.nearmee;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser
{
    private HashMap<String,String> getsinglenearybyPlace(JSONObject googleplacejson)
    {
        HashMap<String,String> googleplacemap = new HashMap<>();
        String NameofPlace = "-NA-";
        String vicinty = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        try {
       if(!googleplacejson.isNull("name")) {
           NameofPlace = googleplacejson.getString("name");
       }

            if(!googleplacejson.isNull("vicinity")) {
                vicinty = googleplacejson.getString("vicinity");
            }

            latitude = googleplacejson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googleplacejson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            reference = googleplacejson.getString("reference");

            googleplacemap.put("place_name",NameofPlace);
            googleplacemap.put("vicinity",vicinty);
            googleplacemap.put("lat",latitude);
            googleplacemap.put("lng",longitude);
            googleplacemap.put("reference",reference);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googleplacemap;
    }

    private List<HashMap<String,String>> getallnearbyplace(JSONArray jsonArray)
    {

        int counter = jsonArray.length();
        List<HashMap<String,String>> nearybyplacelist = new ArrayList<>();
        HashMap<String,String> nearbyplacemap = null;

        for(int i=0;i<counter;i++)
        {
            try {
                nearbyplacemap = getsinglenearybyPlace((JSONObject) jsonArray.get(i));
                nearybyplacelist.add(nearbyplacemap);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return nearybyplacelist;
    }



    public List<HashMap<String,String>> parse(String jsondata)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsondata);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getallnearbyplace(jsonArray);

    }
}
