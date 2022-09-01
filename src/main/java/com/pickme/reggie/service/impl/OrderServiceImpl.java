package com.pickme.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pickme.reggie.common.MC;
import com.pickme.reggie.common.exception.BusinessException;
import com.pickme.reggie.common.util.LocalContext;
import com.pickme.reggie.dto.OrdersDto;
import com.pickme.reggie.mapper.OrderMapper;
import com.pickme.reggie.pojo.*;
import com.pickme.reggie.service.inter.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private UserService userService;

    /**
     * 添加用户支付的订单数据
     * @param orders
     */
    @Override
    public boolean submit(Orders orders) {
        //获得当前用户id, 查询当前用户的购物车数据
        Long userId = LocalContext.getCurrentId();

        //根据当前登录用户id, 查询用户数据
        User user = userService.getById(userId);

        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new BusinessException(MC.E_CAR_NULL);
        }

        //根据地址ID, 查询地址数据
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null) throw new BusinessException(MC.E_ADDRESS);

        long orderId = IdWorker.getId(); //MybatisPlus id获取器，生成一个唯一id
        AtomicInteger amount = new AtomicInteger(); //一个可以原子更新的int值，处理数字类型的工具

        //组装订单明细数据, 批量保存订单明细
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            //计算订单金额
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        orderDetailService.saveBatch(orderDetails);

        //组装订单数据, 批量保存订单数据
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        //地址信息字符串拼接
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        this.save(orders);

        //清空当前用户购物车数据
        return shoppingCartService.remove(wrapper);
    }

    @Override
    public Page<OrdersDto> pageWithDetail(Page<Orders> ordersPage, Wrapper<Orders> wrapper) {

        Page<OrdersDto> dtoPage = new Page<>();
        List<Orders> ordersList = this.page(ordersPage, wrapper).getRecords();
        BeanUtils.copyProperties(ordersPage,dtoPage,"records");

        List<OrdersDto> dtoList = ordersList.stream().map((item) -> {
            Long orderId = item.getId();
            OrdersDto dto = new OrdersDto();
            BeanUtils.copyProperties(item,dto);

            //查询订单明细列表
            LambdaQueryWrapper<OrderDetail> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.eq(orderId != null,OrderDetail::getOrderId,orderId);
            List<OrderDetail> details = orderDetailService.list(orderWrapper);

            if (details != null) dto.setOrderDetails(details);
            return dto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    @Override
    public OrdersDto byIdRecurOrders(Long id) {
        Orders orders = this.getById(id);

        //查询订单明细列表
        LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDetail::getOrderId,id);
        List<OrderDetail> orderDetails = orderDetailService.list(wrapper);

        /*List<ShoppingCart> shoppingCarts = orderDetails.stream().map(item -> {
            Long dishId = item.getDishId();
            Long setmealId = item.getSetmealId();
            if (dishId != null) {

            }
            return
        }).collect(Collectors.toList());*/

        return null;
    }


}