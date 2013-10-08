package com.tzapps.tzpalette.ui.dialog;

public enum PaletteDataOption
{
    View("View"),
    Edit("Edit"),
    Rename("Rename"),
    Delete("Delete");
    
    private String name;    
    
    private PaletteDataOption(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public static PaletteDataOption fromString(String name)
    {
        if (name != null)
        {
            for (PaletteDataOption option : PaletteDataOption.values())
            {
                if (name.equalsIgnoreCase(option.name))
                    return option;
            }
        }
        
        return null;
    }
}
