package net.ahwater.base.web.security;

import net.ahwater.base.web.security.annotation.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Created by Reeye on 2020/4/21 14:02
 * Nothing is true but improving yourself.
 */
@Configuration
public class CurrentUserConfigurer implements HandlerMethodArgumentResolver, WebMvcConfigurer {

    private final SecurityService<? extends User> securityService;

    @Autowired
    public CurrentUserConfigurer(SecurityService<? extends User> securityService) {
        this.securityService = securityService;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(this);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class) && User.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container, NativeWebRequest request, WebDataBinderFactory factory) {
        String token = request.getHeader(WebSecurityOnRbac.HEADER_TOKEN_NAME);
        return securityService.getUserByToken(token);
    }

}
