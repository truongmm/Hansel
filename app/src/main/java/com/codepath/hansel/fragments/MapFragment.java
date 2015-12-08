package com.codepath.hansel.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.hansel.R;
import com.codepath.hansel.models.Mapper;
import com.codepath.hansel.models.Pebble;
import com.codepath.hansel.models.User;
import com.codepath.hansel.utils.DatabaseHelper;
import com.codepath.hansel.utils.DrawableHelper;
import com.codepath.hansel.utils.MarkerCallback;
import com.codepath.hansel.utils.TimeHelper;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MapFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        RoutingListener,
        LocationListener {

    private Date earliestDate;
    private Date seekDate;
    private SupportMapFragment mapFragment;
    private SeekBar sbMapRelativeTime;
    private TextView tvMapRelativeTime;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private DatabaseHelper dbHelper;
    private Mapper mapper;
    private Bitmap bPebble;
    private LatLngBounds.Builder boundBuilder;
    private HashMap<Marker, Pebble> markerPebbleMap;
    private ArrayList<Polyline> polylines;
    private ProgressDialog progressDialog;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private int routeIndex = 0;
    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        markerPebbleMap = new HashMap<>();
        polylines = new ArrayList<>();
        dbHelper = DatabaseHelper.getInstance(getContext());
        mapper = Mapper.getInstance();

        buildPebbleMarker();
        fetchData();
    }

    private void buildPebbleMarker() {
        Bitmap b = BitmapFactory.decodeResource(getResources(), android.R.drawable.radiobutton_off_background);
        bPebble = Bitmap.createScaledBitmap(b, b.getWidth(), b.getHeight() * 4 / 5, false);
        bPebble = DrawableHelper.addShadow(bPebble, bPebble.getWidth(), bPebble.getHeight(), Color.BLACK, 3, 1, 3);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map, container, false);
        sbMapRelativeTime = (SeekBar) view.findViewById(R.id.sbMapRelativeTime);
        sbMapRelativeTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekDate = seekDate(seekBar.getProgress());
