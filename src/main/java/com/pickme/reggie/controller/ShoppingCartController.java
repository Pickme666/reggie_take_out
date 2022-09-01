package com.pickme.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pickme.reggie.common.Res;
import com.pickme.reggie.common.util.LocalContext;
import com.pickme.reggie.pojo.ShoppingCart;
import com.pickme.reggie.service.inter.ShoppingCartService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车管理
 */
@Api(tags = "购物车管理")
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加菜品或套餐到购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public Res<String> save(@RequestBody ShoppingCart shoppingCart) {
        Long userId = LocalContext.getCurrentId();
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        //构造条件
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(userId != null,ShoppingCart::getUserId,userId)
                .eq(dishId != null,ShoppingCart::getDishId,dishId)
                .eq(setmealId != null,ShoppingCart::getSetmealId,setmealId)
        ;
        //查询当前菜品或者套餐是否在购物车中
        ShoppingCart cartServiceOne = shoppingCartService.getOne(wrapper);
        if (cartServiceOne != null) {
            //如果存在，修改当前数量 + 1
            cartServiceOne.setNumber(cartServiceOne.getNumber() + 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
            //如果不存在，添加到购物车
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
        }
        return Res.success("");
    }

    /**
     * 删除购物车中商品
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public Res<String> remove(@RequestBody ShoppingCart shoppingCart) {
        Long userId = LocalContext.getCurrentId();
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(userId != null,ShoppingCart::getUserId,userId)
                .eq(dishId != null,ShoppingCart::getDishId,dishId)
                .eq(setmealId != null,ShoppingCart::getSetmealId,setmealId)
        ;
        //判断购物车中是否还存有该商品
        ShoppingCart cartServiceOne = shoppingCartService.getOne(wrapper);
        if (cartServiceOne != null && cartServiceOne.getNumber() > 1) {
            cartServiceOne.setNumber(cartServiceOne.getNumber() - 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
            shoppingCartService.remove(wrapper);
        }
        return Res.success("");
    }

    /**
     * 删除全部商品，清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public Res<String> removeAll() {
        shoppingCartService.cleanCart();
        return Res.success("");
    }

    /**
     * 查询购物车列表
     * @return
     */
    @GetMapping("/list")
    public Res<List<ShoppingCart>> list() {
        Long userId = LocalContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId != null,ShoppingCart::getUserId,userId);
        wrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(wrapper);
        return Res.success(list);
    }
}