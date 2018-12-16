/*
 * Copyright (c) 2015, All rights reserved.
 */
package com.whlylc.fabricrest.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * String utility functions
 * @author Zeal
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
	
	/**
	 * Generate UUID 
	 */
	public static String generateUUID() {
		return StringUtils.remove(UUID.randomUUID().toString(), '-');
	}
	
	/**
	 * Convenience method to return a String array as a delimited (e.g. CSV)
	 * String. E.g. useful for {@code toString()} implementations.
	 * @param arr the array to display
	 * @param delim the delimiter to use (probably a ",")
	 * @return the delimited String
	 */
	public static String arrayToDelimitedString(Object[] arr, String delim) {
		if (arr == null || arr.length <= 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) {
				sb.append(delim);
			}
			sb.append(arr[i]);
		}
		return sb.toString();
	}

	/**
	 * Convenience method to return a Collection as a delimited (e.g. CSV)
	 * String. E.g. useful for {@code toString()} implementations.
	 * @param coll the Collection to display
	 * @param delimiter the delimiter to use (probably a ",")
	 * @return the delimited String
	 */
	public static String collectionToDelimitedString(Collection<?> coll, String delimiter) {
		return collectionToDelimitedString(coll, delimiter, "", "");
	}	
	
	
	/**
	 * Convenience method to return a Collection as a delimited (e.g. CSV)
	 * String. E.g. useful for {@code toString()} implementations.
	 * @param coll the Collection to display
	 * @param delim the delimiter to use (probably a ",")
	 * @param prefix the String to start each element with
	 * @param suffix the String to end each element with
	 * @return the delimited String
	 */
	public static String collectionToDelimitedString(Collection<?> coll, String delim, String prefix, String suffix) {
		if (coll == null || coll.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		Iterator<?> it = coll.iterator();
		while (it.hasNext()) {
			sb.append(prefix).append(it.next()).append(suffix);
			if (it.hasNext()) {
				sb.append(delim);
			}
		}
		return sb.toString();
	}
	
	/**
	 * Tokenize the given String into a String array via a StringTokenizer.
	 * Trims tokens and omits empty tokens.
	 * <p>The given delimiters string is supposed to consist of any number of
	 * delimiter characters. Each of those characters can be used to separate
	 * tokens. A delimiter is always a single character; for multi-character
	 * delimiters, consider using {@code delimitedListToStringArray}
	 * @param str the String to tokenize
	 * @param delimiters the delimiter characters, assembled as String
	 * (each of those characters is individually considered as delimiter).
	 * @return an array of the tokens
	 */
	public static List<String> tokenizeToStringList(String str, String delimiters) {
		return tokenizeToStringList(str, delimiters, true, true);
	}
	
	/**
	 * Tokenize the given String into a String array via a StringTokenizer.
	 * <p>The given delimiters string is supposed to consist of any number of
	 * delimiter characters. Each of those characters can be used to separate
	 * tokens. A delimiter is always a single character; for multi-character
	 * delimiters, consider using {@code delimitedListToStringArray}
	 * @param str the String to tokenize
	 * @param delimiters the delimiter characters, assembled as String
	 * (each of those characters is individually considered as delimiter)
	 * @param trimTokens trim the tokens via String's {@code trim}
	 * @param ignoreEmptyTokens omit empty tokens from the result array
	 * (only applies to tokens that are empty after trimming; StringTokenizer
	 * will not consider subsequent delimiters as token in the first place).
	 * @return an array of the tokens ({@code null} if the input String
	 * was {@code null})
	 */
	public static List<String> tokenizeToStringList(
			String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

		if (str == null) {
			return new ArrayList<>(0);
		}
		StringTokenizer st = new StringTokenizer(str, delimiters);
		List<String> tokens = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if (!ignoreEmptyTokens || token.length() > 0) {
				tokens.add(token);
			}
		}
		return tokens;
	}
	
	/**
	 * Camel string to delimeted string like underline '_'
	 * @param str
	 * @param seperator
	 * @return
	 */
	public static String camelToDelimitedString(String str, String seperator) {
		if (StringUtils.isBlank(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder((int)((double)str.length() * 1.5));
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)){
                sb.append(seperator);
                sb.append(Character.toLowerCase(c));
            }
            else{
                sb.append(c);
            }
        }
        return sb.toString();		
	}
	
	/**
	 * Convert to camel format
	 * @param str
	 * @param seperator like '_'
	 * @return
	 */
	public static String toCamelString(String str, char seperator) {
		if (StringUtils.isBlank(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str.length());
		boolean toUppercase = false;
		for (int i = 0; i < str.length(); ++i) {
			char c = str.charAt(i);
			//Convert next char to uppercase
			if (c == seperator) {
				toUppercase = true;
				continue;
			}
			if (toUppercase) {
			    sb.append(Character.toUpperCase(c));
			    toUppercase = false;
			}
			else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	/**@deprecated 迁移到URLUtils */
	public static String urlEncode(String src) {
		return urlEncode(src, "UTF-8");
	}
	
	/**@deprecated 迁移到URLUtils */
	public static String urlEncode(String src, String charset) {
		try {
			return URLEncoder.encode(src, charset);
		} 
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return src;
		}
	}

    
	 public static boolean isMobile(String mobile) {
		// /^(13[0-9]|15[0-35-9]|18[0-9]|14[57]|17[0678])\d{8}$/;
		//Pattern p = Pattern.compile("^1(3[0-9]|5[0-9]|8[0-9]|4[57]|7[0678])\\d{8}$");
		Pattern p = Pattern.compile("^1(3[0-9]|5[0-9]|8[0-9]|4[0-9]|7[0-9])\\d{8}$");
		return p.matcher(mobile).matches();
	 }
	 
	 public static String maskString(String str, int fromIndex, int size, char replaceChar) {
		 if (str == null || fromIndex < 0 || fromIndex > str.length()-1) {
			 return str;
		 }
		 StringBuilder sb = new StringBuilder(str.length());
		 sb.append(str.substring(0,fromIndex));
		 int endIndex = fromIndex+size-1;
		 if (endIndex > str.length()-1) {
			 endIndex = str.length() - 1;
		 }
		 for (int i = fromIndex; i <= endIndex; ++i) {
			 sb.append(replaceChar);
		 }
		 if (endIndex < str.length()-1) {
			 sb.append(str.substring(endIndex+1));
		 }
		 return sb.toString();
	 }
	 
	public static boolean isEmail(String email) {
		//String regex = "[a-zA-Z0-9_-]+@\\w+\\.[a-z]+(\\.[a-z]+)?";  // 匹配email的正则  
		//var regex = /^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/;
		String regex = "[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+";
		Pattern p = Pattern.compile(regex);
		return p.matcher(email).matches();
	}
	
	public static String generateRandomNumber(int length) { // length表示生成字符串的长度
		String base = "0123456789";
		return generateRandomString(base, length);
	} 
	
	public static String generateRandomString(int length) { // length表示生成字符串的长度
		String base = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		return generateRandomString(base, length);
	} 
	
	public static String generateRandomString(String base, int length) { // length表示生成字符串的长度
		Random random = new Random(System.currentTimeMillis());
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	} 

	
}
