package com.tzapps.tzpalette.db;

import android.provider.BaseColumns;

public final class PaletteDataContract
{
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor
    public PaletteDataContract() {}
    
    /* Inner class that defines the palettes table contents */
    public static abstract class PaletteDataEntry implements BaseColumns
    {
        public static final String TABLE_NAME           = "Palettes";
        public static final String COLUMN_NAME_TITLE    = "title";
        public static final String COLUMN_NAME_COLORS   = "colors";
        public static final String COLUMN_NAME_UPDATED  = "updated";
        public static final String COLUMN_NAME_IMAGEURL = "imageurl";
        
        public static final String COLUMN_NAME_NULLABLE = "title";
    }
    
    /* Inner class that defines the thumbs table contents */
    public static abstract class PaletteThumbEntry implements BaseColumns
    {
        public static final String TABLE_NAME             = "Thumbs";
        public static final String COLUMN_NAME_PALETTE_ID = "palette_id";
        public static final String COLUMN_NAME_THUMB      = "thumb";
    }
}
