package com.pickme.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pickme.reggie.common.util.LocalContext;
import com.pickme.reggie.mapper.ShoppingCartMapper;
import com.pickme.reggie.pojo.ShoppingCart;
import com.pickme.reggie.service.inter.ShoppingCartService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
        implements ShoppingCartService {

    @Override
    public boolean saveToCart(ShoppingCart shoppingCart) {
        Long userId = LocalContext.getCurrentId();
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        //构造条件
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(userId != null,ShoppingCart::getUserId,userId)
                .eq(dishId != null,ShoppingCart::getDishId,dishId)
                .eq(setmealId != null,ShoppingCart::getSetmealId,setmealId);

        //查询当前菜品或者套餐是否在购物车中
        ShoppingCart cartServiceOne = this.getOne(wrapper);
        if (cartServiceOne != null) {
            //如果存在，修改当前数量 + 1
            cartServiceOne.setNumber(cartServiceOne.getNumber() + 1);
            this.updateById(cartServiceOne);
        } else {
            //如果不存在，添加到购物车
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.save(shoppingCart);
        }

        return true;
    }

    @Override
    public boolean removeCart(ShoppingCart shoppingCart) {
        Long userId = LocalContext.getCurrentId();
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(userId != null,ShoppingCart::getUserId,userId)
                .eq(dishId != null,ShoppingCart::getDishId,dishId)
                .eq(setmealId != null,ShoppingCart::getSetmealId,setmealId);

        //判断购物车中是否还存有该商品
        ShoppingCart cartServiceOne = this.getOne(wrapper);
        if (cartServiceOne != null && cartServiceOne.getNumber() > 1) {
            cartServiceOne.setNumber(cartServiceOne.getNumber() - 1);
            this.updateById(cartServiceOne);
        } else {
            this.remove(wrapper);
        }

        return true;
    }

    /**
     * 清空购物车
     * @return
     */
    @Override
    public boolean removeAllCart() {
        Long userId = LocalContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId != null,ShoppingCart::getUserId,userId);
        return this.remove(wrapper);
    }

    @Override
    public List<ShoppingCart> listCarts() {
        Long userId = LocalContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId != null,ShoppingCart::getUserId,userId);
        wrapper.orderByDesc(ShoppingCart::getCreateTime);
        return this.list(wrapper);
    }

}
