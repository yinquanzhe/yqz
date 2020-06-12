package net.ahwater.main;

import net.ahwater.base.log.Log;
import net.ahwater.base.log.LogType;
import org.springframework.stereotype.Service;

/**
 * Created by Reeye on 2020/4/16 13:50
 * Nothing is true but improving yourself.
 */
@Service
public class TestService {

    @Log(type = LogType.ADD, content = {"用户{}{}了一次，结果{}了", "#user.name", "#thing", "#RESULT"})
    public String doSomething(MyUser user, String thing) {
        System.out.println("执行doSomething()");
        return "成功";
    }

}
