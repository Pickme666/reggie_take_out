package com.pickme.reggie.service.inter;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pickme.reggie.dto.OrdersDto;
import com.pickme.reggie.pojo.Orders;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService extends IService<Orders> {

    @Transactional
    boolean submit(Orders orders);

    Page<OrdersDto> pageWithDetail(Page<Orders> ordersPage, Wrapper<Orders> wrapper);

    OrdersDto byIdRecurOrders(Long id);
}
