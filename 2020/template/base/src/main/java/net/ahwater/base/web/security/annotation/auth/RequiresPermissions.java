package net.ahwater.base.web.security.annotation.auth;

import net.ahwater.base.web.security.Logical;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yyc on 2018/3/2 09:29.
 * Nothing is true but improving yourself.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@RequiresAuthentication
public @interface RequiresPermissions {

    String[] value();

    Logical logical() default Logical.AND;

}
