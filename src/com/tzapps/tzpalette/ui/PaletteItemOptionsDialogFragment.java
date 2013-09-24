package com.tzapps.tzpalette.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tzapps.tzpalette.R;

public class PaletteItemOptionsDialogFragment extends DialogFragment
{
    private DialogInterface.OnClickListener mClickListener;
    
    public static PaletteItemOptionsDialogFragment newInstance(String title)
    {
        PaletteItemOptionsDialogFragment frag = new PaletteItemOptionsDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }
    
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        
        // Verify the hold activity implements the callback interface
        try
        {
            // Instantiate the OnClickListener so we can send events to the host
            mClickListener = (DialogInterface.OnClickListener)activity;
        }
        catch (ClassCastException e)
        {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DialogInterface.OnClickListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        String title = getArguments().getString("title");

        AlertDialog.Builder builder = new Builder(getActivity());

        builder.setTitle(title);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setItems(R.array.palette_item_options, mClickListener); 

        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        getDialog().setCanceledOnTouchOutside(true);

        return view;
    }
}
