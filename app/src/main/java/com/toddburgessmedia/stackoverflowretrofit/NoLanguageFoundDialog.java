package com.toddburgessmedia.stackoverflowretrofit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 26/06/16.
 */
public class NoLanguageFoundDialog extends DialogFragment {

        NothingFoundListener listener;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            listener = (NothingFoundListener) activity;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.nothing_dialog_title);
            builder.setMessage(R.string.nothing_dialog_body_github);
            builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listener.positiveClick(NoLanguageFoundDialog.this);
                        }
                    }
            );
            builder.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.negativeClick(NoLanguageFoundDialog.this);
                }
            });

            return builder.create();
        }

        public interface NothingFoundListener {
            public void positiveClick (DialogFragment dialog);
            public void negativeClick (DialogFragment dialog);

        }
    }



