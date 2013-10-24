package com.tzapps.tzpalette.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;

import com.tzapps.common.ui.OnFragmentStatusChangedListener;
import com.tzapps.common.utils.StringUtils;
import com.tzapps.common.utils.UriUtils;
import com.tzapps.tzpalette.Constants;
import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.data.PaletteData;
import com.tzapps.tzpalette.data.PaletteDataHelper;
import com.tzapps.tzpalette.debug.MyDebug;

public class PaletteEditActivity extends Activity implements OnFragmentStatusChangedListener
{
    private static final String TAG = "PaletteEditActivity";
    
    private long dataId;
    private Uri  imageUri;
    
    private PaletteDataHelper mDataHelper;
    
    private PaletteEditFragment mEditFrag;
    private ProgressDialog mProgresDialog;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        dataId = getIntent().getExtras().getLong(Constants.PALETTE_DATA_ID);
        imageUri = getIntent().getData();
        
        mDataHelper = PaletteDataHelper.getInstance(this);
        
        mEditFrag = (PaletteEditFragment)getFragmentManager().findFragmentByTag("PaletteEditFragment");
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if (mEditFrag == null)
        {
            mEditFrag = new PaletteEditFragment(); 
            transaction.add(mEditFrag, "PaletteEditFragment");
        }

