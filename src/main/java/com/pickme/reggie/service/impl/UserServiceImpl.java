package com.pickme.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pickme.reggie.mapper.UserMapper;
import com.pickme.reggie.pojo.User;
import com.pickme.reggie.service.inter.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
