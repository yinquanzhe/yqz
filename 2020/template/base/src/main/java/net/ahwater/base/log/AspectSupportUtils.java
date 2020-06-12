package net.ahwater.base.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;

public class AspectSupportUtils {

    private static ExpressionEvaluator evaluator = new ExpressionEvaluator();

    public static Object getKeyValue(JoinPoint joinPoint, String keyExpression, @Nullable Map<String, Object> contextVariable) {
        return getKeyValue(joinPoint.getTarget(),
                joinPoint.getTarget().getClass(),
                ((MethodSignature) joinPoint.getSignature()).getMethod(),
                joinPoint.getArgs(),
                keyExpression,
                contextVariable);
    }

    private static Object getKeyValue(Object object, Class<?> clazz, Method method, Object[] args , String keyExpression, @Nullable Map<String, Object> contextVariable) {
        if (StringUtils.hasText(keyExpression)) {
            EvaluationContext evaluationContext = evaluator.createEvaluationContext(object, clazz, method, args);
            if (contextVariable != null) {
                contextVariable.forEach(evaluationContext::setVariable);
            }
            AnnotatedElementKey methodKey = new AnnotatedElementKey(method, clazz);
            return evaluator.key(keyExpression, methodKey, evaluationContext);
        }
        return SimpleKeyGenerator.generateKey(args);
    }

}
