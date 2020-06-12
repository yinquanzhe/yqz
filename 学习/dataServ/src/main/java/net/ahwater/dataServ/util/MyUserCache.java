package net.ahwater.dataServ.util;

import net.ahwater.dataServ.entity.bo.UserBO;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Reeye on 2019/8/12 10:00
 * Nothing is true but improving yourself.
 */
@Component
public class MyUserCache extends ConcurrentMapCache implements UserCache {

    public static final String USER_CACHE_NAME = "_USER_";

    public MyUserCache() {
        super(USER_CACHE_NAME, false);
    }

    @Override
    public UserDetails getUserFromCache(String s) {
        Object value = lookup(s);
        return (UserDetails) value;
    }

    @Override
    public void putUserInCache(UserDetails userDetails) {
        put(userDetails.getUsername(), userDetails);
    }

    @Override
    public void removeUserFromCache(String s) {
        evict(s);
    }

    /**
     * 根据用户id清除缓存
     */
    public void evictByUserid(int userid) {
        ConcurrentMap<Object, Object> nativeCache = getNativeCache();
        UserBO wrapper = new UserBO();
        Optional<Object> item = nativeCache.values().stream().filter(e -> {
            if (e instanceof UserBO) {
                UserBO bo = (UserBO) e;
                boolean res = bo.getId() == userid;
                if (res) {
                    wrapper.setUsername(bo.getUsername());
                }
                return res;
            }
            return false;
        }).findFirst();
        if (item.isPresent()) {
            evict(wrapper.getUsername());
        }
    }

}
