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
 * @date 2015-12-14
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

	
    public static void main(String[] args) throws Exception {
		//System.out.println(StringUtils.generateUUID());
    	//System.out.println(maskString("133", 4, 5, '*'));
    	//System.out.println(StringUtils.isEmail("6a_aaa6@163,com"));
		System.out.println(System.currentTimeMillis());
//		long l = 1531293897690L;
		String str = "data:image/jpeg;base64,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";
		String regex = "^data:image/[\\w]+;base64,.+$";
		boolean match = Pattern.matches(regex, str);
		System.out.println(match);
		System.out.println(StringUtils.isMobile("17300000000"));
		

	}
	
}
