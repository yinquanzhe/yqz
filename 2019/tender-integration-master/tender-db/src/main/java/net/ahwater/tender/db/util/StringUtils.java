package net.ahwater.tender.db.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Reeye on 2018/7/9 11:32
 * Nothing is true but improving yourself.
 */
public class StringUtils {

    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    /**
     * unicode转中文
     */
    public static String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }

    public static String urlDecode(String str, String enc) {
        try {
            return URLDecoder.decode(str, enc);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
