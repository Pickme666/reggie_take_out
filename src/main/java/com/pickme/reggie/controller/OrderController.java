package com.pickme.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pickme.reggie.common.Res;
import com.pickme.reggie.dto.OrdersDto;
import com.pickme.reggie.pojo.Orders;
import com.pickme.reggie.service.inter.OrderService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 订单管理
 */
@Api(tags = "订单管理")
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户支付，添加订单记录
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public Res<String> submit(@RequestBody Orders orders) {
        orderService.submit(orders);
        return Res.success("");
    }

    /**
     * 修改订单状态（后台管理端）
     * @param orders
     * @return
     */
    @PutMapping
    public Res<String> update(@RequestBody Orders orders) {
        orderService.updateById(orders);
        return Res.success("");
    }

    /**
     * 分页查询订单列表（移动端）
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public Res<Page<OrdersDto>> userPage(Integer page, Integer pageSize) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Orders::getCheckoutTime);
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage = orderService.pageWithDetail(ordersPage, wrapper);
        return Res.success(ordersDtoPage);
    }

    /**
     * 分页查询订单列表（后台管理端）
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Res<Page<Orders>> page(Integer page, Integer pageSize, Long number,
                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime
    ) {
        //log.info("beginTime: {}",beginTime);
        //log.info("endTime: {}",endTime);
        Page<Orders> p = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(number != null,Orders::getId,number)
                .ge(beginTime != null,Orders::getOrderTime,beginTime)
                .le(endTime != null,Orders::getOrderTime,endTime)
                .orderByDesc(Orders::getOrderTime);

        orderService.page(p,wrapper);
        return Res.success(p);
    }

    /**
     * （待开发完成）根据id查询订单明细信息并添加至购物车，再来一单
     * @param orders
     * @return
     */
    @PostMapping("/again")
    public Res<String> againOrder(@RequestBody Orders orders) {
        orderService.byIdRecurOrders(orders.getId());
        return Res.success("");
    }
}
