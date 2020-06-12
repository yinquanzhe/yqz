package net.ahwater.zjk;

import cn.hutool.http.HttpUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@SpringBootTest
class ZjkApplicationTests {

	@Test
	void contextLoads() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String today = sdf.format(new Date());
		Calendar calendar = Calendar.getInstance();//获取日历实例
		calendar.setTime(sdf.parse(today));
		calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
		calendar.set(Calendar.HOUR_OF_DAY ,9);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		System.out.println(calendar.getTime());
	}
	@Test
	void test(){
		String url = "http://192.168.0.140:8086/appsys/listById";
		HashMap<String, Object> paramMap = new HashMap<>();
		paramMap.put("id", 1);

		String result= HttpUtil.get(url, paramMap);

		System.out.println(result);
	}
}
