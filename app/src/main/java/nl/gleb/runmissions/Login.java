package nl.gleb.runmissions;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Gleb on 02/11/14.
 */
public class Login extends Fragment implements View.OnClickListener {

    private EditText email_input, password_input;
    private Button login_button, signup_button;
    Comm comm;

    public static Login newInstance() {
        return new Login();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        comm = (Comm) activity;
        comm.setTitle(getString(R.string.title_login));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        email_input = (EditText) getActivity().findViewById(R.id.email);
        password_input = (EditText) getActivity().findViewById(R.id.password);

        password_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submitCredentials();
                    handled = true;
                }
                return handled;
            }
        });

        login_button = (Button) getActivity().findViewById(R.id.login_button);
        login_button.setOnClickListener(this);

        signup_button = (Button) getActivity().findViewById(R.id.signup_link_button);
        signup_button.setOnClickListener(this);
    }

    private void submitCredentials() {
        // Hide soft keyboard
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(login_button.getWindowToken(), 0);

        String email = email_input.getText().toString().trim();
        String password = password_input.getText().toString().trim();

        if (email != null && password != null) {
            comm.handleLogin(email, password);
        }
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ) {
            case R.id.login_button:
                submitCredentials();
                break;
            case R.id.signup_link_button:
                comm.openSignup();
                break;
        }
    }
}
