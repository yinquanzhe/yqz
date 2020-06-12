package net.ahwater.tender.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.ahwater.tender.db.entity.BeanUser;
import net.ahwater.tender.db.mapper.UserMapper;
import net.ahwater.tender.web.service.UserService;
import net.ahwater.tender.web.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by Reeye on 2019/1/7 14:57
 * Nothing is true but improving yourself.
 */
@SuppressWarnings({"SpringJavaInjectionPointsAutowiringInspection", "SpringJavaAutowiredFieldsWarningInspection"})
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("loadUserByUsername>>>{}", username);
        BeanUser user = userMapper.selectOne(new LambdaQueryWrapper<BeanUser>().eq(BeanUser::getUsername, username));
//        BeanUser user = userMapper.selectByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("该用户不存在: '%s'.", username));
        } else {
            return user;
        }
    }

    @Override
    public BeanUser findOneById(int id) {
        return userMapper.selectById(id);
    }

    @Override
    public String login(String username, String pwd) {
        log.debug("login: {}", username);
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, pwd);
        Authentication authentication = authenticationManager.authenticate(upToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = loadUserByUsername(username);
        return jwtUtils.generateToken(userDetails);
    }

    @Override
    public BeanUser register(BeanUser user) {
        String username = user.getUsername();
        if (userMapper.selectOne(new LambdaQueryWrapper<BeanUser>().eq(BeanUser::getUsername, username)) != null) {
            throw new RuntimeException("用户名已存在");
        }
        user.setPwd(passwordEncoder.encode(user.getPwd()))
                .setRoleId(4)
                .setUnitId(0)
                .setCreateTime(new Date());
        int res = userMapper.insert(user);
        if (res != 1) {
            throw new RuntimeException("用户注册失败: " + user.getUsername());
        } else {
            return user;
        }
    }

    @Override
    public String refreshToken(String oldToken) {
        String token = oldToken.substring("Bearer ".length());
        if (!jwtUtils.isTokenExpired(token)) {
            return jwtUtils.refreshToken(token);
        }
        throw new RuntimeException("刷新Token失败");
    }

    @Override
    public boolean modifyPwd(int id, String newPwd) {
        BeanUser user = new BeanUser().setId(id).setPwd(passwordEncoder.encode(newPwd));
        return userMapper.updateById(user) == 1;
    }

}
