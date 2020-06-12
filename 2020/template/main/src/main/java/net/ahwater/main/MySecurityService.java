package net.ahwater.main;

import net.ahwater.base.web.security.SecurityService;
import net.ahwater.base.web.security.User;
import org.springframework.stereotype.Component;
/**
 * Created by Reeye on 2020/4/20 10:25
 * Nothing is true but improving yourself.
 */
@Component
public class MySecurityService implements SecurityService {

    @Override
    public User getUserByToken(String token) {
        if ("1".equals(token)) {
            return new MyUser("张三");
        }
        return null;
    }

    @Override
    public String generateToken(User user) {
        if (user.identity().equals("张三")) {
            return "1";
        }
        return null;
    }

}
