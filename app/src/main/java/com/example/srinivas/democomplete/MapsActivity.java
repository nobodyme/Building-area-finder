package com.example.srinivas.democomplete;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import static android.support.design.widget.FloatingActionButton.SIZE_MINI;
import static android.support.design.widget.FloatingActionButton.SIZE_NORMAL;
import static com.google.maps.android.SphericalUtil.computeArea;

public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker0;
    private Button autoCompleteButton;
    private Button solarsitecalcButton;

    //j keeps count of the number of the markers
    static int j = -1;
    private double[] distanceArray = new double[200];
    private Polyline polyline;
    private PolylineOptions polylineOptions;
    private ArrayList<LatLng> coodlist = new ArrayList<LatLng>();
    private ArrayList<Double> computedArea = new ArrayList<Double>();
    private float[] dis = new float[1];
    private PlaceAutocompleteFragment autocompleteFragment;
    private static int current_initial_marker_value = 0;
    private ArrayList<Integer> end_marker_value_array = new ArrayList<Integer>();
    private FloatingActionButton addareafab;
    private FloatingActionButton minusareafab;
    private int firstmarkerno = -1;
    private int lastmarkerno = -1;
    private boolean areacalcflag = false;
    private double temp = 0;
    private double temp1 = 0;
    private int addorsubflag = 1;


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
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getApplicationContext()));


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


        //addareafab

        addareafab = (FloatingActionButton) findViewById(R.id.fabadd);
        addareafab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //flag for indicating that area is under change
                areacalcflag = false;

                if (j == -1)
                    Toast.makeText(MapsActivity.this, "First calculate area to add", Toast.LENGTH_SHORT).show();

                else {

                    if (!autoCompleteButton.getText().equals("Auto Complete")) {
                        autoCompleteButton.setText("Auto Complete");
                    }

                    //check if minus area is active while this button is pressed
                    if (minusareafab.getSize() == minusareafab.SIZE_NORMAL)
                        Toast.makeText(MapsActivity.this, "Minus Area is active now", Toast.LENGTH_SHORT).show();

                        //if button is inactive (and minus is also inactive as previously checked) make the current one active
                    else if (addareafab.getSize() == addareafab.SIZE_MINI) {

                        //stores marker value as soon as add/sub button is pressed
                        firstmarkerno = j;

                        addareafab.setSize(SIZE_NORMAL);

                        //stores first value of marker of a new group
                        current_initial_marker_value = j + 1;
                    } else if (addareafab.getSize() == addareafab.SIZE_NORMAL) {

                        //just a variable to check is no marker is placed between making the button active and inactive
                        lastmarkerno = j;
                        if (firstmarkerno == lastmarkerno)
                            addareafab.setSize(SIZE_MINI);
                        else
                            Toast.makeText(MapsActivity.this, "Add area is progress, delete items to cancel", Toast.LENGTH_SHORT).show();


                    }
                }

            }
        });


        //minusareafab

        minusareafab = (FloatingActionButton) findViewById(R.id.fabminus);
        minusareafab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //flag for indicating that area is under change
                areacalcflag = false;


                if (j == -1)
                    Toast.makeText(MapsActivity.this, "First calculate area to subtract", Toast.LENGTH_SHORT).show();

                else {

                    if (!autoCompleteButton.getText().equals("Auto Complete")) {
                        autoCompleteButton.setText("Auto Complete");
                    }

                    //check if add area is active while this button is pressed
                    if (addareafab.getSize() == addareafab.SIZE_NORMAL)
                        Toast.makeText(MapsActivity.this, "Add Area is active now", Toast.LENGTH_SHORT).show();

                    //if button is inactive and add is also inactive as previously checked make the current one active
                    else if (minusareafab.getSize() == minusareafab.SIZE_MINI) {

                        //stores marker value as soon as add/sub button is pressed
                        firstmarkerno = j;

                        minusareafab.setSize(SIZE_NORMAL);

                        //make flag -1 to indicate current option is minus and hence area stored has to be subtracted
                        addorsubflag = -1;

                        //stores first value of marker of a new group
                        current_initial_marker_value = j + 1;

                    } else if (minusareafab.getSize() == minusareafab.SIZE_NORMAL) {

                        //just a variable to check is no marker is placed between making the button active and inactive
                        lastmarkerno = j;

                        if (firstmarkerno == lastmarkerno) {
                            minusareafab.setSize(SIZE_MINI);
                            addorsubflag = 1;
                        } else
                            Toast.makeText(MapsActivity.this, "Minus area is progress, delete items to cancel", Toast.LENGTH_SHORT).show();


                    }
                }
            }
        });


        //Long click listener for placing marker on map
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                //a flag to check if area of a group is already calculated
                if (areacalcflag == true)
                    Toast.makeText(MapsActivity.this, "Delete markers to recompute area or use add/sub buttons for additional computations", Toast.LENGTH_LONG).show();
                else {

                    //areacalcflag = false;


                    ++j;

                    //intialize marker and its attributes
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .visible(true)
                            .draggable(true)
                            .title(String.valueOf(distanceArray[j]) + " m")
                            .snippet("Marker " + (j + 1)));
                    if (j == current_initial_marker_value)
                        marker0 = marker;

                    marker.setDraggable(false);

                    //gets markerposition and stores in array of coords
                    coodlist.add(marker.getPosition());

                    //check if no of points is greater than 1 to find distance
                    if (j > current_initial_marker_value) {

                        //calculate distance between current point and previous point
                        Location.distanceBetween((coodlist.get(j - 1)).latitude, (coodlist.get(j - 1)).longitude, (coodlist.get(j)).latitude, (coodlist.get(j)).longitude, dis);
                        //keep distance in distance array
                        distanceArray[j] = round(dis[0], 2);

                        marker.setTitle(String.valueOf(distanceArray[j]) + " m");

                        //connect line between the two points
                        polylineOptions = new PolylineOptions().add(new LatLng((coodlist.get(j - 1)).latitude, (coodlist.get(j - 1)).longitude)).add(new
                                LatLng((coodlist.get(j)).latitude, (coodlist.get(j)).longitude));
                        polyline = mMap.addPolyline(polylineOptions.color(Color.BLUE).clickable(true));
                    }
                }


            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {

            @Override
            public void onPolylineClick(Polyline polyline) {

                if (!autoCompleteButton.getText().equals("Auto Complete")) {
                    //if theres more than one computed area, remove the last one, and show the previous computed area
                    if (computedArea.size() > 1) {
                        computedArea.remove(computedArea.size() - 1);
                        autoCompleteButton.setText(computedArea.get(computedArea.size() - 1) + " sq ft");
                        //autoCompleteButton.append(cs);
                    } else
                        autoCompleteButton.setText("Auto Complete");
                }

                polyline.remove();
            }
        });


        autoCompleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //check if minimun of 3 markers are present to compute area
                if (j > 1 && j - firstmarkerno > 2) {

                    //set true because autocomplete computes area of the group
                    areacalcflag = true;

                    //if there are more than one group, check if marker of last group is equal to the current one, hence to indicate the autocomplete is unknowingly pressed and no need to add marker value to group of new markers
                    if (end_marker_value_array.size() >= 1) {

                        if (end_marker_value_array.get(end_marker_value_array.size() - 1) == current_initial_marker_value)
                            Toast.makeText(MapsActivity.this, "No changes made to the area", Toast.LENGTH_SHORT).show();
                    } else
                        //if last added marker and current is different add the current one
                        end_marker_value_array.add(current_initial_marker_value);


                    polylineOptions = new PolylineOptions().add(new LatLng((coodlist.get(j)).latitude, (coodlist.get(j)).longitude)).add(new LatLng((coodlist.get(current_initial_marker_value)).latitude, (coodlist.get(current_initial_marker_value)).longitude));
                    polyline = mMap.addPolyline(polylineOptions.color(Color.BLUE).clickable(true));

                    Location.distanceBetween((coodlist.get(current_initial_marker_value)).latitude, (coodlist.get(current_initial_marker_value)).longitude, (coodlist.get(j)).latitude, (coodlist.get(j)).longitude, dis);

                    distanceArray[j] = round(dis[0], 2);
                    //Toast.makeText(MapsActivity.this,""+dis[0],Toast.LENGTH_SHORT).show();

                    marker0.setTitle(String.valueOf(distanceArray[j]) + " m");

                    //converting square meter to square foot and rounding it off to 2 decimal places
                    //converting factor = 10.7639
                    temp = round(computeArea(coodlist) * 10.7639, 2);
                    //if only one computed area exists, show  it
                    if (end_marker_value_array.size() == 1)
                        computedArea.add(temp);
                    //add all stored areas and show it by adding it with the current one
                    else {
                        for (int i = 0; i < computedArea.size(); i++) {
                            temp1 = temp1 + computedArea.get(i) * addorsubflag;
                        }

                        temp1 = temp1 + temp;
                        computedArea.add(temp1);
                    }


                    if (autoCompleteButton.getText().equals("Auto Complete")) {
                        if (computedArea.size() > 1) {
                            autoCompleteButton.setText(computedArea.get(computedArea.size() - 1) + " sq ft");
                           // autoCompleteButton.append(cs);
                        }
                        else {
                            autoCompleteButton.setText(computedArea.get(0) + " sq ft");
                            //autoCompleteButton.append(cs);
                        }
                    }
                } else {
                    Toast.makeText(MapsActivity.this, "Need atleast three points", Toast.LENGTH_LONG).show();
                }

                addareafab.setSize(SIZE_MINI);
                minusareafab.setSize(SIZE_MINI);
            }
        });


        //remove markers
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                //if current stored value of a marker is of new group that doesnt exist remove it from the array
                if (current_initial_marker_value > j) {
                    if (end_marker_value_array.size() > 1)
                        end_marker_value_array.remove(end_marker_value_array.size() - 1);
                    //set current marker value to last stored value
                    current_initial_marker_value = end_marker_value_array.get(end_marker_value_array.size() - 1);
                    firstmarkerno = current_initial_marker_value - 1;
                }


                //remove marker if its only of the new group ie greater than or the last stored marker value
                if (coodlist.indexOf(marker.getPosition()) >= current_initial_marker_value) {
                    //remove coordinates of the marker
                    coodlist.remove(marker.getPosition());
                    //remove marker
                    marker.remove();
                   //decrement total marker number
                    --j;

                } else {
                    Toast.makeText(MapsActivity.this, "Delete newly created segments before this ", Toast.LENGTH_SHORT).show();
                }

                if (!autoCompleteButton.getText().equals("Auto Complete")) {
                    if (computedArea.size() > 1) {
                        computedArea.remove(computedArea.size() - 1);
                        autoCompleteButton.setText(computedArea.get(computedArea.size() - 1) + " sq ft");
                        //autoCompleteButton.append(cs);
                    } else
                        autoCompleteButton.setText("Auto Complete");
                }
            }
        });

        //move to solar activity for calculations
        solarsitecalcButton = (Button) findViewById(R.id.bsolarpanel);
        solarsitecalcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://indiagoessolar.com/solar-calculator/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (computedArea.size() > 1) {

                    startActivity(intent);
                } else if (computedArea.size() == 1) {
                    startActivity(intent);
                } else
                    Toast.makeText(MapsActivity.this, "Calculate area to find solar panel details", Toast.LENGTH_SHORT).show();
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
                    current_initial_marker_value = 0;

                    firstmarkerno = -1;
                    lastmarkerno = -1;

                    end_marker_value_array.clear();


                    computedArea.clear();

                    areacalcflag = false;


                    addareafab.setSize(SIZE_MINI);
                    minusareafab.setSize(SIZE_MINI);

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


}
