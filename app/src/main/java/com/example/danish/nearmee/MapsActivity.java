package com.example.danish.nearmee;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener

{

    private double latitue,longitude;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentUserlocationMarker;
    private static final int Request_User_Location_Code = 99;
    private int proximityRaduis = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkuserlocationpermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public void onClick(View v)
    {
        String hospital = "hospital",
                school = "school",
                resturant = "resturant";


        Object transferdata[] = new Object[2];
        GetNearByPlaces getNearByPlaces = new GetNearByPlaces();


        switch (v.getId())
        {
            case R.id.dd:
                EditText AddressField = (EditText)findViewById(R.id.locationsearch);
                String address = AddressField.getText().toString();

                List<Address> addressList = null;
                MarkerOptions usermarkeroption = new MarkerOptions();

                if(!TextUtils.isEmpty(address))
                {
                    Geocoder geocoder = new Geocoder(this);
                    try {
                        addressList = geocoder.getFromLocationName(address,6);
                   if (addressList != null)
                   {
                       for (int i=0; i<addressList.size(); i++)
                       {
                           Address useraddress = addressList.get(i);
                           LatLng latLng = new LatLng(useraddress.getLatitude(),useraddress.getLongitude());
                           usermarkeroption.position(latLng);
                           usermarkeroption.title(address);
                           usermarkeroption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                           mMap.addMarker(usermarkeroption);
                           mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                           mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                       }
                   }
                   else
                   {
                       Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                   }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(this, "Enter a address first", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.hospital_nearby:
                mMap.clear();
                String url = geturl(latitue,longitude,hospital);
                transferdata[0] = mMap;
                transferdata[1] = url;

                getNearByPlaces.execute(transferdata);
                Toast.makeText(this, "Searching for nearby hospital...", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Showing nearyby Hospitals", Toast.LENGTH_SHORT).show();
                break;


            case R.id.school_nearby:
                mMap.clear();
                String url = geturl(latitue,longitude,school);
                transferdata[0] = mMap;
                transferdata[1] = url;

                getNearByPlaces.execute(transferdata);
                Toast.makeText(this, "Searching for nearby schools...", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Showing nearyby schools", Toast.LENGTH_SHORT).show();
                break;


            case R.id.resturants_nearby:
                mMap.clear();
                String url = geturl(latitue,longitude,resturant);
                transferdata[0] = mMap;
                transferdata[1] = url;

                getNearByPlaces.execute(transferdata);
                Toast.makeText(this, "Searching for nearby resturant...", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Showing nearyby resturant", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private String geturl(double latitue, double longitude, String nearbyplace) {
        StringBuilder googleurl = new StringBuilder("https://maps.googleapis.com/maps/api/place/findplacefromtext/output?parameters/json?");
        googleurl.append("location" + latitue + "," + longitude);
        googleurl.append("&radius" + proximityRaduis);
        googleurl.append("&type"+ nearbyplace);
        googleurl.append("&sensor=true");
        googleurl.append("&key="+ );



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {

            buildGoogleApiclient();


            mMap.setMyLocationEnabled(true);
        }

    }

    public boolean checkuserlocationpermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            return false;
        }
        else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case Request_User_Location_Code:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if(googleApiClient == null)
                        {
                            buildGoogleApiclient();

                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    protected synchronized void buildGoogleApiclient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        lastlocation = location;
        if(currentUserlocationMarker!=null)
        {
            currentUserlocationMarker.remove();

        }
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("You are currently here");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentUserlocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(13));

        if(googleApiClient!=null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);

        }

    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
        }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