//                Toast.makeText(getActivity(), String.valueOf(), Toast.LENGTH_LONG).show();
                fetchData();
                reloadMap();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String relativeTime;
                if (progress == 100) {
                    relativeTime = "Now";
                } else {
                    relativeTime = TimeHelper.getShortRelativeTimeAgo(seekDate(progress));
                }
                tvMapRelativeTime.setText(relativeTime);
            }

            public Date seekDate(int progress) {
                Date earliestDate = mapper.getEarliestDate();
                if (earliestDate != null) {
                    long currentTime = System.currentTimeMillis();
                    return new Date(currentTime - (currentTime - earliestDate.getTime()) * (100 - progress) / 100);
                }
                return null;
            }
        });
        tvMapRelativeTime = (TextView) view.findViewById(R.id.tvMapRelativeTime);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        GoogleMap googleMap = mapFragment.getMap();
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setPadding(20, 20, 20, 100);
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            private final View mWindow = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null);
            private final View mContents = getActivity().getLayoutInflater().inflate(R.layout.custom_info_contents, null);

            @Override
            public View getInfoWindow(Marker marker) {
                render(marker, mWindow);
                return mWindow;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker marker) {
                render(marker, mContents);
                return mContents;
            }

            private void render(Marker marker, View view) {
                Pebble pebble = markerPebbleMap.get(marker);
                RoundedImageView ivProfileImage = (RoundedImageView) view.findViewById(R.id.ivProfileImage);

                Picasso.with(getContext())
                        .load(pebble.getUserImageUrl())
                        .placeholder(R.mipmap.ic_default_profile)
                        .into(ivProfileImage, new MarkerCallback(marker));

                int color = Mapper.getInstance().getColorForUser(pebble.getUser());
                ivProfileImage.setBorderColor(getContext().getResources().getColor(color));

                TextView tvName = (TextView) view.findViewById(R.id.tvName);
                tvName.setText(pebble.getUser().getFullName());

                TextView tvGeolocation = (TextView) view.findViewById(R.id.tvGeolocation);
                tvGeolocation.setText(pebble.getCoordinate());

                TextView tvTimestamp = (TextView) view.findViewById(R.id.tvTimestamp);
                tvTimestamp.setText(pebble.getRelativeTimeAgo());
            }
        });
        reloadMap();
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("OptionsMenu", "option 1 selected from frag 1");
        switch (item.getItemId()) {
            case R.id.refresh:
                seekDate = null;
                sbMapRelativeTime.setProgress(100);
                fetchData();
                reloadMap();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchData() {
        List<User> users = new ArrayList<>();
        for (User user : dbHelper.getAllUsers()) {
            ArrayList<Pebble> pebbles = dbHelper.getPebblesForUsersBeforeDate(new User[]{user}, seekDate, false, false);
            if (!pebbles.isEmpty()) {
                user.setPebbles(pebbles);
                users.add(user);
            }
        }
        mapper.setUsers(users);
    }

    private void reloadMap() {
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                    drawRoutes();
                }
            });
        } else {
            // Toast.makeText(getActivity(), "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void drawRoutes() {
        progressDialog = ProgressDialog.show(getActivity(), "Please wait.",
                "Fetching route information.", true);
        boundBuilder = new LatLngBounds.Builder();
        boolean noRoute = true;

        for (User user : mapper.getUsers()) {
            ArrayList<Pebble> pebbles = user.getPebbles();
            for (int i = 0; i < pebbles.size(); i++) {
                Pebble pebble = pebbles.get(i);
                boundBuilder.include(pebble.getLatLng());

                MarkerOptions options = new MarkerOptions();
                options.position(pebble.getLatLng());

                if (i == pebbles.size() - 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(user.getHue()));
                } else {
                    options.icon(BitmapDescriptorFactory.fromBitmap(bPebble));
                }

                Marker marker = map.addMarker(options);
                markerPebbleMap.put(marker, pebble);
            }

            ArrayList<LatLng> latLngs = user.getLatLngs();
            if (latLngs.size() > 1) {
                Routing routing = new Routing.Builder()
                        .travelMode(AbstractRouting.TravelMode.WALKING)
                        .withListener(this)
                        .alternativeRoutes(false)
                        .waypoints(latLngs)
                        .build();
                routing.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                noRoute = false;
            }
        }
        if (noRoute) {
            progressDialog.dismiss();
        }

    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            routeIndex = 0;
            map.clear();

            // Map is ready
            // Toast.makeText(getActivity(), "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            map.setMyLocationEnabled(true);

            // Now that map has loaded, let's get our location!
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();

            connectClient();
        } else {
            // Toast.makeText(getActivity(), "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    protected void connectClient() {
        // Connect the client.
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /*
     * Called when the Activity becomes visible.
    */
    @Override
    public void onStart() {
        super.onStart();
        connectClient();
    }

    /*
     * Called when the Activity is no longer visible.
	 */
    @Override
    public void onStop() {
        // Disconnecting the client invalidates it.
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    /*
     * Handle results returned to the FragmentActivity by Google Play services
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {

            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
            /*
             * If the result code is Activity.RESULT_OK, try to connect again
			 */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mGoogleApiClient.connect();
                        break;
                }

        }
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getActivity().getSupportFragmentManager(), "Location Updates");
            }

            return false;
        }
    }

    /*
     * Called by Location Services when the request to connect the client
     * finishes successfully. At this point, you can request the current
     * location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            // Toast.makeText(getActivity(), "GPS location was found!", Toast.LENGTH_SHORT).show();
//            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
//            map.animateCamera(cameraUpdate);
            startLocationUpdates();
        } else {
            // Toast.makeText(getActivity(), "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
//        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    /*
     * Called by Location Services if the connection to the location client
     * drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            // Toast.makeText(getActivity(), "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            // Toast.makeText(getActivity(), "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Called by Location Services if the attempt to Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            // Toast.makeText(getActivity().getApplicationContext(),
            //        "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRoutingFailure() {
        progressDialog.dismiss();
        // Toast.makeText(getActivity(), "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> routes, int shortestRouteIndex) {
        progressDialog.dismiss();
        if (routes.isEmpty()) {
            return;
        }
//        LatLng start = latLngs.get(0);
//        LatLng end = latLngs.get(latLngs.size() - 1);
//        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
//        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        Route route = routes.get(0);
//        map.moveCamera(center);
        User user = mapper.getBestUserForRoute(route);
        user.setRoute(route);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(boundBuilder.build(), 100);
        map.animateCamera(cameraUpdate);

//        if(polylines.size()>0) {
//            for (Polyline poly : polylines) {
//                poly.remove();
//            }
//        }

        polylines = new ArrayList<>();
        //add route(s) to the map.

        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(getResources().getColor(user.getColor()));
        polyOptions.width(10);
        polyOptions.addAll(route.getPoints());
        Polyline polyline = map.addPolyline(polyOptions);
        polylines.add(polyline);

//            Toast.makeText(getActivity().getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();

        routeIndex++;
//        // Start marker
//        MarkerOptions options = new MarkerOptions();
//        options.position(start);
////        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
//        map.addMarker(options);
//
//        // End marker
//        options = new MarkerOptions();
//        options.position(end);
////        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
//        map.addMarker(options);
    }

    @Override
    public void onRoutingCancelled() {
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
}
