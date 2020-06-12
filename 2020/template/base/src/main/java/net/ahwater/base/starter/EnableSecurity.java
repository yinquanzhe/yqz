package net.ahwater.base.starter;

import net.ahwater.base.web.security.CurrentUserConfigurer;
import net.ahwater.base.web.security.SecurityService;
import net.ahwater.base.web.security.WebSecurityOnRbac;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by Reeye on 2020/4/20 9:17
 * Nothing is true but improving yourself.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ WebSecurityOnRbac.class, CurrentUserConfigurer.class})
public @interface EnableSecurity {

    Class<? extends SecurityService> securityService();

}
