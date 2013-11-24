package com.tzapps.common.utils;

import java.util.Locale;
import java.util.regex.Pattern;


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
		return str == null ? "": str;
	}
	
	/**
	 * Check whether the target string contains the indicated sub string, and case insensitive
	 * 
	 * @param target  the target string
	 * @param subStr  the sub string
	 * @return the check result
	 */
	public static boolean containsIgnoreCase(String target, String subStr)
	{
	    target = deNull(target);
	    subStr = deNull(subStr);
	    
	    return Pattern.compile(Pattern.quote(subStr), Pattern.CASE_INSENSITIVE).matcher(target).find();
	}

}
