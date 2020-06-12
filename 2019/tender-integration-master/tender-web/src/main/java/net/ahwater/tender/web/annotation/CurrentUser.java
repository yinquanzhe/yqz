package net.ahwater.tender.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yyc on 2018/11/24 10:09.
 * Nothing is true but improving yourself.
 * 标记当前用户
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {

}
