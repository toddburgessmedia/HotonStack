package com.toddburgessmedia.stackoverflowretrofit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.toddburgessmedia.stackoverflowretrofit.eventbus.SearchDialogMessage;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by tburgess on 10/06/16.
 */
public class SearchDialog extends DialogFragment {

    View view;

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
                EditText search = (EditText) view.findViewById(R.id.search_tag);
                EventBus.getDefault().post(new SearchDialogMessage(search.getText().toString()));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EventBus.getDefault().post(new SearchDialogMessage(false));
            }
        });
        return builder.create();
    }

}
