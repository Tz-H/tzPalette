package com.tzapps.tzpalette.data;

import java.util.Comparator;


public final class PaletteDataComparator
{
    /**
     * Compare palatte data by the updated time to put the latest updated 
     * palette data at the top
     */
    public static class UpdatedTime implements Comparator<PaletteData>
    {
        @Override
        public int compare(PaletteData leftData, PaletteData rightData)
        {
            long leftUpdated = leftData.getUpdated();
            long rightUpdated = rightData.getUpdated();
            
            if (leftUpdated > rightUpdated)
                return -1;
            else if (leftUpdated < rightUpdated)
                return 1;
            else 
                return 0;
        }
    }

}
