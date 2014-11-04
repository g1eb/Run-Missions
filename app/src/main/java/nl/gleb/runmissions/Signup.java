package nl.gleb.runmissions;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Gleb on 04/11/14.
 */
public class Signup extends Fragment {

    Comm comm;
    private EditText signup_email, signup_username, signup_password, signup_confirm_password;
    private Button signup_button;

    public static Signup newInstance() {
        return new Signup();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        comm = (Comm) activity;
        comm.setTitle(getString(R.string.title_signup));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.signup, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        signup_email = (EditText) getActivity().findViewById(R.id.signup_email);
        signup_username = (EditText) getActivity().findViewById(R.id.signup_username);
        signup_password = (EditText) getActivity().findViewById(R.id.signup_password);
        signup_confirm_password = (EditText) getActivity().findViewById(R.id.signup_confirm_password);

        signup_confirm_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    confirmSignup();
                    handled = true;
                }
                return handled;
            }
        });

        signup_button = (Button) getActivity().findViewById(R.id.signup_button);
        signup_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                confirmSignup();
            }
        });
    }

    private void confirmSignup() {
        String email = signup_email.getText().toString().trim();
        String username = signup_username.getText().toString().trim();
        String password1 = signup_password.getText().toString().trim();
        String password2 = signup_confirm_password.getText().toString().trim();

        if (!password1.equals(password2)) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Errrrrr")
                    .setMessage("Passwords do not match..")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            if (!email.equals("") && !username.equals("") && !password1.equals("")) {
                comm.handleSignup(email, password1, username);
            }
        }
    }
}
