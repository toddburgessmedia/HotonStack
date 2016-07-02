package com.toddburgessmedia.stackoverflowretrofit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 01/07/16.
 */
public class TimeFrameDialog extends DialogFragment {

    TimeFrameDialogListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        listener = (TimeFrameDialogListener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.time_dialog_title)
                .setItems(R.array.time_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.positiveClick(TimeFrameDialog.this, which);
                    }
                });
        return builder.create();

    }

    public interface TimeFrameDialogListener {
        public void positiveClick(DialogFragment fragment, int which);

        public void negativeClick(DialogFragment fragment, int which);
    }
}
