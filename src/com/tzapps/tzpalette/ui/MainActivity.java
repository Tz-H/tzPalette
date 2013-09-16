package com.tzapps.tzpalette.ui;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.algorithm.ClusterCenter;
import com.tzapps.tzpalette.algorithm.KMeansProcessor;
import com.tzapps.tzpalette.utils.ActivityUtils;
import com.tzapps.tzpalette.utils.ColorUtils;

public class MainActivity extends Activity
{
    private final static String TAG = "MainActivity";
    
    /** Called when the user clicks the TakePicture button */
    static final int TAKE_PHOTE_RESULT   = 1; 
    /** Called when the user clicks the LoadPicture button */
    static final int LOAD_PICTURE_RESULT = 2;
    
    ViewPager   mViewPager;
    TabsAdapter mTabsAdapter;
    Bitmap      mBitmap;
    
    CaptureFragment       mCaptureFragment;
    MyPaletteListFragment mPaletteListFragment;
    CaptureFragment       fragment3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.pager);
        setContentView(mViewPager);
        
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        
        mCaptureFragment = (CaptureFragment)Fragment.instantiate(this, CaptureFragment.class.getName(), null);
        mPaletteListFragment = (MyPaletteListFragment)Fragment.instantiate(this, MyPaletteListFragment.class.getName(), null);
        fragment3 = (CaptureFragment)Fragment.instantiate(this, CaptureFragment.class.getName(), null);
        
        mTabsAdapter = new TabsAdapter(this, mViewPager);
        mTabsAdapter.addTab(actionBar.newTab().setText("Capture"), mCaptureFragment);
        mTabsAdapter.addTab(actionBar.newTab().setText("My Palette"),mPaletteListFragment);
        mTabsAdapter.addTab(actionBar.newTab().setText("About"), fragment3);
        
        if (savedInstanceState != null)
        {
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab",0));
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle presses on the action bar items
        switch (item.getItemId())
        {
            case R.id.action_settings:
                // openSettings();
                return true;

            case R.id.action_about:
                // openAbout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** Called when the user clicks the TakePhoto button */
    public void takePhoto(View view)
    {
        Log.d(TAG, "take a photo");
        
        if (ActivityUtils.isIntentAvailable(getBaseContext(), MediaStore.ACTION_IMAGE_CAPTURE))
        {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, TAKE_PHOTE_RESULT);
        }
        else
        {
            Log.e(TAG, "no camera found");
        }
    }
    /** Called when the user clicks the Analysis button */
    public void analysisPicture(View view)
    {
        Log.d(TAG, "analysis the picture");
        
        if (mBitmap == null)
            return;
        
        KMeansProcessor proc = new KMeansProcessor(8, 10);        
        proc.processKMean(mBitmap);
        
        for (ClusterCenter center : proc.getClusterCenters())
        {
            Log.i(TAG, "Center color is " + ColorUtils.colorToRGBString(center.getValue()));
        }
        
        int[] colors = new int[8];
        
        for (int i = 0; i < 8; i++)
            colors[i] = proc.getClusterCenters().get(i).getValue();
        
        mCaptureFragment.updateColors(colors);
    }

    /** Called when the user clicks the PickPicture button */
    public void loadPicture(View view)
    {
        Log.d(TAG, "load a picture");
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, LOAD_PICTURE_RESULT);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Check which request we are responding to
        switch(requestCode)
        {
            case TAKE_PHOTE_RESULT:
            case LOAD_PICTURE_RESULT:
                if (resultCode == RESULT_OK)
                    handlePicture(data);
                break;
                
            default:
                break;
        }
    }
    
    private void handlePicture(Intent intent)
    {
        Uri selectedImage = intent.getData();
        Bundle extras     = intent.getExtras();
        
        if (mBitmap != null)
        {
            mBitmap.recycle();
            mBitmap = null;
        }
        
        if (selectedImage != null)
        {
            InputStream imageStream;
            try
            {
                imageStream  = getContentResolver().openInputStream(selectedImage);
                mBitmap = BitmapFactory.decodeStream(imageStream);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
                Log.e(TAG, "load picture failed");
            }
        }
        else if (extras != null)
        {
            mBitmap = (Bitmap) extras.get("data");
        }
        
        assert(mBitmap != null);
        
        mCaptureFragment.updateImageView(mBitmap);
    }

    /**
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated TabHost.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between pages.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabsAdapter supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct paged in the ViewPager whenever the selected
     * tab changes.
     */
    public static class TabsAdapter extends FragmentPagerAdapter
                                    implements ActionBar.TabListener, ViewPager.OnPageChangeListener
    {
        private final Context mContext;
        private final ActionBar mActionBar;
        private final ViewPager mViewPager;
        private final ArrayList<Fragment> mFragments = new ArrayList<Fragment>();
        
        public TabsAdapter(Activity activity, ViewPager pager)
        {
            super(activity.getFragmentManager());
            mContext = activity;
            mActionBar = activity.getActionBar();
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }
        
        public void addTab(ActionBar.Tab tab, Fragment fragment)
        {
            mFragments.add(fragment);
            
            tab.setTabListener(this);
            mActionBar.addTab(tab);
            
            notifyDataSetChanged();
        }
        
        @Override
        public int getCount()
        {
            return mFragments.size();
        }
        
        @Override
        public Fragment getItem(int position)
        {
            return mFragments.get(position);
        }
        
        @Override
        public void onPageSelected(int position)
        {
            mActionBar.setSelectedNavigationItem(position);
            
            //mActionBar.setTitle(mActionBar.getSelectedTab().getText());
        }
        
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){}
        
        @Override
        public void onPageScrollStateChanged(int state){}
        
        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft)
        {
            mViewPager.setCurrentItem(tab.getPosition());
        }
        
        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft){}
        
        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft){}
    }
}
