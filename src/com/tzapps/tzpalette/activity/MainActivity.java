package com.tzapps.tzpalette.activity;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.fragment.CaptureFragment;
import com.tzapps.tzpalette.utils.ActivityUtils;

public class MainActivity extends FragmentActivity
{
    private final static String TAG = "com.tzapps.tzpalette.activity.MainActivity";
    
    /** Called when the user clicks the TakePicture button */
    static final int TAKE_PHOTE_RESULT   = 1; 
    /** Called when the user clicks the LoadPicture button */
    static final int LOAD_PICTURE_RESULT = 2;
    
    ViewPager mViewPager;
    TabsAdapter mTabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.pager);
        setContentView(mViewPager);
        
        final ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        mTabsAdapter = new TabsAdapter(this, mViewPager);
        mTabsAdapter.addTab(bar.newTab().setText("My Palette"),
                            CaptureFragment.class,null);
        mTabsAdapter.addTab(bar.newTab().setText("Capture"), 
                            CaptureFragment.class,null);
        mTabsAdapter.addTab(bar.newTab().setText("About"), 
                            CaptureFragment.class,null);
        
        if (savedInstanceState != null)
        {
            bar.setSelectedNavigationItem(savedInstanceState.getInt("tab",0));
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
            case R.id.action_share:
                // openSearch();
                return true;

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

    /** Called when the user clicks the PickPicture button */
    public void loadPicture(View view)
    {
        Log.i(TAG, "load a picture");
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, LOAD_PICTURE_RESULT);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Check which request we are responding to
        if (requestCode == TAKE_PHOTE_RESULT)
        {
            if (resultCode == RESULT_OK)
            {
                Bundle extras = data.getExtras();
                assert(extras != null);
                
                Intent intent = new Intent(this, ShowPictureActivity.class);
                intent.putExtras(extras);
                startActivity(intent);
            }
        }
        else if (requestCode == LOAD_PICTURE_RESULT)
        {
            if (resultCode == RESULT_OK)
            {
                Uri selectedImage = data.getData();
                assert(selectedImage != null);
                
                Intent intent = new Intent(this, ShowPictureActivity.class);
                intent.setData(selectedImage);
                startActivity(intent);
            }
        }
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
        
        public TabsAdapter(FragmentActivity activity, ViewPager pager)
        {
            super(activity.getSupportFragmentManager());
            mContext = activity;
            mActionBar = activity.getActionBar();
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
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
            Log.i(TAG, "page " + position + " selected");
            mActionBar.setSelectedNavigationItem(position);
            
            mActionBar.setTitle(mActionBar.getSelectedTab().getText());
        }
        
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){}
        
        @Override
        public void onPageScrollStateChanged(int state){}
        
        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft)
        {
            Object tag = tab.getTag();
            
            for (int i = 0; i < mTabs.size(); i++)
            {
                if (mTabs.get(i) == tag)
                {
                    mViewPager.setCurrentItem(i);
                    break;
                }
            }
        }
        
        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft){}
        
        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft){}
    }
}
