package net.ahwater.base.web.security.annotation;

import java.lang.annotation.*;

/**
 * 标记当前登录用户
 * 用于controller层action参数中
 * Created by Reeye on 2020/4/21 13:58
 * Nothing is true but improving yourself.
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {
}
