package net.ahwater.dataServ.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser{

}
