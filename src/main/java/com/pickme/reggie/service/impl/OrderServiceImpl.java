package com.pickme.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pickme.reggie.common.MC;
import com.pickme.reggie.common.exception.BusinessException;
import com.pickme.reggie.common.util.LocalContext;
import com.pickme.reggie.mapper.OrderMapper;
import com.pickme.reggie.pojo.*;
import com.pickme.reggie.pojo.dto.OrdersDto;
import com.pickme.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
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
    public boolean saveSubmitOrders(Orders orders) {

        //获得当前用户id, 查询当前用户的购物车数据
        Long userId = LocalContext.getCurrentId();
        //根据当前登录用户id, 查询用户数据
        User user = userService.getById(userId);

        //查询当前用户购物车列表
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new BusinessException(MC.E_CAR_NULL);
        }

        //MybatisPlus id获取器，生成一个唯一id
        long orderId = IdWorker.getId();
        //一个可以原子更新的int值，处理数字类型的工具
        AtomicInteger amounts = new AtomicInteger();

        //遍历购物车列表，设置订单明细数据
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(item,orderDetail,"id");
            orderDetail.setOrderId(orderId);

            //计算订单金额：总价格 += 当前商品价格 * 100 * 数量。intValue()：将BigDecimal类型转换为int类型返回
            //因为 AtomicInteger 只能添加int值，所以先将价格乘以100再计算，防止精度丢失
            BigDecimal sum = item.getAmount().multiply(new BigDecimal(100));
            amounts.addAndGet(sum.multiply(new BigDecimal(item.getNumber())).intValue());

            return orderDetail;
        }).collect(Collectors.toList());
        //批量保存订单明细数据
        orderDetailService.saveBatch(orderDetails);

        //根据地址ID, 查询地址数据
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null) throw new BusinessException(MC.E_ADDRESS);

        //设置订单基本信息并保存
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal((double) amounts.get() / 100));//总金额，除以100保留小数
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        //地址信息字符串拼接
        orders.setAddress(
                  (addressBook.getProvinceName() == null        ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null            ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null        ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null              ? "" : addressBook.getDetail())
        );
        //保存
        this.save(orders);
        log.info("用户 " + user.getPhone() + " 提交订单：{}", orders);

        //清空当前用户购物车数据
        return shoppingCartService.remove(wrapper);
    }

    /**
     * 分页查询订单列表及明细
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page<OrdersDto> pageOrdersWithDetail(Integer page, Integer pageSize) {

        Page<Orders> ordersPage = new Page<>(page,pageSize);
        Page<OrdersDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Orders::getCheckoutTime);
        List<Orders> ordersList = this.page(ordersPage, wrapper).getRecords();
        BeanUtils.copyProperties(ordersPage,dtoPage,"records");

        List<OrdersDto> dtoList = ordersList.stream().map((item) -> {
            Long orderId = item.getId();
            OrdersDto dto = new OrdersDto();
            BeanUtils.copyProperties(item,dto);

            //查询订单明细列表
            LambdaQueryWrapper<OrderDetail> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.eq(orderId != null,OrderDetail::getOrderId,orderId);
            List<OrderDetail> detailList = orderDetailService.list(orderWrapper);

            if (detailList != null) dto.setOrderDetails(detailList);
            return dto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    @Override
    public Page<Orders> pageOrders(Integer page, Integer pageSize, Long number, Date beginTime, Date endTime) {
        Page<Orders> p = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(number != null,Orders::getId,number)
                .ge(beginTime != null,Orders::getOrderTime,beginTime)
                .le(endTime != null,Orders::getOrderTime,endTime)
                .orderByDesc(Orders::getOrderTime);

        return this.page(p,wrapper);
    }

    /**
     * 再来一单，重新添加当前订单的商品信息到购物车
     * @param id
     * @return
     */
    @Override
    public boolean getByIdAgainOrders(Long id) {

        //查询订单明细列表
        LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDetail::getOrderId,id);
        List<OrderDetail> orderDetails = orderDetailService.list(wrapper);

        //清空当前购物车
        shoppingCartService.removeAllCart();

        //遍历订单明细列表，设置购物车信息
        List<ShoppingCart> shoppingCarts = orderDetails.stream().map(item -> {
            ShoppingCart cart = new ShoppingCart();
            BeanUtils.copyProperties(item,cart,"id");
            cart.setUserId(LocalContext.getCurrentId());
            cart.setCreateTime(LocalDateTime.now());
            return cart;
        }).collect(Collectors.toList());

        //重新添加到购物车
        return shoppingCartService.saveBatch(shoppingCarts);
    }

}