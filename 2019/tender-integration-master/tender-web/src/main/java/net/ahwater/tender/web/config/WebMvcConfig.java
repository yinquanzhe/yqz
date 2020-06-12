package net.ahwater.tender.web.config;

import lombok.extern.slf4j.Slf4j;
import net.ahwater.tender.db.entity.BeanUser;
import net.ahwater.tender.web.annotation.CurrentUser;
import net.ahwater.tender.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Created by Reeye on 2018/11/23 20:01
 * Nothing is true but improving yourself.
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private UserService userService;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(CurrentUser.class) && parameter.getParameterType().equals(BeanUser.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                Object principal = authentication.getPrincipal();
                if (principal instanceof BeanUser) {
                    return principal;
                } else if (principal instanceof String && !"anonymousUser".equals(principal)) {
                    return userService.loadUserByUsername("" + principal);
                } else {
                    return new BeanUser();
                }
            }
        });
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {

    }

}
