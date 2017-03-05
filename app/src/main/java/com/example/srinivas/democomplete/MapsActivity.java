package com.example.srinivas.democomplete;

import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import static com.google.maps.android.SphericalUtil.computeArea;

public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker0;
    private Button autoCompleteButton;

    //j keeps count of the number of the markers
    static int j = -1;
    private double[] distanceArray = new double[200];
    private Polyline polyline;
    private PolylineOptions polylineOptions;
    private ArrayList<LatLng> coodlist = new ArrayList<LatLng>();
    private double computedArea;
    private float[] dis = new float[1];
    private PlaceAutocompleteFragment autocompleteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

         autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        //Button creation for autocomplete and find area
        autoCompleteButton = (Button) findViewById(R.id.autoComplete);

        //setting up custom info window adapter to map
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                //Log.i("Maps Activity", "Place: ");

//                Marker markerinvisible= mMap.addMarker(new MarkerOptions()
//                .position(place.getLatLng())
//                .visible(true));
                LatLng latlngdummy = place.getLatLng();
                mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(latlngdummy.latitude,latlngdummy.longitude) , 19.0f) );
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                //Log.i("Maps activity", "An error occurred: " + status);
            }
        });


        //Long click listener for placing marker on map
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                ++j;

                //intialize marker and its attributes
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .visible(true)
                        .draggable(true)
                        .title(String.valueOf(distanceArray[j]))
                        .snippet("Marker " + (j + 1)));
                if (j == 0)
                    marker0 = marker;

                marker.setDraggable(false);

                //gets markerposition and stores in array of coords
                coodlist.add(marker.getPosition());

                //check if no of points is greater than 1 to find distance
                if (j > 0) {


                    //calculate distance between current point and previous point
                    Location.distanceBetween((coodlist.get(j - 1)).latitude, (coodlist.get(j - 1)).longitude, (coodlist.get(j)).latitude, (coodlist.get(j)).longitude, dis);
                    //keep distance in distance array
                    distanceArray[j] = dis[0];

                    marker.setTitle(String.valueOf(distanceArray[j]));

                    //connect line between the two points
                    polylineOptions = new PolylineOptions().add(new LatLng((coodlist.get(j - 1)).latitude, (coodlist.get(j - 1)).longitude)).add(new
                            LatLng((coodlist.get(j)).latitude, (coodlist.get(j)).longitude));
                    polyline = mMap.addPolyline(polylineOptions.color(Color.BLUE).clickable(true));
                }

            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {

            @Override
            public void onPolylineClick(Polyline polyline) {

                if (!autoCompleteButton.getText().equals("Auto Complete")) {
                    autoCompleteButton.setText("Auto Complete");
                }

                polyline.remove();
            }
        });


        autoCompleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (j > 1) {
                    polylineOptions = new PolylineOptions().add(new LatLng((coodlist.get(j)).latitude, (coodlist.get(j)).longitude)).add(new LatLng((coodlist.get(0)).latitude, (coodlist.get(0)).longitude));
                    polyline = mMap.addPolyline(polylineOptions.color(Color.BLUE).clickable(true));

                    Location.distanceBetween((coodlist.get(0)).latitude, (coodlist.get(0)).longitude, (coodlist.get(j)).latitude, (coodlist.get(j)).longitude, dis);

                    distanceArray[j] = dis[0];
                    //Toast.makeText(MapsActivity.this,""+dis[0],Toast.LENGTH_SHORT).show();

                    marker0.setTitle(String.valueOf(distanceArray[j]));

                    computedArea = computeArea(coodlist);


                    if (autoCompleteButton.getText().equals("Auto Complete")) {
                        autoCompleteButton.setText("Area = " + computedArea);
                    } else {
                        autoCompleteButton.setText("Auto Complete");
                    }
                } else {
                    Toast.makeText(MapsActivity.this, "Need atleast three points", Toast.LENGTH_LONG).show();
                }
            }
        });


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (!autoCompleteButton.getText().equals("Auto Complete")) {
                    autoCompleteButton.setText("Auto Complete");
                }
                coodlist.remove(marker.getPosition());
                marker.remove();

                --j;
            }
        });


    }

    public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private View view;

        //to display distance from other markers
        private String distance_no;

        //to display snippet from original adpater
        private String custom_snippet;


        public CustomInfoWindowAdapter() {

            view = getLayoutInflater().inflate(R.layout.custom_info_window, null);
        }

        @Override
        public View getInfoContents(Marker marker) {


            return null;
        }


        @Override
        public View getInfoWindow(final Marker marker) {

            distance_no = marker.getTitle();
            custom_snippet = marker.getSnippet();

            //set titles and snippet from original infowindow
            TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
            subtitle.setText(distance_no);


            TextView snippet = (TextView) view.findViewById(R.id.snippet);
            snippet.setText(custom_snippet);


            return view;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.maptypeHYBRID:
                if (mMap != null) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    return true;
                }
            case R.id.maptypeNORMAL:
                if (mMap != null) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    return true;
                }
            case R.id.maptypeSATELLITE:
                if (mMap != null) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    return true;
                }
            case R.id.maptypeTERRAIN:
                if (mMap != null) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    return true;
                }

            case R.id.removeall:
                if (mMap != null) {
                    mMap.clear();
                    coodlist.clear();
                    j = -1;

                    if (!autoCompleteButton.getText().equals("Auto Complete")) {
                        autoCompleteButton.setText("Auto Complete");
                    }

                }


        }
        return super.onOptionsItemSelected(item);

    }
}