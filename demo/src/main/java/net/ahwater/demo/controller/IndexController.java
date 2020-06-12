package net.ahwater.demo.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.ahwater.demo.entity.VO.R;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by yqz on 2019/12/27
 */

@Slf4j
@Api(tags = "主要控制类")
@RestController
@CrossOrigin
public class IndexController{

    @ApiIgnore
    @RequestMapping("/")
    public R hello() {
        return R.ok("Hello World!");
    }
}