package net.ahwater.base.starter;

import net.ahwater.base.log.LogAspect;
import net.ahwater.base.log.LogHandler;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by Reeye on 2020/4/16 12:47
 * Nothing is true but improving yourself.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LogAspect.class)
public @interface EnableLog {

    /**
     * 为了确保handler被实现
     */
    Class<? extends LogHandler> handler();

}
