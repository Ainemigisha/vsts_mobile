package com.example.alvin.vstsmobile;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.alvin.vstsmobile.MapService.NOTIFICATION_ID;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =100;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    public static final int NOTIFICATION_ID = 2020;

    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng mDefaultLocation = new LatLng(0.330401, 32.574059);
    private float DEFAULT_ZOOM = 2.0f;



    private ArrayList<AbnormalAverageSpeeds> vehicleAbnormalSpeeds;
    private ArrayList<Integer> trackedLfs;
    private ArrayList<Locations> locations;

    private MapService mapService;
    private boolean bound = false;
    private Intent intent;

    public MapFragment() {
        // Required empty public constructor
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MapService.MapBinder mapBinder = (MapService.MapBinder) service;

            mapService = mapBinder.getMapService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("Pausing");
        /*intent = new Intent(getContext(), MapService.class);
        intent.putExtra("tracked", trackedLfs);
        intent.putExtra("latitude",mLastKnownLocation.getLatitude());
        intent.putExtra("longitude",mLastKnownLocation.getLongitude());

        Bundle args = new Bundle();
        args.putSerializable("vehicleAbnormalSpeeds",vehicleAbnormalSpeeds);
        args.putSerializable("locations",locations);
        intent.putExtra("bundle",args);
        getActivity().bindService(intent,connection,Context.BIND_AUTO_CREATE);
        */
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*locations = mapService.getFoundLocations();
        trackedLfs = mapService.getTrackedLfs();
        vehicleAbnormalSpeeds = mapService.getVehicleAbnormalSpeeds();

        getActivity().unbindService(connection);
        bound = false;*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        locations = new ArrayList<>();
        vehicleAbnormalSpeeds = new ArrayList<>();
        trackedLfs = new ArrayList<>();
       // if (bound){

       // }
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("Stopping");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Destroying");
        if (bound){
            getActivity().unbindService(connection);
            getActivity().stopService(intent);
            bound = false;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }



    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);

            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        final List<PatternItem> dashItems = new ArrayList<>();
        dashItems.add(new Dot());
        dashItems.add(new Gap(10));

        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());


                            updateLocations();



                            mMap.addCircle(new CircleOptions()
                                    .center(latLng)
                                    .radius(2000)
                                    .strokePattern(dashItems)
                                    .strokeColor(Color.RED));
                            //.fillColor(Color.BLUE));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));

                        } else {
                            //Log.d(TAG, "Current location is null. Using defaults.");
                            //Log.e(TAG, "Exception: %s", task.getException());
                            //mMap.addMarker(new MarkerOptions().position(mDefaultLocation).title("Marker in Makerere"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
        mMap.setOnMarkerClickListener(this);
    }

    private void getLocations(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.MAP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //JSONObject jsonObject = null;
                        try {
                            //jsonObject = new JSONObject(response);

                            JSONArray jsonArray = new JSONArray(response);
                          //  if (!jsonArray.equals(null)){
                                for (int i = 0; i<jsonArray.length(); i++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Locations newLocation = new Locations(jsonObject.getInt("id"),jsonObject.getInt("location_finder_id"),jsonObject.getDouble("speed"),jsonObject.getDouble("latitude")
                                            ,jsonObject.getDouble("longitude"),jsonObject.getJSONObject("location_finder").getJSONObject("bus").getString("number_plate"),
                                            jsonObject.getJSONObject("location_finder").getJSONObject("bus").getJSONObject("bus_company").getString("company_name"),
                                            jsonObject.getJSONObject("location_finder").getJSONObject("bus").getString("driver_name"),jsonObject.getJSONObject("location_finder").getInt("flag"));

                                    boolean locationFound = false;
                                    for(Locations location : locations){
                                        if (location.getLf_id() == newLocation.getLf_id()){
                                            if (location.getLatitude() != newLocation.getLatitude() || location.getLongitude() != newLocation.getLongitude())
                                                updateMarkers(location,newLocation,false);
                                            locationFound = true;
                                        }
                                    }
                                    if (!locationFound){
                                        updateMarkers(null,newLocation,true);
                                    }



                                }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }

                      //  Toast.makeText(getContext(),"hello",Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("latitude",""+mLastKnownLocation.getLatitude());
                params.put("longitude",""+mLastKnownLocation.getLongitude());
                return params;
            }
        };

        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        Locations bus_location = locations.get((int)marker.getTag());
        notifyPolice(bus_location);
        /*Intent intent = new Intent(getContext(),MapService.class);
        getActivity().startService(intent);*/

        Bundle bundle = new Bundle();
        bundle.putSerializable("location",bus_location);

        Fragment fragment = new BusProfileFragment();
        fragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame,fragment);
        ft.addToBackStack(null);
        ft.commit();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getLocationPermission();

        updateLocationUI();

        getDeviceLocation();

    }


    private void updateLocations(){
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                getLocations();
                handler.postDelayed(this,1000);
            }
        });
    }



    private void updateMarkers(Locations oldLocation,Locations newLocation, boolean isNew){



        if (isNew){

            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(newLocation.getLatitude(),newLocation.getLongitude())));

            if (newLocation.getFlag() == 0)
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_bus_30_blue));
            else
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_bus_30_red));

            marker.setTag(locations.size());
            newLocation.setMarker(marker);


            locations.add(newLocation);
        }else {

            int index = locations.indexOf(oldLocation);
            if (newLocation.getFlag() != oldLocation.getFlag()){
                if (newLocation.getFlag() == 1){
                    locations.get(index).setFlag(1);
                    locations.get(index).getMarker().setIcon((BitmapDescriptorFactory.fromResource(R.drawable.icons8_bus_30_red)));
                    if(trackedLfs.indexOf(newLocation.getLf_id()) < 0){
                        notifyPolice(newLocation);
                        trackedLfs.add(newLocation.getLf_id());
                    }

                }else {
                    locations.get(index).setFlag(0);
                    locations.get(index).getMarker().setIcon((BitmapDescriptorFactory.fromResource(R.drawable.icons8_bus_30_blue)));
                    if (trackedLfs.indexOf(newLocation.getLf_id()) > 0){
                        trackedLfs.remove(newLocation.getLf_id());
                    }

                }
            }

            moveVehicle(oldLocation.getMarker(),newLocation,index);
        }
        /*--If it has exceeded limit, track it
        if (newLocation.getSpeed() > 30 && trackedLfs.indexOf(newLocation.getLf_id()) < 0){
            trackedLfs.add(newLocation.getLf_id());
        }


        if (trackedLfs.indexOf(newLocation.getLf_id()) > -1){  //if it is being tracked
            boolean isExisting = false;
            for (AbnormalAverageSpeeds abnormalAverageSpeed : vehicleAbnormalSpeeds){
                if (abnormalAverageSpeed.getLf_id() == newLocation.getLf_id()){   //if the lf has some speeds that are already there just add
                    abnormalAverageSpeed.addAbnormalSpeed(newLocation);
                    isExisting = true;

                    if (abnormalAverageSpeed.isOverSpeeding()){ //if the lf is overspeeding do somen
                        changeColor(oldLocation.getMarker(),"red");
                    }else if(abnormalAverageSpeed.getAv_speed() < 30){ //if proven not to speed, clear flag
                        changeColor(oldLocation.getMarker(),"blue");
                        vehicleAbnormalSpeeds.remove(abnormalAverageSpeed);
                        trackedLfs.remove(newLocation.getLf_id());
                    }
                }

            }

            if (isExisting == false) { //add the first speed
                AbnormalAverageSpeeds newAbnormalAvSpeedLocation = new AbnormalAverageSpeeds(newLocation.getLf_id());
                newAbnormalAvSpeedLocation.addAbnormalSpeed(newLocation);
                vehicleAbnormalSpeeds.add(newAbnormalAvSpeedLocation);

            }
        }

        */
    }

    private void changeColor(final Marker myMarker,String color){
        if (color.equals("blue"))
            myMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icons_bus_blue));
        else
            myMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icons_bus_red));

    }




    private void moveVehicle(final Marker myMarker, final Locations finalPosition, final int index) {


        //final Marker myMarker = location.getMarker();
        final LatLng startPosition = myMarker.getPosition();

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 2000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + (finalPosition.getLatitude()) * t,
                        startPosition.longitude * (1 - t) + (finalPosition.getLongitude()) * t);
                myMarker.setPosition(currentPosition);
                // myMarker.setRotation(finalPosition.getBearing());


                // Repeat till progress is completeelse
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                    // handler.postDelayed(this, 100);
                } else {
                    if (hideMarker) {
                        myMarker.setVisible(false);
                    } else {
                        myMarker.setVisible(true);
                    }
                }
               locations.get(index).setMarker(myMarker);
            }
        });


    }

    private void notifyPolice(Locations locations){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getContext())
                        .setSmallIcon(android.R.drawable.sym_def_app_icon)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentTitle("VSTS")
                        .setContentText("Vehicle "+locations.getNumber_plate()+" over speeding at "+getRoadName(locations.getLatitude(),locations.getLongitude()))
                        .setVibrate(new long[] {0,1000})
                        .setAutoCancel(true);

        Intent actionIntent = new Intent(getActivity(), MainActivity.class);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(
                getContext(),
                0,
                actionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(actionPendingIntent);

        NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID,builder.build());
    }

    private String getRoadName(Double latitude, Double longitude){
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address obj = addresses.get(0);

            return obj.getThoroughfare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


}
