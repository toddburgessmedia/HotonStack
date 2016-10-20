/*
 * Copyright 2016 Todd Burgess Media
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
