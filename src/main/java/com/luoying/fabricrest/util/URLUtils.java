/**
 * 
 */
package com.luoying.fabricrest.util;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

/**
 * @author Administrator
 *
 */
public class URLUtils {
	
	
	/**
	 * 获取URL中的文件名字
	 * @param url
	 * @return
	 */
	public static String getURLFileName(URL url) {
		return getURLFileName(url.getPath());
	}
	
	/**
	 * 获取URL中的文件名字
	 * @param url
	 * @return
	 */
	public static String getURLFileName(String url) {
    	String path = url;
    	if (StringUtils.isEmpty(path)) {
    		return "";
    	}
       	int index = path.lastIndexOf('/');
       	if (index == -1) {
       		return "";
       	}
       	else {
       	    String lastPath = path.substring(index + 1);
       	    if (lastPath.length() <= 0) {
       	    	return lastPath;
       	    }
       	    //去掉#;?
       	    else {	
       	    	lastPath = StringUtils.substringBefore(lastPath, "?");
       	    	lastPath = StringUtils.substringBefore(lastPath, ";");
       	    	lastPath = StringUtils.substringBefore(lastPath, "#");
       	    	//可能外部自己decode URL好些
       	    	return lastPath;
       	    }
       	    
       	}
	}
	
	/**
	 * URL提取文件扩展名
	 * @param url
	 * @return
	 */
	public static String getURLFileExtension(URL url) {
		return getURLFileExtension(url.getPath());
	}
	
	/**
	 * URL提取文件扩展名
	 * @param url
	 * @return
	 */
	public static String getURLFileExtension(String url) {
		String fileName = getURLFileName(url);
		if (StringUtils.isEmpty(fileName)) {
			return fileName;
		}
		else {
			return FilenameUtils.getExtension(fileName);
		}
	}
	
	/**
	 * URL编码
	 * @param src
	 * @return
	 */
	public static String urlEncode(String src) {
		return urlEncode(src, "UTF-8");
	}
	
	/**
	 * URL编码
	 * @param src
	 * @param charset
	 * @return
	 */
	public static String urlEncode(String src, String charset) {
		try {
			return URLEncoder.encode(src, charset);
		} 
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return src;
		}
	}
	
	/**
	 * URL解码
	 * @param str
	 * @return
	 */
	public static String urlDecode(String str) {
		return urlDecode(str, "UTF-8");
	}
	
	/**
	 * URL解码
	 * @param str
	 * @param charset
	 * @return
	 */
	public static String urlDecode(String str, String charset) {
		try {
		    return URLDecoder.decode(str, charset);
		}
		catch (Exception e) {
			e.printStackTrace();
			return str;
		}
	}
	
	/**
	 * URL查询的字符串,没有?
	 * @param params
	 * @param urlEncode
	 * @return
	 */
	public static String getURLQueryString(Map<String,String> params, boolean urlEncode) {
		return getURLQueryString(params, false, urlEncode, "UTF-8");
	}
	
	/**
	 * URL查询的字符串，没有?
	 * @param params
	 * @param ignoreBlank
	 * @param urlEncode
	 * @param charset
	 * @return
	 */
    public static String getURLQueryString(Map<String, String> params, boolean ignoreBlank, boolean urlEncode, String charset) {
		if (params == null || params.size() <= 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();
		int count = 0;
		while (iter.hasNext()) {
			Map.Entry<String, String> entry = iter.next();
			String key = entry.getKey();
			String value = entry.getValue();
			//String value = "";
			if (value == null) {
				//value = valueObject.toString();
				value = "";
			}
			if (ignoreBlank && StringUtils.isBlank(value)) {
				continue;
			}
			if (urlEncode) {
				value = urlEncode(value, charset);
			}
			
			if (count > 0) {
				sb.append('&');
			}
			sb.append(key).append('=').append(value);
			++count;
		}
		return sb.toString();
	}
    
    /**
     * Parse query string
     * @param queryString
     * @return
     */
    public static Map<String,List<String>> parseURLQueryString(String queryString) {
        if (StringUtils.isBlank(queryString)) {
            return new LinkedHashMap<>(0);
        }
        String[] pairs = StringUtils.split(queryString, '&');
        if (pairs == null || pairs.length <= 0) {
            return new LinkedHashMap<>(0);
        }
        Map<String,List<String>> map = new LinkedHashMap<>(pairs.length);
        for (String pair : pairs) {
            if (StringUtils.isBlank(pair)) {
                continue;
            }
            int index = pair.indexOf('=');
            String key = null;
            String value = null;
            if (index == -1) {
                //FIXME Ignore it for invalid format?
                key = pair;
                value = "";
            }
            else {
                key = pair.substring(0, index);
                value = pair.substring(index + 1);
            }
            List<String> values = map.get(key);
            if (values == null) {
                values = new ArrayList<>(4);
                map.put(key, values);
            }
            values.add(value);
        }
        return map;
    }
	

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		URL url = new URL("http://localhost/aa/bb/c.txt?a=1;b=2");
		System.out.println(URLUtils.getURLFileExtension(url));

	}

}
