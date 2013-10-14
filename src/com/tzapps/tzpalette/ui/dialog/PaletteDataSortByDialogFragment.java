package com.tzapps.tzpalette.ui.dialog;

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
import com.tzapps.tzpalette.data.PaletteDataComparator.Sorter;

public class PaletteDataSortByDialogFragment extends DialogFragment implements DialogInterface.OnClickListener
{
    private OnClickPaletteDataSorterListener mCallback;
    
    public interface OnClickPaletteDataSorterListener
    {
        public void onPaletteDataSorterClicked(Sorter sorter);
    }
    
    private String mEntries[];
    private String mValues[];
    
    public static PaletteDataSortByDialogFragment newInstance(String title)
    {
        PaletteDataSortByDialogFragment frag = new PaletteDataSortByDialogFragment();
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
            mCallback = (OnClickPaletteDataSorterListener)activity;
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
        
        mEntries = getResources().getStringArray(R.array.palette_data_sorter_entries);
        mValues  = getResources().getStringArray(R.array.palette_data_sorter_values);

        AlertDialog.Builder builder = new Builder(getActivity());
        builder.setTitle(title);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setItems(mEntries, this);
        
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        mCallback.onPaletteDataSorterClicked(Sorter.fromString(mValues[which]));
    }
}
