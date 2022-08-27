package com.pickme.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pickme.reggie.common.Res;
import com.pickme.reggie.common.util.ValidateCodeUtils;
import com.pickme.reggie.pojo.User;
import com.pickme.reggie.service.inter.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 移动端用户登录
 */

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 发送手机验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public Res<String> sendMsg(@RequestBody User user, HttpSession session) {
        String phone = user.getPhone();
        if (phone == null) return Res.error("发送验证码失败，手机号码为空");
        //随机生成指定位数的验证码，并转换为String类型
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        log.info("验证码：{}",code);

        //调用阿里云提供的短信服务API完成发送短信
        //SMSUtils.sendMessage("{短信签名}","{模板code}",phone,code);

        //将生成的验证码缓存在redis中，并设置5分钟有效时间
        redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

        return Res.success(code);
    }

    /**
     * 用户登录验证
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public Res<User> login(@RequestBody Map map, HttpSession session) {
        //获取前端传递的手机号和验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //从redis中获取缓存的验证码
        String redisCode = redisTemplate.opsForValue().get(phone);

        //进行验证码的比对, 如果比对失败, 直接返回错误信息
        if (!code.equals(redisCode)) return Res.error("验证码错误");
        //如果比对成功, 需要根据手机号查询当前用户, 如果用户不存在, 则自动注册一个新用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone,phone);
        User user = userService.getOne(wrapper);
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            userService.save(user);
        }

        //将登录用户的ID存储到Session中
        session.setAttribute("user",user.getId());
        //登录成功后删除验证码
        redisTemplate.delete(phone);
        return Res.success(user);
    }

    /**
     * 退出登录
     * @param session
     * @return
     */
    @PostMapping("/loginout")
    public Res<String> loginOut(HttpSession session) {
        session.removeAttribute("user");
        return Res.success("");
    }
}