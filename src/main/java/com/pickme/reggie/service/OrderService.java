package com.pickme.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pickme.reggie.pojo.dto.OrdersDto;
import com.pickme.reggie.pojo.Orders;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

public interface OrderService extends IService<Orders> {

    @Transactional
    boolean saveSubmitOrders(Orders orders);

    Page<OrdersDto> pageOrdersWithDetail(Integer page, Integer pageSize);

    Page<Orders> pageOrders(Integer page, Integer pageSize, Long number, Date beginTime, Date endTime);

    @Transactional
    boolean getByIdAgainOrders(Long id);
}
