package net.ahwater.base.web.security;

import net.ahwater.base.web.security.annotation.auth.ClearRequire;
import net.ahwater.base.web.security.annotation.auth.RequiresAuthentication;
import net.ahwater.base.web.security.annotation.auth.RequiresPermissions;
import net.ahwater.base.web.security.annotation.auth.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 基于RBAC的controller层请求拦截
 * Created by Reeye on 2020/4/17 14:43
 * Nothing is true but improving yourself.
 */
@Configuration
public class WebSecurityOnRbac extends HandlerInterceptorAdapter implements WebMvcConfigurer {

    static final String HEADER_TOKEN_NAME = "token";
    private static final String ANNOTATION_PACKAGE = RequiresAuthentication.class.getPackage().getName();
    private final List<Pattern> PATTERNS = new ArrayList<>();

    private final SecurityService<? extends User> securityService;

    @Autowired
    public WebSecurityOnRbac(SecurityService<? extends User> securityService) {
        this.securityService = securityService;
        List<String> patternStrList = securityService.configPermitUrlPatterns();
        patternStrList.forEach(e -> PATTERNS.add(Pattern.compile(e)));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//        response.addHeader("Access-Control-Expose-Headers", HEADER_TOKEN_NAME);
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            String uri = request.getRequestURI();
            if (PATTERNS.stream().anyMatch(e -> e.matcher(uri).matches())) {
                return true;
            }
            List<Annotation> annotations = new ArrayList<>();
            annotations.addAll(Arrays.asList(handlerMethod.getBeanType().getDeclaredAnnotations()));
            annotations.addAll(Arrays.asList(handlerMethod.getMethod().getDeclaredAnnotations()));
            annotations = annotations.stream()
                    .filter(e -> e.annotationType().getPackage().getName().equals(ANNOTATION_PACKAGE))
                    .collect(Collectors.toList());
            if (!annotations.isEmpty() && annotations.stream().noneMatch(e -> e instanceof ClearRequire)) {
                String token = request.getHeader(HEADER_TOKEN_NAME);
                if (StringUtils.isEmpty(token)) {
                    throw new RuntimeException("empty token");
                }
                User user = securityService.getUserByToken(token);
                if (user == null) {
                    throw new RuntimeException("invalid token");
                }
                if (user.disabled()) {
                    throw new RuntimeException("disabled user");
                }
                boolean flag = annotations.stream().anyMatch(e -> {
                    if (e instanceof RequiresRoles)  {
                        RequiresRoles anno = (RequiresRoles) e;
                        Stream<String> stream = Stream.of(anno.value());
                        return anno.logical() == Logical.AND ? stream.allMatch(v -> user.getRoles().contains(v)) :
                                stream.anyMatch(v ->user.getRoles().contains(v));
                    }
                    if (e instanceof RequiresPermissions)  {
                        RequiresPermissions anno = (RequiresPermissions) e;
                        Stream<String> stream = Stream.of(anno.value());
                        return anno.logical() == Logical.AND ? stream.allMatch(v -> user.getPermissions().contains(v)) :
                                stream.anyMatch(v ->user.getPermissions().contains(v));
                    }
                    return false;
                });
                if (!flag) {
                    throw new RuntimeException("Forbidden");
                }
            }
        }
        return true;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this);
    }

}
