package com.tzapps.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;

public class BaseListFragment extends ListFragment
{
    OnFragmentStatusChangedListener mCallback;
    
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        
        // This makes sure the container activity has implemented
        // the callback interface. If not, it throws an exception
        try
        {
            mCallback = (OnFragmentStatusChangedListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentStatusChangedListener");
        }
    }
    
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        
        mCallback.onFragmentViewCreated(this); 
    }
}
