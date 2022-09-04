package com.pickme.reggie.service.inter;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pickme.reggie.pojo.User;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface UserService extends IService<User> {

    String sendSecurityCode(User user);

    User userLoginVerify(Map<String,String> map, HttpSession session);
}
