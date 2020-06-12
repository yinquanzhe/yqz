package net.ahwater.ahwaterCloud.web.config;

import net.ahwater.ahwaterCloud.util.RespResult;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by TECHMAN on 2018/3/18.
 * Description: 当用户输入不合法的url时，返回标准结果(json字符串，而不是页面)
 */
@RestController
public class AppErrorUrlController implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public RespResult<?> error() {
        return new RespResult<String>(0, "输入的url不合法");
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}