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
import com.tzapps.tzpalette.data.PaletteData;
import com.tzapps.tzpalette.utils.ActivityUtils;

public class MainActivity extends Activity implements BaseFragment.OnFragmentStatusChangedListener
{
    private final static String TAG = "MainActivity";
    
    /** Called when the user clicks the TakePicture button */
    static final int TAKE_PHOTE_RESULT   = 1; 
    /** Called when the user clicks the LoadPicture button */
    static final int LOAD_PICTURE_RESULT = 2;
    
    ViewPager   mViewPager;
    TabsAdapter mTabsAdapter;
    PaletteData mPaletteData;
    
    CaptureFragment       mCaptureFrag;
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
        
        mCaptureFrag = null;
        mPaletteListFragment = null;
        fragment3 = null;
        
        mTabsAdapter = new TabsAdapter(this, mViewPager);
        mTabsAdapter.addTab(actionBar.newTab().setText("Capture"), CaptureFragment.class, null);
        mTabsAdapter.addTab(actionBar.newTab().setText("My Palette"),MyPaletteListFragment.class, null);
        mTabsAdapter.addTab(actionBar.newTab().setText("About"),  CaptureFragment.class, null);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
        
        if (mPaletteData != null)
        {
            outState.putParcelable("bitmap", mPaletteData.getThumb());
            outState.putIntArray("colors", mPaletteData.getColors());
        }
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        
        mTabsAdapter.setSelectedTab(savedInstanceState.getInt("tab",0));
        
        Bitmap bitmap = (Bitmap)savedInstanceState.getParcelable("bitmap");
        int[]  colors = savedInstanceState.getIntArray("colors");
               
        if (mPaletteData == null)
            mPaletteData = new PaletteData();

        mPaletteData.setThumb(bitmap);
        mPaletteData.addColors(colors, /*reset*/true);
        
        refresh();
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
    
    /** refresh fragments in main activity with persisted mPaletteData */
    private void refresh()
    {
        if (mCaptureFrag != null && mPaletteData != null)
        {
            mCaptureFrag.updateImageView(mPaletteData.getThumb());
            mCaptureFrag.updateColors(mPaletteData.getColors());
        }
    }
    
    @Override
    public void onFragmentViewCreated(Fragment fragment)
    {
        if (fragment instanceof CaptureFragment)
            mCaptureFrag = (CaptureFragment) fragment;
        
        refresh();
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
        
        if (mPaletteData == null)
            return;
        
        // TODO: async this process to avoid UI thread block
        mPaletteData.analysis(/*rest*/true);
        mCaptureFrag.updateColors(mPaletteData.getColors());
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
        Bitmap bitmap     = null;
      
        if (selectedImage != null)
        {
            InputStream imageStream;
            try
            {
                imageStream  = getContentResolver().openInputStream(selectedImage);
                bitmap = BitmapFactory.decodeStream(imageStream);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
                Log.e(TAG, "load picture failed");
            }
        }
        else if (extras != null)
        {
            bitmap = (Bitmap) extras.get("data");
        }
        
        assert(bitmap != null);
        
        mPaletteData = new PaletteData(bitmap);

        refresh();
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
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
        
        static final class TabInfo
        {
            private final Class<?> clss;
            private final Bundle args;
            
            TabInfo(Class<?> _class, Bundle _args)
            {
                clss = _class;
                args = _args;
            }
        }
        
        public TabsAdapter(Activity activity, ViewPager pager)
        {
            super(activity.getFragmentManager());
            mContext = activity;
            mActionBar = activity.getActionBar();
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }
        
        public void setSelectedTab(int position)
        {
            mActionBar.setSelectedNavigationItem(position);
        }

        public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args)
        {
            TabInfo info = new TabInfo(clss, args);
            tab.setTag(info);
            tab.setTabListener(this);
            
            mTabs.add(info);
            mActionBar.addTab(tab);
            
            notifyDataSetChanged();
        }
        
        @Override
        public int getCount()
        {
            return mTabs.size();
        }
        
        @Override
        public Fragment getItem(int position)
        {
            TabInfo info = mTabs.get(position);
            
            return Fragment.instantiate(mContext, info.clss.getName(), info.args);
        }
        
        @Override
        public void onPageSelected(int position)
        {
            mActionBar.setSelectedNavigationItem(position);
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
