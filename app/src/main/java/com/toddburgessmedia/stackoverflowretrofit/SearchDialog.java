package com.toddburgessmedia.stackoverflowretrofit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by tburgess on 10/06/16.
 */
public class SearchDialog extends DialogFragment {

    SearchDialogListener listener;
    View view;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        listener = (SearchDialogListener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflate = getActivity().getLayoutInflater();

        view = inflate.inflate(R.layout.search_dialog,null);
        builder.setView(view);
        builder.setTitle("Tag to Search for:");
        builder.setPositiveButton("Search",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(MainActivity.TAG, "onClick: d'oh");
                listener.positiveClick(SearchDialog.this);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(MainActivity.TAG, "onCancel: WooHoo!");
                listener.negativeClick(SearchDialog.this);

            }
        });

        return builder.create();

    }

    public interface SearchDialogListener {
        public void positiveClick (DialogFragment fragment);
        public void negativeClick (DialogFragment fragment);
    }
}
