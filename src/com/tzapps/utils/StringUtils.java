package com.tzapps.utils;


public class StringUtils
{
    /**
     * Check whether the indicated str is empty
     * 
     * @param str the string to check
     * @return true if it is empty, otherwise false
     */
	public static boolean isEmpty(String str)
	{
		return "".equals( str ) || str == null;
	}
	
	/**
	 * Convert a (possible) null string to "" if necessary
	 * 
	 * @param str a (possible) null string
	 * @return "" if the string is null, or no change
	 */
	public static String deNull(String str)
	{
		return str == null? "": str;
	}

}
