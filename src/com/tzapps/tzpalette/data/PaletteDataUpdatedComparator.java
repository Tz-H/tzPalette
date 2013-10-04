package com.tzapps.tzpalette.data;

import java.util.Comparator;


public class PaletteDataUpdatedComparator implements Comparator<PaletteData>
{
    @Override
    public int compare(PaletteData leftData, PaletteData rightData)
    {
        /* compare palatte data's update time, and put the data which 
         * updated recently at left side
         */
        return (int)(rightData.getUpdated() - leftData.getUpdated());
    }

}
