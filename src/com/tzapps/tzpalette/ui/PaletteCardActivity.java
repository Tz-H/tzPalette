package com.tzapps.tzpalette.ui;

import com.tzapps.common.ui.OnFragmentStatusChangedListener;
import com.tzapps.tzpalette.data.PaletteData;
import com.tzapps.tzpalette.data.PaletteDataHelper;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class PaletteCardActivity extends Activity implements OnFragmentStatusChangedListener
{
    private static final String TAG = "PaletteCardActivity";
    
    private PaletteData mPaletteData;
    private PaletteCardFragment mPaletteCardFragment;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        long dataId = getIntent().getExtras().getLong(MainActivity.PALETTE_CARD_DATA_ID);
        mPaletteData = PaletteDataHelper.getInstance(this).get(dataId);
        
        // Display the fragment as the main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PaletteCardFragment())
                .commit();

        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if (mPaletteData != null)
            outState.putParcelable("currentPaletteData", mPaletteData);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        mPaletteData = savedInstanceState.getParcelable("currentPaletteData");

        updatePaletteCardFragment();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentViewCreated(Fragment fragment)
    {
        if (fragment instanceof PaletteCardFragment)
        {
            mPaletteCardFragment = (PaletteCardFragment)fragment;
            updatePaletteCardFragment();
        }
    }
    
    private void updatePaletteCardFragment()
    {
        if (mPaletteCardFragment == null || mPaletteData == null)
            return;
        
        mPaletteCardFragment.update(mPaletteData);
    }

}
