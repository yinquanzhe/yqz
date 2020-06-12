package net.ahwater.tender.wx;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Reeye on 2018/7/6 16:09
 * Nothing is true but improving yourself.
 */
public class NormalTest {

    @Test
    public void test0() {
        String str = "\\u5B89\\u5FBD\\u5DE5\\u4E1A\\u7ECF\\u6D4E\\u804C\\u4E1A\\u6280\\u672F\\u5B66\\u96622018-2019\\u5B66\\u5E74\\uFF08\\u79CB\\u6625\\u5B63\\uFF09\\u6559\\u6750\\u91C7\\u8D2D\\u9879\\u76EE\\u516C\\u5F00\\u62DB\\u6807\\u516C\\u544A";
        System.out.println(unicodeToString(str));
//        for (int i = 0; i < 10; i++) {
//            System.out.println(UUID.randomUUID().toString().replaceAll("-", "").length());
//        }
        String a = null;
        System.out.println("aa" + a);

        String b = null;
        System.out.println(null instanceof String);
        System.out.println("-------------------------------");
        outer: for (int i = 0; i < 10; i++) {
            System.out.println("i========" + i);
            for (int j = 0; j < 5; j++) {
                System.out.println("j========" + j);
                if (j == 2) {
                    break outer;
                }
            }
        }
        String ss = "abc123";
        System.out.println(ss.replaceAll("$0", ""));
        System.out.println(ss.replaceAll(Matcher.quoteReplacement("$a"), ""));
        System.out.println(ss.replaceAll("(\\d)(\\d+)", "$1"));
        System.out.println(ss.replaceAll("\\d", Matcher.quoteReplacement("$1")));
    }

    public static String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            String group = matcher.group(2);
            ch = (char) Integer.parseInt(group, 16);
            String group1 = matcher.group(1);
            str = str.replace(group1, ch + "");
        }
        return str;
    }

}
