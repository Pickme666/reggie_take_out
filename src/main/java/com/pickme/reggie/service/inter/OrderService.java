package com.pickme.reggie.service.inter;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pickme.reggie.pojo.Orders;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService extends IService<Orders> {

    @Transactional
    boolean submit(Orders orders);
}
