package com.tzapps.tzpalette.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tzapps.tzpalette.db.PaletteDataDbHelper;
import com.tzapps.tzpalette.db.PaletteDataContract.PaletteDataEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PaletteDataSource
{
    private static final String TAG = "PaletteDataSource";
    
    // Database fields
    private SQLiteDatabase db;
    private PaletteDataDbHelper dbHelper;
    private String[] allColumns = 
        {
            PaletteDataEntry._ID,
            PaletteDataEntry.COLUMN_NAME_TITLE,
            PaletteDataEntry.COLUMN_NAME_COLORS,
            PaletteDataEntry.COLUMN_NAME_UPDATED,
            PaletteDataEntry.COLUMN_NAME_THUMB
        };
    
    public PaletteDataSource(Context context)
    {
        dbHelper = new PaletteDataDbHelper(context);
    }
    
    public void open()
    {
        db = dbHelper.getWritableDatabase();
    }
    
    public void close()
    {
        dbHelper.close();
    }
    
    public void save(PaletteData data)
    {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PaletteDataEntry.COLUMN_NAME_TITLE, data.getTitle());
        
        String colorsStr = Arrays.toString(data.getColors());
        Log.d(TAG, "colors saved: " + colorsStr);
        
        values.put(PaletteDataEntry.COLUMN_NAME_COLORS, colorsStr);
        values.put(PaletteDataEntry.COLUMN_NAME_UPDATED, System.currentTimeMillis());
        
        // Insert the new row, returning the primary key values of the new row
        long insertId;
        insertId = db.insert(PaletteDataEntry.TABLE_NAME, 
                             PaletteDataEntry.COLUMN_NAME_NULLABLE, 
                             values);
        
        data.setId(insertId);
        
        Log.d(TAG, "PaletteData saved with id:" + insertId);
    }
    
    public void update(PaletteData data)
    {
        long id = data.getId();
      
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PaletteDataEntry.COLUMN_NAME_TITLE, data.getTitle());
        
        String colorsStr = Arrays.toString(data.getColors());
        Log.d(TAG, "colors updated: " + colorsStr);
        values.put(PaletteDataEntry.COLUMN_NAME_COLORS, colorsStr);
        values.put(PaletteDataEntry.COLUMN_NAME_UPDATED, System.currentTimeMillis());
        
        // Issue SQL statement
        db.update(PaletteDataEntry.TABLE_NAME, 
                  values,
                  PaletteDataEntry._ID + " = " + id,
                  null);
        
        Log.d(TAG, "PaletteData updated with id:" + id);
    }
    
    public void delete(PaletteData data)
    {
        long id = data.getId();
        
        Log.d(TAG, "PaletteData deleted with id:" + id);
        
        db.delete(PaletteDataEntry.TABLE_NAME, 
                  PaletteDataEntry._ID + " = " + id, 
                  null);
    }
    
    public List<PaletteData> getAllPaletteData()
    {
        List<PaletteData> dataList = new ArrayList<PaletteData>();
        
        Cursor cursor = db.query(
                PaletteDataEntry.TABLE_NAME,
                allColumns,
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
        
        data.setId(cursor.getLong(cursor.getColumnIndexOrThrow(PaletteDataEntry._ID)));
        data.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(PaletteDataEntry.COLUMN_NAME_TITLE)));
        
        String colorStr = cursor.getString(cursor.getColumnIndexOrThrow(PaletteDataEntry.COLUMN_NAME_COLORS));
        data.addColors(convertColorStrToColors(colorStr), /*reset*/true);
        
        /* TODO:
         * 1. update time
         * 2. thumb
         */
        
        return data;
    }
    
    private int[] convertColorStrToColors(String colorsStr)
    {
        Log.d(TAG, "colors from db: " + colorsStr);
        
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


