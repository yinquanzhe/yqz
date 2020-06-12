package net.ahwater.base.web.security;

import java.util.List;

/**
 * Created by Reeye on 2020/4/20 9:39
 * Nothing is true but improving yourself.
 */
public interface User {

    String identity();

    default boolean disabled() {
        return false;
    }

    List<String> getRoles();
    List<String> getPermissions();

}
