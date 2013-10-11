package com.tzapps.tzpalette;

import com.tzapps.tzpalette.data.PaletteDataComparator.Sorter;

public final class Constants
{
    /** The key name for passing palette data id between activities */
    public static final String PALETTE_DATA_ID = "com.tzapps.tzpalette.PaletteDataId";
    /** The key name for passing palette data isAddNew flag between activities */
    public static final String PALETTE_DATA_ADDNEW = "com.tzapps.tzpalette.PaletteDataAddNew";
    /** The key name for passing paletter data sorter name between activities */
    public static final String PALETTE_DATA_SORTER_NAME = "com.tzapps.tzpalette.PaletteDataSorterName";
    
    /** The folder home of tzpalette app */
    public static final String FOLDER_HOME = "tzpalette";
    /** The sub export directory under the home folder */ 
    public static final String SUBFOLDER_EXPORT = "export";
    /** The prefix for file in tzpalette app */
    public static final String TZPALETTE_FILE_PREFIX = "MyPalette_";
    
    /** The maximum picture size in color analysis process. */
    public static final int PICTURE_ANALYSIS_MAX_SIZE = 512;
    /** The maximum thumb size stored in local db. */
    public static final int THUMB_MAX_SIZE = 1024;
    
    /** The default palette data sorter */
    public static final Sorter PALETTE_DATA_SORTER_DEFAULT = Sorter.Title;
    
    /** The default color analysis accuracy. */
    public static final String ANALYSIS_ACCURACY_DEFAULT = "NORMAL";
    /** The default color analysis type */
    public static final String ANALYSIS_COLOR_TYPE_DEFAULT = "RGB";
    /** The default thumb quality */
    public static final String CACHE_THUMB_QUALITY_DEFAULT = "MEDIUM";
    
}


