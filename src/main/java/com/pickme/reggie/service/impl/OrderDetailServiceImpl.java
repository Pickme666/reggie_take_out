package com.pickme.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pickme.reggie.mapper.OrderDetailMapper;
import com.pickme.reggie.pojo.OrderDetail;
import com.pickme.reggie.service.inter.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}