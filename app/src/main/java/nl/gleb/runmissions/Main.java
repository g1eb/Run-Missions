package nl.gleb.runmissions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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


public class Main extends ActionBarActivity
        implements Comm,
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final long UPDATE_INTERVAL = 10000;
    private static final long FASTEST_INTERVAL = 5000;

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
    static Firebase chatRef;
    private AuthData authData;
    ProgressDialog mAuthProgressDialog;

    // Profile
    User user = null;

    // Chat
    private ChatListAdapter chatListAdapter;
    private ValueEventListener connectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

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
        if (servicesConnected() && !mLocationClient.isConnected() ) {
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
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                ft.replace(R.id.container, Profile.newInstance()).commit();
                break;
            case 1:
                ft.replace(R.id.container, Map.newInstance()).commit();
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
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                openSettings();
                break;
            case R.id.action_logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, Settings.newInstance()).commit();
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
            Firebase userRef = ref.child("users/" + user.getEmail().replaceAll("[^A-Za-z0-9]", "-"));
            userRef.child("lat").setValue(location.getLatitude());
            userRef.child("lng").setValue(location.getLongitude());
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
        lockDrawerHideActionBar();

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

    /*
     * Lock the nav drawer and hide the action bar
     */
    public void lockDrawerHideActionBar() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        getSupportActionBar().hide();
    }

    /*
     * Unlock the nav drawer and show the action bar
     */
    public void unlockDrawerShowActionBar() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        getSupportActionBar().show();
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
        Firebase userRef = ref.child("users/" + email.replaceAll("[^A-Za-z0-9]", "-"));
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
        lockDrawerHideActionBar();

        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, Login.newInstance()).commit();
    }

    public void openMapFragment() {
        unlockDrawerShowActionBar();

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
