package nl.gleb.runmissions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;
import java.util.List;


public class Main extends ActionBarActivity
        implements Comm,
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    /**
     * Constants
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final long UPDATE_INTERVAL = 10000;
    private static final long FASTEST_INTERVAL = 5000;
    private static final int FEEDBACK_RANGE = 50;
    private static final int FINISH_RANGE = 25;

    /**
     * Haptic feedback patterns
     */
    static final long[] LEFT_PATTERN = {0, 300, 150, 100};
    static final long[] RIGHT_PATTERN = {0, 100, 150, 300};
    static final long[] ACCELERATE_PATTERN = {0, 100, 100, 100, 100, 100};
    static final long[] ERROR_PATTERN = {0, 300, 150, 200, 300, 300, 150, 200, 300, 300, 150, 200, 300, 300, 150, 200, 300, 300, 150, 200, 300};
    static final long[] CLOSER_PATTERN = {0, 100, 1000, 100, 900, 150, 800, 150, 700, 200, 600, 200, 500, 250, 400, 250, 300, 300, 200, 300, 100, 3000};

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    // Drawer & title
    private DrawerLayout drawer;
    private CharSequence mTitle;

    // Location
    LocationRequest mLocationRequest;
    LocationClient mLocationClient;
    Location mCurrentLocation;
    boolean mUpdatesRequested;

    // Firebase
    private Firebase ref;
    static Firebase userRef;
    static Firebase usersRef;
    static Firebase chatRef;
    private AuthData authData;
    ProgressDialog mAuthProgressDialog;

    // Profile
    User user = null;

    // Checkpoints
    Place target;
    static PlacesList places;
    List<DirectionsStep> steps = new ArrayList<DirectionsStep>();
    int feedbackCounter = 0;

    // Chat
    private ChatListAdapter chatListAdapter;
    private ValueEventListener connectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Hide the action bar
        getSupportActionBar().hide();

        // Enable strict mode (for the network connection)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getString(R.string.progress_dialog_title));
        mAuthProgressDialog.setMessage(getString(R.string.progress_dialog_message));
        mAuthProgressDialog.setCancelable(false);

        Firebase.setAndroidContext(getApplicationContext());
        ref = new Firebase(getString(R.string.firebase_ref));
        chatRef = ref.child("chat");
        usersRef = ref.child("users");

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        /* Check if the user is authenticated with Firebase already.
         * If this is the case we can set the authenticated
         * user and hide hide any login buttons */
        ref.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                mAuthProgressDialog.hide();
                setAuthenticatedUser(authData);
                if (authData != null) {
                    getProfile(authData.getProviderData().get("email").toString());
                }
            }
        });

        if (authData == null) {
            openLoginFragment();
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setScrimColor(Color.parseColor(getString(R.string.nav_drawer_background)));
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, drawer);

        mLocationClient = new LocationClient(this, this, this);
        mUpdatesRequested = false;

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (servicesConnected() && !mLocationClient.isConnected()) {
            mLocationClient.connect();
        }

        // Finally, a little indication of connection status
        connectedListener = chatRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Log.d("MAIN", "Connected to Firebase (chat service)");
                } else {
                    Log.d("MAIN", "Disconnected from Firebase (chat service)");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("MAIN", "Firebase chat listener disconnected");
            }
        });

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if ( user != null ) {
                    Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                    for (DataSnapshot u : users ) {
                        if ( !user.getUsername().equals(u.child("username").getValue()) ){
                            updateUsersLocation(u);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("MAIN", "Firebase users listener disconnected");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthProgressDialog != null) {
            mAuthProgressDialog.dismiss();
        }
        ref.child("chat").getRoot().child(".info/connected").removeEventListener(connectedListener);
    }

    @Override
    protected void onDestroy() {
        mLocationClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        switch (position) {
            case 0:
                ft.replace(R.id.container, Profile.newInstance(), getString(R.string.title_profile)).commit();
                break;
            case 1:
                ft.replace(R.id.container, Map.newInstance(), getString(R.string.title_map)).commit();
                break;
            case 2:
                ft.replace(R.id.container, Chat.newInstance(user.getUsername())).commit();
                break;
            case 3:
                ft.replace(R.id.container, MissionList.newInstance()).commit();
                break;
            case 4:
                ft.replace(R.id.container, Instructions.newInstance()).commit();
                break;
            case 5:
                ft.replace(R.id.container, Settings.newInstance()).commit();
                break;
            case 6:
                logout();
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();

        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                ft.replace(R.id.container, Settings.newInstance()).commit();
                break;
            case R.id.action_logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
                // If the result code is Activity.RESULT_OK
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // Try the request again
                        break;
                }
        }
    }

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason.
            // resultCode holds the error code.
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialog errorFragment =
                        new ErrorDialog();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(getSupportFragmentManager(),
                        "Location Updates");
            }
        }

        return false;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mCurrentLocation = mLocationClient.getLastLocation();
        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    @Override
    public void onDisconnected() {
        Log.d("Location Updates", "Disconnected. No location :(");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    connectionResult.getErrorCode(),
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialog errorFragment = new ErrorDialog();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(getSupportFragmentManager(),
                        "Location Updates");
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        if (user != null) {
            user.setLocation(location);
            userRef.child("lat").setValue(location.getLatitude());
            userRef.child("lng").setValue(location.getLongitude());
        }

        if (!steps.isEmpty() && target != null) {
            for (DirectionsStep step : steps) {
                double dist = distance(location.getLatitude(), location.getLongitude(), step.end_location.lat, step.end_location.lng);
                if (dist <= FEEDBACK_RANGE) {
                    handleFeedback(step);
                }
            }
            double dist = distance(location.getLatitude(), location.getLongitude(), target.geometry.location.lat, target.geometry.location.lng);
            if (dist <= FINISH_RANGE) {
                handleFinish(target);
            }
        }
    }

    /**
     * Provide haptic feedback for the current state based on the html instructions
     *
     * @param step current route step
     */
    private void handleFeedback(DirectionsStep step) {

        if (feedbackCounter == 0) {

            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            if (step.html_instructions.toLowerCase().contains("left")) {
                vibrator.vibrate(LEFT_PATTERN, -1);
            } else if (step.html_instructions.toLowerCase().contains("right")) {
                vibrator.vibrate(RIGHT_PATTERN, -1);
            } else if (step.html_instructions.toLowerCase().contains("head")) {
                vibrator.vibrate(ACCELERATE_PATTERN, -1);
            } else if (step.html_instructions.toLowerCase().contains("Continue")) {
                vibrator.vibrate(CLOSER_PATTERN, -1);
            }

            feedbackCounter = 3;
        }

        feedbackCounter--;
    }

    /**
     * User is at the finish, give 'em some points
     *
     * @param target
     */
    private void handleFinish(Place target) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(5000);
        Toast.makeText(this, "Congratz! +10 pts", Toast.LENGTH_LONG).show();

        userRef.child("exp").setValue(user.getExp() + 10);
    }

    /**
     * Update other users location on the map
     * @param ds DataSnapshotnof changed user model
     */
    private void updateUsersLocation(DataSnapshot ds) {
        try {
            User user = new User(ds.child("email").getValue().toString(),
                    ds.child("username").getValue().toString(),
                    Integer.parseInt(ds.child("level").getValue().toString()),
                    Integer.parseInt(ds.child("exp").getValue().toString()),
                    Integer.parseInt(ds.child("missions").getValue().toString()),
                    Double.parseDouble(ds.child("lat").getValue().toString()),
                    Double.parseDouble(ds.child("lng").getValue().toString()));

            Map map = (Map) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_map));
            if (map != null) {
                map.updateUsersPosition(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setTitle(String data) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(data);
    }

    @Override
    public void openSignup() {
        lockDrawer();

        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, Signup.newInstance()).commit();
    }

    @Override
    public void handleSignup(final String email, final String password, final String username) {
        ref.createUser(email, password, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                // user was created
                createUser(email, password, username);
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                // there was an error
                new AlertDialog.Builder(Main.this)
                        .setTitle("Errrrrr")
                        .setMessage(firebaseError.getMessage())
                        .setPositiveButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    /*
     * Create initial user profile, use email as key
     */
    public void createUser(String email, String password, String username) {
        User newUser = new User(email, username, mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        Firebase usersRef = ref.child("users");
        usersRef.child(email.replaceAll("[^A-Za-z0-9]", "-")).setValue(newUser);

        handleLogin(email, password);
    }

    /**
     * Lock the nav drawer
     */
    public void lockDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    /**
     * Unlock the nav drawer
     */
    public void unlockDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void sendMessage(String message) {
        // Create our 'model', a Chat object
        ChatMessage chat = new ChatMessage(message, user.getUsername());

        // Create a new, auto-generated child of that chat location, and save our chat data there
        chatRef.push().setValue(chat);
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void updatePlaces(PlacesList places) {
        this.places = places;
    }

    @Override
    public void updateSteps(List<DirectionsStep> steps) {
        this.steps = steps;
    }

    @Override
    public long[] getPattern(String type) {
        if (type.equals("left")) {
            return LEFT_PATTERN;
        } else if (type.equals("right")) {
            return RIGHT_PATTERN;
        } else if (type.equals("accelerate")) {
            return ACCELERATE_PATTERN;
        } else if (type.equals("error")) {
            return ERROR_PATTERN;
        } else if (type.equals("closer")) {
            return CLOSER_PATTERN;
        } else {
            return ACCELERATE_PATTERN;
        }
    }

    @Override
    public void setTarget(Place target) {
        this.target = target;
    }

    /**
     * Login the user, method invoked from the login fragment or after signup
     *
     * @param email
     * @param password
     */
    @Override
    public void handleLogin(String email, String password) {
        mAuthProgressDialog.show();
        ref.authWithPassword(email, password, new AuthResultHandler());
    }

    /*
     * Get user profile data such as username, level etc. (after the login)
     */
    private void getProfile(String email) {
        userRef = ref.child("users/" + email.replaceAll("[^A-Za-z0-9]", "-"));
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    user = new User(dataSnapshot.child("email").getValue().toString(),
                            dataSnapshot.child("username").getValue().toString(),
                            Integer.parseInt(dataSnapshot.child("level").getValue().toString()),
                            Integer.parseInt(dataSnapshot.child("exp").getValue().toString()),
                            Integer.parseInt(dataSnapshot.child("missions").getValue().toString()),
                            Double.parseDouble(dataSnapshot.child("lat").getValue().toString()),
                            Double.parseDouble(dataSnapshot.child("lng").getValue().toString()));

                    Profile profile = (Profile) getSupportFragmentManager().findFragmentByTag(getString(R.string.title_profile));
                    if (profile != null) {
                        profile.setUserInfo(user);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    logout();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                logout();
            }
        });
    }

    public void openLoginFragment() {
        lockDrawer();

        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, Login.newInstance()).commit();
    }

    public void openMapFragment() {
        unlockDrawer();

        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, Map.newInstance()).commit();
    }

    /**
     * Unauthenticate from Firebase and from providers where necessary.
     */
    public void logout() {
        if (this.authData != null) {
            /* logout of Firebase */
            ref.unauth();
            setAuthenticatedUser(null);
            openLoginFragment();
        }
    }

    /**
     * Once a user is logged in, take the authData provided from Firebase and "use" it.
     */
    private void setAuthenticatedUser(AuthData authData) {
        this.authData = authData;
        if (authData != null) {
            getProfile(authData.getProviderData().get("email").toString());
            openMapFragment();
        } else {
            logout();
        }
    }

    /**
     * Helper method to calculate distance between two GPS points (given lat/lng)
     *
     * @return distance in meters
     */
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344 * 1000;
        return (dist);
    }

    /**
     * This function converts decimal degrees to radians
     *
     * @param deg
     * @return
     */
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /**
     * This function converts radians to decimal degrees
     *
     * @param rad
     * @return
     */
    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    /**
     * Utility class for authentication results
     */
    private class AuthResultHandler implements Firebase.AuthResultHandler {

        public AuthResultHandler() {
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            mAuthProgressDialog.hide();
            Log.d("AuthResultHandler", "auth successful");
            setAuthenticatedUser(authData);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            mAuthProgressDialog.hide();
            Log.d("MAIN", "on auth error");
            showErrorDialog(firebaseError.toString());
        }

        /**
         * Show errors to users
         */
        private void showErrorDialog(String message) {
            new AlertDialog.Builder(Main.this)
                    .setTitle("Error")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}
