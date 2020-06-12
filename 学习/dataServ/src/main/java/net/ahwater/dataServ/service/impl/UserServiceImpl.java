package net.ahwater.dataServ.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.ahwater.dataServ.dao.UserDao;
import net.ahwater.dataServ.entity.bo.UserBO;
import net.ahwater.dataServ.entity.po.UserPO;
import net.ahwater.dataServ.entity.vo.R;
import net.ahwater.dataServ.entity.vo.UserVO;
import net.ahwater.dataServ.service.UserService;
import net.ahwater.dataServ.util.DozerUtil;
import net.ahwater.dataServ.util.JwtUtils;
import net.ahwater.dataServ.util.MyUserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
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
 * Created by yqz on 2020/3/26
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserDao,UserPO> implements UserService{

    @Autowired
    private MyUserCache userCache;

    @Autowired
    private DozerUtil dozerUtil;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${project-config.passwordSalt}")
    private String aesPasswordSalt;

    @Override
    public R login(String username, String password) throws Exception {
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username,
                passwordEncoder.encode(password));
        Authentication authentication = authenticationManager.authenticate(upToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserBO userBO = (UserBO) authentication.getPrincipal();
        UserVO userVO = dozerUtil.convert(userBO, UserVO.class);
        userVO.setToken(jwtUtils.generateToken(userBO));
        return R.ok("登录成功",userVO);
    }

    @Override
    public R list() {
        return R.ok("查询成功",baseMapper.selectList(null));
    }

    @Cacheable(cacheNames = MyUserCache.USER_CACHE_NAME,  key = "#user.username")
    @Override
    public R addUser(UserPO user) {
        UserPO po = user;
        po.setCreateTime(new Date());
        boolean res = save(po);
       return res?R.ok("新增成功"):R.error("新增失败");
    }

    @Override
    public UserBO loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userBoCache = userCache.getUserFromCache(username);
        if(userBoCache !=null){
            return (UserBO)userBoCache;
        } else {
            UserPO userPO = getOne(new LambdaQueryWrapper<UserPO>().eq(UserPO::getUsername, username));
            if (userPO == null) {
                throw new UsernameNotFoundException(String.format("该用户不存在: '%s'.", username));
            } else {
                UserBO userBO = dozerUtil.convert(userPO, UserBO.class);
            /*    RolePO rolePO =roleDao.selectById(userBO.getRoleId());
                userBO.setRoles(dozerUtil.convert(rolePO,RoleVO.class));*/
                userCache.putUserInCache(userBO);
                return userBO;
            }
        }
    }
}