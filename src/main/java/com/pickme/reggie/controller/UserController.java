package com.pickme.reggie.controller;

import com.pickme.reggie.common.Res;
import com.pickme.reggie.pojo.User;
import com.pickme.reggie.service.inter.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 移动端用户登录
 */
@Api(tags = "移动端用户登录")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送手机验证码
     * @param user
     */
    @PostMapping("/sendMsg")
    public Res<String> sendMsg(@RequestBody User user) {
        String code = userService.sendSecurityCode(user);
        log.info("验证码：{}",code);
        return Res.success(code);
    }

    /**
     * 用户登录验证
     * @param map
     * @param session
     */
    @PostMapping("/login")
    public Res<User> login(@RequestBody Map<String,String> map, HttpSession session) {
        User user = userService.userLoginVerify(map, session);
        log.info("移动端登录用户ID：{}",user.getId());
        return Res.success(user);
    }

    /**
     * 退出登录
     * @param session
     */
    @PostMapping("/loginout")
    public Res<String> loginOut(HttpSession session) {
        session.removeAttribute("user");
        return Res.success("");
    }
}
