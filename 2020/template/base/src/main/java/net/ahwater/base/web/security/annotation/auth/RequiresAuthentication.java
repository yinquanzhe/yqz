package net.ahwater.base.web.security.annotation.auth;

import java.lang.annotation.*;

/**
 * Created by yyc on 2018/3/2 09:26.
 * Nothing is true but improving yourself.
 * 权限验证 - 需要认证(登录)
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresAuthentication {

}