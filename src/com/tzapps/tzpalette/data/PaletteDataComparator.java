package com.tzapps.tzpalette.data;

import java.util.Comparator;


public final class PaletteDataComparator
{
    public enum Sorter
    {
        /** Sort the palette data by its title in A to Z order */
        Title("Title", new Title()),
        /** Sort the palette data by its updated time */
        UpdatedTime("Updated", new UpdatedTime());
        
        private String name;
        private Comparator<PaletteData> comparator;
        
        private Sorter(String name, Comparator<PaletteData> comparator)
        {
            this.name = name;
            this.comparator = comparator;
        }
        
        public Comparator<PaletteData> getComparator()
        {
            return comparator;
        }
        
        public String getName()
        {
            return name;
        }
        
        public static Sorter fromString(String name)
        {
            if (name != null)
            {
                for (Sorter sorter : Sorter.values())
                {
                    if (name.equalsIgnoreCase(sorter.name))
                        return sorter;
                }
            }

            return null;
        }
    }
    
    /**
     * Compare palette data by the title
     */
    private static class Title implements Comparator<PaletteData>
    {
        @Override
        public int compare(PaletteData leftData, PaletteData rightData)
        {
            int ret = compareFavourite(leftData, rightData);
            
            if (ret != 0)
            {
                return ret;
            }
            else
            {
                String leftTitle = leftData.getTitle();
                String rightTitle = rightData.getTitle();
                        
                return leftTitle.compareToIgnoreCase(rightTitle);
            }
        }
    }
    
    /**
     * Compare palatte data by the updated time and put the latest updated 
     * palette data at the top
     */
    private static class UpdatedTime implements Comparator<PaletteData>
    {
        @Override
        public int compare(PaletteData leftData, PaletteData rightData)
        {
            int ret = compareFavourite(leftData, rightData);
            
            if (ret != 0)
            {
                return ret;
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
    
    private static int compareFavourite(PaletteData leftData, PaletteData rightData)
    {
        if (leftData.isFavourite() && !rightData.isFavourite())
            //left data is marked as favourite, right data not
            //so keep left data still at left
            return -1;
        else if (!leftData.isFavourite() && rightData.isFavourite())
            //right data is marked as favourite, left data not
            //so move right data to be left
            return 1;
        else
            //both left/right data are favourite or not
            //then return 0 to compare other criteria
            return 0;
    }

}
