package com.tzapps.tzpalette.ui.dialog;

import java.util.LinkedList;
import java.util.List;

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
import com.tzapps.tzpalette.data.PaletteData;

public class PaletteDataOptionsDialogFragment extends DialogFragment implements DialogInterface.OnClickListener
{
    private OnClickPaletteItemOptionListener mCallback;
    
    public interface OnClickPaletteItemOptionListener
    {
        public void onPaletteItemOptionClicked(int position, long dataId, PaletteDataOption option);
    }
    
    private PaletteData mData;
    private int mPosition;
    private long mDataId;
    private String mOptionEntries[];
    private String mOptionValues[];
    
    public static PaletteDataOptionsDialogFragment newInstance(int position, long dataId, PaletteData data)
    {
        PaletteDataOptionsDialogFragment frag = new PaletteDataOptionsDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", data.getTitle());
        args.putInt("position", position);
        args.putLong("dataId", dataId);
        args.putParcelable("data", data);
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
            mCallback = (OnClickPaletteItemOptionListener)activity;
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
        
        mPosition      = getArguments().getInt("position");
        mDataId        = getArguments().getLong("dataId");
        mData          = getArguments().getParcelable("data");
        
        mOptionEntries = getResources().getStringArray(R.array.palette_itemOption_entries);
        mOptionValues  = getResources().getStringArray(R.array.palette_itemOption_valeus);
        
        if (mData.isFavourite())
        {
            mOptionEntries = removeElements(mOptionEntries, getString(R.string.action_favourite));
            mOptionValues = removeElements(mOptionValues, PaletteDataOption.Favourite.getName());
        }
        else
        {
            mOptionEntries = removeElements(mOptionEntries, getString(R.string.action_unfavourite));
            mOptionValues = removeElements(mOptionValues, PaletteDataOption.UnFavourite.getName());
        }

        AlertDialog.Builder builder = new Builder(getActivity());
        builder.setTitle(title);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setItems(mOptionEntries, this);

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        
        return dialog;
    }
    
    public static String[] removeElements(String[] input, String...removeElements)
    {
        List<String> result = new LinkedList<String>();
        
        for (String item : input)
        {
            for (String removed : removeElements)
            {
                if (!removed.equals(item))
                    result.add(item);
            }
        }
        
        return result.toArray(new String[]{});
    }

    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        mCallback.onPaletteItemOptionClicked(mPosition, mDataId, PaletteDataOption.fromString(mOptionValues[which]));
    }
}
