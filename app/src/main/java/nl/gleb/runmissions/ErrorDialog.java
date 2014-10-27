package nl.gleb.runmissions;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Gleb on 27/10/14.
 */
public class ErrorDialog extends DialogFragment {
    // Global field to contain the error dialog
    private Dialog mDialog;
    // Default constructor. Sets the dialog field to null
    public ErrorDialog() {
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