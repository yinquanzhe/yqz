package net.ahwater.tender.web.service;

import net.ahwater.tender.db.entity.BeanUser;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Created by Reeye on 2019/1/7 15:49
 * Nothing is true but improving yourself.
 */
public interface UserService extends UserDetailsService {

    BeanUser findOneById(int id);

    String login(String username, String pwd);

    BeanUser register(BeanUser user);

    String refreshToken(String oldToken);

    boolean modifyPwd(int id, String newPwd);

}
