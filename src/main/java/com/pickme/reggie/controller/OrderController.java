package com.pickme.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pickme.reggie.common.Res;
import com.pickme.reggie.pojo.Orders;
import com.pickme.reggie.service.inter.OrderService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单管理
 */
@Api(tags = "订单管理")
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
    public Res<Page<Orders>> userPage(Integer page, Integer pageSize) {
        Page<Orders> p = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Orders::getCheckoutTime);
        orderService.page(p,wrapper);
        return Res.success(p);
    }

    /**
     * 分页查询订单列表（后台管理端）
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Res<Page<Orders>> page(Integer page, Integer pageSize, Long orderId) {
        Page<Orders> p = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(orderId != null,Orders::getId,orderId);
        wrapper.orderByDesc(Orders::getCheckoutTime);
        orderService.page(p,wrapper);
        return Res.success(p);
    }

    /**
     * （代开发）根据id查询订单信息，再来一单
     * @param orders
     * @return
     */
    @PostMapping("/again")
    public Res<Orders> againOrder(@RequestBody Orders orders) {

        return Res.success(null);
    }
}
