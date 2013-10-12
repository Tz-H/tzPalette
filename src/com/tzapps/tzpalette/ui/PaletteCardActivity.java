package com.tzapps.tzpalette.ui;

import java.io.File;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tzapps.common.ui.OnFragmentStatusChangedListener;
import com.tzapps.common.utils.BitmapUtils;
import com.tzapps.tzpalette.Constants;
import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.data.PaletteData;
import com.tzapps.tzpalette.data.PaletteDataComparator.Sorter;
import com.tzapps.tzpalette.data.PaletteDataHelper;
import com.tzapps.tzpalette.debug.MyDebug;

public class PaletteCardActivity extends Activity implements OnFragmentStatusChangedListener
{
    private static final String TAG = "PaletteCardActivity";

    private static final int PALETTE_CARD_EDIT_RESULT  = 0;
    private static final int PALETTE_CARD_SHARE_RESULT = 1;
    
    private ViewPager mViewPager;
    private PaletteCardAdapter mCardAdapter;
    private PaletteDataHelper mDataHelper;
    
    private Sorter mSorter;
    private File mTempShareFile;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette_card);
        
        String sorterName = getIntent().getExtras().getString(Constants.PALETTE_DATA_SORTER_NAME);
        mSorter = Sorter.fromString(sorterName);
        
        if (mSorter == null)
            mSorter = Constants.PALETTE_DATA_SORTER_DEFAULT;
        
        mViewPager = (ViewPager) findViewById(R.id.palette_card_pager);
        
        mDataHelper = PaletteDataHelper.getInstance(this);
        mCardAdapter = new PaletteCardAdapter(this, mViewPager);
        
        long dataId = getIntent().getExtras().getLong(Constants.PALETTE_DATA_ID);
        mCardAdapter.setCurrentCard(dataId);
        
        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        clearTemp();
    }
    
    private void clearTemp()
    {
        if (mTempShareFile != null)
            mTempShareFile.delete();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.palette_card_view_actions, menu);

        //Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_share);

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
                
            case R.id.action_share:
                sharePaletteCard();
                return true;
                
            case R.id.action_edit:
                openEditView();
                return true;
                
            case R.id.action_emailPalette:
                if (MyDebug.LOG)
                    Log.d(TAG, "send palette card via email");
                return true;
                
            case R.id.action_export:
                if (MyDebug.LOG)
                    Log.d(TAG, "export palette card");
                
                exportPaletteCard();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_edit:
                openEditView();
                break;
        }
    }

    @Override
    public void onFragmentViewCreated(Fragment fragment){}
    
    public void setSorter(Sorter sorter)
    {
        mSorter = sorter;
    }
    
    public Sorter getSorter()
    {
        return mSorter;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        // Check which request we are responding to
        switch (requestCode)
        {
            case PALETTE_CARD_EDIT_RESULT:
                if (resultCode == RESULT_OK)
                {
                    long dataId = intent.getLongExtra(Constants.PALETTE_DATA_ID, Long.valueOf(-1));
                    
                    if (dataId != -1)
                    {
                        mCardAdapter.updateCard(dataId);
                    }
                }
                break;
                
            case PALETTE_CARD_SHARE_RESULT:
                if (mTempShareFile != null)
                    mTempShareFile.delete();
                break;
        }
    }
    
    private File getTempFile(String filename)
    {
        String path = Environment.getExternalStorageDirectory().toString();
        File file = new File(path + File.separator + Constants.FOLDER_HOME + File.separator + 
                             Constants.SUBFOLDER_TEMP + File.separator + filename);
        file.getParentFile().mkdirs();
        
        if (file.exists())
            file.delete();
        
        return file;
    }
    
    private void sharePaletteCard()
    {
        View view = mCardAdapter.getCurrentView();
        View paletteCard = view.findViewById(R.id.palette_card_frame);
        Bitmap bitmap = BitmapUtils.getBitmapFromView(paletteCard);

        assert (bitmap != null);

        mTempShareFile = getTempFile(Constants.TZPALETTE_TEMP_SHARE_FILE_NAME);
        BitmapUtils.saveBitmapToFile(bitmap, mTempShareFile);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(mTempShareFile));
        shareIntent.putExtra(Intent.EXTRA_TEXT, mCardAdapter.getCurrentData().getTitle());
        
        String actionTitle = getString(R.string.title_share_action);
        
        startActivityForResult(Intent.createChooser(shareIntent, actionTitle),
                               PALETTE_CARD_SHARE_RESULT);
    }
    
    private void exportPaletteCard()
    {
        View view = mCardAdapter.getCurrentView();
        View paletteCard = view.findViewById(R.id.palette_card_frame);
        Bitmap bitmap = BitmapUtils.getBitmapFromView(paletteCard);

        assert (bitmap != null);
        
        String title = mCardAdapter.getCurrentData().getTitle();

        if (title == null)
            title = getResources().getString(R.string.palette_title_default);
        
        String folderName = Constants.FOLDER_HOME + File.separator + Constants.SUBFOLDER_EXPORT;
        String fileName = Constants.TZPALETTE_FILE_PREFIX + title.replace(" ", "_");

        File file = BitmapUtils.saveBitmapToSDCard(bitmap, folderName + File.separator + fileName);

        /* invoke the system's media scanner to add the photo to
         * the Media Provider's database
         */
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
        
        Toast.makeText(this, "Palette Card <" + title + "> exported", Toast.LENGTH_SHORT).show();
    }
    
    private void openEditView()
    {
        PaletteData data = mCardAdapter.getCurrentData();
        
        if (data == null)
            return;
        
        if (MyDebug.LOG)
            Log.d(TAG, "edit palette card: " + data);
        
        Intent intent = new Intent(this, PaletteEditActivity.class);
        
        intent.putExtra(Constants.PALETTE_DATA_ID, data.getId());
        
        startActivityForResult(intent, PALETTE_CARD_EDIT_RESULT);
    }
    
    private class PaletteCardAdapter extends FragmentStatePagerAdapter 
    {
        private Context mContext;
        private ViewPager mViewPager;
        private List<PaletteData> dataList;
        private PaletteDataHelper mDataHelper;
        private View mCurrentView;

        public PaletteCardAdapter(Activity activity, ViewPager pager)
        {
            super(activity.getFragmentManager());
            mContext = activity;
            mViewPager = pager;
            mDataHelper = PaletteDataHelper.getInstance(mContext);
            
            dataList = mDataHelper.getAllData();
            Collections.sort(dataList, mSorter.getComparator());
            
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
        
        public PaletteData getCurrentData()
        {
            int index = mViewPager.getCurrentItem();
            
            return dataList.get(index);
        }
        
        public View getCurrentView()
        {
            return mCurrentView;
        }
        
        public void updateCard(long dataId)
        {
            for (PaletteData d : dataList)
            {
                if (d.getId() == dataId)
                {
                    d.copy(mDataHelper.get(dataId));
                    Collections.sort(dataList, mSorter.getComparator());
                    notifyDataSetChanged();
                    break;
                }
            }
        }
        
        @Override 
        public void setPrimaryItem(ViewGroup container, int position, Object object)
        {
            super.setPrimaryItem(container, position, object);
            
            mCurrentView = ((Fragment)object).getView();
        }
        
        @Override
        public int getItemPosition(Object object)
        {
            // force to destroy and recreate palette card in given 
            // fragment, it will fix the issue that the palette card
            // view cannot updated when open it in edit view and then
            // save the change to back. However, it is inefficient 
            // and might have the performance issue. And if so
            // it will need to have a further tweaking. 
            //
            // see more detailed discussion on 
            // http://stackoverflow.com/questions/10849552/android-viewpager-cant-update-dynamically
            return POSITION_NONE;
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
