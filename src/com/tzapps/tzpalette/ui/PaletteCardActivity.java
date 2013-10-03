package com.tzapps.tzpalette.ui;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.tzapps.common.ui.OnFragmentStatusChangedListener;
import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.data.PaletteData;
import com.tzapps.tzpalette.data.PaletteDataHelper;

public class PaletteCardActivity extends Activity implements OnFragmentStatusChangedListener
{
    private static final String TAG = "PaletteCardActivity";
    
    private ViewPager mViewPager;
    private PaletteCardAdapter mCardAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.palette_card_pager);
        setContentView(mViewPager);
        
        mCardAdapter = new PaletteCardAdapter(this, mViewPager);
        
        long dataId = getIntent().getExtras().getLong(MainActivity.PALETTE_CARD_DATA_ID);
        mCardAdapter.setCurrentCard(dataId);
        
        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.palette_card_view_actions, menu);

        // Locate MenuItem with ShareActionProvider
        //MenuItem item = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        //mShareActionProvider = (ShareActionProvider) item.getActionProvider();

        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            
            case R.id.action_sendEmail:
                Log.d(TAG, "send email");
                return true;
                
            case R.id.action_delete:
                Log.d(TAG, "delete palette card");
                return true;
                
            case R.id.action_export:
                Log.d(TAG, "export palette card");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentViewCreated(Fragment fragment){}
    
    public static class PaletteCardAdapter extends FragmentPagerAdapter
    {
        private Context mContext;
        private ViewPager mViewPager;
        private List<PaletteData> dataList;

        public PaletteCardAdapter(Activity activity, ViewPager pager)
        {
            super(activity.getFragmentManager());
            mContext = activity;
            mViewPager = pager;
            
            dataList = PaletteDataHelper.getInstance(mContext).getAllData();
            mViewPager.setAdapter(this);
        }

        public void setCurrentCard(long dataId)
        {
            int index = 0;
            
            for (int i = 0; i < dataList.size(); i++)
            {
                PaletteData data = dataList.get(i);
                
                if (data.getId() == dataId)
                {
                    index = i;
                    break;
                }
            }
            
            mViewPager.setCurrentItem(index);
        }

        @Override
        public Fragment getItem(int position)
        {
            PaletteData data = dataList.get(position);
            
            PaletteCardFragment fragment = (PaletteCardFragment)Fragment.instantiate(mContext, 
                                        PaletteCardFragment.class.getName(), null);
            fragment.setData(data);
            
            return fragment;
        }
        
        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            PaletteCardFragment fragment = (PaletteCardFragment)super.instantiateItem(container, position);
            
            PaletteData data = dataList.get(position);
            fragment.setData(data);
            
            return fragment;
        }
        
        @Override
        public int getCount()
        {
            return dataList.size();
        }
    }

}
