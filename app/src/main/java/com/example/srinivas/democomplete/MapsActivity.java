package com.example.srinivas.democomplete;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import static com.google.maps.android.SphericalUtil.computeArea;

public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker0;
    private Button autoCompleteButton;
    private Button solarpanelButton;

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

        //Obtain autocomplete fragment for place suggestion in google maps 
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


        //listener code to move to the place when one is selected from the fragment
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                // get latitude of the selected place
                LatLng latlngdummy = place.getLatLng();
                //move camera position with animation to the chosen place
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latlngdummy.latitude, latlngdummy.longitude), 19.0f));
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(MapsActivity.this, "Unknown error occured, try again later", Toast.LENGTH_SHORT).show();
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

                    //converting square meter to square foot and rounding it off to 2 decimal places
                    //converting factor = 10.7639
                    computedArea = round(computeArea(coodlist)*10.7639,2);


                    if (autoCompleteButton.getText().equals("Auto Complete")) {
                        autoCompleteButton.setText(""+computedArea);
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

        //move to solar activity for calculations
        solarpanelButton = (Button)findViewById(R.id.bsolarpanel);
        solarpanelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(MapsActivity.this,SolarActivity.class);
                bundle.putDouble("computedarea",computedArea);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }


    // simple class for perfect rounding of computed area
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
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
}
