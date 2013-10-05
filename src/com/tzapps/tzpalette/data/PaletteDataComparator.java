package com.tzapps.tzpalette.data;

import java.util.Comparator;


public final class PaletteDataComparator
{
    /**
     * Compare palatte data by the updated time to put the data which 
     * latest updated at the top
     */
    public static class UpdatedComparator implements Comparator<PaletteData>
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
