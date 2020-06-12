package net.ahwater.dataServ.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.ahwater.dataServ.entity.po.UserPO;
import net.ahwater.dataServ.entity.vo.R;
import net.ahwater.dataServ.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Created by yqz on 2020/3/26
 */
@Api(description = "用户管理类")
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController{

    @Autowired
    private UserService userService;

    @ApiOperation(value = "登录", notes = "使用username与pwd登录")
    @GetMapping("/login")
    public R getToken(String username,String password) throws Exception {
        return userService.login(username, password);
    }

    @ApiOperation(value = "查询所有用户信息", notes = "使用username与pwd登录")
    @GetMapping("/list")
    public R list() {
        return userService.list();
    }

    @ApiOperation(value = "添加用户")
    @PostMapping("/add")
    public R addUser(UserPO user) {
        return R.ok("添加成功", userService.addUser(user));
    }

}