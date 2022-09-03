package com.pickme.reggie.controller;

import com.pickme.reggie.common.Res;
import com.pickme.reggie.pojo.ShoppingCart;
import com.pickme.reggie.service.inter.ShoppingCartService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
     */
    @PostMapping("/add")
    public Res<String> save(@RequestBody ShoppingCart shoppingCart) {
        shoppingCartService.saveToCart(shoppingCart);
        return Res.success("");
    }

    /**
     * 删除购物车中商品
     * @param shoppingCart
     */
    @PostMapping("/sub")
    public Res<String> remove(@RequestBody ShoppingCart shoppingCart) {
        shoppingCartService.removeCart(shoppingCart);
        return Res.success("");
    }

    /**
     * 删除全部商品，清空购物车
     */
    @DeleteMapping("/clean")
    public Res<String> removeAll() {
        shoppingCartService.removeAllCart();
        return Res.success("");
    }

    /**
     * 查询购物车列表
     */
    @GetMapping("/list")
    public Res<List<ShoppingCart>> list() {
        List<ShoppingCart> list = shoppingCartService.listCarts();
        return Res.success(list);
    }
}