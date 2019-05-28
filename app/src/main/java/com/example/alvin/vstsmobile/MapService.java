package com.example.alvin.vstsmobile;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapService extends Service {
    public static final int NOTIFICATION_ID = 2020;

    private ArrayList<AbnormalAverageSpeeds> vehicleAbnormalSpeeds;
    private ArrayList<Integer> trackedLfs;
    private ArrayList<Locations> foundLocations;
    private Location mLastKnownLocation;
    private GoogleMap mMap;
    /*
    public MapService() {
        super("MapService");
    }*/

    private IBinder binder = new MapBinder();



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        trackedLfs =  intent.getIntegerArrayListExtra("tracked");
        mLastKnownLocation.setLatitude(intent.getDoubleExtra("latitude",0.00));
        mLastKnownLocation.setLongitude(intent.getDoubleExtra("longitude",0.00));

        Bundle bundle = intent.getBundleExtra("bundle");
        vehicleAbnormalSpeeds = (ArrayList<AbnormalAverageSpeeds>)bundle.getSerializable("vehicleAbnormalSpeeds");
        foundLocations = (ArrayList<Locations>)bundle.getSerializable("locations");
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        foundLocations = new ArrayList<>();
        vehicleAbnormalSpeeds = new ArrayList<>();
        trackedLfs = new ArrayList<>();

        executeTask();
    }

    private void executeTask(){
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                //notifyPolice();
                if (mLastKnownLocation.getLongitude() != 0){
                    getLocations();
                    notifyPolice(mLastKnownLocation.getLatitude());
                }

                handler.postDelayed(this,8000);
            }
        });
    }



    private void notifyPolice(Double lat){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.sym_def_app_icon)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentTitle("VSTS")
                        .setContentText("Location latitude is at "+lat)
                        .setVibrate(new long[] {0,1000})
                        .setAutoCancel(true);

        Intent actionIntent = new Intent(this, MainActivity.class);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(
                this,
                0,
                actionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(actionPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID,builder.build());
    }


    public class MapBinder extends Binder{
        MapService getMapService(){
            return MapService.this;
        }
    }



    public void getLocations(){
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
                                for(Locations location : foundLocations){
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
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }

                        //  Toast.makeText(getContext(),"hello",Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
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

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    private void updateMarkers(Locations oldLocation,Locations newLocation, boolean isNew){

        if (isNew){
            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(newLocation.getLatitude(),newLocation.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons_bus_blue)));
            marker.setTag(foundLocations.size());
            newLocation.setMarker(marker);


            foundLocations.add(newLocation);
        }else {

            int index = foundLocations.indexOf(oldLocation);
            moveVehicle(oldLocation.getMarker(),newLocation,index);
        }
        /*--If it has exceeded limit, track it */
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
                        notifyPolice(oldLocation.getLatitude());

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
                foundLocations.get(index).setMarker(myMarker);
            }
        });


    }


    public ArrayList<AbnormalAverageSpeeds> getVehicleAbnormalSpeeds() {
        return vehicleAbnormalSpeeds;
    }

    public ArrayList<Integer> getTrackedLfs() {
        return trackedLfs;
    }

    public ArrayList<Locations> getFoundLocations() {
        return foundLocations;
    }

    public Location getmLastKnownLocation() {
        return mLastKnownLocation;
    }
}
