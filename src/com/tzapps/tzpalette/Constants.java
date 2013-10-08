package com.tzapps.tzpalette;

public final class Constants
{
    /** The key name for passing palette data id between activities */
    public static final String PALETTE_DATA_ID = "com.tzapps.tzpalette.PaletteDataId";
    /** The key name for passing palette data isAddNew flag between activities */
    public static final String PALETTE_DATA_ADDNEW = "com.tzapps.tzpalette.PaletteDataAddNew";
    
    /** The folder home of tzpalette app */
    public static final String FOLDER_HOME = "tzpalette";
    /** The prefix for file in tzpalette app */
    public static final String TZPALETTE_FILE_PREFIX = "MyPalette";
    
    /** The maximum picture width in color analysis process. */
    public static final int PICTURE_ANALYSIS_MAX_WIDTH  = 512;
    /** The maximum picture height in color analysis process. */
    public static final int PICTURE_ANALYSIS_MAX_HEIGHT = 512;
    
    /** The maximum thumb width stored in local db. */
    public static final int THUMB_WIDTH_MAX = 1024;
    /** The maximum thumb height stored in local db. */
    public static final int THUMB_HEIGHT_MAX = 1024;
}
