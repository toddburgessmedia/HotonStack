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

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 25/09/16.
 */

public class StackExchangeSortDialog extends DialogFragment {


    StackExchangeSortDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a = (Activity) context;
        listener = (StackExchangeSortDialogListener) a;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
        .setTitle(R.string.tags_dialog_sort)
        .setItems(R.array.tags_sortby, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.stackExchangeSortpositiveClick(StackExchangeSortDialog.this, which);
                }
            });
        return builder.create();

        }

    public interface StackExchangeSortDialogListener {
        void stackExchangeSortpositiveClick(DialogFragment fragment, int which);
    }

}
