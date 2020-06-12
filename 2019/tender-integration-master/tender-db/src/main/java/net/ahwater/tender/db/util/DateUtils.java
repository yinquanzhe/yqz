package net.ahwater.tender.db.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Reeye on 2018/7/24 14:55
 * Nothing is true but improving yourself.
 */
public class DateUtils {

    public static String format(String formatter, Date date) {
        return new SimpleDateFormat(formatter).format(date);
    }

    //判断选择的日期是否是本周
    public static boolean isThisWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(new Date(time));
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        if (paramWeek == currentWeek) {
            return true;
        }
        return false;
    }

    //判断选择的日期是否是今天
    public static boolean isToday(long time) {
        return isThisTime(time, "yyyy-MM-dd");
    }

    //判断选择的日期是否是本月
    public static boolean isThisMonth(long time) {
        return isThisTime(time, "yyyy-MM");
    }

    private static boolean isThisTime(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        if (param.equals(now)) {
            return true;
        }
        return false;
    }

    /**
     * date2比date1多的天数
     */
    public static int differentDays(Date date1,Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2) {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++) {
                if(i%4==0 && i%100!=0 || i%400==0) {    //闰年
                    timeDistance += 366;
                } else {   //不是闰年
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2-day1) ;
        } else {
            return day2-day1;
        }
    }

    /**
     * 转换时间类型为 今日17:30 昨日17:30 7月15日 17:30
     * @param time
     * @return
     */
    public static String formatTime(Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar c = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        c.setTime(time);
        String date;
        if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR) && c.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
            date = "今日";
        } else {
            now.add(Calendar.DATE, -1);
            if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR) && c.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
                date = "昨日";
            } else {
                date = c.get(Calendar.MONTH) + "月" + c.get(Calendar.DATE) + "日";
            }
        }
        return date + sdf.format(time);
    }

    public static String addParams2Url(String url, Map<String, ?> params) {
        return params.keySet()
                .stream()
                .reduce(url, (pre, curr) -> pre + (pre.equals(url) ? "?" : "&") + curr + "=" + params.get(curr));
    }

    public static void main(String[] args) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = sdf.parse("2018-08-01 15:30");
        System.out.println(formatTime(date));

        String url = "http://baidu.com";
        Map<String, Object> map = new HashMap<>();
        map.put("a", "123");
        map.put("b", "43421");
        map.put("c", "12213");
        System.out.println(addParams2Url(url, map));
    }

}
