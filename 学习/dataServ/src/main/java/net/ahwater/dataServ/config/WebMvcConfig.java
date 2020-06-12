package net.ahwater.dataServ.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.ahwater.dataServ.annotation.CurrentUser;
import net.ahwater.dataServ.entity.bo.UserBO;
import net.ahwater.dataServ.entity.vo.R;
import net.ahwater.dataServ.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by Reeye on 2019/8/12 10:00
 * Nothing is true but improving yourself.
 */
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(CurrentUser.class) && parameter.getParameterType().equals(UserBO.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                Object principal = authentication.getPrincipal();
                if (principal instanceof UserBO) {
                    return principal;
                } else if (principal instanceof String && !"anonymousUser".equals(principal)) {
                    return userService.loadUserByUsername("" + principal);
                } else {
                    HttpServletResponse response = (HttpServletResponse) webRequest.getNativeResponse();
                    response.setContentType("application/json;charset=UTF-8");
                    try {
                        PrintWriter writer = response.getWriter();
                        R result = R.of(R.UNAUTHORIZED, "token不正确", null);
                        writer.write(objectMapper.writeValueAsString(result));
                        writer.flush();
                        writer.close();
                    } catch (Exception ignored) {}
                    return null;
                }
            }
        });
    }

}
