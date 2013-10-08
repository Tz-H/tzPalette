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
            if (leftData.isFavourite() && !rightData.isFavourite())
            {
                //left data is marked as favourite, right data not
                //so keep left data still at left
                return -1;
            }
            else if (!leftData.isFavourite() && rightData.isFavourite())
            {
                //right data is marked as favourite, left data not
                //so move right data to be left
                return 1;
            }
            else
            {
                //both left/right data are favourite or not
                //then compared them by their updated time
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

}
