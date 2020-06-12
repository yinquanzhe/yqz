package net.ahwater.base.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Reeye on 2020/4/16 11:11
 * Nothing is true but improving yourself.
 */
@Slf4j
@Order
@Aspect
@Configuration
public class LogAspect {

    private static final String REPLACEMETNT = "{}";

    private final LogHandler handler;

    @Autowired
    public LogAspect(LogHandler handler) {
        this.handler = handler;
    }

    @Pointcut("@annotation(net.ahwater.base.log.Log)")
    public void doPerform() {
    }

    @AfterReturning(pointcut = "doPerform()", returning = "result")
    public void determine(JoinPoint jp, Object result) {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        if (null != signature) {
            Method method = signature.getMethod();
            if (null != method && method.isAnnotationPresent(Log.class)) {
                Log logAnno = method.getAnnotation(Log.class);
                String[] logContent = logAnno.content();
                int length = logContent.length;
                if (length > 0) {
                    LogEntity entity = new LogEntity();
                    entity.setType( logAnno.type());
                    entity.setTime(new Date());
                    entity.setContent(logContent[0]);
                    if (length > 1) {
                        Map<String, Object> variable = new HashMap<>();
                        variable.put("RESULT", result);
                        int index = 1;
                        while (entity.getContent().contains(REPLACEMETNT) || index < length) {
                            try {
                                String value = "" +  AspectSupportUtils.getKeyValue(jp, logContent[index++], variable);
                                entity.setContent(entity.getContent().replaceFirst(Pattern.quote(REPLACEMETNT), value));
                            } catch (Exception e) {
                                log.error("出错:{}", e.getMessage());
                            }
                        }
                    }
                    handler.handle(entity);
                }
            }
        }
    }

}
