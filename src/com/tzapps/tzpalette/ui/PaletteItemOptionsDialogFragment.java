package com.tzapps.tzpalette.ui;

import java.util.ArrayList;
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

public class PaletteItemOptionsDialogFragment extends DialogFragment implements DialogInterface.OnClickListener
{
    private OnClickPaletteItemOptionListener mCallback;
    
    public interface OnClickPaletteItemOptionListener
    {
        public void onClick(int position, long dataId, PaletteItemOption option);
    }
    
    public enum PaletteItemOption
    {
        Rename(R.string.action_rename),
        Delete(R.string.action_delete);
        
        private final int resId;    
        
        private PaletteItemOption(int resId)
        {
            this.resId = resId;
        }
        
        public int getResId()
        {
            return resId;
        }
    }
    
    private int mPosition;
    private long mDataId;
    private PaletteItemOption mOptions[];
    private String mOptionNames[];
    
    public static PaletteItemOptionsDialogFragment newInstance(String title, int position, long dataId)
    {
        PaletteItemOptionsDialogFragment frag = new PaletteItemOptionsDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("position", position);
        args.putLong("dataId", dataId);
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
        
        mOptions = PaletteItemOption.values();
        mOptionNames = new String[mOptions.length];
        
        for (int i = 0; i < mOptions.length; i++)
            mOptionNames[i] = getActivity().getResources().getString(mOptions[i].getResId());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        String title = getArguments().getString("title");
        
        mPosition = getArguments().getInt("position");
        mDataId   = getArguments().getLong("dataId");

        AlertDialog.Builder builder = new Builder(getActivity());
        builder.setTitle(title);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setItems(mOptionNames, this);

        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        getDialog().setCanceledOnTouchOutside(true);

        return view;
    }
    
    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        mCallback.onClick(mPosition, mDataId, mOptions[which]);
    }
}