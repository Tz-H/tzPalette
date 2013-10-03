package com.tzapps.tzpalette.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tzapps.common.utils.BitmapUtils;
import com.tzapps.common.utils.StringUtils;
import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.data.PaletteData;
import com.tzapps.tzpalette.db.PaletteDataContract.PaletteDataEntry;
import com.tzapps.tzpalette.db.PaletteDataContract.PaletteThumbEntry;
import com.tzapps.tzpalette.debug.MyDebug;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class PaletteDataSource
{
    private static final String TAG = "PaletteDataSource";
    
    private Context mContext;
    // Database fields
    private SQLiteDatabase db;
    private PaletteDataDbHelper dbHelper;
    private String[] paletteDataColumns = 
        {
            PaletteDataEntry._ID,
            PaletteDataEntry.COLUMN_NAME_TITLE,
            PaletteDataEntry.COLUMN_NAME_COLORS,
            PaletteDataEntry.COLUMN_NAME_UPDATED,
            PaletteDataEntry.COLUMN_NAME_IMAGEURL
        };
    
    private String[] paletteThumbColumns =
        {
            PaletteThumbEntry._ID,
            PaletteThumbEntry.COLUMN_NAME_PALETTE_ID,
            PaletteThumbEntry.COLUMN_NAME_THUMB
        };
    
    public PaletteDataSource(Context context)
    {
        mContext = context;
        dbHelper = new PaletteDataDbHelper(context);
    }
    
    public void open(boolean writable)
    {
        if (writable)
            db = dbHelper.getWritableDatabase();
        else
            db = dbHelper.getReadableDatabase();
    }
    
    public void close()
    {
        dbHelper.close();
    }
    
    /**
     * insert a new PaletteData record into db
     * 
     * @param data         the palette data to save
     */
    public void add(PaletteData data)
    {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PaletteDataEntry.COLUMN_NAME_TITLE, data.getTitle());
        
        String colorsStr = Arrays.toString(data.getColors());
        values.put(PaletteDataEntry.COLUMN_NAME_COLORS, colorsStr);
        
        String title =  data.getTitle();
        if (StringUtils.isEmpty(title))
            title = mContext.getResources().getString(R.string.palette_title_default);
        
        values.put(PaletteDataEntry.COLUMN_NAME_TITLE, title);
        values.put(PaletteDataEntry.COLUMN_NAME_IMAGEURL, data.getImageUrl());
        
        long updated = System.currentTimeMillis();
        values.put(PaletteDataEntry.COLUMN_NAME_UPDATED, updated);
        
        // Insert the new row, returning the primary key values of the new row
        long insertId;
        insertId = db.insert(PaletteDataEntry.TABLE_NAME, 
                             PaletteDataEntry.COLUMN_NAME_NULLABLE, 
                             values);
        
        data.setId(insertId);
        data.setTitle(title);
        data.setUpdated(updated);
        
        // Insert the thumb into the thumb table
        addThumb(insertId, data.getThumb());
        
        if (MyDebug.LOG)
            Log.d(TAG, "PaletteData saved with id:" + insertId);
    }
    
    private void addThumb(long dataId, Bitmap thumb)
    {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        
        values.put(PaletteThumbEntry.COLUMN_NAME_PALETTE_ID, dataId);
        
        values.put(PaletteThumbEntry.COLUMN_NAME_THUMB, BitmapUtils.convertBitmapToByteArray(thumb));
        
        // Insert the thumb into database...
        db.insert(PaletteThumbEntry.TABLE_NAME, null, values);
    }
    
    /**
     * Update an existing PaletteData record in db
     * 
     * @param data         the palette data to update
     * @param updateThumb  flag to indicate if update thumb data
     */
    public void update(PaletteData data, boolean updateThumb)
    {
        long id = data.getId();
      
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        
        String title =  data.getTitle();
        if (StringUtils.isEmpty(title))
        {
            title = mContext.getResources().getString(R.string.palette_title_default);
            data.setTitle(title);
        }
        values.put(PaletteDataEntry.COLUMN_NAME_TITLE, title);
        
        String colorsStr = Arrays.toString(data.getColors());
        values.put(PaletteDataEntry.COLUMN_NAME_COLORS, colorsStr);
        
        long updated = System.currentTimeMillis();
        values.put(PaletteDataEntry.COLUMN_NAME_UPDATED, updated);
        
        if (updateThumb)
        {
            values.put(PaletteDataEntry.COLUMN_NAME_IMAGEURL, data.getImageUrl());
            updateThumb(id, data.getThumb());
        }
        
        // Issue SQL statement
        db.update(PaletteDataEntry.TABLE_NAME, 
                  values,
                  PaletteDataEntry._ID + " = " + id,
                  null);
        
        data.setUpdated(updated);
        
        if (MyDebug.LOG)
            Log.d(TAG, "PaletteData updated with id:" + id);
    }
    
    private void updateThumb(long dataId, Bitmap thumb)
    {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        
        values.put(PaletteThumbEntry.COLUMN_NAME_PALETTE_ID, dataId);
        values.put(PaletteThumbEntry.COLUMN_NAME_THUMB, BitmapUtils.convertBitmapToByteArray(thumb));
        
        // Issue SQL statement
        db.update(PaletteThumbEntry.TABLE_NAME, 
                  values,
                  PaletteThumbEntry.COLUMN_NAME_PALETTE_ID + " = " + dataId,
                  null);
    }
    
    /**
     * Delete a PaletteData record from db
     * 
     * @param data the palette data to delete
     */
    public void delete(PaletteData data)
    {
        delete(data.getId());
    }
    
    /**
     * Delete a PaletteData record based on its id
     * 
     * @param id the palette data id
     */
    public void delete(long id)
    {
        if (MyDebug.LOG)
            Log.d(TAG, "PaletteData deleted with id:" + id);
        
        db.delete(PaletteDataEntry.TABLE_NAME, 
                  PaletteDataEntry._ID + " = " + id, 
                  null);
        
        db.delete(PaletteThumbEntry.TABLE_NAME,
                  PaletteThumbEntry.COLUMN_NAME_PALETTE_ID + " = " + id, 
                  null);
    }
    
    public Bitmap getThumb(long dataId)
    {
        Bitmap bitmap = null;
        
        Cursor cursor = db.query(
                PaletteThumbEntry.TABLE_NAME,
                paletteThumbColumns,
                PaletteThumbEntry.COLUMN_NAME_PALETTE_ID + " = " + dataId,
                null,
                null,
                null,
                null
                );
        
        if (cursor.getCount() != 0)
        {
            cursor.moveToFirst();
            
            byte[] thumb = cursor.getBlob(cursor.getColumnIndexOrThrow(PaletteThumbEntry.COLUMN_NAME_THUMB));
            bitmap = BitmapFactory.decodeByteArray(thumb, 0, thumb.length); 
        }
        else
        {
            if (MyDebug.LOG)
                Log.d(TAG, "the palette data =" + dataId + " doesn't have a thumb");
        }
        
        // Make sure to close the cursor
        cursor.close();
        
        return bitmap;
    }
    
    /**
     * Get a PaletteData record based on its id
     * 
     * @param id    the palette data id
     * @return the PaletteData
     */
    public PaletteData get(long id)
    {
        PaletteData data = null;
        
        Cursor cursor = db.query(
                PaletteDataEntry.TABLE_NAME,
                paletteDataColumns,
                PaletteDataEntry._ID + " = " + id,
                null,
                null,
                null,
                null
                );
        
        if (cursor.getCount() != 0)
        {
            cursor.moveToFirst();
            data = cursorToPaletteData(cursor);
        }
        else
        {
            if (MyDebug.LOG)
                Log.e(TAG, "get palette data with id=" + id + "failed");
        }
        
        // Make sure to close the cursor
        cursor.close();
        
        return data;
    }
    
    /**
     * Get the palette data count from db
     * 
     * @return the count of palette data
     */
    public int count()
    {
        String sql = "SELECT COUNT(*) FROM " + PaletteDataEntry.TABLE_NAME;
        
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        
        return count;
    }
    
    /**
     * Get all PaletteData records from db
     * 
     * @return the array list with all PaletteData records
     */
    public List<PaletteData> getAllPaletteData()
    {
        List<PaletteData> dataList = new ArrayList<PaletteData>();
        
        Cursor cursor = db.query(
                PaletteDataEntry.TABLE_NAME,
                paletteDataColumns,
                null,
                null,
                null,
                null,
                null
                );
        
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            PaletteData data = cursorToPaletteData(cursor);
            dataList.add(data);
            cursor.moveToNext();
        }
        
        // Make sure to close the cursor
        cursor.close();
        return dataList;
    }
    
    private PaletteData cursorToPaletteData(Cursor cursor)
    {
        PaletteData data = new PaletteData();
         
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(PaletteDataEntry._ID));
        data.setId(id);
        
        String title = cursor.getString(cursor.getColumnIndexOrThrow(PaletteDataEntry.COLUMN_NAME_TITLE));
        data.setTitle(title);
        
        String colorStr = cursor.getString(cursor.getColumnIndexOrThrow(PaletteDataEntry.COLUMN_NAME_COLORS));
        data.addColors(convertColorStrToColors(colorStr), /*reset*/true);
        
        long updated = cursor.getLong(cursor.getColumnIndexOrThrow(PaletteDataEntry.COLUMN_NAME_UPDATED));
        data.setUpdated(updated);
        
        String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(PaletteDataEntry.COLUMN_NAME_IMAGEURL));
        data.setImageUrl(imageUrl);
        
        if (MyDebug.LOG)
            Log.d(TAG, "PaletteData fetched from db: " + data.toString());
        
        return data;
    }
    
    private int[] convertColorStrToColors(String colorsStr)
    {
        String[] items = colorsStr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "").split(",");
        
        int[] colors = new int[items.length];
        
        for (int i = 0; i < items.length; i++)
        {
            try
            {
                colors[i] = Integer.parseInt(items[i]);
            }
            catch (NumberFormatException nfe) {};
        }
        
        return colors;
    }
    
}


