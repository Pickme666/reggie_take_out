package com.pickme.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pickme.reggie.common.MC;
import com.pickme.reggie.common.exception.BusinessException;
import com.pickme.reggie.common.util.LocalContext;
import com.pickme.reggie.common.util.ValidateCodeUtils;
import com.pickme.reggie.mapper.UserMapper;
import com.pickme.reggie.pojo.User;
import com.pickme.reggie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate; //redis数据操作

    public String sendSecurityCode(User user) {
        String phone = user.getPhone();
        if (phone == null) throw new BusinessException(MC.E_PHONE_NULL);

        //随机生成指定位数的验证码，并转换为String类型
        String code = ValidateCodeUtils.generateValidateCode(4).toString();

        //调用阿里云提供的短信服务API完成发送短信
        //SMSUtils.sendMessage("{短信签名}","{模板code}",phone,code);

        //将生成的验证码缓存在redis中，并设置5分钟有效时间
        redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

        return code;
    }

    @Override
    public User userLoginVerify(Map<String, String> map, HttpSession session) {
        //获取前端传递的手机号和验证码
        String phone = map.get("phone");
        String code = map.get("code");
        //从redis中获取缓存的验证码
        String redisCode = redisTemplate.opsForValue().get(phone);

        //进行验证码的比对, 如果比对失败, 直接抛出异常信息
        if (!code.equals(redisCode)) throw new BusinessException(MC.E_CODE);
        //如果比对成功, 需要根据手机号查询当前用户, 如果用户不存在, 则自动注册一个新用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone,phone);
        User user = this.getOne(wrapper);
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            this.save(user);
        }

        //将登录用户的ID存储到Session中
        session.setAttribute("user",user.getId());
        LocalContext.setCurrentId(user.getId());

        //登录成功后删除验证码
        redisTemplate.delete(phone);

        return user;
    }
}
