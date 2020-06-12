package net.ahwater.tender.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import net.ahwater.tender.db.entity.BeanUser;
import net.ahwater.tender.db.entity.R;
import net.ahwater.tender.web.annotation.CurrentUser;
import net.ahwater.tender.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by yyc on 2018/4/9 14:22.
 * Nothing is true but improving yourself.
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Api(tags = "用户控制类")
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @ApiOperation(value = "获取当前用户信息", response = BeanUser.class)
    public R findUserById(@CurrentUser BeanUser user) {
        return R.ok(user);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "查询用户", notes = "根据用户ID查找用户信息", response = BeanUser.class)
    public R findUserById(
            @CurrentUser BeanUser user,
            @ApiParam(name = "id", value = "要查找的用户ID", required = true, example = "1")
            @PathVariable Integer id) {
        return R.ok(userService.findOneById(id));
    }

    @ApiOperation("修改密码")
    @PostMapping("/pwd/modify")
    public R modifyPwd(
            @CurrentUser BeanUser user,
            @ApiParam(name = "newPwd", value = "新密码", required = true)
            @RequestParam String newPwd) {
        if (userService.modifyPwd(user.getId(), newPwd)) {
            return R.ok("修改成功", null);
        } else {
            return R.error("修改失败");
        }
    }

}
