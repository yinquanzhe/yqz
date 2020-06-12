package net.ahwater.tender.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.ahwater.tender.db.entity.BeanUser;
import net.ahwater.tender.db.entity.R;
import net.ahwater.tender.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.security.web.WebAttributes;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by yyc on 2018/4/9 14:22.
 * Nothing is true but improving yourself.
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Slf4j
@Api(tags = "主要控制类")
@RestController
@CrossOrigin
public class IndexController extends AbstractErrorController {

    @Autowired
    private UserService userService;

    public IndexController(ErrorAttributes errorAttributes, List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorViewResolvers);
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @ApiIgnore
    @RequestMapping("/")
    public R hello() {
        return R.ok("Hello World!");
    }

    @ApiIgnore
    @RequestMapping("/error")
    public R error(HttpServletRequest request) {

        log.error("{}>>> {}", WebAttributes.ACCESS_DENIED_403, request.getAttribute(WebAttributes.ACCESS_DENIED_403));
        log.error("{}>>> {}", WebAttributes.AUTHENTICATION_EXCEPTION, request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION));
        log.error("{}>>> {}", WebAttributes.WEB_INVOCATION_PRIVILEGE_EVALUATOR_ATTRIBUTE, request.getAttribute(WebAttributes.WEB_INVOCATION_PRIVILEGE_EVALUATOR_ATTRIBUTE));

        Map<String, Object> errorInfo = this.getErrorAttributes(request, false);
        log.error("出错: {}", errorInfo);
        Object status = errorInfo.get("status");
        if (status instanceof Integer) {
            int st = (int) status;
            if (st == 404) {
                return R.of(R.NOT_FOUND, "未找到", null);
            } else if (st == 403) {
                return R.of(R.UNAUTHORIZED, "未授权", null);
            }
        }
        Object ex = request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        if (ex instanceof Exception) {
            return R.error(((Exception) ex).getMessage());
        }
        String message = errorInfo.get("message") + "";
        return R.error(message);
    }

    @ApiIgnore
    @RequestMapping("/403")
    public R unauthorized() {
        return R.of(R.UNAUTHORIZED, "未授权", null);
    }

    @ApiOperation(value = "获取Token", notes = "使用username与pwd登录", httpMethod = "POST")
    @PostMapping("/token")
    public R getToken(String username, String pwd) {
        return R.ok("获取成功", userService.login(username, pwd));
    }

    @ApiOperation(value = "刷新Token")
    @PostMapping(value = "/token/refresh")
    public R refreshToken(@RequestHeader String authorization) {
        return R.ok("刷新Token成功", userService.refreshToken(authorization));
    }

    @ApiOperation(value = "用户注册", notes = "使用username与pwd注册", httpMethod = "POST")
    @PostMapping(value = "/register")
    public R register(BeanUser user) {
        return R.ok("注册成功", userService.register(user));
    }

}
