package com.tzapps.tzpalette.activity;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.tzapps.tzpalette.R;

public class ShowPictureActivity extends Activity
{
    private final static String TAG = "com.tzapps.tzpalette.activity.ShowPictureActivity";
    
    Bitmap mImageBitmap;
    ImageView mImageView;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture);
        
        // Show the Up button in the action bar.
        setupActionBar();
        
        mImageView = (ImageView) findViewById(R.id.imageview_picture);
        
        //Get the picture from the intent and handle it
        try
        {
            handleSmallCameraPhoto(getIntent());
        }
        catch (FileNotFoundException e)
        {
            Log.e(TAG, "handle image load failed");
            e.printStackTrace();
        }
    }
    
    private void handleSmallCameraPhoto(Intent intent) throws FileNotFoundException
    {
        Uri selectedImage = intent.getData();
        Bundle extras = intent.getExtras();
        assert(extras != null);
        
        if (selectedImage != null)
        {
            InputStream imageStream = getContentResolver().openInputStream(selectedImage);
            mImageBitmap = BitmapFactory.decodeStream(imageStream);
        }
        else if (extras != null)
        {
            mImageBitmap = (Bitmap) extras.get("data");
        }
        
        assert(mImageBitmap != null);
        
        mImageView.setImageBitmap(mImageBitmap);
    }
    
//    private void setPic()
//    {
//        // Get the dimensions of the view
//        int targetW = mImageView.getWidth();
//        int targetH = mImageView.getHeight();
//        
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//        
//        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//        
//        // Decode the image file into a Bitmap sized to fill the view
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//        
//        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        mImageView.setImageBitmap(bitmap);
//    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
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

}
