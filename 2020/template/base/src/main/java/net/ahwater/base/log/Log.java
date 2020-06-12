package net.ahwater.base.log;

import java.lang.annotation.*;

/**
 * Created by Reeye on 2020/4/16 13:27
 * Nothing is true but improving yourself.
 * 建议放在service层
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Log {

    LogType type();

    String[] content();

}
