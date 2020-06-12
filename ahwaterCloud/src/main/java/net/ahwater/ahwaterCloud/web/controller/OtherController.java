package net.ahwater.ahwaterCloud.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by TECHMAN on 2018/1/19.
 */
@Controller
public class OtherController {

    /**
     * 登录页面
     */
    @RequestMapping(value = "/")
    public String index() {
        return "login";
    }
}
