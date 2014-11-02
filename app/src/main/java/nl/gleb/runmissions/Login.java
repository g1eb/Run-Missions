package nl.gleb.runmissions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Gleb on 02/11/14.
 */
public class Login extends Fragment {

    private EditText email_input, password_input;
    private Button login_button;
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
        return inflater.inflate(R.layout.log_in, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        email_input = (EditText) getActivity().findViewById(R.id.email);
        password_input = (EditText) getActivity().findViewById(R.id.password);

        login_button = (Button) getActivity().findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String email = email_input.getText().toString().trim();
                String password = password_input.getText().toString().trim();

                if (email != null && password != null) {
                    comm.handleLogin(email, password);
                }
            }
        });
    }
}
