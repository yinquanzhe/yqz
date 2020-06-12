package net.ahwater.base.web.security;

import java.util.Arrays;
import java.util.List;

/**
 * 这里用来配置忽略验证的请求URL，以及user、token的相互查找
 * 可以添加用户数据的缓存
 * Created by Reeye on 2020/4/20 9:38
 * Nothing is true but improving yourself.
 */
public interface SecurityService<T extends User> {

    /**
     * 注意：这里需要正则字符串
     */
    default List<String> configPermitUrlPatterns() {
        return Arrays.asList("/", "/user/login",
                "/swagger.*" , "/webjars.*", "/v2/api-docs");
    }

    T getUserByToken(String token);

    String generateToken(T user);

}