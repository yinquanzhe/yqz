package net.ahwater.dataServ.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.ahwater.dataServ.entity.po.UserPO;
import net.ahwater.dataServ.entity.vo.R;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends IService<UserPO>, UserDetailsService{
    R login(String username, String password) throws Exception;

    R list();

    R addUser(UserPO user);
}