        // Display the fragment as the main content
        transaction.replace(android.R.id.content, mEditFrag);
        transaction.commit();
        
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
        inflater.inflate(R.menu.palette_edit_view_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
                
            case R.id.action_cancel:
                doCancel();
                return true;
                
            case R.id.action_save:
                doSave();
                return true;
                
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_analysis:
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.title_reanalyse_colors)
                        .setMessage(R.string.message_reanalyse_colors)
                        .setPositiveButton(R.string.action_continue, new OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) 
                            {
                                //clear all existing colors in edit view
                                mEditFrag.clearColors();
                                doAnalysis(mEditFrag.getData());
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) 
                            {
                                dialog.dismiss();
                            }
                        })
                        .create();

                dialog.setCanceledOnTouchOutside(true);
                
                dialog.show();
                break;
                
            case R.id.palette_edit_view_title:
                showRenameDialog(mEditFrag.getData());
                break;
                
            case R.id.palette_edit_view_favourite:
                CheckBox chk = (CheckBox)view;
                boolean favourite = chk.isChecked();
                mEditFrag.updateFavourite(favourite);
                break;
        }
    }
    
    @Override
    public void onBackPressed()
    {
        PaletteData curData = mEditFrag.getData();
        PaletteData dbData = mDataHelper.get(curData.getId());
        
        //check whether the platte data has been changed
        if (!curData.equals(dbData))
        {
            AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle(R.string.title_exit_without_save)
                            .setMessage(R.string.message_exit_without_save)
                            .setPositiveButton(R.string.action_exit, new OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) 
                                {
                                    finish();
                                }
                            })
                            .setNeutralButton(R.string.action_saveAndExit, new OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) 
                                {
                                    doSave();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) 
                                {
                                    dialog.dismiss();
                                }
                            })
                            .create();
            
            dialog.setCanceledOnTouchOutside(true);
            
            dialog.show();
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentViewCreated(Fragment fragment)
    {
        if (fragment instanceof PaletteEditFragment)
        {
            mEditFrag = (PaletteEditFragment)fragment;
            
            if (mEditFrag.getData() != null)
                return;
            
           if (dataId != -1)
            {
                PaletteData data = mDataHelper.get(dataId);
                mEditFrag.updateData(data, true);
                dataId = -1;
            }
            else if (imageUri != null)
            {
                handlePicture(imageUri);
                imageUri = null;
                getIntent().setData(null);
            }
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }
    
    /** Called when the user performs the Analysis action */
    private void doAnalysis(PaletteData data)
    {
        if (MyDebug.LOG)
            Log.d(TAG, "analysis the picture");

        if (data == null)
            return;
        
        new PaletteDataAnalysisTask().execute(data);
    }
    
    /** Called when the user performs change title action */
    private void showRenameDialog(PaletteData data)
    {
        if (data == null)
            return;
        
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);

        input.setText(data.getTitle());
        input.setSingleLine(true);
        input.setSelectAllOnFocus(true);

        alert.setTitle(R.string.title_palette_edit_view_rename)
                .setView(input)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String text = input.getText().toString();
                        updatePaletteDataTitle(text);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);

        final AlertDialog dialog = alert.create();

        input.setOnFocusChangeListener(new OnFocusChangeListener()
            {
                @Override
                public void onFocusChange(View v, boolean hasFocus)
                {
                    if (hasFocus)
                    {
                        dialog.getWindow().setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                }
            }
        );

        dialog.show();
    }
    
    protected void updatePaletteDataTitle(String text)
    {
        mEditFrag.updateTitle(text);
    }

    /** Called when the user performs the Cancel action */
    private void doCancel()
    {
        if (MyDebug.LOG)
            Log.d(TAG, "cancel the edit");
        
        setResult(RESULT_CANCELED);
        finish();
    }
    
    /** Called when the user performs the Save action */
    private void doSave()
    {
        if (MyDebug.LOG)
            Log.d(TAG, "save the edit");
        
        PaletteData data = mEditFrag.getData();

        if (data == null)
            return;
        
        long    id;
        boolean addNew;

        if (data.getId() == -1)
        {
            id = mDataHelper.add(data);
            addNew = true;
        }
        else
        {
            mDataHelper.update(data, /*updateThumb*/false);
            
            id = data.getId();
            addNew = false;
        }
        
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.PALETTE_DATA_ID, id);
        intent.putExtra(Constants.PALETTE_DATA_ADDNEW, addNew);
        
        setResult(RESULT_OK, intent);
        finish();
    }
    
    /** refresh edit fragment with persisted palette data */
    private void updateEditVeiw(PaletteData data, boolean updatePicture)
    {
        if (data == null || mEditFrag == null)
            return;
        
        mEditFrag.updateData(data, updatePicture);
    }
    
    private String getPictureTilteFromUri(Uri uri)
    {
        String displayName = UriUtils.getUriDisplayName(this, uri);
        
        // if we cannot fetch the display name then give it a default name... 
        if (StringUtils.isEmpty(displayName))
            displayName = getResources().getString(R.string.palette_title_default);
        
        //TEST
        String path = UriUtils.getUriPath(this, uri);
        Log.d(TAG, "uri path: "  + path);
        
        return displayName;
    }
    
    private void handlePicture(Uri imageUrl)
    {
        assert(imageUrl != null);
        
        PaletteData data = new PaletteData();
       
        data.setTitle(getPictureTilteFromUri(imageUrl));
        data.setImageUrl(imageUrl.toString());
        
        mEditFrag.updateData(data, true);
        
        // start to analysis the picture immediately after loading it
        new PaletteDataAnalysisTask().execute(data);
    }

    private void startAnalysis()
    {
        if (mProgresDialog == null)
            mProgresDialog = new ProgressDialog(this);

        mProgresDialog.setMessage(getResources().getText(R.string.analysis_picture_in_process));
        mProgresDialog.setIndeterminate(true);
        mProgresDialog.setCancelable(false);
        mProgresDialog.show();
    }

    private void stopAnalysis()
    {
        mProgresDialog.hide();
    }

    /**
     * This is a helper class used to do the palette data analysis process asynchronously
     */
    private class PaletteDataAnalysisTask extends AsyncTask<PaletteData, Void, PaletteData>
    {
        protected void onPreExecute()
        {
            super.onPreExecute();
            startAnalysis();
        }

        /*
         * The system calls this to perform work in a worker thread and delivers it the parameters
         * given to AsyncTask.execute()
         */
        protected PaletteData doInBackground(PaletteData... dataArray)
        {
            PaletteData data = dataArray[0];
            
            mDataHelper.analysis(data, /* reset */true);

            return data;
        }

        /*
         * The system calls this to perform work in the UI thread and delivers the result from
         * doInBackground()
         */
        protected void onPostExecute(PaletteData result)
        {
            stopAnalysis();
            updateEditVeiw(result, false);
        }
    }
    
}
