package nl.gleb.runmissions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * Created by Gleb on 02/11/14.
 */
public class Login extends Activity {

    private static final String TAG = "LoginActivity";

    EditText email_input, password_input;
    Button login_button;
    ProgressDialog mAuthProgressDialog;

    Firebase ref;
    private AuthData authData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getString(R.string.progress_dialog_title));
        mAuthProgressDialog.setMessage(getString(R.string.progress_dialog_message));
        mAuthProgressDialog.setCancelable(false);

        email_input = (EditText) findViewById(R.id.email);
        password_input = (EditText) findViewById(R.id.password);

        login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide soft keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(login_button.getWindowToken(), 0);

                mAuthProgressDialog.show();
                loginWithPassword();
            }
        });

        Firebase.setAndroidContext(getApplicationContext());
        ref = new Firebase(getString(R.string.firebase_ref));

        /* Check if the user is authenticated with Firebase already.
         * If this is the case we can set the authenticated
         * user and hide hide any login buttons */
        ref.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                mAuthProgressDialog.hide();
                setAuthenticatedUser(authData);
            }
        });
    }

    /**
     * This method fires when any startActivityForResult finishes.
     * The requestCode maps to the value passed into startActivityForResult.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loginWithPassword() {
        String email = email_input.getText().toString().trim();
        String pass = password_input.getText().toString().trim();

        if (email != null && pass != null) {
            mAuthProgressDialog.show();
            ref.authWithPassword(email, pass, new AuthResultHandler(getString(R.string.login_type)));
        }
    }

    /**
     * Unauthenticate from Firebase and from providers where necessary.
     */
    private void logout() {
        if (this.authData != null) {
            /* logout of Firebase */
            ref.unauth();
            /* Update authenticated user and show login buttons */
            setAuthenticatedUser(null);
        }
    }

    /**
     * Once a user is logged in, take the authData provided from Firebase and "use" it.
     */
    private void setAuthenticatedUser(AuthData authData) {
        if (authData != null) {
            startActivity(new Intent("nl.gleb.runmissions.MAIN"));

            String name = authData.getUid();
            if (name != null) {
                Toast.makeText(getApplicationContext(), "Logged in as " + name + " (" + authData.getProvider() + ")", Toast.LENGTH_LONG).show();
            }
        }
        this.authData = authData;
    }

    /**
     * Utility class for authentication results
     */
    private class AuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;

        public AuthResultHandler(String provider) {
            this.provider = provider;
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            mAuthProgressDialog.hide();
            Log.i(TAG, provider + " auth successful");
            setAuthenticatedUser(authData);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            mAuthProgressDialog.hide();
            Log.i(TAG, "on auth error");
            showErrorDialog(firebaseError.toString());
        }

        /**
         * Show errors to users
         */
        private void showErrorDialog(String message) {
            new AlertDialog.Builder(Login.this)
                    .setTitle("Error")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}
